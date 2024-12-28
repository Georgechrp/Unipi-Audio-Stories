package com.unipi.george.unipiaudiostories;

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

import com.squareup.picasso.Picasso;

public class PlayerFragment extends Fragment {

    private static final String ARG_IMAGE_URL = "imageUrl";
    private static final String ARG_TEXT = "text";
    private static final String ARG_TITLE = "title";
    private static final String ARG_AUTHOR = "author";
    private static final String ARG_YEAR = "year";

    private String imageUrl;
    private String text;
    private String title;
    private String author;
    private String year;
    private MyTts myTts;
    private ImageView iconStart;
    private ImageView iconPause;
    private boolean isPlaying = false;

    public PlayerFragment() {
        // Required empty public constructor
    }

    public static PlayerFragment newInstance(String imageUrl, String text, String title, String author, String year) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URL, imageUrl);
        args.putString(ARG_TEXT, text);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_AUTHOR, author);
        args.putString(ARG_YEAR, year);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageUrl = getArguments().getString(ARG_IMAGE_URL);
            text = getArguments().getString(ARG_TEXT);
            title = getArguments().getString(ARG_TITLE);
            author = getArguments().getString(ARG_AUTHOR);
            year = getArguments().getString(ARG_YEAR);
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

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void startSpeaking() {
        if (!isPlaying) {
            if (text != null && !text.isEmpty()) {
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
        if (myTts != null) {
            myTts.stopSpeaking();
        }
    }
}
