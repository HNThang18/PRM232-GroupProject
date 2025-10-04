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
import android.media.SoundPool;

public class RegisterActivity extends AppCompatActivity {
    private EditText edtUsername, edtPassword, edtConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private SoundPool soudPool;
    private int buttonSoundId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        soudPool = new SoundPool.Builder().setMaxStreams(1).build();
        buttonSoundId = soudPool.load(this, R.raw.button, 1);

        // Ánh xạ view
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        // Sự kiện Register
        btnRegister.setOnClickListener(v -> {
            soudPool.play(buttonSoundId, 1, 1, 0, 0, 1);
            register();
        });

        // Quay lại Login
        tvLogin.setOnClickListener(v -> {
            soudPool.play(buttonSoundId, 1, 1, 0, 0, 1);
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
