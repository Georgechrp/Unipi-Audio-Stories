package com.unipi.george.unipiaudiostories.aboutStatistics;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

public class StatisticItem {
    private final String documentId;
    private final long listeningTime;
    private final int listeningCount;
    private FirebaseFirestore db;
    private String title;

    public StatisticItem(String documentId, long listeningTime, int listeningCount) {
        this.documentId = documentId; // Το μοναδικό ID του εγγράφου στο Firestore
        this.listeningTime = listeningTime; // Συνολικός χρόνος ακρόασης
        this.listeningCount = listeningCount; // Αριθμός φορών που αναπαράχθηκε η ιστορία
        this.db = FirebaseFirestore.getInstance(); // Αρχικοποίηση της σύνδεσης με το Firestore
    }

    // Μέθοδος για την ανάκτηση του τίτλου μιας ιστορίας από το Firestore
    public void fetchTitle(OnTitleFetchedListener listener) {
        db.collection("stories") // Ορισμός της συλλογής "stories" στο Firestore
                .document(documentId) // Αναφορά στο συγκεκριμένο έγγραφο μέσω του documentId
                .get() // Ανάκτηση του εγγράφου
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        title = task.getResult().getString("title"); // Ανάκτηση του πεδίου "title" από το έγγραφο
                        listener.onTitleFetched(title); // Επιστροφή του τίτλου μέσω του listener
                    } else {
                        Log.w(TAG, "Error fetching document", task.getException());
                        listener.onTitleFetched(null); // Επιστροφή null σε περίπτωση σφάλματος
                    }
                });
    }


    public String getDocumentId() {
        return documentId;
    }

    public long getListeningTime() {
        return listeningTime;
    }

    public int getListeningCount() {
        return listeningCount;
    }

    public interface OnTitleFetchedListener {
        void onTitleFetched(String title); // Μέθοδος που καλείται όταν ολοκληρωθεί η ανάκτηση του τίτλου
    }
}
