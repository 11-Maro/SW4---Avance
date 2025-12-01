package com.icesi.mio.benchmark;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class DataGenerator {

    public static void generateLineStops(String outputFile, int count) throws IOException {
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write("LINESTOPID,STOPSEQUENCE,ORIENTATION,LINEID,STOPID,PLANVERSIONID,LINEVARIANT,LINEVARIANTTYPE\n");

            Random rand = new Random();

            for (int i = 1; i <= count; i++) {
                int stopSeq = i % 200;
                int orientation = rand.nextInt(2); // 0 o 1
                int lineId = rand.nextInt(2000) + 1;
                int stopId = rand.nextInt(5000) + 1;
                writer.write(i + "," + stopSeq + "," + orientation + "," + lineId + "," + stopId + ",241,,\n");
            }

            System.out.println("Archivo generado: " + outputFile);
        }
    }
}
