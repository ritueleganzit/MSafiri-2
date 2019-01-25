package com.eleganz.msafiri.model;

/**
 * Created by eleganz on 15/11/18.
 */

public class Home {
    String title,address,id;


    public Home(String title, String address, String id) {
        this.title = title;
        this.address = address;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
