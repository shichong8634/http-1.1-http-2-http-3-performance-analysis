package utils;


import model.PerformanceRecord;

import java.io.FileWriter;
import java.io.IOException;


public class CsvWriter {


    private FileWriter writer;



    public CsvWriter(String filename)
            throws IOException {


        writer =
                new FileWriter(filename);


        writer.write(
                "Protocol," +
                        "Run," +
                        "Timestamp," +
                        "AverageRTT," +
                        "MinRTT," +
                        "MaxRTT," +
                        "PacketLoss," +
                        "Jitter," +
                        "ResponseTime," +
                        "TTFB," +
                        "DataSize," +
                        "ThroughputMbps," +
                        "StatusCode\n"
        );

    }



    public void write(
            PerformanceRecord record
    )
            throws IOException {


        writer.write(
                record.toCSV()
        );


        writer.write("\n");

        writer.flush();

    }



    public void close()
            throws IOException {


        writer.close();

    }

}