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
import com.example.collegecampusnavigation.model.CampusCardItem;

import java.util.List;

public class CampusCardAdapter extends RecyclerView.Adapter<CampusCardAdapter.CardViewHolder> {

    private List<CampusCardItem> items;
    private final OnCardActionListener listener;

    public interface OnCardActionListener {
        void onCardSelected(CampusCardItem item);
        void onNavigateRequested(CampusCardItem item);
        void onIndoorRequested(CampusCardItem item);
        void onPhotoZoomRequested(CampusCardItem item);
    }

    public CampusCardAdapter(List<CampusCardItem> items, OnCardActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_campus_card, parent, false);
        view.setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CampusCardItem item = items.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvSubtitle.setText(item.getSubtitle());
        holder.tvCategoryBadge.setText(item.getCategoryTag());
        holder.tvOccupancyBadge.setText("● " + item.getOccupancyRate() + "% Traffic");

        if (item.getImageResId() != 0) {
            holder.ivPhoto.setImageResource(item.getImageResId());
        } else {
            holder.ivPhoto.setImageResource(R.drawable.ic_building_academic);
        }

        // Occupancy indicator color
        if (item.getOccupancyRate() < 45) {
            holder.tvOccupancyBadge.setTextColor(Color.parseColor("#00E676")); // Accent green
        } else if (item.getOccupancyRate() < 75) {
            holder.tvOccupancyBadge.setTextColor(Color.parseColor("#FFB74D")); // Amber
        } else {
            holder.tvOccupancyBadge.setTextColor(Color.parseColor("#FF5252")); // Red
        }

        holder.itemView.setOnClickListener(v -> listener.onCardSelected(item));
        holder.btnNavigate.setOnClickListener(v -> listener.onNavigateRequested(item));
        holder.btnIndoor.setOnClickListener(v -> listener.onIndoorRequested(item));
        holder.btnZoom.setOnClickListener(v -> listener.onPhotoZoomRequested(item));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<CampusCardItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto, btnZoom;
        TextView tvCategoryBadge, tvOccupancyBadge, tvTitle, tvSubtitle;
        Button btnNavigate, btnIndoor;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivCampusCardPhoto);
            btnZoom = itemView.findViewById(R.id.btnCardZoomPhoto);
            tvCategoryBadge = itemView.findViewById(R.id.tvCardCategoryBadge);
            tvOccupancyBadge = itemView.findViewById(R.id.tvCardOccupancyBadge);
            tvTitle = itemView.findViewById(R.id.tvCampusCardTitle);
            tvSubtitle = itemView.findViewById(R.id.tvCampusCardSubtitle);
            btnNavigate = itemView.findViewById(R.id.btnCardNavigate);
            btnIndoor = itemView.findViewById(R.id.btnCardIndoor);
        }
    }
}
