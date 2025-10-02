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
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ProgressBar progress1, progress2, progress3;
    ImageView horse1, horse2, horse3;
    Button btnStart, btnReset;

    CheckBox cb1, cb2, cb3;

    TextView tvBalanceNum, tvUsername;

    TextInputEditText edtBet1, edtBet2, edtBet3;

    Map<Integer, Integer> betMap = new HashMap<>();
    boolean betPlaced = false;
    int currentBalance = 100;
    User user;

    Random random = new Random();
    Handler handler = new Handler(Looper.getMainLooper());

    ArrayList<Integer> rank = new ArrayList<>();

    volatile boolean raceFinished = false; // để biết đã có Winner chưa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        //btnStart.setOnClickListener(v -> startRace());
        btnStart.setOnClickListener(v -> {
            if (!betPlaced) {
                placeBet();
                if (!betPlaced) return;
            }
            startRace();
        });

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

                        resolveBet(rank.get(0)); // chỉ con về nhất

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
        betPlaced = false;
        betMap.clear();
        edtBet1.setText("");
        edtBet2.setText("");
        edtBet3.setText("");
        cb1.setChecked(false);
        cb2.setChecked(false);
        cb3.setChecked(false);

    }

    private void initViews() {
        user = (User) getIntent().getSerializableExtra("user");

        View horseView1 = findViewById(R.id.horse1);
        View horseView2 = findViewById(R.id.horse2);
        View horseView3 = findViewById(R.id.horse3);

        progress1 = horseView1.findViewById(R.id.progressHorse);
        progress2 = horseView2.findViewById(R.id.progressHorse);
        progress3 = horseView3.findViewById(R.id.progressHorse);

        cb1 = horseView1.findViewById(R.id.cbHorse);
        cb2 = horseView2.findViewById(R.id.cbHorse);
        cb3 = horseView3.findViewById(R.id.cbHorse);

        edtBet1 = horseView1.findViewById(R.id.edtBet);
        edtBet2 = horseView2.findViewById(R.id.edtBet);
        edtBet3 = horseView3.findViewById(R.id.edtBet);

        cb1.setText("Horse 1");
        cb2.setText("Horse 2");
        cb3.setText("Horse 3");

        horse1 = horseView1.findViewById(R.id.iconHorse);
        horse2 = horseView2.findViewById(R.id.iconHorse);
        horse3 = horseView3.findViewById(R.id.iconHorse);

        tvBalanceNum = findViewById(R.id.tvBalanceNum);
        if (user != null) {
            currentBalance = user.getMoney();
        }
        tvBalanceNum.setText("$" + currentBalance);

        tvUsername = findViewById(R.id.tvUsername);
        tvUsername.setText("Username: " + user.getUsername());

        btnStart = findViewById(R.id.btnStart);
        btnReset = findViewById(R.id.btnReset);

        cb1.setOnCheckedChangeListener((buttonView, isChecked) -> updateBalancePreview());
        cb2.setOnCheckedChangeListener((buttonView, isChecked) -> updateBalancePreview());
        cb3.setOnCheckedChangeListener((buttonView, isChecked) -> updateBalancePreview());

        edtBet1.addTextChangedListener(new SimpleTextWatcher(() -> updateBalancePreview()));
        edtBet2.addTextChangedListener(new SimpleTextWatcher(() -> updateBalancePreview()));
        edtBet3.addTextChangedListener(new SimpleTextWatcher(() -> updateBalancePreview()));
    }

    private void placeBet() {
        betMap.clear();

        if (cb1.isChecked()) {
            String s = edtBet1.getText().toString().trim();
            if (s.isEmpty()) { Toast.makeText(this, "Enter bet for Horse 1", Toast.LENGTH_SHORT).show(); return; }
            try {
                int b = Integer.parseInt(s);
                if (b <= 0) { Toast.makeText(this, "Bet must be >0 for Horse 1", Toast.LENGTH_SHORT).show(); return; }
                betMap.put(1, b);
            } catch (NumberFormatException e) { Toast.makeText(this, "Invalid number Horse 1", Toast.LENGTH_SHORT).show(); return; }
        }

        if (cb2.isChecked()) {
            String s = edtBet2.getText().toString().trim();
            if (s.isEmpty()) { Toast.makeText(this, "Enter bet for Horse 2", Toast.LENGTH_SHORT).show(); return; }
            try {
                int b = Integer.parseInt(s);
                if (b <= 0) { Toast.makeText(this, "Bet must be >0 for Horse 2", Toast.LENGTH_SHORT).show(); return; }
                betMap.put(2, b);
            } catch (NumberFormatException e) { Toast.makeText(this, "Invalid number Horse 2", Toast.LENGTH_SHORT).show(); return; }
        }

        if (cb3.isChecked()) {
            String s = edtBet3.getText().toString().trim();
            if (s.isEmpty()) { Toast.makeText(this, "Enter bet for Horse 3", Toast.LENGTH_SHORT).show(); return; }
            try {
                int b = Integer.parseInt(s);
                if (b <= 0) { Toast.makeText(this, "Bet must be >0 for Horse 3", Toast.LENGTH_SHORT).show(); return; }
                betMap.put(3, b);
            } catch (NumberFormatException e) { Toast.makeText(this, "Invalid number Horse 3", Toast.LENGTH_SHORT).show(); return; }
        }

        if (betMap.isEmpty()) {
            Toast.makeText(this, "Please select at least one horse and enter bet.", Toast.LENGTH_SHORT).show();
            return;
        }

        int totalStake = 0;
        for (int v : betMap.values()) totalStake += v;

        if (totalStake > currentBalance) {
            Toast.makeText(this, "Total stake ("+totalStake+"$) exceeds balance ("+currentBalance+"$).", Toast.LENGTH_LONG).show();
            return;
        }

        betPlaced = true;
        Toast.makeText(this, "Bet placed: " + betMap.toString() + "  (total: $" + totalStake + ")", Toast.LENGTH_LONG).show();
    }

    private void resolveBet(int winner) {
        int netChange = 0;

        for (Map.Entry<Integer, Integer> entry : betMap.entrySet()) {
            int horseNum = entry.getKey();
            int betAmount = entry.getValue();

            if (horseNum == winner) {
                // nếu con ngựa này về nhất -> cộng (tiền cược + thưởng = 2 lần cược)
                netChange += betAmount;  // lãi thêm = số tiền cược
            } else {
                // nếu không phải -> mất tiền cược
                netChange -= betAmount;
            }
        }

        currentBalance += netChange;
        if (currentBalance < 0) currentBalance = 0;

        String msg;
        if (netChange >= 0) {
            msg = "Winner: Horse " + winner + ". You WIN! +" + netChange + " $";
        } else {
            msg = "Winner: Horse " + winner + ". You LOST " + (-netChange) + " $";
        }

        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        tvBalanceNum.setText("$" + currentBalance);

        betPlaced = false;
        betMap.clear();
        edtBet1.setText("");
        edtBet2.setText("");
        edtBet3.setText("");
        cb1.setChecked(false);
        cb2.setChecked(false);
        cb3.setChecked(false);

        if (user != null) {
            user.setMoney(currentBalance);
        }
    }
    private void updateBalancePreview() {
        int totalStake = 0;

        // đọc từ horse 1
        if (cb1.isChecked()) {
            String s = edtBet1.getText().toString().trim();
            if (!s.isEmpty()) {
                try { totalStake += Integer.parseInt(s); } catch (NumberFormatException ignored) {}
            }
        }
        // horse 2
        if (cb2.isChecked()) {
            String s = edtBet2.getText().toString().trim();
            if (!s.isEmpty()) {
                try { totalStake += Integer.parseInt(s); } catch (NumberFormatException ignored) {}
            }
        }
        // horse 3
        if (cb3.isChecked()) {
            String s = edtBet3.getText().toString().trim();
            if (!s.isEmpty()) {
                try { totalStake += Integer.parseInt(s); } catch (NumberFormatException ignored) {}
            }
        }

        // Balance hiển thị = currentBalance - totalStake
        int previewBalance = currentBalance - totalStake;
        if (previewBalance < 0) previewBalance = 0;
        tvBalanceNum.setText("$" + previewBalance);
    }
    // tiện dụng để không phải override hết 3 hàm
    private static class SimpleTextWatcher implements android.text.TextWatcher {
        private Runnable callback;
        SimpleTextWatcher(Runnable callback) { this.callback = callback; }

        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override public void afterTextChanged(android.text.Editable s) { callback.run(); }
    }


}