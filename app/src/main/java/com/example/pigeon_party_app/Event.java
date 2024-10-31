package com.example.pigeon_party_app;

import android.widget.ImageView;

import com.google.type.DateTime;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Event implements Serializable {
    private String eventId;
    private String title;
    private Date dateTime;
    private int waitlistCapacity; // Optional, set to -1 if not applicable
    private String status;
    private ImageView imageUrl;
    private String details;
    private Facility facility;
    private boolean requiresLocation;// later add event image
    private Map<String, Map<String, Object>> usersWaitlist = new HashMap<>();
    private Map<String, Map<String, Object>> usersInvited = new HashMap<>();
    private Map<String, Map<String, Object>> usersCancelled = new HashMap<>();
    private User organizer;

    private NotificationHelper notificationHelper; //add to constructors
    //for organizer events

    public Event(){

    }

    public Event(String eventId, String title, Date dateTime, int waitlistCapacity, String details, Facility facility, boolean requiresLocation, Map<String, Map<String, Object>> usersWaitlist, Map<String, Map<String, Object>> usersInvited, Map<String, Map<String, Object>> usersCancelled, User organizer) {
        this.eventId = eventId;
        this.title = title;
        this.dateTime = dateTime;
        this.waitlistCapacity = waitlistCapacity;
        //this.imageUrl = imageUrl;
        this.details = details;
        this.facility = facility;
        this.requiresLocation = requiresLocation;
        this.usersWaitlist = usersWaitlist;
        this.usersInvited = usersInvited;
        this.usersCancelled = usersCancelled;
        this.organizer = organizer;
    }

    //for entrant events
    public Event(String title, Date dateTime, String status, String details,ImageView imageUrl, Facility facility, boolean requiresLocation) {
        this.title = title;
        this.dateTime = dateTime;
        this.status = status;
        this.imageUrl = imageUrl;
        this.details = details;
        this.facility = facility;
        this.requiresLocation = requiresLocation;
        this.waitlistCapacity = -1; // No capacity for entrant events
    }

    public String getTitle() {
        return title;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public int getWaitlistCapacity() {
        return waitlistCapacity;
    }

    public String getStatus() {
        return status;
    }

    public ImageView getImageUrl() {
        return imageUrl;
    }

    public String getDetails() {
        return details;
    }

    public Facility getFacility() {
        return facility;
    }

    public boolean requiresLocation() {
        return requiresLocation;
    }

    public String getEventId() {
        return eventId;
    }

    // Add user to waitlist, invited, or cancelled list
    public void addUserToWaitlist(User user) {
        usersWaitlist.put(user.getUniqueId(), createUserDetails(user, "Waitlisted"));
    }

    public void addUserToInvited(User user) {
        usersInvited.put(user.getUniqueId(), createUserDetails(user, "Invited"));
    }

    public void addUserToCancelled(User user) {
        usersCancelled.put(user.getUniqueId(), createUserDetails(user, "Cancelled"));
    }

    public Map<String, Object> createUserDetails(User user, String status) {
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("name", user.getName());
        userDetails.put("status", status);
        // Add more user-specific fields as needed
        return userDetails;
    }

    public boolean isRequiresLocation() {
        return requiresLocation;
    }

    /*
    public void notifyUserByStatus(String status){ //need to make separate one for each status
        for --iterate through event hash map of users
            String message
            if (status.equals("selected"){
                message = "Congratulations! You have been selected for the event: " + title;
            } else if (status.equals("waitlisted")) {
                message = "You are on the waitlist for the event: " + title;
            } else if (status.equals("cancelled")) {
                message = "Sorry, you have not been selected for the event: " + title;
               }
    }
    */
}
