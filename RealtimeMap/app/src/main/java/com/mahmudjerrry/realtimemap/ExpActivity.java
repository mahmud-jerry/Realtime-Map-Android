package com.mahmudjerrry.realtimemap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Marker;

public class ExpActivity extends AppCompatActivity implements LocationListener {
    private final int MIN_TIME = 1000;
    private final int MIN_DIS = 1;
    private LocationManager manager;

    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;

    private Button toggleBtn ;
    private boolean isUpdating = false ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exp);

        toggleBtn = findViewById(R.id.btn);

        toggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isUpdating){
                    isUpdating = false;
                    fusedLocationProviderClient.removeLocationUpdates(getPendingIntent());
                }else{
                    updateLocation();
                }

            }
        });


        manager = (LocationManager)getSystemService(LOCATION_SERVICE);

        getLocationiUpdates();
        updateLocation();


    }
    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setSmallestDisplacement(1f);
    }
    private void updateLocation() {
        buildLocationRequest();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());
        isUpdating = true;
    }
    private PendingIntent getPendingIntent() {

        Intent intent = new Intent(this,MyBroadcastServices.class);
        intent.setAction(MyBroadcastServices.ACTION_PROCESS_UPDATE);
        return PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==101){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getLocationiUpdates();
                updateLocation();
            }else{
                Toast.makeText(this, "Permission Required!", Toast.LENGTH_SHORT).show();
            }
        }

    }
    private void getLocationiUpdates() {
        if(manager!=null){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED&&
                    ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME,MIN_DIS,this);
                }else if(manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME,MIN_DIS,this);
                }else{
                    Toast.makeText(this, "No provider Enabled", Toast.LENGTH_SHORT).show();
                }
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},101);
            }

        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(location!=null){
            //saveLocation(location);
            Log.e("latlang",location.getLatitude()+"lang"+location.getLongitude());
        }else{
            Toast.makeText(this, "No Location", Toast.LENGTH_SHORT).show();
        }
    }

}