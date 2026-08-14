package metrics;

public class NetworkMetrics {

    private double averageRTT;

    private double minRTT;

    private double maxRTT;

    private double packetLoss;

    private double jitter;


    public NetworkMetrics(
            double averageRTT,
            double minRTT,
            double maxRTT,
            double packetLoss,
            double jitter
    ){

        this.averageRTT = averageRTT;
        this.minRTT = minRTT;
        this.maxRTT = maxRTT;
        this.packetLoss = packetLoss;
        this.jitter = jitter;

    }


    public double getAverageRTT(){
        return averageRTT;
    }


    public double getMinRTT(){
        return minRTT;
    }


    public double getMaxRTT(){
        return maxRTT;
    }


    public double getPacketLoss(){
        return packetLoss;
    }


    public double getJitter(){
        return jitter;
    }

}