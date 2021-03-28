package com.mahmudjerrry.realtimemap;

public class RealtimeLocation {
    private double latitude ;
    private double longitude;

    public RealtimeLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public RealtimeLocation() { }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
