package com.example.collegecampusnavigation.helper;

import com.example.collegecampusnavigation.R;
import com.example.collegecampusnavigation.model.PanoramaHotspot;
import com.example.collegecampusnavigation.model.PanoramaScene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PanoramaTourRepository {

    private static final Map<String, PanoramaScene> sceneMap = new HashMap<>();
    private static final List<PanoramaScene> allScenes = new ArrayList<>();

    static {
        initTourScenes();
    }

    private static void initTourScenes() {
        sceneMap.clear();
        allScenes.clear();

        // ==========================================
        // GROUND FLOOR (0) - MCA & MAIN ACADEMIC
        // ==========================================

        // 1. Main Campus Entrance Gate
        PanoramaScene sceneGate = new PanoramaScene(
                "SCENE_GATE",
                "Main Campus Entrance & Arch",
                "Main Security Checkpoint • Tech Boulevard",
                1, "Main Academic Block", 0, "GATE-01",
                R.drawable.img_main_entrance, 0f, 0f, 15f, 85f
        );
        sceneGate.addHotspot(new PanoramaHotspot(
                "H_GATE_RECEPTION", "Main Academic Lobby", "Walk to Admin & Classrooms",
                12f, -5f, PanoramaHotspot.HotspotType.WALK, "SCENE_RECEPTION", "RECEPT-01"
        ));
        sceneGate.addHotspot(new PanoramaHotspot(
                "H_GATE_MCA", "MCA Amphitheatre", "Walk toward MCA Dept & Stage",
                -45f, -2f, PanoramaHotspot.HotspotType.WALK, "SCENE_MCA_AMP", "AUD-MCA-100"
        ));
        registerScene(sceneGate);

        // 2. Main Academic Reception & Lobby
        PanoramaScene sceneReception = new PanoramaScene(
                "SCENE_RECEPTION",
                "Ground Floor Main Reception & Atrium",
                "Information Desk • Central Corridor • Elevators",
                1, "Main Academic Block", 0, "RECEPT-01",
                R.drawable.img_main_reception, 0f, 0f, 50f, 50f
        );
        sceneReception.addHotspot(new PanoramaHotspot(
                "H_REC_ELEVATOR_GF", "Elevator & Stair Core", "Access Floors 1, 2 & 3",
                35f, 2f, PanoramaHotspot.HotspotType.ELEVATOR, "SCENE_ELEVATOR_GF", "ELEVATOR-GF"
        ));
        sceneReception.addHotspot(new PanoramaHotspot(
                "H_REC_MCA", "MCA Department & Stage", "Ground Floor Amphitheatre Wing",
                -60f, -3f, PanoramaHotspot.HotspotType.ENTER_DOOR, "SCENE_MCA_AMP", "AUD-MCA-100"
        ));
        sceneReception.addHotspot(new PanoramaHotspot(
                "H_REC_GATE", "Main Gate & Plaza", "Exit to Campus Grounds",
                175f, -5f, PanoramaHotspot.HotspotType.WALK, "SCENE_GATE", "GATE-01"
        ));
        registerScene(sceneReception);

        // 3. Ground Floor Elevator Core & Stairs
        PanoramaScene sceneElevatorGf = new PanoramaScene(
                "SCENE_ELEVATOR_GF",
                "Elevator Lobby & Central Stairs (Ground Floor)",
                "Elevator Bank A & B • Marble Staircase",
                1, "Main Academic Block", 0, "ELEVATOR-GF",
                R.drawable.img_stairs, 0f, 0f, 65f, 50f
        );
        sceneElevatorGf.addHotspot(new PanoramaHotspot(
                "H_ELEV_UP_F1", "Take Elevator to Floor 1", "Library & Smart Seminar Hall",
                -15f, 15f, PanoramaHotspot.HotspotType.ELEVATOR, "SCENE_LIB_READING", "LIB-READING-01"
        ));
        sceneElevatorGf.addHotspot(new PanoramaHotspot(
                "H_ELEV_UP_F2", "Take Elevator to Floor 2", "Cybersecurity & Networking Labs",
                20f, 20f, PanoramaHotspot.HotspotType.ELEVATOR, "SCENE_ELEVATOR_F2", "ELEVATOR-F2"
        ));
        sceneElevatorGf.addHotspot(new PanoramaHotspot(
                "H_ELEV_REC", "Main Reception Lobby", "Exit to Main Atrium",
                170f, -4f, PanoramaHotspot.HotspotType.WALK, "SCENE_RECEPTION", "RECEPT-01"
        ));
        registerScene(sceneElevatorGf);

        // 4. MCA Department Amphitheatre & Cultural Stage
        PanoramaScene sceneMcaAmp = new PanoramaScene(
                "SCENE_MCA_AMP",
                "MCA Department & Amphitheatre Stage",
                "Cultural Stage • Saraswati Relief • MCA Hall 101",
                3, "MCA Department & Amphitheatre", 0, "AUD-MCA-100",
                R.drawable.img_mca_amphitheatre, 0f, 0f, 30f, 35f
        );
        sceneMcaAmp.addHotspot(new PanoramaHotspot(
                "H_MCA_ROOM_101", "Enter MCA Smart Class 101", "Interactive Multimedia Lecture Hall",
                55f, -2f, PanoramaHotspot.HotspotType.ENTER_DOOR, "SCENE_MCA_101", "MCA-101"
        ));
        sceneMcaAmp.addHotspot(new PanoramaHotspot(
                "H_MCA_REC", "Return to Main Reception", "Corridor to Admin Block",
                -130f, -4f, PanoramaHotspot.HotspotType.WALK, "SCENE_RECEPTION", "RECEPT-01"
        ));
        sceneMcaAmp.addHotspot(new PanoramaHotspot(
                "H_MCA_ELEV", "Elevator to 2nd Floor Labs", "Quick elevator to Cyber Lab",
                -40f, 8f, PanoramaHotspot.HotspotType.ELEVATOR, "SCENE_ELEVATOR_F2", "ELEVATOR-F2"
        ));
        registerScene(sceneMcaAmp);

        // 5. MCA Smart Classroom 101
        PanoramaScene sceneMca101 = new PanoramaScene(
                "SCENE_MCA_101",
                "MCA Smart Classroom 101",
                "60-Capacity Tiered Multimedia Lecture Room",
                3, "MCA Department & Amphitheatre", 0, "MCA-101",
                R.drawable.img_smart_classroom, 0f, 0f, 75f, 30f
        );
        sceneMca101.addHotspot(new PanoramaHotspot(
                "H_101_STAGE", "Exit to Amphitheatre Stage", "Main MCA Atrium & Stage",
                175f, -5f, PanoramaHotspot.HotspotType.ENTER_DOOR, "SCENE_MCA_AMP", "AUD-MCA-100"
        ));
        registerScene(sceneMca101);

        // 6. Main 500-Seat Auditorium
        PanoramaScene sceneAuditorium = new PanoramaScene(
                "SCENE_AUDITORIUM",
                "Main College Auditorium & Stage",
                "500-Seat Tiered Theatre • Dolby Acoustic System",
                5, "Main Auditorium & Seminar Complex", 0, "AUD-MAIN-01",
                R.drawable.img_auditorium_hall, 0f, 0f, 80f, 80f
        );
        sceneAuditorium.addHotspot(new PanoramaHotspot(
                "H_AUD_REC", "Auditorium Foyer & Reception", "Exit to Quadrangle Plaza",
                180f, -5f, PanoramaHotspot.HotspotType.WALK, "SCENE_RECEPTION", "RECEPT-01"
        ));
        sceneAuditorium.addHotspot(new PanoramaHotspot(
                "H_AUD_CANTEEN", "Walk to Cafeteria", "Student Dining & Cafe",
                -75f, -2f, PanoramaHotspot.HotspotType.WALK, "SCENE_CANTEEN", "CANTEEN-01"
        ));
        registerScene(sceneAuditorium);

        // 7. Student Cafeteria & Food Court
        PanoramaScene sceneCanteen = new PanoramaScene(
                "SCENE_CANTEEN",
                "College Cafeteria & Student Dining",
                "Multi-Cuisine Meal Service • Stainless Steel Seating",
                6, "College Cafeteria & Student Dining", 0, "CANTEEN-01",
                R.drawable.img_canteen_exterior, 0f, 0f, 20f, 20f
        );
        sceneCanteen.addHotspot(new PanoramaHotspot(
                "H_CAN_GATE", "Exit to Campus Walkway", "Walk to Entrance Gate",
                160f, -4f, PanoramaHotspot.HotspotType.WALK, "SCENE_GATE", "GATE-01"
        ));
        sceneCanteen.addHotspot(new PanoramaHotspot(
                "H_CAN_AUD", "Walk to Main Auditorium", "Auditorium & AV Complex",
                45f, -2f, PanoramaHotspot.HotspotType.WALK, "SCENE_AUDITORIUM", "AUD-MAIN-01"
        ));
        registerScene(sceneCanteen);

        // ==========================================
        // FLOOR 1 - CENTRAL LIBRARY & SEMINAR SUITE
        // ==========================================

        // 8. Central Library Silent Sanctuary
        PanoramaScene sceneLibrary = new PanoramaScene(
                "SCENE_LIB_READING",
                "Central Library & Silent Study Sanctuary",
                "Floor 1 • Bookstacks • Digital Research Pods",
                2, "Central Library & Media Center", 1, "LIB-READING-01",
                R.drawable.img_reading_room, 0f, 0f, 40f, 40f
        );
        sceneLibrary.addHotspot(new PanoramaHotspot(
                "H_LIB_SEM", "Smart AV Seminar Hall", "Executive Presentation Suite",
                65f, 0f, PanoramaHotspot.HotspotType.ENTER_DOOR, "SCENE_SEM_HALL", "SEM-AV-02"
        ));
        sceneLibrary.addHotspot(new PanoramaHotspot(
                "H_LIB_DOWN_GF", "Elevator to Ground Floor", "Main Lobby & MCA Dept",
                -140f, -8f, PanoramaHotspot.HotspotType.ELEVATOR, "SCENE_ELEVATOR_GF", "ELEVATOR-GF"
        ));
        sceneLibrary.addHotspot(new PanoramaHotspot(
                "H_LIB_UP_F2", "Elevator to Floor 2 Labs", "Cybersecurity & Cisco Labs",
                -110f, 12f, PanoramaHotspot.HotspotType.ELEVATOR, "SCENE_ELEVATOR_F2", "ELEVATOR-F2"
        ));
        registerScene(sceneLibrary);

        // 9. Smart AV Seminar Hall
        PanoramaScene sceneSemHall = new PanoramaScene(
                "SCENE_SEM_HALL",
                "Executive Smart AV Seminar Hall",
                "Floor 1 • SAP HANA Suite • Tiered Seating",
                5, "Main Auditorium & Seminar Complex", 1, "SEM-AV-02",
                R.drawable.img_seminar_hall, 0f, 0f, 75f, 40f
        );
        sceneSemHall.addHotspot(new PanoramaHotspot(
                "H_SEM_LIB", "Return to Library Sanctuary", "Central Bookstacks & Study Area",
                175f, -4f, PanoramaHotspot.HotspotType.WALK, "SCENE_LIB_READING", "LIB-READING-01"
        ));
        registerScene(sceneSemHall);

        // ==========================================
        // FLOOR 2 - TECH LABS & CYBERSECURITY HUB
        // ==========================================

        // 10. Floor 2 Elevator Lobby & Signage
        PanoramaScene sceneElevatorF2 = new PanoramaScene(
                "SCENE_ELEVATOR_F2",
                "Floor 2 Elevator Lobby & Lab Concourse",
                "Direction Signage • Tech Corridor • Restrooms",
                4, "Advanced Computer & Networking Labs", 2, "ELEVATOR-F2",
                R.drawable.img_elevators, 0f, 0f, 35f, 65f
        );
        sceneElevatorF2.addHotspot(new PanoramaHotspot(
                "H_F2_CORRIDOR", "Tech Labs Corridor", "Access Lab 204 & Cisco Networking Lab",
                -10f, -3f, PanoramaHotspot.HotspotType.WALK, "SCENE_CORRIDOR_F2", "CORR-200"
        ));
        sceneElevatorF2.addHotspot(new PanoramaHotspot(
                "H_F2_DOWN_GF", "Take Elevator to Ground Floor", "Main Reception & MCA Stage",
                160f, -12f, PanoramaHotspot.HotspotType.ELEVATOR, "SCENE_ELEVATOR_GF", "ELEVATOR-GF"
        ));
        sceneElevatorF2.addHotspot(new PanoramaHotspot(
                "H_F2_SMART_201", "Smart Classroom 201", "Lecture Room 201",
                70f, -2f, PanoramaHotspot.HotspotType.ENTER_DOOR, "SCENE_SMART_CLASS_201", "ROOM-201"
        ));
        registerScene(sceneElevatorF2);

        // 11. Floor 2 Tech Labs Corridor
        PanoramaScene sceneCorridorF2 = new PanoramaScene(
                "SCENE_CORRIDOR_F2",
                "Floor 2 Advanced Tech Laboratories Corridor",
                "Connecting Cybersecurity, AI Hub & Server Room",
                4, "Advanced Computer & Networking Labs", 2, "CORR-200",
                R.drawable.img_corridors, 0f, 0f, 50f, 40f
        );
        sceneCorridorF2.addHotspot(new PanoramaHotspot(
                "H_CORR_CYBER", "Enter Lab 204 (Cybersecurity)", "AI Workstations & Security Sandbox",
                25f, -2f, PanoramaHotspot.HotspotType.LAB, "SCENE_CYBER_204", "LAB-CYBER-204"
        ));
        sceneCorridorF2.addHotspot(new PanoramaHotspot(
                "H_CORR_NET", "Enter Networking Lab 201", "Cisco Routers & Network Racks",
                -45f, -3f, PanoramaHotspot.HotspotType.LAB, "SCENE_NET_LAB", "LAB-NET-201"
        ));
        sceneCorridorF2.addHotspot(new PanoramaHotspot(
                "H_CORR_LOBBY", "Back to Elevator Lobby", "Elevator Core & Stairs",
                178f, -4f, PanoramaHotspot.HotspotType.WALK, "SCENE_ELEVATOR_F2", "ELEVATOR-F2"
        ));
        registerScene(sceneCorridorF2);

        // 12. Lab 204 Cybersecurity & AI Workstations
        PanoramaScene sceneCyber204 = new PanoramaScene(
                "SCENE_CYBER_204",
                "Lab 204: Cybersecurity & Cloud Computing Center",
                "High-Performance Dual-Monitor GPU Workstations",
                4, "Advanced Computer & Networking Labs", 2, "LAB-CYBER-204",
                R.drawable.img_cybersecurity_lab, 0f, 0f, 75f, 30f
        );
        sceneCyber204.addHotspot(new PanoramaHotspot(
                "H_CYBER_CORR", "Exit to Corridor", "Tech Labs Concourse",
                175f, -5f, PanoramaHotspot.HotspotType.WALK, "SCENE_CORRIDOR_F2", "CORR-200"
        ));
        registerScene(sceneCyber204);

        // 13. Networking & Cisco Enterprise Lab
        PanoramaScene sceneNetLab = new PanoramaScene(
                "SCENE_NET_LAB",
                "Cisco Networking & Systems Infrastructure Lab",
                "Hardware Server Racks • Packet Analyzer Terminals",
                4, "Advanced Computer & Networking Labs", 2, "LAB-NET-201",
                R.drawable.img_networking_lab, 0f, 0f, 25f, 30f
        );
        sceneNetLab.addHotspot(new PanoramaHotspot(
                "H_NET_CORR", "Exit to Corridor", "Tech Labs Concourse",
                170f, -5f, PanoramaHotspot.HotspotType.WALK, "SCENE_CORRIDOR_F2", "CORR-200"
        ));
        registerScene(sceneNetLab);

        // 14. Smart Classroom 201
        PanoramaScene sceneSmartClass = new PanoramaScene(
                "SCENE_SMART_CLASS_201",
                "Smart Interactive Classroom 201",
                "Floor 2 • Dual 4K Smart Interactive Displays",
                1, "Main Academic Block", 2, "ROOM-201",
                R.drawable.img_smart_classroom, 0f, 0f, 70f, 70f
        );
        sceneSmartClass.addHotspot(new PanoramaHotspot(
                "H_201_LOBBY", "Exit to Floor 2 Lobby", "Elevator Core & Labs Corridor",
                175f, -4f, PanoramaHotspot.HotspotType.WALK, "SCENE_ELEVATOR_F2", "ELEVATOR-F2"
        ));
        registerScene(sceneSmartClass);

        // ==========================================
        // FLOOR 3 - ROOFTOP & SKY LOUNGE
        // ==========================================

        // 15. Rooftop Dining & Student Lounge
        PanoramaScene sceneRooftop = new PanoramaScene(
                "SCENE_ROOFTOP_DINE",
                "Floor 3 Sky Garden & Rooftop Lounge",
                "Shaded Gazebos • Panoramic Campus Views",
                6, "College Cafeteria & Student Dining", 3, "TERRACE-DINE",
                R.drawable.img_canteen_exterior, 0f, 0f, 50f, 50f
        );
        sceneRooftop.addHotspot(new PanoramaHotspot(
                "H_ROOF_DOWN_F2", "Elevator to Floor 2 Labs", "Computer & AI Labs",
                -120f, -10f, PanoramaHotspot.HotspotType.ELEVATOR, "SCENE_ELEVATOR_F2", "ELEVATOR-F2"
        ));
        sceneRooftop.addHotspot(new PanoramaHotspot(
                "H_ROOF_DOWN_GF", "Elevator to Ground Floor", "Main Entrance & Cafeteria",
                140f, -15f, PanoramaHotspot.HotspotType.ELEVATOR, "SCENE_ELEVATOR_GF", "ELEVATOR-GF"
        ));
        registerScene(sceneRooftop);
    }

    private static void registerScene(PanoramaScene scene) {
        sceneMap.put(scene.getId(), scene);
        allScenes.add(scene);
    }

    public static PanoramaScene getScene(String sceneId) {
        return sceneMap.get(sceneId);
    }

    public static List<PanoramaScene> getAllScenes() {
        return new ArrayList<>(allScenes);
    }

    public static PanoramaScene getInitialSceneForBuildingAndFloor(int buildingId, int floor) {
        for (PanoramaScene scene : allScenes) {
            if (scene.getBuildingId() == buildingId && scene.getFloor() == floor) {
                return scene;
            }
        }
        for (PanoramaScene scene : allScenes) {
            if (scene.getFloor() == floor) {
                return scene;
            }
        }
        return sceneMap.get("SCENE_MCA_AMP");
    }

    public static PanoramaScene findSceneByRoomCode(String roomCode) {
        if (roomCode == null || roomCode.trim().isEmpty()) return null;
        String normalized = roomCode.trim().toUpperCase();

        for (PanoramaScene s : allScenes) {
            if (s.getRoomCode() != null && s.getRoomCode().equalsIgnoreCase(normalized)) {
                return s;
            }
        }
        for (PanoramaScene s : allScenes) {
            if (s.getRoomCode() != null && (s.getRoomCode().contains(normalized) || normalized.contains(s.getRoomCode()))) {
                return s;
            }
        }
        if (normalized.contains("CYBER") || normalized.contains("204")) return getScene("SCENE_CYBER_204");
        if (normalized.contains("NET") || normalized.contains("201")) return getScene("SCENE_NET_LAB");
        if (normalized.contains("MCA") || normalized.contains("100") || normalized.contains("AMP")) return getScene("SCENE_MCA_AMP");
        if (normalized.contains("LIB")) return getScene("SCENE_LIB_READING");
        if (normalized.contains("AUD")) return getScene("SCENE_AUDITORIUM");
        if (normalized.contains("CANTEEN") || normalized.contains("CAFE")) return getScene("SCENE_CANTEEN");
        if (normalized.contains("SEM") || normalized.contains("AV")) return getScene("SCENE_SEM_HALL");
        if (normalized.contains("ELEV")) return getScene("SCENE_ELEVATOR_GF");

        return null;
    }
}
