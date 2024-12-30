package com.unipi.george.unipiaudiostories;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
public class StatisticItem {
    private final String documentId;
    private final long listeningTime;
    private FirebaseFirestore db;
    private String title;

    public StatisticItem(String documentId, long listeningTime) {
        this.documentId = documentId;
        this.listeningTime = listeningTime;
        this.db = FirebaseFirestore.getInstance(); // Αρχικοποίηση της Firestore instance
    }

    public void fetchTitle(OnTitleFetchedListener listener) {
        db.collection("stories")
                .document(documentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        title = task.getResult().getString("title");
                        listener.onTitleFetched(title); // Επιστροφή του τίτλου μέσω callback
                    } else {
                        Log.w(TAG, "Error fetching document", task.getException());
                        listener.onTitleFetched(null); // Επιστροφή null σε περίπτωση αποτυχίας
                    }
                });

    }

    public String getDocumentId() {
        return documentId;
    }

    public long getListeningTime() {
        return listeningTime;
    }

    // Callback για την επιστροφή του τίτλου
    public interface OnTitleFetchedListener {
        void onTitleFetched(String title);
    }
}
