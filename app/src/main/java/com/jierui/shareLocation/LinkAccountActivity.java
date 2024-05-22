package com.jierui.shareLocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class LinkAccountActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private DatabaseReference mDat;

    ArrayList<String> Names = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_account);

        mAuth = FirebaseAuth.getInstance();


        Button link = findViewById(R.id.Link);
        //updateList();

        /*
        mDat = FirebaseDatabase.getInstance().getReference("Users/"+mAuth.getUid()+"/Trackers");
        mDat.push().setValue("Account1");
         */
          //Give my account a tracker

        /*
        mDat = FirebaseDatabase.getInstance().getReference("Users/Account1/canTrack");
        mDat.push().setValue(user.getUid());
         */
         //Allow account 1 to track me

        updateUI();

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editUID = findViewById(R.id.editUID);
                String UID = String.valueOf(editUID.getText());
                makeCanTrack(UID);


            }
        });



    }


    private void updateUI() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null)
        {
            return;
        }
        mDat = FirebaseDatabase.getInstance().getReference("Users/" + user.getUid() + "/Trackers");
        mDat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Clear();
                int n = 0;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    n++;
                    getDisplayName(postSnapshot,n);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("finderActivity", "loadPost:onCancelled", error.toException());
            }
        });
    }

    private void makeTracker(String uid) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null)
        {
            return;
        }
        mDat = FirebaseDatabase.getInstance().getReference("Users/"+user.getUid()+"/Trackers");
        mDat.push().setValue(uid);
    }

    private void makeCanTrack(String uid) {
        mAuth.getCurrentUser();
        String value1 = String.valueOf(mAuth.getUid());
        if (value1.equals(uid)){
            Toast.makeText(this, "Value cannot be your own UID!", Toast.LENGTH_SHORT).show();
        }
        else{
            mDat = FirebaseDatabase.getInstance().getReference("Users/"+uid);
            mDat.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        if (task.getResult().getValue() == null)
                        {

                            Toast.makeText(LinkAccountActivity.this, "No account detected!", Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            Toast.makeText(LinkAccountActivity.this, "Tracker added successfully", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            mDat = FirebaseDatabase.getInstance().getReference("Users/"+ uid +"/canTrack");
                            if (user == null)
                            {
                                return;
                            }
                            mDat.push().setValue(user.getUid());
                            makeTracker(uid);
                        }

                    }

                    
                }
                
            });

        }


    }




    private void Clear() {
        LinearLayout ll = findViewById(R.id.aLinearLayout);
        ll.removeAllViews();
    }
    private void getDisplayName(DataSnapshot postSnapshot, int n) {

        String value = Objects.requireNonNull(postSnapshot.getValue()).toString();
        Toast.makeText(this, value,Toast.LENGTH_SHORT).show();
        mDat = FirebaseDatabase.getInstance().getReference("Users/" + value + "/DisplayName");
        mDat.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        updateList(String.valueOf(task.getResult().getValue()),value,n);
                    }
                });




    } //Get info about who can track me
    private void updateList(String displayName,String ID,int n) {

        LinearLayout ll = findViewById(R.id.aLinearLayout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        Button btn = new Button(this);
        btn.setId(n);
        final int id_ = btn.getId();
        btn.setText(displayName);
        ll.addView(btn, params);
        Names.add(ID);
        Button btn1 = (findViewById(id_));
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlert(Names.get(id_-1));
            }
        });
    } //Continued...
    private void createAlert(String Tracker) {
        //TODO Get the tracker's ID, then loop through the list to find my ID... Delete ID from Account1's canTrack, then remove Account1 as a tracker...

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        AlertDialog alertDialog = builder.create();
        alertDialog.setMessage("Delete This Person?");
        alertDialog.setTitle("Confirmation");
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Yes",
                (dialog, which) -> deleteUser(Tracker));
        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "No",
                (dialog, which) -> dialog.cancel());
        alertDialog.show();

    }
    private void deleteUser(String tracker){

        FirebaseUser user = mAuth.getCurrentUser();
        mDat = FirebaseDatabase.getInstance().getReference("Users/"+tracker+"/canTrack");
        mDat.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getResult().getChildren())
                {
                    String value1 = Objects.requireNonNull(postSnapshot.getValue()).toString();
                    if (value1.equals(Objects.requireNonNull(user).getUid()))
                    {
                        Toast.makeText(LinkAccountActivity.this, "Tracker removed", Toast.LENGTH_SHORT).show();
                        mDat = FirebaseDatabase.getInstance().getReference("Users/"+tracker+"/canTrack/"+postSnapshot.getKey());
                        mDat.removeValue();

                    }
                    else{
                        Toast.makeText(LinkAccountActivity.this, "."+postSnapshot.getValue().toString()+"=/="+user.getUid(), Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

        RemoveTracker(tracker);
    }
    private void RemoveTracker(String tracker){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDat = FirebaseDatabase.getInstance().getReference("Users/"+ Objects.requireNonNull(user).getUid()+"/Trackers");
        mDat.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getResult().getChildren())
                {
                    if (Objects.requireNonNull(postSnapshot.getValue()).toString().equals(tracker))
                    {

                        String value1 = postSnapshot.getValue().toString();
                        if (value1.equals(tracker))
                        {
                            Toast.makeText(LinkAccountActivity.this, "Tracker Removed", Toast.LENGTH_SHORT).show();
                            mDat = FirebaseDatabase.getInstance().getReference("Users/"+user.getUid()+"/Trackers/"+postSnapshot.getKey());
                            mDat.removeValue();
                        }
                    }

                }
            }
        });
/*


        mDat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseUser user = mAuth.getCurrentUser();
                for (DataSnapshot postSnapshot: snapshot.getChildren())
                {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(linkAccount.this, "Failed...", Toast.LENGTH_SHORT).show();
            }
        });
            */

    }



}


