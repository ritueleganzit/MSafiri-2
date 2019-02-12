package com.eleganz.msafiri.model;

/**
 * Created by eleganz on 12/2/19.
 */

public class FavouritesData {
    String trip_id,from_title,to_title;


    public FavouritesData(String trip_id, String from_title, String to_title) {
        this.trip_id = trip_id;
        this.from_title = from_title;
        this.to_title = to_title;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getFrom_title() {
        return from_title;
    }

    public void setFrom_title(String from_title) {
        this.from_title = from_title;
    }

    public String getTo_title() {
        return to_title;
    }

    public void setTo_title(String to_title) {
        this.to_title = to_title;
    }
}
