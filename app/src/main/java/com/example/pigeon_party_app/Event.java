package com.example.pigeon_party_app;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Event {
    private String eventId;
    private String title;
    private Date dateTime;
    private int waitlistCapacity; // Optional, set to -1 if not applicable
    private String status;
    private String imageUrl;
    private String details;
    private Facility location; // facility object
    private User organizer;
    private boolean requiresLocation;// later add event image
    private Map<String, Map<String, Object>> usersWaitlist = new HashMap<>();
    private Map<String, Map<String, Object>> usersInvited = new HashMap<>();
    private Map<String, Map<String, Object>> usersCancelled = new HashMap<>();

    // Constructor for organizer events
    public Event(String eventId, String title, Date dateTime, int waitlistCapacity, String details, Facility location, boolean requiresLocation, User organizer) {
        this.eventId = eventId;
        this.title = title;
        this.dateTime = dateTime;
        this.waitlistCapacity = waitlistCapacity;
        this.status = null;
        this.imageUrl = null;
        this.details = details;
        this.location = location;
        this.requiresLocation = requiresLocation;
        this.organizer = organizer;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDetails() {
        return details;
    }

    public Facility getFacility() {
        return location;
    }

    public boolean requiresLocation() {
        return requiresLocation;
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

    private Map<String, Object> createUserDetails(User user, String status) {
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("name", user.getName());
        userDetails.put("status", status);
        // Add more user-specific fields as needed
        return userDetails;
    }
}
