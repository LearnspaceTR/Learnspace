package com.example.learnspace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.learnspace.Adapter.QuizQuestionsAdapter;
import com.example.learnspace.Model.QuizQuestionModel;
import com.example.learnspace.databinding.ActivityQuizAddQuestionsBinding;
import com.example.learnspace.databinding.ActivityQuizQuestionsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class QuizQuestionsActivity extends AppCompatActivity {


    ActivityQuizQuestionsBinding binding;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    ArrayList<QuizQuestionModel>list;
    QuizQuestionsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyTheme();
        super.onCreate(savedInstanceState);
        binding = ActivityQuizQuestionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();

        list = new ArrayList<>();


        int setNum = getIntent().getIntExtra("setNum", 0);
        String categoryName = getIntent().getStringExtra("categoryName");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyQuestions.setLayoutManager(layoutManager);

        adapter = new QuizQuestionsAdapter(this, list, categoryName, new QuizQuestionsAdapter.DeleteListener() {
            @Override
            public void onLongClick(int postion, String id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(QuizQuestionsActivity.this);
                builder.setTitle("Delete question");
                builder.setMessage("Are you sure, you want to delete this question?");
                builder.setPositiveButton("Yes", ((dialogInterface, i) -> {

                    database.getReference().child("Sets").child(categoryName).child("questions")
                            .child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    Toast.makeText(QuizQuestionsActivity.this, "question deleted", Toast.LENGTH_SHORT).show();
                                }
                            });

                }));
                builder.setNegativeButton("No",((dialogInterface, i) -> {

                    dialogInterface.dismiss();
                }));

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        binding.recyQuestions.setAdapter(adapter);

        database.getReference().child("Sets").child(categoryName).child("questions")
                        .orderByChild("setNum").equalTo(setNum)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()){
                                    list.clear();

                                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                        QuizQuestionModel model = dataSnapshot.getValue(QuizQuestionModel.class);
                                        model.setKey(dataSnapshot.getKey());
                                        list.add(model);

                                        binding.startQuizBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(QuizQuestionsActivity.this, QuizGameActivity.class);
                                                intent.putExtra("categoryName", categoryName); // Use "categoryName" as the key
                                                intent.putExtra("setNum", setNum);
                                                startActivity(intent);
                                            }
                                        });
                                    }

                                    adapter.notifyDataSetChanged();

                                }
                                else {
                                    Toast.makeText(QuizQuestionsActivity.this, "Please add some questions", Toast.LENGTH_SHORT).show();
                                    binding.startQuizBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Toast.makeText(QuizQuestionsActivity.this, "You need to add questions first", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

        binding.addQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(QuizQuestionsActivity.this, QuizAddQuestionsActivity.class);
                intent.putExtra("category", categoryName);
                intent.putExtra("setNum", setNum);
                startActivity(intent);

            }
        });

        binding.quizBackBtnQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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