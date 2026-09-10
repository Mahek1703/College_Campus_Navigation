package com.example.collegecampusnavigation.model;

import com.google.android.gms.maps.model.LatLng;

public class NavigationStep {
    public enum Maneuver {
        DEPART,
        STRAIGHT,
        TURN_LEFT,
        TURN_RIGHT,
        SLIGHT_LEFT,
        SLIGHT_RIGHT,
        RAMP,
        ELEVATOR,
        STAIRS,
        ARRIVE
    }

    private String instruction;
    private int distanceMeters;
    private Maneuver maneuver;
    private LatLng location;
    private String hint;

    public NavigationStep(String instruction, int distanceMeters, Maneuver maneuver, LatLng location, String hint) {
        this.instruction = instruction;
        this.distanceMeters = distanceMeters;
        this.maneuver = maneuver;
        this.location = location;
        this.hint = hint;
    }

    public String getInstruction() { return instruction; }
    public int getDistanceMeters() { return distanceMeters; }
    public Maneuver getManeuver() { return maneuver; }
    public LatLng getLocation() { return location; }
    public String getHint() { return hint; }

    public String getFormattedDistance() {
        if (distanceMeters < 1000) {
            return distanceMeters + " m";
        }
        return String.format(java.util.Locale.US, "%.1f km", distanceMeters / 1000.0);
    }
}
