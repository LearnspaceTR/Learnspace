package com.example.learnspace.Adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import com.example.learnspace.Model.QuizQuestionModel;
import com.example.learnspace.R;
import com.example.learnspace.databinding.QuizItemQuestionsBinding;

import java.util.ArrayList;

public class QuizQuestionsAdapter extends RecyclerView.Adapter<QuizQuestionsAdapter.viewHolder> {


    Context context;
    ArrayList<QuizQuestionModel>list;
    String categoryName;
    DeleteListener listener;

    public QuizQuestionsAdapter(Context context, ArrayList<QuizQuestionModel> list, String categoryName, DeleteListener listener) {
        this.context = context;
        this.list = list;
        this.categoryName = categoryName;
        this.listener = listener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.quiz_item_questions, parent, false);

        return new viewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        QuizQuestionModel model = list.get(position);

        holder.binding.question.setText(model.getDefinition());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.onLongClick(position,list.get(position).getKey());

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{


        QuizItemQuestionsBinding binding;
        public viewHolder(@NonNull View itemView){
            super(itemView);

            binding = QuizItemQuestionsBinding.bind(itemView);
        }
    }

    public interface DeleteListener{
        public void onLongClick(int postion, String id);
    }

}
