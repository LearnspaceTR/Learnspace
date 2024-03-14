package com.example.learnspace.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnspace.R;
import com.example.learnspace.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {
    DataSnapshot dataSnapshot;
    private List<Users> userList;

    public LeaderboardAdapter(List<Users> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users = userList.get(position);
        holder.bind(users, position);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView rankTextView;
        private TextView nameTextView;
        private TextView gemsTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rankTextView = itemView.findViewById(R.id.rankTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            gemsTextView = itemView.findViewById(R.id.gemsTextView);
        }

        public void bind(Users user, int position) {
            rankTextView.setText(String.valueOf(position + 1)); // Rank starts from 1
            nameTextView.setText(dataSnapshot.child("name").getValue(String.class));
            gemsTextView.setText(dataSnapshot.child("gems").getValue(String.class));
        }
    }
}
