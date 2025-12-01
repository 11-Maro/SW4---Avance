package com.icesi.mio.graph;

import com.icesi.mio.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para GraphBuilder
 */
class GraphBuilderTest {

    private Map<Integer, Line> lines;
    private Map<Integer, Stop> stops;
    private List<LineStop> lineStops;
    private GraphBuilder graphBuilder;

    @BeforeEach
    void setUp() {
        // Crear datos de prueba
        lines = new HashMap<>();
        stops = new HashMap<>();
        lineStops = new ArrayList<>();

        // Línea de prueba
        Line line1 = new Line(1, 241, "T01", "Ruta Test", LocalDateTime.now());
        lines.put(1, line1);

        // Paradas de prueba
        Stop stop1 = new Stop(101, 241, "P1", "Parada 1", 0, 0, -76.5, 3.4);
        Stop stop2 = new Stop(102, 241, "P2", "Parada 2", 0, 0, -76.5, 3.4);
        Stop stop3 = new Stop(103, 241, "P3", "Parada 3", 0, 0, -76.5, 3.4);
        
        stops.put(101, stop1);
        stops.put(102, stop2);
        stops.put(103, stop3);

        // LineStops de prueba - IDA
        lineStops.add(new LineStop(1, 1, 0, 1, 101, 241, "", ""));
        lineStops.add(new LineStop(2, 2, 0, 1, 102, 241, "", ""));
        lineStops.add(new LineStop(3, 3, 0, 1, 103, 241, "", ""));

        // LineStops de prueba - VUELTA
        lineStops.add(new LineStop(4, 1, 1, 1, 103, 241, "", ""));
        lineStops.add(new LineStop(5, 2, 1, 1, 102, 241, "", ""));
        lineStops.add(new LineStop(6, 3, 1, 1, 101, 241, "", ""));

        graphBuilder = new GraphBuilder(lines, stops, lineStops);
    }

    @Test
    void testBuildGraphs() {
        Map<Integer, RouteGraph> graphs = graphBuilder.buildGraphs();
        
        assertNotNull(graphs);
        assertEquals(1, graphs.size());
        assertTrue(graphs.containsKey(1));
    }

    @Test
    void testGraphHasCorrectArcs() {
        Map<Integer, RouteGraph> graphs = graphBuilder.buildGraphs();
        RouteGraph graph = graphs.get(1);
        
        assertNotNull(graph);
        
        // Debería tener 2 arcos en IDA (101->102, 102->103)
        assertEquals(2, graph.getArcsIda().size());
        
        // Debería tener 2 arcos en VUELTA (103->102, 102->101)
        assertEquals(2, graph.getArcsVuelta().size());
    }

    @Test
    void testGraphHasCorrectStops() {
        Map<Integer, RouteGraph> graphs = graphBuilder.buildGraphs();
        RouteGraph graph = graphs.get(1);
        
        assertEquals(3, graph.getTotalStops());
        assertTrue(graph.getStops().containsKey(101));
        assertTrue(graph.getStops().containsKey(102));
        assertTrue(graph.getStops().containsKey(103));
    }

    @Test
    void testArcSequence() {
        Map<Integer, RouteGraph> graphs = graphBuilder.buildGraphs();
        RouteGraph graph = graphs.get(1);
        
        List<Arc> arcsIda = graph.getArcsIda();
        
        // Verificar que los arcos están en orden correcto
        assertEquals(101, arcsIda.get(0).getFromStop().getStopId());
        assertEquals(102, arcsIda.get(0).getToStop().getStopId());
        
        assertEquals(102, arcsIda.get(1).getFromStop().getStopId());
        assertEquals(103, arcsIda.get(1).getToStop().getStopId());
    }
}
