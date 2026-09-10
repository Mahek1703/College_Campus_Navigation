package com.example.collegecampusnavigation.model;

import com.google.android.gms.maps.model.LatLng;
import java.util.List;
import java.util.Locale;

public class CampusRoute {
    public enum Mode {
        WALK,
        CYCLING,
        SHUTTLE,
        ACCESSIBLE
    }

    private String originName;
    private String destinationName;
    private List<LatLng> polylinePoints;
    private int totalDistanceMeters;
    private int estimatedDurationSeconds;
    private Mode mode;
    private List<NavigationStep> steps;
    private boolean avoidedConstruction;
    private boolean isAccessiblePath;

    public CampusRoute(String originName, String destinationName, List<LatLng> polylinePoints,
                       int totalDistanceMeters, int estimatedDurationSeconds, Mode mode,
                       List<NavigationStep> steps, boolean avoidedConstruction, boolean isAccessiblePath) {
        this.originName = originName;
        this.destinationName = destinationName;
        this.polylinePoints = polylinePoints;
        this.totalDistanceMeters = totalDistanceMeters;
        this.estimatedDurationSeconds = estimatedDurationSeconds;
        this.mode = mode;
        this.steps = steps;
        this.avoidedConstruction = avoidedConstruction;
        this.isAccessiblePath = isAccessiblePath;
    }

    public String getOriginName() { return originName; }
    public String getDestinationName() { return destinationName; }
    public List<LatLng> getPolylinePoints() { return polylinePoints; }
    public int getTotalDistanceMeters() { return totalDistanceMeters; }
    public int getEstimatedDurationSeconds() { return estimatedDurationSeconds; }
    public Mode getMode() { return mode; }
    public List<NavigationStep> getSteps() { return steps; }
    public boolean isAvoidedConstruction() { return avoidedConstruction; }
    public boolean isAccessiblePath() { return isAccessiblePath; }

    public String getFormattedDuration() {
        int minutes = Math.max(1, (int) Math.ceil(estimatedDurationSeconds / 60.0));
        if (minutes < 60) {
            return minutes + " min";
        }
        int hrs = minutes / 60;
        int remMins = minutes % 60;
        return hrs + " hr " + (remMins > 0 ? remMins + " min" : "");
    }

    public String getFormattedDistance() {
        if (totalDistanceMeters < 1000) {
            return totalDistanceMeters + " m";
        }
        return String.format(Locale.US, "%.1f km", totalDistanceMeters / 1000.0);
    }
}
