// SlidePanel.java
package com.unipi.george.unipiaudiostories;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class SlidePanel extends Fragment {

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
        button.setOnClickListener(v -> Toast.makeText(getActivity(), "Button clicked!", Toast.LENGTH_SHORT).show());


        return view;
    }
}
