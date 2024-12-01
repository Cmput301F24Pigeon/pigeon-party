package com.example.pigeon_party_app;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to create and manage users
 */
public class User implements Serializable {
    private Facility facility;
    private String name;
    private String email;
    private String phoneNumber;             // optional
    private String uniqueId;
    private boolean entrant;
    private boolean organizer;
    private boolean notificationStatus;
    private String colour;
    private List<String> notifications;

    private ArrayList<String> entrantEventList;
    private ArrayList<String> organizerEventList;


    private boolean admin;
    private Bitmap profileImagePath;


    public User() {
        notifications = new ArrayList<>();
        organizerEventList = new ArrayList<>();
        entrantEventList = new ArrayList<>();

    }

    /**
     * Contructor method for the User class, creates a User object
     * @param name the user's name
     * @param email the user's email
     * @param phoneNumber the user's phone number
     * @param uniqueId the user's id, used to identify them in Firebase
     * @param isOrganizer a boolean representing if the user is an organizer
     * @param isEntrant a boolean representing if the user is an entrant
     * @param facility a facility object associated with the user
     * @param notificationStatus a boolean representing if the user has notifications turned on
     * @param colour a string representing the background colour for a user's avatar
     * @param entrantEventList a list of events that our user has joined
     * @param organizerEventList a list of events the user has made
     * @param isAdmin a boolean representing if the user is an admin
     */
    public User(String name, String email, String phoneNumber, String uniqueId, boolean isOrganizer, boolean isEntrant, Facility facility, boolean notificationStatus, String colour, ArrayList<String> entrantEventList, ArrayList<String> organizerEventList, boolean isAdmin) {

        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.uniqueId = uniqueId;
        this.organizer = isOrganizer;
        this.entrant = isEntrant;
        this.facility = facility;
        this.notificationStatus = notificationStatus;
        this.colour = colour;
        this.notifications = notifications != null ? notifications : new ArrayList<>();
        this.entrantEventList = entrantEventList != null ? entrantEventList : new ArrayList<>();
        this.organizerEventList = organizerEventList != null ? organizerEventList : new ArrayList<>();
        this.admin = isAdmin;
    }

    public User(String userId, Map<String, Object> userMap) {
    }

    /**
     * Getter for getting the user's name
     * @return name, the user's name as a String
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for setting the user's name
     * @param name the String representing the user's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for getting the user's email
     * @return email, the user's email as a String
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter for setting the user's email
     * @param email the String representing the user's email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Getter for getting the user's phone number
     * @return phoneNumber, the user's phone number as a String if provided, or an empty string
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Setter for setting the user's phoneNumber
     * @param phoneNumber the String representing the user's phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Getter for getting the user's Id
     * @return uniqueId, the user's Id as a String
     */
    public String getUniqueId() {
        return uniqueId;
    }

    /**
     * Setter for setting the user's Id
     * @param uniqueId the String representing the user's uniqueId
     */
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    /**
     * Getter for getting the user's entrant status
     * @return entrant, a boolean representing whether or not the user is an entrant
     */
    public boolean isEntrant() {
        return entrant;
    }

    /**
     * Setter for setting the user's entrant status
     * @param entrant a boolean representing if the user in an entrant
     */
    public void setEntrant(boolean entrant) {
        this.entrant = entrant;
    }

    /**
     * Getter for getting the user's organizer status
     * @return organizer, a boolean representing whether or not the user is an organizer
     */
    public boolean isOrganizer() {
        return organizer;
    }

    /**
     * Setter for setting the user's organizer status
     * @param Organizer a boolean representing if the user in an organizer
     */
    public void setOrganizer(boolean Organizer) {
        this.organizer = Organizer;
    }

    /**
     * Getter for getting the user's facility
     * @return facility, the facility associated with the user if they are an organizer or null if they are an entrant
     */
    public Facility getFacility() {
        return facility;
    }

    /**
     * Setter for setting the user's facility
     * @param facility a Facility object to be associated with the user
     */
    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    /**
     * Getter for getting the user's notification setting
     * @return notificationStatus, a boolean representing whether or not the user has notifications turned on
     */
    public boolean hasNotificationsOn() {
        return notificationStatus;
    }

    /**
     * Setter for setting the user's notification settings
     * @param notificationStatus a boolean representing if the user has notifications turned on
     */
    public void setNotificationsOn(boolean notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    /**
     * Getter for user's avatar background colour
     * @return colour a String representing the colour for the user's default avatar background
     */
    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }


    public List<String> getNotifications() {
        return notifications;
    }

    /**
     * adding notifications to the list of notifications
     * @param notification a string representing the notification
     */
    public void addNotification(String notification) {
        this.notifications.add(notification);
    }

    public void clearNotifications() {
        this.notifications.clear(); // Clear notifications if needed
    }

    public void setNotifications(List<String> notifications) {
        this.notifications = notifications;
    }

    public ArrayList<String> getEntrantEventList() {
        return entrantEventList;
    }

    public ArrayList<String> getOrganizerEventList() {
        return organizerEventList;
    }

    public void setOrganizerEventList(ArrayList<String> organizerEventList) {
        this.organizerEventList = organizerEventList;
    }

    public void setEntrantEventList(ArrayList<String> entrantEventList) {
        this.entrantEventList = entrantEventList;
    }

    public Bitmap getProfileImagePath(){
        return profileImagePath;
    }

    public void setProfileImagePath (Bitmap profileImagePath){
        this.profileImagePath = profileImagePath;
    }

    /**
     * adding the event to our entrant event list
     * @param event an event we are adding to our `list
     */
    public void addEntrantEventList (String event){
            this.entrantEventList.add(event);
        }

    /**
     * remove the event to our entrant event list
     * @param pos an event we are adding to our `list
     */
    public void removeEntrantEventList(int pos){
        this.entrantEventList.remove(pos);
    }

    /**
     * adding the event to our organizer event list
     * @param event an event we are adding to our `list
     */
    public void addOrganizerEventList(String event){
        this.organizerEventList.add(event);
    }

    /**
     * remove the event to our organizer event list
     * @param pos the int value of an event we are adding to our `list
     */
    public void removeOrganizerEventList(int pos){
        this.organizerEventList.remove(pos);
    }

    /**
     * Getter for user's admin status either true or false
     * @return admin status as a boolean of true or false
     */
    public boolean isAdmin() {
        return admin;
    }

    /**
     * Converts User object to a Map for Firebase
     *
     * @return userMap
     */
    public Map<String, Object> toMap() {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", this.name);
        userMap.put("email", this.email);
        userMap.put("phoneNumber", this.phoneNumber);
        userMap.put("uniqueId", this.uniqueId);
        userMap.put("entrant", this.entrant);
        userMap.put("organizer", this.organizer);
        userMap.put("notificationStatus", this.notificationStatus);
        userMap.put("notifications", this.notifications);
        userMap.put("colour", this.colour);


        userMap.put("entrantEventList", this.entrantEventList);

        userMap.put("organizerEventList", this.organizerEventList);

        // Handle facility serialization if needed
        if (facility != null) {
            userMap.put("facility", facility.toMap()); // Assuming Facility has a similar toMap() method
        }

        return userMap;
    }



    /**
     * Helper method to convert a list of events to a Map (to store in Firestore).
     */
    private List<Map<String, Object>> convertEventListToMap(List<Event> eventList) {
        List<Map<String, Object>> eventMapList = new ArrayList<>();
        for (Event event : eventList) {
            eventMapList.add(event.toMap());
        }
        return eventMapList;
    }

}
