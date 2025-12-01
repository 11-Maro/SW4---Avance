package com.icesi.mio.distributed;

import com.icesi.mio.graph.GraphBuilder;
import com.icesi.mio.model.RouteGraph;
import com.icesi.mio.model.Line;
import com.icesi.mio.model.Stop;
import com.icesi.mio.parser.LineParser;
import com.icesi.mio.parser.LineStopParser;
import com.icesi.mio.parser.StopParser;
import com.icesi.mio.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Demo runner que carga el grafo y ejecuta el DistributedComputeCoordinator contra un CSV de datagramas.
 */
public class DemoRunner {
    private static final Logger logger = LoggerFactory.getLogger(DemoRunner.class);

    public static void main(String[] args) throws Exception {
        String dataDir = args.length > 0 ? args[0] : Constants.DEFAULT_DATA_DIR;
        String linesFile = dataDir + Constants.LINES_FILE;
        String stopsFile = dataDir + Constants.STOPS_FILE;
        String lineStopsFile = dataDir + Constants.LINESTOPS_FILE;
        // Por defecto usamos el archivo histórico real de datagramas; se puede sobreescribir por argumento
        String datagramsFile = args.length > 1 ? args[1] : dataDir + "datagrams4history.csv";

        LineParser lp = new LineParser();
        StopParser sp = new StopParser();
        LineStopParser lsp = new LineStopParser();

        Map<Integer, Line> lines = lp.parseLines(linesFile);
        Map<Integer, Stop> stops = sp.parseStops(stopsFile);
        var lineStops = lsp.parseLineStops(lineStopsFile);

        GraphBuilder gb = new GraphBuilder(lines, stops, lineStops);
        Map<Integer, RouteGraph> graphs = gb.buildGraphs();

        // Construir índice arcId -> Arc para consulta legible
        var arcIndex = com.icesi.mio.util.ArcIndexBuilder.buildIndex(graphs);

        PartitionManager pm = new PartitionManager(4);
        DistributedComputeCoordinator dcc = new DistributedComputeCoordinator(pm, 4, 600);

        long start = System.currentTimeMillis();
        var res = dcc.runJob(datagramsFile, graphs);
        long end = System.currentTimeMillis();

        logger.info("Averages computed: {} arcs", res.size());
        logger.info("Processing time: {} ms", (end - start));

        // Imprimir algunos resultados
        if (!res.isEmpty()) {
            logger.info("=== SAMPLE RESULTS ===");
            int printed = 0;
            for (var e : res.entrySet()) {
                long arcId = e.getKey();
                double avg = e.getValue();
                var arc = arcIndex.get(arcId);
                if (arc != null) {
                    logger.info("arcId={} line={} orientation={} seq={} from={} to={} avgSpeedKph={}",
                            arcId, arc.getLineId(), arc.getOrientationName(), arc.getSequence(), arc.getFromStop().getShortName(), arc.getToStop().getShortName(), avg);
                } else {
                    logger.info("arcId={} avgSpeedKph={}", arcId, avg);
                }
                if (++printed >= 20) break;
            }
        }
    }
}
