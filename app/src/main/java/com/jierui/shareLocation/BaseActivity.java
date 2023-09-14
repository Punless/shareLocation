package com.jierui.shareLocation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.function.Consumer;

public abstract class BaseActivity extends AppCompatActivity {
    public DatabaseReference mDat;
    FirebaseAuth mAuth;
    private static final String TAG = "BaseActivity";

    //User properties
    public String username;
    public String uid;

    //return values


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null)
        {

            uid = mAuth.getUid();
            mDat = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("DisplayName");
            mDat.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    username = task.getResult().getValue().toString();
                }
            });
                    //("Users/" + uid + "/DisplayName");

        }
        else
        {
            Intent intent = new Intent(this, SignInActivity.class);
            showToast("Please sign in!");
            startActivity(intent);
        }

    }

    protected void showToast(String mToastMsg) {
        Toast.makeText(this, mToastMsg, Toast.LENGTH_SHORT).show();
    }

    public void readDataOnce(String path, Consumer<Task<DataSnapshot>> consumer){
        mDat = FirebaseDatabase.getInstance().getReference(path);
        mDat.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete  (@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful())
                {
                    consumer.accept(task);
                }
                if (task.isCanceled())
                {
                    consumer.accept(null);
                    Log.e(TAG, "Error retrieving data...");
                }
            }
        });
    }
    public void attachListener(String path, Consumer<String> consumer){
        mDat = FirebaseDatabase.getInstance().getReference(path);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                consumer.accept(snapshot.getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                consumer.accept(null);
                Log.e(TAG, "Error retrieving data...");
            }
        };
        mDat.addValueEventListener(postListener);
    }








    public void signIn (String email,String password){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            boolean emailVerified = user.isEmailVerified();
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference reference = database.getReference("Users/"+user.getUid()+"/Verified");

                            if (emailVerified == true) {

                                reference.setValue(true);
                                startMyActivity(user.getUid());
                            }
                            else
                            {
                                reference.setValue(false);
                                showToast("Authentication Success");
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("BaseActivity", "signInWithEmail:failure", task.getException());
                            showToast("Authentication Failed");

                        }
                    }
                });
        //sign in and update ui

    }
    public void startMyActivity(String UID) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference aRef = database.getReference("Users/" + UID + "/DisplayName");
        aRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tempValue = snapshot.getValue(String.class);
                Intent intent = new Intent(getApplicationContext(), SendCoordinatesActivity.class);
                intent.putExtra("User", tempValue);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("BaseActivity", "Failed to read value.", error.toException());
            }
        });
    }


    protected abstract int getContentView();



}
