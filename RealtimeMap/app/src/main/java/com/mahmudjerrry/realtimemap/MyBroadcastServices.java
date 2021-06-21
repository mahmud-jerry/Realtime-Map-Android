package com.mahmudjerrry.realtimemap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyBroadcastServices extends BroadcastReceiver {
    public static final String ACTION_PROCESS_UPDATE = "com.mahmudjerrry.backgroundlocation.UPDATE_LOCATION";
    DatabaseReference myreference ;
    LocationPreference locationPreference ;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null){
            final String action = intent.getAction();
            if(ACTION_PROCESS_UPDATE.equals(action)){
                LocationResult result = LocationResult.extractResult(intent);
                if(result!=null){
                    Location location = result.getLastLocation();
                    String  stringBuilder = new StringBuilder(" "+location.getLatitude()).append("/").append(location.getLongitude()).toString();

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    myreference = database.getReference("message");
                    myreference.setValue(location);
                    Utils.sendNotification(context, Utils.getLocationResultTitle(context, location));
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    MediaPlayer mp = MediaPlayer.create(context.getApplicationContext(), notification);
                    mp.start();

                    //save to preference
                    locationPreference = new LocationPreference(context);
                    String locationHistory = locationPreference.onTokenGet();
                    if(!locationHistory.equals("null")){
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("latitude",location.getLatitude());
                            jsonObject.put("longitude",location.getLongitude());
                            jsonObject.put("time",location.getTime());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        locationHistory = locationHistory+","+jsonObject.toString();
                    }else{
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("latitude",location.getLatitude());
                            jsonObject.put("longitude",location.getLongitude());
                            jsonObject.put("time",location.getTime());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        locationHistory = "["+jsonObject.toString();
                    }
                    locationPreference.onTokenSet(locationHistory);
                    Log.e("locationPreference",locationPreference.onTokenGet()+"]");

                    Log.e("locationArray",location.getLatitude()+" "+location.getLongitude()+" "+location.getTime());
                    Toast.makeText(context, "My Location: "+location.getLatitude(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
