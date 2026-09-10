package com.example.collegecampusnavigation.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PanoramaScene implements Serializable {

    private String id;
    private String title;
    private String subtitle;
    private int buildingId;
    private String buildingName;
    private int floor; // 0 = Ground, 1 = Floor 1, 2 = Floor 2, 3 = Floor 3
    private String roomCode;
    private int imageResId;
    private float defaultYaw;
    private float defaultPitch;
    private float miniMapX; // 0.0 - 100.0% on 2D floorplan
    private float miniMapY; // 0.0 - 100.0% on 2D floorplan
    private List<PanoramaHotspot> hotspots;

    public PanoramaScene(String id, String title, String subtitle, int buildingId, String buildingName,
                         int floor, String roomCode, int imageResId, float defaultYaw, float defaultPitch,
                         float miniMapX, float miniMapY) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.buildingId = buildingId;
        this.buildingName = buildingName;
        this.floor = floor;
        this.roomCode = roomCode;
        this.imageResId = imageResId;
        this.defaultYaw = defaultYaw;
        this.defaultPitch = defaultPitch;
        this.miniMapX = miniMapX;
        this.miniMapY = miniMapY;
        this.hotspots = new ArrayList<>();
    }

    public void addHotspot(PanoramaHotspot hotspot) {
        this.hotspots.add(hotspot);
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public int getBuildingId() { return buildingId; }
    public String getBuildingName() { return buildingName; }
    public int getFloor() { return floor; }
    public String getRoomCode() { return roomCode; }
    public int getImageResId() { return imageResId; }
    public float getDefaultYaw() { return defaultYaw; }
    public float getDefaultPitch() { return defaultPitch; }
    public float getMiniMapX() { return miniMapX; }
    public float getMiniMapY() { return miniMapY; }
    public List<PanoramaHotspot> getHotspots() { return hotspots; }
}
