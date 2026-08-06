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
        // 设置每晚9点提醒
        ReminderReceiver.setReminderAlarm(this);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = getString(R.string.notif_channel_name);
            String desc = getString(R.string.notif_channel_desc);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(desc);

            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) {
                nm.createNotificationChannel(channel);
            }
        }
    }
}
