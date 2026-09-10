package com.example.collegecampusnavigation.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.collegecampusnavigation.R;
import com.example.collegecampusnavigation.model.CampusBuilding;
import com.example.collegecampusnavigation.model.ClassScheduleItem;
import com.example.collegecampusnavigation.model.IndoorRoom;
import com.example.collegecampusnavigation.model.PointOfInterest;
import com.example.collegecampusnavigation.model.ShuttleStop;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CampusNav.db";
    private static final int DATABASE_VERSION = 4; // Bumped version for photos and full college data

    // Table Names
    public static final String TABLE_BUILDINGS = "buildings";
    public static final String TABLE_INDOOR_ROOMS = "indoor_rooms";
    public static final String TABLE_SCHEDULES = "class_schedules";
    public static final String TABLE_POIS = "points_of_interest";
    public static final String TABLE_SHUTTLE_STOPS = "shuttle_stops";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Buildings Table
        db.execSQL("CREATE TABLE " + TABLE_BUILDINGS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "description TEXT, " +
                "latitude REAL, " +
                "longitude REAL, " +
                "category TEXT, " +
                "total_floors INTEGER, " +
                "open_hours TEXT, " +
                "occupancy_rate INTEGER, " +
                "is_accessible INTEGER, " +
                "wheelchair_lat REAL, " +
                "wheelchair_lng REAL, " +
                "departments TEXT, " +
                "image_res_id INTEGER)");

        // 2. Indoor Rooms Table
        db.execSQL("CREATE TABLE " + TABLE_INDOOR_ROOMS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "building_id INTEGER, " +
                "building_name TEXT, " +
                "room_code TEXT, " +
                "name TEXT, " +
                "floor INTEGER, " +
                "room_type TEXT, " +
                "x_pct REAL, " +
                "y_pct REAL, " +
                "w_pct REAL, " +
                "h_pct REAL, " +
                "description TEXT, " +
                "image_res_id INTEGER)");

        // 3. Class Schedules Table
        db.execSQL("CREATE TABLE " + TABLE_SCHEDULES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "day_of_week TEXT, " +
                "course_code TEXT, " +
                "course_name TEXT, " +
                "start_time TEXT, " +
                "end_time TEXT, " +
                "start_h INTEGER, " +
                "start_m INTEGER, " +
                "end_h INTEGER, " +
                "end_m INTEGER, " +
                "building_name TEXT, " +
                "building_id INTEGER, " +
                "room_code TEXT, " +
                "floor INTEGER, " +
                "instructor TEXT, " +
                "category TEXT, " +
                "color_hex TEXT)");

        // 4. Points of Interest Table
        db.execSQL("CREATE TABLE " + TABLE_POIS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "category TEXT, " +
                "building_name TEXT, " +
                "building_id INTEGER, " +
                "floor INTEGER, " +
                "latitude REAL, " +
                "longitude REAL, " +
                "occupancy_rate INTEGER, " +
                "max_capacity INTEGER, " +
                "available_units INTEGER, " +
                "is_open INTEGER, " +
                "status_text TEXT, " +
                "features TEXT, " +
                "image_res_id INTEGER)");

        // 5. Shuttle Stops Table
        db.execSQL("CREATE TABLE " + TABLE_SHUTTLE_STOPS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "latitude REAL, " +
                "longitude REAL, " +
                "route_name TEXT, " +
                "eta_minutes INTEGER, " +
                "next_bus_time TEXT)");

        seedAllCampusData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUILDINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INDOOR_ROOMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POIS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHUTTLE_STOPS);
        onCreate(db);
    }

    private void seedAllCampusData(SQLiteDatabase db) {
        // --- 1. Exterior & Campus Buildings Data ---
        insertBuilding(db, 1, "Main Academic Block", "Primary lecture halls, academic administration, normal & smart classrooms, faculty offices.", 19.2183, 73.1631, "Campus", 4, "07:30 AM - 09:30 PM", 62, 1, 19.2182, 73.1630, "Academic Deans, General Classrooms", R.drawable.img_academic_building);
        insertBuilding(db, 2, "Central Library & Media Center", "4 floors of digital archives, silent reading rooms, study pods, group study rooms & garden.", 19.2188, 73.1638, "Library", 4, "08:00 AM - 11:00 PM", 38, 1, 19.2187, 73.1637, "Library Resources, Research Archival", R.drawable.img_library_building);
        insertBuilding(db, 3, "MCA Department & Amphitheatre", "MCA classrooms, departmental faculty rooms, amphitheatre & cultural stage.", 19.2185, 73.1627, "MCA Dept", 3, "08:00 AM - 08:30 PM", 55, 1, 19.2184, 73.1626, "MCA, Computer Applications, Software Tech", R.drawable.img_mca_amphitheatre);
        insertBuilding(db, 4, "Advanced Computer & Networking Labs", "State-of-the-art Networking Lab, Cisco routers, AI/ML GPU workstations, Cybersecurity lab.", 19.2192, 73.1633, "Labs", 3, "08:00 AM - 08:00 PM", 68, 1, 19.2191, 73.1632, "Computer Engineering, Networking, AI/ML", R.drawable.img_computer_lab);
        insertBuilding(db, 5, "Main Auditorium & Seminar Complex", "500-seat tiered theatre auditorium & executive smart AV seminar hall for conferences.", 19.2176, 73.1636, "Auditorium", 2, "08:30 AM - 09:00 PM", 45, 1, 19.2175, 73.1635, "Events, Conferences, Seminars", R.drawable.img_auditorium_hall);
        insertBuilding(db, 6, "College Cafeteria & Student Dining", "Spacious dining hall with outdoor patio seating, student lounge, fresh food kiosks.", 19.2178, 73.1625, "Canteen", 2, "07:00 AM - 10:00 PM", 78, 1, 19.2177, 73.1624, "Food Services, Refreshments, Dining", R.drawable.img_canteen_exterior);
        insertBuilding(db, 7, "Sports & Recreation Arena", "Athletic football & soccer field, red running track, basketball courts, and indoor gym.", 19.2195, 73.1645, "Sports", 2, "06:00 AM - 09:00 PM", 30, 1, 19.2194, 73.1644, "Athletics, Track & Field, Fitness", R.drawable.img_sports_ground);
        insertBuilding(db, 8, "Administration & Admission Hall", "Principal's office, Vice Principal, Examination cell, Accounts, and Student Affairs.", 19.2180, 73.1642, "Administration", 3, "08:30 AM - 05:00 PM", 42, 1, 19.2179, 73.1641, "Registrar, Admissions, Administration", R.drawable.img_admin_building);

        // --- 2. Indoor Rooms, Labs, Offices & Facilities Data ---

        // Building 1: Main Academic Block
        insertIndoorRoom(db, 1, "Main Academic Block", "SEC-GATE-01", "Campus Main Security Gate", 0, "Security", 10, 10, 30, 20, "24/7 Security checkpoint, campus access monitoring, barrier gates", R.drawable.img_security_gate);
        insertIndoorRoom(db, 1, "Main Academic Block", "MAIN-ENTRANCE", "Academic Main Entrance & Ramps", 0, "Entrance", 45, 10, 30, 20, "Grand architectural entry with accessible wheelchair ramp and automatic glass doors", R.drawable.img_building_entrances);
        insertIndoorRoom(db, 1, "Main Academic Block", "RECEPT-01", "Main Reception & Information Desk", 0, "Office", 10, 35, 35, 25, "Central visitor helpdesk, student inquiries, campus directory", R.drawable.img_main_reception);
        insertIndoorRoom(db, 1, "Main Academic Block", "CLS-NORM-101", "General Lecture Classroom 101", 0, "Classroom", 50, 35, 40, 25, "Modern collegiate classroom with wooden desks and HD ceiling projector", R.drawable.img_normal_classroom);
        insertIndoorRoom(db, 1, "Main Academic Block", "CORR-GF", "Central Ground Floor Corridor", 0, "Corridor", 10, 65, 80, 15, "Spacious well-lit central corridor with directional signboards and room indicators", R.drawable.img_corridors);
        insertIndoorRoom(db, 1, "Main Academic Block", "ELEV-GF", "Elevator Lobby & Grand Staircase", 0, "Elevator", 10, 82, 35, 15, "Dual elevator shafts and wide granite stairways connecting all 4 floors", R.drawable.img_stairs);
        insertIndoorRoom(db, 1, "Main Academic Block", "MED-ROOM", "Campus Medical & First Aid Center", 0, "Amenity", 50, 82, 30, 15, "Campus healthcare clinic, first-aid station, nurse on duty", R.drawable.img_medical_room);

        // Building 1 - Floor 1: Administration Offices
        insertIndoorRoom(db, 1, "Main Academic Block", "PRIN-OFFICE", "Principal's Executive Office", 1, "Office", 10, 15, 35, 30, "Executive conference chamber and office of the College Principal", R.drawable.img_principal_office);
        insertIndoorRoom(db, 1, "Main Academic Block", "V-PRIN-OFFICE", "Vice Principal's Office", 1, "Office", 50, 15, 35, 30, "Academic coordinator and Vice Principal office", R.drawable.img_vice_principal_office);
        insertIndoorRoom(db, 1, "Main Academic Block", "EXAM-CELL", "Examination & Confidential Cell", 1, "Office", 10, 50, 35, 25, "University examination registration, transcripts, and evaluation office", R.drawable.img_exam_cell);
        insertIndoorRoom(db, 1, "Main Academic Block", "ACCOUNTS-OFFICE", "Accounts & Finance Department", 1, "Office", 50, 50, 35, 25, "Student fees payment, scholarships, and payroll processing desk", R.drawable.img_accounts_office);
        insertIndoorRoom(db, 1, "Main Academic Block", "STUDENT-AFFAIRS", "Student Affairs & Council Office", 1, "Office", 10, 78, 35, 20, "Student council lounge, clubs headquarters, and extracurricular activity hub", R.drawable.img_student_affairs);
        insertIndoorRoom(db, 1, "Main Academic Block", "STAFF-ROOM-01", "Faculty Common Staff Room", 1, "Office", 50, 78, 35, 20, "Comfortable faculty seating, personal workstations, and tea lounge", R.drawable.img_staff_room);

        // Building 3: MCA Department & Smart Classrooms
        insertIndoorRoom(db, 3, "MCA Department & Amphitheatre", "AUD-MCA-100", "MCA Amphitheatre Stage", 0, "Classroom", 10, 15, 45, 35, "Stage & amphitheatre with cultural stage, fresher fests and induction events", R.drawable.img_mca_amphitheatre);
        insertIndoorRoom(db, 3, "MCA Department & Amphitheatre", "MCA-101", "Interactive Smart Classroom 101", 0, "Classroom", 60, 15, 30, 30, "Multi-touch digital smartboards, modular rolling desks, and laptop charging hubs", R.drawable.img_smart_classroom);
        insertIndoorRoom(db, 3, "MCA Department & Amphitheatre", "MCA-HOD", "HOD & Computer Applications Office", 0, "Office", 10, 58, 35, 28, "MCA Department Head and Professors Consultation Office", R.drawable.img_hod_office);
        insertIndoorRoom(db, 3, "MCA Department & Amphitheatre", "MCA-LOUNGE", "Student Collaborative Lounge", 0, "Amenity", 50, 58, 25, 20, "Cozy study booths, charging stations, and collaborative group seating", R.drawable.img_student_lounge);
        insertIndoorRoom(db, 3, "MCA Department & Amphitheatre", "STAIRS-MCA", "MCA West Staircase & Signboards", 0, "Stairs", 78, 58, 15, 20, "Stairs to 1st Floor MCA Classrooms with digital floor directory", R.drawable.img_signboards);

        // Building 4: Advanced Computer, Networking & AI/ML Labs
        insertIndoorRoom(db, 4, "Advanced Computer & Networking Labs", "LAB-CS-101", "Computer Programming Lab 101", 1, "Laboratory", 10, 15, 40, 35, "60 Dual-monitor desktop workstations with Python, C++, and Java IDEs", R.drawable.img_computer_lab);
        insertIndoorRoom(db, 4, "Advanced Computer & Networking Labs", "LAB-AIML-102", "AI & Machine Learning Lab 102", 1, "Laboratory", 55, 15, 38, 35, "High-performance GPU workstations for Deep Learning & Neural Network training", R.drawable.img_aiml_lab);
        insertIndoorRoom(db, 4, "Advanced Computer & Networking Labs", "LAB-NET-201", "Advanced Cisco Networking Lab", 2, "Laboratory", 10, 15, 45, 35, "Live Cisco server racks, switches, routers, packet tracer testing benches", R.drawable.img_networking_lab);
        insertIndoorRoom(db, 4, "Advanced Computer & Networking Labs", "LAB-CYBER-204", "Cyber Security Operations Lab 204", 2, "Laboratory", 60, 15, 35, 35, "Cyber defense sandbox, ethical hacking consoles, SOC monitoring screens", R.drawable.img_cybersecurity_lab);
        insertIndoorRoom(db, 4, "Advanced Computer & Networking Labs", "LAB-ELEC-205", "Electronics & Microcontrollers Lab", 2, "Laboratory", 10, 58, 40, 28, "Digital oscilloscopes, embedded system kits, Raspberry Pi & Arduino setups", R.drawable.img_electronics_lab);
        insertIndoorRoom(db, 4, "Advanced Computer & Networking Labs", "LAB-MECH-206", "Mechanical CAD & Simulation Lab", 2, "Laboratory", 55, 58, 38, 28, "3D CAD modeling workstations, FEA simulation tools, 3D rapid prototyping printers", R.drawable.img_mechanical_lab);

        // Building 5: Main Auditorium & Seminar Complex
        insertIndoorRoom(db, 5, "Main Auditorium & Seminar Complex", "AUD-MAIN-01", "Grand College Auditorium", 0, "Classroom", 10, 15, 55, 40, "500-seat tiered theatre auditorium with Dolby Atmos sound and motorized 4K screen", R.drawable.img_auditorium_hall);
        insertIndoorRoom(db, 5, "Main Auditorium & Seminar Complex", "SEM-AV-02", "Executive Seminar Presentation Hall", 1, "Classroom", 10, 15, 50, 38, "SAP HANA & corporate tech seminar hall with dual projectors and smart podium", R.drawable.img_seminar_hall);

        // Building 2: Central Library & Media Center
        insertIndoorRoom(db, 2, "Central Library & Media Center", "LIB-CIRC-01", "Main Library & Circulation Desk", 0, "Office", 10, 15, 30, 25, "Book issue & return desk, digital catalog OPAC terminals", R.drawable.img_central_library_interior);
        insertIndoorRoom(db, 2, "Central Library & Media Center", "LIB-READING-01", "Quiet Reading Room Sanctuary", 1, "Amenity", 10, 15, 55, 40, "Silent individual study pods, Wi-Fi 6, reference book stacks, natural lighting", R.drawable.img_reading_room);
        insertIndoorRoom(db, 2, "Central Library & Media Center", "LIB-STUDY-02", "Open Study Area & Garden View", 1, "Amenity", 70, 15, 25, 40, "Collaborative study tables overlooking the botanical courtyard garden", R.drawable.img_study_area);

        // Building 6: College Cafeteria & Student Dining
        insertIndoorRoom(db, 6, "College Cafeteria & Student Dining", "CANTEEN-01", "Main Dining Hall & Food Court", 0, "Dining", 10, 15, 55, 45, "Multi-cuisine food court, fresh juice counter, and stainless steel dining tables", R.drawable.img_canteen_exterior);
        insertIndoorRoom(db, 6, "College Cafeteria & Student Dining", "CANTEEN-PATIO", "Outdoor Shaded Garden Patio", 0, "Dining", 70, 15, 25, 45, "Open-air patio dining with umbrellas surrounded by campus greenery", R.drawable.img_campus_garden);

        // --- 3. Class Schedule Items (5 Days) ---
        insertSchedule(db, "Monday", "MCA-301", "Advanced Web Frameworks & Cloud", "09:00 AM", "10:15 AM", 9, 0, 10, 15, "MCA Department & Amphitheatre", 3, "MCA-101", 0, "Prof. Rajesh Sharma", "Lecture", "#1E88E5");
        insertSchedule(db, "Monday", "NET-210", "Network Protocols & Routing", "10:30 AM", "12:30 PM", 10, 30, 12, 30, "Advanced Computer & Networking Labs", 4, "LAB-NET-201", 2, "Prof. Anita Desai", "Lab", "#7E57C2");
        insertSchedule(db, "Monday", "SAP-400", "Enterprise SAP HANA Systems", "02:00 PM", "04:00 PM", 14, 0, 16, 0, "Main Auditorium & Seminar Complex", 5, "SEM-AV-02", 1, "Dr. Vikram Kulkarni", "Seminar", "#00897B");

        insertSchedule(db, "Tuesday", "MCA-305", "Database Systems & Optimization", "09:30 AM", "11:00 AM", 9, 30, 11, 0, "MCA Department & Amphitheatre", 3, "MCA-101", 0, "Dr. Sunita Rao", "Lecture", "#E65100");
        insertSchedule(db, "Tuesday", "AI-450", "Deep Learning & Neural Networks", "11:15 AM", "01:15 PM", 11, 15, 13, 15, "Advanced Computer & Networking Labs", 4, "LAB-AIML-102", 1, "Prof. Arvind Mehta", "Lab", "#C2185B");
        insertSchedule(db, "Tuesday", "CULT-101", "Freshers Orientation & Cultural Forum", "02:30 PM", "04:30 PM", 14, 30, 16, 30, "MCA Department & Amphitheatre", 3, "AUD-MCA-100", 0, "Student Council", "Activity", "#3949AB");

        insertSchedule(db, "Wednesday", "MCA-301", "Advanced Web Frameworks & Cloud", "09:00 AM", "10:15 AM", 9, 0, 10, 15, "MCA Department & Amphitheatre", 3, "MCA-101", 0, "Prof. Rajesh Sharma", "Lecture", "#1E88E5");
        insertSchedule(db, "Wednesday", "NET-210", "Cisco Packet Tracer Workshop", "11:00 AM", "01:00 PM", 11, 0, 13, 0, "Advanced Computer & Networking Labs", 4, "LAB-NET-201", 2, "Prof. Anita Desai", "Lab", "#00ACC1");
        insertSchedule(db, "Wednesday", "TECH-500", "Keynote: Next-Gen AI Architectures", "03:00 PM", "04:30 PM", 15, 0, 16, 30, "Main Auditorium & Seminar Complex", 5, "AUD-MAIN-01", 0, "Guest Speaker - Google", "Seminar", "#43A047");

        insertSchedule(db, "Thursday", "MATH-240", "Discrete Mathematics & Graphs", "09:30 AM", "11:00 AM", 9, 30, 11, 0, "MCA Department & Amphitheatre", 3, "MCA-101", 0, "Dr. Katherine Johnson", "Lecture", "#E65100");
        insertSchedule(db, "Thursday", "RES-320", "Library Research & Thesis Workshop", "01:30 PM", "03:30 PM", 13, 30, 15, 30, "Central Library & Media Center", 2, "LIB-READING-01", 1, "Dr. Maya Angelou", "Seminar", "#F4511E");

        insertSchedule(db, "Friday", "MCA-395", "MCA Capstone Project Review", "10:00 AM", "12:30 PM", 10, 0, 12, 30, "Main Auditorium & Seminar Complex", 5, "SEM-AV-02", 1, "Evaluation Committee", "Seminar", "#6D4C41");
        insertSchedule(db, "Friday", "NET-210", "Network Security Sandbox Defense", "02:00 PM", "04:00 PM", 14, 0, 16, 0, "Advanced Computer & Networking Labs", 4, "LAB-CYBER-204", 2, "Prof. Anita Desai", "Lab", "#00897B");

        // --- 4. Points of Interest (POIs) with High-Res Image Assets ---
        insertPoi(db, "Main Campus Security & Entrance", "Entrance", "Campus Grounds", 1, 0, 19.2173, 73.1622, 50, 500, 250, 1, "Main vehicular entry & visitor security kiosk", "24/7 Guards, Visitor Pass, Boom Barriers", R.drawable.img_security_gate);
        insertPoi(db, "Main College Parking & EV Hub", "Parking", "Campus Grounds", 1, 0, 19.2171, 73.1620, 40, 200, 80, 1, "Multi-bay parking with solar canopy and EV charging stations", "200 Car spots, 12 EV Superchargers, 24/7 CCTV", R.drawable.img_campus_parking);
        insertPoi(db, "MCA Department Amphitheatre", "MCA Dept", "MCA Department & Amphitheatre", 3, 0, 19.2185, 73.1627, 45, 300, 165, 1, "Open Amphitheatre & Stage • Saraswati Quad", "Stage lighting, sound setup, event arena", R.drawable.img_mca_amphitheatre);
        insertPoi(db, "Advanced Networking & Cisco Lab", "Computer Lab", "Advanced Computer & Networking Labs", 4, 2, 19.2192, 73.1633, 68, 50, 16, 1, "45 Cisco Network Workstations Active", "Cisco Routers, Gigabit switches, Network Charts", R.drawable.img_networking_lab);
        insertPoi(db, "Cyber Security & Defense Lab", "Computer Lab", "Advanced Computer & Networking Labs", 4, 2, 19.2192, 73.1633, 60, 45, 18, 1, "Cyber defense sandbox & threat analysis center", "SOC monitors, isolated network vLANs", R.drawable.img_cybersecurity_lab);
        insertPoi(db, "Main College Grand Auditorium", "Auditorium", "Main Auditorium & Seminar Complex", 5, 0, 19.2176, 73.1636, 40, 500, 300, 1, "500-Seat Tiered Theatre Seating", "Dolby Sound, Motorized Screen, AC Hall", R.drawable.img_auditorium_hall);
        insertPoi(db, "Smart AV Seminar Presentation Hall", "Seminar", "Main Auditorium & Seminar Complex", 5, 1, 19.2176, 73.1636, 35, 120, 78, 1, "Executive SAP HANA & Presentation Suite", "Dual 4K Projectors, Smart Podium, Mic Setup", R.drawable.img_seminar_hall);
        insertPoi(db, "College Cafeteria & Food Court", "Dining", "College Cafeteria & Student Dining", 6, 0, 19.2178, 73.1625, 78, 250, 55, 1, "High Traffic • Multi-cuisine dining & coffee bar", "Stainless Steel Tables, Patio Dining, Espresso Bar", R.drawable.img_canteen_exterior);
        insertPoi(db, "Central Library Silent Reading Sanctuary", "Study Room", "Central Library & Media Center", 2, 1, 19.2188, 73.1638, 38, 120, 74, 1, "Quiet Zone Active • 74 Study Pods Available", "Tall Bookstacks, Wi-Fi 6, Charging Outlets", R.drawable.img_reading_room);
        insertPoi(db, "Collegiate Sports Arena & Field", "Sports", "Sports & Recreation Arena", 7, 0, 19.2195, 73.1645, 30, 400, 120, 1, "Synthetic running track, football pitch & courts", "Track, Soccer Field, Tennis, Basketball", R.drawable.img_sports_ground);
        insertPoi(db, "Administration Headquarters & Atrium", "Administration", "Administration & Admission Hall", 8, 0, 19.2180, 73.1642, 42, 150, 85, 1, "Principal's Office, Dean & Admission Services", "Grand Atrium, Fountain, Boardroom", R.drawable.img_admin_building);

        // --- Shuttle Stops ---
        insertShuttleStop(db, "Student Union & Canteen Stop", 19.2178, 73.1623, "Campus Circulator (Route A)", 3, "10:15 AM");
        insertShuttleStop(db, "MCA & Academic North Gate", 19.2185, 73.1628, "Campus Circulator (Route A)", 6, "10:18 AM");
        insertShuttleStop(db, "Auditorium & Library Transit Stop", 19.2188, 73.1639, "Campus Circulator (Route A)", 10, "10:22 AM");
        insertShuttleStop(db, "Networking Labs East Stop", 19.2193, 73.1634, "North Campus Express (Route B)", 5, "10:17 AM");
        insertShuttleStop(db, "Sports Complex Transit Island", 19.2196, 73.1646, "North Campus Express (Route B)", 11, "10:23 AM");
    }

    private void insertBuilding(SQLiteDatabase db, int id, String name, String desc, double lat, double lng,
                                String cat, int floors, String hours, int occ, int acc, double wLat, double wLng, String dept, int imageResId) {
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("name", name);
        cv.put("description", desc);
        cv.put("latitude", lat);
        cv.put("longitude", lng);
        cv.put("category", cat);
        cv.put("total_floors", floors);
        cv.put("open_hours", hours);
        cv.put("occupancy_rate", occ);
        cv.put("is_accessible", acc);
        cv.put("wheelchair_lat", wLat);
        cv.put("wheelchair_lng", wLng);
        cv.put("departments", dept);
        cv.put("image_res_id", imageResId);
        db.insertWithOnConflict(TABLE_BUILDINGS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    private void insertIndoorRoom(SQLiteDatabase db, int bId, String bName, String code, String name,
                                  int floor, String type, float x, float y, float w, float h, String desc, int imageResId) {
        ContentValues cv = new ContentValues();
        cv.put("building_id", bId);
        cv.put("building_name", bName);
        cv.put("room_code", code);
        cv.put("name", name);
        cv.put("floor", floor);
        cv.put("room_type", type);
        cv.put("x_pct", x);
        cv.put("y_pct", y);
        cv.put("w_pct", w);
        cv.put("h_pct", h);
        cv.put("description", desc);
        cv.put("image_res_id", imageResId);
        db.insert(TABLE_INDOOR_ROOMS, null, cv);
    }

    private void insertSchedule(SQLiteDatabase db, String day, String code, String name,
                                String start, String end, int sh, int sm, int eh, int em,
                                String bName, int bId, String rCode, int floor, String instr, String cat, String color) {
        ContentValues cv = new ContentValues();
        cv.put("day_of_week", day);
        cv.put("course_code", code);
        cv.put("course_name", name);
        cv.put("start_time", start);
        cv.put("end_time", end);
        cv.put("start_h", sh);
        cv.put("start_m", sm);
        cv.put("end_h", eh);
        cv.put("end_m", em);
        cv.put("building_name", bName);
        cv.put("building_id", bId);
        cv.put("room_code", rCode);
        cv.put("floor", floor);
        cv.put("instructor", instr);
        cv.put("category", cat);
        cv.put("color_hex", color);
        db.insert(TABLE_SCHEDULES, null, cv);
    }

    private void insertPoi(SQLiteDatabase db, String name, String cat, String bName, int bId, int floor,
                           double lat, double lng, int occ, int maxCap, int avail, int isOpen, String status, String feat, int imageResId) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("category", cat);
        cv.put("building_name", bName);
        cv.put("building_id", bId);
        cv.put("floor", floor);
        cv.put("latitude", lat);
        cv.put("longitude", lng);
        cv.put("occupancy_rate", occ);
        cv.put("max_capacity", maxCap);
        cv.put("available_units", avail);
        cv.put("is_open", isOpen);
        cv.put("status_text", status);
        cv.put("features", feat);
        cv.put("image_res_id", imageResId);
        db.insert(TABLE_POIS, null, cv);
    }

    private void insertShuttleStop(SQLiteDatabase db, String name, double lat, double lng, String route, int eta, String nextTime) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("latitude", lat);
        cv.put("longitude", lng);
        cv.put("route_name", route);
        cv.put("eta_minutes", eta);
        cv.put("next_bus_time", nextTime);
        db.insert(TABLE_SHUTTLE_STOPS, null, cv);
    }

    // --- Query Methods ---

    public List<CampusBuilding> getAllBuildings() {
        List<CampusBuilding> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BUILDINGS + " ORDER BY id ASC", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                double lat = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
                double lng = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));
                String cat = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                int floors = cursor.getInt(cursor.getColumnIndexOrThrow("total_floors"));
                String hours = cursor.getString(cursor.getColumnIndexOrThrow("open_hours"));
                int occ = cursor.getInt(cursor.getColumnIndexOrThrow("occupancy_rate"));
                boolean isAcc = cursor.getInt(cursor.getColumnIndexOrThrow("is_accessible")) == 1;
                double wLat = cursor.getDouble(cursor.getColumnIndexOrThrow("wheelchair_lat"));
                double wLng = cursor.getDouble(cursor.getColumnIndexOrThrow("wheelchair_lng"));
                String dept = cursor.getString(cursor.getColumnIndexOrThrow("departments"));
                int imgRes = cursor.getInt(cursor.getColumnIndexOrThrow("image_res_id"));

                list.add(new CampusBuilding(id, name, desc, new LatLng(lat, lng), cat, floors, hours, occ, isAcc, new LatLng(wLat, wLng), dept, imgRes));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<IndoorRoom> getRoomsForBuildingAndFloor(int buildingId, int floor) {
        List<IndoorRoom> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_INDOOR_ROOMS + " WHERE building_id = ? AND floor = ?",
                new String[]{String.valueOf(buildingId), String.valueOf(floor)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String bName = cursor.getString(cursor.getColumnIndexOrThrow("building_name"));
                String code = cursor.getString(cursor.getColumnIndexOrThrow("room_code"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("room_type"));
                float x = cursor.getFloat(cursor.getColumnIndexOrThrow("x_pct"));
                float y = cursor.getFloat(cursor.getColumnIndexOrThrow("y_pct"));
                float w = cursor.getFloat(cursor.getColumnIndexOrThrow("w_pct"));
                float h = cursor.getFloat(cursor.getColumnIndexOrThrow("h_pct"));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                int imgRes = cursor.getInt(cursor.getColumnIndexOrThrow("image_res_id"));

                list.add(new IndoorRoom(id, buildingId, bName, code, name, floor, type, x, y, w, h, desc, imgRes));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<IndoorRoom> searchIndoorRooms(String query) {
        List<IndoorRoom> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String wild = "%" + query + "%";
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_INDOOR_ROOMS + " WHERE room_code LIKE ? OR name LIKE ? OR building_name LIKE ?",
                new String[]{wild, wild, wild});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                int bId = cursor.getInt(cursor.getColumnIndexOrThrow("building_id"));
                String bName = cursor.getString(cursor.getColumnIndexOrThrow("building_name"));
                String code = cursor.getString(cursor.getColumnIndexOrThrow("room_code"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                int floor = cursor.getInt(cursor.getColumnIndexOrThrow("floor"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("room_type"));
                float x = cursor.getFloat(cursor.getColumnIndexOrThrow("x_pct"));
                float y = cursor.getFloat(cursor.getColumnIndexOrThrow("y_pct"));
                float w = cursor.getFloat(cursor.getColumnIndexOrThrow("w_pct"));
                float h = cursor.getFloat(cursor.getColumnIndexOrThrow("h_pct"));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                int imgRes = cursor.getInt(cursor.getColumnIndexOrThrow("image_res_id"));

                list.add(new IndoorRoom(id, bId, bName, code, name, floor, type, x, y, w, h, desc, imgRes));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<ClassScheduleItem> getScheduleForDay(String dayOfWeek) {
        List<ClassScheduleItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SCHEDULES + " WHERE day_of_week = ? ORDER BY start_h ASC, start_m ASC",
                new String[]{dayOfWeek});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String day = cursor.getString(cursor.getColumnIndexOrThrow("day_of_week"));
                String code = cursor.getString(cursor.getColumnIndexOrThrow("course_code"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("course_name"));
                String start = cursor.getString(cursor.getColumnIndexOrThrow("start_time"));
                String end = cursor.getString(cursor.getColumnIndexOrThrow("end_time"));
                int sh = cursor.getInt(cursor.getColumnIndexOrThrow("start_h"));
                int sm = cursor.getInt(cursor.getColumnIndexOrThrow("start_m"));
                int eh = cursor.getInt(cursor.getColumnIndexOrThrow("end_h"));
                int em = cursor.getInt(cursor.getColumnIndexOrThrow("end_m"));
                String bName = cursor.getString(cursor.getColumnIndexOrThrow("building_name"));
                int bId = cursor.getInt(cursor.getColumnIndexOrThrow("building_id"));
                String rCode = cursor.getString(cursor.getColumnIndexOrThrow("room_code"));
                int floor = cursor.getInt(cursor.getColumnIndexOrThrow("floor"));
                String instr = cursor.getString(cursor.getColumnIndexOrThrow("instructor"));
                String cat = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                String color = cursor.getString(cursor.getColumnIndexOrThrow("color_hex"));

                list.add(new ClassScheduleItem(id, day, code, name, start, end, sh, sm, eh, em, bName, bId, rCode, floor, instr, cat, color));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<PointOfInterest> getAllPois(String categoryFilter) {
        List<PointOfInterest> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_POIS;
        String[] args = null;
        if (categoryFilter != null && !categoryFilter.equalsIgnoreCase("All")) {
            query += " WHERE category = ?";
            args = new String[]{categoryFilter};
        }
        query += " ORDER BY id ASC";

        Cursor cursor = db.rawQuery(query, args);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String cat = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                String bName = cursor.getString(cursor.getColumnIndexOrThrow("building_name"));
                int bId = cursor.getInt(cursor.getColumnIndexOrThrow("building_id"));
                int floor = cursor.getInt(cursor.getColumnIndexOrThrow("floor"));
                double lat = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
                double lng = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));
                int occ = cursor.getInt(cursor.getColumnIndexOrThrow("occupancy_rate"));
                int maxCap = cursor.getInt(cursor.getColumnIndexOrThrow("max_capacity"));
                int avail = cursor.getInt(cursor.getColumnIndexOrThrow("available_units"));
                boolean isOpen = cursor.getInt(cursor.getColumnIndexOrThrow("is_open")) == 1;
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status_text"));
                String feat = cursor.getString(cursor.getColumnIndexOrThrow("features"));
                int imgRes = cursor.getInt(cursor.getColumnIndexOrThrow("image_res_id"));

                list.add(new PointOfInterest(id, name, cat, bName, bId, floor, new LatLng(lat, lng), occ, maxCap, avail, isOpen, status, feat, imgRes));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<ShuttleStop> getAllShuttleStops() {
        List<ShuttleStop> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SHUTTLE_STOPS + " ORDER BY id ASC", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                double lat = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
                double lng = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));
                String route = cursor.getString(cursor.getColumnIndexOrThrow("route_name"));
                int eta = cursor.getInt(cursor.getColumnIndexOrThrow("eta_minutes"));
                String nextTime = cursor.getString(cursor.getColumnIndexOrThrow("next_bus_time"));

                list.add(new ShuttleStop(id, name, new LatLng(lat, lng), route, eta, nextTime));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }
}