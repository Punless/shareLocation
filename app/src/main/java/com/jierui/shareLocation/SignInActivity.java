package com.jierui.shareLocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;



    
public class SignInActivity extends BaseActivity {

    @Override
    protected int getContentView() {
        return R.layout.activity_signin;//your layout
    }

    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseAuth mAuth;

        TextView showUID = findViewById(R.id.showUID);

// ...
// Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            String autologinusername = currentUser.getUid();
            Toast.makeText(SignInActivity.this,"User already signed in...",
                    Toast.LENGTH_SHORT).show();
            if (currentUser.isEmailVerified() == true)
            {
                startMyActivity(autologinusername);
            }
            else
            {
                Toast.makeText(SignInActivity.this,"Please verify!",
                        Toast.LENGTH_SHORT).show();
                sendEmailVerification();
            }

        }
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        Log.d(TAG, "This program is running");
        //TODO Allow the user to input a name, then a key phrase. Output the keyphrase when loading
        Toolbar toolbar = findViewById(R.id.toolbar);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Button submitButton = findViewById(R.id.registbutton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegistering();
                /*
                EditText usernameInput = findViewById(R.id.usernameText);
                EditText passwordInput = findViewById(R.id.passwordText);
                String inputStr = String.valueOf(usernameInput.getText());
                String passStr = String.valueOf(passwordInput.getText());
                UsernamePath = inputStr;
                final Boolean[] newAccountCreated = {false};
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Users/"+inputStr+"/Password");
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String tempValue = snapshot.getValue(String.class);
                        TextView rMessage = findViewById(R.id.receivedMessage);
                        if (testNull(tempValue))
                        {
                            Log.d(TAG, "New Account Created!");
                            rMessage.setText("New account created! \n Username:" + inputStr + "\n Password:" + passStr);
                            myRef.setValue(passStr);
                            newAccountCreated[0] = true;
                        }
                        else
                        {

                            Log.d(TAG, "Username taken; " + newAccountCreated[0]);
                            if(newAccountCreated[0])
                            {
                                newAccountCreated[0] = false;
                            }
                            else
                            {
                                rMessage.setText("Username has been taken!");
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });

                 */
            }
        });
        Button loginButton = findViewById(R.id.loginbutton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText usernameInput = findViewById(R.id.usernameText);
                EditText passwordInput = findViewById(R.id.passwordText);
                String userCheck = usernameInput.getText().toString();
                String passCheck = passwordInput.getText().toString();
                if (userCheck.equals("")||passCheck.equals(""))
                {
                    Toast.makeText(SignInActivity.this,"Login Error",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    signIn(usernameInput.getText().toString(),passwordInput.getText().toString());

                }


                /*
                String inputStr = String.valueOf(usernameInput.getText());


                FirebaseDatabase database = FirebaseDatabase.getInstance();
                TextView rMessage = findViewById(R.id.receivedMessage);



                DatabaseReference aRef = database.getReference("Users/"+inputStr+"/Password");
                aRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String tempValue = snapshot.getValue(String.class);
                        Log.d(TAG, "Another Value is: " + tempValue);
                        String passStr = String.valueOf(passwordInput.getText());
                        if (passStr.equals(tempValue))
                        {
                            Log.d(TAG, "Login Successful");
                            rMessage.setText("You have successfully logged in!");
                            startMyActivity(inputStr);
                        }
                        else
                        {
                            if(testNull(tempValue))
                            {
                                Log.d(TAG, "No User Detected");
                                rMessage.setText("Please make a new account!");
                            }
                            else
                            {
                                Log.d(TAG, "Password Error");
                                rMessage.setText("Password is incorrect.");
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });


                */


            }

        });



/*
        String Username = UsernamePath;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users/"+Username+"/Password");
        Log.d(TAG,"Users/"+Username+"/Password");
// Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
                TextView rMessage = findViewById(R.id.receivedMessage);
                rMessage.setText("The message received is: " + UsernamePath);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
            private DatabaseReference mDatabase;

        });
        */





    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
    public boolean testNull(String s){
        boolean returnValue;
        returnValue = s == null;
        if (returnValue == true)
        {
            Log.d(TAG, "Got a null value!");
        }
        else
        {
            Log.d(TAG, "Did not get a null value!");
        }
        return returnValue;
    }





    public void openRegistering(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void signIn (String email,String password){
 /*
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                String displayName = user.getDisplayName();
                                boolean emailVerified = user.isEmailVerified();
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference anotherRef = database.getReference("Users/"+user.getUid()+"/Verified");


                                if (emailVerified == true) {

                                    anotherRef.setValue(true);
                                    updateUI(user);
                                }
                                else
                                {
                                    anotherRef.setValue(false);
                                    Toast.makeText(SignInActivity.this, "Please verify your email!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(SignInActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

     */
        //sign in and update ui



    }

    private void updateUI(FirebaseUser o) {
        startMyActivity(o.getUid());
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
}
