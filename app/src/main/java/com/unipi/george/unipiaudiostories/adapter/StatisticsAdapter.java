package com.unipi.george.unipiaudiostories.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.unipi.george.unipiaudiostories.R;
import com.unipi.george.unipiaudiostories.aboutStatistics.StatisticItem;

import java.util.List;

public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsAdapter.ViewHolder> {


    private final List<StatisticItem> statisticsList;


    public StatisticsAdapter(List<StatisticItem> statisticsList) {
        this.statisticsList = statisticsList;
    }

    // Δημιουργεί ένα νέο ViewHolder για ένα στοιχείο της λίστας
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistic, parent, false);
        return new ViewHolder(view);
    }

    // Συνδέει τα δεδομένα της λίστας με το ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Παίρνει το στοιχείο από τη λίστα στη συγκεκριμένη θέση
        StatisticItem item = statisticsList.get(position);

        // Εμφανίζει placeholder κείμενο αρχικά
        holder.titleTextView.setText("Loading title...");
        holder.timeTextView.setText("Listening Time: " + item.getListeningTime() + " seconds");
        holder.countTextView.setText("Listening Count: " + item.getListeningCount()); // Εμφάνιση listeningCount

        // Εναλλαγή χρώματος φόντου ανά γραμμή
        if (position % 2 == 0) {
            holder.cardView.setCardBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.light_gray));
        } else {
            holder.cardView.setCardBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.white));
        }

        // Ανάκτηση και ενημέρωση του τίτλου
        item.fetchTitle(title -> {
            if (title != null) {
                holder.titleTextView.setText("Story: " + title);
            } else {
                holder.titleTextView.setText("Error loading title");
            }
        });
    }

    // Επιστρέφει τον αριθμό των στοιχείων στη λίστα
    @Override
    public int getItemCount() {
        return statisticsList.size();
    }

    // ViewHolder: Κρατάει αναφορές στα views για ένα στοιχείο της λίστας
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView timeTextView;
        TextView countTextView;
        CardView cardView; 

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Αρχικοποίηση των views με βάση τα IDs τους
            titleTextView = itemView.findViewById(R.id.titleTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            countTextView = itemView.findViewById(R.id.countTextView);
            cardView = itemView.findViewById(R.id.statisticCardView);
        }
    }
}
