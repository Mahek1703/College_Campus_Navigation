package com.example.collegecampusnavigation.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collegecampusnavigation.R;
import com.example.collegecampusnavigation.adapter.ScheduleAdapter;
import com.example.collegecampusnavigation.helper.DatabaseHelper;
import com.example.collegecampusnavigation.model.ClassScheduleItem;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@SuppressLint("SetTextI18n")
public class ClassScheduleActivity extends AppCompatActivity implements ScheduleAdapter.OnScheduleClickListener {

    private TabLayout tabLayoutDays;
    private RecyclerView rvSchedule;
    private TextView tvInterClassSummary;
    private ScheduleAdapter scheduleAdapter;

    private final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private String currentSelectedDay = "Monday";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_schedule);

        MaterialToolbar toolbar = findViewById(R.id.toolbarSchedule);
        toolbar.setNavigationOnClickListener(v -> finish());

        tabLayoutDays = findViewById(R.id.tabLayoutDays);
        rvSchedule = findViewById(R.id.rvSchedule);
        tvInterClassSummary = findViewById(R.id.tvInterClassSummary);

        rvSchedule.setLayoutManager(new LinearLayoutManager(this));
        scheduleAdapter = new ScheduleAdapter(new ArrayList<>(), this);
        rvSchedule.setAdapter(scheduleAdapter);

        for (String day : days) {
            tabLayoutDays.addTab(tabLayoutDays.newTab().setText(day.substring(0, 3)));
        }

        // Determine current day of week
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int selectedTabIndex = 0;
        switch (dayOfWeek) {
            case Calendar.TUESDAY: selectedTabIndex = 1; break;
            case Calendar.WEDNESDAY: selectedTabIndex = 2; break;
            case Calendar.THURSDAY: selectedTabIndex = 3; break;
            case Calendar.FRIDAY: selectedTabIndex = 4; break;
            default: selectedTabIndex = 0; break;
        }

        TabLayout.Tab targetTab = tabLayoutDays.getTabAt(selectedTabIndex);
        if (targetTab != null) {
            targetTab.select();
        }
        currentSelectedDay = days[selectedTabIndex];

        tabLayoutDays.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                if (pos >= 0 && pos < days.length) {
                    currentSelectedDay = days[pos];
                    loadScheduleForDay(currentSelectedDay);
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        loadScheduleForDay(currentSelectedDay);
    }

    private void loadScheduleForDay(String day) {
        List<ClassScheduleItem> items;
        try (DatabaseHelper db = new DatabaseHelper(this)) {
            items = db.getScheduleForDay(day);
        }

        scheduleAdapter.updateList(items);

        if (items.isEmpty()) {
            tvInterClassSummary.setText("No classes scheduled for " + day + ". Enjoy your free study or project time!");
        } else if (items.size() == 1) {
            tvInterClassSummary.setText("Single class scheduled today (" + items.get(0).getCourseCode() + " at " + items.get(0).getBuildingName() + ").");
        } else {
            ClassScheduleItem c1 = items.get(0);
            ClassScheduleItem c2 = items.get(1);
            int gap = c2.getStartTotalMinutes() - c1.getEndTotalMinutes();
            if (gap >= 0) {
                tvInterClassSummary.setText("⚡ Inter-Class Transition: " + gap + " min break between " + c1.getCourseCode() + " and " + c2.getCourseCode() + ".\nDirect walk: ~4 mins. Recommendation: " + (gap > 15 ? "Time for quick coffee at Student Union!" : "Proceed directly to " + c2.getBuildingName()));
            } else {
                tvInterClassSummary.setText("⚠️ Consecutive classes with zero gap. Use express pedestrian walkway.");
            }
        }
    }

    @Override
    public void onClassNavigate(ClassScheduleItem item) {
        // Return selected class info to MainActivity or launch IndoorMapActivity
        Intent intent = new Intent(this, IndoorMapActivity.class);
        intent.putExtra("BUILDING_ID", item.getBuildingId());
        intent.putExtra("BUILDING_NAME", item.getBuildingName());
        intent.putExtra("TARGET_ROOM", item.getRoomCode());
        intent.putExtra("FLOOR", item.getFloor());
        startActivity(intent);

        Toast.makeText(this, "Opening navigation to " + item.getCourseCode() + " in " + item.getBuildingName(), Toast.LENGTH_SHORT).show();
    }
}
