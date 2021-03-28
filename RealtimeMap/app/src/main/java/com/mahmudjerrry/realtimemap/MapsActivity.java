package com.mahmudjerrry.realtimemap;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private DatabaseReference reference , reference1;
    private LocationManager manager;

    private final int MIN_TIME = 1000;
    private final int MIN_DIS = 1;
    Marker marker , marker2 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //FirebaseDatabase.getInstance().getReference().setValue("this is tracker app");

        manager = (LocationManager)getSystemService(LOCATION_SERVICE);
//        reference = FirebaseDatabase.getInstance().getReference().child("user-101");


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference("message");
        reference1 = database.getReference("message-2");


//        FirebaseDatabase database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://scenic-scholar-309006-default-rtdb.firebaseio.com/");
        //DatabaseReference myRef = database.getReference("message");
        //myRef.setValue("this is a tracking app");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);






        getLocationiUpdates();
        readChanges();
        readChange2();
    }

    private void readChange2() {
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    RealtimeLocation location = snapshot.getValue(RealtimeLocation.class);
                    if(location!=null){
                        marker2.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
                    }
                }catch (Exception e){
                    Toast.makeText(MapsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    Log.e("error",e.toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==101){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getLocationiUpdates();
            }else{
                Toast.makeText(this, "Permission Required!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(24.37914471, 88.6219901);
        marker = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        marker2 = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.setMinZoomPreference(12);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker2.getPosition()));
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(location!=null){
            saveLocation(location);
            Log.e("latlang",location.getLatitude()+"lang"+location.getLongitude());
        }else{
            Toast.makeText(this, "No Location", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveLocation(Location location) {
        reference.setValue(location);
    }
    private void readChanges(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    RealtimeLocation location = snapshot.getValue(RealtimeLocation.class);
                    if(location!=null){
                            marker.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
                    }
                }catch (Exception e){
                    Toast.makeText(MapsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    Log.e("error",e.toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}