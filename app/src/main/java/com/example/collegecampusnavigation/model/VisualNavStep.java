package com.example.collegecampusnavigation.model;

import java.io.Serializable;

public class VisualNavStep implements Serializable {

    public enum DirectionType {
        STRAIGHT,
        TURN_LEFT,
        TURN_RIGHT,
        ELEVATOR_UP,
        ELEVATOR_DOWN,
        STAIRS_UP,
        STAIRS_DOWN,
        ENTER_DOOR,
        DESTINATION_REACHED
    }

    private final int stepNumber;
    private final String title;
    private final String instruction;
    private final int imageResId;
    private final DirectionType directionType;
    private final String floorLevel;
    private final int distanceMeters;
    private final String areaTag;
    private final String landmarkDetail;

    public VisualNavStep(int stepNumber, String title, String instruction, int imageResId,
                         DirectionType directionType, String floorLevel, int distanceMeters,
                         String areaTag, String landmarkDetail) {
        this.stepNumber = stepNumber;
        this.title = title;
        this.instruction = instruction;
        this.imageResId = imageResId;
        this.directionType = directionType;
        this.floorLevel = floorLevel;
        this.distanceMeters = distanceMeters;
        this.areaTag = areaTag;
        this.landmarkDetail = landmarkDetail;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public String getTitle() {
        return title;
    }

    public String getInstruction() {
        return instruction;
    }

    public int getImageResId() {
        return imageResId;
    }

    public DirectionType getDirectionType() {
        return directionType;
    }

    public String getFloorLevel() {
        return floorLevel;
    }

    public int getDistanceMeters() {
        return distanceMeters;
    }

    public String getAreaTag() {
        return areaTag;
    }

    public String getLandmarkDetail() {
        return landmarkDetail;
    }

    public String getDirectionIconSymbol() {
        switch (directionType) {
            case STRAIGHT: return "⬆️";
            case TURN_LEFT: return "⬅️";
            case TURN_RIGHT: return "➡️";
            case ELEVATOR_UP: return "🛗⬆️";
            case ELEVATOR_DOWN: return "🛗⬇️";
            case STAIRS_UP: return "🪜⬆️";
            case STAIRS_DOWN: return "🪜⬇️";
            case ENTER_DOOR: return "🚪";
            case DESTINATION_REACHED: return "🎯";
            default: return "📍";
        }
    }

    public String getDirectionText() {
        switch (directionType) {
            case STRAIGHT: return "Go Straight Ahead";
            case TURN_LEFT: return "Turn Left";
            case TURN_RIGHT: return "Turn Right";
            case ELEVATOR_UP: return "Take Elevator Up";
            case ELEVATOR_DOWN: return "Take Elevator Down";
            case STAIRS_UP: return "Take Stairs Up";
            case STAIRS_DOWN: return "Take Stairs Down";
            case ENTER_DOOR: return "Enter Entrance";
            case DESTINATION_REACHED: return "You Have Arrived";
            default: return "Follow Path";
        }
    }
}
