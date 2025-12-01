package com.icesi.mio.distributed;

import com.icesi.mio.parser.CSVReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Crea particiones sencillas a partir de un CSV leyendo en memoria y repartiendo
 * por "lineId % nPartitions" si el CSV tiene lineId en la columna esperada.
 */
public class PartitionManager {

    private final int partitions;

    public PartitionManager(int partitions) {
        this.partitions = partitions;
    }

    public List<DatagramPartition> createPartitions(String csvPath) throws IOException {
        List<String[]> rows = CSVReader.readCSV(csvPath);
        List<DatagramPartition> parts = new ArrayList<>();
        for (int i = 0; i < partitions; i++) parts.add(new DatagramPartition(i));

        for (String[] r : rows) {
            // Formato real de datagrams MIO:
            // 0:eventType, 1:registerdate, 2:stopId, 3:odometer, 4:lat, 5:lon,
            // 6:taskId, 7:lineId, 8:tripId, 9:unknown, 10:datagramDate, 11:busId
            try {
                if (r.length > 7) {
                    int lineId = Integer.parseInt(r[7].trim());
                    int idx = Math.floorMod(lineId, partitions);
                    parts.get(idx).addRow(r);
                } else {
                    // Fila mal formada, mandar a partición 0
                    parts.get(0).addRow(r);
                }
            } catch (Exception e) {
                // Si falla el parseo de lineId, enviar a la partición 0
                parts.get(0).addRow(r);
            }
        }
        return parts;
    }
}

