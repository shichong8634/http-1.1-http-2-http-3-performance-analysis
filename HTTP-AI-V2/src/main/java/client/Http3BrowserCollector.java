package client;

import model.PerformanceRecord;
import metrics.NetworkMetrics;
import metrics.PingMonitor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Http3BrowserCollector {

    private static final String EDGE_PATH =
            "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\151.0.4129.78\\msedge.exe";

    private static final int DEBUG_PORT = 9223;

    private static final String TARGET_ORIGIN =
            "https://www.yunlong-performance-research.com";

    private static final String DEFAULT_URL =
            TARGET_ORIGIN + "/index.html";

    private final HttpClient httpClient =
            HttpClient.newHttpClient();

    private WebSocket webSocket;

    private Process edgeProcess;

    private CountDownLatch finished;

    private String targetUrl;

    private String mainRequestId = null;

    private String protocol = "";

    private int statusCode = -1;

    private double requestTimestamp = -1;

    private double responseTimestamp = -1;

    private double loadingFinishedTimestamp = -1;

    private long dataSize = 0;

    private int activeRequests = 0;

    private boolean mainRequestFailed = false;


    // ============================================================
    // TEST
    // ============================================================

    public PerformanceRecord test(String url, int run) throws Exception {

        reset();

        targetUrl =
                (url == null || url.isBlank())
                        ? DEFAULT_URL
                        : url;

        System.out.println();
        System.out.println("========================================");
        System.out.println("HTTP/3 Browser Performance Test");
        System.out.println("Target URL: " + targetUrl);
        System.out.println("Browser: Microsoft Edge");
        System.out.println("CDP Port: " + DEBUG_PORT);
        System.out.println("========================================");

        // --------------------------------------------------------
        // 1. Start Edge
        // --------------------------------------------------------

        startEdge();

        // --------------------------------------------------------
        // 2. Wait for CDP
        // --------------------------------------------------------

        waitForCDP();

        // --------------------------------------------------------
        // 3. Find real page target
        // --------------------------------------------------------

        String targets =
                getTargets();

        String wsUrl =
                extractPageWebSocketUrl(targets);

        if (wsUrl == null) {

            throw new RuntimeException(
                    "没有找到 Edge page target"
            );
        }

        System.out.println(
                "Page WebSocket found:"
        );

        System.out.println(wsUrl);

        // --------------------------------------------------------
        // 4. Connect WebSocket
        // --------------------------------------------------------

        System.out.println(
                "Connecting to Edge CDP..."
        );

        webSocket =
                httpClient
                        .newWebSocketBuilder()
                        .buildAsync(
                                URI.create(wsUrl),
                                new CDPListener()
                        )
                        .join();

        System.out.println(
                "Connected to Edge PAGE CDP."
        );

        // --------------------------------------------------------
        // 5. Enable Page
        // --------------------------------------------------------

        sendCommand(
                1,
                "Page.enable",
                "{}"
        );

        // --------------------------------------------------------
        // 6. Enable Network
        // --------------------------------------------------------

        sendCommand(
                2,
                "Network.enable",
                "{}"
        );

        // --------------------------------------------------------
        // 7. Disable cache
        // --------------------------------------------------------

        sendCommand(
                3,
                "Network.setCacheDisabled",
                "{\"cacheDisabled\":true}"
        );

        Thread.sleep(500);

        // --------------------------------------------------------
        // 8. Navigate
        // --------------------------------------------------------

        System.out.println(
                "Navigating to:"
        );

        System.out.println(targetUrl);

        sendCommand(
                4,
                "Page.navigate",
                "{\"url\":\""
                        + escapeJson(targetUrl)
                        + "\"}"
        );

        // --------------------------------------------------------
        // 9. Wait
        // --------------------------------------------------------

        boolean completed =
                finished.await(
                        30,
                        TimeUnit.SECONDS
                );

        if (!completed) {

            System.out.println(
                    "WARNING: Main request timeout."
            );

            System.out.println(
                    "mainRequestId = "
                            + mainRequestId
            );
        }

        // A 200 response without loadingFinished is incomplete.
        if (mainRequestFailed || !completed) {
            statusCode = -1;
        }

        // --------------------------------------------------------
        // 10. Calculate response time
        // --------------------------------------------------------

        double responseTime = -1;

        if (requestTimestamp >= 0 &&
                loadingFinishedTimestamp >= 0) {

            responseTime =
                    (loadingFinishedTimestamp
                            - requestTimestamp)
                            * 1000.0;
        }

        // --------------------------------------------------------
        // 11. Result
        // --------------------------------------------------------

        System.out.println();
        System.out.println(
                "========== HTTP/3 RESULT =========="
        );

        System.out.println(
                "Protocol: " + protocol
        );

        System.out.println(
                "Status Code: " + statusCode
        );

        System.out.println(
                "Response Time: "
                        + responseTime
                        + " ms"
        );

        System.out.println(
                "Data Size: "
                        + dataSize
                        + " bytes"
        );

        // --------------------------------------------------------
        // 12. Close
        // --------------------------------------------------------

        close();

        long ttfb = -1;
        if (requestTimestamp >= 0 && responseTimestamp >= 0) {
            ttfb = Math.round((responseTimestamp - requestTimestamp) * 1000.0);
        }

        long responseTimeMs = responseTime >= 0 ? Math.round(responseTime) : -1;

        // Unified throughput unit: Mbps.
        double seconds =
                responseTimeMs > 0
                        ? responseTimeMs / 1000.0
                        : 0.0;

        double throughput =
                statusCode > 0
                        && seconds > 0
                        && dataSize > 0
                        ? (dataSize * 8.0)
                        / seconds
                        / 1_000_000.0
                        : 0.0;

        // Same network-metric source as HTTP/1.1 and HTTP/2.
        PingMonitor pingMonitor =
                new PingMonitor();

        NetworkMetrics networkMetrics =
                pingMonitor.measure(
                        URI.create(targetUrl).getHost()
                );

        double averageRTT =
                networkMetrics.getAverageRTT();

        double minRTT =
                networkMetrics.getMinRTT();

        double maxRTT =
                networkMetrics.getMaxRTT();

        double packetLoss =
                networkMetrics.getPacketLoss();

        double jitter =
                networkMetrics.getJitter();

        return new PerformanceRecord(
                "HTTP/3",
                run,
                responseTimeMs,
                ttfb,
                dataSize,
                throughput,
                statusCode,
                averageRTT,
                minRTT,
                maxRTT,
                packetLoss,
                jitter
        );
    }


    // ============================================================
    // RESET
    // ============================================================

    private void reset() {

        finished =
                new CountDownLatch(1);

        mainRequestId = null;

        protocol = "";

        statusCode = -1;

        requestTimestamp = -1;

        responseTimestamp = -1;

        loadingFinishedTimestamp = -1;

        dataSize = 0;

        activeRequests = 0;

        mainRequestFailed = false;
    }


    // ============================================================
    // START EDGE
    // ============================================================

    private void startEdge()
            throws IOException {

        Path profile =
                Path.of(
                        System.getProperty(
                                "java.io.tmpdir"
                        ),
                        "EdgeHTTP3ResearchProfile"
                );

        Files.createDirectories(profile);

        System.out.println(
                "Starting Microsoft Edge..."
        );

        edgeProcess =
                new ProcessBuilder(
                        EDGE_PATH,

                        "--user-data-dir="
                                + profile.toAbsolutePath(),

                        "--remote-debugging-port="
                                + DEBUG_PORT,

                        "--enable-quic",

                        "--origin-to-force-quic-on="
                                + "www.yunlong-performance-research.com:443",

                        "--no-first-run",

                        "--no-default-browser-check",

                        "--disable-popup-blocking",

                        DEFAULT_URL
                )
                        .redirectErrorStream(true)
                        .start();

        System.out.println(
                "Microsoft Edge process started."
        );
    }


    // ============================================================
    // WAIT CDP
    // ============================================================

    private void waitForCDP()
            throws Exception {

        System.out.println(
                "Waiting for Edge CDP..."
        );

        long start =
                System.currentTimeMillis();

        while (true) {

            try {

                HttpRequest request =
                        HttpRequest.newBuilder()
                                .uri(
                                        URI.create(
                                                "http://localhost:"
                                                        + DEBUG_PORT
                                                        + "/json/version"
                                        )
                                )
                                .GET()
                                .build();

                HttpResponse<String> response =
                        httpClient.send(
                                request,
                                HttpResponse.BodyHandlers
                                        .ofString()
                        );

                if (response.statusCode() == 200) {

                    System.out.println(
                            "Edge CDP is ready."
                    );

                    return;
                }

            } catch (Exception ignored) {
            }

            if (System.currentTimeMillis()
                    - start > 15000) {

                throw new RuntimeException(
                        "Edge CDP Startup timeout"
                );
            }

            Thread.sleep(300);
        }
    }


    // ============================================================
    // GET TARGETS
    // ============================================================

    private String getTargets()
            throws Exception {

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        "http://localhost:"
                                                + DEBUG_PORT
                                                + "/json"
                                )
                        )
                        .GET()
                        .build();

        return httpClient.send(
                request,
                HttpResponse.BodyHandlers
                        .ofString()
        ).body();
    }


    // ============================================================
    // FIND PAGE
    // ============================================================

    private String extractPageWebSocketUrl(
            String json
    ) {

        Pattern objectPattern =
                Pattern.compile(
                        "\\{(.*?)\\}",
                        Pattern.DOTALL
                );

        Matcher matcher =
                objectPattern.matcher(json);

        while (matcher.find()) {

            String object =
                    matcher.group();

            String type =
                    extractString(
                            object,
                            "type"
                    );

            String url =
                    extractString(
                            object,
                            "url"
                    );

            String ws =
                    extractString(
                            object,
                            "webSocketDebuggerUrl"
                    );

            if ("page".equals(type)
                    && url != null
                    && ws != null
                    && (
                    url.startsWith("http://")
                            ||
                            url.startsWith("https://")
            )) {

                System.out.println(
                        "Selected Edge page:"
                );

                System.out.println(
                        "URL: " + url
                );

                return ws;
            }
        }

        return null;
    }


    // ============================================================
    // SEND CDP COMMAND
    // ============================================================

    private void sendCommand(
            int id,
            String method,
            String params
    ) {

        String message =
                "{"
                        + "\"id\":" + id + ","
                        + "\"method\":\""
                        + method
                        + "\","
                        + "\"params\":"
                        + params
                        + "}";

        System.out.println(
                "CDP SEND: " + message
        );

        webSocket.sendText(
                message,
                true
        );
    }


    // ============================================================
    // STRING
    // ============================================================

    private String extractString(
            String json,
            String key
    ) {

        Pattern pattern =
                Pattern.compile(
                        "\""
                                + Pattern.quote(key)
                                + "\"\\s*:\\s*\"([^\"]*)\""
                );

        Matcher matcher =
                pattern.matcher(json);

        if (matcher.find()) {

            return matcher.group(1);
        }

        return null;
    }


    // ============================================================
    // DOUBLE
    // ============================================================

    private double extractDouble(
            String json,
            String key
    ) {

        Pattern pattern =
                Pattern.compile(
                        "\""
                                + Pattern.quote(key)
                                + "\"\\s*:\\s*(-?[0-9]+(?:\\.[0-9]+)?)"
                );

        Matcher matcher =
                pattern.matcher(json);

        if (matcher.find()) {

            return Double.parseDouble(
                    matcher.group(1)
            );
        }

        return -1;
    }


    // ============================================================
    // LONG
    // ============================================================

    private long extractLong(
            String json,
            String key
    ) {

        Pattern pattern =
                Pattern.compile(
                        "\""
                                + Pattern.quote(key)
                                + "\"\\s*:\\s*(-?[0-9]+)"
                );

        Matcher matcher =
                pattern.matcher(json);

        if (matcher.find()) {

            return Long.parseLong(
                    matcher.group(1)
            );
        }

        return 0;
    }


    // ============================================================
    // INT
    // ============================================================

    private int extractInt(
            String json,
            String key
    ) {

        Pattern pattern =
                Pattern.compile(
                        "\""
                                + Pattern.quote(key)
                                + "\"\\s*:\\s*(-?[0-9]+)"
                );

        Matcher matcher =
                pattern.matcher(json);

        if (matcher.find()) {

            return Integer.parseInt(
                    matcher.group(1)
            );
        }

        return -1;
    }


    // ============================================================
    // ESCAPE
    // ============================================================

    private String escapeJson(
            String text
    ) {

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }


    // ============================================================
    // CDP LISTENER
    // ============================================================

    private class CDPListener
            implements WebSocket.Listener {

        private final StringBuilder buffer =
                new StringBuilder();


        @Override
        public void onOpen(
                WebSocket webSocket
        ) {

            System.out.println(
                    "CDP WebSocket OPEN."
            );

            webSocket.request(1);
        }


        @Override
        public CompletionStage<?> onText(
                WebSocket webSocket,
                CharSequence data,
                boolean last
        ) {

            buffer.append(data);

            if (last) {

                String message =
                        buffer.toString();

                buffer.setLength(0);

                System.out.println(
                        "CDP RECEIVE: "
                                + message
                );

                processMessage(message);
            }

            webSocket.request(1);

            return null;
        }


        @Override
        public void onError(
                WebSocket webSocket,
                Throwable error
        ) {

            System.out.println(
                    "CDP WebSocket ERROR:"
            );

            error.printStackTrace();
        }


        @Override
        public CompletionStage<?> onClose(
                WebSocket webSocket,
                int statusCode,
                String reason
        ) {

            System.out.println(
                    "CDP WebSocket CLOSED: "
                            + statusCode
                            + " / "
                            + reason
            );

            return null;
        }


        // ========================================================
        // PROCESS EVENT
        // ========================================================

        private void processMessage(
                String message
        ) {

            // ----------------------------------------------------
            // requestWillBeSent
            // ----------------------------------------------------

            if (message.contains(
                    "\"Network.requestWillBeSent\""
            )) {

                String requestId =
                        extractString(
                                message,
                                "requestId"
                        );

                String url =
                        extractString(
                                message,
                                "url"
                        );

                double timestamp =
                        extractDouble(
                                message,
                                "timestamp"
                        );

                System.out.println(
                        "NETWORK REQUEST:"
                );

                System.out.println(
                        "requestId = "
                                + requestId
                );

                System.out.println(
                        "url = "
                                + url
                );


                boolean isMainDocument =
                        message.contains("\"type\":\"Document\"")
                                && requestId != null
                                && timestamp >= 0
                                && url != null
                                && url.startsWith(targetUrl)
                                && mainRequestId == null;

                if (isMainDocument) {

                    mainRequestId =
                            requestId;

                    activeRequests = 1;

                    requestTimestamp =
                            timestamp;

                    System.out.println(
                            ">>> MAIN REQUEST FOUND <<<"
                    );

                    System.out.println(
                            "mainRequestId = "
                                    + mainRequestId
                    );
                }else if (requestId != null
                        && url != null
                        && url.startsWith(TARGET_ORIGIN)) {

                    activeRequests++;

                    System.out.println(
                            "Active page requests = "
                                    + activeRequests
                    );
                }
            }


            // ----------------------------------------------------
            // responseReceived
            // ----------------------------------------------------

            if (message.contains(
                    "\"Network.responseReceived\""
            )) {

                String requestId =
                        extractString(
                                message,
                                "requestId"
                        );

                System.out.println(
                        "NETWORK RESPONSE:"
                );

                System.out.println(
                        "requestId = "
                                + requestId
                );

                if (requestId != null
                        && requestId.equals(
                        mainRequestId
                )) {

                    String response =
                            extractObject(
                                    message,
                                    "\"response\":{"
                            );

                    if (response != null) {

                        statusCode =
                                extractInt(
                                        response,
                                        "status"
                                );

                        protocol =
                                extractString(
                                        response,
                                        "protocol"
                                );

                        responseTimestamp =
                                extractDouble(
                                        message,
                                        "timestamp"
                                );

                        System.out.println(
                                ">>> MAIN RESPONSE FOUND <<<"
                        );

                        System.out.println(
                                "Protocol = "
                                        + protocol
                        );

                        System.out.println(
                                "Status = "
                                        + statusCode
                        );
                    }
                }
            }


            // ----------------------------------------------------
            // dataReceived
            // ----------------------------------------------------

            if (message.contains(
                    "\"Network.dataReceived\""
            )) {

                String requestId =
                        extractString(
                                message,
                                "requestId"
                        );

                if (requestId != null) {

                    long dataLength =
                            extractLong(
                                    message,
                                    "dataLength"
                            );

                    if (dataLength > 0) {
                        dataSize += dataLength;
                    }
                }
            }


            // ----------------------------------------------------
            // loadingFailed
            // ----------------------------------------------------

            if (message.contains(
                    "\"Network.loadingFailed\""
            )) {

                String requestId =
                        extractString(
                                message,
                                "requestId"
                        );

                if (requestId != null
                        && requestId.equals(mainRequestId)) {

                    mainRequestFailed = true;

                    System.out.println(
                            ">>> MAIN REQUEST FAILED <<<"
                    );

                    finished.countDown();
                }
            }


            // ----------------------------------------------------
            // loadingFinished
            // ----------------------------------------------------

            if (message.contains(
                    "\"Network.loadingFinished\""
            )) {

                String requestId =
                        extractString(
                                message,
                                "requestId"
                        );

                System.out.println(
                        "NETWORK LOADING FINISHED:"
                );

                System.out.println(
                        "requestId = "
                                + requestId
                );

                if (requestId != null
                        && requestId.equals(
                        mainRequestId
                )) {

                    loadingFinishedTimestamp =
                            extractDouble(
                                    message,
                                    "timestamp"
                            );

                    finished.countDown();

                    System.out.println(
                            ">>> MAIN REQUEST FINISHED <<<"
                    );
                }
            }
        }


        // ========================================================
        // EXTRACT OBJECT
        // ========================================================

        private String extractObject(
                String json,
                String start
        ) {

            int index =
                    json.indexOf(start);

            if (index == -1) {

                return null;
            }

            int begin =
                    index
                            + start.length()
                            - 1;

            int depth = 0;

            boolean inString = false;

            for (
                    int i = begin;
                    i < json.length();
                    i++
            ) {

                char c =
                        json.charAt(i);

                if (c == '"'
                        && (
                        i == 0
                                ||
                                json.charAt(i - 1)
                                        != '\\'
                )) {

                    inString =
                            !inString;
                }

                if (!inString) {

                    if (c == '{') {

                        depth++;
                    }

                    if (c == '}') {

                        depth--;

                        if (depth == 0) {

                            return json.substring(
                                    begin,
                                    i + 1
                            );
                        }
                    }
                }
            }

            return null;
        }
    }


    // ============================================================
    // CLOSE
    // ============================================================

    private void close() {

        if (webSocket != null) {

            try {

                webSocket.sendClose(
                        WebSocket.NORMAL_CLOSURE,
                        "done"
                ).join();

            } catch (Exception ignored) {
            }

            webSocket = null;
        }

        if (edgeProcess != null) {

            try {

                edgeProcess.destroy();

            } catch (Exception ignored) {
            }

            edgeProcess = null;
        }
    }



}