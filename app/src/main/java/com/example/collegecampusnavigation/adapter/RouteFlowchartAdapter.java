package com.example.collegecampusnavigation.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collegecampusnavigation.R;
import com.example.collegecampusnavigation.model.VisualNavStep;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class RouteFlowchartAdapter extends RecyclerView.Adapter<RouteFlowchartAdapter.NodeViewHolder> {

    public interface OnNodeClickListener {
        void onNodeClick(int stepIndex);
    }

    private final List<VisualNavStep> steps;
    private final int currentStepIndex;
    private final OnNodeClickListener listener;

    public RouteFlowchartAdapter(List<VisualNavStep> steps, int currentStepIndex, OnNodeClickListener listener) {
        this.steps = steps;
        this.currentStepIndex = currentStepIndex;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_route_flowchart_node, parent, false);
        return new NodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NodeViewHolder holder, int position) {
        VisualNavStep step = steps.get(position);
        holder.tvNodeNumber.setText(String.valueOf(step.getStepNumber()));
        holder.tvNodeTitle.setText(step.getTitle());
        holder.tvNodeInstruction.setText(step.getInstruction());
        holder.tvNodeDirectionIcon.setText(step.getDirectionIconSymbol());
        holder.tvNodeFloorBadge.setText(step.getFloorLevel());
        holder.tvNodeDistance.setText("• " + step.getDistanceMeters() + "m");

        if (step.getImageResId() != 0) {
            holder.ivNodePhoto.setImageResource(step.getImageResId());
        }

        // Hide bottom vertical line for the last node
        if (position == steps.size() - 1) {
            holder.viewTimelineLine.setVisibility(View.INVISIBLE);
        } else {
            holder.viewTimelineLine.setVisibility(View.VISIBLE);
        }

        // Highlight current step
        if (position == currentStepIndex) {
            holder.cardNode.setStrokeColor(Color.parseColor("#2563EB"));
            holder.cardNode.setStrokeWidth(4);
            holder.cardNode.setCardBackgroundColor(Color.parseColor("#EFF6FF"));
            holder.tvNodeNumber.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#2563EB")));
        } else if (position < currentStepIndex) {
            // Already passed
            holder.cardNode.setStrokeColor(Color.parseColor("#E2E8F0"));
            holder.cardNode.setStrokeWidth(2);
            holder.cardNode.setCardBackgroundColor(Color.parseColor("#F8FAFC"));
            holder.tvNodeNumber.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#10B981"))); // Green completed
        } else {
            // Upcoming
            holder.cardNode.setStrokeColor(Color.parseColor("#E2E8F0"));
            holder.cardNode.setStrokeWidth(2);
            holder.cardNode.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.tvNodeNumber.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#64748B")));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNodeClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return steps != null ? steps.size() : 0;
    }

    static class NodeViewHolder extends RecyclerView.ViewHolder {
        TextView tvNodeNumber, tvNodeTitle, tvNodeInstruction, tvNodeDirectionIcon, tvNodeFloorBadge, tvNodeDistance;
        ImageView ivNodePhoto;
        View viewTimelineLine;
        MaterialCardView cardNode;

        public NodeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNodeNumber = itemView.findViewById(R.id.tvNodeNumber);
            tvNodeTitle = itemView.findViewById(R.id.tvNodeTitle);
            tvNodeInstruction = itemView.findViewById(R.id.tvNodeInstruction);
            tvNodeDirectionIcon = itemView.findViewById(R.id.tvNodeDirectionIcon);
            tvNodeFloorBadge = itemView.findViewById(R.id.tvNodeFloorBadge);
            tvNodeDistance = itemView.findViewById(R.id.tvNodeDistance);
            ivNodePhoto = itemView.findViewById(R.id.ivNodePhoto);
            viewTimelineLine = itemView.findViewById(R.id.viewTimelineLine);
            cardNode = itemView.findViewById(R.id.cardNode);
        }
    }
}
