package com.icesi.mio.distributed;

import java.util.HashMap;
import java.util.Map;

/**
 * Resultado parcial devuelto por un worker: map arcId -> SumCount
 */
public class PartialResult {
    private final int partitionId;
    private final Map<Long, SumCount> arcStats = new HashMap<>();

    public PartialResult(int partitionId) {
        this.partitionId = partitionId;
    }

    public int getPartitionId() {
        return partitionId;
    }

    public Map<Long, SumCount> getArcStats() {
        return arcStats;
    }

    public void addSample(long arcId, double speed) {
        arcStats.compute(arcId, (k, v) -> {
            if (v == null) return new SumCount(speed, 1);
            v.sum += speed; v.count += 1; return v;
        });
    }

    public static class SumCount {
        public double sum;
        public long count;

        public SumCount(double sum, long count) {
            this.sum = sum;
            this.count = count;
        }
    }
}

