package com.example.learnspace;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    public SharedPreferences sharedPreferences;
    public static final String PREF_LAST_LOGIN_DATE = "last_login_date";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daily_reward);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        checkDailyLoginReward();
    }

    public void checkDailyLoginReward() {
        String lastLoginDate = sharedPreferences.getString(PREF_LAST_LOGIN_DATE, "");
        String currentDate = getCurrentDate();

        if (!lastLoginDate.equals(currentDate)) {
            // User hasn't logged in today
            // Give daily reward to the user
            giveDailyReward();

            // Save current date as last login date
            sharedPreferences.edit().putString(PREF_LAST_LOGIN_DATE, currentDate).apply();
        }
    }

    public void giveDailyReward() {
        // Logic to give the daily reward to the user
        // Update reward balance, show reward dialog, etc.
        // For example:
        int currentReward = sharedPreferences.getInt("reward_balance", 0);
        int newReward = currentReward + 10; // Assuming the daily reward is 10
        sharedPreferences.edit().putInt("reward_balance", newReward).apply();

        // Show reward dialog or update UI to inform the user about the reward
        Toast.makeText(this, "You've received a daily reward!", Toast.LENGTH_SHORT).show();
    }

    public String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }
}
