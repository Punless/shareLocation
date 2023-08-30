package com.jierui.shareLocation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;


import android.widget.Toast;


public class SendCoordinatesActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private static final String TAG = "MainActivity";
    private DatabaseReference mDatabase;
    private LocationRequest locationRequest;
    TextView address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_coords);
        Log.d(TAG, "This program is running");
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //Textview+buttons
        address = findViewById(R.id.locationOutput);

        Button button = findViewById(R.id.button);
        FloatingActionButton finder = findViewById(R.id.finderButton);

        Button Signout = findViewById(R.id.signOut);
        TextView showUID = findViewById(R.id.showUID);

        //Get data from previous activity
        Intent intent = getIntent();
        String Activity2User = intent.getStringExtra("User");
        TextView GMessage = findViewById(R.id.greetingMessage);
        //id="@+id/greetingMessage
        String GreetingMessageSet = "Hello, " + Activity2User;
        GMessage.setText(GreetingMessageSet);


        mDatabase = FirebaseDatabase.getInstance().getReference("Users/" + Activity2User).child("StoredData");

        showUID.setText("UID: "+ user.getUid());
        Signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);

            }
        });



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(SendCoordinatesActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        getLocation();
                    } else {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }
                }
            }
        });

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SendCoordinatesActivity.this, "Please find location", Toast.LENGTH_SHORT).show();
            }
        });
        finder.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), AccountSelectionActivity.class);
                startActivity(intent);
            }
        });

        ImageButton setting = findViewById(R.id.settingButton);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LinkAccountActivity.class);
                startActivity(intent);
            }
        });


    }

    private FusedLocationProviderClient fusedLocationClient;

    private void getLocation() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            address.setText("Longitude: " + location.getLongitude() + "\nLatitude: "+ location.getLatitude());
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference lat = database.getReference("Users/"+user.getUid()+"/Location/Latitude");
                            lat.setValue(location.getLatitude());
                            DatabaseReference longi = database.getReference("Users/"+user.getUid()+"/Location/Longitude");
                            longi.setValue(location.getLongitude());


                            address.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                                    intent.putExtra("Latitude", ( location.getLatitude()));
                                    intent.putExtra("Longitude", ( location.getLongitude()));
                                    startActivity(intent);
                                }
                            });

                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Cannot get location!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });



    }


}