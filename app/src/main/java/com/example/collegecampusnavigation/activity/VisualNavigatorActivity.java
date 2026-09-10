package com.example.collegecampusnavigation.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collegecampusnavigation.R;
import com.example.collegecampusnavigation.adapter.RouteFlowchartAdapter;
import com.example.collegecampusnavigation.helper.VisualRoutingEngine;
import com.example.collegecampusnavigation.model.VisualNavRoute;
import com.example.collegecampusnavigation.model.VisualNavStep;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressLint("SetTextI18n")
public class VisualNavigatorActivity extends AppCompatActivity {

    public static final String EXTRA_START_ID = "EXTRA_START_ID";
    public static final String EXTRA_DEST_ID = "EXTRA_DEST_ID";

    private Spinner spinnerStartLocation, spinnerDestLocation;
    private ImageView ivCurrentStepPhoto;
    private TextView tvDirectionIcon, tvDirectionText, tvStepFloorBadge;
    private TextView tvStepCounterBadge, tvStepDistanceBadge;
    private TextView tvStepTitle, tvStepInstruction, tvStepLandmark, tvRouteTotalEst;
    private LinearProgressIndicator stepProgressBar;
    private Button btnPrevStep, btnNextStep, btnAutoPlay, btnSpeakStep, btnToolbarFlowchart, btnSwapRoute;

    private List<VisualRoutingEngine.CampusLocationItem> locationItems = new ArrayList<>();
    private VisualNavRoute currentRoute;
    private int currentStepIndex = 0;
    private boolean isAutoPlaying = false;
    private final Handler autoPlayHandler = new Handler(Looper.getMainLooper());
    private Runnable autoPlayRunnable;
    private TextToSpeech ttsEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_navigator);

        MaterialToolbar toolbar = findViewById(R.id.toolbarVisualNav);
        toolbar.setNavigationOnClickListener(v -> finish());

        spinnerStartLocation = findViewById(R.id.spinnerStartLocation);
        spinnerDestLocation = findViewById(R.id.spinnerDestLocation);
        btnSwapRoute = findViewById(R.id.btnSwapRoute);
        btnToolbarFlowchart = findViewById(R.id.btnToolbarFlowchart);

        ivCurrentStepPhoto = findViewById(R.id.ivCurrentStepPhoto);
        tvDirectionIcon = findViewById(R.id.tvDirectionIcon);
        tvDirectionText = findViewById(R.id.tvDirectionText);
        tvStepFloorBadge = findViewById(R.id.tvStepFloorBadge);
        tvStepCounterBadge = findViewById(R.id.tvStepCounterBadge);
        tvStepDistanceBadge = findViewById(R.id.tvStepDistanceBadge);

        tvStepTitle = findViewById(R.id.tvStepTitle);
        tvStepInstruction = findViewById(R.id.tvStepInstruction);
        tvStepLandmark = findViewById(R.id.tvStepLandmark);
        tvRouteTotalEst = findViewById(R.id.tvRouteTotalEst);
        stepProgressBar = findViewById(R.id.stepProgressBar);

        btnPrevStep = findViewById(R.id.btnPrevStep);
        btnNextStep = findViewById(R.id.btnNextStep);
        btnAutoPlay = findViewById(R.id.btnAutoPlay);
        btnSpeakStep = findViewById(R.id.btnSpeakStep);

        // Setup TTS
        ttsEngine = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                ttsEngine.setLanguage(Locale.US);
            }
        });

        // Initialize Locations & Spinners
        initLocationSpinners();

        // Setup Quick Chips
        setupQuickChips();

        // Check incoming Intent extras
        String incomingStart = getIntent().getStringExtra(EXTRA_START_ID);
        String incomingDest = getIntent().getStringExtra(EXTRA_DEST_ID);
        if (incomingStart != null && incomingDest != null) {
            selectLocationsInSpinners(incomingStart, incomingDest);
        } else {
            // Default to Featured: Main Entrance -> Cyber Security Lab 204
            selectLocationsInSpinners("LOC_GATE", "LOC_CYBER_204");
        }

        // Navigation Controls
        btnPrevStep.setOnClickListener(v -> navigateStep(-1));
        btnNextStep.setOnClickListener(v -> navigateStep(1));
        btnAutoPlay.setOnClickListener(v -> toggleAutoPlay());
        btnSpeakStep.setOnClickListener(v -> speakCurrentStep());
        btnToolbarFlowchart.setOnClickListener(v -> showFlowchartBottomSheet());
        btnSwapRoute.setOnClickListener(v -> swapStartAndDestination());
    }

    private void initLocationSpinners() {
        locationItems = VisualRoutingEngine.getAllCampusLocations();
        List<String> locationNames = new ArrayList<>();
        for (VisualRoutingEngine.CampusLocationItem item : locationItems) {
            locationNames.add(item.getName() + " (" + item.getFloorDisplay() + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, locationNames);
        spinnerStartLocation.setAdapter(adapter);
        spinnerDestLocation.setAdapter(adapter);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int startPos = spinnerStartLocation.getSelectedItemPosition();
                int destPos = spinnerDestLocation.getSelectedItemPosition();
                if (startPos >= 0 && destPos >= 0 && startPos < locationItems.size() && destPos < locationItems.size()) {
                    loadRoute(locationItems.get(startPos).getId(), locationItems.get(destPos).getId());
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerStartLocation.setOnItemSelectedListener(listener);
        spinnerDestLocation.setOnItemSelectedListener(listener);
    }

    private void selectLocationsInSpinners(String startId, String destId) {
        int startIndex = 0;
        int destIndex = 0;
        for (int i = 0; i < locationItems.size(); i++) {
            if (locationItems.get(i).getId().equals(startId)) startIndex = i;
            if (locationItems.get(i).getId().equals(destId)) destIndex = i;
        }
        spinnerStartLocation.setSelection(startIndex);
        spinnerDestLocation.setSelection(destIndex);
        loadRoute(startId, destId);
    }

    private void swapStartAndDestination() {
        int startPos = spinnerStartLocation.getSelectedItemPosition();
        int destPos = spinnerDestLocation.getSelectedItemPosition();
        spinnerStartLocation.setSelection(destPos);
        spinnerDestLocation.setSelection(startPos);
    }

    private void setupQuickChips() {
        findViewById(R.id.chipCyberLab).setOnClickListener(v -> selectLocationsInSpinners("LOC_GATE", "LOC_CYBER_204"));
        findViewById(R.id.chipSmartClass).setOnClickListener(v -> selectLocationsInSpinners("LOC_GATE", "LOC_SMART_CLASS_201"));
        findViewById(R.id.chipAuditorium).setOnClickListener(v -> selectLocationsInSpinners("LOC_GATE", "LOC_AUDITORIUM"));
        findViewById(R.id.chipLibrary).setOnClickListener(v -> selectLocationsInSpinners("LOC_GATE", "LOC_LIBRARY"));
        findViewById(R.id.chipCanteen).setOnClickListener(v -> selectLocationsInSpinners("LOC_GATE", "LOC_CANTEEN"));
        findViewById(R.id.chipNetLab).setOnClickListener(v -> selectLocationsInSpinners("LOC_RECEPTION", "LOC_NET_LAB"));
    }

    private void loadRoute(String startId, String destId) {
        stopAutoPlay();
        currentRoute = VisualRoutingEngine.getRouteBetween(startId, destId);
        currentStepIndex = 0;
        renderCurrentStep(false);
    }

    private void renderCurrentStep(boolean animate) {
        if (currentRoute == null || currentRoute.getSteps().isEmpty()) return;

        List<VisualNavStep> steps = currentRoute.getSteps();
        if (currentStepIndex < 0) currentStepIndex = 0;
        if (currentStepIndex >= steps.size()) currentStepIndex = steps.size() - 1;

        VisualNavStep step = steps.get(currentStepIndex);

        // Animate Photo transition
        if (animate) {
            Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            fadeIn.setDuration(400);
            ivCurrentStepPhoto.startAnimation(fadeIn);
        }

        if (step.getImageResId() != 0) {
            ivCurrentStepPhoto.setImageResource(step.getImageResId());
        }

        tvDirectionIcon.setText(step.getDirectionIconSymbol());
        tvDirectionText.setText(step.getDirectionText());
        tvStepFloorBadge.setText(step.getFloorLevel());

        tvStepCounterBadge.setText("Step " + step.getStepNumber() + " of " + steps.size());
        tvStepDistanceBadge.setText("• " + step.getDistanceMeters() + "m");

        tvStepTitle.setText(step.getTitle());
        tvStepInstruction.setText(step.getInstruction());
        tvStepLandmark.setText("📍 " + step.getLandmarkDetail());
        tvRouteTotalEst.setText("Total: " + currentRoute.getTotalDistanceMeters() + "m • ~" + currentRoute.getEstimatedMinutes() + " mins");

        int progress = (int) (((float) (currentStepIndex + 1) / steps.size()) * 100);
        stepProgressBar.setProgressCompat(progress, true);

        // Update button states
        btnPrevStep.setEnabled(currentStepIndex > 0);
        if (currentStepIndex == steps.size() - 1) {
            btnNextStep.setText("Finish Route");
            btnNextStep.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#10B981")));
        } else {
            btnNextStep.setText("Next Step");
            btnNextStep.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#0284C7")));
        }
    }

    private void navigateStep(int delta) {
        if (currentRoute == null) return;
        int nextIndex = currentStepIndex + delta;
        if (nextIndex >= currentRoute.getStepCount()) {
            stopAutoPlay();
            Toast.makeText(this, "🎉 You have reached your destination!", Toast.LENGTH_SHORT).show();
            speakOut("You have reached your destination: " + currentRoute.getDestinationName());
            return;
        }
        if (nextIndex >= 0) {
            currentStepIndex = nextIndex;
            renderCurrentStep(true);
            speakCurrentStep();
        }
    }

    private void toggleAutoPlay() {
        if (isAutoPlaying) {
            stopAutoPlay();
        } else {
            startAutoPlay();
        }
    }

    private void startAutoPlay() {
        if (currentRoute == null) return;
        isAutoPlaying = true;
        btnAutoPlay.setText("Pause");
        btnAutoPlay.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#EF4444")));

        autoPlayRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentStepIndex < currentRoute.getStepCount() - 1) {
                    navigateStep(1);
                    autoPlayHandler.postDelayed(this, 3500); // 3.5 seconds per step
                } else {
                    stopAutoPlay();
                }
            }
        };
        autoPlayHandler.postDelayed(autoPlayRunnable, 3500);
        Toast.makeText(this, "▶️ Auto-Play Tour started", Toast.LENGTH_SHORT).show();
    }

    private void stopAutoPlay() {
        isAutoPlaying = false;
        autoPlayHandler.removeCallbacksAndMessages(null);
        btnAutoPlay.setText("Auto");
        btnAutoPlay.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#0369A1")));
    }

    private void speakCurrentStep() {
        if (currentRoute == null || currentRoute.getSteps().isEmpty()) return;
        VisualNavStep step = currentRoute.getSteps().get(currentStepIndex);
        speakOut(step.getDirectionText() + ". " + step.getInstruction());
    }

    private void speakOut(String text) {
        if (ttsEngine != null) {
            ttsEngine.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private void showFlowchartBottomSheet() {
        if (currentRoute == null) return;

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_route_flowchart, null);
        dialog.setContentView(dialogView);

        TextView tvFlowchartRouteSummary = dialogView.findViewById(R.id.tvFlowchartRouteSummary);
        TextView tvFlowchartMetrics = dialogView.findViewById(R.id.tvFlowchartMetrics);
        RecyclerView rvFlowchartNodes = dialogView.findViewById(R.id.rvFlowchartNodes);
        ImageView btnCloseFlowchart = dialogView.findViewById(R.id.btnCloseFlowchart);

        tvFlowchartRouteSummary.setText(currentRoute.getStartLocationName() + " ➔ " + currentRoute.getDestinationName());
        tvFlowchartMetrics.setText(currentRoute.getStepCount() + " Visual Milestones • " + currentRoute.getTotalDistanceMeters() + "m • ~" + currentRoute.getEstimatedMinutes() + " mins walk");

        rvFlowchartNodes.setLayoutManager(new LinearLayoutManager(this));
        RouteFlowchartAdapter adapter = new RouteFlowchartAdapter(
                currentRoute.getSteps(),
                currentStepIndex,
                stepIndex -> {
                    currentStepIndex = stepIndex;
                    renderCurrentStep(true);
                    dialog.dismiss();
                    speakCurrentStep();
                }
        );
        rvFlowchartNodes.setAdapter(adapter);

        btnCloseFlowchart.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        stopAutoPlay();
        if (ttsEngine != null) {
            ttsEngine.stop();
            ttsEngine.shutdown();
        }
        super.onDestroy();
    }
}
