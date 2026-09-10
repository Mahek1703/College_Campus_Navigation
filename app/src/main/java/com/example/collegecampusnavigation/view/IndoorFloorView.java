package com.example.collegecampusnavigation.view;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.collegecampusnavigation.model.IndoorRoom;

import java.util.ArrayList;
import java.util.List;

public class IndoorFloorView extends View {

    public interface OnRoomClickListener {
        void onRoomClicked(IndoorRoom room);
    }

    private List<IndoorRoom> rooms = new ArrayList<>();
    private IndoorRoom selectedRoom;
    private List<PointF> navigationWaypoints = new ArrayList<>();
    private OnRoomClickListener roomClickListener;

    // Paints
    private Paint backgroundPaint;
    private Paint gridPaint;
    private Paint wallPaint;
    private Paint roomPaint;
    private Paint roomStrokePaint;
    private Paint selectedRoomPaint;
    private Paint textCodePaint;
    private Paint textNamePaint;
    private Paint pathPaint;
    private Paint pathMarkerPaint;
    private Paint userLocationPaint;

    // Touch & Zoom Transformations
    private float scaleFactor = 1.0f;
    private float posX = 0f;
    private float posY = 0f;
    private float lastTouchX;
    private float lastTouchY;
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    // Path Animation
    private float pathPhase = 0;
    private ValueAnimator pathAnimator;

    public IndoorFloorView(Context context) {
        super(context);
        init(context);
    }

    public IndoorFloorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public IndoorFloorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.parseColor("#0F172A")); // Blueprint navy

        gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#1E293B"));
        gridPaint.setStrokeWidth(1.5f);

        wallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wallPaint.setColor(Color.parseColor("#64748B"));
        wallPaint.setStyle(Paint.Style.STROKE);
        wallPaint.setStrokeWidth(6.0f);

        roomPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        roomPaint.setStyle(Paint.Style.FILL);

        roomStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        roomStrokePaint.setStyle(Paint.Style.STROKE);
        roomStrokePaint.setStrokeWidth(3.0f);
        roomStrokePaint.setColor(Color.parseColor("#334155"));

        selectedRoomPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectedRoomPaint.setStyle(Paint.Style.STROKE);
        selectedRoomPaint.setStrokeWidth(6.0f);
        selectedRoomPaint.setColor(Color.parseColor("#00E676")); // Vibrant green highlight

        textCodePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textCodePaint.setColor(Color.parseColor("#F8FAFC"));
        textCodePaint.setTextSize(26f);
        textCodePaint.setTextAlign(Paint.Align.CENTER);
        textCodePaint.setFakeBoldText(true);

        textNamePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textNamePaint.setColor(Color.parseColor("#94A3B8"));
        textNamePaint.setTextSize(18f);
        textNamePaint.setTextAlign(Paint.Align.CENTER);

        pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathPaint.setColor(Color.parseColor("#38BDF8")); // Cyan path
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeWidth(8.0f);
        pathPaint.setStrokeCap(Paint.Cap.ROUND);
        pathPaint.setStrokeJoin(Paint.Join.ROUND);

        pathMarkerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathMarkerPaint.setColor(Color.parseColor("#0284C7"));
        pathMarkerPaint.setStyle(Paint.Style.FILL);

        userLocationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        userLocationPaint.setColor(Color.parseColor("#10B981"));
        userLocationPaint.setStyle(Paint.Style.FILL);

        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        gestureDetector = new GestureDetector(context, new GestureListener());

        pathAnimator = ValueAnimator.ofFloat(0, 40);
        pathAnimator.setDuration(1200);
        pathAnimator.setRepeatCount(ValueAnimator.INFINITE);
        pathAnimator.addUpdateListener(anim -> {
            pathPhase = (float) anim.getAnimatedValue();
            invalidate();
        });
        pathAnimator.start();
    }

    public void setRooms(List<IndoorRoom> rooms) {
        this.rooms = rooms != null ? rooms : new ArrayList<>();
        this.selectedRoom = null;
        this.navigationWaypoints.clear();
        invalidate();
    }

    public void setSelectedRoom(IndoorRoom room) {
        this.selectedRoom = room;
        invalidate();
    }

    public void setNavigationWaypoints(List<PointF> waypoints) {
        this.navigationWaypoints = waypoints != null ? waypoints : new ArrayList<>();
        invalidate();
    }

    public void setOnRoomClickListener(OnRoomClickListener listener) {
        this.roomClickListener = listener;
    }

    public void resetZoom() {
        scaleFactor = 1.0f;
        posX = 0;
        posY = 0;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        if (width == 0 || height == 0) return;

        canvas.save();
        canvas.translate(posX, posY);
        canvas.scale(scaleFactor, scaleFactor, width / 2.0f, height / 2.0f);

        // Draw Blueprint Grid
        canvas.drawColor(Color.parseColor("#0F172A"));
        int gridSize = 50;
        for (int x = 0; x < width; x += gridSize) {
            canvas.drawLine(x, 0, x, height, gridPaint);
        }
        for (int y = 0; y < height; y += gridSize) {
            canvas.drawLine(0, y, width, y, gridPaint);
        }

        // Draw Outer Building Floor Boundary
        float marginX = width * 0.05f;
        float marginY = height * 0.08f;
        RectF outerRect = new RectF(marginX, marginY, width - marginX, height - marginY);
        canvas.drawRoundRect(outerRect, 24, 24, wallPaint);

        // Draw Hallway / Corridor Guidelines
        Paint corridorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        corridorPaint.setColor(Color.parseColor("#1E293B"));
        corridorPaint.setStyle(Paint.Style.FILL);
        RectF mainHallway = new RectF(width * 0.08f, height * 0.45f, width * 0.92f, height * 0.53f);
        canvas.drawRect(mainHallway, corridorPaint);

        // Draw Rooms
        for (IndoorRoom room : rooms) {
            float rx = width * (room.getXPercent() / 100.0f);
            float ry = height * (room.getYPercent() / 100.0f);
            float rw = width * (room.getWidthPercent() / 100.0f);
            float rh = height * (room.getHeightPercent() / 100.0f);

            RectF roomRect = new RectF(rx, ry, rx + rw, ry + rh);

            // Fill color based on room type
            roomPaint.setColor(getRoomColor(room.getRoomType()));
            canvas.drawRoundRect(roomRect, 12, 12, roomPaint);
            canvas.drawRoundRect(roomRect, 12, 12, roomStrokePaint);

            // Selected room highlight
            if (selectedRoom != null && selectedRoom.getId() == room.getId()) {
                canvas.drawRoundRect(roomRect, 12, 12, selectedRoomPaint);
            }

            // Room text labels
            float cx = roomRect.centerX();
            float cy = roomRect.centerY();
            canvas.drawText(room.getRoomCode(), cx, cy - 4, textCodePaint);
            if (room.getName() != null && room.getName().length() < 18) {
                canvas.drawText(room.getName(), cx, cy + 22, textNamePaint);
            }
        }

        // Draw Main Entrance Marker at Bottom
        float entX = width * 0.50f;
        float entY = height * 0.90f;
        userLocationPaint.setColor(Color.parseColor("#10B981"));
        canvas.drawCircle(entX, entY, 14, userLocationPaint);
        Paint entTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        entTextPaint.setColor(Color.parseColor("#10B981"));
        entTextPaint.setTextSize(20f);
        entTextPaint.setTextAlign(Paint.Align.CENTER);
        entTextPaint.setFakeBoldText(true);
        canvas.drawText("▼ MAIN ENTRANCE", entX, entY + 28, entTextPaint);

        // Draw Indoor Navigation Route Path if active
        if (navigationWaypoints != null && navigationWaypoints.size() > 1) {
            Path path = new Path();
            pathPaint.setPathEffect(new DashPathEffect(new float[]{24, 16}, pathPhase));

            PointF startP = navigationWaypoints.get(0);
            path.moveTo(width * (startP.x / 100.0f), height * (startP.y / 100.0f));

            for (int i = 1; i < navigationWaypoints.size(); i++) {
                PointF p = navigationWaypoints.get(i);
                float px = width * (p.x / 100.0f);
                float py = height * (p.y / 100.0f);
                path.lineTo(px, py);
                canvas.drawCircle(px, py, 7, pathMarkerPaint);
            }

            canvas.drawPath(path, pathPaint);

            // Destination pin circle
            PointF destP = navigationWaypoints.get(navigationWaypoints.size() - 1);
            Paint destPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            destPaint.setColor(Color.parseColor("#00E676"));
            canvas.drawCircle(width * (destP.x / 100.0f), height * (destP.y / 100.0f), 12, destPaint);
        }

        canvas.restore();
    }

    private int getRoomColor(String roomType) {
        if (roomType == null) return Color.parseColor("#1E293B");
        switch (roomType.toLowerCase()) {
            case "classroom": return Color.parseColor("#1E3A8A"); // Deep blue
            case "laboratory": return Color.parseColor("#134E4A"); // Deep teal
            case "restroom": return Color.parseColor("#581C87"); // Deep purple
            case "office": return Color.parseColor("#334155"); // Slate
            case "amenity": return Color.parseColor("#78350F"); // Amber
            case "elevator": return Color.parseColor("#065F46"); // Emerald
            case "stairs": return Color.parseColor("#374151"); // Grey
            default: return Color.parseColor("#1E293B");
        }
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
                break;

            case MotionEvent.ACTION_MOVE:
                if (!scaleGestureDetector.isInProgress()) {
                    float dx = event.getX() - lastTouchX;
                    float dy = event.getY() - lastTouchY;
                    posX += dx;
                    posY += dy;
                    invalidate();
                }
                lastTouchX = event.getX();
                lastTouchY = event.getY();
                break;
        }
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.6f, Math.min(scaleFactor, 3.5f));
            invalidate();
            return true;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            int width = getWidth();
            int height = getHeight();
            if (width == 0 || height == 0) return false;

            // Invert scale & translation to find canvas percentage coords
            float touchX = (e.getX() - posX - (width / 2.0f) * (1 - scaleFactor)) / scaleFactor;
            float touchY = (e.getY() - posY - (height / 2.0f) * (1 - scaleFactor)) / scaleFactor;

            float xPct = (touchX / width) * 100.0f;
            float yPct = (touchY / height) * 100.0f;

            for (IndoorRoom room : rooms) {
                if (xPct >= room.getXPercent() && xPct <= (room.getXPercent() + room.getWidthPercent()) &&
                    yPct >= room.getYPercent() && yPct <= (room.getYPercent() + room.getHeightPercent())) {
                    selectedRoom = room;
                    invalidate();
                    if (roomClickListener != null) {
                        roomClickListener.onRoomClicked(room);
                    }
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (pathAnimator != null) {
            pathAnimator.cancel();
        }
        super.onDetachedFromWindow();
    }
}
