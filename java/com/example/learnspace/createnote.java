package com.example.learnspace;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class createnote extends AppCompatActivity {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        EditText createtitleofnote,createcontentofnote;
        FloatingActionButton savenote;
        FirebaseAuth firebaseAuth;
        FirebaseUser firebaseUser;
        FirebaseFirestore firebaseFirestore;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_createnote);

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

            savenote=findViewById(R.id.savenote);
            createtitleofnote=findViewById(R.id.createtitleofnote);
            createcontentofnote=findViewById(R.id.createcontentofnote);


            firebaseAuth=FirebaseAuth.getInstance();
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


            savenote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String title = createtitleofnote.getText().toString();
                    String content = createcontentofnote.getText().toString();
                    if(title.isEmpty() || content.isEmpty())
                    {
                        Toast.makeText(getApplicationContext(),"Both fields are requiered", Toast.LENGTH_SHORT).show();
                    }
                    else{

                        DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document();
                        Map<String ,Object> note= new HashMap<>();
                        note.put("title",title);
                        note.put("content",content);

                        documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Note Created Successfully",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(createnote.this, NotesActivity.class));
                                int newGemsValue = Users.gems + 5;
                                // Update the 'gems' field in the user's node
                                userRef.child("gems").setValue(newGemsValue);
                                Users.gems = newGemsValue;
                                Toast.makeText(getApplicationContext(), "You gained 5 gems through creating a note.", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"Failed to create note",Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                }
            });

        }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

            if(item.getItemId() == android.R.id.home){
                onBackPressed();
            }


        return super.onOptionsItemSelected(item);
    }
}