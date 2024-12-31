package com.unipi.george.unipiaudiostories;

import static android.content.ContentValues.TAG;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsAdapter.ViewHolder> {

    private final List<StatisticItem> statisticsList;

    public StatisticsAdapter(List<StatisticItem> statisticsList) {
        this.statisticsList = statisticsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StatisticItem item = statisticsList.get(position);

        // Display placeholder text initially
        holder.titleTextView.setText("Loading title...");
        holder.timeTextView.setText("Listening Time: " + item.getListeningTime() + " seconds");

        // Alternate background color for rows
        if (position % 2 == 0) {
            holder.cardView.setCardBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.light_gray));
        } else {
            holder.cardView.setCardBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.white));
        }

        // Fetch and update title asynchronously
        item.fetchTitle(title -> {
            if (title != null) {
                holder.titleTextView.setText("Story: " + title);
            } else {
                holder.titleTextView.setText("Error loading title");
            }
        });
    }

    @Override
    public int getItemCount() {
        return statisticsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView timeTextView;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            cardView = itemView.findViewById(R.id.statisticCardView);
        }
    }
}