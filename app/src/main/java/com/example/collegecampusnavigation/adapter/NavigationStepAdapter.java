package com.example.collegecampusnavigation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collegecampusnavigation.R;
import com.example.collegecampusnavigation.model.NavigationStep;

import java.util.List;

public class NavigationStepAdapter extends RecyclerView.Adapter<NavigationStepAdapter.StepViewHolder> {

    private List<NavigationStep> steps;

    public NavigationStepAdapter(List<NavigationStep> steps) {
        this.steps = steps;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_navigation_step, parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        NavigationStep step = steps.get(position);
        holder.tvStepInstruction.setText(step.getInstruction());
        holder.tvStepHint.setText(step.getHint());
        holder.tvStepDistance.setText(step.getFormattedDistance());

        // Set maneuver icon
        switch (step.getManeuver()) {
            case RAMP:
                holder.ivManeuverIcon.setImageResource(R.drawable.ic_accessible);
                break;
            case ELEVATOR:
                holder.ivManeuverIcon.setImageResource(R.drawable.ic_elevator);
                break;
            case STAIRS:
                holder.ivManeuverIcon.setImageResource(R.drawable.ic_stairs);
                break;
            case ARRIVE:
                holder.ivManeuverIcon.setImageResource(R.drawable.ic_location);
                break;
            case TURN_LEFT:
            case SLIGHT_LEFT:
            case TURN_RIGHT:
            case SLIGHT_RIGHT:
            case STRAIGHT:
            case DEPART:
            default:
                holder.ivManeuverIcon.setImageResource(R.drawable.ic_walk);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return steps != null ? steps.size() : 0;
    }

    public static class StepViewHolder extends RecyclerView.ViewHolder {
        ImageView ivManeuverIcon;
        TextView tvStepInstruction, tvStepHint, tvStepDistance;

        public StepViewHolder(@NonNull View itemView) {
            super(itemView);
            ivManeuverIcon = itemView.findViewById(R.id.ivManeuverIcon);
            tvStepInstruction = itemView.findViewById(R.id.tvStepInstruction);
            tvStepHint = itemView.findViewById(R.id.tvStepHint);
            tvStepDistance = itemView.findViewById(R.id.tvStepDistance);
        }
    }
}
