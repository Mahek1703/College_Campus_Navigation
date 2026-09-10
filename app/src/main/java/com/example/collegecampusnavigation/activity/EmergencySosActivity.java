package com.example.collegecampusnavigation.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.collegecampusnavigation.R;
import com.example.collegecampusnavigation.helper.NotificationHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

@SuppressLint("SetTextI18n")
public class EmergencySosActivity extends AppCompatActivity {

    private TextView tvSosLocationCoords;
    private Button btnBroadcastSos, btnSirenAlarm, btnStrobeFlashlight;

    private FusedLocationProviderClient fusedLocationClient;
    private ToneGenerator toneGenerator;
    private boolean isSirenPlaying = false;
    private boolean isFlashlightOn = false;
    private CameraManager cameraManager;
    private String cameraId;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable strobeRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_sos);

        MaterialToolbar toolbar = findViewById(R.id.toolbarSos);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvSosLocationCoords = findViewById(R.id.tvSosLocationCoords);
        btnBroadcastSos = findViewById(R.id.btnBroadcastSos);
        btnSirenAlarm = findViewById(R.id.btnSirenAlarm);
        btnStrobeFlashlight = findViewById(R.id.btnStrobeFlashlight);

        findViewById(R.id.cardCallSecurity).setOnClickListener(v -> dialNumber("tel:8005557233"));
        findViewById(R.id.cardCallMedical).setOnClickListener(v -> dialNumber("tel:8005553627"));
        findViewById(R.id.cardCallSafeWalk).setOnClickListener(v -> dialNumber("tel:8005559255"));

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            if (cameraManager != null && cameraManager.getCameraIdList().length > 0) {
                cameraId = cameraManager.getCameraIdList()[0];
            }
        } catch (Exception ignored) {}

        btnBroadcastSos.setOnClickListener(v -> triggerEmergencyBroadcast());
        btnSirenAlarm.setOnClickListener(v -> toggleSirenAlarm());
        btnStrobeFlashlight.setOnClickListener(v -> toggleFlashlight());

        fetchCurrentCoordinates();
    }

    private void fetchCurrentCoordinates() {
        boolean hasFine = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean hasCoarse = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (hasFine || hasCoarse) {
            try {
                if (fusedLocationClient != null) {
                    fusedLocationClient.getLastLocation().addOnSuccessListener(this, loc -> {
                        if (loc != null) {
                            tvSosLocationCoords.setText("GPS: " + String.format(java.util.Locale.US, "%.5f, %.5f", loc.getLatitude(), loc.getLongitude()) + " • Main Quad");
                        }
                    });
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    private void triggerEmergencyBroadcast() {
        // Haptic feedback
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(500);
            }
        }

        String locStr = tvSosLocationCoords.getText().toString();
        NotificationHelper.sendEmergencySosNotification(this, locStr);

        new MaterialAlertDialogBuilder(this)
                .setTitle("🚨 SOS Broadcast Activated")
                .setMessage("Your exact GPS coordinates and distress signal have been transmitted to Campus Security & Dispatch.\n\nNearest Officer ETA: < 2 mins.\nKeep your device on and stay in a visible area.")
                .setPositiveButton("Call Security Now", (d, w) -> dialNumber("tel:8005557233"))
                .setNegativeButton("Dismiss", null)
                .show();

        Toast.makeText(this, "Distress beacon sent to Campus Security Dispatch!", Toast.LENGTH_LONG).show();
    }

    private void toggleSirenAlarm() {
        if (!isSirenPlaying) {
            try {
                toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                toneGenerator.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 15000);
                isSirenPlaying = true;
                btnSirenAlarm.setText("Stop Siren Alarm");
            } catch (Exception e) {
                Toast.makeText(this, "Unable to start siren tone", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (toneGenerator != null) {
                toneGenerator.stopTone();
                toneGenerator.release();
                toneGenerator = null;
            }
            isSirenPlaying = false;
            btnSirenAlarm.setText("Loud Siren Alarm");
        }
    }

    private void toggleFlashlight() {
        if (cameraId == null || cameraManager == null) {
            Toast.makeText(this, "Flashlight unavailable on this device", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isFlashlightOn) {
            isFlashlightOn = true;
            btnStrobeFlashlight.setText("Turn Off Flash");
            strobeRunnable = new Runnable() {
                private boolean state = false;
                @Override
                public void run() {
                    if (!isFlashlightOn) return;
                    try {
                        state = !state;
                        cameraManager.setTorchMode(cameraId, state);
                        handler.postDelayed(this, 250); // Strobe flash
                    } catch (CameraAccessException ignored) {}
                }
            };
            handler.post(strobeRunnable);
        } else {
            isFlashlightOn = false;
            handler.removeCallbacks(strobeRunnable);
            try {
                cameraManager.setTorchMode(cameraId, false);
            } catch (Exception ignored) {}
            btnStrobeFlashlight.setText("SOS Flashlight");
        }
    }

    private void dialNumber(String uriStr) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(uriStr));
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        if (toneGenerator != null) {
            toneGenerator.stopTone();
            toneGenerator.release();
        }
        if (isFlashlightOn && cameraManager != null && cameraId != null) {
            try {
                cameraManager.setTorchMode(cameraId, false);
            } catch (Exception ignored) {}
        }
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
