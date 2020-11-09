package com.example.adminquizapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.viewholder> {
    private List<QuestionModel> list;
    private String category;
    private DeleteListener listener;

    public QuestionsAdapter(List<QuestionModel> list, String category, DeleteListener listener) {
        this.list = list;
        this.category = category;
        this.listener=listener;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        String question = list.get(position).getQuestion();
        String answer = list.get(position).getAnswer();
        holder.setData(question, answer, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class viewholder extends RecyclerView.ViewHolder {
        private TextView question, answer;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question);
            answer = itemView.findViewById(R.id.answer);
        }

        public void setData(final String question, final String answer, final int position) {
            this.question.setText(position + 1 + " " + question);
            this.answer.setText("Answer " + answer);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View v) {
                    Intent edit = new Intent(itemView.getContext(), AddQuestionActivity.class);
                    edit.putExtra("categoryName", category);
                    edit.putExtra("setNo", list.get(position).getSet());
                    edit.putExtra("position", position);
                    itemView.getContext().startActivity(edit);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onLongClick(position,list.get(position).getId());
                    return false;
                }

            });
        }
    }
    public interface DeleteListener{
        void onLongClick(int position,String id);
    }
}





