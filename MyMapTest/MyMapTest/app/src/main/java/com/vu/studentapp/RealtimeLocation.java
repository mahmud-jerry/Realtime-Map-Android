package com.vu.studentapp;

public class RealtimeLocation {
    private double latitude ;
    private double longitude;
    private float bearing ;

    public RealtimeLocation(double latitude, double longitude , float bearing) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.bearing = bearing ;
    }
    public RealtimeLocation() { }

    public float getBearing() {
        return bearing;
    }

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
