package com.example.collegecampusnavigation.model;

public class IndoorRoom {
    private int id;
    private int buildingId;
    private String buildingName;
    private String roomCode;
    private String name;
    private int floor; // 0 = Ground, 1 = 1st Floor, etc.
    private String roomType; // Classroom, Laboratory, Restroom, Office, Elevator, Stairs, Amenity
    private float xPercent;
    private float yPercent;
    private float widthPercent;
    private float heightPercent;
    private String description;
    private int imageResId;

    public IndoorRoom(int id, int buildingId, String buildingName, String roomCode, String name,
                      int floor, String roomType, float xPercent, float yPercent,
                      float widthPercent, float heightPercent, String description) {
        this(id, buildingId, buildingName, roomCode, name, floor, roomType, xPercent, yPercent, widthPercent, heightPercent, description, 0);
    }

    public IndoorRoom(int id, int buildingId, String buildingName, String roomCode, String name,
                      int floor, String roomType, float xPercent, float yPercent,
                      float widthPercent, float heightPercent, String description, int imageResId) {
        this.id = id;
        this.buildingId = buildingId;
        this.buildingName = buildingName;
        this.roomCode = roomCode;
        this.name = name;
        this.floor = floor;
        this.roomType = roomType;
        this.xPercent = xPercent;
        this.yPercent = yPercent;
        this.widthPercent = widthPercent;
        this.heightPercent = heightPercent;
        this.description = description;
        this.imageResId = imageResId;
    }

    public int getId() { return id; }
    public int getBuildingId() { return buildingId; }
    public String getBuildingName() { return buildingName; }
    public String getRoomCode() { return roomCode; }
    public String getName() { return name; }
    public int getFloor() { return floor; }
    public String getRoomType() { return roomType; }
    public float getXPercent() { return xPercent; }
    public float getYPercent() { return yPercent; }
    public float getWidthPercent() { return widthPercent; }
    public float getHeightPercent() { return heightPercent; }
    public String getDescription() { return description; }
    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }

    public float getCenterX() {
        return xPercent + (widthPercent / 2.0f);
    }

    public float getCenterY() {
        return yPercent + (heightPercent / 2.0f);
    }
}
