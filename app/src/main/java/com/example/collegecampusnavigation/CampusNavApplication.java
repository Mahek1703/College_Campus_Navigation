package com.example.collegecampusnavigation;

import android.app.Application;
import android.util.Log;

public class CampusNavApplication extends Application {

    private static final String TAG = "CampusNavApp";

    @Override
    public void onCreate() {
        super.onCreate();

        // Global crash guard to prevent OS force-close popups from unhandled background threads
        final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Log.e(TAG, "Uncaught exception intercepted in thread " + thread.getName(), throwable);
            try {
                // If it's a security or location exception, recover gracefully
                if (throwable instanceof SecurityException || throwable instanceof IllegalArgumentException) {
                    Log.w(TAG, "Suppressed non-fatal permission/location exception: " + throwable.getMessage());
                    return;
                }
            } catch (Exception ignored) {}

            if (defaultHandler != null) {
                defaultHandler.uncaughtException(thread, throwable);
            }
        });
    }
}
