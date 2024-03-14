package com.example.learnspace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager; // Import LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.learnspace.Adapter.QuizCategoryAdapter;
import com.example.learnspace.Model.QuizCategoryModel;
import com.example.learnspace.databinding.ActivityQuizCreateBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class QuizActivity extends AppCompatActivity {

    FirebaseDatabase database;
    FirebaseStorage storage;
    CircleImageView categoryImage;
    EditText inputCategoryName;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    Button createCategory;
    View fetchImage;
    Uri imageUri;

    // Declare a default image resource ID
    int defaultImageResource = R.drawable.ls_logo;

    int i = 0;
    ArrayList<QuizCategoryModel> list;
    QuizCategoryAdapter adapter;

    ProgressDialog progressDialog;

    Dialog dialog;
    ActivityQuizCreateBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyTheme();
        super.onCreate(savedInstanceState);
        binding = ActivityQuizCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        list = new ArrayList<>();

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.item_add_category);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(true);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Upload");
        progressDialog.setMessage("please wait");

        createCategory = dialog.findViewById(R.id.create_category_btn);
        inputCategoryName = dialog.findViewById(R.id.inputCategoryName);
        categoryImage = dialog.findViewById(R.id.category_images);
        fetchImage = dialog.findViewById(R.id.fetchImage);

        RecyclerView recyclerView = binding.recyCategory;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new QuizCategoryAdapter(this, list);
        recyclerView.setAdapter(adapter);

        database.getReference().child("categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        list.add(new QuizCategoryModel(
                                dataSnapshot.child("categoryName").getValue().toString(),
                                dataSnapshot.child("categoryImage").getValue().toString(),
                                dataSnapshot.getKey(),
                                Integer.parseInt(dataSnapshot.child("setNum").getValue().toString())
                        ));
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(QuizActivity.this, "category not existing", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuizActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        fetchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        adapter.setOnItemLongClickListener(new QuizCategoryAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position, String id) {
                confirmCategoryDeletion(position, id);
            }
        });

        createCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = inputCategoryName.getText().toString();
                if (name.isEmpty()) {
                    inputCategoryName.setError("Enter category name");
                } else {
                    uploadData();
                }
            }

            private void uploadData() {
                final StorageReference reference = storage.getReference()
                        .child("category").child(new Date().getTime() + "");
                Uri selectedImageUri = (imageUri != null) ? imageUri : Uri.parse("android.resource://com.example.learnspace/drawable/ls_logo");

                reference.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                QuizCategoryModel categoryModel = new QuizCategoryModel();
                                categoryModel.setCategoryName(inputCategoryName.getText().toString());
                                categoryModel.setSetNum(0);
                                categoryModel.setCategoryImage(uri.toString());

                                database.getReference().child("categories").child("category" + i++)
                                        .setValue(categoryModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(QuizActivity.this, "Data uploaded", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                                dialog.dismiss();
                                                clearDialogFields(); // Clear fields after successful upload
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(QuizActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                            }
                        });
                    }
                });
            }
        });

        binding.categBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (data != null && data.getData() != null) {
                imageUri = data.getData();
                categoryImage.setImageURI(imageUri);
            } else {
                categoryImage.setImageResource(defaultImageResource);
                imageUri = null;
            }
        }
    }

    private void confirmCategoryDeletion(int position, String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);
        builder.setTitle("Delete Category");
        builder.setMessage("Are you sure you want to delete this category?");
        builder.setPositiveButton("Yes", ((dialogInterface, i) -> {
            database.getReference().child("categories").child(id).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(QuizActivity.this, "Category deleted", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(QuizActivity.this, "Deletion failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }));
        builder.setNegativeButton("No",((dialogInterface, i) -> {
            dialogInterface.dismiss();
        }));
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void clearDialogFields() {
        inputCategoryName.setText(""); // Clear EditText
        categoryImage.setImageResource(defaultImageResource); // Reset ImageView to default image
        imageUri = null; // Reset imageUri
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