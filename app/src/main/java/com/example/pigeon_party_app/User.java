package com.example.pigeon_party_app;

import java.io.Serializable;

public class User implements Serializable {
    //first testing with just name
    private Facility facility;
    private String name;
    private String email;
    private String phoneNumber;             // optional
    private boolean isEntrant;
    private boolean isOrganizer;
    private String id;
    //private boolean isAdmin;
    // need to make an images for our profile


    //default user object doesnt need facility information
    public User(String name, String email) {
        this(name, email, null, false, true);
    }

    public User(String name, String email, String phoneNumber, boolean isOrganizer, boolean isEntrant) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isOrganizer = isOrganizer;
        this.isEntrant = isEntrant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    public boolean isEntrant() {
        return isEntrant;
    }

    public boolean isOrganizer() {
        return isOrganizer;
    }

    public void setOrganizer(boolean organizer) {
        isOrganizer = organizer;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

}
