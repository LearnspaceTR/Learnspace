package com.example.learnspace;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Users {

    String userId, name, profile;
    public static int gems;
    public static int rank;

    public static int dailyloginstreak;
    public static int taskCount;
    public static int didlogin;
    public Users() {
    }

    public static int getStreak(){
        return dailyloginstreak;
    }

    public static void setStreak(int addstreak){
        dailyloginstreak += addstreak;
    }
    public static int getRank(){
        return rank;
    }

    public static void setRank(int newrank){
        rank += newrank;
    }
    public static int getGems(){
        return gems;
    }

    public static void setGems(int addgems){gems += addgems;}

    public Users(String userId, String name, String profile, int gems, int rank, int dailyloginstreak, int taskCount) {
        this.userId = userId;
        this.name = name;
        this.profile = profile;
        this.gems = gems;
        this.rank = rank;
        this.dailyloginstreak = dailyloginstreak;
        this.taskCount = taskCount;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public static void readGemsFromDatabase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

            userRef.child("gems").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        gems = dataSnapshot.getValue(Integer.class);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle any errors here
                }
            });
        }
    }
    public static void readRankFromDatabase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

            userRef.child("rank").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        rank = dataSnapshot.getValue(Integer.class);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle any errors here
                }
            });
        }
    }
    public static void readStreakFromDatabase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

            userRef.child("Daily Login").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        dailyloginstreak = dataSnapshot.getValue(Integer.class);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle any errors here
                }
            });
        }
    }

    public static void readTaskCountFromDatabase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

            userRef.child("taskCount").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        taskCount = dataSnapshot.getValue(Integer.class);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle any errors here
                }
            });
        }
    }
}
