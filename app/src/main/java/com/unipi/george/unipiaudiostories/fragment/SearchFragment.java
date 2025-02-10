package com.unipi.george.unipiaudiostories.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.unipi.george.unipiaudiostories.R;
import com.unipi.george.unipiaudiostories.utils.StoryCardHelper;

public class SearchFragment extends Fragment {

    private EditText searchEditText;
    private FirebaseFirestore db;
    private CollectionReference storiesRef;
    private LinearLayout cardsContainer; // Container για τις κάρτες

    public SearchFragment() {
        // Required empty constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Αρχικοποίηση Firestore
        db = FirebaseFirestore.getInstance();
        storiesRef = db.collection("stories");

        // Αναφορά στο LinearLayout που θα περιέχει τις κάρτες
        cardsContainer = view.findViewById(R.id.cards_container);

        // Αναφορά στο πεδίο αναζήτησης
        searchEditText = view.findViewById(R.id.search_edit_text);

        // Προσθήκη listener για real-time αναζήτηση
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchStories(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void searchStories(String query) {
        if (query.isEmpty()) {
            cardsContainer.removeAllViews(); // Καθαρισμός των προηγούμενων καρτών
            return;
        }

        storiesRef.orderBy("title")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }

                        // Καθαρισμός προηγούμενων καρτών
                        cardsContainer.removeAllViews();

                        if (value != null) {
                            for (QueryDocumentSnapshot doc : value) {
                                String title = doc.getString("title");
                                String imageUrl = doc.getString("imageURL");
                                String text = doc.getString("text");
                                String author = doc.getString("author");
                                String year = doc.getString("year");
                                String documentId = doc.getId();

                                // Δημιουργία κάρτας και προσθήκη στο layout
                                View cardView = StoryCardHelper.createStoryCard(getContext(), title, imageUrl, text, author, year, documentId, cardsContainer);
                                if (cardView != null) {
                                    cardsContainer.addView(cardView);
                                }
                            }
                        }
                    }
                });
    }
}
