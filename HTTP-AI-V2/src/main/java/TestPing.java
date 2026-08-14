import metrics.NetworkMetrics;
import metrics.PingMonitor;


public class TestPing {


    public static void main(String[] args) {


        PingMonitor monitor =
                new PingMonitor();


        NetworkMetrics result =
                monitor.measure(
                        "www.yunlong-performance-research.com"
                );


        System.out.println(
                "Average RTT: "
                        + result.getAverageRTT()
        );


        System.out.println(
                "Min RTT: "
                        + result.getMinRTT()
        );


        System.out.println(
                "Max RTT: "
                        + result.getMaxRTT()
        );


        System.out.println(
                "Packet Loss: "
                        + result.getPacketLoss()
        );


        System.out.println(
                "Jitter: "
                        + result.getJitter()
        );

    }

}