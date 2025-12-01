package com.icesi.mio.model;

import java.util.Objects;

/**
 * Representa una parada del SITM-MIO
 */
public class Stop {
    private final int stopId;
    private final int planVersionId;
    private final String shortName;
    private final String longName;
    private final long gpsX;
    private final long gpsY;
    private final double decimalLong;
    private final double decimalLat;

    public Stop(int stopId, int planVersionId, String shortName, String longName, 
                long gpsX, long gpsY, double decimalLong, double decimalLat) {
        this.stopId = stopId;
        this.planVersionId = planVersionId;
        this.shortName = shortName;
        this.longName = longName;
        this.gpsX = gpsX;
        this.gpsY = gpsY;
        this.decimalLong = decimalLong;
        this.decimalLat = decimalLat;
    }

    public int getStopId() {
        return stopId;
    }

    public int getPlanVersionId() {
        return planVersionId;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public long getGpsX() {
        return gpsX;
    }

    public long getGpsY() {
        return gpsY;
    }

    public double getDecimalLong() {
        return decimalLong;
    }

    public double getDecimalLat() {
        return decimalLat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stop stop = (Stop) o;
        return stopId == stop.stopId && planVersionId == stop.planVersionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stopId, planVersionId);
    }

    @Override
    public String toString() {
        return "Stop{" +
                "stopId=" + stopId +
                ", shortName='" + shortName + '\'' +
                ", longName='" + longName + '\'' +
                '}';
    }
}
