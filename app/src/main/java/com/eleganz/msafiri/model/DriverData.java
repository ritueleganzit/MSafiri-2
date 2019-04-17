package com.eleganz.msafiri.model;

/**
 * Created by eleganz on 28/11/18.
 */

public class DriverData {

    String id,trip_id,driver_id,photo,vehiclename,pickup,destination,time,rating,price;


    public DriverData(String trip_id, String driver_id, String photo, String vehiclename, String pickup, String destination, String time, String rating, String price) {
        this.trip_id = trip_id;
        this.driver_id = driver_id;
        this.photo = photo;
        this.vehiclename = vehiclename;
        this.pickup = pickup;
        this.destination = destination;
        this.time = time;
        this.rating = rating;
        this.price = price;
    }



    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getVehiclename() {
        return vehiclename;
    }

    public void setVehiclename(String vehiclename) {
        this.vehiclename = vehiclename;
    }

    public String getPickup() {
        return pickup;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}


