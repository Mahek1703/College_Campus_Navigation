package com.example.collegecampusnavigation.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collegecampusnavigation.R;
import com.example.collegecampusnavigation.model.ClassScheduleItem;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<ClassScheduleItem> scheduleList;
    private final OnScheduleClickListener listener;

    public interface OnScheduleClickListener {
        void onClassNavigate(ClassScheduleItem item);
    }

    public ScheduleAdapter(List<ClassScheduleItem> scheduleList, OnScheduleClickListener listener) {
        this.scheduleList = scheduleList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        ClassScheduleItem item = scheduleList.get(position);
        holder.tvCourseCode.setText(item.getCourseCode());
        holder.tvCourseName.setText(item.getCourseName());
        holder.tvCourseCategory.setText(item.getCategory());
        holder.tvCourseTime.setText(item.getStartTime() + " - " + item.getEndTime());
        holder.tvCourseLocation.setText("📍 " + item.getBuildingName() + " (Room " + item.getRoomCode() + ", Floor " + item.getFloor() + ")");
        holder.tvInstructor.setText("👤 " + item.getInstructor());

        // Inter-Class Transition Calculation
        if (position < scheduleList.size() - 1) {
            ClassScheduleItem nextItem = scheduleList.get(position + 1);
            int currentEnd = item.getEndTotalMinutes();
            int nextStart = nextItem.getStartTotalMinutes();
            int gapMinutes = nextStart - currentEnd;

            if (gapMinutes > 0) {
                if (gapMinutes <= 15) {
                    holder.tvInterClassStatus.setText("⚡ " + gapMinutes + " min gap to next class (" + nextItem.getCourseCode() + ") • 4 min walk");
                    holder.tvInterClassStatus.setTextColor(Color.parseColor("#E65100"));
                } else {
                    holder.tvInterClassStatus.setText("☕ " + gapMinutes + " min break before next class • Optimal coffee/study window");
                    holder.tvInterClassStatus.setTextColor(Color.parseColor("#2E7D32"));
                }
            } else {
                holder.tvInterClassStatus.setText("⚠️ Back-to-back class with " + nextItem.getCourseCode());
                holder.tvInterClassStatus.setTextColor(Color.parseColor("#C62828"));
            }
        } else {
            holder.tvInterClassStatus.setText("🎉 Last scheduled class for today");
            holder.tvInterClassStatus.setTextColor(Color.parseColor("#1565C0"));
        }

        holder.btnNavigateClass.setOnClickListener(v -> listener.onClassNavigate(item));
    }

    @Override
    public int getItemCount() {
        return scheduleList != null ? scheduleList.size() : 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<ClassScheduleItem> newList) {
        this.scheduleList = newList;
        notifyDataSetChanged();
    }

    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseCode, tvCourseName, tvCourseCategory, tvCourseTime, tvCourseLocation, tvInstructor, tvInterClassStatus;
        Button btnNavigateClass;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseCode = itemView.findViewById(R.id.tvCourseCode);
            tvCourseName = itemView.findViewById(R.id.tvCourseName);
            tvCourseCategory = itemView.findViewById(R.id.tvCourseCategory);
            tvCourseTime = itemView.findViewById(R.id.tvCourseTime);
            tvCourseLocation = itemView.findViewById(R.id.tvCourseLocation);
            tvInstructor = itemView.findViewById(R.id.tvInstructor);
            tvInterClassStatus = itemView.findViewById(R.id.tvInterClassStatus);
            btnNavigateClass = itemView.findViewById(R.id.btnNavigateClass);
        }
    }
}
