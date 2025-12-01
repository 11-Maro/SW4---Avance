package com.icesi.mio.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa una ruta del SITM-MIO
 */
public class Line {
    private final int lineId;
    private final int planVersionId;
    private final String shortName;
    private final String description;
    private final LocalDateTime activationDate;

    public Line(int lineId, int planVersionId, String shortName, String description, LocalDateTime activationDate) {
        this.lineId = lineId;
        this.planVersionId = planVersionId;
        this.shortName = shortName;
        this.description = description;
        this.activationDate = activationDate;
    }

    public int getLineId() {
        return lineId;
    }

    public int getPlanVersionId() {
        return planVersionId;
    }

    public String getShortName() {
        return shortName;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getActivationDate() {
        return activationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return lineId == line.lineId && planVersionId == line.planVersionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineId, planVersionId);
    }

    @Override
    public String toString() {
        return "Line{" +
                "lineId=" + lineId +
                ", shortName='" + shortName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
