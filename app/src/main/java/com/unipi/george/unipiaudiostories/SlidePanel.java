// SlidePanel.java
package com.unipi.george.unipiaudiostories;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SlidePanel extends Fragment {


    FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance(); // Αρχικοποίηση της αυθεντικοποίησης Firebase
       // FirebaseUser currentUser = mAuth.getCurrentUser(); // Έλεγχος αν υπάρχει συνδεδεμένος χρήστης

    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut(); // Αποσύνδεση από το Firebase
        Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Μεταφορά του χρήστη στην οθόνη εισόδου
        Intent intent = new Intent(getActivity(), Login.class);
        startActivity(intent);
        getActivity().finish();
    }

    public SlidePanel() {
        // Απαραίτητο
    }

    public static SlidePanel newInstance() {
        return new SlidePanel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Μέθοδος για τη δημιουργία του UI του Fragment
        View view = inflater.inflate(R.layout.fragment_slide_panel, container, false); // Φόρτωση του layout XML

        Button button = view.findViewById(R.id.my_button); // Εύρεση του κουμπιού από το layout

        button.setOnClickListener(v -> {
            signOut();
        });

        return view;
    }

}
