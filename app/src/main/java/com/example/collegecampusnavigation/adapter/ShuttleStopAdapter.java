package com.example.collegecampusnavigation.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collegecampusnavigation.R;
import com.example.collegecampusnavigation.model.ShuttleStop;

import java.util.List;

public class ShuttleStopAdapter extends RecyclerView.Adapter<ShuttleStopAdapter.StopViewHolder> {

    private List<ShuttleStop> stopList;
    private final OnStopClickListener listener;

    public interface OnStopClickListener {
        void onStopClick(ShuttleStop stop);
    }

    public ShuttleStopAdapter(List<ShuttleStop> stopList, OnStopClickListener listener) {
        this.stopList = stopList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shuttle_stop, parent, false);
        return new StopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StopViewHolder holder, int position) {
        ShuttleStop stop = stopList.get(position);
        holder.tvStopName.setText(stop.getName());
        holder.tvStopRoute.setText("🚌 " + stop.getRouteName());
        holder.tvStopCountdown.setText(stop.getEtaMinutes() + " MIN");
        holder.tvNextBusTime.setText("Following bus: " + stop.getNextBusTime());

        holder.itemView.setOnClickListener(v -> listener.onStopClick(stop));
    }

    @Override
    public int getItemCount() {
        return stopList != null ? stopList.size() : 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<ShuttleStop> newList) {
        this.stopList = newList;
        notifyDataSetChanged();
    }

    public static class StopViewHolder extends RecyclerView.ViewHolder {
        TextView tvStopName, tvStopRoute, tvStopCountdown, tvNextBusTime;

        public StopViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStopName = itemView.findViewById(R.id.tvStopName);
            tvStopRoute = itemView.findViewById(R.id.tvStopRoute);
            tvStopCountdown = itemView.findViewById(R.id.tvStopCountdown);
            tvNextBusTime = itemView.findViewById(R.id.tvNextBusTime);
        }
    }
}
