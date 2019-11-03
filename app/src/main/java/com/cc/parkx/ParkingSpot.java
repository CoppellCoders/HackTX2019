package com.cc.parkx;

public class ParkingSpot {
    String address;
    double price;
    double distance;

    public ParkingSpot(String address, double price, double distance) {
        this.address = address;
        this.price = price;
        this.distance = distance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
