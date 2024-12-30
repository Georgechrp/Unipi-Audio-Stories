package com.unipi.george.unipiaudiostories;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PlayerFragment extends Fragment {

    private static final String ARG_IMAGE_URL = "imageUrl";
    private static final String ARG_TEXT = "text";
    private static final String ARG_TITLE = "title";
    private static final String ARG_AUTHOR = "author";
    private static final String ARG_YEAR = "year";
    private static final String ARG_DOCUMENT_ID = "documentId";

    private String imageUrl;
    private String text;
    private String title;
    private String author;
    private String year;
    private String documentId;
    private MyTts myTts;
    private ImageView iconStart;
    private ImageView iconPause;
    private boolean isPlaying = false;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private long startTime = 0; // Χρόνος έναρξης σε milliseconds
    private long totalListeningTime = 0; // Συνολικός χρόνος ακρόασης σε δευτερόλεπτα

    public PlayerFragment() {
        // Required empty public constructor
    }

    public static PlayerFragment newInstance(String imageUrl, String text, String title, String author, String year, String documentId) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URL, imageUrl);
        args.putString(ARG_TEXT, text);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_AUTHOR, author);
        args.putString(ARG_YEAR, year);
        args.putString(ARG_DOCUMENT_ID, documentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (getArguments() != null) {
            imageUrl = getArguments().getString(ARG_IMAGE_URL);
            text = getArguments().getString(ARG_TEXT);
            title = getArguments().getString(ARG_TITLE);
            author = getArguments().getString(ARG_AUTHOR);
            year = getArguments().getString(ARG_YEAR);
            documentId = getArguments().getString(ARG_DOCUMENT_ID);
        }
        myTts = new MyTts(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        // Αρχικοποίηση στοιχείων του layout
        ImageView imageView = view.findViewById(R.id.imageViewSelected);
        TextView titleView = view.findViewById(R.id.titleTextView);
        TextView authorView = view.findViewById(R.id.authorTextView);
        TextView yearView = view.findViewById(R.id.yearTextView);
        iconStart = view.findViewById(R.id.iconStart);
        iconPause = view.findViewById(R.id.iconPause);

        // Φόρτωση εικόνας
        if (imageUrl != null) {
            Picasso.get().load(imageUrl).into(imageView);
        } else {
            Toast.makeText(getContext(), "Image URL not available", Toast.LENGTH_SHORT).show();
        }

        // Ρύθμιση κειμένου για τίτλο, συγγραφέα και έτος
        if (title != null) {
            titleView.setText(title);
        }
        if (author != null) {
            authorView.setText(author);
        }
        if (year != null) {
            yearView.setText(year);
        }

        // Ρύθμιση κουμπιών
        iconStart.setOnClickListener(v -> startSpeaking());
        iconPause.setOnClickListener(v -> pauseSpeaking());

        // Αρχική κατάσταση εικονιδίων
        toggleIcons();

        ImageView saveButton = view.findViewById(R.id.iconRight);
        saveButton.setOnClickListener(v -> saveTheStory());

        return view;
    }

    private void createOrUpdateStatistics(String userId, String documentIdToAdd) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Αναφορά στο document του χρήστη
        firestore.collection("statistics").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        // Αν το document υπάρχει, ενημέρωσε το πεδίο "saved"
                        firestore.collection("statistics").document(userId)
                                .update("saved", FieldValue.arrayUnion(documentIdToAdd));
                    } else {
                        // Αν το document δεν υπάρχει, δημιουργεί το "saved" array
                        Map<String, Object> data = new HashMap<>();
                        data.put("saved", Arrays.asList(userId, documentIdToAdd));

                        firestore.collection("statistics").document(userId)
                                .set(data);
                    }
                });
    }

    private void recordListeningTime(String userId, String documentId, long listeningTimeInSeconds) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Αναφορά στο document του χρήστη
        firestore.collection("statistics").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        // Αν το document υπάρχει, ενημέρωσε το πεδίο "listeningTime" για τη συγκεκριμένη ιστορία
                        firestore.collection("statistics").document(userId)
                                .update("listeningTime." + documentId, FieldValue.increment(listeningTimeInSeconds));
                    } else {
                        // Αν το document δεν υπάρχει, δημιούργησε ένα νέο με το "listeningTime"
                        Map<String, Object> data = new HashMap<>();
                        Map<String, Object> listeningTime = new HashMap<>();
                        listeningTime.put(documentId, listeningTimeInSeconds);
                        data.put("listeningTime", listeningTime);

                        firestore.collection("statistics").document(userId)
                                .set(data);
                    }
                });
    }

    public void saveTheStory() {
        if (documentId != null) {
            createOrUpdateStatistics(user.getUid(), documentId);
            PreferencesManager.saveStory(requireContext(), documentId, imageUrl, text, title, author, year);
        } else {
            Toast.makeText(getContext(), "Document ID not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void startSpeaking() {
        if (!isPlaying) {
            if (text != null && !text.isEmpty()) {
                startTime = System.currentTimeMillis(); // Καταγραφή ώρας έναρξης
                isPlaying = true;
                myTts.speak(text);
                toggleIcons();
            } else {
                Toast.makeText(getContext(), "No text to read", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void pauseSpeaking() {
        if (isPlaying) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = (currentTime - startTime) / 1000;
            totalListeningTime += elapsedTime;
            startTime = 0;

            recordListeningTime(user.getUid(), documentId, elapsedTime);

            isPlaying = false;
            myTts.stopSpeaking();
            toggleIcons();
        } else {
            Toast.makeText(getContext(), "Nothing is being spoken", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleIcons() {
        if (isPlaying) {
            iconStart.setVisibility(View.GONE);
            iconPause.setVisibility(View.VISIBLE);
        } else {
            iconStart.setVisibility(View.VISIBLE);
            iconPause.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isPlaying) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = (currentTime - startTime) / 1000; // Υπολογισμός χρόνου σε δευτερόλεπτα
            totalListeningTime += elapsedTime;

            // Αποθήκευση του χρόνου ακρόασης στο Firestore
            recordListeningTime(user.getUid(), documentId, elapsedTime);
        }
        if (myTts != null) {
            myTts.stopSpeaking();
        }
    }
}
