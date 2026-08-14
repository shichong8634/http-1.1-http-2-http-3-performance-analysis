package metrics;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PingMonitor {


    private static final int PING_COUNT = 5;

    private long extractRTT(String line) {


        try {


            int timeIndex =
                    line.indexOf("时间=");


            if(timeIndex == -1){

                timeIndex =
                        line.indexOf("time=");

            }


            if(timeIndex == -1){

                return -1;

            }


            int start =
                    timeIndex
                            +
                            (line.contains("时间=") ? 3 : 5);



            int end =
                    line.indexOf(
                            "ms",
                            start
                    );


            return Long.parseLong(
                    line.substring(
                            start,
                            end
                    ).trim()
            );


        } catch(Exception e){


            return -1;

        }

    }


    public NetworkMetrics measure(String host) {


        List<Long> rttValues = new ArrayList<>();

        int sent = 0;

        int received = 0;


        try {


            Process process =
                    Runtime.getRuntime().exec(
                            "ping -n " + PING_COUNT + " " + host
                    );


            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    process.getInputStream(),
                                    "GBK"
                            )
                    );


            String line;


            while ((line = reader.readLine()) != null) {
                System.out.println(line);


                if (line.contains("TTL=")) {

                    received++;

                    long rtt = extractRTT(line);

                    if (rtt != -1) {
                        rttValues.add(rtt);
                    }

                }

            }


            process.waitFor();
            sent = PING_COUNT;


        } catch (Exception e) {


            e.printStackTrace();

        }



        double packetLoss;


        if (sent == 0) {


            packetLoss = 100.0;


        } else {


            packetLoss =
                    ((sent - received) * 100.0)
                            / sent;

        }



        double averageRTT =
                calculateAverage(rttValues);



        double minRTT =
                calculateMin(rttValues);



        double maxRTT =
                calculateMax(rttValues);



        double jitter =
                calculateJitter(rttValues);



        return new NetworkMetrics(
                averageRTT,
                minRTT,
                maxRTT,
                packetLoss,
                jitter
        );


    }



    private double calculateAverage(
            List<Long> values
    ) {


        if (values.isEmpty()) {

            return -1;

        }


        long sum = 0;


        for (long value : values) {

            sum += value;

        }


        return (double) sum / values.size();

    }



    private double calculateMin(
            List<Long> values
    ) {


        if (values.isEmpty()) {

            return -1;

        }


        return Collections.min(values);

    }



    private double calculateMax(
            List<Long> values
    ) {


        if (values.isEmpty()) {

            return -1;

        }


        return Collections.max(values);

    }



    private double calculateJitter(
            List<Long> values
    ) {


        if (values.size() < 2) {

            return 0;

        }


        double difference = 0;


        for (int i = 1; i < values.size(); i++) {


            difference +=
                    Math.abs(
                            values.get(i)
                                    -
                                    values.get(i - 1)
                    );

        }


        return difference /
                (values.size() - 1);

    }

}