package com.unipi.george.unipiaudiostories;

import android.Manifest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.content.Context;
import android.content.res.Configuration;
import java.util.Locale;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    ImageView topRightImage;

    private final int REQUEST_PERMISSION_CODE = 100;
    private final int PICK_IMAGE_REQUEST = 200;
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

    public void saveTheStorie(View view) {
        // Παράδειγμα δεδομένων ιστορίας
        String documentId = "exampleDocumentId";  // Χρησιμοποίησε το πραγματικό documentId
        // Κάλεσε την αντίστοιχη μέθοδο από το Fragment
        PlayerFragment fragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag("PlayerFragment");
        if (fragment != null) {
            fragment.saveTheStorie(view, documentId);
        }
    }

    public void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = getResources().getConfiguration();
        config.setLocale(locale);

        createConfigurationContext(config);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
    public void openSlidePanel(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(
                R.anim.slide_in,
                R.anim.slide_out,
                R.anim.slide_in, // Reverse animation (αν επιστρέφουμε)
                R.anim.slide_out
        );

        if (isSlidePanelVisible) {
            // Αφαίρεση του SlidePanel
            SlidePanel slidePanel = (SlidePanel) fragmentManager.findFragmentByTag("SlidePanel");
            if (slidePanel != null) {
                transaction.remove(slidePanel);
            }
            isSlidePanelVisible = false;
            findViewById(R.id.slide_panel_container).setVisibility(View.INVISIBLE);
        } else {
            // Εμφάνιση του SlidePanel
            SlidePanel slidePanel = SlidePanel.newInstance();
            transaction.add(R.id.slide_panel_container, slidePanel, "SlidePanel");
            findViewById(R.id.slide_panel_container).setVisibility(View.VISIBLE);
            isSlidePanelVisible = true;
        }

        transaction.addToBackStack(null);
        transaction.commit();
    }


    public void insertPic(View view) {
        // Ελέγξτε και ζητήστε άδεια αν χρειάζεται
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_PERMISSION_CODE);
            } else {
                openGallery();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
            } else {
                openGallery();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                topRightImage.setImageURI(imageUri);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                // Ενημερώστε τον χρήστη ότι η άδεια απορρίφθηκε
            }
        }
    }








}
