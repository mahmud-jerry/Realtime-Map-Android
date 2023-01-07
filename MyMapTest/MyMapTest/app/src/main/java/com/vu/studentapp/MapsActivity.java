package com.vu.studentapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vu.studentapp.databinding.ActivityMapsBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private DatabaseReference reference , reference1 , reference2, reference3;
    private LocationManager manager;
    LocationCallback locationCallback;

    private final int MIN_TIME = 100;
    private final int MIN_DIS = 1;
    Marker marker , marker2 , marker3 , marker4 ;
    Random random ;
    MarkerOptions markerOptions;

    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    String getRef ;

    Button defBtn ;

    //marker
    private Marker kajlaMarker , talaimariMarker, cseMarker;
    private Marker myMarker ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        markerOptions = new MarkerOptions();
//        binding = ActivityMapsBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
        setContentView(R.layout.new_map);

        defBtn = findViewById(R.id.default_loc_btn);

        defBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLatLang();
            }
        });

        Intent i = getIntent();
        getRef = i.getStringExtra("refKey");


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        manager = (LocationManager)getSystemService(LOCATION_SERVICE);
//        reference = FirebaseDatabase.getInstance().getReference().child("user-101");


        FirebaseDatabase database = FirebaseDatabase.getInstance();

        reference = database.getReference(getResources().getStringArray(R.array.busarray)[0]);
        reference1 = database.getReference(getResources().getStringArray(R.array.busarray)[1]);

        getLocationiUpdates();
        updateLocation();
        readChange();

    }
    private void getMyLatLang(){
        final String[] latlang = {"not found"};
        //loication
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng mylL = new LatLng(location.getLatitude(),location.getLongitude());
                            myMarker = mMap.addMarker(new MarkerOptions().position(mylL).title("My Position"));
                            myMarker.setIcon(BitmapFromVector(getApplicationContext(), R.drawable.ic_baseline_emoji_people_24));
                            myMarker.setFlat(true);
                            myMarker.setAnchor(0.5f,0.5f);
                            myMarker.showInfoWindow();
                            latlang[0] = location.getLatitude()+","+location.getLongitude();
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));

                        }else{

                        }
                    }
                });

    }
    private void getLatLang(){
        final String[] latlang = {"not found"};
        //loication
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            myMarker.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
                            latlang[0] = location.getLatitude()+","+location.getLongitude();
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));
                            mMap.animateCamera( CameraUpdateFactory.zoomTo( 17.0f ) );
                        }else{

                        }
                    }
                });
    }
    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
    }
    private void updateLocation() {
        buildLocationRequest();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());
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
    private PendingIntent getPendingIntent() {

        Intent intent = new Intent(this,MyBroadcastServices.class);
        intent.setAction(MyBroadcastServices.ACTION_PROCESS_UPDATE);
        return PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

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

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);

        LatLng kajla = new LatLng(24.364835, 88.633658);
        LatLng talaimari = new LatLng(24.361522, 88.626773);
        LatLng cse = new LatLng(24.360987, 88.625653);

        List<LatLng> latLngList = new ArrayList<>();
        latLngList.add(kajla);
        latLngList.add(talaimari);
        latLngList.add(cse);

        List<String> latlangString = new ArrayList<>();
        latlangString.add("Main Bus Stand");
        latlangString.add("Talaimari Bus Stand");
        latlangString.add("CSE Stand");

        for(int i = 0 ; i<latLngList.size() ; i++){
            mMap.addMarker(new MarkerOptions().position(latLngList.get(i)).title(latlangString.get(i))).setIcon(BitmapFromVector(getApplicationContext(),R.drawable.vu_stand));
        }

        getMyLatLang();


        marker2 = mMap.addMarker(new MarkerOptions().position(sydney).title("Bus Two - Girls"));
        marker2.setIcon(BitmapFromVector(getApplicationContext(), R.drawable.vu_bus_ver));
        marker2.setFlat(true);
        marker2.setAnchor(0.5f,0.5f);
        marker2.showInfoWindow();

        marker = mMap.addMarker(new MarkerOptions().position(sydney).title("Bus One - Boys"));
        marker.setIcon(BitmapFromVector(getApplicationContext(), R.drawable.vu_bus_ver));
        marker.setFlat(true);
        marker.setAnchor(0.5f,0.5f);
        marker.showInfoWindow();


        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 17.0f ) );
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(location!=null){
            //Toast.makeText(MapsActivity.this, "My Speed: "+location.getSpeed(), Toast.LENGTH_SHORT).show();
            myMarker.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
//            markerOptions.position(new LatLng(location.getLatitude(),location.getLongitude()));
//            markerOptions.title("hello marker");
//            //mMap.clear();
//            mMap.addMarker(markerOptions);

            //saveLocation(location);
            Log.e("latlang",location.getLatitude()+"lang"+location.getLongitude());
        }else{
            Toast.makeText(this, "No Location", Toast.LENGTH_SHORT).show();
        }
    }
    private void saveLocation(Location location) {

        switch (getRef){
            case "Bus - 1":
                reference.setValue(location);
                break;
            case "Bus - 2":
                reference1.setValue(location);
                break;

        }

    }
    private void readChange(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try{
                    RealtimeLocation location = snapshot.getValue(RealtimeLocation.class);
                    if(location!=null){
                        //marker = mMap.addMarker(new MarkerOptions().position(sydney).title("Bus One - VU"));
                        marker.setRotation(location.getBearing());
                        marker.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
                        LatLng latlang = new LatLng(location.getLatitude(),location.getLongitude());

                    }
                }catch (Exception e){
                    Toast.makeText(MapsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    Log.e("error-hell-2",e.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }

        });
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try{
                    RealtimeLocation location = snapshot.getValue(RealtimeLocation.class);
                    if(location!=null){
                        marker2.setRotation(location.getBearing());
                        marker2.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
                        LatLng latlang = new LatLng(location.getLatitude(),location.getLongitude());

                    }
                }catch (Exception e){
                    Toast.makeText(MapsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    Log.e("error-hell-2",e.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }

        });
    }
    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    protected void onPause() {
        fusedLocationProviderClient.removeLocationUpdates(getPendingIntent());
        super.onPause();
    }
}