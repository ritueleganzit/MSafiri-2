package com.eleganz.msafiri.model;

/**
 * Created by eleganz on 15/2/18.
 */

public class TripData {
    double lat,lng;
    String price,vechiclename,date;

    public TripData(double lat, double lng, String price, String vechiclename, String date) {
        this.lat = lat;
        this.lng = lng;
        this.price = price;
        this.vechiclename = vechiclename;
        this.date = date;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getVechiclename() {
        return vechiclename;
    }

    public void setVechiclename(String vechiclename) {
        this.vechiclename = vechiclename;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
