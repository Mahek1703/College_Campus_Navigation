package com.example.collegecampusnavigation.model;

import com.google.android.gms.maps.model.LatLng;

public class ShuttleStop {
    private int id;
    private String name;
    private LatLng location;
    private String routeName;
    private int etaMinutes; // Live calculated or scheduled ETA
    private String nextBusTime;

    public ShuttleStop(int id, String name, LatLng location, String routeName, int etaMinutes, String nextBusTime) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.routeName = routeName;
        this.etaMinutes = etaMinutes;
        this.nextBusTime = nextBusTime;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public LatLng getLocation() { return location; }
    public String getRouteName() { return routeName; }
    public int getEtaMinutes() { return etaMinutes; }
    public void setEtaMinutes(int etaMinutes) { this.etaMinutes = etaMinutes; }
    public String getNextBusTime() { return nextBusTime; }
    public void setNextBusTime(String nextBusTime) { this.nextBusTime = nextBusTime; }
}
