package com.icesi.mio.model;

import java.util.*;

/**
 * Representa el grafo de una ruta con sus dos orientaciones (ida y vuelta)
 */
public class RouteGraph {
    private final Line line;
    private final List<Arc> arcsIda;
    private final List<Arc> arcsVuelta;
    private final Map<Integer, Stop> stops;

    public RouteGraph(Line line) {
        this.line = line;
        this.arcsIda = new ArrayList<>();
        this.arcsVuelta = new ArrayList<>();
        this.stops = new HashMap<>();
    }

    public void addArc(Arc arc) {
        if (arc.getOrientation() == 0) {
            arcsIda.add(arc);
        } else {
            arcsVuelta.add(arc);
        }
        stops.put(arc.getFromStop().getStopId(), arc.getFromStop());
        stops.put(arc.getToStop().getStopId(), arc.getToStop());
    }

    public Line getLine() {
        return line;
    }

    public List<Arc> getArcsIda() {
        return Collections.unmodifiableList(arcsIda);
    }

    public List<Arc> getArcsVuelta() {
        return Collections.unmodifiableList(arcsVuelta);
    }

    public Map<Integer, Stop> getStops() {
        return Collections.unmodifiableMap(stops);
    }

    public int getTotalArcs() {
        return arcsIda.size() + arcsVuelta.size();
    }

    public int getTotalStops() {
        return stops.size();
    }

    @Override
    public String toString() {
        return "RouteGraph{" +
                "line=" + line.getShortName() +
                ", arcsIda=" + arcsIda.size() +
                ", arcsVuelta=" + arcsVuelta.size() +
                ", totalStops=" + stops.size() +
                '}';
    }
}
