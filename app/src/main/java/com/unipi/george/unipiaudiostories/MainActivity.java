package com.unipi.george.unipiaudiostories;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    TextView welcomeUserText;
    FirebaseUser user;
    ImageView logout, imageView2;
    ImageView topRightImage;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        topRightImage = findViewById(R.id.top_right_image);

        // Αν θέλεις να προσθέσεις μια αλληλεπίδραση, π.χ. click listener
        topRightImage.setOnClickListener(v -> {
            // Εμφανίζει το SlidePanel με το animation
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new SlidePanel())  // Παράμετροι αν χρειάζονται
                    .addToBackStack(null)  // Επιτρέπει στον χρήστη να επιστρέψει
                    .commit();
        });








        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }

        // Ορισμός του αρχικού Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new HomeFragment()) // Αρχικό Fragment
                .commit();

        // Βρες το BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // Αντικατέστησε το Fragment με το HomeFragment
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new HomeFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.nav_search) {
                // Αντικατέστησε το Fragment με το SearchFragment
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new SearchFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.nav_library) {
                // Αντικατέστησε το Fragment με το LibraryFragment
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new LibraryFragment())
                        .commit();
                return true;
            } else {
                return false;
            }
        });






    }


}
