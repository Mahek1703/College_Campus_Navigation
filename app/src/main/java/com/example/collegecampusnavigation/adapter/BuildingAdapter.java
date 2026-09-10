package com.example.collegecampusnavigation.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collegecampusnavigation.R;
import com.example.collegecampusnavigation.model.CampusBuilding;

import java.util.List;

public class BuildingAdapter extends RecyclerView.Adapter<BuildingAdapter.BuildingViewHolder> {

    private List<CampusBuilding> buildingList;
    private final OnBuildingClickListener listener;

    public interface OnBuildingClickListener {
        void onBuildingClick(CampusBuilding building);
    }

    public BuildingAdapter(List<CampusBuilding> buildingList, OnBuildingClickListener listener) {
        this.buildingList = buildingList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BuildingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_building, parent, false);
        return new BuildingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BuildingViewHolder holder, int position) {
        CampusBuilding building = buildingList.get(position);
        holder.tvItemName.setText(building.getName());
        holder.tvItemCategory.setText(building.getCategory());
        holder.tvItemDesc.setText(building.getDescription());
        holder.tvItemHours.setText("⏰ " + building.getOpeningHours() + " • " + building.getTotalFloors() + " Floors");

        if (building.getImageResId() != 0) {
            holder.ivPhoto.setImageResource(building.getImageResId());
        } else {
            holder.ivPhoto.setImageResource(R.drawable.ic_building_academic);
        }

        int occ = building.getOccupancyRate();
        holder.tvItemOccupancy.setText("● " + building.getOccupancyStatusText());
        if (occ < 40) {
            holder.tvItemOccupancy.setTextColor(Color.parseColor("#2E7D32"));
        } else if (occ < 75) {
            holder.tvItemOccupancy.setTextColor(Color.parseColor("#EF6C00"));
        } else {
            holder.tvItemOccupancy.setTextColor(Color.parseColor("#C62828"));
        }

        holder.itemView.setOnClickListener(v -> listener.onBuildingClick(building));
    }

    @Override
    public int getItemCount() {
        return buildingList != null ? buildingList.size() : 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<CampusBuilding> newList) {
        this.buildingList = newList;
        notifyDataSetChanged();
    }

    public static class BuildingViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        TextView tvItemName, tvItemCategory, tvItemDesc, tvItemHours, tvItemOccupancy;

        public BuildingViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivItemBuildingPhoto);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemCategory = itemView.findViewById(R.id.tvItemCategory);
            tvItemDesc = itemView.findViewById(R.id.tvItemDesc);
            tvItemHours = itemView.findViewById(R.id.tvItemHours);
            tvItemOccupancy = itemView.findViewById(R.id.tvItemOccupancy);
        }
    }
}