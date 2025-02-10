package com.unipi.george.unipiaudiostories.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.squareup.picasso.Picasso;
import com.unipi.george.unipiaudiostories.R;
import com.unipi.george.unipiaudiostories.fragment.PlayerFragment;

public class StoryCardHelper {

    private static final String TAG = "StoryCardHelper";

    public static CardView createStoryCard(Context context, String title, String imageUrl, String text, String author, String year, String documentId, LinearLayout linearLayout) {
        if (context == null) {
            Log.e(TAG, "Context is null! Cannot create card.");
            return null;
        }

        // Δημιουργία CardView
        CardView cardView = new CardView(context);
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
        TextView textView = new TextView(context);
        textView.setText(title);
        textView.setTextSize(18);
        textView.setPadding(0, 0, 0, 16);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        // Δημιουργία ImageView για την εικόνα
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                500
        ));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Φόρτωση εικόνας με Picasso
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.errorimage)
                .into(imageView);

        // Δημιουργία LinearLayout για την ομαδοποίηση των στοιχείων
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(textView);
        layout.addView(imageView);

        cardView.addView(layout);

        // Προσθήκη event listener στην εικόνα
        imageView.setOnClickListener(v -> {
            PlayerFragment playerFragment = PlayerFragment.newInstance(imageUrl, text, title, author, year, documentId);
            if (linearLayout.getContext() instanceof androidx.fragment.app.FragmentActivity) {
                ((androidx.fragment.app.FragmentActivity) linearLayout.getContext())
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, playerFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                Log.e(TAG, "Error: Could not get FragmentManager.");
            }
        });

        return cardView;
    }
}
