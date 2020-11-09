package com.example.adminquizapp;

import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.app.ProgressDialog;

import android.app.Activity;


import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {
    Button btnSignIn;
    TextView txtsignUp;
    ProgressDialog pd;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mAuth = FirebaseAuth.getInstance();
      if (mAuth.getCurrentUser() != null) {
            updateUI(mAuth.getCurrentUser());
        }
    }


    public void signIn(View V) {

        final EditText editTextUserName = (EditText) findViewById(R.id.editTextUserNameToLogin);
        final EditText editTextPassword = (EditText) findViewById(R.id.editTextPasswordToLogin);

        Button btnSignIn = (Button) findViewById(R.id.buttonSignIn);


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pd = new ProgressDialog(MainActivity.this);
                pd.setTitle("Login...");
                pd.setMessage("Authenticating");
                pd.setCancelable(false);
                pd.setIndeterminate(true);
                pd.show();


                final String userName = editTextUserName.getText().toString();
                final String password = editTextPassword.getText().toString();

// fetch the Password form database for respective user name
                // String storedPassword=loginDataBaseAdapter.getSinlgeEntry(userName);

                //if(password.equals(storedPassword))
                if (TextUtils.isEmpty(userName)) {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }


                mAuth.signInWithEmailAndPassword(userName, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            pd.dismiss();
                            // Toast.makeText(MainActivity.this, "Congrats: Login Successfull", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                              updateUI(user);
                                finish();

                        } else {// there was an error
                            if (password.length() < 6) {
                                pd.dismiss();
                                editTextPassword.setError("Minimum Password");
                            } else
                                pd.dismiss();
                            Toast.makeText(MainActivity.this, "User Name or Password does not match", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }

            //@Override
            // protected void onDestroy() {
            //    super.onDestroy();
// Close The Database
            // loginDataBaseAdapter.close();
            // }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
    }

    public void updateUI(FirebaseUser currentUser) {

            Intent profileIntent = new Intent(MainActivity.this, adminCategory.class);
            profileIntent.putExtra("email", currentUser.getEmail());
            Log.v("DATA", currentUser.getUid());
            startActivity(profileIntent);

    }
    }
