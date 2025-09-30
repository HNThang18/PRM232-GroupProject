package com.example.group_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText edtUsername, edtPassword, edtConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Ánh xạ view
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        // Sự kiện Register
        btnRegister.setOnClickListener(v -> register());

        // Quay lại Login
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void register() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirm = edtConfirmPassword.getText().toString().trim();

        if (username.isEmpty()) {
            edtUsername.setError("Username is required");
            return;
        }
        if (password.isEmpty()) {
            edtPassword.setError("Password is required");
            return;
        }
        if (confirm.isEmpty()) {
            edtConfirmPassword.setError("Confirm Password is required");
            return;
        }
        if (!password.equals(confirm)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sp = getSharedPreferences("Users", MODE_PRIVATE);
        if (sp.contains(username)) {
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = sp.edit();
        editor.putString(username, password);
        editor.apply();

        Toast.makeText(this, "Register successful", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }
}
