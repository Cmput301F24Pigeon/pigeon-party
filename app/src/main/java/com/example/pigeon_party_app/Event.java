package com.example.pigeon_party_app;

import java.util.Date;

public class Event {
    private String eventId;
    private String title;
    private Date dateTime;
    private int waitlistCapacity; // Optional, set to -1 if not applicable
    private String status;
    private String imageUrl;
    private String details;
    private Facility facility;
    private boolean requiresLocation;// later add event image
    private NotificationHelper notificationHelper; //add to constructors
    //for organizer events
    public Event(String eventId, String title, Date dateTime, int waitlistCapacity, String details, Facility facility, boolean requiresLocation) {
        this.eventId = eventId;
        this.title = title;
        this.dateTime = dateTime;
        this.waitlistCapacity = waitlistCapacity;
        this.status = null; // No status for organizer events
        this.imageUrl = null; // No image for organizer events
        this.details = details;
        this.facility = facility;
        this.requiresLocation = requiresLocation;
    }

    //for entrant events
    public Event(String title, Date dateTime, String status, String imageUrl, String details, Facility facility, boolean requiresLocation) {
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

    public String getImageUrl() {
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

    public boolean isRequiresLocation() {
        return requiresLocation;
    }

    public String getEventId() {
        return eventId;
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
