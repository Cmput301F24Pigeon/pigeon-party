package com.example.pigeon_party_app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
     * @param entrantEventList
     * @param organizerEventList
     */
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


    public List<String> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<String> notifications) {
        this.notifications = notifications;
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


    public ArrayList<Event> getEntrantEventList() {
        return entrantEventList;
    }

    public ArrayList<Event> getOrganizerEventList() {
        return organizerEventList;
    }

    public void setOrganizerEventList(ArrayList<Event> organizerEventList) {
        this.organizerEventList = organizerEventList;
    }

    /**
     * adding and event to our entrant event list
     * @param event an event we are adding to our `list
     */
    public void addEntrantEventList(Event event){
        this.entrantEventList.add(event);
    }
    /**
     * adding and event to our organizer event list
     * @param event an event we are adding to our `list
     */
    public void addOrganizerEventList(Event event){
        this.organizerEventList.add(event);
    }
}

