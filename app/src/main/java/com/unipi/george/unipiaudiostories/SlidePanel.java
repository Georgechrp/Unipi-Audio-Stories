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
        mAuth = FirebaseAuth.getInstance(); // Αρχικοποίηση του mAuth
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Αν υπάρχει ενεργός χρήστης, ξεκινάμε το MainActivity και τερματίζουμε το Fragment Activity.
            /*Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish(); // Κλείσιμο του Activity*/
        }
    }
    private void signOut() {
        FirebaseAuth.getInstance().signOut(); // Εκτέλεση του logout
        Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Επιστροφή στην οθόνη εισόδου (Login Activity)
        Intent intent = new Intent(getActivity(), Login.class);
        startActivity(intent);
        getActivity().finish(); // Κλείσιμο του Activity που φιλοξενεί το Fragment
    }
    public SlidePanel() {
        // Required empty public constructor
    }

    public static SlidePanel newInstance() {
        return new SlidePanel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_slide_panel, container, false);

        // Initialize UI elements
        Button button = view.findViewById(R.id.my_button);
        // Ορίζουμε τον OnClickListener για το κουμπί
        button.setOnClickListener(v -> {
            // Εμφανίζουμε μήνυμα Toast
            //Toast.makeText(getActivity(), "Logout successfully!", Toast.LENGTH_SHORT).show();

            // Καλούμε τη συνάρτηση signOut
            signOut();
        });


        return view;
    }



}
