package com.example.redconnectlogo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    Button signInBtn;
    TextView registerBtn, forgotPasswordBtn;
    ImageView eyeIcon;

    EditText usernameBox, passwordBox;

    boolean isPasswordVisible = false;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        signInBtn = findViewById(R.id.b5);
        registerBtn = findViewById(R.id.b3);
        forgotPasswordBtn = findViewById(R.id.b4);

        usernameBox = findViewById(R.id.p3);
        passwordBox = findViewById(R.id.p4);

        eyeIcon = findViewById(R.id.img);

        // 👁 Password show/hide
        eyeIcon.setOnClickListener(v -> {

            if (isPasswordVisible) {

                passwordBox.setTransformationMethod(
                        PasswordTransformationMethod.getInstance());
                eyeIcon.setImageResource(R.drawable.ic_eye_closed);

            } else {

                passwordBox.setTransformationMethod(
                        HideReturnsTransformationMethod.getInstance());
                eyeIcon.setImageResource(R.drawable.ic_eye_open);
            }

            passwordBox.setSelection(passwordBox.getText().length());
            isPasswordVisible = !isPasswordVisible;
        });

        // 🔐 LOGIN BUTTON
        signInBtn.setOnClickListener(v -> {

            String email = usernameBox.getText().toString().trim();
            String password = passwordBox.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                usernameBox.setError("Enter Email");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                passwordBox.setError("Enter Password");
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {

                            Toast.makeText(LoginActivity.this,
                                    "Login Successful",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(
                                    LoginActivity.this,
                                    MenuActivity.class);

                            startActivity(intent);
                            finish();

                        } else {

                            Toast.makeText(LoginActivity.this,
                                    "Invalid Email or Password",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // 📝 Register
        registerBtn.setOnClickListener(v -> {

            Intent intent =
                    new Intent(LoginActivity.this,
                            DonorRegistrationActivity.class);

            startActivity(intent);
        });

        // 🔄 Forgot Password
        forgotPasswordBtn.setOnClickListener(v -> {

            Intent intent =
                    new Intent(LoginActivity.this,
                            ResetPasswordActivity.class);

            startActivity(intent);
        });
    }

}