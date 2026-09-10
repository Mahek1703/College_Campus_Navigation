package com.example.collegecampusnavigation.model;

import com.google.android.gms.maps.model.LatLng;

public class CampusBuilding {
    private int id;
    private String name;
    private String description;
    private LatLng location;
    private String category;
    private int totalFloors;
    private String openingHours;
    private int occupancyRate; // 0 - 100%
    private boolean isAccessible;
    private LatLng wheelchairEntrance;
    private String departments;
    private int imageResId;

    public CampusBuilding(String name, String description, LatLng location, String category) {
        this(0, name, description, location, category, 3, "08:00 AM - 09:00 PM", 45, true, location, "General", 0);
    }

    public CampusBuilding(int id, String name, String description, LatLng location, String category,
                          int totalFloors, String openingHours, int occupancyRate, boolean isAccessible,
                          LatLng wheelchairEntrance, String departments) {
        this(id, name, description, location, category, totalFloors, openingHours, occupancyRate, isAccessible, wheelchairEntrance, departments, 0);
    }

    public CampusBuilding(int id, String name, String description, LatLng location, String category,
                          int totalFloors, String openingHours, int occupancyRate, boolean isAccessible,
                          LatLng wheelchairEntrance, String departments, int imageResId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.category = category;
        this.totalFloors = totalFloors;
        this.openingHours = openingHours != null ? openingHours : "08:00 AM - 08:00 PM";
        this.occupancyRate = occupancyRate;
        this.isAccessible = isAccessible;
        this.wheelchairEntrance = wheelchairEntrance != null ? wheelchairEntrance : location;
        this.departments = departments != null ? departments : "General";
        this.imageResId = imageResId;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public LatLng getLocation() { return location; }
    public String getCategory() { return category; }
    public int getTotalFloors() { return totalFloors; }
    public String getOpeningHours() { return openingHours; }
    public int getOccupancyRate() { return occupancyRate; }
    public boolean isAccessible() { return isAccessible; }
    public LatLng getWheelchairEntrance() { return wheelchairEntrance; }
    public String getDepartments() { return departments; }
    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }

    public String getOccupancyStatusText() {
        if (occupancyRate < 40) return "Low Traffic (" + occupancyRate + "%)";
        if (occupancyRate < 75) return "Moderate Traffic (" + occupancyRate + "%)";
        return "High Traffic (" + occupancyRate + "%)";
    }
}