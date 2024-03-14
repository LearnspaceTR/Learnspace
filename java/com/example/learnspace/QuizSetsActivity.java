package com.example.learnspace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.learnspace.Adapter.QuizCategoryAdapter;
import com.example.learnspace.Adapter.QuizGridAdapter;
import com.example.learnspace.Model.QuizQuestionModel;
import com.example.learnspace.databinding.ActivityQuizSetsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

public class QuizSetsActivity extends AppCompatActivity {

    ActivityQuizSetsBinding binding;
    FirebaseDatabase database;

    QuizGridAdapter adapter;

    int a = 1;

    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyTheme();
        super.onCreate(savedInstanceState);
        binding = ActivityQuizSetsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        key = getIntent().getStringExtra("key");

        adapter = new QuizGridAdapter(getIntent().getIntExtra("sets", 0),
                getIntent().getStringExtra("category"), key, new QuizGridAdapter.GridListener() {


            @Override
            public void addSets() {

                database.getReference().child("categories").child(key)
                        .child("setNum").setValue(getIntent().getIntExtra("sets", 0)+a++).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                adapter.sets++;
                                adapter.notifyDataSetChanged();
                            }
                            else {

                                Toast.makeText(QuizSetsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            }
                        });

            }
        });
        binding.setsBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.gridView.setAdapter(adapter);

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