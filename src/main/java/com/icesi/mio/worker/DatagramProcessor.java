package com.icesi.mio.worker;

import com.icesi.mio.distributed.DatagramPartition;
import com.icesi.mio.distributed.PartialResult;
import com.icesi.mio.model.RouteGraph;
import com.icesi.mio.model.Arc;
import com.icesi.mio.model.Stop;

import java.util.*;

/**
 * Procesador mejorado: asigna cada par de datagramas al arco más cercano
 * en la ruta indicada por lineId (usando ArcMatcher) y acumula velocidades por arcId.
 */
public class DatagramProcessor {
    private final DatagramPartition partition;
    private final Map<Integer, RouteGraph> routeGraphs;

    // Formato real de datagrams MIO (sin header):
    // 0:eventType, 1:registerdate (dd-MMM-yy), 2:stopId, 3:odometer,
    // 4:latitude (entero, grados * 1e7), 5:longitude (entero, grados * 1e7),
    // 6:taskId, 7:lineId, 8:tripId, 9:unknown, 10:datagramDate (yyyy-MM-dd HH:mm:ss), 11:busId

    public DatagramProcessor(DatagramPartition partition, Map<Integer, RouteGraph> routeGraphs) {
        this.partition = partition;
        this.routeGraphs = routeGraphs;
    }

    public PartialResult process() {
        PartialResult result = new PartialResult(partition.getId());

        // Agrupar por busId (columna 11 en el formato real)
        Map<String, List<String[]>> byBus = new HashMap<>();
        for (String[] r : partition.getRows()) {
            String busId = r.length > 11 ? r[11] : "";
            byBus.computeIfAbsent(busId, k -> new ArrayList<>()).add(r);
        }

        for (Map.Entry<String, List<String[]>> e : byBus.entrySet()) {
            List<String[]> rows = e.getValue();
            // Ordenar por fecha/hora de datagrama (columna 10, yyyy-MM-dd HH:mm:ss)
            rows.sort(Comparator.comparing(a -> a.length > 10 ? a[10] : ""));

            String[] prev = null;
            for (String[] cur : rows) {
                if (prev != null) {
                    try {
                        // Parseo de coordenadas: vienen como enteros en grados * 1e7, las convertimos a grados decimales
                        double lat1 = parseLat(prev);
                        double lon1 = parseLon(prev);
                        double lat2 = parseLat(cur);
                        double lon2 = parseLon(cur);

                        // Parseo de tiempos: usamos la columna datagramDate (10)
                        double dt = parseDeltaSeconds(prev, cur);
                        if (dt <= 0) { prev = cur; continue; }

                        double dist = haversine(lat1, lon1, lat2, lon2); // metros
                        double speedMps = dist / dt;
                        double speedKph = speedMps * 3.6;

                        if (!(speedKph > 0 && speedKph < 200)) { prev = cur; continue; }

                        // elegir lineId (preferimos cur; si no, usamos prev)
                        int lineId = 0;
                        try {
                            lineId = Integer.parseInt(cur.length > 7 ? cur[7] : "0");
                        } catch(Exception ex) {
                            try {
                                lineId = Integer.parseInt(prev.length > 7 ? prev[7] : "0");
                            } catch(Exception e2) {
                                lineId = 0;
                            }
                        }

                        if (lineId == 0) { prev = cur; continue; }

                        long arcId = ArcMatcher.matchArc(lineId, (lat1+lat2)/2.0, (lon1+lon2)/2.0, routeGraphs);
                        if (arcId != -1) {
                            result.addSample(arcId, speedKph);
                        }
                    } catch (Exception ex) {
                        // ignorar fila mal formada
                    }
                }
                prev = cur;
            }
        }

        // breve log
        System.out.println("[DatagramProcessor] partition=" + partition.getId() + " rows=" + partition.size() + " buses=" + byBus.size() + " arcSamples=" + result.getArcStats().size());

        return result;
    }

    // Haversine distance in meters
    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // metres
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double dphi = Math.toRadians(lat2 - lat1);
        double dlambda = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dphi/2) * Math.sin(dphi/2) +
                   Math.cos(phi1) * Math.cos(phi2) *
                   Math.sin(dlambda/2) * Math.sin(dlambda/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    // Helpers para el formato real de datagrams
    private static double parseLat(String[] row) {
        if (row.length <= 4) return 0.0;
        double raw = Double.parseDouble(row[4]);
        return raw / 1e7; // convertir a grados decimales
    }

    private static double parseLon(String[] row) {
        if (row.length <= 5) return 0.0;
        double raw = Double.parseDouble(row[5]);
        return raw / 1e7; // convertir a grados decimales
    }

    private static double parseDeltaSeconds(String[] prev, String[] cur) {
        if (prev.length <= 10 || cur.length <= 10) return 0.0;
        try {
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            java.time.LocalDateTime t1 = java.time.LocalDateTime.parse(prev[10], fmt);
            java.time.LocalDateTime t2 = java.time.LocalDateTime.parse(cur[10], fmt);
            java.time.ZoneOffset offset = java.time.ZoneOffset.UTC;
            long s1 = t1.toEpochSecond(offset);
            long s2 = t2.toEpochSecond(offset);
            return (double)(s2 - s1);
        } catch (Exception e) {
            return 0.0;
        }
    }
}
