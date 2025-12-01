package com.icesi.mio.tools;

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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DumpT31 {
    public static void main(String[] args) throws Exception {
        String dataDir = Constants.DEFAULT_DATA_DIR;
        String linesFile = dataDir + Constants.LINES_FILE;
        String stopsFile = dataDir + Constants.STOPS_FILE;
        String lineStopsFile = dataDir + Constants.LINESTOPS_FILE;

        LineParser lp = new LineParser();
        StopParser sp = new StopParser();
        LineStopParser lsp = new LineStopParser();

        Map<Integer, Line> lines = lp.parseLines(linesFile);
        Map<Integer, Stop> stops = sp.parseStops(stopsFile);
        List<LineStop> lineStops = lsp.parseLineStops(lineStopsFile);

        int t31LineId = 131;

        System.out.println("=== LineStops para T31 (lineId=" + t31LineId + ") ===");
        List<LineStop> t31Stops = lineStops.stream()
                .filter(ls -> ls.getLineId() == t31LineId)
                .sorted((a, b) -> {
                    int cmp = Integer.compare(a.getOrientation(), b.getOrientation());
                    if (cmp != 0) return cmp;
                    // agrupar por variante dentro de la orientación
                    cmp = a.getLineVariant().compareTo(b.getLineVariant());
                    if (cmp != 0) return cmp;
                    return Integer.compare(a.getStopSequence(), b.getStopSequence());
                })
                .collect(Collectors.toList());

        System.out.printf("Total LineStops T31: %d\n", t31Stops.size());
        System.out.println("orientation,lineVariant,stopSequence,stopId,shortName,longName");
        for (LineStop ls : t31Stops) {
            Stop s = stops.get(ls.getStopId());
            String shortName = s != null ? s.getShortName() : "?";
            String longName = s != null ? s.getLongName() : "?";
            System.out.printf("%d,%s,%d,%d,%s,%s\n",
                    ls.getOrientation(),
                    ls.getLineVariant(),
                    ls.getStopSequence(),
                    ls.getStopId(),
                    shortName,
                    longName);
        }

        System.out.println("\n=== Arcos para T31 construidos por GraphBuilder ===");
        GraphBuilder gb = new GraphBuilder(lines, stops, lineStops);
        Map<Integer, RouteGraph> graphs = gb.buildGraphs();
        RouteGraph g = graphs.get(t31LineId);

        if (g == null) {
            System.out.println("No se encontró grafo para T31 (lineId=" + t31LineId + ")");
            return;
        }

        GraphPrinter printer = new GraphPrinter();
        printer.printRouteGraph(g);
    }
}
