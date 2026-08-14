package client;

import metrics.NetworkMetrics;
import metrics.PingMonitor;
import model.PerformanceRecord;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Http2PerformanceCollector {

    public PerformanceRecord test(
            String url,
            int run
    ) throws Exception {

        HttpClient client =
                HttpClient.newBuilder()
                        .version(
                                HttpClient.Version.HTTP_2
                        )
                        .build();

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

        long start = System.nanoTime();

        HttpResponse<InputStream> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofInputStream()
                );

        // client.send() returns after response headers are available.
        long headersReceived =
                System.nanoTime();

        InputStream input =
                response.body();

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
                        URI.create(url).getHost()
                );

        return new PerformanceRecord(
                "HTTP/2",
                run,
                responseTime,
                ttfb,
                size,
                throughput,
                response.statusCode(),
                networkMetrics.getAverageRTT(),
                networkMetrics.getMinRTT(),
                networkMetrics.getMaxRTT(),
                networkMetrics.getPacketLoss(),
                networkMetrics.getJitter()
        );
    }
}
