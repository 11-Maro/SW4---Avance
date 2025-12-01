package com.icesi.mio;

import com.icesi.mio.graph.GraphBuilder;
import com.icesi.mio.graph.GraphPrinter;
import com.icesi.mio.model.Line;
import com.icesi.mio.model.LineStop;
import com.icesi.mio.model.RouteGraph;
import com.icesi.mio.model.Stop;
import com.icesi.mio.parser.LineParser;
import com.icesi.mio.parser.LineStopParser;
import com.icesi.mio.parser.StopParser;
import com.icesi.mio.util.Constants;
import com.icesi.mio.worker.StreamingFullFileProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase principal para el análisis de grafos del SITM-MIO
 * Calcula velocidades promedio de arcos usando datos históricos y streaming
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            logger.info("Iniciando análisis de grafos SITM-MIO con cálculo de velocidades...");
            
            // Determinar rutas de archivos
            String dataDir = args.length > 0 ? args[0] : Constants.DEFAULT_DATA_DIR;
            String linesFile = dataDir + Constants.LINES_FILE;
            String stopsFile = dataDir + Constants.STOPS_FILE;
            String lineStopsFile = dataDir + Constants.LINESTOPS_FILE;
            String historicalDataFile = dataDir + "datagrams4history.csv";
            String streamingDataFile = dataDir + "datagrams4streaming.csv";

            // Verificar que existan los archivos básicos
            validateFiles(linesFile, stopsFile, lineStopsFile);

            // 1. Parsear datos
            logger.info("=== FASE 1: PARSEO DE DATOS ===");
            LineParser lineParser = new LineParser();
            StopParser stopParser = new StopParser();
            LineStopParser lineStopParser = new LineStopParser();

            Map<Integer, Line> lines = lineParser.parseLines(linesFile);
            Map<Integer, Stop> stops = stopParser.parseStops(stopsFile);
            List<LineStop> lineStops = lineStopParser.parseLineStops(lineStopsFile);

            // 2. Construir grafos
            logger.info("\n=== FASE 2: CONSTRUCCIÓN DE GRAFOS ===");
            GraphBuilder graphBuilder = new GraphBuilder(lines, stops, lineStops);
            Map<Integer, RouteGraph> graphs = graphBuilder.buildGraphs();
            graphBuilder.printStatistics(graphs);

            // 3. Calcular velocidades promedio usando datos históricos
            Map<Long, Double> historicalSpeeds = new HashMap<>();
            if (new File(historicalDataFile).exists()) {
                logger.info("\n=== FASE 3: CÁLCULO DE VELOCIDADES PROMEDIO (DATOS HISTÓRICOS) ===");
                logger.info("Procesando archivo: {}", historicalDataFile);
                StreamingFullFileProcessor historicalProcessor = new StreamingFullFileProcessor(historicalDataFile, graphs);
                historicalSpeeds = historicalProcessor.process();
                logger.info("Velocidades calculadas para {} arcos", historicalSpeeds.size());
            } else {
                logger.warn("Archivo de datos históricos no encontrado: {}", historicalDataFile);
            }

            // 4. (BONUS) Actualizar velocidades con datos de streaming
            Map<Long, Double> streamingSpeeds = new HashMap<>();
            if (new File(streamingDataFile).exists()) {
                logger.info("\n=== FASE 4: ACTUALIZACIÓN CON DATOS DE STREAMING (BONUS) ===");
                logger.info("Procesando archivo: {}", streamingDataFile);
                StreamingFullFileProcessor streamingProcessor = new StreamingFullFileProcessor(streamingDataFile, graphs);
                streamingSpeeds = streamingProcessor.process();
                logger.info("Velocidades actualizadas para {} arcos", streamingSpeeds.size());
                
                // Combinar resultados: streaming tiene prioridad sobre históricos
                for (Map.Entry<Long, Double> entry : streamingSpeeds.entrySet()) {
                    historicalSpeeds.put(entry.getKey(), entry.getValue());
                }
                logger.info("Total de arcos con velocidad: {}", historicalSpeeds.size());
            } else {
                logger.warn("Archivo de datos de streaming no encontrado: {}", streamingDataFile);
            }

            // 5. Mostrar resultados en consola con velocidades
            logger.info("\n=== FASE 5: VISUALIZACIÓN DE RESULTADOS CON VELOCIDADES ===");
            GraphPrinter printer = new GraphPrinter();
            printer.printAllGraphsWithSpeeds(graphs, historicalSpeeds);

            logger.info("\n✓ Análisis completado exitosamente");
            logger.info("Total de arcos con velocidad promedio calculada: {}", historicalSpeeds.size());

        } catch (Exception e) {
            logger.error("Error durante el análisis", e);
            System.err.println("\n✗ Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Valida que existan los archivos necesarios
     */
    private static void validateFiles(String... files) {
        for (String file : files) {
            File f = new File(file);
            if (!f.exists()) {
                throw new RuntimeException("Archivo no encontrado: " + file);
            }
            logger.info("Archivo encontrado: {}", file);
        }
    }
}
