package com.example.collegecampusnavigation.helper;

import android.graphics.PointF;
import com.example.collegecampusnavigation.model.IndoorRoom;
import java.util.ArrayList;
import java.util.List;

public class IndoorRoutingEngine {

    public static class IndoorPathResult {
        public List<PointF> waypoints;
        public int distanceFeet;
        public int estimatedSeconds;
        public List<String> directions;
        public boolean usesElevator;

        public IndoorPathResult(List<PointF> waypoints, int distanceFeet, int estimatedSeconds, List<String> directions, boolean usesElevator) {
            this.waypoints = waypoints;
            this.distanceFeet = distanceFeet;
            this.estimatedSeconds = estimatedSeconds;
            this.directions = directions;
            this.usesElevator = usesElevator;
        }
    }

    public static IndoorPathResult calculateIndoorPath(IndoorRoom targetRoom, int currentFloor, boolean preferAccessible) {
        List<PointF> points = new ArrayList<>();
        List<String> directions = new ArrayList<>();

        // Start at Building Main Entrance (center-bottom of blueprint, 50%, 92%)
        PointF entrance = new PointF(50.0f, 90.0f);
        PointF corridorMain = new PointF(50.0f, 48.0f); // Central hallway hub

        points.add(entrance);
        directions.add("Enter building through automatic ADA entrance");

        boolean changeFloor = (targetRoom.getFloor() != currentFloor);
        boolean usesElevator = preferAccessible || changeFloor;

        if (changeFloor) {
            PointF elevatorPoint = new PointF(72.0f, 48.0f);
            points.add(corridorMain);
            points.add(elevatorPoint);
            directions.add("Proceed along corridor to Central Elevator");
            directions.add("Take Elevator to Floor " + (targetRoom.getFloor() == 0 ? "G" : targetRoom.getFloor()));
            points.add(new PointF(72.0f, 48.0f)); // Exit elevator on target floor
        }

        points.add(corridorMain);
        directions.add("Follow hallway towards " + targetRoom.getName());

        // Target room center
        PointF roomCenter = new PointF(targetRoom.getCenterX(), targetRoom.getCenterY());
        PointF roomDoor = new PointF(targetRoom.getCenterX(), corridorMain.y);

        points.add(roomDoor);
        points.add(roomCenter);
        directions.add("Arrive at " + targetRoom.getRoomCode() + " (" + targetRoom.getName() + ")");

        int distFeet = 60 + (changeFloor ? 80 : 0) + (int)(Math.abs(roomCenter.x - entrance.x) * 1.5f);
        int estSec = (int)(distFeet / 3.5f); // ~3.5 ft/sec walking speed

        return new IndoorPathResult(points, distFeet, estSec, directions, usesElevator);
    }
}
