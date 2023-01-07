package com.vu.studentapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyBroadcastServices extends BroadcastReceiver {
    public static final String ACTION_PROCESS_UPDATE = "com.mahmudjerrry.backgroundlocation.UPDATE_LOCATION";
    DatabaseReference myreference ;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null){
            final String action = intent.getAction();
            if(ACTION_PROCESS_UPDATE.equals(action)){
                LocationResult result = LocationResult.extractResult(intent);
                if(result!=null){
                    Location location = result.getLastLocation();
                    String  stringBuilder = new StringBuilder(" "+location.getLatitude()).append("/").append(location.getLongitude()).toString();

//                    FirebaseDatabase database = FirebaseDatabase.getInstance();
//                    myreference = database.getReference("Bus - 1");
//                    myreference.setValue(location);
//                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                    MediaPlayer mp = MediaPlayer.create(context.getApplicationContext(), notification);
//                    mp.start();
//                    Toast.makeText(context, "My Location: "+location.getLatitude(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
