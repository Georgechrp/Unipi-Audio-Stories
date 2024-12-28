package com.unipi.george.unipiaudiostories;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SlidePanel extends Fragment {

    private boolean isPanelOpen = false; // Κατάσταση του panel

    public SlidePanel() {
        // Απαιτείται κενός δημόσιος κατασκευαστής
    }

    public static SlidePanel newInstance(String param1, String param2) {
        SlidePanel fragment = new SlidePanel();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Εμφανίζει το layout του Fragment
        return inflater.inflate(R.layout.fragment_slide_panel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Εύρεση του FrameLayout που θα εμφανίζεται/αποκρύβεται
        FrameLayout slidePanel = view.findViewById(R.id.slidePanel);

        // Αρχικά το panel είναι κρυφό
        slidePanel.setVisibility(View.GONE);

        // Αν υπάρχει trigger κουμπί, κάνε το click listener
        ImageView triggerButton = view.findViewById(R.id.imageViewTrigger); // Προσάρμοσε το ID αν χρειάζεται
        triggerButton.setOnClickListener(v -> toggleSlidePanel(slidePanel));
    }

    private void toggleSlidePanel(FrameLayout slidePanel) {
        Animation animation;
        if (isPanelOpen) {
            // Slide out animation
            animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    slidePanel.setVisibility(View.GONE); // Απόκρυψη όταν τελειώσει το animation
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        } else {
            // Slide in animation
            animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in);
            slidePanel.setVisibility(View.VISIBLE); // Εμφάνιση πριν ξεκινήσει το animation
        }
        slidePanel.startAnimation(animation);
        isPanelOpen = !isPanelOpen;
    }
}
