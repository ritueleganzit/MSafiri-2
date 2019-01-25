package com.eleganz.msafiri.utils;

import java.io.Serializable;

/**
 * Created by eleganz on 4/1/19.
 */

public class HistoryData implements Serializable {

    String driver_id,trip_id,rating,comments,user_trip_status,status,from_title,from_lat,from_lng,from_address
       ,    to_title,to_lat,to_lng,to_address,end_datetime,last_lat,last_lng,date,fullname,photo,vehicle_name,trip_price,calculate_time,trip_screenshot;


    public String getTrip_screenshot() {
        return trip_screenshot;
    }

    public void setTrip_screenshot(String trip_screenshot) {
        this.trip_screenshot = trip_screenshot;
    }

    public HistoryData(String driver_id, String trip_id, String rating, String comments, String user_trip_status, String status, String from_title, String from_lat, String from_lng, String from_address, String to_title, String to_lat, String to_lng, String to_address, String end_datetime, String last_lat, String last_lng, String fullname, String photo, String vehicle_name, String trip_price, String calculate_time, String trip_screenshot) {
        this.driver_id = driver_id;
        this.trip_id = trip_id;
        this.rating = rating;
        this.comments = comments;
        this.user_trip_status = user_trip_status;
        this.status = status;
        this.from_title = from_title;
        this.from_lat = from_lat;
        this.from_lng = from_lng;
        this.from_address = from_address;
        this.to_title = to_title;
        this.to_lat = to_lat;
        this.to_lng = to_lng;
        this.to_address = to_address;
        this.end_datetime = end_datetime;
        this.last_lat = last_lat;
        this.last_lng = last_lng;
        this.fullname = fullname;
        this.photo = photo;
        this.vehicle_name = vehicle_name;
        this.trip_price = trip_price;
        this.calculate_time = calculate_time;
        this.trip_screenshot = trip_screenshot;
    }

    public String getCalculate_time() {
        return calculate_time;
    }

    public void setCalculate_time(String calculate_time) {
        this.calculate_time = calculate_time;
    }

    public String getTrip_price() {
        return trip_price;
    }

    public void setTrip_price(String trip_price) {
        this.trip_price = trip_price;
    }

    public String getVehicle_name() {
        return vehicle_name;
    }

    public void setVehicle_name(String vehicle_name) {
        this.vehicle_name = vehicle_name;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getUser_trip_status() {
        return user_trip_status;
    }

    public void setUser_trip_status(String user_trip_status) {
        this.user_trip_status = user_trip_status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFrom_title() {
        return from_title;
    }

    public void setFrom_title(String from_title) {
        this.from_title = from_title;
    }

    public String getFrom_lat() {
        return from_lat;
    }

    public void setFrom_lat(String from_lat) {
        this.from_lat = from_lat;
    }

    public String getFrom_lng() {
        return from_lng;
    }

    public void setFrom_lng(String from_lng) {
        this.from_lng = from_lng;
    }

    public String getFrom_address() {
        return from_address;
    }

    public void setFrom_address(String from_address) {
        this.from_address = from_address;
    }

    public String getTo_title() {
        return to_title;
    }

    public void setTo_title(String to_title) {
        this.to_title = to_title;
    }

    public String getTo_lat() {
        return to_lat;
    }

    public void setTo_lat(String to_lat) {
        this.to_lat = to_lat;
    }

    public String getTo_lng() {
        return to_lng;
    }

    public void setTo_lng(String to_lng) {
        this.to_lng = to_lng;
    }

    public String getTo_address() {
        return to_address;
    }

    public void setTo_address(String to_address) {
        this.to_address = to_address;
    }

    public String getEnd_datetime() {
        return end_datetime;
    }

    public void setEnd_datetime(String end_datetime) {
        this.end_datetime = end_datetime;
    }

    public String getLast_lat() {
        return last_lat;
    }

    public void setLast_lat(String last_lat) {
        this.last_lat = last_lat;
    }

    public String getLast_lng() {
        return last_lng;
    }

    public void setLast_lng(String last_lng) {
        this.last_lng = last_lng;
    }
}
