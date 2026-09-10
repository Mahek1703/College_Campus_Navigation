package com.example.collegecampusnavigation.helper;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;

import com.example.collegecampusnavigation.model.ShuttleBus;
import com.example.collegecampusnavigation.model.ShuttleStop;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class ShuttleSimulator {

    public interface ShuttleUpdateListener {
        void onShuttlesUpdated(List<ShuttleBus> buses, List<ShuttleStop> stops);
    }

    private static ShuttleSimulator instance;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isRunning = false;
    private final List<ShuttleUpdateListener> listeners = new ArrayList<>();

    private final List<ShuttleBus> activeBuses = new ArrayList<>();
    private final List<ShuttleStop> activeStops = new ArrayList<>();

    private ShuttleSimulator() {
        initializeShuttles();
    }

    public static synchronized ShuttleSimulator getInstance() {
        if (instance == null) {
            instance = new ShuttleSimulator();
        }
        return instance;
    }

    private void initializeShuttles() {
        // Route A: Campus Circulator (Loop around quad)
        List<LatLng> routeAPath = new ArrayList<>();
        routeAPath.add(new LatLng(19.2178, 73.1623)); // Student Union Stop
        routeAPath.add(new LatLng(19.2181, 73.1628));
        routeAPath.add(new LatLng(19.2185, 73.1630)); // North Gate Stop
        routeAPath.add(new LatLng(19.2188, 73.1638));
        routeAPath.add(new LatLng(19.2190, 73.1642)); // Sports Stop
        routeAPath.add(new LatLng(19.2180, 73.1642));
        routeAPath.add(new LatLng(19.2172, 73.1635));
        routeAPath.add(new LatLng(19.2168, 73.1620)); // Living Quad Stop

        // Route B: North Express
        List<LatLng> routeBPath = new ArrayList<>();
        routeBPath.add(new LatLng(19.2167, 73.1618)); // Dorm Stop
        routeBPath.add(new LatLng(19.2175, 73.1622));
        routeBPath.add(new LatLng(19.2183, 73.1631));
        routeBPath.add(new LatLng(19.2195, 73.1628)); // Science Stop
        routeBPath.add(new LatLng(19.2196, 73.1627));

        ShuttleBus busA = new ShuttleBus("BUS-101", "Campus Circulator (Route A)", routeAPath.get(0), "Main Academic North Gate", 3, Color.parseColor("#FB8C00"), routeAPath);
        ShuttleBus busB = new ShuttleBus("BUS-202", "North Campus Express (Route B)", routeBPath.get(0), "Science Complex Transit Island", 5, Color.parseColor("#0288D1"), routeBPath);

        activeBuses.add(busA);
        activeBuses.add(busB);
    }

    public void startSimulation(List<ShuttleStop> stops) {
        if (stops != null && !stops.isEmpty()) {
            activeStops.clear();
            activeStops.addAll(stops);
        }
        if (isRunning) return;
        isRunning = true;
        handler.post(simulationRunnable);
    }

    public void stopSimulation() {
        isRunning = false;
        handler.removeCallbacks(simulationRunnable);
    }

    public void addListener(ShuttleUpdateListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            listener.onShuttlesUpdated(activeBuses, activeStops);
        }
    }

    public void removeListener(ShuttleUpdateListener listener) {
        listeners.remove(listener);
    }

    private final Runnable simulationRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isRunning) return;

            // Move each bus along its waypoint path
            for (ShuttleBus bus : activeBuses) {
                List<LatLng> path = bus.getPathWaypoints();
                if (path.isEmpty()) continue;

                int nextIdx = (bus.getCurrentWaypointIndex() + 1) % path.size();
                bus.setCurrentWaypointIndex(nextIdx);
                bus.setCurrentLocation(path.get(nextIdx));

                // Update countdown ETA
                int newEta = Math.max(1, (bus.getEtaNextStopMin() - 1 + 6) % 6);
                bus.setEtaNextStopMin(newEta == 0 ? 1 : newEta);
            }

            // Update stops countdowns dynamically
            for (ShuttleStop stop : activeStops) {
                int eta = stop.getEtaMinutes() - 1;
                if (eta <= 0) eta = 8 + (int)(Math.random() * 4);
                stop.setEtaMinutes(eta);
            }

            for (ShuttleUpdateListener l : listeners) {
                l.onShuttlesUpdated(activeBuses, activeStops);
            }

            handler.postDelayed(this, 3500); // Update every 3.5 seconds
        }
    };

    public List<ShuttleBus> getActiveBuses() { return activeBuses; }
    public List<ShuttleStop> getActiveStops() { return activeStops; }
}
