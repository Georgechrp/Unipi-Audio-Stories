package com.unipi.george.unipiaudiostories;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

        // Firebase Initialization
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // RecyclerView Setup
        recyclerView = findViewById(R.id.statisticsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        statisticsList = new ArrayList<>();
        adapter = new StatisticsAdapter(statisticsList);
        recyclerView.setAdapter(adapter);

        // Load data
        loadStatistics();
    }

    private void loadStatistics() {
        String userId = auth.getCurrentUser().getUid();

        firestore.collection("statistics").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        DocumentSnapshot document = task.getResult();
                        Map<String, Object> data = document.getData();
                        if (data != null) {
                            // Process listening data
                            Map<String, Object> listeningTime = (Map<String, Object>) data.get("listeningTime");
                            if (listeningTime != null) {
                                for (Map.Entry<String, Object> entry : listeningTime.entrySet()) {
                                    String documentId = entry.getKey();
                                    long time = ((Number) entry.getValue()).longValue();
                                    statisticsList.add(new StatisticItem(documentId, time));
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(this, "Failed to load statistics", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error loading statistics", task.getException());
                    }
                });
    }
}