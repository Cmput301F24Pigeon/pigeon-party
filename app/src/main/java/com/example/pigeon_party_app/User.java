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
    //private boolean isAdmin;
    // need to make an images for our profile

    public User(){

    }

    public User(String name, String email, String phoneNumber, boolean isOrganizer, boolean isEntrant, Facility facility) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isOrganizer = isOrganizer;
        this.isEntrant = isEntrant;
        this.facility = facility;
    }




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    }

    public boolean isEntrant() {
        return isEntrant;
    }

    public void setEntrant(boolean entrant) {
        isEntrant = entrant;
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

    public Facility getFacility() {
        return facility;
    }
}
