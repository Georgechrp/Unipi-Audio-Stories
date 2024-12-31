package com.unipi.george.unipiaudiostories;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

public class StatisticItem {
    private final String documentId;
    private final long listeningTime;
    private FirebaseFirestore db;
    private String title;

    public StatisticItem(String documentId, long listeningTime) {
        this.documentId = documentId;
        this.listeningTime = listeningTime;
        this.db = FirebaseFirestore.getInstance();
    }

    public void fetchTitle(OnTitleFetchedListener listener) {
        db.collection("stories")
                .document(documentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        title = task.getResult().getString("title");
                        listener.onTitleFetched(title);
                    } else {
                        Log.w(TAG, "Error fetching document", task.getException());
                        listener.onTitleFetched(null);
                    }
                });
    }

    public String getDocumentId() {
        return documentId;
    }

    public long getListeningTime() {
        return listeningTime;
    }

    public interface OnTitleFetchedListener {
        void onTitleFetched(String title);
    }
}
