package com.example.nogame;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

public class StreakManager {

    private static final String PREFS_NAME = "streak_prefs";
    private static final String KEY_CURRENT_STREAK = "current_streak";
    private static final String KEY_BEST_STREAK = "best_streak";
    private static final String KEY_LAST_CONFIRM_DATE = "last_confirm_date";
    private static final String KEY_TODAY_CONFIRMED = "today_confirmed";

    private final SharedPreferences prefs;

    public StreakManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public int getCurrentStreak() {
        return prefs.getInt(KEY_CURRENT_STREAK, 0);
    }

    public int getBestStreak() {
        return prefs.getInt(KEY_BEST_STREAK, 0);
    }

    public boolean isTodayConfirmed() {
        return prefs.getBoolean(KEY_TODAY_CONFIRMED, false);
    }

    public int confirmYes() {
        int current = getCurrentStreak() + 1;
        int best = getBestStreak();

        prefs.edit()
                .putInt(KEY_CURRENT_STREAK, current)
                .putInt(KEY_BEST_STREAK, Math.max(current, best))
                .putString(KEY_LAST_CONFIRM_DATE, getTodayDateStr())
                .putBoolean(KEY_TODAY_CONFIRMED, true)
                .apply();

        return current;
    }

    public void confirmNo() {
        prefs.edit()
                .putInt(KEY_CURRENT_STREAK, 0)
                .putString(KEY_LAST_CONFIRM_DATE, getTodayDateStr())
                .putBoolean(KEY_TODAY_CONFIRMED, true)
                .apply();
    }

    public void checkNewDay() {
        String lastDate = prefs.getString(KEY_LAST_CONFIRM_DATE, null);
        if (lastDate == null) return;

        String today = getTodayDateStr();
        if (today.equals(lastDate)) return;

        String yesterday = getYesterdayDateStr();
        SharedPreferences.Editor editor = prefs.edit();

        if (!lastDate.equals(yesterday)) {
            editor.putInt(KEY_CURRENT_STREAK, 0);
        }

        editor.putBoolean(KEY_TODAY_CONFIRMED, false);
        editor.apply();
    }

    private static String getTodayDateStr() {
        Calendar c = Calendar.getInstance();
        return String.format("%04d-%02d-%02d",
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH) + 1,
                c.get(Calendar.DAY_OF_MONTH));
    }

    private static String getYesterdayDateStr() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -1);
        return String.format("%04d-%02d-%02d",
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH) + 1,
                c.get(Calendar.DAY_OF_MONTH));
    }
}
