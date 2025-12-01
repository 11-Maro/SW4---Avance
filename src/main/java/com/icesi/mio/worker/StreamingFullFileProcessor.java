package com.icesi.mio.worker;

import com.icesi.mio.distributed.PartialResult;
import com.icesi.mio.model.RouteGraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Procesador "streaming" para archivos gigantes de datagramas.
 * Recorre el CSV una sola vez, manteniendo sólo el último datagrama
 * por bus en memoria y acumulando velocidades promedio por arco.
 *
 * Este procesador está pensado para soportar archivos muy grandes
 * (decenas de GB) sin cargar todo en memoria.
 */
public class StreamingFullFileProcessor {

    private final String csvPath;
    private final Map<Integer, RouteGraph> routeGraphs;

    // Formato real: 0:eventType, 1:registerdate, 2:stopId, 3:odometer,
    // 4:lat, 5:lon, 6:taskId, 7:lineId, 8:tripId, 9:unknown, 10:datagramDate, 11:busId

    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StreamingFullFileProcessor(String csvPath, Map<Integer, RouteGraph> routeGraphs) {
        this.csvPath = csvPath;
        this.routeGraphs = routeGraphs;
    }

    public Map<Long, Double> process() throws IOException {
        Map<String, String[]> lastByBus = new HashMap<>();
        PartialResult global = new PartialResult(-1);

        long lineCount = 0L;

        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lineCount++;
                String[] cur = line.split(",");
                if (cur.length < 12) {
                    continue; // fila mal formada
                }

                String busId = cur[11];
                String[] prev = lastByBus.get(busId);
                if (prev != null) {
                    try {
                        double lat1 = parseLat(prev);
                        double lon1 = parseLon(prev);
                        double lat2 = parseLat(cur);
                        double lon2 = parseLon(cur);

                        double dt = parseDeltaSeconds(prev, cur);
                        if (dt <= 0) {
                            lastByBus.put(busId, cur);
                            continue;
                        }

                        double dist = haversine(lat1, lon1, lat2, lon2); // metros
                        double speedMps = dist / dt;
                        double speedKph = speedMps * 3.6;

                        if (!(speedKph > 0 && speedKph < 200)) {
                            lastByBus.put(busId, cur);
                            continue; // descartar outliers
                        }

                        int lineId = parseLineId(prev, cur);
                        if (lineId == 0) {
                            lastByBus.put(busId, cur);
                            continue;
                        }

                        double midLat = (lat1 + lat2) / 2.0;
                        double midLon = (lon1 + lon2) / 2.0;

                        long arcId = ArcMatcher.matchArc(lineId, midLat, midLon, routeGraphs);
                        if (arcId != -1) {
                            global.addSample(arcId, speedKph);
                        }
                    } catch (Exception ignore) {
                        // ignorar fila defectuosa
                    }
                }
                // actualizar último datagrama del bus
                lastByBus.put(busId, cur);

                if (lineCount % 1_000_000 == 0) {
                    System.out.println("[StreamingFullFileProcessor] procesadas " + lineCount + " filas...");
                }
            }
        }

        System.out.println("[StreamingFullFileProcessor] FIN. Filas procesadas=" + lineCount +
                " arcs=" + global.getArcStats().size());

        // Convertir SumCount a promedio
        Map<Long, Double> res = new HashMap<>();
        for (Map.Entry<Long, PartialResult.SumCount> e : global.getArcStats().entrySet()) {
            PartialResult.SumCount sc = e.getValue();
            double avg = sc.count == 0 ? 0.0 : sc.sum / sc.count;
            res.put(e.getKey(), avg);
        }
        return res;
    }

    private static double parseLat(String[] row) {
        double raw = Double.parseDouble(row[4]);
        return raw / 1e7;
    }

    private static double parseLon(String[] row) {
        double raw = Double.parseDouble(row[5]);
        return raw / 1e7;
    }

    private static double parseDeltaSeconds(String[] prev, String[] cur) {
        try {
            LocalDateTime t1 = LocalDateTime.parse(prev[10], TS_FMT);
            LocalDateTime t2 = LocalDateTime.parse(cur[10], TS_FMT);
            long s1 = t1.toEpochSecond(ZoneOffset.UTC);
            long s2 = t2.toEpochSecond(ZoneOffset.UTC);
            return (double) (s2 - s1);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private static int parseLineId(String[] prev, String[] cur) {
        try {
            return Integer.parseInt(cur[7].trim());
        } catch (Exception e) {
            try {
                return Integer.parseInt(prev[7].trim());
            } catch (Exception ex) {
                return 0;
            }
        }
    }

    // Haversine distance in meters
    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // metres
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double dphi = Math.toRadians(lat2 - lat1);
        double dlambda = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dphi / 2) * Math.sin(dphi / 2) +
                Math.cos(phi1) * Math.cos(phi2) *
                        Math.sin(dlambda / 2) * Math.sin(dlambda / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
