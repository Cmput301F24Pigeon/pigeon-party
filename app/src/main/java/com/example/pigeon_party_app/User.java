package com.example.pigeon_party_app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to create and manage users
 */
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
    private List<String> notifications;
    private ArrayList<Event> entrantEventList;
    private ArrayList<Event> organizerEventList;

    //private boolean isAdmin;
    // need to make an images for our profile

    public User() {
        notifications = new ArrayList<>();
        organizerEventList = new ArrayList<>();
        entrantEventList = new ArrayList<>();

    }

    public User(String name, String email, String phoneNumber, String uniqueId, boolean isOrganizer, boolean isEntrant, Facility facility, boolean notificationStatus, ArrayList<Event> entrantEventList, ArrayList<Event> organizerEventList) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.uniqueId = uniqueId;
        this.organizer = isOrganizer;
        this.entrant = isEntrant;
        this.facility = facility;
        this.notificationStatus = notificationStatus;
        this.notifications = notifications != null ? notifications : new ArrayList<>();
        this.entrantEventList = entrantEventList != null ? entrantEventList : new ArrayList<>();
        this.organizerEventList = organizerEventList != null ? organizerEventList : new ArrayList<>();
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

    public List<String> getNotifications() {
        return notifications;
    }

    public void addNotification(String notification) {
        this.notifications.add(notification);
    }

    public void clearNotifications() {
        this.notifications.clear(); // Clear notifications if needed
    }

    public void setNotifications(List<String> notifications) {
        this.notifications = notifications;
    }

    public ArrayList<Event> getEntrantEventList() {
        return entrantEventList;
    }

    public ArrayList<Event> getOrganizerEventList() {
        return organizerEventList;
    }

    public void addEntrantEventList(Event event){
        this.entrantEventList.add(event);
    }

    public void addOrganizerEventList(Event event){
        this.organizerEventList.add(event);
    }

    public void setOrganizerEventList(ArrayList<Event> organizerEventList) {
        this.organizerEventList = organizerEventList;
    }

    /**
     * Converts User Object to Map for Firebase
     * @return userMap
     */
    public Map<String, Object> toMap() {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", this.name);
        userMap.put("email", this.email);
        // Add other fields as needed
        return userMap;
    }
}

