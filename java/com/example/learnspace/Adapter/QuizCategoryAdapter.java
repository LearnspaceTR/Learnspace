package com.example.learnspace.Adapter;

import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.learnspace.Model.QuizCategoryModel;
import com.example.learnspace.QuizSetsActivity;
import com.example.learnspace.R;
import com.example.learnspace.databinding.ItemCategoryBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class QuizCategoryAdapter extends RecyclerView.Adapter<QuizCategoryAdapter.viewHolder>{

    private Context context;
    private ArrayList<QuizCategoryModel> list;
    private OnItemLongClickListener longClickListener; // Add long click listener

    // Constructor to set context and list
    public QuizCategoryAdapter(Context context, ArrayList<QuizCategoryModel> list) {
        this.context = context;
        this.list = list;
    }

    // Interface for long click listener
    public interface OnItemLongClickListener {
        void onItemLongClick(int position, String id);
    }

    // Method to set long click listener
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        QuizCategoryModel model = list.get(position);

        holder.binding.categoryName.setText(model.getCategoryName());

        Picasso.get()
                .load(model.getCategoryName())
                .placeholder(R.drawable.ls_logo)
                .into(holder.binding.categoryImages);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, QuizSetsActivity.class);
                intent.putExtra("category",model.getCategoryName());
                intent.putExtra("sets",model.getSetNum());
                intent.putExtra("key",model.getKey());

                context.startActivity(intent);

            }
        });

        // Set long click listener
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (longClickListener != null) {
                    longClickListener.onItemLongClick(position, model.getKey());
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        ItemCategoryBinding binding;
        public viewHolder(@NonNull View itemView){
            super(itemView);

            binding = ItemCategoryBinding.bind(itemView);
        }
    }
}