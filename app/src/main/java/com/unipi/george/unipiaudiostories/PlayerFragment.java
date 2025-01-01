package com.unipi.george.unipiaudiostories;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
    private ImageView iconStart, savedButton;
    private ImageView iconPause;
    private boolean isPlaying = false;
    private static boolean flag = false;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private long startTime = 0; // Χρόνος έναρξης σε milliseconds
    private long totalListeningTime = 0; // Συνολικός χρόνος ακρόασης σε δευτερόλεπτα
    private EditText editText;

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
        myTts.setCompletionListener(() -> {
            if (user != null && documentId != null) {
                recordListeningCompletion(user.getUid(), documentId);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        ImageView imageView = view.findViewById(R.id.imageViewSelected);
        TextView titleView = view.findViewById(R.id.titleTextView);
        TextView authorView = view.findViewById(R.id.authorTextView);
        TextView yearView = view.findViewById(R.id.yearTextView);
        EditText multilineTextView = view.findViewById(R.id.multilineTextView);
        iconStart = view.findViewById(R.id.iconStart);
        iconPause = view.findViewById(R.id.iconPause);

        // Save button
        ImageView saveButton = view.findViewById(R.id.iconRight);
        final boolean[] isSaved = {false};

        // Φόρτωση εικόνας
        if (imageUrl != null) {
            Picasso.get().load(imageUrl).into(imageView);
        } else {
            Toast.makeText(getContext(), "Image URL not available", Toast.LENGTH_SHORT).show();
        }

        // Ρύθμιση κειμένου
        if (title != null) {
            titleView.setText(title);
        }
        if (author != null) {
            authorView.setText(author);
        }
        if (year != null) {
            yearView.setText(year);
        }
        if (text != null) {
            multilineTextView.setText(text);
        } else {
            multilineTextView.setText(R.string.no_text_available);
        }

        // Έλεγχος κατάστασης από Firestore
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        if (user != null && documentId != null) {
            firestore.collection("statistics").document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Έλεγχος αν το documentId περιέχεται στη λίστα "saved"
                            if (documentSnapshot.contains("saved")) {
                                isSaved[0] = ((List<String>) documentSnapshot.get("saved")).contains(documentId);
                                saveButton.setImageResource(isSaved[0] ? R.drawable.checked : R.drawable.check);
                            }
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error fetching saved status", Toast.LENGTH_SHORT).show());
        }

        // Ρύθμιση κουμπιού save
        saveButton.setOnClickListener(v -> {
            if (isSaved[0]) {
                // Αφαίρεση της ιστορίας
                removeTheStory(documentId);
                saveButton.setImageResource(R.drawable.check);
            } else {
                // Αποθήκευση της ιστορίας
                saveTheStory();
                saveButton.setImageResource(R.drawable.checked);
            }
            isSaved[0] = !isSaved[0]; // Αναστροφή κατάστασης
        });

        // Ρύθμιση κουμπιών για έναρξη/παύση
        iconStart.setOnClickListener(v -> startSpeaking());
        iconPause.setOnClickListener(v -> pauseSpeaking());
        toggleIcons();

        return view;
    }


    private void removeTheStory(String storyId) {
        // Λήψη του τρέχοντος χρήστη
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid(); // Αντικατέστησε με το userId του χρήστη

            // Αναφορά στο Firestore για το document του χρήστη
            DocumentReference userRef = FirebaseFirestore.getInstance().collection("statistics").document(userId);

            // Αφαίρεση της ιστορίας από τη λίστα των αποθηκευμένων
            userRef.update("saved", FieldValue.arrayRemove(storyId))
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Ιστορία αφαιρέθηκε επιτυχώς");
                        // Ενημέρωση του UI ή άλλες ενέργειες
                        Toast.makeText(getContext(), "Η ιστορία αφαιρέθηκε από τα αγαπημένα", Toast.LENGTH_SHORT).show();
                        // Εδώ μπορείς να κάνεις refresh ή να κλείσεις το fragment
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Σφάλμα κατά την αφαίρεση της ιστορίας", e);
                        Toast.makeText(getContext(), "Σφάλμα κατά την αφαίρεση της ιστορίας", Toast.LENGTH_SHORT).show();
                    });
        }
    }



    public void saveTheStory() {
        if (documentId != null) {
            createOrUpdateStatistics(user.getUid(), documentId);
        } else {
            Toast.makeText(getContext(), "Document ID not available", Toast.LENGTH_SHORT).show();
        }
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

    private void recordListeningCompletion(String userId, String documentId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("statistics").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        // Αν το document υπάρχει, ενημέρωσε το πεδίο "listeningCount" για τη συγκεκριμένη ιστορία
                        firestore.collection("statistics").document(userId)
                                .update("listeningCount." + documentId, FieldValue.increment(1));
                    } else {
                        // Αν το document δεν υπάρχει, δημιούργησε ένα νέο με το "listeningCount"
                        Map<String, Object> data = new HashMap<>();
                        Map<String, Object> listeningCount = new HashMap<>();
                        listeningCount.put(documentId, 1);
                        data.put("listeningCount", listeningCount);

                        firestore.collection("statistics").document(userId)
                                .set(data);
                    }
                });
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
