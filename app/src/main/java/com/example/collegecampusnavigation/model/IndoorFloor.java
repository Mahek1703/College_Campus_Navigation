package com.example.collegecampusnavigation.model;

import java.util.List;

public class IndoorFloor {
    private int floorLevel; // 0, 1, 2, 3
    private String floorName;
    private int buildingId;
    private List<IndoorRoom> rooms;

    public IndoorFloor(int floorLevel, String floorName, int buildingId, List<IndoorRoom> rooms) {
        this.floorLevel = floorLevel;
        this.floorName = floorName;
        this.buildingId = buildingId;
        this.rooms = rooms;
    }

    public int getFloorLevel() { return floorLevel; }
    public String getFloorName() { return floorName; }
    public int getBuildingId() { return buildingId; }
    public List<IndoorRoom> getRooms() { return rooms; }
}
