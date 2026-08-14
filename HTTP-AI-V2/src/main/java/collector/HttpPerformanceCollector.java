package collector;

import metrics.NetworkMetrics;
import metrics.PingMonitor;
import model.PerformanceRecord;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpPerformanceCollector {

    public PerformanceRecord test(
            String url,
            String protocol,
            int run
    ) throws Exception {

        URL target = new URL(url);

        HttpURLConnection connection =
                (HttpURLConnection) target.openConnection();

        connection.setInstanceFollowRedirects(true);
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(30000);
        connection.setRequestProperty("Connection", "close");
        connection.setRequestProperty(
                "User-Agent",
                "HTTP-AI-Research-Agent"
        );
        connection.setRequestMethod("GET");

        long start = System.nanoTime();

        // getResponseCode() returns after response headers arrive.
        int status = connection.getResponseCode();

        long headersReceived =
                System.nanoTime();

        InputStream input = connection.getInputStream();

        long firstByte = -1;
        long size = 0;

        byte[] buffer = new byte[8192];
        int length;

        while ((length = input.read(buffer)) != -1) {
            if (firstByte < 0) {
                firstByte = System.nanoTime();
            }
            size += length;
        }

        long end = System.nanoTime();

        input.close();
        connection.disconnect();

        long responseTime =
                (end - start) / 1_000_000;

        // TTFB = request start -> response headers.
        long ttfb =
                (headersReceived - start) / 1_000_000;

        double seconds =
                (end - start) / 1_000_000_000.0;

        // Unified unit: Mbps.
        double throughput =
                seconds > 0
                        ? (size * 8.0)
                        / seconds
                        / 1_000_000.0
                        : 0.0;

        PingMonitor pingMonitor =
                new PingMonitor();

        NetworkMetrics networkMetrics =
                pingMonitor.measure(
                        target.getHost()
                );

        return new PerformanceRecord(
                protocol,
                run,
                responseTime,
                ttfb,
                size,
                throughput,
                status,
                networkMetrics.getAverageRTT(),
                networkMetrics.getMinRTT(),
                networkMetrics.getMaxRTT(),
                networkMetrics.getPacketLoss(),
                networkMetrics.getJitter()
        );
    }
}