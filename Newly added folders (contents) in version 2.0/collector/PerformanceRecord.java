package model;

public class PerformanceRecord {

    private String protocol;
    private long responseTime;
    private long ttfb;
    private long dataSize;
    private double throughput;


    public PerformanceRecord(
            String protocol,
            long responseTime,
            long ttfb,
            long dataSize,
            double throughput
    ){
        this.protocol = protocol;
        this.responseTime = responseTime;
        this.ttfb = ttfb;
        this.dataSize = dataSize;
        this.throughput = throughput;
    }


    public String toCSV(){

        return protocol + "," +
                responseTime + "," +
                ttfb + "," +
                dataSize + "," +
                throughput;

    }

}