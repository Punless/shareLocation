package com.jierui.shareLocation;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "registPage";
    public String UsernamePath;
    public int randomNumber;
    private FirebaseAuth mAuth;
    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(TAG, "This program is running");
        Toolbar toolbar = findViewById(R.id.toolbar);
        EditText email = findViewById(R.id.registEmail);
        EditText username = findViewById(R.id.registUsername);
        EditText password = findViewById(R.id.registPassword);

        mAuth = FirebaseAuth.getInstance();


        Button sendCode = findViewById(R.id.sendEmail);
        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (email.getText().toString().isEmpty()) {
                    invalidinfo(1);
                    return;
                }
                if (!email.getText().toString().contains("@")){
                    invalidinfo(2);
                    return;
                }
                if (username.getText().toString().isEmpty()){
                    invalidinfo(3);
                    return;
                }
                if (password.getText().toString().isEmpty())
                {
                    invalidinfo(4);
                    return;
                }
                createAccount(email.getText().toString(),password.getText().toString(),username.getText().toString());



            }
        });


        Button submission = findViewById(R.id.submitinfo);
        submission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( !username.getText().toString().isEmpty())
                {
                    passDataToRealtime(username.getText().toString());
                }
                else{
                    Toast.makeText(RegisterActivity.this,"Please input a username",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });


        }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

// Initialize Firebase Auth

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Toast.makeText(RegisterActivity.this,"User already signed in...",
                    Toast.LENGTH_SHORT).show();
        }
    }
    private void createAccount(String email, String password, String username) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            sendEmailVerification();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .build();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            sendEmailVerification();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .build();
                        }
                    }
                });
    }
    private void invalidinfo(int errorcode)
    {
        //TODO Turn this into switchcase + default clause


        String errorMessage = null;
        if (errorcode == 1)
        {
            errorMessage = "Please enter an email";
        }
        else if (errorcode == 2)
        {
            errorMessage = "Your email is invalid";
        }
        else if (errorcode == 3)
        {
            errorMessage = "Please enter an username";
        }
        else if (errorcode == 4)
        {
            errorMessage = "Please enter an password";
        }
        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
    }
    public void sendEmailVerification() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
    }
    public void passDataToRealtime(String displayName){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(RegisterActivity.this, "No user detected...", Toast.LENGTH_SHORT).show();
        }
        else {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build();
            String email = user.getEmail();
            String UID = user.getUid();
            boolean emailVerified = user.isEmailVerified();
            //put information into realtime
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Users/"+UID+"/DisplayName");
            myRef.setValue(displayName);

            if (emailVerified == true)
            {
                DatabaseReference anotherRef = database.getReference("Users/"+UID+"/Verified");
                anotherRef.setValue(true);

                Intent intent = new Intent(getApplicationContext(), SendCoordinatesActivity.class);
                intent.putExtra("User", displayName);
                startActivity(intent);
            }
            else
            {
                DatabaseReference anotherRef = database.getReference("Users/"+UID+"/Verified");
                anotherRef.setValue(false);
                Toast.makeText(RegisterActivity.this, "Please verify your email!",
                        Toast.LENGTH_SHORT).show();
            }

        }


    }


}
