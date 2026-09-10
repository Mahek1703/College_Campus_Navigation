package com.example.collegecampusnavigation.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.collegecampusnavigation.model.IndoorRoom;

import java.util.ArrayList;
import java.util.List;

public class MiniMapView extends View {

    private List<IndoorRoom> rooms = new ArrayList<>();
    private IndoorRoom selectedRoom;
    private float userXPercent = 50f;
    private float userYPercent = 50f;
    private float cameraYaw = 0f; // degrees

    private Paint gridPaint;
    private Paint wallPaint;
    private Paint roomFillPaint;
    private Paint selectedRoomPaint;
    private Paint textPaint;
    private Paint radarPaint;
    private Paint userBeaconPaint;
    private Paint userDotPaint;

    private final RectF roomRect = new RectF();
    private final Path radarPath = new Path();

    public interface OnMiniMapRoomClickListener {
        void onMiniMapRoomClicked(IndoorRoom room);
    }

    private OnMiniMapRoomClickListener listener;

    public MiniMapView(Context context) {
        super(context);
        init();
    }

    public MiniMapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MiniMapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(Color.parseColor("#152438"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(1.5f);

        wallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wallPaint.setColor(Color.parseColor("#4FC3F7"));
        wallPaint.setStyle(Paint.Style.STROKE);
        wallPaint.setStrokeWidth(3f);

        roomFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        roomFillPaint.setColor(Color.parseColor("#1E293B"));
        roomFillPaint.setStyle(Paint.Style.FILL);

        selectedRoomPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectedRoomPaint.setColor(Color.parseColor("#3300E676"));
        selectedRoomPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#94A3B8"));
        textPaint.setTextSize(18f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);

        radarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        radarPaint.setColor(Color.parseColor("#4D00E676")); // Semi-transparent green radar cone
        radarPaint.setStyle(Paint.Style.FILL);

        userBeaconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        userBeaconPaint.setColor(Color.parseColor("#8000E676"));
        userBeaconPaint.setStyle(Paint.Style.STROKE);
        userBeaconPaint.setStrokeWidth(2.5f);

        userDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        userDotPaint.setColor(Color.parseColor("#00E676"));
        userDotPaint.setStyle(Paint.Style.FILL);
    }

    public void setOnMiniMapRoomClickListener(OnMiniMapRoomClickListener listener) {
        this.listener = listener;
    }

    public void setRooms(List<IndoorRoom> rooms) {
        this.rooms = rooms != null ? rooms : new ArrayList<>();
        invalidate();
    }

    public void setSelectedRoom(IndoorRoom room) {
        this.selectedRoom = room;
        if (room != null) {
            this.userXPercent = room.getCenterX();
            this.userYPercent = room.getCenterY();
        }
        invalidate();
    }

    public void setUserPosition(float xPercent, float yPercent) {
        this.userXPercent = xPercent;
        this.userYPercent = yPercent;
        invalidate();
    }

    public void setCameraYaw(float yawDegrees) {
        this.cameraYaw = yawDegrees;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        // Draw blueprint grid background
        int step = 24;
        for (int x = 0; x < w; x += step) {
            canvas.drawLine(x, 0, x, h, gridPaint);
        }
        for (int y = 0; y < h; y += step) {
            canvas.drawLine(0, y, w, y, gridPaint);
        }

        // Draw rooms
        for (IndoorRoom r : rooms) {
            float left = (r.getXPercent() / 100f) * w;
            float top = (r.getYPercent() / 100f) * h;
            float right = left + ((r.getWidthPercent() / 100f) * w);
            float bottom = top + ((r.getHeightPercent() / 100f) * h);

            roomRect.set(left, top, right, bottom);

            // Fill
            if (selectedRoom != null && selectedRoom.getId() == r.getId()) {
                canvas.drawRoundRect(roomRect, 8f, 8f, selectedRoomPaint);
                wallPaint.setColor(Color.parseColor("#00E676"));
            } else {
                canvas.drawRoundRect(roomRect, 8f, 8f, roomFillPaint);
                wallPaint.setColor(Color.parseColor("#4FC3F7"));
            }

            // Outline
            canvas.drawRoundRect(roomRect, 8f, 8f, wallPaint);

            // Code Label
            String code = r.getRoomCode();
            if (code.length() > 8) code = code.substring(0, 8);
            canvas.drawText(code, roomRect.centerX(), roomRect.centerY() + 6f, textPaint);
        }

        // Draw User Location Beacon & Dynamic Field-of-View Radar Cone
        float px = (userXPercent / 100f) * w;
        float py = (userYPercent / 100f) * h;

        // Dynamic Radar Cone (60 degrees FOV)
        float coneLength = Math.min(w, h) * 0.38f;
        float halfFov = 32f;
        float startAngleRad = (float) Math.toRadians(-cameraYaw - 90 - halfFov);
        float endAngleRad = (float) Math.toRadians(-cameraYaw - 90 + halfFov);

        radarPath.reset();
        radarPath.moveTo(px, py);
        radarPath.lineTo(
                px + coneLength * (float) Math.cos(startAngleRad),
                py + coneLength * (float) Math.sin(startAngleRad)
        );
        radarPath.lineTo(
                px + coneLength * (float) Math.cos(endAngleRad),
                py + coneLength * (float) Math.sin(endAngleRad)
        );
        radarPath.close();

        canvas.drawPath(radarPath, radarPaint);

        // Pulsing User Dot
        canvas.drawCircle(px, py, 14f, userBeaconPaint);
        canvas.drawCircle(px, py, 6f, userDotPaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && listener != null) {
            float touchXPercent = (event.getX() / getWidth()) * 100f;
            float touchYPercent = (event.getY() / getHeight()) * 100f;

            for (IndoorRoom r : rooms) {
                if (touchXPercent >= r.getXPercent() && touchXPercent <= (r.getXPercent() + r.getWidthPercent()) &&
                    touchYPercent >= r.getYPercent() && touchYPercent <= (r.getYPercent() + r.getHeightPercent())) {
                    listener.onMiniMapRoomClicked(r);
                    return true;
                }
            }
        }
        return true;
    }
}
