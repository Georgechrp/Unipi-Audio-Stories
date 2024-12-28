package com.unipi.george.unipiaudiostories;

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

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment"; // Tag για logs
    private FirebaseFirestore db;
    private LinearLayout linearLayout; // Για εμφάνιση δεδομένων

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
                            String documentId = document.getId(); // Λήψη του documentId

                            // Προσθήκη κάθε εγγραφής στο LinearLayout
                            addDataToView(title, imageUrl, documentId);
                            Log.d(TAG, "Title: " + title + ", ImageUrl: " + imageUrl + ", DocumentId: " + documentId);
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

        // Προσθήκη Click Listener
        imageView.setOnClickListener(v -> {
            // Φόρτωση του textField από το Firestore
            db.collection("stories")
                    .document(documentId) // Χρησιμοποιούμε το ID του document
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Απόκτηση του textField
                            String textField = documentSnapshot.getString("text");
                            String author = documentSnapshot.getString("author");
                            String year = documentSnapshot.getString("year");

                            if (textField != null) {
                                // Εμφάνιση με Toast ή σε άλλο TextView
                                //Toast.makeText(getContext(), textField, Toast.LENGTH_LONG).show();

                                PlayerFragment playerFragment = PlayerFragment.newInstance(imageUrl, textField, title, author, year);

                                // Αρχικοποίηση FragmentTransaction για να αντικαταστήσεις το τρέχον fragment με το νέο
                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.container, playerFragment)  // Το container είναι το FrameLayout στο activity
                                        .addToBackStack(null)  // Επιτρέπει την επιστροφή στο προηγούμενο fragment
                                        .commit();

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
