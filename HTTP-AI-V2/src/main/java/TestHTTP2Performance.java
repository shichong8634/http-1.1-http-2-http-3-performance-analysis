import client.Http2PerformanceCollector;
import model.PerformanceRecord;


public class TestHTTP2Performance {


    public static void main(String[] args)
            throws Exception {


        Http2PerformanceCollector collector =
                new Http2PerformanceCollector();


        PerformanceRecord record =
                collector.test(
                        "https://www.yunlong-performance-research.com",
                        1
                );


        System.out.println(
                record.toCSV()
        );


    }

}