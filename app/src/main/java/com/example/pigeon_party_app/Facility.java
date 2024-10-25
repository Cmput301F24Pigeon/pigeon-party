package com.example.pigeon_party_app;

import android.widget.ImageView;

public class Facility {
    private String address;
    private String name;
    private User owner;
    //private ImageView facilityPoster;

    public Facility(User owner, String address, String name) {
        this.address = address;
        this.name = name;
        //this.facilityPoster = facilityPoster;
        this.owner = owner;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public User getOwner() {
        return owner;
    }
}
