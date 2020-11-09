package com.example.adminquizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class AddQuestionActivity extends AppCompatActivity {
    private RadioGroup options;
    private LinearLayout answers;
    private Button upload;
    private Dialog loading;
    private String category;
    private EditText question;
    private QuestionModel questionModel;
    int setNo, position;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        Toolbar toolbar = findViewById(R.id.tb1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Question");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        answers = findViewById(R.id.answers);
        upload = findViewById(R.id.addQuestion);
        options = findViewById(R.id.options);
        question = findViewById(R.id.question);
        category = getIntent().getStringExtra("categoryName");
        setNo = getIntent().getIntExtra("setNo", -1);
        position = getIntent().getIntExtra("position", -1);
        loading = new Dialog(this);
        loading.setContentView(R.layout.loading);
        loading.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corners));
        loading.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loading.setCancelable(false);
        if (setNo == -1) {
            finish();
            return;
        }
        if (position != -1) {
            questionModel = new QuestionActivity().list.get(position);
            setData();
        }
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (question.getText().toString().isEmpty()) {
                    question.setError("Required");
                    return;

                }
                uploadData();
            }
        });
    }

    private void uploadData() {
        int correct = -1;
        for (int i = 0; i < options.getChildCount(); i++) {
            EditText answer = (EditText) answers.getChildAt(i);
            if (answer.getText().toString().isEmpty()) {
                answer.setError("Required");
                return;
            }
            RadioButton radioButton = (RadioButton) options.getChildAt(i);
            if (radioButton.isChecked()) {
                correct = i;
                break;

            }
        }
        if (correct == -1) {
            Toast.makeText(this, "Please mark the correct option", Toast.LENGTH_SHORT).show();
            return;
        }
        final HashMap<String, Object> map = new HashMap<>();
        map.put("correctAns", ((EditText) answers.getChildAt(correct)).getText().toString());
        map.put("option1", ((EditText) answers.getChildAt(0)).getText().toString());
        map.put("option2", ((EditText) answers.getChildAt(1)).getText().toString());
        map.put("option3", ((EditText) answers.getChildAt(2)).getText().toString());
        map.put("option4", ((EditText) answers.getChildAt(3)).getText().toString());
        map.put("question", question.getText().toString());
        map.put("setNo", setNo);
        if (position != -1) {
            id = questionModel.getId();
        } else {
            id = UUID.randomUUID().toString();
        }
        loading.show();
        FirebaseDatabase.getInstance().getReference().child("SETS").child(category).child("questions").child(id)
                .setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    QuestionModel questionModel = new QuestionModel(id, map.get("question").toString(),
                            map.get("option1").toString(), map.get("option2").toString(), map.get("option3").toString(), map.get("option4").toString(),
                            map.get("correctAns").toString(), (int) map.get("setNo"));
                    if (position != -1) {
                        QuestionActivity.list.set(position, questionModel);
                    } else {
                        QuestionActivity.list.add(questionModel);
                    }
                    Toast.makeText(AddQuestionActivity.this, "Question Added", Toast.LENGTH_SHORT).show();

                    finish();
                } else {
                    Toast.makeText(AddQuestionActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
                loading.dismiss();
            }
        });
    }

    public void setData() {
        question.setText(questionModel.getQuestion());
        ((EditText) answers.getChildAt(0)).setText(questionModel.getA());
        ((EditText) answers.getChildAt(1)).setText(questionModel.getB());
        ((EditText) answers.getChildAt(2)).setText(questionModel.getC());
        ((EditText) answers.getChildAt(3)).setText(questionModel.getD());

        for (int i = 0; i < answers.getChildCount(); i++) {
            if (((EditText) answers.getChildAt(i)).getText().toString().equals(questionModel.getAnswer())) {

                RadioButton radioButton = (RadioButton) options.getChildAt(i);
                radioButton.setChecked(true);
                break;
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
