package com.example.group_2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.group_2.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ProgressBar progress1, progress2, progress3;
    ImageView horse1, horse2, horse3;
    Button btnStart, btnReset;

    CheckBox cb1, cb2, cb3;

    TextView tvBalanceNum, tvUsername;

    Random random = new Random();
    Handler handler = new Handler(Looper.getMainLooper());

    ArrayList<Integer> rank = new ArrayList<>();

    volatile boolean raceFinished = false; // để biết đã có Winner chưa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        btnStart.setOnClickListener(v -> startRace());
        btnReset.setOnClickListener(v -> resetRace());
    }

    private void startRace() {
        raceFinished = false;
        rank.clear();

        btnStart.setEnabled(false);
        btnReset.setEnabled(false);

        progress1.setProgress(0);
        progress2.setProgress(0);
        progress3.setProgress(0);

        horse1.setTranslationX(0);
        horse2.setTranslationX(0);
        horse3.setTranslationX(0);

        Glide.with(MainActivity.this)
                .asGif()
                .load(R.drawable.running_horse) // file horse_running.gif đặt trong res/drawable
                .into(horse1);

        Glide.with(MainActivity.this)
                .asGif()
                .load(R.drawable.running_horse)
                .into(horse2);

        Glide.with(MainActivity.this)
                .asGif()
                .load(R.drawable.running_horse)
                .into(horse3);

        runHorse(1, horse1, progress1);
        runHorse(2, horse2, progress2);
        runHorse(3, horse3, progress3);
    }

    private void runHorse(int horseNumber, ImageView horse, ProgressBar progressBar) {
        new Thread(() -> {
            int progress = 0;
            int maxProgress = 2000;
            progressBar.setMax(maxProgress);

            while (progress < maxProgress) { // bỏ raceFinished ở đây
                try {
                    int step = random.nextInt(30) + 1;
                    progress += step;
                    if (progress > maxProgress) progress = maxProgress;

                    int finalProgress = progress;

                    handler.post(() -> {
                        progressBar.setProgress(finalProgress);

                        float maxDistance = progressBar.getWidth() - horse.getWidth();
                        horse.setTranslationX(maxDistance * finalProgress / (float) maxProgress);
                    });

                    Thread.sleep(random.nextInt(20) + 30);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // check khi đạt maxProgress
            if (progress >= maxProgress) {
                handler.post(() -> {
                    horse.setImageResource(R.drawable.horse_stop_icon);

                    // nếu chưa ghi nhận thì thêm vào rank
                    if (!rank.contains(horseNumber)) {
                        rank.add(horseNumber);
                    }

                    // nếu đủ số ngựa về đích thì hiện kết quả
                    if (rank.size() == 3) { // hoặc totalHorses nếu bạn có nhiều hơn 3
                        StringBuilder result = new StringBuilder("Kết quả: ");
                        for (int i = 0; i < rank.size(); i++) {
                            result.append(rank.get(i));
                        }

                        Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_LONG).show();

                        btnStart.setEnabled(true);
                        btnReset.setEnabled(true);
                    }
                });
            }

        }).start();
    }


    private void resetRace() {
        progress1.setProgress(0);
        progress2.setProgress(0);
        progress3.setProgress(0);

        horse1.setTranslationX(0);
        horse2.setTranslationX(0);
        horse3.setTranslationX(0);

        horse1.setImageResource(R.drawable.horse_stop_icon);
        horse2.setImageResource(R.drawable.horse_stop_icon);
        horse3.setImageResource(R.drawable.horse_stop_icon);

        raceFinished = false;
    }

    private void initViews() {
        User user = (User) getIntent().getSerializableExtra("user");

        View horseView1 = findViewById(R.id.horse1);
        View horseView2 = findViewById(R.id.horse2);
        View horseView3 = findViewById(R.id.horse3);

        progress1 = horseView1.findViewById(R.id.progressHorse);
        progress2 = horseView2.findViewById(R.id.progressHorse);
        progress3 = horseView3.findViewById(R.id.progressHorse);

        cb1 = horseView1.findViewById(R.id.cbHorse);
        cb2 = horseView2.findViewById(R.id.cbHorse);
        cb3 = horseView3.findViewById(R.id.cbHorse);

        cb1.setText("Horse 1");
        cb2.setText("Horse 2");
        cb3.setText("Horse 3");

        horse1 = horseView1.findViewById(R.id.iconHorse);
        horse2 = horseView2.findViewById(R.id.iconHorse);
        horse3 = horseView3.findViewById(R.id.iconHorse);

        tvBalanceNum = findViewById(R.id.tvBalanceNum);
        tvBalanceNum.setText(String.valueOf(user.getMoney()));

        tvUsername = findViewById(R.id.tvUsername);
        tvUsername.setText("Username: " + user.getUsername());

        btnStart = findViewById(R.id.btnStart);
        btnReset = findViewById(R.id.btnReset);
    }
}