package com.unipi.george.unipiaudiostories.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
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
import com.unipi.george.unipiaudiostories.R;
import com.unipi.george.unipiaudiostories.utils.StoryCardHelper;

import java.util.List;

public class LibraryFragment extends Fragment {

    private static final String TAG = "LibraryFragment"; // Tag για logs
    private FirebaseFirestore db;
    private LinearLayout linearLayout; // Για εμφάνιση δεδομένων
    private String userId; // Αναγνωριστικό χρήστη που ανακτάται από FirebaseAuth
    private FirebaseAuth auth;
    private FirebaseUser user;

    private Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

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
                        if (!isAdded() || getContext() == null) {
                            Log.e("LibraryFragment", "Fragment is not attached. Skipping UI update.");
                            return; // Μην προχωρήσεις αν το Fragment δεν είναι έτοιμο
                        }
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
