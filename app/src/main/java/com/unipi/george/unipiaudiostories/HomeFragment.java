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
    private LinearLayout linearLayout; // Για εμφάνιση δεδομένων
    private String text, author, year;

    public HomeFragment() {
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
        loadAllDocuments();

        return view;
    }
    private void loadAllDocuments() {
        db.collection("stories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            String imageUrl = document.getString("imageURL");
                            text = document.getString("text");
                            author = document.getString("author");
                            year = document.getString("year");
                            String documentId = document.getId();


                            addDataToView(title, imageUrl, documentId);

                        }
                    } else {
                        Log.w(TAG, "Error", task.getException());
                    }
                });
    }


    private void addDataToView(String title, String imageUrl, String documentId) {
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
