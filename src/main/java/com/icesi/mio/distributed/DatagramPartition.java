package com.icesi.mio.distributed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contenedor simple para una partición de datagramas (filas CSV ya parseadas)
 */
public class DatagramPartition {
    private final int id;
    private final List<String[]> rows;

    public DatagramPartition(int id) {
        this.id = id;
        this.rows = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void addRow(String[] row) {
        rows.add(row);
    }

    public List<String[]> getRows() {
        return Collections.unmodifiableList(rows);
    }

    public int size() {
        return rows.size();
    }
}

