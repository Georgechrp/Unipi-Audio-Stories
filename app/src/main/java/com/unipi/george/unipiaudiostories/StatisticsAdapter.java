package com.unipi.george.unipiaudiostories;

import static android.content.ContentValues.TAG;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

        // Πρώτα, δείξε έναν placeholder τίτλο
        holder.titleTextView.setText("Loading title...");
        holder.timeTextView.setText("Listening Time: " + item.getListeningTime() + " seconds");

        // Φόρτωσε τον τίτλο από τη βάση
        item.fetchTitle(title -> {
            if (title != null) {
                holder.titleTextView.setText("Story: " + title); // Ενημέρωση του UI
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
        }
    }
}
