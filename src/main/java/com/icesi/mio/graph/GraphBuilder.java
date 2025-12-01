package com.icesi.mio.graph;

import com.icesi.mio.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Construye los grafos de rutas a partir de los datos parseados
 */
public class GraphBuilder {
    private static final Logger logger = LoggerFactory.getLogger(GraphBuilder.class);

    private final Map<Integer, Line> lines;
    private final Map<Integer, Stop> stops;
    private final List<LineStop> lineStops;

    public GraphBuilder(Map<Integer, Line> lines, Map<Integer, Stop> stops, List<LineStop> lineStops) {
        this.lines = lines;
        this.stops = stops;
        this.lineStops = lineStops;
    }

    /**
     * Construye todos los grafos de rutas
     */
    public Map<Integer, RouteGraph> buildGraphs() {
        logger.info("Construyendo grafos de rutas...");
        
        Map<Integer, RouteGraph> graphs = new HashMap<>();
        
        // Agrupar lineStops por lineId
        Map<Integer, List<LineStop>> lineStopsByLine = groupByLine();
        
        // Crear grafo para cada ruta
        for (Map.Entry<Integer, List<LineStop>> entry : lineStopsByLine.entrySet()) {
            int lineId = entry.getKey();
            List<LineStop> stopsInLine = entry.getValue();
            
            Line line = lines.get(lineId);
            if (line == null) {
                logger.warn("Línea no encontrada: {}", lineId);
                continue;
            }
            
            RouteGraph graph = buildGraphForLine(line, stopsInLine);
            graphs.put(lineId, graph);
        }
        
        logger.info("Total de grafos construidos: {}", graphs.size());
        return graphs;
    }

    /**
     * Construye el grafo para una ruta específica.
     *
     * IMPORTANTE: se respetan las variantes de línea (lineVariant), es decir,
     * los arcos solo se construyen entre paradas consecutivas dentro de la
     * misma orientación Y la misma variante. No se crean arcos "falsos" que
     * crucen de una variante a otra.
     */
    private RouteGraph buildGraphForLine(Line line, List<LineStop> stopsInLine) {
        RouteGraph graph = new RouteGraph(line);

        // Agrupar por orientación y luego por variante
        Map<Integer, Map<String, List<LineStop>>> byOrientationAndVariant = new HashMap<>();

        for (LineStop ls : stopsInLine) {
            int orientation = ls.getOrientation();
            String variant = ls.getLineVariant() == null ? "" : ls.getLineVariant();

            byOrientationAndVariant
                    .computeIfAbsent(orientation, o -> new HashMap<>())
                    .computeIfAbsent(variant, v -> new ArrayList<>())
                    .add(ls);
        }

        // Para cada orientación y variante, ordenar por stopSequence y crear arcos
        for (Map.Entry<Integer, Map<String, List<LineStop>>> orientEntry : byOrientationAndVariant.entrySet()) {
            int orientation = orientEntry.getKey();
            Map<String, List<LineStop>> variants = orientEntry.getValue();

            for (Map.Entry<String, List<LineStop>> varEntry : variants.entrySet()) {
                List<LineStop> group = varEntry.getValue();
                group.sort(Comparator.comparingInt(LineStop::getStopSequence));

                // Solo creamos arcos dentro de este grupo (misma orientación y variante)
                createArcs(graph, group, orientation);
            }
        }

        return graph;
    }

    /**
     * Crea arcos entre paradas consecutivas
     */
    private void createArcs(RouteGraph graph, List<LineStop> orderedStops, int orientation) {
        for (int i = 0; i < orderedStops.size() - 1; i++) {
            LineStop current = orderedStops.get(i);
            LineStop next = orderedStops.get(i + 1);
            
            Stop fromStop = stops.get(current.getStopId());
            Stop toStop = stops.get(next.getStopId());
            
            if (fromStop != null && toStop != null) {
                Arc arc = new Arc(
                    current.getLineId(),
                    orientation,
                    fromStop,
                    toStop,
                    current.getStopSequence()
                );
                graph.addArc(arc);
            } else {
                if (fromStop == null) {
                    logger.warn("Parada origen no encontrada: {}", current.getStopId());
                }
                if (toStop == null) {
                    logger.warn("Parada destino no encontrada: {}", next.getStopId());
                }
            }
        }
    }

    /**
     * Agrupa lineStops por lineId
     */
    private Map<Integer, List<LineStop>> groupByLine() {
        Map<Integer, List<LineStop>> grouped = new HashMap<>();
        
        for (LineStop ls : lineStops) {
            grouped.computeIfAbsent(ls.getLineId(), k -> new ArrayList<>()).add(ls);
        }
        
        return grouped;
    }

    /**
     * Obtiene estadísticas de los grafos
     */
    public void printStatistics(Map<Integer, RouteGraph> graphs) {
        logger.info("=== ESTADÍSTICAS DE GRAFOS ===");
        logger.info("Total de rutas: {}", graphs.size());
        
        int totalArcs = graphs.values().stream()
                .mapToInt(RouteGraph::getTotalArcs)
                .sum();
        
        logger.info("Total de arcos: {}", totalArcs);
        logger.info("Total de paradas únicas: {}", stops.size());
    }
}
