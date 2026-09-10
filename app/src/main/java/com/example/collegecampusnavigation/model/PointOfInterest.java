package com.example.collegecampusnavigation.model;

import com.google.android.gms.maps.model.LatLng;

public class PointOfInterest {
    private int id;
    private String name;
    private String category; // Study Room, Restroom, Computer Lab, Printer, ATM, Hydration, Dining, Emergency
    private String buildingName;
    private int buildingId;
    private int floor;
    private LatLng location;
    private int occupancyRate; // 0 - 100%
    private int maxCapacity;
    private int availableUnits; // e.g. 12 free seats or free printers
    private boolean isOpen;
    private String statusText;
    private String features;
    private int imageResId;

    public PointOfInterest(int id, String name, String category, String buildingName, int buildingId,
                           int floor, LatLng location, int occupancyRate, int maxCapacity,
                           int availableUnits, boolean isOpen, String statusText, String features) {
        this(id, name, category, buildingName, buildingId, floor, location, occupancyRate, maxCapacity, availableUnits, isOpen, statusText, features, 0);
    }

    public PointOfInterest(int id, String name, String category, String buildingName, int buildingId,
                           int floor, LatLng location, int occupancyRate, int maxCapacity,
                           int availableUnits, boolean isOpen, String statusText, String features, int imageResId) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.buildingName = buildingName;
        this.buildingId = buildingId;
        this.floor = floor;
        this.location = location;
        this.occupancyRate = occupancyRate;
        this.maxCapacity = maxCapacity;
        this.availableUnits = availableUnits;
        this.isOpen = isOpen;
        this.statusText = statusText;
        this.features = features;
        this.imageResId = imageResId;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getBuildingName() { return buildingName; }
    public int getBuildingId() { return buildingId; }
    public int getFloor() { return floor; }
    public LatLng getLocation() { return location; }
    public int getOccupancyRate() { return occupancyRate; }
    public int getMaxCapacity() { return maxCapacity; }
    public int getAvailableUnits() { return availableUnits; }
    public boolean isOpen() { return isOpen; }
    public String getStatusText() { return statusText; }
    public String getFeatures() { return features; }
    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }

    public String getFloorDisplay() {
        if (floor == 0) return "Ground Floor";
        if (floor == 1) return "1st Floor";
        if (floor == 2) return "2nd Floor";
        if (floor == 3) return "3rd Floor";
        return "Floor " + floor;
    }
}
