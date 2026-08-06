package com.example.nogame;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

/**
 * 管理自律连续天数数据的存储与读取
 */
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

    /** 获取当前连续天数 */
    public int getCurrentStreak() {
        return prefs.getInt(KEY_CURRENT_STREAK, 0);
    }

    /** 获取历史最长连续天数 */
    public int getBestStreak() {
        return prefs.getInt(KEY_BEST_STREAK, 0);
    }

    /** 今天是否已经确认过 */
    public boolean isTodayConfirmed() {
        return prefs.getBoolean(KEY_TODAY_CONFIRMED, false);
    }

    /**
     * 记录"做到了"：连续天数 +1
     * 返回更新后的连续天数
     */
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

    /**
     * 记录"破戒了"：连续天数重置为 0
     */
    public void confirmNo() {
        prefs.edit()
                .putInt(KEY_CURRENT_STREAK, 0)
                .putString(KEY_LAST_CONFIRM_DATE, getTodayDateStr())
                .putBoolean(KEY_TODAY_CONFIRMED, true)
                .apply();
    }

    /**
     * 重置今天的确认状态（用于第二天自动重置）
     */
    public void resetTodayConfirmed() {
        prefs.edit()
                .putBoolean(KEY_TODAY_CONFIRMED, false)
                .apply();
    }

    /** 获取今天的日期字符串 yyyy-MM-dd */
    public static String getTodayDateStr() {
        Calendar c = Calendar.getInstance();
        return String.format("%04d-%02d-%02d",
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH) + 1,
                c.get(Calendar.DAY_OF_MONTH));
    }

    /** 获取当前日期与上次确认日期的天数差（用于检测新的一天） */
    public int daysSinceLastConfirm() {
        String lastDate = prefs.getString(KEY_LAST_CONFIRM_DATE, null);
        if (lastDate == null) return -1;

        String today = getTodayDateStr();
        if (today.equals(lastDate)) return 0;

        // 简单的跨日检测：如果上次确认不是今天，且不是昨天，则说明跳过了一天以上
        return 1;
    }

    /** 检查并处理新的一天（如果上次确认日期不是今天且不是昨天，则重置连续） */
    public void checkNewDay() {
        String lastDate = prefs.getString(KEY_LAST_CONFIRM_DATE, null);
        if (lastDate == null) return;

        String today = getTodayDateStr();
        if (today.equals(lastDate)) return; // 今天已经确认过

        // 如果上次确认不是昨天，说明跳过了，重置
        String yesterday = getYesterdayDateStr();
        if (!lastDate.equals(yesterday)) {
            // 跳过了一天以上，重置连续天数但保留最佳记录
            prefs.edit()
                    .putInt(KEY_CURRENT_STREAK, 0)
                    .apply();
        }

        // 新的一天，重置今天确认状态
        prefs.edit()
                .putBoolean(KEY_TODAY_CONFIRMED, false)
                .apply();
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
