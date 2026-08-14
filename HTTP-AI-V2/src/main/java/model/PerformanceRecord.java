package model;


public class PerformanceRecord {


    private String protocol;
    private int run;
    private long responseTime;
    private long ttfb;
    private long dataSize;
    private double throughput;
    private int statusCode;
    private long timestamp;
    private double averageRTT;
    private double minRTT;
    private double maxRTT;
    private double packetLoss;
    private double jitter;



    public PerformanceRecord(
            String protocol,
            int run,
            long responseTime,
            long ttfb,
            long dataSize,
            double throughput,
            int statusCode,
            double averageRTT,
            double minRTT,
            double maxRTT,
            double packetLoss,
            double jitter
    ){

        this.protocol = protocol;
        this.run = run;
        this.responseTime = responseTime;
        this.ttfb = ttfb;
        this.dataSize = dataSize;
        this.throughput = throughput;
        this.statusCode = statusCode;
        this.averageRTT = averageRTT;
        this.minRTT = minRTT;
        this.maxRTT = maxRTT;
        this.packetLoss = packetLoss;
        this.jitter = jitter;
        this.timestamp =
                System.currentTimeMillis();
    }



    public String toCSV(){

        return protocol + "," +
                run + "," +
                timestamp + "," +
                averageRTT + "," +
                minRTT + "," +
                maxRTT + "," +
                packetLoss + "," +
                jitter + "," +
                responseTime + "," +
                ttfb + "," +
                dataSize + "," +
                throughput + "," +
                statusCode;

    }


}