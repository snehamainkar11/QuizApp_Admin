package com.example.adminquizapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

public class adminCategory extends AppCompatActivity {
    CategoryAdapter adapter;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    private List<CategoryModel> list;
    private RecyclerView recyclerView;
    private Dialog loading,categoryDialog;
    private CircleImageView addImage;
    EditText categoryadd;
    Button add;
    private Uri image;
    String downloadUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Categories");

        loading=new Dialog(this);
        loading.setContentView(R.layout.loading);
        loading.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corners));
        loading.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loading.setCancelable(false);
        setCategoryDialog();

        recyclerView=findViewById(R.id.rv);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        list=new ArrayList<>();
        adapter=new CategoryAdapter(list, new CategoryAdapter.DeleteListener() {
            @Override
            public void onDelete(final String key, final int position) {

                new AlertDialog.Builder(adminCategory.this,R.style.Theme_AppCompat_Light_Dialog).setTitle("Delete Category").setMessage("Are you Sure.You want to to delete this category?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loading.show();
                                myRef.child("Categories").child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            myRef.child("SETS").child(list.get(position).getName()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        list.remove(position);
                                                        adapter.notifyDataSetChanged();
                                                        loading.dismiss();
                                                        Toast.makeText(adminCategory.this, "Deleted", Toast.LENGTH_SHORT).show();

                                                    }else
                                                    {
                                                        Toast.makeText(adminCategory.this, "Failed to delete", Toast.LENGTH_SHORT).show();

                                                    }
                                                    loading.dismiss();

                                                }
                                            });


                                        }else
                                        {
                                            Toast.makeText(adminCategory.this, "Failed to delete", Toast.LENGTH_SHORT).show();
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
        loading.show();
        myRef.child("Categories").addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                try{
                    for(DataSnapshot datasnapshot1: datasnapshot.getChildren()){
                        list.add(new CategoryModel(datasnapshot1.child("name").getValue().toString(),Integer.parseInt(datasnapshot1.child("sets").getValue().toString())
                        ,datasnapshot1.child("url").getValue().toString(),datasnapshot1.getKey()));

                    }
                    adapter.notifyDataSetChanged();
                    loading.dismiss();

                }


                catch(Exception e)
                {
                    Toast.makeText(adminCategory.this,e.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(adminCategory.this,error.getMessage(), Toast.LENGTH_LONG).show();
                loading.dismiss();
                finish();
            }


        } );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.categoryadd,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if((item.getItemId() == (R.id.add)))
        {
            categoryDialog.show();
        }
        return super.onOptionsItemSelected(item);
}
    private void setCategoryDialog()
    {
        categoryDialog=new Dialog(this);
        categoryDialog.setContentView(R.layout.add_category_dialog);
        categoryDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.category_bg));

        categoryDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        categoryDialog.setCancelable(true);
        add=categoryDialog.findViewById(R.id.add);
        categoryadd=categoryDialog.findViewById(R.id.category_name);
        addImage=categoryDialog.findViewById(R.id.image1);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery,101);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(categoryadd.getText().toString().isEmpty()){
                   categoryadd.setError("Required");
                   return;
               }
               if(image==null){
                   Toast.makeText(adminCategory.this, "Please select your image", Toast.LENGTH_SHORT).show();
                   return;
               }
               for(CategoryModel model:list){
                   if(categoryadd.getText().toString().equals(model.getName()))
                   {
                       categoryadd.setError("Category name alreday present");
                       return;
                   }

               }
               categoryDialog.dismiss();
            uploadData();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101){
            if(resultCode==RESULT_OK){
                image=data.getData();
                addImage.setImageURI(image);
            }
        }
    }
    private void uploadData(){
        loading.show();
        StorageReference storageReference= FirebaseStorage.getInstance().getReference();
        final StorageReference imageReference=storageReference.child("categories").child(image.getLastPathSegment());
        UploadTask uploadTask = imageReference.putFile(image);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            downloadUrl=task.getResult().toString();
                            uploadCategoryName();
                        }else{
                            loading.dismiss();
                            Toast.makeText(adminCategory.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                } else {
                    loading.dismiss();
                    Toast.makeText(adminCategory.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    private void uploadCategoryName(){
        Map<String,Object> map=new HashMap<>();
        map.put("name",categoryadd.getText().toString());
        map.put("sets",0);
        map.put("url",downloadUrl);
        FirebaseDatabase database=  FirebaseDatabase.getInstance();
        database.getReference().child("Categories").child("Category"+(list.size()+1)).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    list.add(new CategoryModel(categoryadd.getText().toString(),0,downloadUrl,"ccc"));
                    adapter.notifyDataSetChanged();
                }else
                {
                    loading.dismiss();
                    Toast.makeText(adminCategory.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                }
                loading.dismiss();
            }
        });


    }
}