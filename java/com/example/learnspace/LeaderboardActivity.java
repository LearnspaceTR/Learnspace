package com.example.learnspace;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LeaderboardActivity extends AppCompatActivity {

    private ListView leaderboardListView;
    private ArrayList<Users> userList;
    private ArrayAdapter<Users> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        leaderboardListView = findViewById(R.id.recyclerView);
        userList = new ArrayList<Users>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        leaderboardListView.setAdapter(adapter);

        // Fetch user data from Firebase and populate the leaderboard
        fetchUserDataFromFirebase();
    }

    private void fetchUserDataFromFirebase() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        usersRef.orderByChild("gems").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Users users = userSnapshot.getValue(Users.class);
                    if (users != null) {
                        userList.add(users);
                    }
                }

                // Sort the userList based on gems value
                Collections.sort(userList, (user1, user2) -> {
                    return Integer.compare(user2.getGems(), user1.getGems()); // Descending order
                });

                // Update the ListView
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }
}
