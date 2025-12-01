package com.icesi.mio.benchmark;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DistributedProcessor {

    /**
     * Procesa el CSV simulando una carga de trabajo, repartiendo el archivo
     * en bloques grandes por worker para reducir overhead de tareas y
     * obtener métricas de escalabilidad más realistas.
     */
    public static long processCSV(String file, int workers) throws Exception {
        long start = System.currentTimeMillis();

        // 1) Cargar todas las líneas (sin header) en memoria.
        // Para el benchmark sintético con tamaños en el orden de 10^5 - 10^6
        // es razonable y simplifica el diseño.
        java.util.List<String> lines = new java.util.ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); // saltar header
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }

        if (lines.isEmpty() || workers <= 1) {
            // Caso trivial: procesar secuencialmente.
            for (String current : lines) {
                String[] parts = current.split(",");
                int seq = Integer.parseInt(parts[1]);
                int orientation = Integer.parseInt(parts[2]);
            }
            long end = System.currentTimeMillis();
            return end - start;
        }

        // 2) Crear un pool fijo y asignar a cada worker un bloque contiguo de líneas.
        ExecutorService pool = Executors.newFixedThreadPool(workers);

        int total = lines.size();
        int chunkSize = (total + workers - 1) / workers; // ceil(total / workers)

        for (int w = 0; w < workers; w++) {
            final int from = w * chunkSize;
            final int to = Math.min(from + chunkSize, total);
            if (from >= to) break; // puede pasar si hay más workers que líneas

            pool.submit(() -> {
                for (int i = from; i < to; i++) {
                    String current = lines.get(i);
                    // Simulación de carga: parseo simple como antes
                    String[] parts = current.split(",");
                    int seq = Integer.parseInt(parts[1]);
                    int orientation = Integer.parseInt(parts[2]);
                }
            });
        }

        pool.shutdown();
        while (!pool.isTerminated()) { Thread.sleep(10); }

        long end = System.currentTimeMillis();
        return end - start;
    }
}
