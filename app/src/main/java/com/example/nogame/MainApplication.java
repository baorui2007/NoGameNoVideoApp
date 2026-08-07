package com.example.nogame;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class MainApplication extends Application {

    public static final String CHANNEL_ID = "daily_reminder";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        // 设备重启或应用更新后重新设置闹钟
        ReminderReceiver.setReminderAlarm(this);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "每日提醒",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("每天晚9点的自律确认提醒");

            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) {
                nm.createNotificationChannel(channel);
            }
        }
    }
}
