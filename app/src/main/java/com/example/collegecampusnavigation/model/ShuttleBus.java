package com.example.collegecampusnavigation.model;

import com.google.android.gms.maps.model.LatLng;
import java.util.List;

public class ShuttleBus {
    private String busId;
    private String routeName;
    private LatLng currentLocation;
    private String nextStopName;
    private int etaNextStopMin;
    private int routeColor;
    private List<LatLng> pathWaypoints;
    private int currentWaypointIndex;

    public ShuttleBus(String busId, String routeName, LatLng currentLocation, String nextStopName,
                      int etaNextStopMin, int routeColor, List<LatLng> pathWaypoints) {
        this.busId = busId;
        this.routeName = routeName;
        this.currentLocation = currentLocation;
        this.nextStopName = nextStopName;
        this.etaNextStopMin = etaNextStopMin;
        this.routeColor = routeColor;
        this.pathWaypoints = pathWaypoints;
        this.currentWaypointIndex = 0;
    }

    public String getBusId() { return busId; }
    public String getRouteName() { return routeName; }
    public LatLng getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(LatLng currentLocation) { this.currentLocation = currentLocation; }
    public String getNextStopName() { return nextStopName; }
    public void setNextStopName(String nextStopName) { this.nextStopName = nextStopName; }
    public int getEtaNextStopMin() { return etaNextStopMin; }
    public void setEtaNextStopMin(int etaNextStopMin) { this.etaNextStopMin = etaNextStopMin; }
    public int getRouteColor() { return routeColor; }
    public List<LatLng> getPathWaypoints() { return pathWaypoints; }
    public int getCurrentWaypointIndex() { return currentWaypointIndex; }
    public void setCurrentWaypointIndex(int index) { this.currentWaypointIndex = index; }
}
