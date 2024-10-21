package com.example.pigeon_party_app;

import java.io.Serializable;

public class User implements Serializable {
    //first testing with just name
    private String name;
    private String email;
    private String phoneNumber;             // optional
    private boolean isEntrant;
    private boolean isOrganizer;
    private boolean isAdmin;
    private String facilityAddress;
    private String facilityName;
    // need to make an images for our profile


    public String getName() {

        return name;
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

    public boolean isAdmin() {
        return isAdmin;
    }

}
