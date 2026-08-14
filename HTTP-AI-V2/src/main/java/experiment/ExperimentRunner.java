package experiment;

import client.Http2PerformanceCollector;
import client.Http3BrowserCollector;
import collector.HttpPerformanceCollector;
import model.PerformanceRecord;
import utils.CsvWriter;

public class ExperimentRunner {

    private final HttpPerformanceCollector http1Collector;
    private final Http2PerformanceCollector http2Collector;
    private final Http3BrowserCollector http3Collector;

    public ExperimentRunner() {

        http1Collector =
                new HttpPerformanceCollector();

        http2Collector =
                new Http2PerformanceCollector();

        http3Collector =
                new Http3BrowserCollector();
    }


    // ============================================================
    // HTTP/1.1
    // ============================================================

    public void runHTTP1(
            String url,
            int times,
            String workload
    ) throws Exception {

        CsvWriter csv =
                new CsvWriter(
                        "dataset_HTTP1_" + workload + ".csv"
                );

        for (int i = 1; i <= times; i++) {

            System.out.println(
                    "HTTP/1.1 [" + workload + "] Experiment "
                            + i + "/" + times
            );

            try {
                PerformanceRecord record =
                        http1Collector.test(
                                url,
                                "HTTP/1.1",
                                i
                        );

                System.out.println(
                        record.toCSV()
                );

                csv.write(record);

            } catch (Exception e) {

                System.out.println(
                        "HTTP/1.1 Experiment "
                                + i
                                + " FAILED: "
                                + e.getClass().getSimpleName()
                );
            }

            Thread.sleep(2000);
        }

        csv.close();

        System.out.println(
                "HTTP/1.1 [" + workload + "] Finished"
        );
    }


    // ============================================================
    // HTTP/2
    // ============================================================

    public void runHTTP2(
            String url,
            int times,
            String workload
    ) throws Exception {

        CsvWriter csv =
                new CsvWriter(
                        "dataset_HTTP2_" + workload + ".csv"
                );

        for (int i = 1; i <= times; i++) {

            System.out.println(
                    "HTTP/2 [" + workload + "] Experiment "
                            + i + "/" + times
            );

            PerformanceRecord record =
                    http2Collector.test(
                            url,
                            i
                    );

            System.out.println(
                    record.toCSV()
            );

            csv.write(record);

            Thread.sleep(2000);
        }

        csv.close();

        System.out.println(
                "HTTP/2 [" + workload + "] Finished"
        );
    }


    // ============================================================
    // HTTP/3
    // ============================================================

    public void runHTTP3(
            String url,
            int times,
            String workload
    ) throws Exception {

        CsvWriter csv =
                new CsvWriter(
                        "dataset_HTTP3_" + workload + ".csv"
                );

        for (int i = 1; i <= times; i++) {

            System.out.println(
                    "HTTP/3 [" + workload + "] Experiment "
                            + i + "/" + times
            );

            PerformanceRecord record =
                    http3Collector.test(
                            url,
                            i
                    );

            System.out.println(
                    record.toCSV()
            );

            csv.write(record);

            Thread.sleep(2000);
        }

        csv.close();

        System.out.println(
                "HTTP/3 [" + workload + "] Finished"
        );
    }


    // ============================================================
    // ALL PROTOCOLS
    // ============================================================

    public void runAll(
            String url,
            int times,
            String workload
    ) throws Exception {

        System.out.println();
        System.out.println("========================================");
        System.out.println("HTTP PERFORMANCE EXPERIMENT");
        System.out.println("HTTP/1.1 + HTTP/2 + HTTP/3");
        System.out.println("URL: " + url);
        System.out.println("Runs: " + times);
        System.out.println("Workload: " + workload);
        System.out.println("========================================");

        runHTTP1(url, times, workload);

        runHTTP2(url, times, workload);

        runHTTP3(url, times, workload);

        System.out.println();
        System.out.println("========================================");
        System.out.println("ALL PROTOCOLS FINISHED");
        System.out.println("========================================");
    }
}