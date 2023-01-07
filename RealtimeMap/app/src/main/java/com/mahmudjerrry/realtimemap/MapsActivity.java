package com.mahmudjerrry.realtimemap;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private DatabaseReference reference , reference1;
    private LocationManager manager;

    private final int MIN_TIME = 1000;
    private final int MIN_DIS = 1;
    Marker marker , marker2 ;
    Random random ;

    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        random = new Random();

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

//        //map service
//        Notification notification = new Notification.Builder(this)
//                        .setContentTitle("mapping")
//                        .setContentText("getting")
//                        .setSmallIcon(R.drawable.ic_launcher_background)
//                        .setContentIntent(getPendingIntent())
//                        .setTicker("OkTracker")
//                        .build();
//
//        startForegroundService(notification);


        getLocationiUpdates();
        updateLocation();
        readChanges();
        //readChange2();
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setSmallestDisplacement(1f);
    }

    private PendingIntent getPendingIntent() {

        Intent intent = new Intent(this,MyBroadcastServices.class);
        intent.setAction(MyBroadcastServices.ACTION_PROCESS_UPDATE);
        return PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

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

        marker = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Employee"));
        //marker2 = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

//        LatLng latLng = new LatLng();
//        try {
//            JSONArray jsonArray = new JSONArray("[{\"latitude\":24.3791347,\"longitude\":88.621968,\"time\":1617289405633},{\"latitude\":25.3791452,\"longitude\":86.621969,\"time\":1617289411821},{\"latitude\":27.3791441,\"longitude\":88.621958,\"time\":1617289418837},{\"latitude\":28.3791327,\"longitude\":88.6219591,\"time\":1617289429016},{\"latitude\":29.3791502,\"longitude\":88.6219612,\"time\":1617289434761},{\"latitude\":30.3791349,\"longitude\":88.6219589,\"time\":1617289440683},{\"latitude\"31.3791421,\"longitude\":88.6219661,\"time\":1617289456494},{\"latitude\":32.3792372,\"longitude\":88.6219803,\"time\":1617289462343},{\"latitude\":33.3791596,\"longitude\":88.6219679,\"time\":1617289468321},{\"latitude\":34.3791317,\"longitude\":88.6219622,\"time\":1617289474380},{\"latitude\":35.3791495,\"longitude\":88.6219699,\"time\":1617289480144},{\"latitude\":36.3792111,\"longitude\":88.62198,\"time\":1617289499204},{\"latitude\":37.3791429,\"longitude\":88.6219611,\"time\":1617289504957},{\"latitude\":38.3791281,\"longitude\":88.6219636,\"time\":1617289511316}]");
//
//            for(int i = 0 ; i<jsonArray.length(); i++){
//
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }



//        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
//                .clickable(true)
//                .add(
//                        new LatLng(24.3791347, 88.621968),
//                        new LatLng(25.3791452, 86.621969),
//                        new LatLng(27.3791441, 88.621958),
//                        new LatLng(28.3791327, 88.6219591),
//                        new LatLng(29.3791502, 88.6219612)
//                ));

        //mMap.setMinZoomPreference(12);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(location!=null){
            marker.setPosition(new LatLng(location.getLatitude()+randomNum(),location.getLongitude()));

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
                    Log.e("onchange","changed");
                    if(location!=null){
                            marker.setPosition(new LatLng(location.getLatitude()+randomNum(),location.getLongitude()));
//                        LatLng latLng = new LatLng(location.getLatitude()+randomNum(), location.getLongitude());
//                        mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Employee"));
//                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                    }
                }catch (Exception e){
                    Toast.makeText(MapsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    Log.e("error-hell-1",e.toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                    Log.e("error-hell-2",e.toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    float randomNum(){
        int x = random.nextInt(6) + 5;
        return Float.valueOf(String.valueOf(x));
    }
}