package com.example.collegecampusnavigation.helper;

import com.example.collegecampusnavigation.model.CampusRoute;
import com.example.collegecampusnavigation.model.NavigationStep;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class CampusRoutingEngine {

    public static class Node {
        public int id;
        public LatLng location;
        public String name;
        public boolean hasStairs;
        public boolean hasRamp;
        public boolean isConstructionZone;

        public Node(int id, double lat, double lng, String name, boolean hasStairs, boolean hasRamp, boolean isConstructionZone) {
            this.id = id;
            this.location = new LatLng(lat, lng);
            this.name = name;
            this.hasStairs = hasStairs;
            this.hasRamp = hasRamp;
            this.isConstructionZone = isConstructionZone;
        }
    }

    public static class Edge {
        public Node target;
        public double distanceMeters;
        public boolean isStaircase;
        public boolean isBikeFriendly;
        public boolean isAccessible;
        public boolean isConstruction;

        public Edge(Node target, double distanceMeters, boolean isStaircase, boolean isBikeFriendly, boolean isAccessible, boolean isConstruction) {
            this.target = target;
            this.distanceMeters = distanceMeters;
            this.isStaircase = isStaircase;
            this.isBikeFriendly = isBikeFriendly;
            this.isAccessible = isAccessible;
            this.isConstruction = isConstruction;
        }
    }

    private static final List<Node> campusNodes = new ArrayList<>();
    private static final Map<Integer, List<Edge>> adjacencyList = new HashMap<>();

    static {
        initializeCampusNetwork();
    }

    private static void initializeCampusNetwork() {
        // Node Network centered around campus (19.2165 to 19.2200, 73.1615 to 73.1650)
        // Main Waypoint Nodes matching college facilities
        Node n0 = new Node(0, 19.2178, 73.1625, "College Cafeteria & Student Dining", false, true, false);
        Node n1 = new Node(1, 19.2183, 73.1631, "Main Academic Quadrangle", false, true, false);
        Node n2 = new Node(2, 19.2188, 73.1638, "Central Library & Media Center", false, true, false);
        Node n3 = new Node(3, 19.2185, 73.1627, "MCA Department & Amphitheatre", false, true, false);
        Node n4 = new Node(4, 19.2192, 73.1633, "Advanced Computer & Networking Labs", false, true, false);
        Node n5 = new Node(5, 19.2176, 73.1636, "Main Auditorium & Seminar Complex", false, true, false);
        Node n6 = new Node(6, 19.2195, 73.1645, "Sports & Recreation Arena", false, true, false);
        Node n7 = new Node(7, 19.2180, 73.1642, "Administration & Admission Hall", false, true, false);

        // Intermediate Walkway, Stair, and Ramp Intersection Nodes
        Node n8 = new Node(8, 19.2181, 73.1628, "Central Quad Connector", false, true, false);
        Node n9 = new Node(9, 19.2186, 73.1634, "Library-Academic Connector", false, true, false);
        Node n10 = new Node(10, 19.2190, 73.1630, "North Walkway (Staircase Section)", true, false, false);
        Node n11 = new Node(11, 19.2190, 73.1626, "West Accessible Ramp Detour", false, true, false);
        Node n12 = new Node(12, 19.2185, 73.1640, "East Tree Promenade (Paving Construction)", false, false, true);
        Node n13 = new Node(13, 19.2175, 73.1622, "South Dining Pathway", false, true, false);

        campusNodes.add(n0); campusNodes.add(n1); campusNodes.add(n2); campusNodes.add(n3);
        campusNodes.add(n4); campusNodes.add(n5); campusNodes.add(n6); campusNodes.add(n7);
        campusNodes.add(n8); campusNodes.add(n9); campusNodes.add(n10); campusNodes.add(n11);
        campusNodes.add(n12); campusNodes.add(n13);

        for (Node n : campusNodes) {
            adjacencyList.put(n.id, new ArrayList<>());
        }

        // Add Bi-directional Pathway Edges
        addEdgeBi(n0, n8, false, true, true, false);
        addEdgeBi(n8, n1, false, true, true, false);
        addEdgeBi(n8, n3, false, true, true, false); // Direct path to MCA Dept
        addEdgeBi(n3, n11, false, true, true, false); // MCA to West Ramp
        addEdgeBi(n1, n9, false, true, true, false);
        addEdgeBi(n9, n2, false, true, true, false);
        addEdgeBi(n9, n4, false, true, true, false); // Academic to Networking Labs
        addEdgeBi(n4, n10, true, false, false, false); // Labs via stairs
        addEdgeBi(n4, n11, false, true, true, false); // Labs via accessible ramp

        // Library (2) to Sports (6)
        addEdgeBi(n2, n6, false, true, true, false);
        addEdgeBi(n7, n12, false, true, true, true);   // Construction Zone
        addEdgeBi(n12, n2, false, true, true, true);   // Construction Zone

        // Admin (7) & Auditorium (5) Connectors
        addEdgeBi(n1, n7, false, true, true, false);
        addEdgeBi(n7, n5, false, true, true, false);
        addEdgeBi(n5, n0, false, true, true, false);
        addEdgeBi(n1, n5, false, true, true, false);

        // Dining (0) Connectors
        addEdgeBi(n13, n0, false, true, true, false);
        addEdgeBi(n13, n3, false, true, true, false);
    }

    private static void addEdgeBi(Node a, Node b, boolean stairs, boolean bike, boolean accessible, boolean construction) {
        double dist = calculateDistanceMeters(a.location, b.location);
        adjacencyList.get(a.id).add(new Edge(b, dist, stairs, bike, accessible, construction));
        adjacencyList.get(b.id).add(new Edge(a, dist, stairs, bike, accessible, construction));
    }

    public static CampusRoute calculateRoute(LatLng startLoc, LatLng endLoc, String startName, String endName, CampusRoute.Mode mode) {
        Node startNode = findClosestNode(startLoc, mode == CampusRoute.Mode.ACCESSIBLE);
        Node endNode = findClosestNode(endLoc, mode == CampusRoute.Mode.ACCESSIBLE);

        if (startNode == null || endNode == null || startNode.id == endNode.id) {
            // Direct route
            List<LatLng> points = new ArrayList<>();
            points.add(startLoc);
            points.add(endLoc);
            double dist = calculateDistanceMeters(startLoc, endLoc);
            int speed = getSpeedForMode(mode);
            int duration = (int) Math.max(30, dist / speed);

            List<NavigationStep> steps = new ArrayList<>();
            steps.add(new NavigationStep("Depart towards " + endName, (int) dist, NavigationStep.Maneuver.DEPART, startLoc, "Follow marked pathway"));
            steps.add(new NavigationStep("Arrive at " + endName, 0, NavigationStep.Maneuver.ARRIVE, endLoc, "You have reached your destination"));

            return new CampusRoute(startName, endName, points, (int) dist, duration, mode, steps, false, mode == CampusRoute.Mode.ACCESSIBLE);
        }

        // Dijkstra algorithm
        Map<Integer, Double> distances = new HashMap<>();
        Map<Integer, Node> previousNodes = new HashMap<>();
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>((a, b) -> Double.compare(a.distance, b.distance));

        for (Node n : campusNodes) {
            distances.put(n.id, Double.MAX_VALUE);
        }
        distances.put(startNode.id, 0.0);
        pq.add(new NodeDistance(startNode, 0.0));

        Set<Integer> visited = new HashSet<>();

        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            Node u = current.node;
            if (visited.contains(u.id)) continue;
            visited.add(u.id);

            if (u.id == endNode.id) break;

            List<Edge> edges = adjacencyList.get(u.id);
            if (edges != null) {
                for (Edge edge : edges) {
                    // Check Mode Constraints
                    if (mode == CampusRoute.Mode.ACCESSIBLE) {
                        if (edge.isStaircase || edge.isConstruction || !edge.isAccessible) {
                            continue; // Skip inaccessible edges
                        }
                    } else if (mode == CampusRoute.Mode.CYCLING) {
                        if (edge.isStaircase) {
                            continue; // Skip stairs for bikes
                        }
                    }

                    double newDist = distances.get(u.id) + edge.distanceMeters;
                    if (newDist < distances.get(edge.target.id)) {
                        distances.put(edge.target.id, newDist);
                        previousNodes.put(edge.target.id, u);
                        pq.add(new NodeDistance(edge.target, newDist));
                    }
                }
            }
        }

        // Reconstruct path
        List<Node> pathNodes = new ArrayList<>();
        Node step = endNode;
        while (step != null) {
            pathNodes.add(step);
            step = previousNodes.get(step.id);
        }
        Collections.reverse(pathNodes);

        List<LatLng> polyline = new ArrayList<>();
        polyline.add(startLoc);
        for (Node n : pathNodes) {
            polyline.add(n.location);
        }
        polyline.add(endLoc);

        double totalDist = 0;
        for (int i = 0; i < polyline.size() - 1; i++) {
            totalDist += calculateDistanceMeters(polyline.get(i), polyline.get(i + 1));
        }

        int speed = getSpeedForMode(mode);
        int durationSec = (int) Math.max(45, totalDist / speed);

        List<NavigationStep> steps = generateSteps(polyline, pathNodes, startName, endName, mode);

        boolean avoidedConstruction = (mode == CampusRoute.Mode.ACCESSIBLE);
        boolean isAcc = (mode == CampusRoute.Mode.ACCESSIBLE);

        return new CampusRoute(startName, endName, polyline, (int) totalDist, durationSec, mode, steps, avoidedConstruction, isAcc);
    }

    private static List<NavigationStep> generateSteps(List<LatLng> polyline, List<Node> nodes, String startName, String endName, CampusRoute.Mode mode) {
        List<NavigationStep> steps = new ArrayList<>();
        if (polyline.isEmpty()) return steps;

        steps.add(new NavigationStep("Head towards " + (nodes.size() > 0 ? nodes.get(0).name : endName),
                (int) calculateDistanceMeters(polyline.get(0), polyline.get(1)),
                NavigationStep.Maneuver.DEPART, polyline.get(0),
                mode == CampusRoute.Mode.ACCESSIBLE ? "Accessible ramp pathway selected" : "Follow campus pathway"));

        for (int i = 0; i < nodes.size() - 1; i++) {
            Node curr = nodes.get(i);
            Node next = nodes.get(i + 1);
            int stepDist = (int) calculateDistanceMeters(curr.location, next.location);

            NavigationStep.Maneuver maneuver = NavigationStep.Maneuver.STRAIGHT;
            String hint = "Continue along paved track";

            if (next.hasRamp && mode == CampusRoute.Mode.ACCESSIBLE) {
                maneuver = NavigationStep.Maneuver.RAMP;
                hint = "Take smooth ADA compliant ramp";
            } else if (i % 2 == 1) {
                maneuver = NavigationStep.Maneuver.TURN_RIGHT;
                hint = "Turn right past " + curr.name;
            } else {
                maneuver = NavigationStep.Maneuver.TURN_LEFT;
                hint = "Turn left towards " + next.name;
            }

            steps.add(new NavigationStep("Continue towards " + next.name, stepDist, maneuver, curr.location, hint));
        }

        steps.add(new NavigationStep("Arrive at " + endName, 0, NavigationStep.Maneuver.ARRIVE, polyline.get(polyline.size() - 1),
                "Destination is on your side"));

        return steps;
    }

    private static int getSpeedForMode(CampusRoute.Mode mode) {
        switch (mode) {
            case CYCLING: return 4; // ~4.5 m/s (16 km/h)
            case SHUTTLE: return 8; // ~8 m/s (28 km/h)
            case ACCESSIBLE: return 1; // ~1.1 m/s (4 km/h)
            case WALK:
            default:
                return 1; // ~1.3 m/s (5 km/h)
        }
    }

    private static Node findClosestNode(LatLng loc, boolean requireAccessible) {
        Node closest = null;
        double minDist = Double.MAX_VALUE;
        for (Node n : campusNodes) {
            if (requireAccessible && (n.hasStairs || n.isConstructionZone)) {
                continue;
            }
            double dist = calculateDistanceMeters(loc, n.location);
            if (dist < minDist) {
                minDist = dist;
                closest = n;
            }
        }
        return closest;
    }

    public static double calculateDistanceMeters(LatLng p1, LatLng p2) {
        if (p1 == null || p2 == null) return 0;
        double earthRadius = 6371000; // meters
        double dLat = Math.toRadians(p2.latitude - p1.latitude);
        double dLng = Math.toRadians(p2.longitude - p1.longitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(p1.latitude)) * Math.cos(Math.toRadians(p2.latitude)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

    private static class NodeDistance {
        Node node;
        double distance;

        public NodeDistance(Node node, double distance) {
            this.node = node;
            this.distance = distance;
        }
    }
}
