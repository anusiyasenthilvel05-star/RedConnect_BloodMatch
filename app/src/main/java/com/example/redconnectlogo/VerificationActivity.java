package com.example.redconnectlogo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerificationActivity extends AppCompatActivity {

    EditText etEmail;
    Button btnSendVerification, btnCheckVerification;

    FirebaseAuth mAuth;
    FirebaseUser user;

    String email;
    String tempPassword = "TempPass@123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        etEmail = findViewById(R.id.emailInput);
        btnSendVerification = findViewById(R.id.btnSendLink);
        btnCheckVerification = findViewById(R.id.btnCheck);

        mAuth = FirebaseAuth.getInstance();

        btnSendVerification.setOnClickListener(v -> sendVerification());

        btnCheckVerification.setOnClickListener(v -> checkVerification());
    }

    private void sendVerification() {

        email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Enter email");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter valid email");
            return;
        }

        // Try creating account first
        mAuth.createUserWithEmailAndPassword(email, tempPassword)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        sendVerificationEmail();

                    } else {

                        // If account already exists, sign in and resend verification
                        mAuth.signInWithEmailAndPassword(email, tempPassword)
                                .addOnCompleteListener(signInTask -> {

                                    if (signInTask.isSuccessful()) {

                                        sendVerificationEmail();

                                    } else {

                                        Toast.makeText(
                                                VerificationActivity.this,
                                                "Error sending verification",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                });
    }

    private void sendVerificationEmail() {

        user = mAuth.getCurrentUser();

        if (user != null) {

            user.sendEmailVerification()
                    .addOnSuccessListener(unused ->
                            Toast.makeText(
                                    this,
                                    "Verification link sent to email",
                                    Toast.LENGTH_LONG).show())

                    .addOnFailureListener(e ->
                            Toast.makeText(
                                    this,
                                    "Failed to send email",
                                    Toast.LENGTH_LONG).show());
        }
    }

    private void checkVerification() {

        user = mAuth.getCurrentUser();

        if (user == null) {

            Toast.makeText(
                    this,
                    "Please send verification email first",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        user.reload().addOnSuccessListener(unused -> {

            if (user.isEmailVerified()) {

                Toast.makeText(
                        this,
                        "Email verified successfully",
                        Toast.LENGTH_LONG).show();

                Intent intent = new Intent(
                        VerificationActivity.this,
                        BloodRequestFormActivity.class);

                startActivity(intent);
                finish();

            } else {

                Toast.makeText(
                        this,
                        "Please verify your email before continuing",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}