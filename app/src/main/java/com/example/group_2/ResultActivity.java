package com.example.group_2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import android.media.SoundPool;

public class ResultActivity extends AppCompatActivity {
    private SoundPool soudPool;
    private int buttonSoundId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_layout);

        soudPool = new SoundPool.Builder().setMaxStreams(1).build();
        buttonSoundId = soudPool.load(this, R.raw.button, 1);

        TextView tvRank1 = findViewById(R.id.tvRank1);
        TextView tvRank2 = findViewById(R.id.tvRank2);
        TextView tvRank3 = findViewById(R.id.tvRank3);
        TextView tvWinLose = findViewById(R.id.tvWinLose);
        TextView tvMoneyResult = findViewById(R.id.tvMoneyResult);

        Button btnPlayAgain = findViewById(R.id.btnPlayAgain);
        Button btnExit = findViewById(R.id.btnExit);

        Intent intent = getIntent();
        ArrayList<Integer> rank = intent.getIntegerArrayListExtra("rank");
        String winLoseMsg = intent.getStringExtra("winLose");
        int balance = intent.getIntExtra("balance", 0);

        // Gán kết quả
        if (rank != null && rank.size() >= 3) {
            tvRank1.setText("🥇 Top 1. Horse " + rank.get(0));
            tvRank2.setText("🥈 Top 2. Horse " + rank.get(1));
            tvRank3.setText("🥉 Top 3. Horse " + rank.get(2));
        }

        tvWinLose.setText(winLoseMsg);
        if (winLoseMsg.contains("THẮNG")) {
            tvWinLose.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        } else {
            tvWinLose.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }
        tvMoneyResult.setText("Số dư: $" + balance);

        btnPlayAgain.setOnClickListener(v -> {
            soudPool.play(buttonSoundId, 1, 1, 0, 0, 1);
            Intent i = new Intent(ResultActivity.this, MainActivity.class);
            i.putExtra("user", getIntent().getSerializableExtra("user"));
            startActivity(i);
            finish();
        });

        btnExit.setOnClickListener(v -> {
            soudPool.play(buttonSoundId, 1, 1, 0, 0, 1);
            finishAffinity();
        });
    }
}