package com.example.collegecampusnavigation.model;

import java.io.Serializable;

public class PanoramaHotspot implements Serializable {

    public enum HotspotType {
        WALK("ic_walk", "Walk Forward", "#1E88E5"),
        ENTER_DOOR("ic_route", "Enter Room", "#00E676"),
        ELEVATOR("ic_elevator", "Elevator", "#FFB300"),
        STAIRS("ic_stairs", "Stairs", "#AB47BC"),
        LAB("ic_layers", "Tech Lab", "#00B0FF"),
        INFO("ic_info", "Info Point", "#FF7043");

        public final String iconName;
        public final String label;
        public final String colorHex;

        HotspotType(String iconName, String label, String colorHex) {
            this.iconName = iconName;
            this.label = label;
            this.colorHex = colorHex;
        }
    }

    private String id;
    private String title;
    private String subtitle;
    private float yaw;   // Horizontal angle (-180 to +180 degrees)
    private float pitch; // Vertical angle (-90 to +90 degrees)
    private HotspotType type;
    private String targetSceneId;
    private String targetRoomCode;

    public PanoramaHotspot(String id, String title, String subtitle, float yaw, float pitch,
                           HotspotType type, String targetSceneId, String targetRoomCode) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.yaw = yaw;
        this.pitch = pitch;
        this.type = type;
        this.targetSceneId = targetSceneId;
        this.targetRoomCode = targetRoomCode;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
    public HotspotType getType() { return type; }
    public String getTargetSceneId() { return targetSceneId; }
    public String getTargetRoomCode() { return targetRoomCode; }
}
