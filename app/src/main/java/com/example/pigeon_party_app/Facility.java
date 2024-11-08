package com.example.pigeon_party_app;

import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

/**
 * The facility class represents the facility which an organizer hosts their events
 */
public class Facility {
    private String address;
    private String name;
    private String ownerId;
    //private ImageView facilityPoster;

    public Facility(){

    }

    /**
     * Constructor method for creating new facilities
     * @param ownerId
     * @param address
     * @param name
     */
    public Facility( String ownerId, String address, String name) {
        this.address = address;
        this.name = name;
        //this.facilityPoster = facilityPoster;
        this.ownerId = ownerId;
    }

    /**
     * setter to set the name of the facility
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * setter to set the address of the facility
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * getter to get the address of the facility
     * @return address The address of the facility
     */
    public String getAddress() {
        return address;
    }

    /**
     * getter for getting the name of the facility
     * @return name The name of the facility
     */
    public String getName() {
        return name;
    }

    /**
     * getter for getting the user who owns the facility
     * @return ownerId The uniqueId of the user which created the facility
     */
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * Converts Facility instance to a Map for Firestore.
     *
     * @return a Map<String, Object> representation of the Facility.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> facilityMap = new HashMap<>();
        facilityMap.put("address", address);
        facilityMap.put("name", name);
        facilityMap.put("ownerId", ownerId);
        return facilityMap;
    }
}
