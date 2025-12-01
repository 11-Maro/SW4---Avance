package com.icesi.mio.graph;

import com.icesi.mio.model.Arc;
import com.icesi.mio.model.RouteGraph;
import com.icesi.mio.util.ArcIndexBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Imprime los grafos de rutas en consola de manera ordenada
 */
public class GraphPrinter {

    /**
     * Imprime todos los grafos ordenados por ruta
     */
    public void printAllGraphs(Map<Integer, RouteGraph> graphs) {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("GRAFOS DE RUTAS SITM-MIO - ANÁLISIS DE ARCOS");
        System.out.println("=".repeat(100) + "\n");

        // Ordenar por lineId
        List<Integer> sortedLineIds = graphs.keySet().stream()
                .sorted()
                .collect(Collectors.toList());

        for (Integer lineId : sortedLineIds) {
            RouteGraph graph = graphs.get(lineId);
            printRouteGraph(graph);
        }

        printSummary(graphs);
    }

    /**
     * Imprime el grafo de una ruta específica
     */
    public void printRouteGraph(RouteGraph graph) {
        System.out.println("\n" + "-".repeat(100));
        System.out.printf("RUTA: %s - %s (ID: %d)%n", 
                graph.getLine().getShortName(),
                graph.getLine().getDescription(),
                graph.getLine().getLineId());
        System.out.println("-".repeat(100));

        // Imprimir arcos de IDA
        printArcs("IDA", graph.getArcsIda());

        // Imprimir arcos de VUELTA
        printArcs("VUELTA", graph.getArcsVuelta());

        // Resumen de la ruta
        System.out.printf("\nRESUMEN: %d paradas, %d arcos (IDA: %d, VUELTA: %d)%n",
                graph.getTotalStops(),
                graph.getTotalArcs(),
                graph.getArcsIda().size(),
                graph.getArcsVuelta().size());
    }

    /**
     * Imprime los arcos de una orientación
     */
    private void printArcs(String orientation, List<Arc> arcs) {
        if (arcs.isEmpty()) {
            System.out.printf("\n  %s: Sin arcos%n", orientation);
            return;
        }

        System.out.printf("\n  %s (%d arcos):%n", orientation, arcs.size());
        System.out.println("  " + "-".repeat(96));
        System.out.printf("  %-5s %-30s %-10s -> %-30s %-10s%n", 
                "SEQ", "PARADA ORIGEN", "ID", "PARADA DESTINO", "ID");
        System.out.println("  " + "-".repeat(96));

        for (Arc arc : arcs) {
            System.out.printf("  %-5d %-30s %-10d -> %-30s %-10d%n",
                    arc.getSequence(),
                    truncate(arc.getFromStop().getLongName(), 30),
                    arc.getFromStop().getStopId(),
                    truncate(arc.getToStop().getLongName(), 30),
                    arc.getToStop().getStopId());
        }
    }

    /**
     * Imprime resumen general
     */
    private void printSummary(Map<Integer, RouteGraph> graphs) {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("RESUMEN GENERAL");
        System.out.println("=".repeat(100));

        int totalRoutes = graphs.size();
        int totalArcs = graphs.values().stream()
                .mapToInt(RouteGraph::getTotalArcs)
                .sum();
        
        long totalStopsAcrossAllRoutes = graphs.values().stream()
                .mapToLong(RouteGraph::getTotalStops)
                .sum();

        System.out.printf("Total de rutas analizadas: %d%n", totalRoutes);
        System.out.printf("Total de arcos generados: %d%n", totalArcs);
        System.out.printf("Total de paradas (con duplicados entre rutas): %d%n", totalStopsAcrossAllRoutes);
        System.out.println("=".repeat(100) + "\n");
    }

    /**
     * Trunca un string a una longitud máxima
     */
    private String truncate(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }

    /**
     * Imprime solo una ruta específica por ID
     */
    public void printRoute(Map<Integer, RouteGraph> graphs, int lineId) {
        RouteGraph graph = graphs.get(lineId);
        if (graph != null) {
            printRouteGraph(graph);
        } else {
            System.out.printf("Ruta con ID %d no encontrada%n", lineId);
        }
    }

    /**
     * Imprime todos los grafos con velocidades promedio
     */
    public void printAllGraphsWithSpeeds(Map<Integer, RouteGraph> graphs, Map<Long, Double> speeds) {
        System.out.println("\n" + "=".repeat(120));
        System.out.println("GRAFOS DE RUTAS SITM-MIO - ANÁLISIS DE ARCOS CON VELOCIDADES PROMEDIO");
        System.out.println("=".repeat(120) + "\n");

        // Construir índice de arcos para acceso rápido
        Map<Long, Arc> arcIndex = ArcIndexBuilder.buildIndex(graphs);

        // Ordenar por lineId
        List<Integer> sortedLineIds = graphs.keySet().stream()
                .sorted()
                .collect(Collectors.toList());

        for (Integer lineId : sortedLineIds) {
            RouteGraph graph = graphs.get(lineId);
            printRouteGraphWithSpeeds(graph, speeds, arcIndex);
        }

        printSummaryWithSpeeds(graphs, speeds, arcIndex);
    }

    /**
     * Imprime el grafo de una ruta específica con velocidades
     */
    private void printRouteGraphWithSpeeds(RouteGraph graph, Map<Long, Double> speeds, Map<Long, Arc> arcIndex) {
        System.out.println("\n" + "-".repeat(120));
        System.out.printf("RUTA: %s - %s (ID: %d)%n", 
                graph.getLine().getShortName(),
                graph.getLine().getDescription(),
                graph.getLine().getLineId());
        System.out.println("-".repeat(120));

        // Imprimir arcos de IDA
        printArcsWithSpeeds("IDA", graph.getArcsIda(), speeds, arcIndex);

        // Imprimir arcos de VUELTA
        printArcsWithSpeeds("VUELTA", graph.getArcsVuelta(), speeds, arcIndex);

        // Resumen de la ruta
        int arcsWithSpeed = countArcsWithSpeed(graph, speeds, arcIndex);
        System.out.printf("\nRESUMEN: %d paradas, %d arcos (IDA: %d, VUELTA: %d), %d con velocidad promedio%n",
                graph.getTotalStops(),
                graph.getTotalArcs(),
                graph.getArcsIda().size(),
                graph.getArcsVuelta().size(),
                arcsWithSpeed);
    }

    /**
     * Imprime los arcos de una orientación con velocidades
     */
    private void printArcsWithSpeeds(String orientation, List<Arc> arcs, Map<Long, Double> speeds, Map<Long, Arc> arcIndex) {
        if (arcs.isEmpty()) {
            System.out.printf("\n  %s: Sin arcos%n", orientation);
            return;
        }

        System.out.printf("\n  %s (%d arcos):%n", orientation, arcs.size());
        System.out.println("  " + "-".repeat(116));
        System.out.printf("  %-5s %-25s %-10s -> %-25s %-10s %-15s%n", 
                "SEQ", "PARADA ORIGEN", "ID", "PARADA DESTINO", "ID", "VEL. PROM (km/h)");
        System.out.println("  " + "-".repeat(116));

        for (Arc arc : arcs) {
            long arcId = ArcIndexBuilder.generateId(arc);
            Double speed = speeds.get(arcId);
            String speedStr = speed != null ? String.format("%.2f", speed) : "N/A";
            
            System.out.printf("  %-5d %-25s %-10d -> %-25s %-10d %-15s%n",
                    arc.getSequence(),
                    truncate(arc.getFromStop().getLongName(), 25),
                    arc.getFromStop().getStopId(),
                    truncate(arc.getToStop().getLongName(), 25),
                    arc.getToStop().getStopId(),
                    speedStr);
        }
    }

    /**
     * Cuenta cuántos arcos tienen velocidad calculada
     */
    private int countArcsWithSpeed(RouteGraph graph, Map<Long, Double> speeds, Map<Long, Arc> arcIndex) {
        int count = 0;
        for (Arc arc : graph.getArcsIda()) {
            long arcId = ArcIndexBuilder.generateId(arc);
            if (speeds.containsKey(arcId)) count++;
        }
        for (Arc arc : graph.getArcsVuelta()) {
            long arcId = ArcIndexBuilder.generateId(arc);
            if (speeds.containsKey(arcId)) count++;
        }
        return count;
    }

    /**
     * Imprime resumen general con estadísticas de velocidades
     */
    private void printSummaryWithSpeeds(Map<Integer, RouteGraph> graphs, Map<Long, Double> speeds, Map<Long, Arc> arcIndex) {
        System.out.println("\n" + "=".repeat(120));
        System.out.println("RESUMEN GENERAL CON VELOCIDADES");
        System.out.println("=".repeat(120));

        int totalRoutes = graphs.size();
        int totalArcs = graphs.values().stream()
                .mapToInt(RouteGraph::getTotalArcs)
                .sum();
        
        int arcsWithSpeed = 0;
        double sumSpeeds = 0.0;
        double minSpeed = Double.MAX_VALUE;
        double maxSpeed = 0.0;

        for (Double speed : speeds.values()) {
            if (speed != null && speed > 0) {
                arcsWithSpeed++;
                sumSpeeds += speed;
                if (speed < minSpeed) minSpeed = speed;
                if (speed > maxSpeed) maxSpeed = speed;
            }
        }

        double avgSpeed = arcsWithSpeed > 0 ? sumSpeeds / arcsWithSpeed : 0.0;

        System.out.printf("Total de rutas analizadas: %d%n", totalRoutes);
        System.out.printf("Total de arcos generados: %d%n", totalArcs);
        System.out.printf("Arcos con velocidad promedio calculada: %d (%.1f%%)%n", 
                arcsWithSpeed, totalArcs > 0 ? (100.0 * arcsWithSpeed / totalArcs) : 0.0);
        if (arcsWithSpeed > 0) {
            System.out.printf("Velocidad promedio general: %.2f km/h%n", avgSpeed);
            System.out.printf("Velocidad mínima: %.2f km/h%n", minSpeed);
            System.out.printf("Velocidad máxima: %.2f km/h%n", maxSpeed);
        }
        System.out.println("=".repeat(120) + "\n");
    }
}
