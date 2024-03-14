package com.example.learnspace;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnspace.Adapter.ToDoAdapter;
import com.example.learnspace.Model.ToDoModel;
import com.example.learnspace.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ToDoActivity extends AppCompatActivity implements DialogCloseListener {
    private long lastDialogShowTime = 0;
    private static final long MIN_DIALOG_INTERVAL = 2000;
    FrameLayout acitivty_main_layout;
    FirebaseAuth auth;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    private DatabaseReference usersRef;
    private CircleImageView profileImageView;
    private RecyclerView recyclerView;
    private FloatingActionButton mFab;
    private FirebaseFirestore firestore;
    private ToDoAdapter adapter;
    private ActivityMainBinding binding;
    private List<ToDoModel> mList;
    private Query query;
    private ListenerRegistration listenerRegistration;
    //for counting the todolistsize

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_todo_combined);
        FloatingActionButton backFab = findViewById(R.id.backbtn);
        FloatingActionButton settingsFab = findViewById(R.id.settingsbutton);
        FloatingActionButton gemsFab = findViewById(R.id.gemsbutton);
        recyclerView = findViewById(R.id.recycerlview);
        mFab = findViewById(R.id.floatingActionButton);
        firestore = FirebaseFirestore.getInstance();

        //set the already opened to 1 to not open it again
        MainActivity.alreadyOpened = 1;

        //read the data from the database
        Users.readGemsFromDatabase();
        Users.readRankFromDatabase();
        Users.readStreakFromDatabase();

        //check if already logged in
        mAuth = FirebaseAuth.getInstance();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ToDoActivity.this));

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });

        // Set an OnClickListener for the backbtn
        backFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call a method to switch to the activity_main.xml layout
                switchToMainActivityLayout();
            }
        });
        settingsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call a method to switch to the activity_main.xml layout
                showSettingsDialog();
            }
        });

        gemsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call a method to switch to the activity_main.xml layout
                showGemDialog();
            }
        });
        mList = new ArrayList<>();
        adapter = new ToDoAdapter(ToDoActivity.this, mList);





        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        showData();
        recyclerView.setAdapter(adapter);


        //Make that the User data can be displayed
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        // Set click listeners for the FAB and other buttons
        binding.settingsbutton.setOnClickListener(view -> showSettingsDialog());
        binding.gemsbutton.setOnClickListener(view -> showGemsShopDialog());


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Users");

        profileImageView = findViewById(R.id.profileImageView); // Replace with your actual ImageView ID

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            // User is signed in, retrieve user data
            usersRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userName = dataSnapshot.child("name").getValue(String.class);
                        String profileImageUrl = dataSnapshot.child("profile").getValue(String.class);

                        // Load and display the profile picture using Picasso
                        if (profileImageUrl != null) {
                            Picasso.get().load(profileImageUrl).into(profileImageView);
                        }


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle any errors here
                }
            });
        }
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        CollectionReference taskCollectionRef = firestore.collection("task");

        taskCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int taskCount = queryDocumentSnapshots.size();
                // Now 'taskCount' contains the number of documents in the "task" collection
                userRef.child("taskCount").setValue(taskCount);
                Users.taskCount = taskCount;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle errors
                Log.e("Firestore", "Error getting document count for 'task' collection", e);
            }
        });




    }

    private void showData() {
        query = firestore.collection("task").orderBy("time", Query.Direction.DESCENDING);

        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        String id = documentChange.getDocument().getId();
                        ToDoModel toDoModel = documentChange.getDocument().toObject(ToDoModel.class).withId(id);
                        mList.add(toDoModel);
                        adapter.notifyDataSetChanged();
                    }
                }
                listenerRegistration.remove();

            }
        });
    }

    @Override
    public void handleDialogClose(DialogInterface dialogInterface) {
        mList.clear();
        showData();
        adapter.notifyDataSetChanged();
    }

    private void switchToMainActivityLayout() {
        Intent intent = new Intent(this, MainActivity.class);

        // Start the MainActivity
        startActivity(intent);

        // Finish the current activity (ToDoActivity) if you don't want to return to it later
        finish();
    }
    // Show a dialog displaying the gem count
    public void showGemDialog() {
        long currentTime = System.currentTimeMillis();
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.gem_popup);

        // Check if enough time has passed since the last dialog was shown
        if (currentTime - lastDialogShowTime < MIN_DIALOG_INTERVAL) {
            // Do nothing if not enough time has passed
            return;
        }


        lastDialogShowTime = currentTime;

        // Assuming you have a variable to store the gem count
        int gemCount = Users.gems; // Replace with the actual gem count

        // Find the TextView in the dialog layout
        TextView gemCountTextView = dialog.findViewById(R.id.gemCountTextView);

        // Update the gem count in the TextView
        gemCountTextView.setText(String.valueOf(gemCount));

        // Show the gem count dialog
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.Dialoganimation;
        dialog.getWindow().setGravity(Gravity.CENTER);

        // Set click listeners for dialog buttons
        Button closeButton = dialog.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dialog.dismiss());

        Button rewardsButton = dialog.findViewById(R.id.rewardsButton);
        rewardsButton.setOnClickListener(view -> {
            // Dismiss the current dialog if it is showing
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            // Show the GemsRewardsDialog
            showGemsRewardsDialog();
        });


        Button shopButton = dialog.findViewById(R.id.shopButton);
        shopButton.setOnClickListener(view -> {
            // Dismiss the current dialog if it is showing
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            // Show the GemsRewardsDialog
            showGemsShopDialog();
        });
    }

    // Show the settings dialog
    private void showSettingsDialog() {
        long currentTime = System.currentTimeMillis();

        // Check if enough time has passed since the last dialog was shown
        if (currentTime - lastDialogShowTime < MIN_DIALOG_INTERVAL) {
            // Do nothing if not enough time has passed
            return;
        }

        lastDialogShowTime = currentTime;
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.settings_popup);

        // Show the settings dialog
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.Dialoganimation;
        dialog.getWindow().setGravity(Gravity.CENTER);

        // Set click listener for the close button
        Button closeButton = dialog.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dialog.dismiss());

        Button profileButton = dialog.findViewById(R.id.profileButton);
        profileButton.setOnClickListener(view -> showSettingsProfileDialog());
        Button genralSettingsButton = dialog.findViewById(R.id.generalSettingsButton);
        genralSettingsButton.setOnClickListener(view -> showGeneralSettingsDialog(acitivty_main_layout));
        Button NotificationsSettingsButton = dialog.findViewById(R.id.notificationSettingsButton);
        NotificationsSettingsButton.setOnClickListener(view -> showNotificationsSettingsProfileDialog());

    }

    // Show the gems shop dialog
    private void showGemsShopDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.gems_shop);

        // Show the gems shop dialog
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.Dialoganimation;
        dialog.getWindow().setGravity(Gravity.CENTER);


        Button purchase1 = dialog.findViewById(R.id.purchase1);
        Button purchase2 = dialog.findViewById(R.id.purchase2);
        Button purchase3 = dialog.findViewById(R.id.purchase3);
        Button purchase4 = dialog.findViewById(R.id.purchase4);
        Button purchase5 = dialog.findViewById(R.id.purchase5);
        Button purchase6 = dialog.findViewById(R.id.purchase6);


        //purchase rank1
        purchase1.setOnClickListener(v -> {
            // Call SignOut() method when logoutButton is clicked
            // Assuming you want to add 50 gems
            if (user != null) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                if(Users.gems>=0 || Users.rank == 0) {
                    // Assuming you want to add 50 gems
                    int newGemsValue = Users.gems - 0;
                    // Update the 'gems' field in the user's node
                    userRef.child("gems").setValue(newGemsValue);
                    Users.gems = newGemsValue;
                    int newrank = 1;
                    Users.rank = newrank;
                    userRef.child("rank").setValue(Users.rank);
                    purchase1.setVisibility(View.INVISIBLE);
                    // Also update the local 'gems' field in the Users class
                    MainActivity.alreadyOpened = 1;
                    Intent intent = new Intent(ToDoActivity.this, MainActivity.class);
                    startActivity(intent);
                } else if (Users.gems<=0){
                    Toast.makeText(this, "You don't have enough points", Toast.LENGTH_SHORT).show();
                } else if (Users.rank<=0){
                    Toast.makeText(this, "Your current rank is not high enough to purchase this", Toast.LENGTH_LONG).show();
                }

            }
        });
        purchase2.setOnClickListener(v -> {
            // Call SignOut() method when logoutButton is clicked

            if (user != null) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                if(Users.gems>=0 && Users.rank==1) {
                    // Assuming you want to add 50 gems
                    int newGemsValue = Users.gems - 0;
                    // Update the 'gems' field in the user's node
                    userRef.child("gems").setValue(newGemsValue);
                    int newrank = 2;
                    Users.rank = newrank;
                    userRef.child("rank").setValue(newrank);
                    purchase2.setVisibility(View.INVISIBLE);

                    // Also update the local 'gems' field in the Users class
                    Users.gems = newGemsValue;
                    MainActivity.alreadyOpened = 1;

                    Intent intent = new Intent(ToDoActivity.this, MainActivity.class);
                    startActivity(intent);
                }else if (Users.gems<=0){
                    Toast.makeText(this, "You don't have enough points", Toast.LENGTH_SHORT).show();
                } else if (Users.rank<=1){
                    Toast.makeText(this, "Your current rank is not high enough to purchase this", Toast.LENGTH_LONG).show();

                }

            }
        });
        purchase3.setOnClickListener(v -> {
            // Call SignOut() method when logoutButton is clicked
            // Assuming you want to add 50 gems
            if (user != null) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                if(Users.gems>=0 && Users.rank==2) {
                    // Assuming you want to add 50 gems
                    int newGemsValue = Users.gems - 0;
                    // Update the 'gems' field in the user's node
                    userRef.child("gems").setValue(newGemsValue);
                    int newrank = 3;
                    Users.rank = newrank;
                    userRef.child("rank").setValue(newrank);
                    purchase3.setVisibility(View.INVISIBLE);
                    // Also update the local 'gems' field in the Users class
                    Users.gems = newGemsValue;
                    MainActivity.alreadyOpened = 1;

                    Intent intent = new Intent(ToDoActivity.this, MainActivity.class);
                    startActivity(intent);
                } else if (Users.gems<=0){
                    Toast.makeText(this, "You don't have enough points", Toast.LENGTH_SHORT).show();
                } else if (Users.rank<=2){
                    Toast.makeText(this, "Your current rank is not high enough to purchase this", Toast.LENGTH_LONG).show();
                }

            }
        });
        purchase4.setOnClickListener(v -> {
            // Call SignOut() method when logoutButton is clicked
            // Assuming you want to add 50 gems
            if (user != null) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                if(Users.gems>=0 && Users.rank==3) {
                    // Assuming you want to add 50 gems
                    int newGemsValue = Users.gems - 0;
                    // Update the 'gems' field in the user's node
                    userRef.child("gems").setValue(newGemsValue);
                    int newrank = 4;
                    userRef.child("rank").setValue(newrank);
                    Users.rank = newrank;
                    purchase3.setVisibility(View.INVISIBLE);
                    // Also update the local 'gems' field in the Users class
                    Users.gems = newGemsValue;
                    MainActivity.alreadyOpened = 1;

                    Intent intent = new Intent(ToDoActivity.this, MainActivity.class);
                    startActivity(intent);
                } else if (Users.gems<=0){
                    Toast.makeText(this, "You don't have enough points", Toast.LENGTH_SHORT).show();
                } else if (Users.rank<=3){
                    Toast.makeText(this, "Your current rank is not high enough to purchase this", Toast.LENGTH_LONG).show();

                }

            }
        });
        purchase5.setOnClickListener(v -> {
            // Call SignOut() method when logoutButton is clicked
            // Assuming you want to add 50 gems
            if (user != null) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                if(Users.gems>=0 && Users.rank==4) {
                    // Assuming you want to add 50 gems
                    int newGemsValue = Users.gems - 0;
                    // Update the 'gems' field in the user's node
                    userRef.child("gems").setValue(newGemsValue);
                    int newrank = 5;
                    Users.rank = newrank;
                    userRef.child("rank").setValue(newrank);
                    purchase5.setVisibility(View.INVISIBLE);
                    // Also update the local 'gems' field in the Users class
                    Users.gems = newGemsValue;
                    MainActivity.alreadyOpened = 1;
                    Intent intent = new Intent(ToDoActivity.this, MainActivity.class);
                    startActivity(intent);
                }else if (Users.gems<=0){
                    Toast.makeText(this, "You don't have enough points", Toast.LENGTH_SHORT).show();
                } else if (Users.rank<=4){
                    Toast.makeText(this, "Your current rank is not high enough to purchase this", Toast.LENGTH_LONG).show();

                }

            }
        });
        purchase6.setOnClickListener(v -> {
            // Call SignOut() method when logoutButton is clicked
            // Assuming you want to add 50 gems
            if (user != null) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                if(Users.gems>=0 && Users.rank==5) {
                    // Assuming you want to add 50 gems
                    int newGemsValue = Users.gems - 0;
                    // Update the 'gems' field in the user's node
                    userRef.child("gems").setValue(newGemsValue);
                    int newrank = 6;
                    Users.rank = newrank;
                    userRef.child("rank").setValue(newrank);
                    purchase6.setVisibility(View.INVISIBLE);

                    // Also update the local 'gems' field in the Users class
                    Users.gems = newGemsValue;
                    MainActivity.alreadyOpened = 1;

                    Intent intent = new Intent(ToDoActivity.this, MainActivity.class);
                    startActivity(intent);
                } else if (Users.gems<=0){
                    Toast.makeText(this, "You don't have enough points", Toast.LENGTH_SHORT).show();
                } else if (Users.rank<=5){
                    Toast.makeText(this, "Your current rank is not high enough to purchase this", Toast.LENGTH_LONG).show();
                }

            }
        });

        // Set click listener for the close button
        Button closeButton = dialog.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dialog.dismiss());
    }
    // Show the gems rewards dialog
    private void showGemsRewardsDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.gems_rewards);

        // Show the gems rewards dialog
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.Dialoganimation;
        dialog.getWindow().setGravity(Gravity.CENTER);

        // Set click listener for the close button
        Button closeButton = dialog.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dialog.dismiss());

    }

    //Settings Profile Dialog
    private void showSettingsProfileDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.settings_profile);
        Button signinbutton = dialog.findViewById(R.id.signinbutton);
        // Show the Settings general dialog
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.Dialoganimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
        if(user==null) {
            signinbutton.setOnClickListener(v -> {
                // Call SignOut() method when logoutButton is clicked
                SignIn();
                // Dismiss the dialog if needed
                dialog.dismiss();
            });
        }else{
            signinbutton.setVisibility(View.GONE);
        }
        // Set click listener for the logout button
        Button profileButton2 = dialog.findViewById(R.id.logoutbutton);

        if(user!=null) {
            profileButton2.setOnClickListener(v -> {
                // Call SignOut() method when logoutButton is clicked
                SignOut();
                // Dismiss the dialog if needed
                dialog.dismiss();
            });
        }else{
            profileButton2.setVisibility(View.GONE);
        }
        // Set click listener for the close button
        Button closeButton = dialog.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dialog.dismiss());
    }

    //Settings General Dialog
    private void showGeneralSettingsDialog(FrameLayout activity_main_layout) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.settings_general);
        Button defaultTheme = dialog.findViewById(R.id.default_theme);
        Button theme1 = dialog.findViewById(R.id.theme_1);
        Button theme2 = dialog.findViewById(R.id.theme_2);
        Button theme3 = dialog.findViewById(R.id.theme_3);
        Button theme4 = dialog.findViewById(R.id.theme_4);
        Button theme5 = dialog.findViewById(R.id.theme_5);
        Button theme6 = dialog.findViewById(R.id.theme_6);

        defaultTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the selected theme preference to SharedPreferences
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("theme", "default");
                editor.apply();

                // Recreate the activity with the new theme
                recreate();
            }
        });

        theme1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                if(Users.rank>=1) {
                    // Save the selected theme preference to SharedPreferences
                    SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("theme", "theme1");
                    editor.apply();
                    // Recreate the activity with the new theme
                    recreate();
                } else{
                    Toast.makeText(ToDoActivity.this, "You need to be at least rank 1 to apply this theme.", Toast.LENGTH_LONG).show();
                }
            }
        });
        theme2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                if(Users.rank>=2) {
                    // Save the selected theme preference to SharedPreferences
                    SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("theme", "theme2");
                    editor.apply();
                    // Recreate the activity with the new theme
                    recreate();
                } else{
                    Toast.makeText(ToDoActivity.this, "You need to be at least rank 2 to apply this theme.", Toast.LENGTH_LONG).show();
                }
            }
        });
        theme3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                if(Users.rank>=3) {
                    // Save the selected theme preference to SharedPreferences
                    SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("theme", "theme3");
                    editor.apply();
                    // Recreate the activity with the new theme
                    recreate();
                } else{
                    Toast.makeText(ToDoActivity.this, "You need to be at least rank 3 to apply this theme.", Toast.LENGTH_LONG).show();
                }
            }
        });
        theme4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                if(Users.rank>=4) {
                    // Save the selected theme preference to SharedPreferences
                    SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("theme", "theme4");
                    editor.apply();
                    // Recreate the activity with the new theme
                    recreate();
                } else{
                    Toast.makeText(ToDoActivity.this, "You need to be at least rank 4 to apply this theme.", Toast.LENGTH_LONG).show();
                }
            }
        });
        theme5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                if(Users.rank>=5) {
                    // Save the selected theme preference to SharedPreferences
                    SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("theme", "theme5");
                    editor.apply();
                    // Recreate the activity with the new theme
                    recreate();
                } else{
                    Toast.makeText(ToDoActivity.this, "You need to be at least rank 5 to apply this theme.", Toast.LENGTH_LONG).show();
                }
            }
        });
        theme6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                if(Users.rank>=6) {
                    // Save the selected theme preference to SharedPreferences
                    SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("theme", "theme6");
                    editor.apply();
                    // Recreate the activity with the new theme
                    recreate();
                } else{
                    Toast.makeText(ToDoActivity.this, "You need to be rank 6 to apply this theme.", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Show the Settings general dialog
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.Dialoganimation;
        dialog.getWindow().setGravity(Gravity.CENTER);

        // Set click listener for the close button
        Button closeButton = dialog.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dialog.dismiss());

    }

    //Settings Notifications Dialog
    private void showNotificationsSettingsProfileDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.settings_notifications);

        // Show the Settings notifications dialog
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.Dialoganimation;
        dialog.getWindow().setGravity(Gravity.CENTER);


        // Set click listener for the close button
        Button closeButton = dialog.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dialog.dismiss());
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
    //Settings Profile Dialog
    private void SignOut() {
        mAuth.signOut();
        Intent intent = new Intent(ToDoActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
        Toast.makeText(ToDoActivity.this, "Logout Successful", Toast.LENGTH_SHORT).show();
    }
    private void SignIn() {
        Intent intent = new Intent(ToDoActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }
}