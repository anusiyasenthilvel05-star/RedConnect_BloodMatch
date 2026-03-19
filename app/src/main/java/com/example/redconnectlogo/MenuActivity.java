package com.example.redconnectlogo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MenuActivity extends AppCompatActivity {

    Button donorBtn, recipientBtn, feedbackBtn, aboutBtn, logoutBtn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mAuth = FirebaseAuth.getInstance();

        donorBtn = findViewById(R.id.donorBtn);
        recipientBtn = findViewById(R.id.recipientBtn);
        feedbackBtn = findViewById(R.id.feedbackBtn);
        aboutBtn = findViewById(R.id.aboutBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        donorBtn.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, DonorRegistrationActivity.class));
        });

        recipientBtn.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, BloodRequestFormActivity.class));
        });

        feedbackBtn.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, com.example.redconnectlogo.FeedbackActivity.class));
        });

        aboutBtn.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, AboutUsActivity.class));
        });

        logoutBtn.setOnClickListener(v -> {

            FirebaseUser user = mAuth.getCurrentUser();

            if (user != null) {
                user.delete().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MenuActivity.this,
                                "Account Deleted Successfully",
                                Toast.LENGTH_LONG).show();

                        mAuth.signOut();

                        startActivity(new Intent(MenuActivity.this,
                                LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(MenuActivity.this,
                                "Re-login Required",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}