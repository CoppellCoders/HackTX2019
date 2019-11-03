package com.cc.parkx;

import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class ParkingSpot {
    public String url;
    String address;
    double price;
    double distance;
    String phone;
    LatLng latLng;
    GoogleMap map;
    ImageButton reserve;

    public ParkingSpot(String address, double price, double distance, LatLng latLng, GoogleMap map, ImageButton reserve, String url, String phone) {
        this.address = address;
        this.price = price;
        this.distance = distance;
        this.latLng = latLng;
        this.map = map;
        this.reserve = reserve;
        this.url = url;
        this.phone = phone;
    }
}
