package com.icesi.mio.benchmark;

import com.icesi.mio.distributed.DatagramPartition;
import com.icesi.mio.distributed.PartitionManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Variante de PartitionManager para benchmark real que solo toma una muestra
 * de las primeras N filas del CSV de datagramas, evitando cargar el archivo
 * completo en memoria.
 */
public class SamplingPartitionManager extends PartitionManager {

    private final int partitions;
    private final int maxRows;

    public SamplingPartitionManager(int partitions, int maxRows) {
        super(partitions);
        this.partitions = partitions;
        this.maxRows = maxRows;
    }

    @Override
    public List<DatagramPartition> createPartitions(String csvPath) throws IOException {
        List<DatagramPartition> parts = new ArrayList<>();
        for (int i = 0; i < partitions; i++) parts.add(new DatagramPartition(i));

        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;
            int count = 0;

            // IMPORTANTE: los archivos datagrams4*.csv no tienen header; no se debe saltar ninguna línea.
            while ((line = br.readLine()) != null && count < maxRows) {
                String[] r = line.split(",");
                try {
                    // Formato real: 0:eventType, 1:registerdate, 2:stopId, 3:odometer,
                    // 4:lat, 5:lon, 6:taskId, 7:lineId, 8:tripId, 9:unknown, 10:datagramDate, 11:busId
                    int lineId = Integer.parseInt(r[7].trim());
                    int idx = Math.floorMod(lineId, partitions);
                    parts.get(idx).addRow(r);
                } catch (Exception e) {
                    // Si algo falla, mandar la fila a la partición 0 para no perder datos
                    parts.get(0).addRow(r);
                }
                count++;
            }
        }

        return parts;
    }
}
