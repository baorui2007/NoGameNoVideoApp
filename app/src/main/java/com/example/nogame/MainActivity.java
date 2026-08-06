package com.example.nogame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private StreakManager streakManager;
    private TextView tvStreak;
    private TextView tvBestStreak;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        streakManager = new StreakManager(this);

        // 检查新的一天并重置确认状态
        streakManager.checkNewDay();

        // 初始化 UI
        tvStreak = findViewById(R.id.tv_streak);
        tvBestStreak = findViewById(R.id.tv_best_streak);
        tvStatus = findViewById(R.id.tv_status);
        Button btnYes = findViewById(R.id.btn_yes);
        Button btnNo = findViewById(R.id.btn_no);

        updateDisplay();

        btnYes.setOnClickListener(v -> onConfirmYes());
        btnNo.setOnClickListener(v -> onConfirmNo());

        // 如果今天还没确认，弹出确认对话框
        if (!streakManager.isTodayConfirmed()) {
            showConfirmDialog();
        }
    }

    private void onConfirmYes() {
        if (streakManager.isTodayConfirmed()) {
            Toast.makeText(this, "今天已经确认过了，明天再来吧！", Toast.LENGTH_SHORT).show();
            return;
        }
        int newStreak = streakManager.confirmYes();
        updateDisplay();

        String msg = "太棒了！已连续坚持 " + newStreak + " 天";
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private void onConfirmNo() {
        if (streakManager.isTodayConfirmed()) {
            Toast.makeText(this, "今天已经确认过了，明天继续加油！", Toast.LENGTH_SHORT).show();
            return;
        }
        streakManager.confirmNo();
        updateDisplay();
        Toast.makeText(this, "没关系，明天重新开始！加油 💪", Toast.LENGTH_LONG).show();
    }

    private void updateDisplay() {
        int current = streakManager.getCurrentStreak();
        int best = streakManager.getBestStreak();
        boolean confirmed = streakManager.isTodayConfirmed();

        if (current > 0) {
            tvStreak.setText(getString(R.string.current_streak, current));
        } else {
            tvStreak.setText("今天还没有记录");
        }

        tvBestStreak.setText(getString(R.string.best_streak, best));

        if (confirmed) {
            if (current > 0) {
                tvStatus.setText("今天已完成确认 ✅");
            } else {
                tvStatus.setText("今天已破戒，明天重新开始吧");
            }
        } else {
            tvStatus.setText("等待今晚9点打卡确认...");
        }
    }

    private void showConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.question_title)
                .setMessage(R.string.question_message)
                .setCancelable(false)
                .setPositiveButton(R.string.btn_yes, (dialog, which) -> {
                    int newStreak = streakManager.confirmYes();
                    updateDisplay();
                    String msg = "太棒了！已连续坚持 " + newStreak + " 天";
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                })
                .setNegativeButton(R.string.btn_no, (dialog, which) -> {
                    streakManager.confirmNo();
                    updateDisplay();
                    Toast.makeText(this, "没关系，明天重新开始！加油 💪", Toast.LENGTH_LONG).show();
                })
                .show();
    }
}
