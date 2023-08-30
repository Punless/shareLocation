package com.jierui.shareLocation;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DisplayCoordinatesActivity extends BaseActivity {

    FirebaseUser Auth;
    final String TAG = "finderActivity";



    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();
        String ID = intent.getStringExtra("UID");
        String Name = intent.getStringExtra("DisplayName");
        String Key = intent.getStringExtra("aKey");
        Toast.makeText(this, ID+","+Name, Toast.LENGTH_SHORT).show();

        TextView textName = findViewById(R.id.greetingMessage);
        textName.setText(Name+"'s location");
        Button getLocation = findViewById(R.id.getLocation);
        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLatitude(ID);
            }
        });
        Button deletion = findViewById(R.id.Delete);
        deletion.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            AlertDialog alertDialog = builder.create();
            alertDialog.setMessage("Delete This Person?");
            alertDialog.setTitle("Confirmation");
            alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Yes",
                    (dialog, which) -> {
                        DeleteUser(Key);
                    });
            alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "No",
                    (dialog, which) -> {
                        dialog.cancel();
                    });
            alertDialog.show();
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_display_coodinates;
    }


    private void DeleteUser(String key) {
        Auth = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDat = FirebaseDatabase.getInstance().getReference().child("Users").child(Auth.getUid()).child("canTrack");
        mDat.child(key).removeValue();
    }


    public void onResume() {

        super.onResume();




    }
    private void getLatitude(String ID){

        //TODO MAKE FUNCTIONS TO GET A REFERENCE AND "PUSH" OR ATTACH LISTENERS
        mDat = FirebaseDatabase.getInstance().getReference("Users/"+ID+"/Location/Latitude");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TextView Lat = findViewById(R.id.Latitude);
                Lat.setText("Latitude: "+snapshot.getValue().toString());
                Log.w(TAG, "Success!");
                double latDouble = Double.parseDouble(snapshot.getValue().toString());
                getLongitude(latDouble, ID);
                if (snapshot==null){
                    Toast.makeText(getApplicationContext(), "Value is null", Toast.LENGTH_SHORT).show();
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error :(", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Failure: ", error.toException());
            }
        };
        mDat.addValueEventListener(postListener);



    }
    private void getLongitude(double lat,String ID){
        mDat = FirebaseDatabase.getInstance().getReference("Users/"+ID+"/Location/Longitude");
        ValueEventListener anotherListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                TextView Long = findViewById(R.id.Longitude);
                Long.setText("Longitude: "+snapshot2.getValue().toString());
                double longDouble = Double.parseDouble(snapshot2.getValue().toString());
                addButtonFunction(lat, longDouble);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failure: ", error.toException());
            }
        };
        mDat.addValueEventListener(anotherListener);

    }

    private void addButtonFunction(double latDouble, double longDouble) {
        FloatingActionButton map = findViewById(R.id.mapButton);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("Latitude", latDouble);
                intent.putExtra("Longitude", longDouble);
                startActivity(intent);
            }
        });
    }
}
