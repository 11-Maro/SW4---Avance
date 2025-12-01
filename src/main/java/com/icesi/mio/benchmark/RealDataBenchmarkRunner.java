package com.icesi.mio.benchmark;

import com.icesi.mio.graph.GraphBuilder;
import com.icesi.mio.model.Line;
import com.icesi.mio.model.RouteGraph;
import com.icesi.mio.model.Stop;
import com.icesi.mio.parser.LineParser;
import com.icesi.mio.parser.LineStopParser;
import com.icesi.mio.parser.StopParser;
import com.icesi.mio.util.Constants;
import com.icesi.mio.distributed.DistributedComputeCoordinator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

/**
 * Benchmark que usa los datos reales del SITM-MIO (carpeta MIO) y permite
 * dos modos de operación:
 *
 *  1) Procesamiento "full streaming" para archivos gigantes (como
 *     datagrams4history.csv ~70GB), leyendo el CSV secuencialmente sin
 *     cargarlo completo en memoria.
 *  2) (Opcional) Benchmark por número de workers usando solo una muestra
 *     del archivo, manteniendo compatibilidad con el enfoque distribuido.
 */
public class RealDataBenchmarkRunner {
    private static final Logger logger = LoggerFactory.getLogger(RealDataBenchmarkRunner.class);

    public static void main(String[] args) throws Exception {
        // args[0] (opcional): directorio base donde está la carpeta MIO.
        //  - Si se omite, se usa Constants.DEFAULT_DATA_DIR (ya configurado a "proyecto-mio/MIO/").
        //  - Si se pasa, puede ser directamente la carpeta MIO o el directorio que la contiene.
        String baseArg = args.length > 0 ? args[0] : null;
        String dataDir;

        if (baseArg == null) {
            dataDir = Constants.DEFAULT_DATA_DIR;
        } else {
            File f = new File(baseArg);
            if (f.isDirectory() && new File(f, "lines-241.csv").exists()) {
                // baseArg ya es la carpeta MIO
                dataDir = f.getPath() + File.separator;
            } else if (f.isDirectory() && new File(f, "MIO").isDirectory()) {
                // baseArg es la carpeta proyecto-mio que contiene MIO
                dataDir = new File(f, "MIO").getPath() + File.separator;
            } else {
                // Fallback: usar constante
                dataDir = Constants.DEFAULT_DATA_DIR;
            }
        }

        String linesFile = dataDir + Constants.LINES_FILE;
        String stopsFile = dataDir + Constants.STOPS_FILE;
        String lineStopsFile = dataDir + Constants.LINESTOPS_FILE;
        String datagramsFile = args.length > 1 ? args[1] : dataDir + "datagrams4history.csv";

        logger.info("Usando dataDir={}, datagramsFile={}", dataDir, datagramsFile);

        // 1) Cargar datos base y construir grafos
        LineParser lp = new LineParser();
        StopParser sp = new StopParser();
        LineStopParser lsp = new LineStopParser();

        Map<Integer, Line> lines = lp.parseLines(linesFile);
        Map<Integer, Stop> stops = sp.parseStops(stopsFile);
        var lineStops = lsp.parseLineStops(lineStopsFile);

        GraphBuilder gb = new GraphBuilder(lines, stops, lineStops);
        Map<Integer, RouteGraph> graphs = gb.buildGraphs();

        logger.info("Grafos construidos: {} rutas", graphs.size());

        // Para decidir el modo, miramos el tamaño del archivo de datagramas.
        File df = new File(datagramsFile);
        long sizeBytes = df.length();
        double sizeGB = sizeBytes / (1024.0 * 1024.0 * 1024.0);
        logger.info("Tamaño de archivo de datagramas: {} bytes (~{} GB)", sizeBytes, String.format("%.2f", sizeGB));

        String outDir = "output/real-benchmark/";
        new File(outDir).mkdirs();

        // Modo único: benchmark por número de workers usando SOLO una muestra
        // de N filas del archivo de datagramas (para no agotar memoria ni disco).
        int[] workersList = {1, 2, 4, 8};
        int maxRowsSample = 1_000_000; // 1 millón de filas para pruebas razonables

        try (FileWriter report = new FileWriter(outDir + "results.csv")) {
            report.write("workers,time_ms,arc_count\\n");

            for (int workers : workersList) {
                logger.info("\n== Ejecutando benchmark real con workers={} (muestra de {} filas) ==", workers, maxRowsSample);

                SamplingPartitionManager pm = new SamplingPartitionManager(workers, maxRowsSample);
                DistributedComputeCoordinator dcc = new DistributedComputeCoordinator(pm, workers, 3600);

                long start = System.currentTimeMillis();
                var res = dcc.runJob(datagramsFile, graphs);
                long end = System.currentTimeMillis();

                long elapsed = end - start;
                int arcCount = res.size();

                logger.info("workers={} -> time_ms={} arcCount={} ", workers, elapsed, arcCount);
                report.write(workers + "," + elapsed + "," + arcCount + "\\n");
            }
        }

        logger.info("\nBenchmark real completo → {}", new File("output/real-benchmark/results.csv").getPath());
    }
}
