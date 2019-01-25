package com.eleganz.msafiri.model;

/**
 * Created by eleganz on 15/2/18.
 */

public class SelectRideModel {
    String img,vehiclename,vehiclenum,locationfrom,locationto,distance,price;
    float ratings;


    public SelectRideModel(String img, String vehiclename, String vehiclenum, String locationfrom, String locationto, String distance, String price, float ratings) {
        this.img = img;
        this.vehiclename = vehiclename;
        this.vehiclenum = vehiclenum;
        this.locationfrom = locationfrom;
        this.locationto = locationto;
        this.distance = distance;
        this.price = price;
        this.ratings = ratings;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getVehiclename() {
        return vehiclename;
    }

    public void setVehiclename(String vehiclename) {
        this.vehiclename = vehiclename;
    }

    public String getVehiclenum() {
        return vehiclenum;
    }

    public void setVehiclenum(String vehiclenum) {
        this.vehiclenum = vehiclenum;
    }

    public String getLocationfrom() {
        return locationfrom;
    }

    public void setLocationfrom(String locationfrom) {
        this.locationfrom = locationfrom;
    }

    public String getLocationto() {
        return locationto;
    }

    public void setLocationto(String locationto) {
        this.locationto = locationto;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public float getRatings() {
        return ratings;
    }

    public void setRatings(float ratings) {
        this.ratings = ratings;
    }
}
