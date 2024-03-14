package com.example.learnspace.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learnspace.QuizQuestionsActivity;
import com.example.learnspace.R;

import org.w3c.dom.Text;

public class QuizGridAdapter extends BaseAdapter {

    public int sets = 0;
    private String category;
    private String key;

    private GridListener listener;

    public QuizGridAdapter(int sets, String category, String key, GridListener listener) {
        this.sets = sets;
        this.category = category;
        this.key = key;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return sets+1;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View view1;

        if (view==null){
            view1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_sets,viewGroup,false);
        }
        else {
            view1 = view;
        }

        if(i==0){
            ((TextView)view1.findViewById(R.id.setNumber)).setText("+");

        }
        else {
            ((TextView)view1.findViewById(R.id.setNumber)).setText(String.valueOf(i));

        }

        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (i==0){
                    listener.addSets();
                }
                else {
                    Intent intent = new Intent(viewGroup.getContext(), QuizQuestionsActivity.class);
                    intent.putExtra("setNum",i);
                    intent.putExtra("categoryName",category);
                    viewGroup.getContext().startActivity(intent);
                }

            }
        });
        return view1;
    }

    public interface GridListener{
        public void addSets();
    }
}
