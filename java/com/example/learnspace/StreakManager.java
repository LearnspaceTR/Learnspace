package com.example.learnspace;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

public class StreakManager {

    private static final String PREF_NAME = "MyPrefs";
    private static final String LAST_LOGIN_DATE_KEY = "lastLoginDate";
    private static final String LAST_SUCCESSFUL_LOGIN_DATE_KEY = "lastSuccessfulLoginDate";
    private static final String STREAK_COUNT_KEY = "streakCount";

    public static boolean isFirstLoginToday(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Get last login date from SharedPreferences
        long lastLoginDateInMillis = preferences.getLong(LAST_LOGIN_DATE_KEY, 0);
        Calendar lastLoginCalendar = Calendar.getInstance();
        lastLoginCalendar.setTimeInMillis(lastLoginDateInMillis);

        // Get current date
        Calendar currentCalendar = Calendar.getInstance();

        // Compare last login date with current date
        if (lastLoginCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                lastLoginCalendar.get(Calendar.DAY_OF_YEAR) == currentCalendar.get(Calendar.DAY_OF_YEAR)) {
            // Same day, not the first login of the day
            return false;
        } else {
            // Different day, first login of the day
            // Update last login date to current date
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(LAST_LOGIN_DATE_KEY, currentCalendar.getTimeInMillis());
            editor.apply();

            // Update streak count
            updateStreakCount(context);

            return true;
        }
    }

    public static boolean hasMissedLogin(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Get last successful login date from SharedPreferences
        long lastSuccessfulLoginDateInMillis = preferences.getLong(LAST_SUCCESSFUL_LOGIN_DATE_KEY, 0);
        Calendar lastSuccessfulLoginCalendar = Calendar.getInstance();
        lastSuccessfulLoginCalendar.setTimeInMillis(lastSuccessfulLoginDateInMillis);

        // Get current date
        Calendar currentCalendar = Calendar.getInstance();

        // Check if there is a day gap between the last successful login and the current login
        if (currentCalendar.getTimeInMillis() - lastSuccessfulLoginCalendar.getTimeInMillis() > 24 * 60 * 60 * 1000) {
            // More than one day has passed since the last successful login
            return true;
        } else {
            // No day gap, user logged in yesterday or today
            return false;
        }
    }

    private static void updateStreakCount(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int streakCount = preferences.getInt(STREAK_COUNT_KEY, 0);

        // Check if there's a missed login
        if (hasMissedLogin(context)) {
            // If missed login, reset streak count
            streakCount = 0;
            Users.dailyloginstreak = streakCount;
        } else {
            // If no missed login, increment streak count
            streakCount++;
            Users.dailyloginstreak = streakCount;
        }

        // Update streak count in SharedPreferences
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(STREAK_COUNT_KEY, streakCount);
        editor.apply();
    }

    public static int getStreakCount(HomeFragment context) {
        SharedPreferences preferences = context.getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(STREAK_COUNT_KEY, 0);
    }
}

