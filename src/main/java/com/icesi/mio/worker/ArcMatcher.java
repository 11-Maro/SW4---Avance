package com.icesi.mio.worker;

import com.icesi.mio.model.RouteGraph;
import com.icesi.mio.model.Arc;
import com.icesi.mio.model.Stop;

import java.util.List;
import java.util.Map;

/**
 * ArcMatcher sencillo: busca el arco (ida o vuelta) de la ruta cuyo segmento
 * (fromStop -> toStop) tiene la menor distancia perpendicular al punto dado.
 * Retorna un arcId consistente (hash de lineId, orientation, sequence) o -1 si no se encuentra.
 */
public class ArcMatcher {

    public static long matchArc(int lineId, double lat, double lon, Map<Integer, RouteGraph> graphs) {
        RouteGraph rg = graphs.get(lineId);
        if (rg == null) return -1;

        double bestDist = Double.MAX_VALUE;
        Arc best = null;

        // evaluar ambos sentidos
        List<Arc> arcs = rg.getArcsIda();
        for (Arc a : arcs) {
            double d = pointToSegmentDistanceMeters(lat, lon, a.getFromStop().getDecimalLat(), a.getFromStop().getDecimalLong(), a.getToStop().getDecimalLat(), a.getToStop().getDecimalLong());
            if (d < bestDist) { bestDist = d; best = a; }
        }

        arcs = rg.getArcsVuelta();
        for (Arc a : arcs) {
            double d = pointToSegmentDistanceMeters(lat, lon, a.getFromStop().getDecimalLat(), a.getFromStop().getDecimalLong(), a.getToStop().getDecimalLat(), a.getToStop().getDecimalLong());
            if (d < bestDist) { bestDist = d; best = a; }
        }

        // Umbral: si el punto está a más de 200 metros del arco, ignorar
        if (best == null || bestDist > 200) return -1;

        // Generar id estable
        long arcId = generateArcId(best);
        return arcId;
    }

    private static long generateArcId(Arc a) {
        return java.util.Objects.hash(a.getLineId(), a.getOrientation(), a.getSequence());
    }

    // Distancia punto-segmento aproximada en metros usando haversine para los extremos
    private static double pointToSegmentDistanceMeters(double plat, double plon, double lat1, double lon1, double lat2, double lon2) {
        // Transformar a coordenadas en metros usando una proyección simple (equirectangular) centrada
        double R = 6371000; // m
        double phi = Math.toRadians((lat1 + lat2 + plat) / 3.0);
        double x = Math.toRadians(plon - lon1) * R * Math.cos(phi);
        double y = Math.toRadians(plat - lat1) * R;

        double x2 = Math.toRadians(lon2 - lon1) * R * Math.cos(phi);
        double y2 = Math.toRadians(lat2 - lat1) * R;

        double segLen2 = x2*x2 + y2*y2;
        if (segLen2 == 0) {
            return Math.hypot(x, y);
        }
        double t = (x * x2 + y * y2) / segLen2;
        t = Math.max(0, Math.min(1, t));
        double projx = t * x2;
        double projy = t * y2;
        double dx = x - projx;
        double dy = y - projy;
        return Math.hypot(dx, dy);
    }
}

