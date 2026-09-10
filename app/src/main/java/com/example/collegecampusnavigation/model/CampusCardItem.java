package com.example.collegecampusnavigation.model;

import com.google.android.gms.maps.model.LatLng;

public class CampusCardItem {
    private String categoryTag; // "Campus", "Library", "MCA Dept", "Labs", "Auditorium", "Canteen"
    private String title;
    private String subtitle;
    private int imageResId;
    private int buildingId;
    private int floor;
    private String badgeText;
    private int occupancyRate;
    private String targetRoomCode;
    private LatLng location;
    private String description;

    public CampusCardItem(String categoryTag, String title, String subtitle, int imageResId,
                          int buildingId, int floor, String badgeText, int occupancyRate,
                          String targetRoomCode, LatLng location, String description) {
        this.categoryTag = categoryTag;
        this.title = title;
        this.subtitle = subtitle;
        this.imageResId = imageResId;
        this.buildingId = buildingId;
        this.floor = floor;
        this.badgeText = badgeText;
        this.occupancyRate = occupancyRate;
        this.targetRoomCode = targetRoomCode;
        this.location = location;
        this.description = description;
    }

    public String getCategoryTag() { return categoryTag; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public int getImageResId() { return imageResId; }
    public int getBuildingId() { return buildingId; }
    public int getFloor() { return floor; }
    public String getBadgeText() { return badgeText; }
    public int getOccupancyRate() { return occupancyRate; }
    public String getTargetRoomCode() { return targetRoomCode; }
    public LatLng getLocation() { return location; }
    public String getDescription() { return description; }
}
