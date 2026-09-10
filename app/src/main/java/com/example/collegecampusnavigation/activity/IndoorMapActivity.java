package com.example.collegecampusnavigation.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.collegecampusnavigation.R;
import com.example.collegecampusnavigation.helper.DatabaseHelper;
import com.example.collegecampusnavigation.helper.IndoorRoutingEngine;
import com.example.collegecampusnavigation.helper.PanoramaTourRepository;
import com.example.collegecampusnavigation.model.IndoorRoom;
import com.example.collegecampusnavigation.model.PanoramaHotspot;
import com.example.collegecampusnavigation.model.PanoramaScene;
import com.example.collegecampusnavigation.view.MiniMapView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressLint("SetTextI18n")
public class IndoorMapActivity extends AppCompatActivity implements MiniMapView.OnMiniMapRoomClickListener, SensorEventListener {

    private WebView webViewPanorama;
    private MiniMapView miniMapView;
    private MaterialCardView cardMiniMap;
    private TabLayout tabLayoutFloors;
    private TextView tvCurrentBuilding, tvCompassHeading, tvMiniMapFloorLabel;
    private TextView tvIndoorRoomCode, tvIndoorRoomName, tvIndoorDirections;
    private ImageView ivCompassRose, ivIndoorRoomCategoryIcon, btnToggleMiniMapSize;
    private Button btnStartIndoorNav, btnSearchRooms, btnIndoorVisualTour;
    private FloatingActionButton fabAutoRotate, fabGyroToggle, fabResetTourView;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private TextToSpeech textToSpeech;

    private SensorManager sensorManager;
    private Sensor rotationSensor;
    private boolean isGyroActive = false;
    private boolean isAutoRotateActive = false;
    private boolean isMiniMapExpanded = false;

    private int buildingId = 3; // Default: MCA Department
    private String buildingName = "MCA Department & Amphitheatre";
    private int currentFloor = 0; // 0 = Ground, 1 = 1st, 2 = 2nd, 3 = 3rd
    private List<IndoorRoom> currentFloorRooms = new ArrayList<>();
    private IndoorRoom selectedRoom;
    private PanoramaScene currentScene;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor_map);

        MaterialToolbar toolbar = findViewById(R.id.toolbarIndoor);
        toolbar.setNavigationOnClickListener(v -> finish());

        webViewPanorama = findViewById(R.id.webViewPanorama);
        miniMapView = findViewById(R.id.miniMapView);
        cardMiniMap = findViewById(R.id.cardMiniMap);
        tabLayoutFloors = findViewById(R.id.tabLayoutFloors);
        tvCurrentBuilding = findViewById(R.id.tvCurrentBuilding);
        tvCompassHeading = findViewById(R.id.tvCompassHeading);
        tvMiniMapFloorLabel = findViewById(R.id.tvMiniMapFloorLabel);
        tvIndoorRoomCode = findViewById(R.id.tvIndoorRoomCode);
        tvIndoorRoomName = findViewById(R.id.tvIndoorRoomName);
        tvIndoorDirections = findViewById(R.id.tvIndoorDirections);
        ivCompassRose = findViewById(R.id.ivCompassRose);
        ivIndoorRoomCategoryIcon = findViewById(R.id.ivIndoorRoomCategoryIcon);
        btnToggleMiniMapSize = findViewById(R.id.btnToggleMiniMapSize);
        btnStartIndoorNav = findViewById(R.id.btnStartIndoorNav);
        btnSearchRooms = findViewById(R.id.btnSearchRooms);
        btnIndoorVisualTour = findViewById(R.id.btnIndoorVisualTour);
        fabAutoRotate = findViewById(R.id.fabAutoRotate);
        fabGyroToggle = findViewById(R.id.fabGyroToggle);
        fabResetTourView = findViewById(R.id.fabResetTourView);

        View bottomSheet = findViewById(R.id.indoorBottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // TTS setup
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
            }
        });

        // Sensor Manager setup for motion panning
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        }

        miniMapView.setOnMiniMapRoomClickListener(this);

        // Extract intent extras
        if (getIntent().hasExtra("BUILDING_ID")) {
            buildingId = getIntent().getIntExtra("BUILDING_ID", 3);
        }
        if (getIntent().hasExtra("BUILDING_NAME")) {
            buildingName = getIntent().getStringExtra("BUILDING_NAME");
        }
        if (getIntent().hasExtra("FLOOR")) {
            currentFloor = getIntent().getIntExtra("FLOOR", 0);
        }
        String targetRoomCode = getIntent().getStringExtra("TARGET_ROOM");

        tvCurrentBuilding.setText("🏢 " + buildingName);

        // Setup Floor Selection Tabs
        tabLayoutFloors.addTab(tabLayoutFloors.newTab().setText("Ground (G)"));
        tabLayoutFloors.addTab(tabLayoutFloors.newTab().setText("Floor 1"));
        tabLayoutFloors.addTab(tabLayoutFloors.newTab().setText("Floor 2"));
        tabLayoutFloors.addTab(tabLayoutFloors.newTab().setText("Floor 3"));

        TabLayout.Tab tab = tabLayoutFloors.getTabAt(currentFloor);
        if (tab != null) tab.select();

        tabLayoutFloors.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentFloor = tab.getPosition();
                onFloorChanged(currentFloor);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Configure 360 Panorama WebView
        setupPanoramaWebView();

        // Setup Action Buttons
        btnSearchRooms.setOnClickListener(v -> showRoomSearchDialog());
        btnToggleMiniMapSize.setOnClickListener(v -> toggleMiniMapSize());
        fabAutoRotate.setOnClickListener(v -> toggleAutoRotation());
        fabGyroToggle.setOnClickListener(v -> toggleGyroscope());
        fabResetTourView.setOnClickListener(v -> resetTourView());

        btnStartIndoorNav.setOnClickListener(v -> {
            if (selectedRoom != null) {
                String prompt = "Starting 360 route to " + selectedRoom.getRoomCode() + ", " + selectedRoom.getName();
                speakOut(prompt);
                Toast.makeText(this, prompt, Toast.LENGTH_SHORT).show();
            } else if (currentScene != null) {
                speakOut("Navigating within " + currentScene.getTitle());
            }
        });

        if (btnIndoorVisualTour != null) {
            btnIndoorVisualTour.setOnClickListener(v -> {
                String destId = "LOC_CYBER_204";
                if (selectedRoom != null) {
                    String code = selectedRoom.getRoomCode();
                    if (code.contains("CYBER") || code.contains("204")) destId = "LOC_CYBER_204";
                    else if (code.contains("201") || code.contains("CS-101")) destId = "LOC_SMART_CLASS_201";
                    else if (code.contains("NET") || code.contains("LAB")) destId = "LOC_NET_LAB";
                    else if (code.contains("AUD-MCA") || buildingId == 3) destId = "LOC_MCA_DEPT";
                    else if (code.contains("AUD") || buildingId == 5) destId = "LOC_AUDITORIUM";
                    else if (code.contains("LIB") || buildingId == 2) destId = "LOC_LIBRARY";
                    else if (code.contains("CANTEEN") || buildingId == 6) destId = "LOC_CANTEEN";
                }
                Intent intent = new Intent(this, VisualNavigatorActivity.class);
                intent.putExtra(VisualNavigatorActivity.EXTRA_START_ID, "LOC_GATE");
                intent.putExtra(VisualNavigatorActivity.EXTRA_DEST_ID, destId);
                startActivity(intent);
            });
        }

        loadFloorRooms(buildingId, currentFloor);

        // Target room auto-focus
        if (targetRoomCode != null) {
            PanoramaScene targetScene = PanoramaTourRepository.findSceneByRoomCode(targetRoomCode);
            if (targetScene != null) {
                switchScene(targetScene);
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupPanoramaWebView() {
        WebSettings settings = webViewPanorama.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        webViewPanorama.setWebChromeClient(new WebChromeClient());
        webViewPanorama.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Load initial 360 scene once HTML engine is ready
                PanoramaScene initial = PanoramaTourRepository.getInitialSceneForBuildingAndFloor(buildingId, currentFloor);
                if (initial != null) {
                    switchScene(initial);
                }
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String uri = request.getUrl().toString();
                if (uri.startsWith("https://appassets.local/drawable/")) {
                    String resName = uri.substring("https://appassets.local/drawable/".length());
                    int id = getResources().getIdentifier(resName, "drawable", getPackageName());
                    if (id != 0) {
                        InputStream is = getResources().openRawResource(id);
                        return new WebResourceResponse("image/jpeg", "UTF-8", is);
                    }
                }
                return super.shouldInterceptRequest(view, request);
            }
        });

        webViewPanorama.addJavascriptInterface(new PanoramaJSBridge(), "AndroidBridge");
        webViewPanorama.loadUrl("file:///android_asset/panorama/viewer.html");
    }

    public class PanoramaJSBridge {
        @JavascriptInterface
        public void onHotspotClicked(String targetSceneId, String targetRoomCode) {
            mainHandler.post(() -> {
                PanoramaScene nextScene = PanoramaTourRepository.getScene(targetSceneId);
                if (nextScene != null) {
                    switchScene(nextScene);
                    speakOut("Entering " + nextScene.getTitle());
                } else {
                    Toast.makeText(IndoorMapActivity.this, "Navigating to " + targetRoomCode, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @JavascriptInterface
        public void onCameraMoved(float yaw, float pitch) {
            mainHandler.post(() -> {
                miniMapView.setCameraYaw(yaw);
                updateCompassHud(yaw);
            });
        }
    }

    private void updateCompassHud(float yaw) {
        float normalized = (yaw % 360 + 360) % 360;
        String dir = "N";
        if (normalized >= 22.5 && normalized < 67.5) dir = "NE";
        else if (normalized >= 67.5 && normalized < 112.5) dir = "E";
        else if (normalized >= 112.5 && normalized < 157.5) dir = "SE";
        else if (normalized >= 157.5 && normalized < 202.5) dir = "S";
        else if (normalized >= 202.5 && normalized < 247.5) dir = "SW";
        else if (normalized >= 247.5 && normalized < 292.5) dir = "W";
        else if (normalized >= 292.5 && normalized < 337.5) dir = "NW";

        tvCompassHeading.setText(dir + " " + (int) normalized + "° • 360° Live");
        ivCompassRose.setRotation(-normalized);
    }

    public void switchScene(PanoramaScene scene) {
        if (scene == null) return;
        this.currentScene = scene;

        // Sync floor tab if different
        if (scene.getFloor() != currentFloor) {
            currentFloor = scene.getFloor();
            TabLayout.Tab tab = tabLayoutFloors.getTabAt(currentFloor);
            if (tab != null && tab.getPosition() != currentFloor) {
                tab.select();
            }
            loadFloorRooms(scene.getBuildingId(), currentFloor);
        }

        // Convert drawable to Base64 data URL for instant WebGL loading
        String imageBase64 = encodeDrawableToBase64(scene.getImageResId());

        try {
            JSONObject sceneJson = new JSONObject();
            sceneJson.put("id", scene.getId());
            sceneJson.put("title", scene.getTitle());
            sceneJson.put("subtitle", scene.getSubtitle());
            sceneJson.put("defaultYaw", scene.getDefaultYaw());
            sceneJson.put("defaultPitch", scene.getDefaultPitch());
            sceneJson.put("imageBase64", imageBase64);

            JSONArray hsArray = new JSONArray();
            for (PanoramaHotspot hs : scene.getHotspots()) {
                JSONObject hObj = new JSONObject();
                hObj.put("id", hs.getId());
                hObj.put("title", hs.getTitle());
                hObj.put("subtitle", hs.getSubtitle());
                hObj.put("yaw", hs.getYaw());
                hObj.put("pitch", hs.getPitch());
                hObj.put("type", hs.getType().name());
                hObj.put("targetSceneId", hs.getTargetSceneId());
                hObj.put("targetRoomCode", hs.getTargetRoomCode());
                hsArray.put(hObj);
            }
            sceneJson.put("hotspots", hsArray);

            webViewPanorama.evaluateJavascript("loadSceneFromData(" + sceneJson.toString() + ");", null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Update MiniMap
        miniMapView.setUserPosition(scene.getMiniMapX(), scene.getMiniMapY());
        tvMiniMapFloorLabel.setText("Floor " + (scene.getFloor() == 0 ? "G" : String.valueOf(scene.getFloor())) + " 2D Radar");

        // Update Room Details Card
        tvIndoorRoomCode.setText(scene.getTitle());
        tvIndoorRoomName.setText(scene.getSubtitle());
        if (ivIndoorRoomCategoryIcon != null) {
            ivIndoorRoomCategoryIcon.setImageResource(scene.getImageResId());
        }

        // Find matching indoor room for walk calculation
        IndoorRoom matchingRoom = null;
        for (IndoorRoom r : currentFloorRooms) {
            if (r.getRoomCode().equalsIgnoreCase(scene.getRoomCode())) {
                matchingRoom = r;
                break;
            }
        }

        if (matchingRoom != null) {
            this.selectedRoom = matchingRoom;
            miniMapView.setSelectedRoom(matchingRoom);
            calculateDirections(matchingRoom);
        } else {
            tvIndoorDirections.setText("📍 Current Location: " + scene.getTitle() + "\n👉 Tap 3D floating markers or mini-map to explore adjacent rooms.");
        }
    }

    private void onFloorChanged(int floor) {
        tvMiniMapFloorLabel.setText("Floor " + (floor == 0 ? "G" : String.valueOf(floor)) + " 2D Radar");
        loadFloorRooms(buildingId, floor);

        PanoramaScene target = PanoramaTourRepository.getInitialSceneForBuildingAndFloor(buildingId, floor);
        if (target != null) {
            switchScene(target);
            speakOut("Switched to Floor " + (floor == 0 ? "Ground" : String.valueOf(floor)));
        }
    }

    private void loadFloorRooms(int bId, int floor) {
        try (DatabaseHelper db = new DatabaseHelper(this)) {
            currentFloorRooms = db.getRoomsForBuildingAndFloor(bId, floor);
        }
        miniMapView.setRooms(currentFloorRooms);
    }

    @Override
    public void onMiniMapRoomClicked(IndoorRoom room) {
        this.selectedRoom = room;
        miniMapView.setSelectedRoom(room);

        PanoramaScene scene = PanoramaTourRepository.findSceneByRoomCode(room.getRoomCode());
        if (scene != null) {
            switchScene(scene);
        } else {
            tvIndoorRoomCode.setText(room.getRoomCode() + " (" + room.getRoomType() + ")");
            tvIndoorRoomName.setText(room.getName() + "\n" + room.getDescription());
            calculateDirections(room);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        speakOut(room.getRoomCode() + ", " + room.getName());
    }

    private void calculateDirections(IndoorRoom room) {
        IndoorRoutingEngine.IndoorPathResult result = IndoorRoutingEngine.calculateIndoorPath(room, currentFloor, true);
        StringBuilder sb = new StringBuilder();
        sb.append("⚡ Est. Walk: ").append(result.distanceFeet).append(" ft (~").append(result.estimatedSeconds).append("s)\n");
        for (int i = 0; i < result.directions.size(); i++) {
            sb.append("👉 ").append(result.directions.get(i)).append("\n");
        }
        tvIndoorDirections.setText(sb.toString().trim());
    }

    private void showRoomSearchDialog() {
        final EditText input = new EditText(this);
        input.setHint("e.g. AUD-MCA-100, LAB-CYBER-204, LAB-NET-201, CANTEEN-01");

        new MaterialAlertDialogBuilder(this)
                .setTitle("Search 360° Virtual Tour Rooms")
                .setView(input)
                .setPositiveButton("Explore 360°", (dialog, which) -> {
                    String query = input.getText().toString().trim();
                    if (!query.isEmpty()) {
                        performRoomSearch(query);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performRoomSearch(String query) {
        PanoramaScene foundScene = PanoramaTourRepository.findSceneByRoomCode(query);
        if (foundScene != null) {
            buildingId = foundScene.getBuildingId();
            buildingName = foundScene.getBuildingName();
            tvCurrentBuilding.setText("🏢 " + buildingName);

            currentFloor = foundScene.getFloor();
            TabLayout.Tab tab = tabLayoutFloors.getTabAt(currentFloor);
            if (tab != null) tab.select();

            switchScene(foundScene);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            return;
        }

        List<IndoorRoom> results;
        try (DatabaseHelper db = new DatabaseHelper(this)) {
            results = db.searchIndoorRooms(query);
        }

        if (results.isEmpty()) {
            Toast.makeText(this, "No 360 rooms or labs matching: " + query, Toast.LENGTH_SHORT).show();
            return;
        }

        IndoorRoom target = results.get(0);
        buildingId = target.getBuildingId();
        buildingName = target.getBuildingName();
        tvCurrentBuilding.setText("🏢 " + buildingName);

        currentFloor = target.getFloor();
        TabLayout.Tab tab = tabLayoutFloors.getTabAt(currentFloor);
        if (tab != null) tab.select();

        onMiniMapRoomClicked(target);
    }

    private void toggleMiniMapSize() {
        isMiniMapExpanded = !isMiniMapExpanded;
        ViewGroup.LayoutParams lp = cardMiniMap.getLayoutParams();
        int targetSizeDp = isMiniMapExpanded ? 240 : 140;
        int pixels = (int) (targetSizeDp * getResources().getDisplayMetrics().density);
        lp.width = pixels;
        lp.height = pixels;
        cardMiniMap.setLayoutParams(lp);
        Toast.makeText(this, isMiniMapExpanded ? "Mini-Map Expanded" : "Mini-Map Compact", Toast.LENGTH_SHORT).show();
    }

    private void toggleAutoRotation() {
        isAutoRotateActive = !isAutoRotateActive;
        webViewPanorama.evaluateJavascript("toggleAutoRotation(" + isAutoRotateActive + ");", null);
        fabAutoRotate.setImageTintList(android.content.res.ColorStateList.valueOf(
                isAutoRotateActive ? android.graphics.Color.parseColor("#00E676") : android.graphics.Color.WHITE
        ));
    }

    private void toggleGyroscope() {
        isGyroActive = !isGyroActive;
        if (isGyroActive && rotationSensor != null) {
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_GAME);
            Toast.makeText(this, "📱 Gyroscope Motion Active: Move device to look around", Toast.LENGTH_SHORT).show();
        } else {
            sensorManager.unregisterListener(this);
            Toast.makeText(this, "👆 Touch Drag Mode Active", Toast.LENGTH_SHORT).show();
        }
        fabGyroToggle.setImageTintList(android.content.res.ColorStateList.valueOf(
                isGyroActive ? android.graphics.Color.parseColor("#00E676") : android.graphics.Color.WHITE
        ));
    }

    private void resetTourView() {
        if (currentScene != null) {
            webViewPanorama.evaluateJavascript("lon = " + currentScene.getDefaultYaw() + "; lat = " + currentScene.getDefaultPitch() + "; targetFov = 75;", null);
        }
    }

    private String encodeDrawableToBase64(int resId) {
        if (resId == 0) return "";
        try {
            BitmapFactory.Options boundsOptions = new BitmapFactory.Options();
            boundsOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), resId, boundsOptions);

            int reqWidth = 1440;
            int reqHeight = 960;
            int inSampleSize = 1;

            if (boundsOptions.outHeight > reqHeight || boundsOptions.outWidth > reqWidth) {
                final int halfHeight = boundsOptions.outHeight / 2;
                final int halfWidth = boundsOptions.outWidth / 2;
                while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                    inSampleSize *= 2;
                }
            }

            BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
            decodeOptions.inSampleSize = Math.max(1, inSampleSize);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId, decodeOptions);
            if (bitmap == null) return "";

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);
            byte[] byteArray = outputStream.toByteArray();
            bitmap.recycle();
            return "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR && isGyroActive) {
            float[] rotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            float[] orientationValues = new float[3];
            SensorManager.getOrientation(rotationMatrix, orientationValues);

            float azimuth = (float) Math.toDegrees(orientationValues[0]);
            float pitch = (float) Math.toDegrees(orientationValues[1]);

            webViewPanorama.evaluateJavascript("lon = " + (-azimuth) + "; lat = " + (-pitch) + ";", null);
        }
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void speakOut(String text) {
        if (textToSpeech != null) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isGyroActive && rotationSensor != null) {
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        super.onDestroy();
    }
}
