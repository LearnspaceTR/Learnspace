package com.example.learnspace;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class PenFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_pen, container, false);

            // Find the button by ID
            Button startQuiz = view.findViewById(R.id.quiz_btn);

            // Set an OnClickListener for the button
            startQuiz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start QuizActivity
                    Intent intent = new Intent(getActivity(), QuizActivity.class);
                    startActivity(intent);
                }
            });
            return view;
        }
    }