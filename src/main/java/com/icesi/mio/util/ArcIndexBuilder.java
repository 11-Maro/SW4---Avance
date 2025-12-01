package com.icesi.mio.util;

import com.icesi.mio.model.Arc;
import com.icesi.mio.model.RouteGraph;

import java.util.HashMap;
import java.util.Map;

/**
 * Construye un índice arcId -> Arc para facilitar inspección y reporting
 */
public class ArcIndexBuilder {
    public static Map<Long, Arc> buildIndex(Map<Integer, RouteGraph> graphs) {
        Map<Long, Arc> idx = new HashMap<>();
        for (RouteGraph rg : graphs.values()) {
            for (Arc a : rg.getArcsIda()) {
                idx.put(generateId(a), a);
            }
            for (Arc a : rg.getArcsVuelta()) {
                idx.put(generateId(a), a);
            }
        }
        return idx;
    }

    public static long generateId(Arc a) {
        return java.util.Objects.hash(a.getLineId(), a.getOrientation(), a.getSequence());
    }
}

