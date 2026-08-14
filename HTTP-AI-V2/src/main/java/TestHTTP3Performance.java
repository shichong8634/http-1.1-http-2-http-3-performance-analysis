import client.Http3PerformanceCollector;

public class TestHTTP3Performance {

    public static void main(String[] args)
            throws Exception {

        Http3PerformanceCollector collector =
                new Http3PerformanceCollector();

        Http3PerformanceCollector.Result result =
                collector.test(
                        "https://www.yunlong-performance-research.com"
                );

        System.out.println();
        System.out.println("========== HTTP/3 RESULT ==========");

        System.out.println(
                "Protocol: " + result.protocol
        );

        System.out.println(
                "Status Code: " + result.statusCode
        );

        System.out.println(
                "Response Time: "
                        + result.responseTimeMs
                        + " ms"
        );

        System.out.println(
                "Data Size: "
                        + result.dataSize
                        + " bytes"
        );
    }
}