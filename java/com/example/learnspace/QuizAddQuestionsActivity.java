package com.example.learnspace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.learnspace.Model.QuizQuestionModel;
import com.example.learnspace.databinding.ActivityQuizAddQuestionsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class QuizAddQuestionsActivity extends AppCompatActivity {

    ActivityQuizAddQuestionsBinding binding;
    int set;
    String categoryName;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyTheme();
        super.onCreate(savedInstanceState);
        binding = ActivityQuizAddQuestionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        set = getIntent().getIntExtra("setNum", -1);
        categoryName = getIntent().getStringExtra("category");

        if (set == -1){
            finish();
            return;
        }

        binding.cancelAddquestionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.QuestionsUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                QuizQuestionModel model = new QuizQuestionModel();

                model.setTerm(binding.inputTerm.getText().toString());
                model.setDefinition(binding.inputDefinition.getText().toString());
                model.setSetNum(set);

                database.getReference().child("Sets").child(categoryName).child("questions")
                        .push()
                        .setValue(model)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(QuizAddQuestionsActivity.this, "Questions added!", Toast.LENGTH_SHORT).show();
                                clearFields(); // Clear all fields after saving
                            }
                        });
            }
        });
    }

    private void clearFields() {
        binding.inputTerm.getText().clear();
        binding.inputDefinition.getText().clear();

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