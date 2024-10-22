package com.example.pigeon_party_app;

import java.io.Serializable;

public class User implements Serializable {
    //first testing with just name
    private String name;
    private String email;
    private String phoneNumber;             // optional
    private boolean isEntrant;
    private boolean isOrganizer;
    private String id;
    //private boolean isAdmin;
    private String facilityAddress;
    private String facilityName;
    // need to make an images for our profile


    //default user object doesnt need facility information
    public User(String name, String email) {
        this(name, email, null, false, true, null, null);
    }
    public User(String name, String email, String phoneNumber, boolean isOrganizer, boolean isEntrant, String facilityName, String facilityAddress) {
        this.facilityName = facilityName;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isOrganizer = isOrganizer;
        this.isEntrant = isEntrant;
        this.facilityAddress = facilityAddress;
    }

    public String getName() {

        return name;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isEntrant() {
        return isEntrant;
    }

    public boolean isOrganizer() {
        return isOrganizer;
    }

    public void setOrganizer(boolean organizer) {
        isOrganizer = organizer;
    }

    public void setFacilityAddress(String facilityAddress) {
        this.facilityAddress = facilityAddress;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

}
