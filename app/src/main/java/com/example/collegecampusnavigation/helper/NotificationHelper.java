package com.example.collegecampusnavigation.helper;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import com.example.collegecampusnavigation.R;

public class NotificationHelper {

    private static final String CHANNEL_ID = "campus_nav_channel";
    private static final String CHANNEL_NAME = "Campus Navigation Alerts";
    private static final String SOS_CHANNEL_ID = "campus_sos_channel";
    private static final String SOS_CHANNEL_NAME = "Campus Emergency SOS";

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    public static void sendCampusNotification(Context context, String title, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    public static void sendEmergencySosNotification(Context context, String locationDetails) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    SOS_CHANNEL_ID,
                    SOS_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.enableVibration(true);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, SOS_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_sos)
                .setContentTitle("EMERGENCY SOS ACTIVE")
                .setContentText("Coordinates broadcasted to Campus Security: " + locationDetails)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Campus Security dispatched to: " + locationDetails))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(false);

        manager.notify(9999, builder.build());
    }
}