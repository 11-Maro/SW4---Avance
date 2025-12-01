package com.icesi.mio.aggregate;

import com.icesi.mio.distributed.PartialResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Agrega resultados parciales (sum/count) y produce promedios finales.
 */
public class ArcSpeedAggregator {
    private final ConcurrentHashMap<Long, PartialResult.SumCount> aggregated = new ConcurrentHashMap<>();

    public void merge(PartialResult partial) {
        for (Map.Entry<Long, PartialResult.SumCount> e : partial.getArcStats().entrySet()) {
            aggregated.merge(e.getKey(), e.getValue(), (a, b) -> {
                a.sum += b.sum; a.count += b.count; return a;
            });
        }
    }

    public ConcurrentHashMap<Long, Double> finalizeAverages() {
        ConcurrentHashMap<Long, Double> res = new ConcurrentHashMap<>();
        for (Map.Entry<Long, PartialResult.SumCount> e : aggregated.entrySet()) {
            PartialResult.SumCount sc = e.getValue();
            double avg = sc.count == 0 ? 0.0 : sc.sum / sc.count;
            res.put(e.getKey(), avg);
        }
        return res;
    }
}

