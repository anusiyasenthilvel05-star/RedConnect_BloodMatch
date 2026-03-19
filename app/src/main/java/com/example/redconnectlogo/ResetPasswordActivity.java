package com.example.redconnectlogo; // change to your package name

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.redconnectlogo.R;
import com.google.android.material.textfield.TextInputEditText;

public class ResetPasswordActivity extends AppCompatActivity {

    TextInputEditText etNewPassword, etConfirmPassword;
    Button btnContinue, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnContinue = findViewById(R.id.btnContinue);
        btnCancel = findViewById(R.id.btnCancel);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newPassword = etNewPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();
                if (TextUtils.isEmpty(newPassword)) {
                    etNewPassword.setError("Enter new password");
                    return;
                }

                if (TextUtils.isEmpty(confirmPassword)) {
                    etConfirmPassword.setError("Enter confirm password");
                    return;
                }
                if (!newPassword.equals(confirmPassword)) {

                    Toast.makeText(ResetPasswordActivity.this,
                            "Password does not match",
                            Toast.LENGTH_SHORT).show();

                    return;
                }
                Toast.makeText(ResetPasswordActivity.this,
                        "Password updated successfully",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ResetPasswordActivity.this,
                        LoginActivity.class);

                startActivity(intent);

                finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPasswordActivity.this,
                        LoginActivity.class);

                startActivity(intent);

                finish();
            }
        });
    }
}
