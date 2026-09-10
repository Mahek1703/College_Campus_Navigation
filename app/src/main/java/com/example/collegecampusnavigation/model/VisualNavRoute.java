package com.example.collegecampusnavigation.model;

import java.io.Serializable;
import java.util.List;

public class VisualNavRoute implements Serializable {

    private final String routeId;
    private final String startLocationId;
    private final String startLocationName;
    private final String destinationId;
    private final String destinationName;
    private final String summary;
    private final int totalDistanceMeters;
    private final int estimatedMinutes;
    private final List<VisualNavStep> steps;

    public VisualNavRoute(String routeId, String startLocationId, String startLocationName,
                          String destinationId, String destinationName, String summary,
                          int totalDistanceMeters, int estimatedMinutes, List<VisualNavStep> steps) {
        this.routeId = routeId;
        this.startLocationId = startLocationId;
        this.startLocationName = startLocationName;
        this.destinationId = destinationId;
        this.destinationName = destinationName;
        this.summary = summary;
        this.totalDistanceMeters = totalDistanceMeters;
        this.estimatedMinutes = estimatedMinutes;
        this.steps = steps;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getStartLocationId() {
        return startLocationId;
    }

    public String getStartLocationName() {
        return startLocationName;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public String getSummary() {
        return summary;
    }

    public int getTotalDistanceMeters() {
        return totalDistanceMeters;
    }

    public int getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public List<VisualNavStep> getSteps() {
        return steps;
    }

    public int getStepCount() {
        return steps != null ? steps.size() : 0;
    }
}
