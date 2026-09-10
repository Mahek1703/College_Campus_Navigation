package com.example.collegecampusnavigation.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collegecampusnavigation.R;
import com.example.collegecampusnavigation.model.IndoorRoom;

import java.util.List;

public class IndoorRoomAdapter extends RecyclerView.Adapter<IndoorRoomAdapter.RoomViewHolder> {

    private List<IndoorRoom> roomList;
    private final OnRoomClickListener listener;

    public interface OnRoomClickListener {
        void onRoomSelected(IndoorRoom room);
    }

    public IndoorRoomAdapter(List<IndoorRoom> roomList, OnRoomClickListener listener) {
        this.roomList = roomList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_indoor_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        IndoorRoom room = roomList.get(position);
        holder.tvRoomCode.setText(room.getRoomCode());
        holder.tvRoomType.setText(room.getRoomType());
        holder.tvRoomName.setText(room.getName());
        holder.tvRoomBuildingFloor.setText("🏢 " + room.getBuildingName() + " • Floor " + (room.getFloor() == 0 ? "G" : room.getFloor()));

        holder.itemView.setOnClickListener(v -> listener.onRoomSelected(room));
    }

    @Override
    public int getItemCount() {
        return roomList != null ? roomList.size() : 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<IndoorRoom> newList) {
        this.roomList = newList;
        notifyDataSetChanged();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomCode, tvRoomType, tvRoomName, tvRoomBuildingFloor;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomCode = itemView.findViewById(R.id.tvRoomCode);
            tvRoomType = itemView.findViewById(R.id.tvRoomType);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvRoomBuildingFloor = itemView.findViewById(R.id.tvRoomBuildingFloor);
        }
    }
}
