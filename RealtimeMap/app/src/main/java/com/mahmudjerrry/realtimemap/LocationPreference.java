package com.mahmudjerrry.realtimemap;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.core.content.ContextCompat;

import org.json.JSONArray;

public class LocationPreference {
    private Context CONTEXT;
    public LocationPreference(Context CONTEXT) {
        this.CONTEXT = CONTEXT;
    }
    public void onTokenSet(String TOKEN){
        SharedPreferences sharedPreferences=CONTEXT.getSharedPreferences("locationData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("locationKey",TOKEN);
        editor.commit();
    }
    public String onTokenGet(){
        SharedPreferences sharedPreferences=CONTEXT.getSharedPreferences("locationData", Context.MODE_PRIVATE);
        String location=sharedPreferences.getString("locationKey","null");
        return location;
    }
}
