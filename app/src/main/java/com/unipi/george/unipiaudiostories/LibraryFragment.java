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
    private String userId;// = "yll2LeyFTgTX0CrGl7z5uxuUcpr1"; // Αντικατάστησε με το πραγματικό userId ή από SharedPreferences.
    private FirebaseAuth auth;
    private FirebaseUser user;
    public LibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
        // Inflate το layout για το fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Αρχικοποίηση του LinearLayout
        linearLayout = view.findViewById(R.id.linearLayoutData);

        // Αρχικοποίηση Firestore
        db = FirebaseFirestore.getInstance();

        // Φόρτωση των αγαπημένων ιστοριών
        loadSavedStories();

        return view;
    }

    private void loadSavedStories() {
        db.collection("statistics")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> savedStories = (List<String>) documentSnapshot.get("saved");
                        if (savedStories != null && !savedStories.isEmpty()) {
                            fetchStoriesData(savedStories);
                        } else {
                            Toast.makeText(getContext(), "Δεν έχετε αποθηκευμένες ιστορίες", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Δεν έχετε αποθηκεύσει κάποια ιστορία", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Σφάλμα ανάκτησης δεδομένων", e));
    }

    private void fetchStoriesData(List<String> storyIds) {
        // Αν δεν υπάρχουν αποθηκευμένες ιστορίες, βγαίνουμε νωρίς
        if (storyIds == null || storyIds.isEmpty()) {
            Toast.makeText(getContext(), "Δεν έχετε αποθηκευμένες ιστορίες", Toast.LENGTH_SHORT).show();
            return;
        }

        // Διατρέχουμε τα saved IDs και φορτώνουμε μόνο τις ιστορίες που είναι αποθηκευμένες
        for (String storyId : storyIds) {
            db.collection("stories")
                    .document(storyId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String title = documentSnapshot.getString("title");
                            String imageUrl = documentSnapshot.getString("imageURL");
                            String text = documentSnapshot.getString("text");
                            String author = documentSnapshot.getString("author");
                            String year = documentSnapshot.getString("year");

                            addDataToView(title, imageUrl, text, author, year, storyId);
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Σφάλμα ανάκτησης ιστορίας: " + storyId, e));
        }
    }


    private void addDataToView(String title, String imageUrl, String text, String author, String year, String documentId) {
        // Δημιουργία CardView
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

        // Δημιουργία TextView για τον τίτλο
        TextView textView = new TextView(getContext());
        textView.setText(title);
        textView.setTextSize(18);
        textView.setPadding(0, 0, 0, 16);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        // Δημιουργία ImageView για την εικόνα
        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                500 // Ύψος της εικόνας
        ));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Φόρτωση εικόνας
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.errorimage)
                .into(imageView);

        // Προσθήκη στο CardView
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(textView);
        layout.addView(imageView);

        cardView.addView(layout);

        // Προσθήκη listener για το ImageView
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
