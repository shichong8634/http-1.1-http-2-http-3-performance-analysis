import client.Http3BrowserCollector;
import model.PerformanceRecord;

public class TestHTTP3Browser {

    public static void main(String[] args)
            throws Exception {

        Http3BrowserCollector collector =
                new Http3BrowserCollector();

        PerformanceRecord record =
                collector.test(
                        "https://www.yunlong-performance-research.com/index.html",
                        1
                );

        System.out.println();
        System.out.println("========== HTTP/3 RESULT ==========");

        System.out.println(
                record.toCSV()
        );
    }
}