package client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Http3PerformanceCollector {

    private final HttpClient httpClient =
            HttpClient.newHttpClient();

    private WebSocket webSocket;

    private final CountDownLatch finished =
            new CountDownLatch(1);

    private String targetUrl;

    private String mainRequestId;

    private String protocol = "";

    private int statusCode = -1;

    private double requestTimestamp = -1;

    private double responseTimestamp = -1;

    private double loadingFinishedTimestamp = -1;

    private long dataSize = 0;

    public Result test(String url) throws Exception {

        targetUrl = url;

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(
                                "http://localhost:9223/json"
                        ))
                        .GET()
                        .build();

        String targets =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                ).body();

        String wsUrl =
                extractWebSocketUrl(targets);

        if (wsUrl == null) {
            throw new RuntimeException(
                    "没有找到 Chrome CDP 页面。"
            );
        }

        System.out.println(
                "Connecting to Chrome CDP..."
        );

        webSocket =
                httpClient.newWebSocketBuilder()
                        .buildAsync(
                                URI.create(wsUrl),
                                new Listener()
                        )
                        .join();

        sendCommand(
                1,
                "Network.enable",
                "{}"
        );

        sendCommand(
                2,
                "Network.setCacheDisabled",
                "{\"cacheDisabled\":true}"
        );

        sendCommand(
                3,
                "Page.navigate",
                "{\"url\":\""
                        + escapeJson(url)
                        + "\"}"
        );

        boolean ok =
                finished.await(
                        20,
                        TimeUnit.SECONDS
                );

        if (!ok) {
            System.out.println(
                    "Warning: main request timeout."
            );
        }

        if (webSocket != null) {
            webSocket.sendClose(
                    WebSocket.NORMAL_CLOSURE,
                    "done"
            ).join();
        }

        double responseTime = -1;

        if (requestTimestamp >= 0 &&
                loadingFinishedTimestamp >= 0) {

            responseTime =
                    (loadingFinishedTimestamp
                            - requestTimestamp)
                            * 1000.0;
        }

        return new Result(
                protocol,
                statusCode,
                responseTime,
                dataSize
        );
    }

    private void sendCommand(
            int id,
            String method,
            String params
    ) {

        String message =
                "{"
                        + "\"id\":" + id + ","
                        + "\"method\":\"" + method + "\","
                        + "\"params\":" + params
                        + "}";

        webSocket.sendText(
                message,
                true
        );
    }

    private String extractWebSocketUrl(
            String json
    ) {

        Pattern pattern =
                Pattern.compile(
                        "\"webSocketDebuggerUrl\"\\s*:\\s*\"([^\"]+)\""
                );

        Matcher matcher =
                pattern.matcher(json);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    private String extractString(
            String json,
            String key
    ) {

        Pattern pattern =
                Pattern.compile(
                        "\"" + key
                                + "\"\\s*:\\s*\"([^\"]*)\""
                );

        Matcher matcher =
                pattern.matcher(json);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    private double extractDouble(
            String json,
            String key
    ) {

        Pattern pattern =
                Pattern.compile(
                        "\"" + key
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

    private long extractLong(
            String json,
            String key
    ) {

        Pattern pattern =
                Pattern.compile(
                        "\"" + key
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

    private int extractInt(
            String json,
            String key
    ) {

        Pattern pattern =
                Pattern.compile(
                        "\"" + key
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

    private String escapeJson(
            String text
    ) {

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    private class Listener
            implements WebSocket.Listener {

        private final StringBuilder buffer =
                new StringBuilder();

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

                // DEBUG
                System.out.println();
                System.out.println("========== CDP EVENT ==========");

                if (message.length() > 1200) {

                    System.out.println(
                            message.substring(0, 1200)
                    );

                    System.out.println(
                            "... [TRUNCATED]"
                    );

                } else {

                    System.out.println(message);
                }

                processMessage(message);
            }

            webSocket.request(1);

            return null;
        }

        private void processMessage(
                String message
        ) {

            /*
             * 找主文档请求
             */
            if (message.contains(
                    "\"method\":\"Network.requestWillBeSent\""
            )) {

                String url =
                        extractString(
                                message,
                                "url"
                        );

                if (message.contains("\"type\":\"Document\"")
                        && url != null
                        && url.startsWith(targetUrl)
                        && mainRequestId == null) {

                    mainRequestId =
                            extractString(
                                    message,
                                    "requestId"
                            );

                    requestTimestamp =
                            extractDouble(
                                    message,
                                    "timestamp"
                            );

                    System.out.println(
                            ">>> MAIN REQUEST FOUND <<<"
                    );

                    System.out.println(
                            "Request ID = "
                                    + mainRequestId
                    );

                    System.out.println(
                            "URL = "
                                    + url
                    );

                    System.out.println(
                            "Timestamp = "
                                    + requestTimestamp
                    );
                }
            }

            /*
             * 找同一个 requestId 的 response
             */
            if (message.contains(
                    "\"method\":\"Network.responseReceived\""
            )) {

                String requestId =
                        extractString(
                                message,
                                "requestId"
                        );

                if (mainRequestId != null
                        && mainRequestId.equals(
                        requestId)) {

                    String response =
                            extractObject(
                                    message,
                                    "\"response\":{"
                            );

                    if (response != null) {

                        protocol =
                                extractString(
                                        response,
                                        "protocol"
                                );

                        statusCode =
                                extractInt(
                                        response,
                                        "status"
                                );

                        responseTimestamp =
                                extractDouble(
                                        message,
                                        "timestamp"
                                );

                        System.out.println(
                                "Protocol: "
                                        + protocol
                        );

                        System.out.println(
                                "Status: "
                                        + statusCode
                        );
                    }
                }
            }

            /*
             * 找同一个 requestId 的 loadingFinished
             */
            if (message.contains(
                    "\"method\":\"Network.loadingFinished\""
            )) {

                String requestId =
                        extractString(
                                message,
                                "requestId"
                        );

                if (mainRequestId != null
                        && mainRequestId.equals(
                        requestId)) {

                    dataSize =
                            extractLong(
                                    message,
                                    "encodedDataLength"
                            );

                    loadingFinishedTimestamp =
                            extractDouble(
                                    message,
                                    "timestamp"
                            );

                    System.out.println(
                            ">>> MAIN REQUEST FINISHED <<<"
                    );

                    finished.countDown();
                }
            }
        }


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
                    json.indexOf(
                            "{",
                            index
                    );

            if (begin == -1) {
                return null;
            }

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
                        && (i == 0
                        || json.charAt(i - 1)
                        != '\\')) {

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

        @Override
        public void onOpen(
                WebSocket socket
        ) {

            socket.request(1);
        }
    }

    public static class Result {

        public final String protocol;

        public final int statusCode;

        public final double responseTimeMs;

        public final long dataSize;

        public Result(
                String protocol,
                int statusCode,
                double responseTimeMs,
                long dataSize
        ) {

            this.protocol = protocol;

            this.statusCode = statusCode;

            this.responseTimeMs =
                    responseTimeMs;

            this.dataSize = dataSize;
        }
    }
}