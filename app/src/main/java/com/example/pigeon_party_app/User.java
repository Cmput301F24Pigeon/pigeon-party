package com.example.pigeon_party_app;

public class User {
    //first testing with just name
    private String name;
    private String email;
    private String phoneNumber;   // optional
    private String role;
//    private boolean entrant;
//    private boolean organizer;
//    private boolean admin;
    //TODO: make images for our profile

    public User(String name, String email, String phoneNumber, String role) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;

        // Could put (if role == 'entrant') {this.entrant = true;} etc. for bool roles
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

//    public boolean isEntrant() {
//        return entrant;
//    }
//
//    public boolean isOrganizer() {
//        return organizer;
//    }
//
//    public boolean isAdmin() {
//        return admin;
//    }

}
