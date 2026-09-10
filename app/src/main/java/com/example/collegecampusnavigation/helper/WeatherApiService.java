package com.example.collegecampusnavigation.helper;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class WeatherApiService {

    public interface WeatherCallback {
        void onSuccess(String weatherSummary);
        void onError(String error);
    }

    public static void fetchCampusWeather(double lat, double lng, WeatherCallback callback) {
        new Thread(() -> {
            try {
                String urlString = "https://wttr.in/" + lat + "," + lng + "?format=j1";
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                InputStream inputStream = conn.getInputStream();
                Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";

                JSONObject jsonObject = new JSONObject(response);
                JSONObject currentCondition = jsonObject.getJSONArray("current_condition").getJSONObject(0);
                String tempC = currentCondition.getString("temp_C");
                String desc = currentCondition.getJSONArray("weatherDesc").getJSONObject(0).getString("value");

                String result = "Campus Weather: " + tempC + "°C, " + desc;

                new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(result));

            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError("Weather unavailable"));
            }
        }).start();
    }
}