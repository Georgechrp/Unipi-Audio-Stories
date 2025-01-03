package com.unipi.george.unipiaudiostories;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LibraryFragment extends Fragment {

    private static final String TAG = "LibraryFragment"; // Tag για logs
    private FirebaseFirestore db;
    private LinearLayout linearLayout; // Για εμφάνιση δεδομένων
    private String userId; // Αναγνωριστικό χρήστη που ανακτάται από FirebaseAuth
    private FirebaseAuth auth;
    private FirebaseUser user;

    public LibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Αρχικοποίηση Firebase Authentication και ανάκτηση τρέχοντος χρήστη
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        linearLayout = view.findViewById(R.id.linearLayoutData);
        db = FirebaseFirestore.getInstance();
        loadSavedStories();// Φόρτωση των αγαπημένων ιστοριών του χρήστη

        return view;
    }

    private void loadSavedStories() {
        // Ανάκτηση της συλλογής "statistics" για το τρέχον userId
        db.collection("statistics")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Ανάκτηση λίστας αποθηκευμένων ιστοριών
                        List<String> savedStories = (List<String>) documentSnapshot.get("saved");
                        if (savedStories != null && !savedStories.isEmpty()) {
                            // Φόρτωση δεδομένων για κάθε αποθηκευμένη ιστορία
                            fetchStoriesData(savedStories);
                        } else {
                            // Ενημέρωση χρήστη αν δεν υπάρχουν αποθηκευμένες ιστορίες
                            Toast.makeText(getContext(), "Δεν έχετε αποθηκευμένες ιστορίες", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Ενημέρωση χρηστη αν δεν υπάρχει το έγγραφο
                        Toast.makeText(getContext(), "Δεν έχετε αποθηκεύσει κάποια ιστορία", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Σφάλμα ανάκτησης δεδομένων", e));
    }

    private void fetchStoriesData(List<String> storyIds) {
        if (storyIds == null || storyIds.isEmpty()) {
            Toast.makeText(getContext(), "Δεν έχετε αποθηκευμένες ιστορίες", Toast.LENGTH_SHORT).show();
            return;
        }

        // Επανάληψη για κάθε αποθηκευμένο ID
        for (String storyId : storyIds) {
            db.collection("stories")
                    .document(storyId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Ανάκτηση πεδίων της ιστορίας από το έγγραφο
                            String title = documentSnapshot.getString("title");
                            String imageUrl = documentSnapshot.getString("imageURL");
                            String text = documentSnapshot.getString("text");
                            String author = documentSnapshot.getString("author");
                            String year = documentSnapshot.getString("year");

                            // Προσθήκη των δεδομένων στο UI
                            addDataToView(title, imageUrl, text, author, year, storyId);
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Σφάλμα ανάκτησης ιστορίας: " + storyId, e));
        }
    }

    private void addDataToView(String title, String imageUrl, String text, String author, String year, String documentId) {
        // Δημιουργία CardView για εμφάνιση της ιστορίας
        CardView cardView = new CardView(getContext());
        cardView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        cardView.setRadius(16);
        cardView.setCardElevation(8);
        cardView.setUseCompatPadding(true);
        cardView.setPadding(16, 16, 16, 16);
        cardView.setContentPadding(16, 16, 16, 16);

        // Δημιουργία TextView για τον τίτλο της ιστορίας
        TextView textView = new TextView(getContext());
        textView.setText(title);
        textView.setTextSize(18);
        textView.setPadding(0, 0, 0, 16);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        // Δημιουργία ImageView για την εικόνα της ιστορίας
        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                500 // Ύψος της εικόνας
        ));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Χρήση Picasso για φόρτωση εικόνας από URL
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.errorimage)
                .into(imageView);

        // Δημιουργία Layout για την ομαδοποίηση τίτλου και εικόνας
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(textView);
        layout.addView(imageView);

        cardView.addView(layout);

        // Listener για την εικόνα που ανοίγει το PlayerFragment
        imageView.setOnClickListener(v -> {
            PlayerFragment playerFragment = PlayerFragment.newInstance(imageUrl, text, title, author, year, documentId);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, playerFragment) // Αντικατάσταση του fragment container
                    .addToBackStack(null) // Προσθήκη στο backstack
                    .commit();
        });

        // Προσθήκη του CardView στο LinearLayout
        linearLayout.addView(cardView);
    }

}
