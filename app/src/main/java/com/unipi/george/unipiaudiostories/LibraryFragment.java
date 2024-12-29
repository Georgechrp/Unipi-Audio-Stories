package com.unipi.george.unipiaudiostories;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.Set;

public class LibraryFragment extends Fragment {

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

        // Φόρτωση δεδομένων από SharedPreferences
        loadStoriesFromPreferences();

        return view;
    }

    private void loadStoriesFromPreferences() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyStories", Context.MODE_PRIVATE);

        // Ανάκτηση όλων των αποθηκευμένων documentIds
        Set<String> documentIds = sharedPreferences.getStringSet("documentIds", new HashSet<>());

        if (documentIds != null && !documentIds.isEmpty()) {
            for (String documentId : documentIds) {
                // Ανάκτηση δεδομένων της ιστορίας
                String storyData = sharedPreferences.getString(documentId, null);

                if (storyData != null) {
                    // Διάσπαση της συμβολοσειράς στα επιμέρους πεδία
                    String[] parts = storyData.split("\\|");
                    if (parts.length == 5) {
                        String imageUrl = parts[0];
                        String text = parts[1];
                        String title = parts[2];
                        String author = parts[3];
                        String year = parts[4];

                        // Προσθήκη της ιστορίας στο view
                        addDataToView(title, imageUrl);
                    }
                }
            }
        } else {
            Toast.makeText(getContext(), "Δεν βρέθηκαν αποθηκευμένες ιστορίες", Toast.LENGTH_SHORT).show();
        }
    }

    private void addDataToView(String title, String imageUrl) {
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

        // Προσθήκη των στοιχείων στο LinearLayout
        linearLayout.addView(textView);
        linearLayout.addView(imageView);
    }
}
