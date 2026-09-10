package com.example.collegecampusnavigation.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collegecampusnavigation.R;
import com.example.collegecampusnavigation.model.PointOfInterest;

import java.util.List;

public class PoiAdapter extends RecyclerView.Adapter<PoiAdapter.PoiViewHolder> {

    private List<PointOfInterest> poiList;
    private final OnPoiClickListener listener;

    public interface OnPoiClickListener {
        void onPoiClick(PointOfInterest poi);
        void onPoiNavigate(PointOfInterest poi);
    }

    public PoiAdapter(List<PointOfInterest> poiList, OnPoiClickListener listener) {
        this.poiList = poiList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PoiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_poi, parent, false);
        return new PoiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PoiViewHolder holder, int position) {
        PointOfInterest poi = poiList.get(position);
        holder.tvPoiName.setText(poi.getName());
        holder.tvPoiCategory.setText(poi.getCategory());
        holder.tvPoiLocation.setText("📍 " + poi.getBuildingName() + " (" + poi.getFloorDisplay() + ")\n✨ " + poi.getFeatures());

        if (poi.getImageResId() != 0) {
            holder.ivPhoto.setImageResource(poi.getImageResId());
        } else {
            holder.ivPhoto.setImageResource(R.drawable.ic_building_academic);
        }

        int occ = poi.getOccupancyRate();
        holder.tvPoiStatus.setText("● " + poi.getStatusText() + " (" + occ + "% Busy)");
        if (occ < 40) {
            holder.tvPoiStatus.setTextColor(Color.parseColor("#2E7D32"));
        } else if (occ < 75) {
            holder.tvPoiStatus.setTextColor(Color.parseColor("#EF6C00"));
        } else {
            holder.tvPoiStatus.setTextColor(Color.parseColor("#C62828"));
        }

        holder.itemView.setOnClickListener(v -> listener.onPoiClick(poi));
        holder.btnPoiRoute.setOnClickListener(v -> listener.onPoiNavigate(poi));
    }

    @Override
    public int getItemCount() {
        return poiList != null ? poiList.size() : 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<PointOfInterest> newList) {
        this.poiList = newList;
        notifyDataSetChanged();
    }

    public static class PoiViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        TextView tvPoiName, tvPoiCategory, tvPoiLocation, tvPoiStatus;
        Button btnPoiRoute;

        public PoiViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPoiPhoto);
            tvPoiName = itemView.findViewById(R.id.tvPoiName);
            tvPoiCategory = itemView.findViewById(R.id.tvPoiCategory);
            tvPoiLocation = itemView.findViewById(R.id.tvPoiLocation);
            tvPoiStatus = itemView.findViewById(R.id.tvPoiStatus);
            btnPoiRoute = itemView.findViewById(R.id.btnPoiRoute);
        }
    }
}
