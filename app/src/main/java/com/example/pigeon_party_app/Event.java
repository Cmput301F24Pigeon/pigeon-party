package com.example.pigeon_party_app;

import com.google.type.DateTime;

import java.time.LocalDateTime;
import java.util.Date;

public class Event {
    private String eventId;
    private String title;
    private Date dateTime;
    private int waitlistCapacity; // Optional, set to -1 if not applicable
    private String status;
    private String imageUrl;
    private String details;
    private String location;
    private boolean requiresLocation;// later add event image

    //for organizer events
    public Event(String eventId, String title, Date dateTime, int waitlistCapacity, String details, String location, boolean requiresLocation) {
        this.eventId = eventId;
        this.title = title;
        this.dateTime = dateTime;
        this.waitlistCapacity = waitlistCapacity;
        this.status = null; // No status for organizer events
        this.imageUrl = null; // No image for organizer events
        this.details = details;
        this.location = location;
        this.requiresLocation = requiresLocation;
    }

    //for entrant events
    public Event(String title, Date dateTime, String status, String imageUrl, String details, String location, boolean requiresLocation) {
        this.title = title;
        this.dateTime = dateTime;
        this.status = status;
        this.imageUrl = imageUrl;
        this.details = details;
        this.location = location;
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

    public String getLocation() {
        return location;
    }

    public boolean requiresLocation() {
        return requiresLocation;
    }
}