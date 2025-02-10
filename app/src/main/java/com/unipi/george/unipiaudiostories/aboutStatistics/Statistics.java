package com.unipi.george.unipiaudiostories.aboutStatistics;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unipi.george.unipiaudiostories.R;
import com.unipi.george.unipiaudiostories.adapter.StatisticsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Statistics extends AppCompatActivity {

    private static final String TAG = "Statistics";
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    private StatisticsAdapter adapter;
    private List<StatisticItem> statisticsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // Αρχικοποίηση Firebase (Firestore και Auth)
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Ρύθμιση του RecyclerView
        recyclerView = findViewById(R.id.statisticsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        statisticsList = new ArrayList<>();
        adapter = new StatisticsAdapter(statisticsList); // Σύνδεση του adapter με τη λίστα
        recyclerView.setAdapter(adapter); // Ορισμός του adapter στο RecyclerView

        // Φόρτωση στατιστικών από τη βάση δεδομένων
        loadStatistics();
    }

    private void loadStatistics() {
        String userId = auth.getCurrentUser().getUid();

        firestore.collection("statistics").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        // Ελέγχουμε αν τα δεδομένα υπάρχουν και επεξεργαζόμαστε το αποτέλεσμα
                        DocumentSnapshot document = task.getResult();
                        Map<String, Object> data = document.getData();
                        if (data != null) {
                            // Ανάκτηση δεδομένων για το χρόνο ακρόασης και τον αριθμό ακροάσεων
                            Map<String, Object> listeningTime = (Map<String, Object>) data.get("listeningTime");
                            Map<String, Object> listeningCount = (Map<String, Object>) data.get("listeningCount");

                            if (listeningTime != null && listeningCount != null) {
                                // Επεξεργασία των δεδομένων και προσθήκη στη λίστα στατιστικών
                                for (String documentId : listeningTime.keySet()) {
                                    long time = ((Number) listeningTime.get(documentId)).longValue(); // Μετατροπή σε long
                                    int count = listeningCount.containsKey(documentId) ?
                                            ((Number) listeningCount.get(documentId)).intValue() : 0;

                                    statisticsList.add(new StatisticItem(documentId, time, count));
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        // Εμφάνιση μηνύματος σφάλματος στον χρήστη
                        Toast.makeText(this, "Failed to load statistics", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error loading statistics", task.getException());
                    }
                });
    }
}
