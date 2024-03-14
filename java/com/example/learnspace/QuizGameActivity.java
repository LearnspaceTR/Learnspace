package com.example.learnspace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.learnspace.Model.QuizQuestionModel;
import com.example.learnspace.QuizScoreActivity;
import com.example.learnspace.databinding.ActivityQuizGameBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuizGameActivity extends AppCompatActivity {

    ActivityQuizGameBinding binding;
    private List<String> terms;
    private List<String> definitions;
    private String categoryName;
    private int set;
    private int currentTermIndex = 0;
    private int correctAnswers = 0;
    private int wrongAnswers = 0;
    private boolean showNextQuestion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyTheme();
        super.onCreate(savedInstanceState);
        binding = ActivityQuizGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        categoryName = getIntent().getStringExtra("categoryName");
        set = getIntent().getIntExtra("setNum", 1);

        terms = new ArrayList<>();
        definitions = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference()
                .child("Sets")
                .child(categoryName)
                .child("questions")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot questionSnapshot : snapshot.getChildren()) {
                                QuizQuestionModel question = questionSnapshot.getValue(QuizQuestionModel.class);
                                if (question != null && question.getSetNum() == set) {
                                    terms.add(question.getTerm());
                                    definitions.add(question.getDefinition());
                                }
                            }
                            // Check if terms are available and display the first term
                            if (!terms.isEmpty()) {
                                displayTerm(currentTermIndex);
                            }
                        } else {
                            Toast.makeText(QuizGameActivity.this, "No questions found for this category and set.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(QuizGameActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        binding.cancelQuizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Set onClickListener for the term item view
        binding.termQuiz.setOnClickListener(view -> {
            // Toggle visibility of term and definition views
            binding.definitionQuiz.setVisibility(View.VISIBLE);
            binding.termQuiz.setVisibility(View.INVISIBLE);
            binding.termText.setVisibility(View.INVISIBLE);
            binding.definitionText.setVisibility(View.VISIBLE);
            // Display definition for the current term
            displayDefinition(currentTermIndex);
            // Set the flag to true to indicate that the next question should be shown when either term or definition is clicked
            showNextQuestion = true;
        });

        // Set onClickListener for the definition item view
        binding.definitionQuiz.setOnClickListener(view -> {
            // Toggle visibility of term and definition views
            binding.termQuiz.setVisibility(View.VISIBLE);
            binding.definitionQuiz.setVisibility(View.INVISIBLE);
            binding.definitionText.setVisibility(View.INVISIBLE);
            binding.termText.setVisibility(View.VISIBLE);
            // Set the flag to false to prevent showing the next question immediately
            showNextQuestion = true;
        });

        // Set onClickListener for the wrong answer button
        binding.wrongAnswrBtn.setOnClickListener(view -> {
            if (showNextQuestion) {
                wrongAnswers++; // Increment wrong answer count
                moveToNextQuestion();
                showNextQuestion = true;
            }
        });

        // Set onClickListener for the right answer button
        binding.rightAnswrBtn.setOnClickListener(view -> {
            if (showNextQuestion) {
                correctAnswers++; // Increment correct answer count
                moveToNextQuestion();
                showNextQuestion = true;
            }
        });

    }



    // Method to move to the next question
    private void moveToNextQuestion() {
        currentTermIndex++; // Move to the next question
        if (currentTermIndex < terms.size()) {
            displayTerm(currentTermIndex);
            // Reset visibility of term and definition views
            binding.termQuiz.setVisibility(View.VISIBLE);
            binding.definitionQuiz.setVisibility(View.INVISIBLE);
            binding.definitionText.setVisibility(View.INVISIBLE);
            binding.termText.setVisibility(View.VISIBLE);
        } else {
            // Last question, move to QuizScoreActivity
            Intent intent = new Intent(QuizGameActivity.this, QuizScoreActivity.class);
            intent.putExtra("correctAnswers", correctAnswers);
            intent.putExtra("totalQuestions", terms.size());
            startActivity(intent);
            finish(); // Finish current activity
        }

    }

    private void displayTerm(int index) {
        if (index >= 0 && index < terms.size()) {
            binding.termQuiz.setText(terms.get(index));
            // Here you could implement functionality to let the user navigate through terms
        }
    }

    private void displayDefinition(int index) {
        if (index >= 0 && index < definitions.size()) {
            binding.definitionQuiz.setText(definitions.get(index));
        } else {
            Toast.makeText(QuizGameActivity.this, "Definition not found for this term.", Toast.LENGTH_SHORT).show();
        }
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