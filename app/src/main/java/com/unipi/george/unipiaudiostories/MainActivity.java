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
        topRightImage = findViewById(R.id.top_right_image);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new HomeFragment())
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                try {
                    // Ορισμός της εικόνας στο ImageView
                    topRightImage.setImageURI(imageUri);
                    Toast.makeText(this, "Image inserted successfully!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Error displaying image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No image selected.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PICK_IMAGE_REQUEST) {
            Toast.makeText(this, "Image selection canceled.", Toast.LENGTH_SHORT).show();
        }
    }
    public void Statistics(View view) {
        Intent intent = new Intent(this, Statistics.class);
        startActivity(intent);
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
        // Έλεγχος άδειας ανάλογα με την έκδοση του Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission(Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void requestPermission(String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            // Εμφάνιση αιτήματος άδειας στον χρήστη
            ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_PERMISSION_CODE);
        } else {
            // Άδεια ήδη δοσμένη, άνοιγμα της γκαλερί
            openGallery();
        }
    }

    private void openGallery() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        } catch (Exception e) {
            Toast.makeText(this, "Error opening gallery: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Άδεια δοσμένη, άνοιγμα γκαλερί
                openGallery();
            } else {
                // Ο χρήστης δεν έδωσε άδεια
                Toast.makeText(this, "Permission is required to select an image.", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
