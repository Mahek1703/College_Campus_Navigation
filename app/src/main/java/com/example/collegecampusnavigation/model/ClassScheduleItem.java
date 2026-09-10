package com.example.collegecampusnavigation.model;

public class ClassScheduleItem {
    private int id;
    private String dayOfWeek;
    private String courseCode;
    private String courseName;
    private String startTime;
    private String endTime;
    private int startHour24;
    private int startMinute24;
    private int endHour24;
    private int endMinute24;
    private String buildingName;
    private int buildingId;
    private String roomCode;
    private int floor;
    private String instructor;
    private String category;
    private String colorHex;

    public ClassScheduleItem(int id, String dayOfWeek, String courseCode, String courseName,
                             String startTime, String endTime, int startHour24, int startMinute24,
                             int endHour24, int endMinute24, String buildingName, int buildingId,
                             String roomCode, int floor, String instructor, String category, String colorHex) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startHour24 = startHour24;
        this.startMinute24 = startMinute24;
        this.endHour24 = endHour24;
        this.endMinute24 = endMinute24;
        this.buildingName = buildingName;
        this.buildingId = buildingId;
        this.roomCode = roomCode;
        this.floor = floor;
        this.instructor = instructor;
        this.category = category;
        this.colorHex = colorHex;
    }

    public int getId() { return id; }
    public String getDayOfWeek() { return dayOfWeek; }
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public int getStartHour24() { return startHour24; }
    public int getStartMinute24() { return startMinute24; }
    public int getEndHour24() { return endHour24; }
    public int getEndMinute24() { return endMinute24; }
    public String getBuildingName() { return buildingName; }
    public int getBuildingId() { return buildingId; }
    public String getRoomCode() { return roomCode; }
    public int getFloor() { return floor; }
    public String getInstructor() { return instructor; }
    public String getCategory() { return category; }
    public String getColorHex() { return colorHex; }

    public int getStartTotalMinutes() {
        return startHour24 * 60 + startMinute24;
    }

    public int getEndTotalMinutes() {
        return endHour24 * 60 + endMinute24;
    }
}
