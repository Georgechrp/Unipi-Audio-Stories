package com.unipi.george.unipiaudiostories;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide; // Για να φορτώσεις εικόνες από URL
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.Set;

public class LibraryFragment extends Fragment {

    private static final String TAG = "HomeFragment"; // Tag για logs
    private FirebaseFirestore db;
    private LinearLayout linearLayout; // Για εμφάνιση δεδομένων

    public LibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate το layout για το fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Αρχικοποίηση του LinearLayout
        linearLayout = view.findViewById(R.id.linearLayoutData);

        // Αρχικοποίηση Firestore
        db = FirebaseFirestore.getInstance();

        // Φόρτωση όλων των documents από τη συλλογή "stories"
        //loadAllDocuments();
        loadDownloadedStories();
        return view;
    }
    private void loadDownloadedStories() {
        // Ανάκτηση των κατεβασμένων ιστοριών από τα SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("DownloadedStories", Context.MODE_PRIVATE);
        Set<String> downloadedIds = sharedPreferences.getStringSet("downloadedIds", new HashSet<>());

        // Αν δεν υπάρχουν κατεβασμένες ιστορίες, τότε δεν κάνουμε τίποτα
        if (downloadedIds.isEmpty()) {
            Toast.makeText(getContext(), "Δεν υπάρχουν κατεβασμένες ιστορίες", Toast.LENGTH_SHORT).show();
            return;
        }

        // Φόρτωση των ιστοριών από το Firestore
        db.collection("stories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String documentId = document.getId(); // Λήψη του documentId

                            // Έλεγχος αν το documentId είναι μέσα στη λίστα των κατεβασμένων ιστοριών
                            if (downloadedIds.contains(documentId)) {
                                String title = document.getString("title");
                                String imageUrl = document.getString("imageURL");

                                // Προσθήκη στην προβολή μόνο αν είναι κατεβασμένο
                                addDataToView(title, imageUrl, documentId);
                            }
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    private void addDataToView(String title, String imageUrl, String documentId) {
        // Δημιουργία TextView για τον τίτλο
        TextView textView = new TextView(getContext());
        textView.setText(title);
        textView.setTextSize(18);
        textView.setPadding(16, 16, 16, 8);

        // Δημιουργία ImageView για την εικόνα
        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                500 // Ύψος της εικόνας
        ));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)  // Εικόνα κατά τη διάρκεια φόρτωσης
                .error(R.drawable.errorimage)  // Εικόνα αν υπάρχει σφάλμα
                .into(imageView);

        // Προσθήκη Click Listener για το imageView
        imageView.setOnClickListener(v -> {
            // Φόρτωση του textField από το Firestore
            db.collection("stories")
                    .document(documentId) // Χρησιμοποιούμε το ID του document
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Απόκτηση του textField
                            String textField = documentSnapshot.getString("text");
                            if (textField != null) {
                                // Εμφάνιση με Toast ή σε άλλο TextView
                                Toast.makeText(getContext(), textField, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getContext(), "Το textField δεν υπάρχει!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Το document δεν υπάρχει!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Σφάλμα κατά την ανάκτηση του textField", e);
                        Toast.makeText(getContext(), "Σφάλμα κατά την ανάκτηση!", Toast.LENGTH_SHORT).show();
                    });
        });

        // Προσθήκη των στοιχείων στο LinearLayout
        linearLayout.addView(textView);
        linearLayout.addView(imageView);
    }

}
