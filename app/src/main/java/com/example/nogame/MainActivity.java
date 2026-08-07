package com.example.nogame;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private StreakManager streakManager;
    private TextView tvStreak;
    private TextView tvBestStreak;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        streakManager = new StreakManager(this);
        streakManager.checkNewDay();

        tvStreak = findViewById(R.id.tv_streak);
        tvBestStreak = findViewById(R.id.tv_best_streak);
        tvStatus = findViewById(R.id.tv_status);
        TextView btnYes = findViewById(R.id.btn_yes);
        TextView btnNo = findViewById(R.id.btn_no);

        updateDisplay();

        btnYes.setOnClickListener(v -> onConfirmYes());
        btnNo.setOnClickListener(v -> onConfirmNo());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFinishing() && !streakManager.isTodayConfirmed()) {
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
        Toast.makeText(this, "太棒了！已连续坚持 " + newStreak + " 天", Toast.LENGTH_LONG).show();
    }

    private void onConfirmNo() {
        if (streakManager.isTodayConfirmed()) {
            Toast.makeText(this, "今天已经确认过了，明天继续加油！", Toast.LENGTH_SHORT).show();
            return;
        }
        streakManager.confirmNo();
        updateDisplay();
        Toast.makeText(this, "没关系，明天重新开始！加油", Toast.LENGTH_LONG).show();
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
            tvStatus.setText(current > 0 ? "今天已完成确认" : "今天已破戒，明天重新开始吧");
        } else {
            tvStatus.setText("等待今晚9点打卡确认...");
        }
    }

    private void showConfirmDialog() {
        if (isFinishing()) return;

        new AlertDialog.Builder(this)
                .setTitle(R.string.question_title)
                .setMessage(R.string.question_message)
                .setCancelable(false)
                .setPositiveButton(R.string.btn_yes, (dialog, which) -> {
                    int newStreak = streakManager.confirmYes();
                    updateDisplay();
                    Toast.makeText(this, "太棒了！已连续坚持 " + newStreak + " 天", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton(R.string.btn_no, (dialog, which) -> {
                    streakManager.confirmNo();
                    updateDisplay();
                    Toast.makeText(this, "没关系，明天重新开始！加油", Toast.LENGTH_LONG).show();
                })
                .show();
    }
}
