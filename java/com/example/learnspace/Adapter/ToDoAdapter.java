package com.example.learnspace.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnspace.AddNewTask;
import com.example.learnspace.Model.ToDoModel;
import com.example.learnspace.R;
import com.example.learnspace.ToDoActivity;
import com.example.learnspace.Users;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    public static List<ToDoModel> todoList;
    private List<ToDoModel> completedList;
    private ToDoActivity activity;
    private FirebaseFirestore firestore;

    public ToDoAdapter(ToDoActivity toDoActivity , List<ToDoModel> todoList){
        this.todoList = todoList;
        activity = toDoActivity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.each_task, parent , false);
        firestore = FirebaseFirestore.getInstance();


        return new MyViewHolder(view);

    }

    public void deleteTask(int position){
        ToDoModel toDoModel = todoList.get(position);
        firestore.collection("task").document(toDoModel.TaskId).delete();
        todoList.remove(position);
        notifyItemRemoved(position);
    }
    public Context getContext(){
        return activity;
    }
    public void editTask(int position){
        ToDoModel toDoModel = todoList.get(position);

        Bundle bundle = new Bundle();
        bundle.putString("task" , toDoModel.getTask());
        bundle.putString("due" , toDoModel.getDue());
        bundle.putString("id" , toDoModel.TaskId);

        AddNewTask addNewTask = new AddNewTask();
        addNewTask.setArguments(bundle);
        addNewTask.show(activity.getSupportFragmentManager() , addNewTask.getTag());
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ToDoModel toDoModel = todoList.get(position);
        holder.mCheckBox.setText(toDoModel.getTask());

        holder.mDueDateTv.setText("Due On " + toDoModel.getDue());

        holder.mCheckBox.setChecked(toBoolean(toDoModel.getStatus()));

        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Backup the ToDoModel to enable undo functionality
                    final ToDoModel deletedToDoModel = toDoModel;

                    // Remove the task from the list immediately
                    todoList.remove(toDoModel);
                    notifyDataSetChanged();
                    // Add animation for deletion
                    Animation animation = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_out_right);
                    animation.setDuration(1000); // Set custom animation duration in milliseconds (e.g., 1000ms = 1 second)
                    holder.itemView.startAnimation(animation);
                    // Show undo button
                    Snackbar snackbar = Snackbar.make(buttonView, "Task deleted", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                            Toast.makeText(getContext(), "20 gems got deducted ", Toast.LENGTH_SHORT).show();
                            int newGemsValue = Users.gems - 20;
                            // Update the 'gems' field in the user's node
                            userRef.child("gems").setValue(newGemsValue);
                            // Also update the local 'gems' field in the Users class
                            Users.gems = newGemsValue;
                            // Restore the task to the list
                            todoList.add(deletedToDoModel);
                            notifyDataSetChanged();
                            // Dismiss the snackbar
                            snackbar.dismiss();
                            // Cancel the scheduled deletion
                            // (Note: this doesn't technically "undo" the deletion from Firestore)
                            firestore.collection("task").document(deletedToDoModel.TaskId).set(deletedToDoModel);
                        }
                    });
                    snackbar.show();



                    // Update gems if user is logged in
                    if (user != null) {
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                        Toast.makeText(getContext(), "You gained 20 gems", Toast.LENGTH_SHORT).show();

                        // Assuming you want to add 80 gems
                        int newGemsValue = Users.gems + 20;

                        // Update the 'gems' field in the user's node
                        userRef.child("gems").setValue(newGemsValue);

                        // Also update the local 'gems' field in the Users class
                        Users.gems = newGemsValue;
                    }

                    // Schedule deletion after 2.5 seconds
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Delete the task from Firestore
                            firestore.collection("task").document(deletedToDoModel.TaskId).delete();
                            // Add to the 'completed' collection
                        }
                    }, 2500); // 2.5 seconds delay
                } else {
                    // Update status to 0 if task is unchecked
                    firestore.collection("task").document(toDoModel.TaskId).update("status", 0);
                }
            }
        });




    }

    private boolean toBoolean(int status){
        return status != 0;
    }

    public void setTodoList(List<ToDoModel> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged();
    }
    public void setCompletedList(List<ToDoModel> completedList) {
        this.completedList = completedList;
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return todoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView mDueDateTv;
        CheckBox mCheckBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mDueDateTv = itemView.findViewById(R.id.due_date_tv);
            mCheckBox = itemView.findViewById(R.id.mcheckbox);

        }

    }

}