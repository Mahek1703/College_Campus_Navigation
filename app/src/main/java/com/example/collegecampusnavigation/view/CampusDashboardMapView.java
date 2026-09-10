package com.example.collegecampusnavigation.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.collegecampusnavigation.model.CampusBuilding;
import com.example.collegecampusnavigation.model.CampusRoute;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class CampusDashboardMapView extends View {

    // Campus geographic bounds for projection
    private static final double MIN_LAT = 19.2165;
    private static final double MAX_LAT = 19.2205;
    private static final double MIN_LNG = 73.1610;
    private static final double MAX_LNG = 73.1660;

    private List<CampusBuilding> buildings = new ArrayList<>();
    private CampusBuilding selectedBuilding;
    private CampusRoute currentRoute;
    private LatLng userLocation = new LatLng(19.2173, 73.1622); // Default entrance

    // Touch and Gesture variables
    private float scaleFactor = 1.0f;
    private float focusX = 0f;
    private float focusY = 0f;
    private float lastTouchX = 0f;
    private float lastTouchY = 0f;
    private boolean isDragging = false;
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    // Paints
    private Paint bgPaint;
    private Paint grassPaint;
    private Paint roadPaint;
    private Paint roadBorderPaint;
    private Paint walkwayPaint;
    private Paint gardenPaint;
    private Paint sportsFieldPaint;
    private Paint trackPaint;
    private Paint courtPaint;
    private Paint parkingPaint;
    private Paint parkingLinePaint;
    private Paint buildingBasePaint;
    private Paint buildingStrokePaint;
    private Paint buildingSelectedPaint;
    private Paint buildingShadowPaint;
    private Paint textTitlePaint;
    private Paint textSubtitlePaint;
    private Paint badgeBgPaint;
    private Paint badgeTextPaint;
    private Paint routePolylinePaint;
    private Paint routeGlowPaint;
    private Paint userPulsePaint;
    private Paint userCorePaint;
    private Paint entranceDotPaint;

    public interface OnBuildingClickListener {
        void onBuildingClicked(CampusBuilding building);
        void onMapBackgroundClicked();
    }

    private OnBuildingClickListener listener;

    public CampusDashboardMapView(Context context) {
        super(context);
        init(context);
    }

    public CampusDashboardMapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CampusDashboardMapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(Color.parseColor("#F1F5F9")); // Slate canvas

        grassPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        grassPaint.setColor(Color.parseColor("#E2F1E8")); // Subtle soft green lawn

        roadPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        roadPaint.setColor(Color.parseColor("#E2E8F0")); // Asphalt driveway
        roadPaint.setStyle(Paint.Style.STROKE);
        roadPaint.setStrokeCap(Paint.Cap.ROUND);
        roadPaint.setStrokeJoin(Paint.Join.ROUND);

        roadBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        roadBorderPaint.setColor(Color.parseColor("#CBD5E1"));
        roadBorderPaint.setStyle(Paint.Style.STROKE);
        roadBorderPaint.setStrokeCap(Paint.Cap.ROUND);
        roadBorderPaint.setStrokeJoin(Paint.Join.ROUND);

        walkwayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        walkwayPaint.setColor(Color.parseColor("#FEF3C7")); // Paved yellow-brick walkways
        walkwayPaint.setStyle(Paint.Style.STROKE);
        walkwayPaint.setStrokeCap(Paint.Cap.ROUND);

        gardenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gardenPaint.setColor(Color.parseColor("#DCFCE7")); // Courtyard botanical garden

        sportsFieldPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sportsFieldPaint.setColor(Color.parseColor("#86EFAC")); // Soccer/football grass

        trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trackPaint.setColor(Color.parseColor("#FCA5A5")); // Red athletic track
        trackPaint.setStyle(Paint.Style.STROKE);

        courtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        courtPaint.setColor(Color.parseColor("#93C5FD")); // Blue tennis/basketball court

        parkingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        parkingPaint.setColor(Color.parseColor("#E2E8F0"));

        parkingLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        parkingLinePaint.setColor(Color.parseColor("#94A3B8"));
        parkingLinePaint.setStyle(Paint.Style.STROKE);
        parkingLinePaint.setStrokeWidth(2f);
        parkingLinePaint.setPathEffect(new DashPathEffect(new float[]{6f, 6f}, 0f));

        buildingBasePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        buildingBasePaint.setColor(Color.parseColor("#FFFFFF"));
        buildingBasePaint.setStyle(Paint.Style.FILL);

        buildingShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        buildingShadowPaint.setColor(Color.parseColor("#200F172A"));
        buildingShadowPaint.setStyle(Paint.Style.FILL);

        buildingStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        buildingStrokePaint.setColor(Color.parseColor("#0284C7")); // Modern blue outline
        buildingStrokePaint.setStyle(Paint.Style.STROKE);
        buildingStrokePaint.setStrokeWidth(3f);

        buildingSelectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        buildingSelectedPaint.setColor(Color.parseColor("#0284C7"));
        buildingSelectedPaint.setStyle(Paint.Style.STROKE);
        buildingSelectedPaint.setStrokeWidth(6f);

        textTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textTitlePaint.setColor(Color.parseColor("#0F172A"));
        textTitlePaint.setTextSize(26f);
        textTitlePaint.setFakeBoldText(true);
        textTitlePaint.setTextAlign(Paint.Align.CENTER);

        textSubtitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textSubtitlePaint.setColor(Color.parseColor("#475569"));
        textSubtitlePaint.setTextSize(18f);
        textSubtitlePaint.setTextAlign(Paint.Align.CENTER);

        badgeBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        badgeBgPaint.setColor(Color.parseColor("#0284C7"));
        badgeBgPaint.setStyle(Paint.Style.FILL);

        badgeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        badgeTextPaint.setColor(Color.WHITE);
        badgeTextPaint.setTextSize(16f);
        badgeTextPaint.setFakeBoldText(true);
        badgeTextPaint.setTextAlign(Paint.Align.CENTER);

        routePolylinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        routePolylinePaint.setColor(Color.parseColor("#0284C7"));
        routePolylinePaint.setStyle(Paint.Style.STROKE);
        routePolylinePaint.setStrokeWidth(10f);
        routePolylinePaint.setStrokeCap(Paint.Cap.ROUND);
        routePolylinePaint.setStrokeJoin(Paint.Join.ROUND);

        routeGlowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        routeGlowPaint.setColor(Color.parseColor("#4D0284C7"));
        routeGlowPaint.setStyle(Paint.Style.STROKE);
        routeGlowPaint.setStrokeWidth(18f);
        routeGlowPaint.setStrokeCap(Paint.Cap.ROUND);

        userPulsePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        userPulsePaint.setColor(Color.parseColor("#4000E676"));
        userPulsePaint.setStyle(Paint.Style.FILL);

        userCorePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        userCorePaint.setColor(Color.parseColor("#00C853"));
        userCorePaint.setStyle(Paint.Style.FILL);

        entranceDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        entranceDotPaint.setColor(Color.parseColor("#EA580C")); // Orange entrance indicator
        entranceDotPaint.setStyle(Paint.Style.FILL);

        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scaleFactor *= detector.getScaleFactor();
                scaleFactor = Math.max(0.6f, Math.min(scaleFactor, 3.5f));
                invalidate();
                return true;
            }
        });

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                handleTap(e.getX(), e.getY());
                return true;
            }
        });
    }

    public void setBuildings(List<CampusBuilding> buildings) {
        this.buildings = buildings != null ? buildings : new ArrayList<>();
        invalidate();
    }

    public void setSelectedBuilding(CampusBuilding building) {
        this.selectedBuilding = building;
        if (building != null) {
            // Animate view focus to building
            centerOnLocation(building.getLocation());
        }
        invalidate();
    }

    public void setCurrentRoute(CampusRoute route) {
        this.currentRoute = route;
        invalidate();
    }

    public void setUserLocation(LatLng loc) {
        this.userLocation = loc;
        invalidate();
    }

    public void setOnBuildingClickListener(OnBuildingClickListener listener) {
        this.listener = listener;
    }

    public void centerOnLocation(LatLng loc) {
        if (loc == null || getWidth() == 0 || getHeight() == 0) return;
        float targetX = projectLngToX(loc.longitude, getWidth());
        float targetY = projectLatToY(loc.latitude, getHeight());
        focusX = (getWidth() / 2f) - targetX * scaleFactor;
        focusY = (getHeight() / 2f) - targetY * scaleFactor;
        invalidate();
    }

    public void resetView() {
        scaleFactor = 1.0f;
        focusX = 0f;
        focusY = 0f;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        // Draw Background
        canvas.drawRect(0, 0, w, h, bgPaint);

        canvas.save();
        canvas.translate(focusX, focusY);
        canvas.scale(scaleFactor, scaleFactor, w / 2f, h / 2f);

        // 1. Draw Campus Perimeter Grounds & Lawns
        drawCampusGrounds(canvas, w, h);

        // 2. Draw Road Networks, Avenues, and Walkways
        drawRoadsAndWalkways(canvas, w, h);

        // 3. Draw Specialized Zones (Sports Track, Courts, Parking, Gardens)
        drawSpecialZones(canvas, w, h);

        // 4. Draw Active Navigation Route
        drawActiveRoute(canvas, w, h);

        // 5. Draw Campus Buildings
        drawBuildings(canvas, w, h);

        // 6. Draw User Location Beacon
        drawUserLocation(canvas, w, h);

        canvas.restore();

        // Trigger continuous subtle ripple animation for user pin
        postInvalidateDelayed(50);
    }

    private void drawCampusGrounds(Canvas canvas, int w, int h) {
        RectF campusBoundary = new RectF(w * 0.04f, h * 0.04f, w * 0.96f, h * 0.96f);
        canvas.drawRoundRect(campusBoundary, 32f, 32f, grassPaint);

        // Central Quadrangle Botanical Garden Lawns
        RectF centralQuad = new RectF(w * 0.38f, h * 0.42f, w * 0.60f, h * 0.58f);
        canvas.drawRoundRect(centralQuad, 24f, 24f, gardenPaint);

        // North Garden Zone
        RectF northGarden = new RectF(w * 0.38f, h * 0.20f, w * 0.60f, h * 0.32f);
        canvas.drawRoundRect(northGarden, 20f, 20f, gardenPaint);
    }

    private void drawRoadsAndWalkways(Canvas canvas, int w, int h) {
        // Main Campus Avenue: From Main Security Gate -> Cafeteria -> Quad -> Labs -> Sports
        roadPaint.setStrokeWidth(w * 0.040f);
        roadBorderPaint.setStrokeWidth(w * 0.050f);

        Path mainAvenue = new Path();
        mainAvenue.moveTo(w * 0.28f, h * 0.92f); // Gate
        mainAvenue.lineTo(w * 0.28f, h * 0.75f);
        mainAvenue.lineTo(w * 0.33f, h * 0.65f); // Cafeteria
        mainAvenue.lineTo(w * 0.42f, h * 0.52f); // Quad Hub
        mainAvenue.lineTo(w * 0.47f, h * 0.35f); // Computer Labs
        mainAvenue.lineTo(w * 0.67f, h * 0.29f); // Sports Arena

        canvas.drawPath(mainAvenue, roadBorderPaint);
        canvas.drawPath(mainAvenue, roadPaint);

        // East Loop: Connecting Administration & Auditorium back to Quad
        Path eastLoop = new Path();
        eastLoop.moveTo(w * 0.42f, h * 0.52f);
        eastLoop.lineTo(w * 0.55f, h * 0.44f); // Library
        eastLoop.lineTo(w * 0.62f, h * 0.61f); // Administration
        eastLoop.lineTo(w * 0.52f, h * 0.69f); // Auditorium
        eastLoop.lineTo(w * 0.33f, h * 0.65f); // Back to Cafeteria

        canvas.drawPath(eastLoop, roadBorderPaint);
        canvas.drawPath(eastLoop, roadPaint);

        // West Pathway: Direct connection to MCA Department
        Path westPath = new Path();
        westPath.moveTo(w * 0.33f, h * 0.65f);
        westPath.lineTo(w * 0.37f, h * 0.50f); // MCA Dept
        westPath.lineTo(w * 0.47f, h * 0.35f); // Labs

        canvas.drawPath(westPath, roadBorderPaint);
        canvas.drawPath(westPath, roadPaint);

        // Paved Pedestrian Walkways
        walkwayPaint.setStrokeWidth(w * 0.016f);
        Path walkways = new Path();
        walkways.moveTo(w * 0.37f, h * 0.50f); // MCA
        walkways.lineTo(w * 0.43f, h * 0.54f); // Main Academic
        walkways.lineTo(w * 0.55f, h * 0.44f); // Library
        walkways.lineTo(w * 0.47f, h * 0.35f); // Labs
        walkways.lineTo(w * 0.37f, h * 0.50f); // Loop back

        walkways.moveTo(w * 0.43f, h * 0.54f);
        walkways.lineTo(w * 0.52f, h * 0.69f); // Academic to Auditorium

        canvas.drawPath(walkways, walkwayPaint);
    }

    private void drawSpecialZones(Canvas canvas, int w, int h) {
        // 1. Sports Arena: Football pitch, Red Running Track, Courts
        RectF sportsArea = new RectF(w * 0.68f, h * 0.12f, w * 0.94f, h * 0.32f);
        canvas.drawRoundRect(sportsArea, 24f, 24f, sportsFieldPaint);

        trackPaint.setStrokeWidth(w * 0.016f);
        RectF trackRect = new RectF(w * 0.70f, h * 0.14f, w * 0.92f, h * 0.30f);
        canvas.drawRoundRect(trackRect, 20f, 20f, trackPaint);

        RectF courtRect = new RectF(w * 0.74f, h * 0.17f, w * 0.88f, h * 0.27f);
        canvas.drawRoundRect(courtRect, 8f, 8f, courtPaint);

        // 2. Parking & EV Charging Hub (South-West)
        RectF parkingRect = new RectF(w * 0.08f, h * 0.76f, w * 0.26f, h * 0.92f);
        canvas.drawRoundRect(parkingRect, 16f, 16f, parkingPaint);
        canvas.drawLine(w * 0.09f, h * 0.81f, w * 0.25f, h * 0.81f, parkingLinePaint);
        canvas.drawLine(w * 0.09f, h * 0.86f, w * 0.25f, h * 0.86f, parkingLinePaint);

        // 3. Central Botanical Garden Fountain (between Quad & Library)
        float fountainX = w * 0.49f;
        float fountainY = h * 0.50f;
        Paint fountainPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fountainPaint.setColor(Color.parseColor("#38BDF8"));
        canvas.drawCircle(fountainX, fountainY, w * 0.030f, fountainPaint);
        fountainPaint.setStyle(Paint.Style.STROKE);
        fountainPaint.setColor(Color.WHITE);
        fountainPaint.setStrokeWidth(3f);
        canvas.drawCircle(fountainX, fountainY, w * 0.030f, fountainPaint);
    }

    private void drawActiveRoute(Canvas canvas, int w, int h) {
        if (currentRoute == null || currentRoute.getPolylinePoints() == null || currentRoute.getPolylinePoints().size() < 2) {
            return;
        }

        Path routePath = new Path();
        List<LatLng> points = currentRoute.getPolylinePoints();
        for (int i = 0; i < points.size(); i++) {
            float px = projectLngToX(points.get(i).longitude, w);
            float py = projectLatToY(points.get(i).latitude, h);
            if (i == 0) {
                routePath.moveTo(px, py);
            } else {
                routePath.lineTo(px, py);
            }
        }

        canvas.drawPath(routePath, routeGlowPaint);
        canvas.drawPath(routePath, routePolylinePaint);
    }

    private void drawBuildings(Canvas canvas, int w, int h) {
        for (CampusBuilding b : buildings) {
            float bx = projectLngToX(b.getLocation().longitude, w);
            float by = projectLatToY(b.getLocation().latitude, h);
            float bw = w * 0.22f;
            float bh = h * 0.085f;

            RectF rect = new RectF(bx - bw / 2f, by - bh / 2f, bx + bw / 2f, by + bh / 2f);
            RectF shadowRect = new RectF(rect.left + 3f, rect.top + 5f, rect.right + 3f, rect.bottom + 5f);

            // Draw 3D Building Drop Shadow
            canvas.drawRoundRect(shadowRect, 14f, 14f, buildingShadowPaint);

            // Draw Building Base
            boolean isSelected = (selectedBuilding != null && selectedBuilding.getId() == b.getId());
            buildingBasePaint.setColor(isSelected ? Color.parseColor("#F0F9FF") : Color.WHITE);
            canvas.drawRoundRect(rect, 14f, 14f, buildingBasePaint);

            // Draw Category Color Top Bar
            int catColor = getCategoryColor(b.getCategory());
            Paint catBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            catBarPaint.setColor(catColor);
            RectF topBar = new RectF(rect.left, rect.top, rect.right, rect.top + 8f);
            canvas.drawRoundRect(topBar, 14f, 14f, catBarPaint);

            // Draw Outline
            if (isSelected) {
                canvas.drawRoundRect(rect, 14f, 14f, buildingSelectedPaint);
            } else {
                buildingStrokePaint.setColor(catColor);
                canvas.drawRoundRect(rect, 14f, 14f, buildingStrokePaint);
            }

            // Draw Entrance Marker Dot
            canvas.drawCircle(rect.centerX(), rect.bottom, 5f, entranceDotPaint);

            // Draw Building Name & Floor Count
            String shortName = getShortBuildingName(b.getName());
            canvas.drawText(shortName, rect.centerX(), rect.centerY() + 2f, textTitlePaint);
            canvas.drawText(b.getTotalFloors() + " Floors • " + b.getCategory(), rect.centerX(), rect.centerY() + 20f, textSubtitlePaint);

            // Draw Occupancy Badge
            drawOccupancyBadge(canvas, rect.centerX(), rect.top - 6f, b.getOccupancyRate());
        }
    }

    private void drawOccupancyBadge(Canvas canvas, float cx, float cy, int occ) {
        String text = occ + "%";
        float bw = 48f;
        float bh = 22f;
        RectF bRect = new RectF(cx - bw / 2f, cy - bh / 2f, cx + bw / 2f, cy + bh / 2f);

        int bColor = Color.parseColor("#16A34A"); // Low
        if (occ >= 75) bColor = Color.parseColor("#DC2626"); // High
        else if (occ >= 45) bColor = Color.parseColor("#EA580C"); // Moderate

        badgeBgPaint.setColor(bColor);
        canvas.drawRoundRect(bRect, 10f, 10f, badgeBgPaint);
        canvas.drawText(text, cx, cy + 5f, badgeTextPaint);
    }

    private void drawUserLocation(Canvas canvas, int w, int h) {
        if (userLocation == null) return;
        float ux = projectLngToX(userLocation.longitude, w);
        float uy = projectLatToY(userLocation.latitude, h);

        long time = SystemClock.uptimeMillis();
        float pulsePhase = (time % 1500) / 1500f; // 0.0 -> 1.0
        float maxRadius = 38f;
        float currentRadius = 12f + (maxRadius - 12f) * pulsePhase;
        int alpha = (int) (160 * (1f - pulsePhase));
        userPulsePaint.setAlpha(alpha);

        canvas.drawCircle(ux, uy, currentRadius, userPulsePaint);
        canvas.drawCircle(ux, uy, 12f, userCorePaint);

        Paint userRing = new Paint(Paint.ANTI_ALIAS_FLAG);
        userRing.setColor(Color.WHITE);
        userRing.setStyle(Paint.Style.STROKE);
        userRing.setStrokeWidth(3f);
        canvas.drawCircle(ux, uy, 12f, userRing);
    }

    private int getCategoryColor(String category) {
        if ("Labs".equalsIgnoreCase(category)) return Color.parseColor("#0284C7");
        if ("Library".equalsIgnoreCase(category)) return Color.parseColor("#7C3AED");
        if ("Canteen".equalsIgnoreCase(category)) return Color.parseColor("#EA580C");
        if ("Auditorium".equalsIgnoreCase(category)) return Color.parseColor("#DB2777");
        if ("Sports".equalsIgnoreCase(category)) return Color.parseColor("#16A34A");
        if ("Administration".equalsIgnoreCase(category)) return Color.parseColor("#D97706");
        return Color.parseColor("#2563EB");
    }

    private String getShortBuildingName(String name) {
        if (name.contains("Academic")) return "Academic Block";
        if (name.contains("Library")) return "Central Library";
        if (name.contains("MCA")) return "MCA Dept & Stage";
        if (name.contains("Computer") || name.contains("Labs")) return "Tech Labs 1-4";
        if (name.contains("Auditorium")) return "Main Auditorium";
        if (name.contains("Cafeteria") || name.contains("Dining")) return "Student Canteen";
        if (name.contains("Sports")) return "Sports Arena";
        if (name.contains("Admin")) return "Administration";
        return name;
    }

    private float projectLngToX(double lng, int width) {
        double clamped = Math.max(MIN_LNG, Math.min(lng, MAX_LNG));
        double pct = (clamped - MIN_LNG) / (MAX_LNG - MIN_LNG);
        return (float) (width * 0.08 + pct * width * 0.84);
    }

    private float projectLatToY(double lat, int height) {
        double clamped = Math.max(MIN_LAT, Math.min(lat, MAX_LAT));
        double pct = (clamped - MIN_LAT) / (MAX_LAT - MIN_LAT);
        return (float) (height * 0.92 - pct * height * 0.84);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = event.getX();
                lastTouchY = event.getY();
                isDragging = true;
                break;

            case MotionEvent.ACTION_MOVE:
                if (isDragging && !scaleGestureDetector.isInProgress()) {
                    float dx = event.getX() - lastTouchX;
                    float dy = event.getY() - lastTouchY;
                    focusX += dx;
                    focusY += dy;
                    lastTouchX = event.getX();
                    lastTouchY = event.getY();
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isDragging = false;
                break;
        }
        return true;
    }

    private void handleTap(float touchX, float touchY) {
        int w = getWidth();
        int h = getHeight();

        // Convert screen coordinates to canvas space
        float canvasX = (touchX - focusX - w / 2f) / scaleFactor + w / 2f;
        float canvasY = (touchY - focusY - h / 2f) / scaleFactor + h / 2f;

        for (CampusBuilding b : buildings) {
            float bx = projectLngToX(b.getLocation().longitude, w);
            float by = projectLatToY(b.getLocation().latitude, h);
            float bw = w * 0.16f;
            float bh = h * 0.12f;
            RectF rect = new RectF(bx - bw / 2f, by - bh / 2f, bx + bw / 2f, by + bh / 2f);

            if (rect.contains(canvasX, canvasY)) {
                this.selectedBuilding = b;
                invalidate();
                if (listener != null) {
                    listener.onBuildingClicked(b);
                }
                return;
            }
        }

        if (listener != null) {
            listener.onMapBackgroundClicked();
        }
    }
}
