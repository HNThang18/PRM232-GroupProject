package com.example.group_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.media.SoundPool;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.group_2.entities.User;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private ArrayList<User> users = new ArrayList<>();
    private SoundPool soudPool;
    private int buttonSoundId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        startService(new Intent(this, BackgroundMusicService.class));

        soudPool = new SoundPool.Builder().setMaxStreams(1).build();
        buttonSoundId = soudPool.load(this, R.raw.button, 1);

        // Ánh xạ view
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        // Lấy danh sách user từ SharedPreferences
        getUsersFromPreferences();

        // Sự kiện Login
        btnLogin.setOnClickListener(v -> {
            soudPool.play(buttonSoundId, 1, 1, 0, 0, 1);
            login();
        });

        // Chuyển sang màn hình Register
        tvRegister.setOnClickListener(v -> {
            soudPool.play(buttonSoundId, 1, 1, 0, 0, 1);
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }

    private void getUsersFromPreferences() {
        SharedPreferences sp = getSharedPreferences("Users", MODE_PRIVATE);
        for (String username : sp.getAll().keySet()) {
            String password = sp.getString(username, "");
            users.add(new User(username, password));
        }
    }
    private void login() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty()) {
            edtUsername.setError("Username is required");
            return;
        }
        if (password.isEmpty()) {
            edtPassword.setError("Password is required");
            return;
        }

        User loggedUser = null;
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                loggedUser = u; // gán thẳng user tìm thấy
                loggedUser.setMoney(1000); // nếu muốn reset money = 0
                break;
            }
        }

        if (loggedUser != null) {
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("user", loggedUser);  // gửi object qua intent

            stopService(new Intent(this, BackgroundMusicService.class));

            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
        }
    }

}
