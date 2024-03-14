package com.example.learnspace;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.learnspace.databinding.ActivityQuizScoreBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class QuizScoreActivity extends AppCompatActivity {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ActivityQuizScoreBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyTheme();
        super.onCreate(savedInstanceState);
        binding = ActivityQuizScoreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //gaining gems for studying
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        Toast.makeText(this, "You gained 10 gems through completing your quiz", Toast.LENGTH_SHORT).show();
        int newGemsValue = Users.gems + 10;
        userRef.child("gems").setValue(newGemsValue);
        Users.gems = newGemsValue;

        //correct and incorrect answers
        int correct = getIntent().getIntExtra("correctAnswers", 0);
        int totalQuestions = getIntent().getIntExtra("totalQuestions", 0);
        int wrong = totalQuestions - correct;

        binding.correctQuestions.setText(String.valueOf(correct));
        binding.wrongQuestions.setText(String.valueOf(wrong));
        binding.totalQuestions.setText(String.valueOf(totalQuestions));

        binding.correctQuestionsCircle.setText(String.valueOf(correct));
        binding.totalQuestionsCircle.setText(String.valueOf(totalQuestions));
        binding.progressBar.setProgressMax(totalQuestions);
        binding.progressBar.setProgress(totalQuestions);
        binding.progressBar.setProgress(correct);

        binding.retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuizScoreActivity.this, QuizActivity.class); // Change QuizActivity to the name of your quiz activity
                startActivity(intent);
                finish(); // Finish the current activity
            }
        });

        binding.quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuizScoreActivity.this, MainActivity.class); // Change QuizActivity to the name of your quiz activity
                startActivity(intent);
                finish(); // Finish the current activity
            }
        });
    }
    private void applyTheme() {
        // Get the saved theme preference
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String selectedTheme = preferences.getString("theme", "default");

        // Set the appropriate theme based on the saved preference
        switch (selectedTheme) {
            case "theme1":
                setTheme(R.style.Theme_learnspace_blue);
                break;
            case "theme2":
                setTheme(R.style.Theme_learnspace_green);
                break;
            case "theme3":
                setTheme(R.style.Theme_learnspace_red);
                break;
            case "theme4":
                setTheme(R.style.Theme_learnspace_purple);
                break;
            case "theme5":
                setTheme(R.style.Theme_learnspace_yellow);
                break;
            case "theme6":
                setTheme(R.style.Theme_learnspace_dark);
                break;
            // Add cases for other themes if needed
            default:
                // Use the default theme if no preference is set or if an invalid preference is saved
                setTheme(R.style.Theme_learnspace);
                break;
        }
    }


}