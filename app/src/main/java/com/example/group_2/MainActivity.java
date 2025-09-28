package com.example.group_2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBarH1, progressBarH2, progressBarH3;
    ImageView iconH1, iconH2, iconH3;

    Button btnStart, btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        AnhXa();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnStart.setEnabled(false);
                btnReset.setEnabled(false);
                startRaceForHorse3();
            }
        });
    }


    private void AnhXa(){
        btnStart = (Button) findViewById(R.id.btnStart);
        btnReset = (Button) findViewById(R.id.btnReset);

        progressBarH3 = (ProgressBar) findViewById(R.id.progressHorse3);
        iconH3 = (ImageView) findViewById(R.id.iconHorse3);
    }

    private void updateHorse3Position() {
        progressBarH3.post(new Runnable() {
            @Override
            public void run() {
                int progress = progressBarH3.getProgress();
                int max = progressBarH3.getMax();

                // Chiều rộng thực tế của progressBar
                // Cần đảm bảo iconH3.getWidth() có giá trị hợp lệ (view đã được vẽ)
                int barWidth = progressBarH3.getWidth() - iconH3.getWidth();

                // Tính toán vị trí icon
                float posX = (progress / (float) max) * barWidth;

                // Set vị trí bằng dịch chuyển ngang
                iconH3.setTranslationX(posX);
            }
        });
    }



    private void startRaceForHorse3() {
        iconH3.setImageResource(R.drawable.horse_running_icon);
        progressBarH3.setProgress(2);
        updateHorse3Position();

        Thread thread = new Thread(new Runnable() { // Thread chính
            @Override
            public void run() {
                for (int i = 0; i <= 100; i++){
                    try {
                        Thread.sleep(50); // Thời gian nghỉ giữa các bước tiến độ
                    } catch (InterruptedException e){
                        e.printStackTrace();
                        // Nếu thread bị gián đoạn, có thể thoát vòng lặp
                        break;
                    }
                    int finali = i;
                    runOnUiThread(new Runnable() { // Sử dụng runOnUiThread để cập nhật UI trong Thread chính
                        @Override
                        public void run() {
                            if (finali <= progressBarH3.getMax()) { // Đảm bảo không vượt quá max
                                progressBarH3.setProgress(finali);
                                updateHorse3Position();
                            }
                        }
                    });
                }
                // Khi dừng -> đổi icon đứng
                runOnUiThread(() -> {
                    iconH3.setImageResource(R.drawable.horse_stop_icon);
                    btnStart.setEnabled(false);
                    btnReset.setEnabled(true);
                });
            }
        });

        thread.start();
    }
}