package com.unipi.george.unipiaudiostories;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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

    /**
     * Φορτώνει όλα τα έγγραφα από τη συλλογή "stories" του Firestore.
     * Αν είναι επιτυχής η φόρτωση, καλεί τη μέθοδο addDataToView για κάθε document.
     */
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
                            addDataToView(title, imageUrl, documentId, text, author, year);
                        }
                    } else {
                        // αν αποτύχει η ανάκτηση
                        Log.w(TAG, "Error", task.getException());
                    }
                });
    }


     //Δημιουργεί και προσθέτει ένα δυναμικό CardView για κάθε έγγραφο.
    private void addDataToView(String title, String imageUrl, String documentId, String text, String author, String year) {
        // Δημιουργία CardView
        CardView cardView = new CardView(getContext());
        cardView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        cardView.setRadius(16); // Στρογγυλεμένες γωνίες
        cardView.setCardElevation(8);
        cardView.setUseCompatPadding(true); // padding για παλαιότερες εκδόσεις
        cardView.setPadding(16, 16, 16, 16); // Εσωτερικό padding
        cardView.setContentPadding(16, 16, 16, 16);

        // Δημιουργία TextView για τον τίτλο της ιστορίας
        TextView textView = new TextView(getContext());
        textView.setText(title);
        textView.setTextSize(18);
        textView.setPadding(0, 0, 0, 16); // Κάτω padding
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER); // Κεντράρισμα κειμένου

        // Δημιουργία ImageView για την εμφάνιση της εικόνας
        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                500 // Ύψος εικόνας
        ));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP); // Προσαρμογή

        // Φόρτωση εικόνας από URL χρησιμοποιώντας Picasso
        Picasso.get()
                .load(imageUrl) // URL εικόνας
                .placeholder(R.drawable.placeholder) // Εικόνα placeholder αν καθυστερεί το φόρτωμα
                .error(R.drawable.errorimage) // Εικόνα σφάλματος αν αποτύχει το φόρτωμα
                .into(imageView);

        // Προσθήκη TextView και ImageView σε LinearLayout
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL); // Κατακόρυφη διάταξη
        layout.addView(textView);
        layout.addView(imageView);

        cardView.addView(layout); // Προσθήκη του layout στο CardView

        // Προσθήκη λειτουργίας click στην εικόνα
        imageView.setOnClickListener(v -> {
            // Δημιουργία και εμφάνιση του PlayerFragment με τα δεδομένα της ιστορίας
            PlayerFragment playerFragment = PlayerFragment.newInstance(imageUrl, text, title, author, year, documentId);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, playerFragment) // Αντικατάσταση του fragment container
                    .addToBackStack(null) // Προσθήκη στο backstack
                    .commit();
        });

        // Προσθήκη του CardView στο LinearLayout της διεπαφής
        linearLayout.addView(cardView);
    }
}
