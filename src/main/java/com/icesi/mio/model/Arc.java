package com.icesi.mio.model;

import java.util.Objects;

/**
 * Representa un arco entre dos paradas consecutivas en una ruta
 */
public class Arc {
    private final int lineId;
    private final int orientation;
    private final Stop fromStop;
    private final Stop toStop;
    private final int sequence;

    public Arc(int lineId, int orientation, Stop fromStop, Stop toStop, int sequence) {
        this.lineId = lineId;
        this.orientation = orientation;
        this.fromStop = fromStop;
        this.toStop = toStop;
        this.sequence = sequence;
    }

    public int getLineId() {
        return lineId;
    }

    public int getOrientation() {
        return orientation;
    }

    public Stop getFromStop() {
        return fromStop;
    }

    public Stop getToStop() {
        return toStop;
    }

    public int getSequence() {
        return sequence;
    }

    public String getOrientationName() {
        return orientation == 0 ? "IDA" : "VUELTA";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arc arc = (Arc) o;
        return lineId == arc.lineId && 
               orientation == arc.orientation && 
               sequence == arc.sequence;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineId, orientation, sequence);
    }

    @Override
    public String toString() {
        return String.format("Arc[Line=%d, %s, Seq=%d]: %s (%d) -> %s (%d)",
                lineId, getOrientationName(), sequence,
                fromStop.getShortName(), fromStop.getStopId(),
                toStop.getShortName(), toStop.getStopId());
    }
}
