package com.example.collegecampusnavigation.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collegecampusnavigation.R;
import com.example.collegecampusnavigation.adapter.ShuttleStopAdapter;
import com.example.collegecampusnavigation.helper.DatabaseHelper;
import com.example.collegecampusnavigation.helper.ShuttleSimulator;
import com.example.collegecampusnavigation.model.ShuttleBus;
import com.example.collegecampusnavigation.model.ShuttleStop;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShuttleTrackerActivity extends AppCompatActivity implements OnMapReadyCallback, ShuttleSimulator.ShuttleUpdateListener {

    private GoogleMap mMap;
    private RecyclerView rvShuttleStops;
    private ShuttleStopAdapter stopAdapter;
    private TextView tvActiveBusesStatus;

    private List<ShuttleStop> shuttleStops = new ArrayList<>();
    private final Map<String, Marker> busMarkers = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shuttle_tracker);

        MaterialToolbar toolbar = findViewById(R.id.toolbarShuttle);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvActiveBusesStatus = findViewById(R.id.tvActiveBusesStatus);
        rvShuttleStops = findViewById(R.id.rvShuttleStops);
        rvShuttleStops.setLayoutManager(new LinearLayoutManager(this));

        try (DatabaseHelper db = new DatabaseHelper(this)) {
            shuttleStops = db.getAllShuttleStops();
        }

        stopAdapter = new ShuttleStopAdapter(shuttleStops, stop -> {
            if (mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(stop.getLocation(), 18f));
                Toast.makeText(this, stop.getName() + " - Next Shuttle: " + stop.getEtaMinutes() + " mins", Toast.LENGTH_SHORT).show();
            }
        });
        rvShuttleStops.setAdapter(stopAdapter);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.shuttleMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        ShuttleSimulator.getInstance().startSimulation(shuttleStops);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        // Add Stop Pins
        for (ShuttleStop stop : shuttleStops) {
            mMap.addMarker(new MarkerOptions()
                    .position(stop.getLocation())
                    .title(stop.getName())
                    .snippet("Route: " + stop.getRouteName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        }

        // Draw Route Polylines
        List<ShuttleBus> buses = ShuttleSimulator.getInstance().getActiveBuses();
        for (ShuttleBus bus : buses) {
            PolylineOptions poly = new PolylineOptions()
                    .addAll(bus.getPathWaypoints())
                    .color(bus.getRouteColor())
                    .width(10f);
            mMap.addPolyline(poly);
        }

        if (!shuttleStops.isEmpty()) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(shuttleStops.get(0).getLocation(), 16.5f));
        }

        ShuttleSimulator.getInstance().addListener(this);
    }

    @Override
    public void onShuttlesUpdated(List<ShuttleBus> buses, List<ShuttleStop> stops) {
        runOnUiThread(() -> {
            if (stopAdapter != null) {
                stopAdapter.updateList(new ArrayList<>(stops));
            }

            if (mMap != null) {
                for (ShuttleBus bus : buses) {
                    Marker marker = busMarkers.get(bus.getBusId());
                    if (marker == null) {
                        marker = mMap.addMarker(new MarkerOptions()
                                .position(bus.getCurrentLocation())
                                .title(bus.getBusId() + " (" + bus.getRouteName() + ")")
                                .snippet("Next: " + bus.getNextStopName() + " (" + bus.getEtaNextStopMin() + " min)")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        if (marker != null) {
                            busMarkers.put(bus.getBusId(), marker);
                        }
                    } else {
                        marker.setPosition(bus.getCurrentLocation());
                        marker.setSnippet("Next: " + bus.getNextStopName() + " (" + bus.getEtaNextStopMin() + " min)");
                    }
                }
            }

            if (tvActiveBusesStatus != null) {
                tvActiveBusesStatus.setText("🟢 " + buses.size() + " Shuttles Active • Real-Time GPS Tracking");
            }
        });
    }

    @Override
    protected void onDestroy() {
        ShuttleSimulator.getInstance().removeListener(this);
        super.onDestroy();
    }
}
