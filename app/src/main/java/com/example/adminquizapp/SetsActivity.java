package com.example.adminquizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

public class SetsActivity extends AppCompatActivity {
    private GridView gridView;
    private Dialog loading;
    private GridAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets);
        Toolbar toolbar = findViewById(R.id.tb1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra("Title"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loading=new Dialog(this);
        loading.setContentView(R.layout.loading);
        loading.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corners));
        loading.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loading.setCancelable(false);
        gridView=findViewById(R.id.gridview);
          adapter =new GridAdapter(getIntent().getIntExtra("sets", 0), getIntent().getStringExtra("title"), new GridAdapter.GridListener() {
            @Override
            public void addSet() {
                loading.show();
                FirebaseDatabase database=FirebaseDatabase.getInstance();
                database.getReference().child("Categories").child(getIntent().getStringExtra("key")).child("sets").setValue(getIntent().getIntExtra("sets", 0)+1)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<Void> task) {
                                                       if(task.isSuccessful()){
                                                            adapter.sets++;
                                                            adapter.notifyDataSetChanged();
                                                       }
                                                       else
                                                       {
                                                           Toast.makeText(SetsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                       }
                                                       loading.dismiss();
                                                   }
                                               }
                        );


            }
        });
        gridView.setAdapter(adapter);


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}