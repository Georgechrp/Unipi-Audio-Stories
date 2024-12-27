package com.unipi.george.unipiaudiostories;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button logout;
    TextView welcomeUserText;
    FirebaseUser user;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        logout = findViewById(R.id.logoutButton);
        welcomeUserText = findViewById(R.id.welcomeUser);
        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent =  new Intent(this, Login.class);
            startActivity(intent);

        }else{
            welcomeUserText.setText("Hello " + user.getEmail());

        }

        logout.setOnClickListener(v -> {
            auth.signOut();
            Intent intent =  new Intent(this, Login.class);
            startActivity(intent);
        });
    }
}