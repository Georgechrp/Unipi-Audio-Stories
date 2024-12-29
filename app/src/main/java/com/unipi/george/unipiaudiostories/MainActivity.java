package com.unipi.george.unipiaudiostories;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;

    private boolean isSlidePanelVisible  = false; // Κατάσταση του panel
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            return;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new HomeFragment()) // Αρχικό Fragment
                .commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new HomeFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.nav_search) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new SearchFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.nav_library) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new LibraryFragment())
                        .commit();
                return true;
            } else {
                return false;
            }
        });

    }

    public void openSlidePanel(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(
                R.anim.slide_in,
                R.anim.slide_out
        );

        if (isSlidePanelVisible) {
            SlidePanel slidePanel = (SlidePanel) fragmentManager.findFragmentByTag("SlidePanel");
            if (slidePanel != null) {
                transaction.remove(slidePanel);
            }
            isSlidePanelVisible = false;
        } else {
            SlidePanel slidePanel = SlidePanel.newInstance();
            transaction.replace(R.id.container, slidePanel, "SlidePanel");
            transaction.addToBackStack(null);
            isSlidePanelVisible = true;
        }

        transaction.commit();
    }
}
