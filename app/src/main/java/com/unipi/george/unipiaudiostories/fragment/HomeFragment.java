package com.unipi.george.unipiaudiostories.fragment;

import android.os.Bundle;
import android.util.Log;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;
import com.unipi.george.unipiaudiostories.R;
import com.unipi.george.unipiaudiostories.utils.StoryCardHelper;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment"; // Tag για logs
    private FirebaseFirestore db;
    private LinearLayout linearLayout; // LinearLayout για εμφάνιση δεδομένων δυναμικά

    public HomeFragment() {
        // κατασκευαστης
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Αρχικοποίηση του LinearLayout (προβολή δεδομένων σε λίστα)
        linearLayout = view.findViewById(R.id.linearLayoutData);

        db = FirebaseFirestore.getInstance(); // Αρχικοποίηση Firestore για πρόσβαση στα δεδομένα

        // Φόρτωση όλων των εγγράφων από τη συλλογή "stories"
        loadAllDocuments();

        return view;
    }


    private void loadAllDocuments() {
        db.collection("stories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Ανάκτηση δεδομένων από το έγγραφο
                            String title = document.getString("title");
                            String imageUrl = document.getString("imageURL");
                            String text = document.getString("text");
                            String author = document.getString("author");
                            String year = document.getString("year");
                            String documentId = document.getId();

                            // Δημιουργία και εμφάνιση των δεδομένων σαν card

                            if (isAdded() && getContext() != null) {
                                addDataToView(title, imageUrl, documentId, text, author, year);
                            } else {
                                Log.e("LibraryFragment", "Fragment is not attached!");
                            }

                        }
                    } else {
                        // αν αποτύχει η ανάκτηση
                        Log.w(TAG, "Error", task.getException());
                    }
                });
    }


     //Δημιουργεί και προσθέτει ένα δυναμικό CardView για κάθε έγγραφο.
     private void addDataToView(String title, String imageUrl, String documentId, String text, String author, String year) {
         if (!isAdded() || getContext() == null) {
             Log.e(TAG, "Fragment is not attached!");
             return;
         }

         // Δημιουργία CardView μέσω της νέας μεθόδου
         CardView cardView = StoryCardHelper.createStoryCard(getContext(), title, imageUrl, text, author, year, documentId, linearLayout);

         // Προσθήκη του CardView στο LinearLayout
         if (cardView != null) {
             linearLayout.addView(cardView);
         }
     }


}
