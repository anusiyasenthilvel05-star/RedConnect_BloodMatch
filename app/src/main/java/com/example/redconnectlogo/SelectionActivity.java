package com.example.redconnectlogo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SelectionActivity extends AppCompatActivity {

    Button btnDonor, btnRecipient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        btnDonor = findViewById(R.id.btnDonor);
        btnRecipient = findViewById(R.id.btnRecipient);

        // Donor Button Click
        btnDonor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SelectionActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Recipient Button Click
        btnRecipient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SelectionActivity.this, VerificationActivity.class);
                startActivity(intent);
            }
        });
    }
}
