package com.example.redconnectlogo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class DonorRegistrationActivity extends AppCompatActivity {

    EditText username, password, name, age, bloodGroup, phone,
            email, state, district, city, pincode;

    CheckBox emergencyCheck, authorizeCheck;
    RadioGroup statusGroup, genderGroup;
    RadioButton availableBtn, unavailableBtn;
    Button submitBtn;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    boolean isEditMode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_registration);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        checkIfUserAlreadyRegistered();

        submitBtn.setOnClickListener(v -> {

            Toast.makeText(DonorRegistrationActivity.this,
                    "Submit button clicked",
                    Toast.LENGTH_SHORT).show();

            if (isEditMode) {
                updateDonorData();
            } else {
                registerDonor();
            }
        });
    }

    private void initializeViews() {
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        bloodGroup = findViewById(R.id.bloodGroup);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        state = findViewById(R.id.state);
        district = findViewById(R.id.district);
        city = findViewById(R.id.city);
        pincode = findViewById(R.id.pincode);

        emergencyCheck = findViewById(R.id.emergencyCheck);
        authorizeCheck = findViewById(R.id.authorizeCheck);
        statusGroup = findViewById(R.id.statusGroup);
        genderGroup = findViewById(R.id.genderGroup);
        availableBtn = findViewById(R.id.available);
        unavailableBtn = findViewById(R.id.unavailable);
        submitBtn = findViewById(R.id.submitBtn);
    }


    private boolean isValidPassword(String pass) {
        Pattern pattern = Pattern.compile(
                "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=!]).{8,}$"
        );
        return pattern.matcher(pass).matches();
    }

    private void registerDonor() {

        String userEmail = username.getText().toString().trim();
        String userPass = password.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            username.setError("Enter valid Email");
            return;
        }

        if (!isValidPassword(userPass)) {
            password.setError("Password must contain 8 characters, uppercase, lowercase, number & special character");
            return;
        }

        if (!authorizeCheck.isChecked()) {
            Toast.makeText(this, "Please accept authorization", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(userEmail, userPass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        saveDonorToFirestore(userEmail);

                        Toast.makeText(this, "Registration Successful", Toast.LENGTH_LONG).show();

                        startActivity(new Intent(this, LoginActivity.class));
                        finish();

                    } else {
                        Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void saveDonorToFirestore(String userEmail) {

        FirebaseUser user = mAuth.getCurrentUser();

        Map<String, Object> donor = new HashMap<>();
        donor.put("uid", user.getUid());
        donor.put("email", userEmail);
        donor.put("name", name.getText().toString());
        donor.put("age", age.getText().toString());
        donor.put("bloodGroup", bloodGroup.getText().toString());
        donor.put("phone", phone.getText().toString());
        donor.put("state", state.getText().toString());
        donor.put("district", district.getText().toString());
        donor.put("city", city.getText().toString());
        donor.put("pincode", pincode.getText().toString());
        donor.put("emergency", emergencyCheck.isChecked());
        donor.put("authorized", authorizeCheck.isChecked());
        donor.put("status", availableBtn.isChecked() ? "Available" : "Unavailable");

        db.collection("Donors")
                .document(user.getUid())
                .set(donor);
    }


    private void checkIfUserAlreadyRegistered() {

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        db.collection("Donors")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {

                        isEditMode = true;

                        username.setText(document.getString("email"));
                        username.setEnabled(false);
                        password.setEnabled(false);
                        emergencyCheck.setEnabled(false);
                        authorizeCheck.setEnabled(false);

                        name.setText(document.getString("name"));
                        age.setText(document.getString("age"));
                        bloodGroup.setText(document.getString("bloodGroup"));
                        phone.setText(document.getString("phone"));
                        state.setText(document.getString("state"));
                        district.setText(document.getString("district"));
                        city.setText(document.getString("city"));
                        pincode.setText(document.getString("pincode"));

                        if ("Available".equals(document.getString("status")))
                            availableBtn.setChecked(true);
                        else
                            unavailableBtn.setChecked(true);
                    }
                });
    }


    private void updateDonorData() {

        FirebaseUser user = mAuth.getCurrentUser();

        Map<String, Object> update = new HashMap<>();
        update.put("name", name.getText().toString());
        update.put("age", age.getText().toString());
        update.put("bloodGroup", bloodGroup.getText().toString());
        update.put("phone", phone.getText().toString());
        update.put("state", state.getText().toString());
        update.put("district", district.getText().toString());
        update.put("city", city.getText().toString());
        update.put("pincode", pincode.getText().toString());

        db.collection("Donors")
                .document(user.getUid())
                .update(update)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Updated Successfully", Toast.LENGTH_LONG).show());
    }
}