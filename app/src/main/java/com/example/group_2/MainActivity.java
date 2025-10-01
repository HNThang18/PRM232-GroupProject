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

import com.example.group_2.entities.User;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ProgressBar progress1, progress2, progress3;
    ImageView horse1, horse2, horse3;
    Button btnStart, btnReset;

    CheckBox cb1, cb2, cb3;

    TextView tvBalanceNum, tvUsername;

    Random random = new Random();
    Handler handler = new Handler(Looper.getMainLooper());

    int winner;

    volatile boolean raceFinished = false; // để biết đã có Winner chưa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        btnStart.setOnClickListener(v -> startRace());
        btnReset.setOnClickListener(v -> resetRace());
    }

    private void startRace() {
        raceFinished = false;

        btnStart.setEnabled(false);
        btnReset.setEnabled(false);

        progress1.setProgress(0);
        progress2.setProgress(0);
        progress3.setProgress(0);

        horse1.setTranslationX(0);
        horse2.setTranslationX(0);
        horse3.setTranslationX(0);

        horse1.setImageResource(R.drawable.horse_running_icon);
        horse2.setImageResource(R.drawable.horse_running_icon);
        horse3.setImageResource(R.drawable.horse_running_icon);

        runHorse(1, horse1, progress1);
        runHorse(2, horse2, progress2);
        runHorse(3, horse3, progress3);
    }

    private void runHorse(int horseNumber, ImageView horse, ProgressBar progressBar) {
        new Thread(() -> {
            int progress = 0;
            int maxProgress = 2000;
            progressBar.setMax(maxProgress);

            while (progress < maxProgress && !raceFinished) {
                try {
                    int step = random.nextInt(25) + 1;
                    progress += step;
                    if (progress > maxProgress) progress = maxProgress;

                    int finalProgress = progress;

                    handler.post(() -> {
                        progressBar.setProgress(finalProgress);

                        float maxDistance = progressBar.getWidth() - horse.getWidth();
                        horse.setTranslationX(maxDistance * finalProgress / (float) maxProgress);
                    });

                    Thread.sleep(random.nextInt(20) + 30); // 30–50ms

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // check khi đạt maxProgress
            if (progress >= maxProgress && !raceFinished) {
                raceFinished = true;
                handler.post(() -> {
                    horse.setImageResource(R.drawable.horse_stop_icon);
                    winner = horseNumber;
                    Toast.makeText(MainActivity.this,
                            "🏆 Horse " + horseNumber + " đã thắng!",
                            Toast.LENGTH_LONG).show();

                    btnStart.setEnabled(true);
                    btnReset.setEnabled(true);
                });
            } else {
                handler.post(() -> horse.setImageResource(R.drawable.horse_stop_icon));
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
}