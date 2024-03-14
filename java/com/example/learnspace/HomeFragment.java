package com.example.learnspace;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeFragment extends Fragment{
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    int tasksize;
    String currentdailystreak = String.valueOf(Users.dailyloginstreak);
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home2, container, false);
        ImageButton Dashboard_widget_button_overlay = view.findViewById(R.id.Dashboard_widget_button_overlay);

        // Check if it's the user's first login of the day
        boolean isFirstLogin = StreakManager.isFirstLoginToday(getContext());
        if (isFirstLogin) {
            Toast.makeText(getContext(), "Welcome back! You've logged in today.", Toast.LENGTH_SHORT).show();
        }
        // Assuming you want to update the streak count when the activity is created

        // Find the TextView by its ID
        TextView dailyGoalTextView = view.findViewById(R.id.streak_text); // Replace with the ID of your TextView
        // Get the streak count
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        Users.dailyloginstreak = StreakManager.getStreakCount(this);
        userRef.child("Login Streak").setValue(Users.dailyloginstreak);
        tasksize = Users.taskCount;
        TextView todoSize = view.findViewById(R.id.Dailygoal_todosize);
        todoSize.setText(String.valueOf(tasksize));

        // Update the TextView text with the streak count
        dailyGoalTextView.setText(currentdailystreak);

        TextView currentGems = view.findViewById(R.id.gemCountTextView);
        int gemCount = Users.gems; // Replace with the actual gem count
        currentGems.setText(String.valueOf(gemCount));

        ImageView rank = view.findViewById(R.id.rankimage);
        ImageView rank1 = view.findViewById(R.id.rankimage1);
        ImageView rank2 = view.findViewById(R.id.rankimage2);
        ImageView rank3 = view.findViewById(R.id.rankimage3);
        ImageView rank4 = view.findViewById(R.id.rankimage4);
        ImageView rank5 = view.findViewById(R.id.rankimage5);
        ImageView rank6 = view.findViewById(R.id.rankimage6);
        ImageView rank7 = view.findViewById(R.id.rankimage7);

        if(Users.rank == 1){
            rank2.setVisibility(View.VISIBLE);
            rank.setVisibility(View.INVISIBLE);
        } else if (Users.rank == 2) {
            rank3.setVisibility(View.VISIBLE);
            rank.setVisibility(View.INVISIBLE);
        } else if (Users.rank == 3) {
            rank4.setVisibility(View.VISIBLE);
            rank.setVisibility(View.INVISIBLE);
        } else if (Users.rank == 4) {
            rank5.setVisibility(View.VISIBLE);
            rank.setVisibility(View.INVISIBLE);
        } else if (Users.rank == 5) {
            rank6.setVisibility(View.VISIBLE);
            rank.setVisibility(View.INVISIBLE);
        } else if (Users.rank == 6) {
            rank7.setVisibility(View.VISIBLE);
            rank.setVisibility(View.INVISIBLE);
        }





        Dashboard_widget_button_overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start ToDoActivity when the button is clicked
                Intent intent = new Intent(getActivity(), CalendarActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }


}
