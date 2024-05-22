package com.jierui.shareLocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AccountSelectionActivity extends AppCompatActivity  {
    private DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    public int buttonID;
    ArrayList<String> ids = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> Keys = new ArrayList<>();


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FirebaseUser user = mAuth.getInstance().getCurrentUser();

        //getUID
        TextView showUID = findViewById(R.id.showUID);
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("UID: ").append(user.getUid());
        showUID.setText("UID: "+ user.getUid());



        TextView finderText = findViewById(R.id.finderText);
        Button button = findViewById(R.id.Add);
        Button button2 = findViewById(R.id.Update);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mDatabase = FirebaseDatabase.getInstance().getReference("Users/"+user.getUid()+"/canTrack");
                mDatabase.push().setValue("Testvalue");

            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase = FirebaseDatabase.getInstance().getReference("Users/"+user.getUid()+"/canTrack");
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Clear();
                        int n = 0;
                        for (DataSnapshot postSnapshot: snapshot.getChildren()){
                            n++;
                            buttonID = n;

                            List(postSnapshot,n);

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("finderActivity", "loadPost:onCancelled", error.toException());
                    }
                });
            }
        });

    }


    private void Clear() {
        TextView finderText = findViewById(R.id.finderText);
        finderText.setText("");
        LinearLayout ll = findViewById(R.id.aLinearLayout);
        ll.removeAllViews();
    }

    private void List(DataSnapshot obj, int i) {

        TextView finderText = findViewById(R.id.finderText);
        finderText.append("\n"+ obj.getValue().toString());
        retrieveName(obj, i);
    }

    private void updateButtons(String dispName, int i, DataSnapshot obj){
        String accountUID = obj.getValue().toString();
        String key = obj.getKey();
        LinearLayout ll = findViewById(R.id.aLinearLayout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        Button btn = new Button(this);
        btn.setId(i);
        final int id_ = btn.getId();
        btn.setText(dispName+"'s button");
        ll.addView(btn, params);
        Button btn1 = ((Button) findViewById(id_));
        ids.add(accountUID);
        names.add(dispName);
        Keys.add(key);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),
                                "You clicked on this button: " + id_, Toast.LENGTH_SHORT)
                        .show();
                startMyActivity(id_);

            }
        });


    }
    private void startMyActivity(int value) {
        Intent intent = new Intent(this, DisplayCoordinatesActivity.class);
        intent.putExtra("UID",ids.get(value-1));
        intent.putExtra("DisplayName",names.get(value-1));
        intent.putExtra("aKey",Keys.get(value-1));
        startActivity(intent);
    }


    private void retrieveName(DataSnapshot snap, int i) {
        final String[] value = new String[1];

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(snap.getValue().toString()).child("DisplayName").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
             if (task.isSuccessful()) {
                Log.d("firebase","retrieved successfully!");
                if (task.getResult().getValue()!=null){
                    Toast.makeText(AccountSelectionActivity.this, "Value gotten successfully: "+ task.getResult().getValue().toString(), Toast.LENGTH_SHORT).show();
                    updateButtons(task.getResult().getValue().toString(), i, snap);

                 }
                 else
                 {
                     Log.e("firebase", "Null value detected", task.getException());
                     Toast.makeText(AccountSelectionActivity.this, "One or more accounts does not have a username!", Toast.LENGTH_SHORT).show();
                     value[0] = "error";

                 }

             }
             else{
                Log.e("firebase", "There was a problem retrieving info...", task.getException());

             }
            }
        });


    } //Returns displayname using a UID





}

