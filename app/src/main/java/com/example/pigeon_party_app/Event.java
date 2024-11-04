package com.example.pigeon_party_app;


import android.widget.ImageView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.widget.ImageView;

import com.google.type.DateTime;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Event implements Serializable {
    private NotificationHelper notificationHelper;
    private String eventId;
    private String title;
    private Date dateTime;
    private int waitlistCapacity; // Optional, set to -1 if not applicable
    private String status;
    private ImageView imageUrl;
    private String details;
    private Facility facility; // facility object
    private User organizer;
    private boolean requiresLocation;// later add event image
    private Map<String, Map<String, Object>> usersWaitlist = new HashMap<>();
    private Map<String, Map<String, Object>> usersInvited = new HashMap<>();
    private Map<String, Map<String, Object>> usersCancelled = new HashMap<>();

    public Event(String eventId, String title, Date dateTime, int waitlistCapacity, String
            details, Facility facility, boolean requiresLocation, Map<
            String, Map<String, Object>> usersWaitlist, Map<String, Map<String, Object>> usersInvited, Map<String, Map<String, Object>> usersCancelled, User
                         organizer) {

        this.eventId = eventId;
        this.title = title;
        this.dateTime = dateTime;
        this.waitlistCapacity = waitlistCapacity;
        this.imageUrl = null;
        //this.imageUrl = imageUrl;
        this.details = details;
        this.facility = facility;
        this.requiresLocation = requiresLocation;
        this.usersWaitlist = usersWaitlist;
        this.usersInvited = usersInvited;
        this.usersCancelled = usersCancelled;
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

    public ImageView getImageUrl() {
        return imageUrl;
    }

    public String getDetails() {
        return details;
    }

    public Facility getFacility() {
        return facility;
    }

    public boolean isRequiresLocation() {
        return requiresLocation;
    }



    public String getEventId() {
        return eventId;
    }

    /**
     * Adds a user to the usersWaitList map.
     *
     * @param user
     */
    public void addUserToWaitlist(User user) {
        usersWaitlist.put(user.getUniqueId(), createUserDetails(user, "Waitlisted"));
    }


    /**
     * Adds a user to the usersInvited map.
     *
     * @param user
     */
    public void addUserToInvited(User user) {
        usersInvited.put(user.getUniqueId(), createUserDetails(user, "Invited"));
    }

    /**
     * Adds a user to the usersCancelled map.
     *
     * @param user
     */
    public void addUserToCancelled(User user) {
        usersCancelled.put(user.getUniqueId(), createUserDetails(user, "Cancelled"));
    }


    /**
     * Retrieves the list of users who are currently on the waitlist for the event.
     *
     * @return A map where each key is a user's unique ID and each value is a map of user details.
     */
    public Map<String, Map<String, Object>> getUsersWaitlisted() {
        return usersWaitlist;
    }

    /**
     * Retrieves the list of users who have been invited to the event.*
     *
     * @return A map where each key is a user's unique ID and each value is a map of user details.
     */
    public Map<String, Map<String, Object>> getUsersInvited() {
        return usersInvited;
    }

    /**
     * Retrieves the list of users who have cancelled their participation in the event.
     *
     * @return A map where each key is a user's unique ID and each value is a map of user details.
     */
    public Map<String, Map<String, Object>> getUsersCancelled() {
        return usersCancelled;
    }

    /**
     * Allows for a user name and status to be parsed into a hash map ready for firestore storage.
     *
     * @param user
     * @param status
     * @return
     */
    public Map<String, Object> createUserDetails(User user, String status) {
            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("name", user.getName());
            userDetails.put("status", status);
            // Add more user-specific fields as needed
            return userDetails;
        }


        /**
         * Samples/Draws a specific number of users among the waitlist to be invited to an event.
         * @param drawAmount
         */
        public void runLottery (int drawAmount){
            // If the amount to draw is more than the waitlist size, match the draw amount with
            // the amount of people in the current waitlist.
            if (usersWaitlist.size() < drawAmount) {
                drawAmount = usersWaitlist.size();
            }

            // Creates a list of user IDs (from waitlist)
            List<String> waitlistUserIds = new ArrayList<>(usersWaitlist.keySet());

            // Shuffles and picks a sample of users
            // https://www.geeksforgeeks.org/collections-shuffle-method-in-java-with-examples/
            Collections.shuffle(waitlistUserIds);
            List<String> selectedUsers = waitlistUserIds.subList(0, drawAmount);

            // Moves users from waitlist to the invited/joined list
            for (String userId : selectedUsers) {
                usersInvited.put(userId, usersWaitlist.get(userId));
                usersWaitlist.remove(userId);

                // Notify them afterwards...
                Map<String, Object> userData = usersInvited.get(userId); // Adjust based on how your data is structured
                User selectedUser = (User) userData.get("user"); // Cast to User type

                // Notify the user if they've been chosen
                if (selectedUser != null && selectedUser.hasNotificationsOn()) {
                    notificationHelper.notifyUserIfChosen(selectedUser, this);
                }
            }
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





