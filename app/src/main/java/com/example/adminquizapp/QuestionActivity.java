package com.example.adminquizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
public class QuestionActivity extends AppCompatActivity {
    private Button add;
    private RecyclerView recyclerView;
    private Dialog loading;
    private QuestionsAdapter adapter;
    public static List<QuestionModel> list;
    private QuestionModel questionModel;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        final String catname=getIntent().getStringExtra("category");
        final int sets=getIntent().getIntExtra("setNo",1);
        Toolbar toolbar = findViewById(R.id.tb1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(catname+"/"+sets);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loading=new Dialog(this);
        loading.setContentView(R.layout.loading);
        loading.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corners));
        loading.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loading.setCancelable(false);
        add=findViewById(R.id.qesadd);
        recyclerView=findViewById(R.id.rv);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        list=new ArrayList<>();
        adapter=new QuestionsAdapter(list, catname, new QuestionsAdapter.DeleteListener() {
            @Override
            public void onLongClick(final int position, final String id) {
                new AlertDialog.Builder(QuestionActivity.this,R.style.Theme_AppCompat_Light_Dialog).setTitle("Delete Question")
                        .setMessage("Are you Sure.You want to to delete this Question?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loading.show();
                                myRef.child("SETS").child(catname).child("questions").child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            list.remove(position);
                                            adapter.notifyItemRemoved(position);
                                            loading.dismiss();
                                            Toast.makeText(QuestionActivity.this, "Deleted", Toast.LENGTH_SHORT).show();

                                        } else {
                                            Toast.makeText(QuestionActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();

                                        }
                                        loading.dismiss();

                                    }
                                });

                            }
                                })
                .setNegativeButton("Cancel",null).setIcon(android.R.drawable.ic_dialog_alert).show();
            }
        });

        recyclerView.setAdapter(adapter);
        getData(catname,sets);
       add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent qes =new Intent(QuestionActivity.this,AddQuestionActivity.class);
                qes.putExtra("categoryName",catname);
                qes.putExtra("setNo",sets);
                startActivity(qes);
            }
        });

    }
private void getData(String categoryname, final int set) {
    loading.show();
    myRef.child("SETS").child(categoryname).child("questions").orderByChild("setNo").equalTo(set).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot datasnapshot1 : snapshot.getChildren()) {
                String id = datasnapshot1.getKey();
                String question = datasnapshot1.child("question").getValue().toString();
                String a = datasnapshot1.child("option1").getValue().toString();
                String b = datasnapshot1.child("option2").getValue().toString();
                String c = datasnapshot1.child("option3").getValue().toString();
                String d = datasnapshot1.child("option4").getValue().toString();
                String correctAns = datasnapshot1.child("correctAns").getValue().toString();
                list.add(new QuestionModel(id, question, a, b, c, d, correctAns, set));

            }
            loading.dismiss();
            adapter.notifyDataSetChanged();
        }



        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(QuestionActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
            loading.dismiss();
            finish();
        }
    });
}

    @Override
  protected  void onStart(){
    super.onStart();
    adapter.notifyDataSetChanged();

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
