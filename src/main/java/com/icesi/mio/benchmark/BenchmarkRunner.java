package com.icesi.mio.benchmark;

import java.io.FileWriter;

public class BenchmarkRunner {

    public static void main(String[] args) throws Exception {

        String outDir = "output/benchmark/";
        new java.io.File(outDir).mkdirs();

        // Tamaños a probar (reducidos para ejecución local razonable)
        int[] sizes = {200_000, 400_000, 800_000};
        int[] workers = {1, 2, 4, 8};

        try (FileWriter report = new FileWriter(outDir + "results.csv")) {
            report.write("size,workers,time_ms\n");

            for (int size : sizes) {
                String csv = outDir + "synthetic_" + size + ".csv";
                DataGenerator.generateLineStops(csv, size);

                for (int w : workers) {
                    System.out.println("\n== Ejecutando size=" + size + " workers=" + w);
                    long time = DistributedProcessor.processCSV(csv, w);

                    report.write(size + "," + w + "," + time + "\n");
                }
            }

            System.out.println("\nBenchmark completo → output/benchmark/results.csv");
        }
    }
}
