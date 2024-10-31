package com.example.pigeon_party_app;

import java.io.Serializable;

public class User implements Serializable {
    //first testing with just name
    private Facility facility;
    private String name;
    private String email;
    private String phoneNumber;             // optional
    private String uniqueId;
    private boolean entrant;
    private boolean organizer;
    private boolean notificationStatus;
    //private boolean isAdmin;
    // need to make an images for our profile

    public User(){

    }

    public User(String name, String email, String phoneNumber, String uniqueId, boolean isOrganizer, boolean isEntrant, Facility facility, boolean notificationStatus) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.uniqueId = uniqueId;
        this.organizer = isOrganizer;
        this.entrant = isEntrant;
        this.facility = facility;
        this.notificationStatus = notificationStatus;
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

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public boolean isEntrant() {
        return entrant;
    }

    public void setEntrant(boolean Entrant) {
        entrant = Entrant;
    }

    public boolean isOrganizer() {
        return organizer;
    }

    public void setOrganizer(boolean Organizer) {
        organizer = Organizer;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setNotificationsOn(boolean notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public boolean hasNotificationsOn() {
        return notificationStatus;
    }
}