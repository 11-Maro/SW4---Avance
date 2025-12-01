package com.icesi.mio.distributed;

import com.icesi.mio.aggregate.ArcSpeedAggregator;
import com.icesi.mio.worker.DatagramProcessor;
import com.icesi.mio.model.RouteGraph;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Orquestador simple que distribuye particiones a un pool local de workers (simula distribución).
 */
public class DistributedComputeCoordinator {
    private final PartitionManager partitionManager;
    private final int numWorkers;
    private final long timeoutSeconds;

    public DistributedComputeCoordinator(PartitionManager partitionManager, int numWorkers, long timeoutSeconds) {
        this.partitionManager = partitionManager;
        this.numWorkers = numWorkers;
        this.timeoutSeconds = timeoutSeconds;
    }

    public Map<Long, Double> runJob(String datagramCsvPath, Map<Integer, RouteGraph> routeGraphs) throws IOException, InterruptedException {
        List<DatagramPartition> parts = partitionManager.createPartitions(datagramCsvPath);

        ExecutorService pool = Executors.newFixedThreadPool(numWorkers);
        CompletionService<PartialResult> ecs = new ExecutorCompletionService<>(pool);

        for (DatagramPartition p : parts) {
            ecs.submit(() -> {
                DatagramProcessor proc = new DatagramProcessor(p, routeGraphs);
                return proc.process();
            });
        }

        ArcSpeedAggregator aggregator = new ArcSpeedAggregator();

        long deadline = System.currentTimeMillis() + timeoutSeconds * 1000;
        int received = 0;
        while (received < parts.size() && System.currentTimeMillis() < deadline) {
            Future<PartialResult> f = ecs.poll(1, TimeUnit.SECONDS);
            if (f != null) {
                try {
                    PartialResult pr = f.get();
                    aggregator.merge(pr);
                    received++;
                } catch (ExecutionException e) {
                    // log and continue
                    e.printStackTrace();
                }
            }
        }

        pool.shutdownNow();

        return aggregator.finalizeAverages();
    }
}

