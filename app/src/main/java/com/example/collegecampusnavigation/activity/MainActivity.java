package com.example.collegecampusnavigation.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collegecampusnavigation.R;
import com.example.collegecampusnavigation.adapter.NavigationStepAdapter;
import com.example.collegecampusnavigation.helper.CampusRoutingEngine;
import com.example.collegecampusnavigation.helper.DatabaseHelper;
import com.example.collegecampusnavigation.model.CampusBuilding;
import com.example.collegecampusnavigation.model.CampusRoute;
import com.example.collegecampusnavigation.model.NavigationStep;
import com.example.collegecampusnavigation.model.PointOfInterest;
import com.example.collegecampusnavigation.view.CampusDashboardMapView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final LatLng CAMPUS_CENTER = new LatLng(19.2185, 73.1633);

    private GoogleMap mMap;
    private CampusDashboardMapView campusDashboardMap;
    private View mapFragmentContainer;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private EditText etSearchInput;
    private ImageView btnClearSearch, ivBuildingCategoryIcon;
    private TextView tvBuildingName, tvBuildingDesc, tvLiveOccupancyBadge, tvRouteSummary;
    private View cardActiveNavHud;
    private TextView tvActiveNavInstruction, tvActiveNavDetails;
    private ImageView ivActiveNavIcon;
    private TabLayout tabLayoutRouteModes;
    private ChipGroup chipGroupCategories;
    private BottomNavigationView bottomNavigation;
    private RecyclerView rvTurnSteps;
    private NavigationStepAdapter stepAdapter;
    private TextToSpeech textToSpeech;
    private FusedLocationProviderClient fusedLocationClient;

    private List<CampusBuilding> campusBuildings = new ArrayList<>();
    private List<PointOfInterest> campusPois = new ArrayList<>();
    private final Map<Marker, CampusBuilding> markerBuildingMap = new HashMap<>();
    private CampusBuilding selectedBuilding;
    private LatLng userLocation = new LatLng(19.2173, 73.1622); // Main Entrance Gate default
    private CampusRoute currentRoute;
    private Polyline routePolyline;
    private CampusRoute.Mode currentMode = CampusRoute.Mode.WALK;
    private boolean is3DMode = false;
    private int currentMapType = GoogleMap.MAP_TYPE_NORMAL;
    private int activeMapLayerIndex = 0; // 0: Vector Campus Canvas, 1: Satellite, 2: Normal, 3: Terrain
    private int currentNavStepIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI View Binding
        campusDashboardMap = findViewById(R.id.campusDashboardMap);
        mapFragmentContainer = findViewById(R.id.map);
        etSearchInput = findViewById(R.id.etSearchInput);
        btnClearSearch = findViewById(R.id.btnClearSearch);
        ivBuildingCategoryIcon = findViewById(R.id.ivBuildingCategoryIcon);
        tvBuildingName = findViewById(R.id.tvBuildingName);
        tvBuildingDesc = findViewById(R.id.tvBuildingDesc);
        tvLiveOccupancyBadge = findViewById(R.id.tvLiveOccupancyBadge);
        tvRouteSummary = findViewById(R.id.tvRouteSummary);
        chipGroupCategories = findViewById(R.id.chipGroupCategories);
        tabLayoutRouteModes = findViewById(R.id.tabLayoutRouteModes);
        cardActiveNavHud = findViewById(R.id.cardActiveNavHud);
        tvActiveNavInstruction = findViewById(R.id.tvActiveNavInstruction);
        tvActiveNavDetails = findViewById(R.id.tvActiveNavDetails);
        ivActiveNavIcon = findViewById(R.id.ivActiveNavIcon);
        rvTurnSteps = findViewById(R.id.rvTurnSteps);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        Button btnNavigate = findViewById(R.id.btnNavigate);
        Button btnViewIndoorFloor = findViewById(R.id.btnViewIndoorFloor);
        Button btnVisualStreetNav = findViewById(R.id.btnVisualStreetNav);
        View btnCloseNav = findViewById(R.id.btnCloseNav);
        View btnVisualTourTop = findViewById(R.id.btnVisualTourTop);
        FloatingActionButton fabToggle3D = findViewById(R.id.fabToggle3D);
        FloatingActionButton fabLayers = findViewById(R.id.fabLayers);
        FloatingActionButton fabMyLocation = findViewById(R.id.fabMyLocation);

        // Setup Interactive Campus Map View Click Listener
        if (campusDashboardMap != null) {
            campusDashboardMap.setOnBuildingClickListener(new CampusDashboardMapView.OnBuildingClickListener() {
                @Override
                public void onBuildingClicked(CampusBuilding building) {
                    selectBuilding(building);
                }

                @Override
                public void onMapBackgroundClicked() {
                    if (cardActiveNavHud.getVisibility() != View.VISIBLE) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }
            });
        }

        // Bottom Sheet setup
        View bottomSheet = findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        rvTurnSteps.setLayoutManager(new LinearLayoutManager(this));

        // TTS setup
        try {
            textToSpeech = new TextToSpeech(this, status -> {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.US);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize Map Fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Load Campus Data
        loadCampusData();

        // 1. Search Bar Functionality
        etSearchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                filterCampusBuildings(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnClearSearch.setOnClickListener(v -> {
            etSearchInput.setText("");
            filterCampusBuildings("");
        });

        etSearchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearchJump(etSearchInput.getText().toString().trim());
                return true;
            }
            return false;
        });

        btnVisualTourTop.setOnClickListener(v -> {
            Intent intent = new Intent(this, VisualNavigatorActivity.class);
            startActivity(intent);
        });

        // 2. Filter Chips
        chipGroupCategories.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chipAll) filterBuildingsByCategory(null);
            else if (checkedId == R.id.chipAcademic) filterBuildingsByCategory("Campus");
            else if (checkedId == R.id.chipLabs) filterBuildingsByCategory("Labs");
            else if (checkedId == R.id.chipLibrary) filterBuildingsByCategory("Library");
            else if (checkedId == R.id.chipDining) filterBuildingsByCategory("Canteen");
            else if (checkedId == R.id.chipAuditorium) filterBuildingsByCategory("Auditorium");
            else if (checkedId == R.id.chipAdmin) filterBuildingsByCategory("Administration");
        });

        // 3. Floating Map Controls
        fabToggle3D.setOnClickListener(v -> toggle3DCameraView());
        fabLayers.setOnClickListener(v -> cycleMapLayers());
        fabMyLocation.setOnClickListener(v -> centerOnUserLocation());

        // 4. Action Buttons
        btnNavigate.setOnClickListener(v -> startTurnByTurnNavigation());
        btnViewIndoorFloor.setOnClickListener(v -> openIndoorTourForSelectedBuilding());
        btnVisualStreetNav.setOnClickListener(v -> openVisualNavigatorForSelectedBuilding());
        btnCloseNav.setOnClickListener(v -> stopTurnByTurnNavigation());

        // 5. Travel Mode Selector Tabs
        tabLayoutRouteModes.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: currentMode = CampusRoute.Mode.WALK; break;
                    case 1: currentMode = CampusRoute.Mode.ACCESSIBLE; break;
                    case 2: currentMode = CampusRoute.Mode.CYCLING; break;
                    case 3: currentMode = CampusRoute.Mode.SHUTTLE; break;
                }
                if (selectedBuilding != null) {
                    calculateAndRenderRoute(selectedBuilding);
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // 6. Bottom Navigation Bar Tabs
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_map) {
                if (mMap != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(CAMPUS_CENTER, 17.5f));
                }
                return true;
            } else if (id == R.id.nav_indoor) {
                openIndoorTourForSelectedBuilding();
                return true;
            } else if (id == R.id.nav_shuttle) {
                startActivity(new Intent(this, ShuttleTrackerActivity.class));
                return true;
            } else if (id == R.id.nav_schedule) {
                startActivity(new Intent(this, ClassScheduleActivity.class));
                return true;
            } else if (id == R.id.nav_sos) {
                startActivity(new Intent(this, EmergencySosActivity.class));
                return true;
            }
            return false;
        });

        // Initialize location services safely
        initLocationSafely();
    }

    private void loadCampusData() {
        try (DatabaseHelper db = new DatabaseHelper(this)) {
            campusBuildings = db.getAllBuildings();
            campusPois = db.getAllPois(null);
            if (campusDashboardMap != null) {
                campusDashboardMap.setBuildings(campusBuildings);
            }
            if (!campusBuildings.isEmpty()) {
                selectBuilding(campusBuildings.get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.mMap = googleMap;

        try {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(false);

            // Center Camera on Campus with gentle 3D tilt
            CameraPosition pos = new CameraPosition.Builder()
                    .target(CAMPUS_CENTER)
                    .zoom(17.8f)
                    .bearing(20f)
                    .tilt(35f)
                    .build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));

            // Draw Campus Zones & Walkway Polygons
            drawCampusOverlays();

            // Populate Map Markers
            renderBuildingMarkers(campusBuildings);

            // Setup Marker Click Listener
            mMap.setOnMarkerClickListener(marker -> {
                CampusBuilding b = markerBuildingMap.get(marker);
                if (b != null) {
                    selectBuilding(b);
                    return true;
                }
                return false;
            });

            mMap.setOnMapClickListener(latLng -> {
                if (cardActiveNavHud.getVisibility() != View.VISIBLE) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            });

            // Auto-select first building
            if (!campusBuildings.isEmpty()) {
                selectBuilding(campusBuildings.get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renderBuildingMarkers(List<CampusBuilding> buildings) {
        if (mMap == null) return;
        mMap.clear();
        markerBuildingMap.clear();
        drawCampusOverlays();

        // Add user start location marker
        mMap.addMarker(new MarkerOptions()
                .position(userLocation)
                .title("Your Current Location")
                .snippet("Tech Campus Main Gate")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        for (CampusBuilding b : buildings) {
            float hue = BitmapDescriptorFactory.HUE_RED;
            if ("Labs".equalsIgnoreCase(b.getCategory())) hue = BitmapDescriptorFactory.HUE_CYAN;
            else if ("Library".equalsIgnoreCase(b.getCategory())) hue = BitmapDescriptorFactory.HUE_VIOLET;
            else if ("Canteen".equalsIgnoreCase(b.getCategory())) hue = BitmapDescriptorFactory.HUE_ORANGE;
            else if ("Auditorium".equalsIgnoreCase(b.getCategory())) hue = BitmapDescriptorFactory.HUE_ROSE;
            else if ("Sports".equalsIgnoreCase(b.getCategory())) hue = BitmapDescriptorFactory.HUE_GREEN;
            else if ("Administration".equalsIgnoreCase(b.getCategory())) hue = BitmapDescriptorFactory.HUE_YELLOW;

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(b.getLocation())
                    .title(b.getName())
                    .snippet(b.getTotalFloors() + " Floors • " + b.getCategory())
                    .icon(BitmapDescriptorFactory.defaultMarker(hue)));

            if (marker != null) {
                markerBuildingMap.put(marker, b);
            }
        }
    }

    private void drawCampusOverlays() {
        if (mMap == null) return;

        // Campus boundary
        mMap.addPolygon(new PolygonOptions()
                .add(
                        new LatLng(19.2205, 73.1615),
                        new LatLng(19.2205, 73.1655),
                        new LatLng(19.2165, 73.1655),
                        new LatLng(19.2165, 73.1615)
                )
                .strokeColor(Color.parseColor("#4D1E88E5"))
                .strokeWidth(3f)
                .fillColor(Color.parseColor("#0D1E88E5")));

        // Main Academic Quadrangle Walkway
        mMap.addPolygon(new PolygonOptions()
                .add(
                        new LatLng(19.2189, 73.1625),
                        new LatLng(19.2189, 73.1642),
                        new LatLng(19.2178, 73.1642),
                        new LatLng(19.2178, 73.1625)
                )
                .strokeColor(Color.parseColor("#4D00E676"))
                .strokeWidth(2f)
                .fillColor(Color.parseColor("#1A00E676")));
    }

    public void selectBuilding(CampusBuilding b) {
        this.selectedBuilding = b;

        tvBuildingName.setText(b.getName());
        tvBuildingDesc.setText(b.getTotalFloors() + " Floors • " + b.getDescription());
        tvLiveOccupancyBadge.setText("● " + b.getOccupancyRate() + "% Traffic");

        // Set Category Vector Icon
        if ("Labs".equalsIgnoreCase(b.getCategory())) {
            ivBuildingCategoryIcon.setImageResource(R.drawable.ic_building_lab);
        } else if ("Library".equalsIgnoreCase(b.getCategory())) {
            ivBuildingCategoryIcon.setImageResource(R.drawable.ic_building_library);
        } else if ("Canteen".equalsIgnoreCase(b.getCategory())) {
            ivBuildingCategoryIcon.setImageResource(R.drawable.ic_building_canteen);
        } else if ("Auditorium".equalsIgnoreCase(b.getCategory())) {
            ivBuildingCategoryIcon.setImageResource(R.drawable.ic_building_auditorium);
        } else if ("Sports".equalsIgnoreCase(b.getCategory())) {
            ivBuildingCategoryIcon.setImageResource(R.drawable.ic_building_sports);
        } else if ("Administration".equalsIgnoreCase(b.getCategory())) {
            ivBuildingCategoryIcon.setImageResource(R.drawable.ic_building_admin);
        } else {
            ivBuildingCategoryIcon.setImageResource(R.drawable.ic_building_academic);
        }

        // Update Vector Campus Map
        if (campusDashboardMap != null) {
            campusDashboardMap.setSelectedBuilding(b);
        }

        // Calculate and display route summary
        calculateAndRenderRoute(b);

        // Move camera if Google Map is active
        if (mMap != null) {
            CameraPosition pos = new CameraPosition.Builder()
                    .target(b.getLocation())
                    .zoom(18.2f)
                    .tilt(is3DMode ? 55f : 20f)
                    .bearing(15f)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos));
        }

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void calculateAndRenderRoute(CampusBuilding b) {
        currentRoute = CampusRoutingEngine.calculateRoute(userLocation, b.getLocation(), "Campus Entrance", b.getName(), currentMode);

        if (currentRoute != null) {
            String accNote = currentRoute.isAccessiblePath() ? "Wheelchair Accessible" : "Standard Route";
            tvRouteSummary.setText("📍 " + currentRoute.getFormattedDistance() + " • ~" +
                    currentRoute.getFormattedDuration() + " • " + accNote);

            stepAdapter = new NavigationStepAdapter(currentRoute.getSteps());
            rvTurnSteps.setAdapter(stepAdapter);

            if (campusDashboardMap != null) {
                campusDashboardMap.setCurrentRoute(currentRoute);
            }

            if (mMap != null) {
                if (routePolyline != null) {
                    routePolyline.remove();
                }

                int color = Color.parseColor("#1E88E5");
                if (currentMode == CampusRoute.Mode.ACCESSIBLE) color = Color.parseColor("#00E676");
                else if (currentMode == CampusRoute.Mode.CYCLING) color = Color.parseColor("#AB47BC");
                else if (currentMode == CampusRoute.Mode.SHUTTLE) color = Color.parseColor("#FF9800");

                routePolyline = mMap.addPolyline(new PolylineOptions()
                        .addAll(currentRoute.getPolylinePoints())
                        .width(10f)
                        .color(color)
                        .geodesic(true));
            }
        }
    }

    private void startTurnByTurnNavigation() {
        if (currentRoute == null || currentRoute.getSteps().isEmpty()) {
            Toast.makeText(this, "Select a destination first", Toast.LENGTH_SHORT).show();
            return;
        }

        cardActiveNavHud.setVisibility(View.VISIBLE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        currentNavStepIndex = 0;
        showNavStep(currentNavStepIndex);

        speakOut("Starting navigation to " + (selectedBuilding != null ? selectedBuilding.getName() : "destination") +
                ". " + currentRoute.getSteps().get(0).getInstruction());

        // Focus map on route start
        if (campusDashboardMap != null) {
            campusDashboardMap.centerOnLocation(userLocation);
        }
        if (mMap != null && !currentRoute.getPolylinePoints().isEmpty()) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentRoute.getPolylinePoints().get(0), 19.5f));
        }
    }

    private void showNavStep(int index) {
        if (currentRoute == null || index >= currentRoute.getSteps().size()) return;
        NavigationStep step = currentRoute.getSteps().get(index);

        tvActiveNavInstruction.setText(step.getInstruction());
        tvActiveNavDetails.setText("Step " + (index + 1) + " of " + currentRoute.getSteps().size() + " • In " + step.getDistanceMeters() + "m");

        if (step.getManeuver() == NavigationStep.Maneuver.TURN_LEFT || step.getManeuver() == NavigationStep.Maneuver.SLIGHT_LEFT) {
            ivActiveNavIcon.setImageResource(R.drawable.ic_arrow_back);
            ivActiveNavIcon.setRotation(0f);
        } else if (step.getManeuver() == NavigationStep.Maneuver.TURN_RIGHT || step.getManeuver() == NavigationStep.Maneuver.SLIGHT_RIGHT) {
            ivActiveNavIcon.setImageResource(R.drawable.ic_chevron_right);
            ivActiveNavIcon.setRotation(0f);
        } else if (step.getManeuver() == NavigationStep.Maneuver.ARRIVE) {
            ivActiveNavIcon.setImageResource(R.drawable.ic_location);
            ivActiveNavIcon.setRotation(0f);
        } else {
            ivActiveNavIcon.setImageResource(R.drawable.ic_walk);
            ivActiveNavIcon.setRotation(0f);
        }
    }

    private void stopTurnByTurnNavigation() {
        cardActiveNavHud.setVisibility(View.GONE);
        if (campusDashboardMap != null) {
            campusDashboardMap.setCurrentRoute(null);
        }
        if (routePolyline != null) {
            routePolyline.remove();
            routePolyline = null;
        }
        speakOut("Navigation ended");
    }

    private void openIndoorTourForSelectedBuilding() {
        int bId = selectedBuilding != null ? selectedBuilding.getId() : 3;
        String bName = selectedBuilding != null ? selectedBuilding.getName() : "MCA Department & Amphitheatre";

        Intent intent = new Intent(this, IndoorMapActivity.class);
        intent.putExtra("BUILDING_ID", bId);
        intent.putExtra("BUILDING_NAME", bName);
        intent.putExtra("FLOOR", 0);
        startActivity(intent);
    }

    private void openVisualNavigatorForSelectedBuilding() {
        String destId = "LOC_CYBER_204";
        if (selectedBuilding != null) {
            if (selectedBuilding.getId() == 3) destId = "LOC_MCA_DEPT";
            else if (selectedBuilding.getId() == 4) destId = "LOC_NET_LAB";
            else if (selectedBuilding.getId() == 2) destId = "LOC_LIBRARY";
            else if (selectedBuilding.getId() == 5) destId = "LOC_AUDITORIUM";
            else if (selectedBuilding.getId() == 6) destId = "LOC_CANTEEN";
            else if (selectedBuilding.getId() == 8) destId = "LOC_ADMIN";
        }
        Intent intent = new Intent(this, VisualNavigatorActivity.class);
        intent.putExtra(VisualNavigatorActivity.EXTRA_START_ID, "LOC_GATE");
        intent.putExtra(VisualNavigatorActivity.EXTRA_DEST_ID, destId);
        startActivity(intent);
    }

    private void toggle3DCameraView() {
        is3DMode = !is3DMode;
        if (campusDashboardMap != null && campusDashboardMap.getVisibility() == View.VISIBLE) {
            if (selectedBuilding != null) {
                campusDashboardMap.centerOnLocation(selectedBuilding.getLocation());
            } else {
                campusDashboardMap.resetView();
            }
            Toast.makeText(this, "Centered & Reset Campus View", Toast.LENGTH_SHORT).show();
        } else if (mMap != null) {
            CameraPosition current = mMap.getCameraPosition();
            CameraPosition newPos = new CameraPosition.Builder()
                    .target(current.target)
                    .zoom(current.zoom)
                    .bearing(current.bearing)
                    .tilt(is3DMode ? 60f : 0f)
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newPos));
            Toast.makeText(this, is3DMode ? "3D Perspective Mode" : "2D Top-Down View", Toast.LENGTH_SHORT).show();
        }
    }

    private void cycleMapLayers() {
        activeMapLayerIndex = (activeMapLayerIndex + 1) % 4;
        switch (activeMapLayerIndex) {
            case 0:
                if (campusDashboardMap != null) campusDashboardMap.setVisibility(View.VISIBLE);
                if (mapFragmentContainer != null) mapFragmentContainer.setVisibility(View.GONE);
                Toast.makeText(this, "🗺️ Interactive Vector Campus Map", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                if (campusDashboardMap != null) campusDashboardMap.setVisibility(View.GONE);
                if (mapFragmentContainer != null) mapFragmentContainer.setVisibility(View.VISIBLE);
                if (mMap != null) mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                Toast.makeText(this, "🛰️ Satellite Imagery", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                if (campusDashboardMap != null) campusDashboardMap.setVisibility(View.GONE);
                if (mapFragmentContainer != null) mapFragmentContainer.setVisibility(View.VISIBLE);
                if (mMap != null) mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                Toast.makeText(this, "🗺️ Google Street Map", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                if (campusDashboardMap != null) campusDashboardMap.setVisibility(View.GONE);
                if (mapFragmentContainer != null) mapFragmentContainer.setVisibility(View.VISIBLE);
                if (mMap != null) mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                Toast.makeText(this, "⛰️ Terrain Elevation", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void centerOnUserLocation() {
        if (campusDashboardMap != null) {
            campusDashboardMap.centerOnLocation(userLocation);
        }
        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 18.5f));
        }
        Toast.makeText(this, "Centered on Campus Entrance", Toast.LENGTH_SHORT).show();
    }

    private void filterBuildingsByCategory(String category) {
        if (category == null) {
            renderBuildingMarkers(campusBuildings);
            if (campusDashboardMap != null) campusDashboardMap.setBuildings(campusBuildings);
            return;
        }
        List<CampusBuilding> filtered = new ArrayList<>();
        for (CampusBuilding b : campusBuildings) {
            if (category.equalsIgnoreCase(b.getCategory())) {
                filtered.add(b);
            }
        }
        renderBuildingMarkers(filtered);
        if (campusDashboardMap != null) campusDashboardMap.setBuildings(filtered);
        if (!filtered.isEmpty()) {
            selectBuilding(filtered.get(0));
        }
    }

    private void filterCampusBuildings(String query) {
        if (query == null || query.trim().isEmpty()) {
            renderBuildingMarkers(campusBuildings);
            if (campusDashboardMap != null) campusDashboardMap.setBuildings(campusBuildings);
            return;
        }
        String lower = query.trim().toLowerCase();
        List<CampusBuilding> matches = new ArrayList<>();
        for (CampusBuilding b : campusBuildings) {
            if (b.getName().toLowerCase().contains(lower) ||
                b.getDescription().toLowerCase().contains(lower) ||
                b.getCategory().toLowerCase().contains(lower)) {
                matches.add(b);
            }
        }
        renderBuildingMarkers(matches);
        if (campusDashboardMap != null) campusDashboardMap.setBuildings(matches);
    }

    private void performSearchJump(String query) {
        if (query.isEmpty()) return;
        String lower = query.toLowerCase();

        for (CampusBuilding b : campusBuildings) {
            if (b.getName().toLowerCase().contains(lower) || b.getDepartments().toLowerCase().contains(lower)) {
                selectBuilding(b);
                return;
            }
        }
        Toast.makeText(this, "No buildings found matching: " + query, Toast.LENGTH_SHORT).show();
    }

    private void initLocationSafely() {
        try {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                    if (location != null) {
                        userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        if (campusDashboardMap != null) {
                            campusDashboardMap.setUserLocation(userLocation);
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void speakOut(String text) {
        if (textToSpeech != null) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}