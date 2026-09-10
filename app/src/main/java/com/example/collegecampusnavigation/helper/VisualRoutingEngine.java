package com.example.collegecampusnavigation.helper;

import com.example.collegecampusnavigation.R;
import com.example.collegecampusnavigation.model.VisualNavRoute;
import com.example.collegecampusnavigation.model.VisualNavStep;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VisualRoutingEngine {

    public static class CampusLocationItem {
        private final String id;
        private final String name;
        private final String buildingName;
        private final String floorDisplay;
        private final int imageResId;

        public CampusLocationItem(String id, String name, String buildingName, String floorDisplay, int imageResId) {
            this.id = id;
            this.name = name;
            this.buildingName = buildingName;
            this.floorDisplay = floorDisplay;
            this.imageResId = imageResId;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getBuildingName() { return buildingName; }
        public String getFloorDisplay() { return floorDisplay; }
        public int getImageResId() { return imageResId; }

        @Override
        public String toString() {
            return name + " (" + buildingName + ", " + floorDisplay + ")";
        }
    }

    public static List<CampusLocationItem> getAllCampusLocations() {
        List<CampusLocationItem> list = new ArrayList<>();
        list.add(new CampusLocationItem("LOC_GATE", "Main Entrance & Security Gate", "Campus Grounds", "Outdoor", R.drawable.img_main_entrance));
        list.add(new CampusLocationItem("LOC_ACAD_EXT", "Academic Building Main Facade", "Academic Block", "Grounds", R.drawable.img_academic_building));
        list.add(new CampusLocationItem("LOC_RECEPTION", "Main Reception & Info Desk", "Academic Block", "Floor 0", R.drawable.img_main_reception));
        list.add(new CampusLocationItem("LOC_CYBER_204", "Cyber Security & Defense Lab 204", "Academic Block", "Floor 2", R.drawable.img_cybersecurity_lab));
        list.add(new CampusLocationItem("LOC_SMART_CLASS_201", "Smart Classroom & Lecture Hall 201", "Academic Block", "Floor 2", R.drawable.img_smart_classroom));
        list.add(new CampusLocationItem("LOC_NET_LAB", "Advanced Networking Lab 201", "Labs Block", "Floor 2", R.drawable.img_networking_lab));
        list.add(new CampusLocationItem("LOC_MCA_DEPT", "MCA Dept Stage & Amphitheatre", "MCA Department", "Floor 0", R.drawable.img_mca_amphitheatre));
        list.add(new CampusLocationItem("LOC_AUDITORIUM", "Grand 500-Seat Auditorium", "Auditorium Complex", "Floor 0", R.drawable.img_auditorium_hall));
        list.add(new CampusLocationItem("LOC_SEMINAR", "AV Presentation & Seminar Hall", "Auditorium Complex", "Floor 1", R.drawable.img_seminar_hall));
        list.add(new CampusLocationItem("LOC_LIBRARY", "Central Library & Study Pods", "Library Sanctuary", "Floor 1", R.drawable.img_library_building));
        list.add(new CampusLocationItem("LOC_CANTEEN", "Central Canteen & Food Court", "Cafeteria Complex", "Floor 0", R.drawable.img_canteen_exterior));
        list.add(new CampusLocationItem("LOC_ADMIN", "Administration & Dean Office", "Admin Block", "Floor 0", R.drawable.img_admin_building));
        return list;
    }

    public static List<VisualNavRoute> getPresetRoutes() {
        List<VisualNavRoute> routes = new ArrayList<>();

        // 1. Featured Route: Main Entrance -> Cyber Security Lab 204
        routes.add(buildMainGateToCyberLabRoute());

        // 2. Main Entrance -> Smart Classroom 201
        routes.add(buildMainGateToSmartClassRoute());

        // 3. Main Entrance -> Grand Auditorium
        routes.add(buildMainGateToAuditoriumRoute());

        // 4. Main Entrance -> Central Library
        routes.add(buildMainGateToLibraryRoute());

        // 5. Main Entrance -> Central Canteen
        routes.add(buildMainGateToCanteenRoute());

        // 6. Academic Block -> MCA Department Stage
        routes.add(buildAcademicToMcaDeptRoute());

        // 7. Academic Block -> Advanced Networking Lab
        routes.add(buildAcademicToNetLabRoute());

        // 8. Main Entrance -> Administration Block
        routes.add(buildMainGateToAdminRoute());

        return routes;
    }

    public static VisualNavRoute getRouteBetween(String startId, String destId) {
        for (VisualNavRoute r : getPresetRoutes()) {
            if (r.getStartLocationId().equals(startId) && r.getDestinationId().equals(destId)) {
                return r;
            }
        }
        // Build dynamically if not directly preset
        return buildDynamicRoute(startId, destId);
    }

    public static VisualNavRoute buildMainGateToCyberLabRoute() {
        List<VisualNavStep> steps = new ArrayList<>();
        steps.add(new VisualNavStep(
                1,
                "Main Entrance & Security Gate",
                "Proceed straight past the security kiosk under the 'Tech Campus' archway along the central avenue.",
                R.drawable.img_main_entrance,
                VisualNavStep.DirectionType.STRAIGHT,
                "Grounds",
                50,
                "Campus Grounds",
                "Archway • Security Kiosk"
        ));
        steps.add(new VisualNavStep(
                2,
                "Academic Block Exterior Facade",
                "Walk up the paved plaza toward the main entrance canopy of the Academic Building.",
                R.drawable.img_academic_building,
                VisualNavStep.DirectionType.ENTER_DOOR,
                "Grounds",
                60,
                "Academic Plaza",
                "Glass Curtain Wall • Sliding Doors"
        ));
        steps.add(new VisualNavStep(
                3,
                "Ground Floor Lobby & Reception",
                "Enter through the glass sliding doors. Pass the white marble Reception counter on your left and head straight.",
                R.drawable.img_main_reception,
                VisualNavStep.DirectionType.STRAIGHT,
                "Floor 0 (Ground)",
                25,
                "Ground Floor Lobby",
                "Information Desk • Slatted Oak Wall"
        ));
        steps.add(new VisualNavStep(
                4,
                "Ground Floor Elevator Lobby",
                "Approach the stainless steel elevators on the left. Press the Up button and board the elevator to Floor 2.",
                R.drawable.img_stairs,
                VisualNavStep.DirectionType.ELEVATOR_UP,
                "Floor 0 (Ground)",
                25,
                "Elevator Core A",
                "Stainless Elevators • Staircase Adjacent"
        ));
        steps.add(new VisualNavStep(
                5,
                "2nd Floor Elevator Lobby",
                "Step out on Floor 2. Look at the wall directory sign and turn right toward Computer Labs 201-208.",
                R.drawable.img_elevators,
                VisualNavStep.DirectionType.TURN_RIGHT,
                "Floor 2",
                15,
                "Floor 2 Core",
                "Floor 2 Directory Sign • Glass Walls"
        ));
        steps.add(new VisualNavStep(
                6,
                "2nd Floor West Wing Corridor",
                "Follow the illuminated green floor guideline down the corridor toward Room 204.",
                R.drawable.img_corridors,
                VisualNavStep.DirectionType.STRAIGHT,
                "Floor 2",
                20,
                "West Wing Corridor",
                "Room 204 Sign • Glass Laboratory Partitions"
        ));
        steps.add(new VisualNavStep(
                7,
                "Cyber Security & Network Defense Lab 204",
                "You have arrived! Enter Room 204: Cyber Security Command & Datacenter Operations Room.",
                R.drawable.img_cybersecurity_lab,
                VisualNavStep.DirectionType.DESTINATION_REACHED,
                "Floor 2",
                15,
                "Room 204",
                "Threat Monitor Wall • Server Racks • Workstations"
        ));

        return new VisualNavRoute(
                "ROUTE_GATE_TO_CYBER204",
                "LOC_GATE",
                "Main Entrance Gate",
                "LOC_CYBER_204",
                "Cyber Security Lab 204",
                "6-Step Visual Journey • Main Gate to Academic Block Floor 2",
                210,
                3,
                steps
        );
    }

    public static VisualNavRoute buildMainGateToSmartClassRoute() {
        List<VisualNavStep> steps = new ArrayList<>();
        steps.add(new VisualNavStep(1, "Main Entrance & Security Gate", "Head past the security kiosk through the Tech Campus archway.", R.drawable.img_main_entrance, VisualNavStep.DirectionType.STRAIGHT, "Grounds", 50, "Campus Grounds", "Main Arch"));
        steps.add(new VisualNavStep(2, "Academic Building Facade", "Enter the main entrance canopy of the Academic Building.", R.drawable.img_academic_building, VisualNavStep.DirectionType.ENTER_DOOR, "Grounds", 60, "Academic Plaza", "Glass Facade"));
        steps.add(new VisualNavStep(3, "Reception & Lobby", "Pass the Information Desk and walk towards the elevator core.", R.drawable.img_main_reception, VisualNavStep.DirectionType.STRAIGHT, "Floor 0", 25, "Lobby", "Marble Desk"));
        steps.add(new VisualNavStep(4, "Elevator Core Ground Floor", "Take the elevator or stairs up to Floor 2.", R.drawable.img_stairs, VisualNavStep.DirectionType.ELEVATOR_UP, "Floor 0", 25, "Elevator Core", "Elevators"));
        steps.add(new VisualNavStep(5, "2nd Floor Lobby", "Exit the elevator and head left toward Lecture Halls.", R.drawable.img_elevators, VisualNavStep.DirectionType.TURN_LEFT, "Floor 2", 20, "Floor 2", "Signage"));
        steps.add(new VisualNavStep(6, "Smart Classroom & Lecture Hall 201", "You have arrived at Lecture Hall 201 Smart Classroom.", R.drawable.img_smart_classroom, VisualNavStep.DirectionType.DESTINATION_REACHED, "Floor 2", 15, "Room 201", "Dual 4K Displays"));

        return new VisualNavRoute(
                "ROUTE_GATE_TO_SMARTCLASS",
                "LOC_GATE",
                "Main Entrance Gate",
                "LOC_SMART_CLASS_201",
                "Smart Classroom 201",
                "Visual Journey to 2nd Floor Smart Lecture Hall",
                195,
                3,
                steps
        );
    }

    public static VisualNavRoute buildMainGateToAuditoriumRoute() {
        List<VisualNavStep> steps = new ArrayList<>();
        steps.add(new VisualNavStep(1, "Main Entrance Gate", "Enter through Main Gate and follow the east avenue.", R.drawable.img_main_entrance, VisualNavStep.DirectionType.STRAIGHT, "Grounds", 60, "Grounds", "East Avenue"));
        steps.add(new VisualNavStep(2, "Academic Quad Plaza", "Walk across the paved Academic Quadrangle towards the Auditorium Complex.", R.drawable.img_campus_garden, VisualNavStep.DirectionType.TURN_RIGHT, "Grounds", 80, "Central Quad", "Fountain"));
        steps.add(new VisualNavStep(3, "Grand 500-Seat Auditorium", "You have arrived at the Grand Auditorium.", R.drawable.img_auditorium_hall, VisualNavStep.DirectionType.DESTINATION_REACHED, "Floor 0", 40, "Auditorium", "Tiered Theatre"));

        return new VisualNavRoute(
                "ROUTE_GATE_TO_AUDITORIUM",
                "LOC_GATE",
                "Main Entrance Gate",
                "LOC_AUDITORIUM",
                "Grand Auditorium",
                "Direct path from Main Entrance to Grand Auditorium",
                180,
                2,
                steps
        );
    }

    public static VisualNavRoute buildMainGateToLibraryRoute() {
        List<VisualNavStep> steps = new ArrayList<>();
        steps.add(new VisualNavStep(1, "Main Entrance Gate", "Enter through Main Gate along the North walkway.", R.drawable.img_main_entrance, VisualNavStep.DirectionType.STRAIGHT, "Grounds", 50, "Grounds", "North Walkway"));
        steps.add(new VisualNavStep(2, "Central Quadrangle Walkway", "Follow the green lawns towards the Central Library entrance.", R.drawable.img_campus_garden, VisualNavStep.DirectionType.TURN_LEFT, "Grounds", 70, "Campus Quad", "Library Walk"));
        steps.add(new VisualNavStep(3, "Central Library & Study Commons", "You have arrived at the Central Library & Silent Reading Pods.", R.drawable.img_library_building, VisualNavStep.DirectionType.DESTINATION_REACHED, "Floor 1", 30, "Library", "Bookstacks • Study Pods"));

        return new VisualNavRoute(
                "ROUTE_GATE_TO_LIBRARY",
                "LOC_GATE",
                "Main Entrance Gate",
                "LOC_LIBRARY",
                "Central Library",
                "Walk from Main Gate to Central Library Sanctuary",
                150,
                2,
                steps
        );
    }

    public static VisualNavRoute buildMainGateToCanteenRoute() {
        List<VisualNavStep> steps = new ArrayList<>();
        steps.add(new VisualNavStep(1, "Main Entrance Gate", "Enter through Main Gate and turn left towards the South lawns.", R.drawable.img_main_entrance, VisualNavStep.DirectionType.TURN_LEFT, "Grounds", 50, "Grounds", "South Lawns"));
        steps.add(new VisualNavStep(2, "Central Canteen Outdoor Patio", "Approach the Central Canteen outdoor pergola and umbrella seating.", R.drawable.img_canteen_exterior, VisualNavStep.DirectionType.ENTER_DOOR, "Grounds", 60, "Patio", "Timber Pergola"));
        steps.add(new VisualNavStep(3, "College Cafeteria & Food Court", "You have arrived at the College Cafeteria and Dining Hall.", R.drawable.img_canteen_exterior, VisualNavStep.DirectionType.DESTINATION_REACHED, "Floor 0", 20, "Canteen", "Food Counters"));

        return new VisualNavRoute(
                "ROUTE_GATE_TO_CANTEEN",
                "LOC_GATE",
                "Main Entrance Gate",
                "LOC_CANTEEN",
                "Central Canteen",
                "Path to Central Canteen & Dining Hall",
                130,
                2,
                steps
        );
    }

    public static VisualNavRoute buildAcademicToMcaDeptRoute() {
        List<VisualNavStep> steps = new ArrayList<>();
        steps.add(new VisualNavStep(1, "Academic Building Entrance", "Start at the Academic Building main entrance canopy.", R.drawable.img_academic_building, VisualNavStep.DirectionType.ENTER_DOOR, "Grounds", 30, "Academic Plaza", "Canopy"));
        steps.add(new VisualNavStep(2, "Ground Floor Lobby", "Walk past Reception towards the West Wing MCA corridor.", R.drawable.img_main_reception, VisualNavStep.DirectionType.TURN_LEFT, "Floor 0", 40, "Lobby", "Reception"));
        steps.add(new VisualNavStep(3, "MCA Dept Stage & Amphitheatre", "You have arrived at the MCA Department Stage & Amphitheatre (AUD-MCA-100).", R.drawable.img_mca_amphitheatre, VisualNavStep.DirectionType.DESTINATION_REACHED, "Floor 0", 25, "MCA Dept", "Saraswati Relief Stage"));

        return new VisualNavRoute(
                "ROUTE_ACAD_TO_MCA",
                "LOC_ACAD_EXT",
                "Academic Building",
                "LOC_MCA_DEPT",
                "MCA Dept Stage",
                "Indoor ground floor route to MCA Department",
                95,
                1,
                steps
        );
    }

    public static VisualNavRoute buildAcademicToNetLabRoute() {
        List<VisualNavStep> steps = new ArrayList<>();
        steps.add(new VisualNavStep(1, "Ground Floor Reception", "Start at the Academic Building Reception Desk.", R.drawable.img_main_reception, VisualNavStep.DirectionType.STRAIGHT, "Floor 0", 20, "Lobby", "Info Counter"));
        steps.add(new VisualNavStep(2, "Elevator Lobby Ground Floor", "Take the elevator up to Floor 2.", R.drawable.img_stairs, VisualNavStep.DirectionType.ELEVATOR_UP, "Floor 0", 25, "Elevator Core", "Elevators"));
        steps.add(new VisualNavStep(3, "2nd Floor Elevator Lobby", "Exit elevator on Floor 2 and turn left towards Labs Block.", R.drawable.img_elevators, VisualNavStep.DirectionType.TURN_LEFT, "Floor 2", 20, "Floor 2", "Directory"));
        steps.add(new VisualNavStep(4, "Advanced Networking Lab 201", "You have arrived at the Advanced Networking & IT Lab (LAB-NET-201).", R.drawable.img_networking_lab, VisualNavStep.DirectionType.DESTINATION_REACHED, "Floor 2", 30, "Labs Block", "Cisco Racks"));

        return new VisualNavRoute(
                "ROUTE_ACAD_TO_NETLAB",
                "LOC_RECEPTION",
                "Main Reception",
                "LOC_NET_LAB",
                "Networking Lab 201",
                "Route to 2nd Floor Advanced Networking Lab",
                95,
                1,
                steps
        );
    }

    public static VisualNavRoute buildMainGateToAdminRoute() {
        List<VisualNavStep> steps = new ArrayList<>();
        steps.add(new VisualNavStep(1, "Main Entrance Gate", "Enter through Main Gate and follow the West driveway.", R.drawable.img_main_entrance, VisualNavStep.DirectionType.TURN_RIGHT, "Grounds", 50, "Grounds", "West Drive"));
        steps.add(new VisualNavStep(2, "Administration Block Plaza", "You have arrived at the Administration Block & Dean's Office.", R.drawable.img_admin_building, VisualNavStep.DirectionType.DESTINATION_REACHED, "Floor 0", 60, "Admin Block", "Fountain Plaza"));

        return new VisualNavRoute(
                "ROUTE_GATE_TO_ADMIN",
                "LOC_GATE",
                "Main Entrance Gate",
                "LOC_ADMIN",
                "Administration Block",
                "Route from Main Gate to Administration Block",
                110,
                1,
                steps
        );
    }

    private static VisualNavRoute buildDynamicRoute(String startId, String destId) {
        List<VisualNavStep> steps = new ArrayList<>();
        steps.add(new VisualNavStep(1, "Starting Point", "Depart from your current starting location.", R.drawable.ic_campus_nav_app_logo, VisualNavStep.DirectionType.STRAIGHT, "Grounds", 40, "Start", "Departure Point"));
        steps.add(new VisualNavStep(2, "Central Corridor Walkway", "Follow the main campus walkway towards the destination zone.", R.drawable.ic_route, VisualNavStep.DirectionType.STRAIGHT, "Grounds", 60, "Campus Quad", "Wayfinding Sign"));
        steps.add(new VisualNavStep(3, "Destination Area", "You have arrived at your destination.", R.drawable.ic_building_academic, VisualNavStep.DirectionType.DESTINATION_REACHED, "Destination", 30, "Destination", "Arrival"));

        return new VisualNavRoute(
                "DYNAMIC_ROUTE",
                startId,
                "Start Location",
                destId,
                "Target Destination",
                "Dynamic visual navigation sequence",
                130,
                2,
                steps
        );
    }
}
