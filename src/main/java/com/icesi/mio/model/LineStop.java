package com.icesi.mio.model;

import java.util.Objects;

/**
 * Representa la relación entre una ruta y una parada
 */
public class LineStop implements Comparable<LineStop> {
    private final int lineStopId;
    private final int stopSequence;
    private final int orientation; // 0 = ida, 1 = vuelta
    private final int lineId;
    private final int stopId;
    private final int planVersionId;
    private final String lineVariant;
    private final String lineVariantType;

    public LineStop(int lineStopId, int stopSequence, int orientation, int lineId, 
                    int stopId, int planVersionId, String lineVariant, String lineVariantType) {
        this.lineStopId = lineStopId;
        this.stopSequence = stopSequence;
        this.orientation = orientation;
        this.lineId = lineId;
        this.stopId = stopId;
        this.planVersionId = planVersionId;
        this.lineVariant = lineVariant;
        this.lineVariantType = lineVariantType;
    }

    public int getLineStopId() {
        return lineStopId;
    }

    public int getStopSequence() {
        return stopSequence;
    }

    public int getOrientation() {
        return orientation;
    }

    public int getLineId() {
        return lineId;
    }

    public int getStopId() {
        return stopId;
    }

    public int getPlanVersionId() {
        return planVersionId;
    }

    public String getLineVariant() {
        return lineVariant;
    }

    public String getLineVariantType() {
        return lineVariantType;
    }

    @Override
    public int compareTo(LineStop other) {
        // Primero por lineId, luego por orientation, luego por stopSequence
        int lineCompare = Integer.compare(this.lineId, other.lineId);
        if (lineCompare != 0) return lineCompare;
        
        int orientationCompare = Integer.compare(this.orientation, other.orientation);
        if (orientationCompare != 0) return orientationCompare;
        
        return Integer.compare(this.stopSequence, other.stopSequence);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineStop lineStop = (LineStop) o;
        return lineStopId == lineStop.lineStopId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineStopId);
    }

    @Override
    public String toString() {
        return "LineStop{" +
                "lineId=" + lineId +
                ", stopId=" + stopId +
                ", sequence=" + stopSequence +
                ", orientation=" + (orientation == 0 ? "IDA" : "VUELTA") +
                '}';
    }
}
