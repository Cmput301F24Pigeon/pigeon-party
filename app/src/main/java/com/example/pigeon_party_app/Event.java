package com.example.pigeon_party_app;


import android.util.Log;
import android.widget.ImageView;


import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to create events
 */
public class Event implements Serializable {
    private NotificationHelper notificationHelper;
    private String eventId;
    private String title;
    private Date dateTime;
    private int waitlistCapacity; // Optional, set to -1 if not applicable
    private String status;
    private String imageUrl;
    private String details;
    private Facility facility; // facility object
    private User organizer;
    private boolean requiresLocation;// later add event image
    private Map<String, User> usersWaitlist = new HashMap<>();
    private Map<String, User> usersInvited = new HashMap<>();
    private Map<String, User> usersCancelled = new HashMap<>();
    private Map<String, User> usersSentInvite = new HashMap<>();

    public Event(){

    }

    public Event(String eventId, String title, Date dateTime, int waitlistCapacity, String
            details, Facility facility, boolean requiresLocation, Map<
            String, User> usersWaitlist, Map<String, User> usersInvited, Map<String, User> usersCancelled, User
                         organizer) {

        this.eventId = eventId;
        this.title = title;
        this.dateTime = dateTime;
        this.waitlistCapacity = waitlistCapacity;
        this.imageUrl = null;
        this.details = details;
        this.facility = facility;
        this.requiresLocation = requiresLocation;
        this.usersWaitlist = usersWaitlist;
        this.usersInvited = usersInvited;
        this.usersCancelled = usersCancelled;
        this.organizer = organizer;
    }
    public Event(String eventId, String title, Date dateTime, int waitlistCapacity, String imageUrl,String
            details, Facility facility, boolean requiresLocation, Map<
            String, User> usersWaitlist, Map<String, User> usersInvited, Map<String, User> usersCancelled, Map<
            String, User> usersSentInvite, User
                         organizer) {

        this.eventId = eventId;
        this.title = title;
        this.dateTime = dateTime;
        this.waitlistCapacity = waitlistCapacity;
        this.imageUrl = imageUrl;
        this.details = details;
        this.facility = facility;
        this.requiresLocation = requiresLocation;
        this.usersWaitlist = usersWaitlist;
        this.usersInvited = usersInvited;
        this.usersCancelled = usersCancelled;
        this.usersSentInvite = usersSentInvite;
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
        return facility;
    }

    public User getOrganizer() { return organizer; }

    public boolean isRequiresLocation() {
        return requiresLocation;
    }

    public String getEventId() {
        return eventId;
    }

    public void setDetails(String details){
        this.details = details;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public void setWaitlistCapacity(int waitlistCapacity)
    {
        this.waitlistCapacity = waitlistCapacity;
    }

    public void setRequiresLocation(boolean requiresLocation) {
        this.requiresLocation = requiresLocation;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setFacility(Facility facility){
        this.facility = facility;
    }

    public void setUsersWaitlisted(Map <String, User> usersWaitlist) {
        this.usersWaitlist = usersWaitlist;
    }

    public void setUsersCancelled(Map <String, User> usersCancelled) {
        this.usersCancelled = usersCancelled;
    }

    public void setUsersInvited(Map <String, User> usersInvited) {
        this.usersInvited = usersInvited;
    }

    public void removeUserFromWaitlist(User user) {
        if(usersWaitlist.get(user.getUniqueId()) != null){
            usersWaitlist.remove(user.getUniqueId());
        }
    }

    public void removeUserFromCancelledList(User user) {
        if(usersCancelled.get(user.getUniqueId()) != null){
            usersCancelled.remove(user.getUniqueId());
        }
    }
    public void removeUserFromInvited(User user) {
        if(usersInvited.get(user.getUniqueId()) != null){
            usersInvited.remove(user.getUniqueId());
        }
    }
    public void removeUserFromSentInvite(User user) {
        if(usersSentInvite.get(user.getUniqueId()) != null){
            usersSentInvite.remove(user.getUniqueId());
        }
    }

    /**
     * Adds a user to the usersWaitList map.
     *
     * @param user
     */
    public void addUserToWaitlist(User user) {
        usersWaitlist.put(user.getUniqueId(), user);
    }

    /**
     * Adds a user to the usersInvited map.
     *
     * @param user
     */
    public void addUserToInvited(User user) {
        usersInvited.put(user.getUniqueId(), user);
    }

    /**
     * Adds a user to the usersCancelled map.
     *
     * @param user
     */
    public void addUserToCancelled(User user) {
        usersCancelled.put(user.getUniqueId(), user);
    }
    /**
     * Adds a user to the usersSentInvite map.
     *
     * @param user
     */
    public void addUserToSentInvite(User user) {
        usersSentInvite.put(user.getUniqueId(), user);
    }


    /**
     * Retrieves the list of users who are currently on the waitlist for the event.
     *
     * @return A map where each key is a user's unique ID and each value is a map of user details.
     */
    public Map<String, User> getUsersWaitlisted() {
        return usersWaitlist != null ? usersWaitlist : new HashMap<>();

    }

    /**
     * Retrieves the list of users who have been invited to the event.*
     *
     * @return A map where each key is a user's unique ID and each value is a map of user details.
     */
    public Map<String, User> getUsersInvited() {
        return usersInvited != null ? usersInvited : new HashMap<>();
    }

    /**
     * Retrieves the list of users who have cancelled their participation in the event.
     *
     * @return A map where each key is a user's unique ID and each value is a map of user details.
     */
    public Map<String, User> getUsersCancelled() {
        return usersCancelled != null ? usersCancelled : new HashMap<>();
    }

    /**
     * Retrieves the list of users who have cancelled their participation in the event.
     *
     * @return A map where each key is a user's unique ID and each value is a map of user details.
     */
    public Map<String, User> getUsersSentInvite() {
        return usersSentInvite != null ? usersSentInvite : new HashMap<>();
    }

//    /**
//     * Allows for a user name and status to be parsed into a hash map ready for firestore storage.
//     *
//     * @param user
//     * @param status
//     * @return
//     */
//    public Map<String, Object> createUserDetails(User user, String status) {
//            Map<String, Object> userDetails = new HashMap<>();
//            userDetails.put("name", user.getName());
//            userDetails.put("status", status);
//            // Add more user-specific fields as needed
//            return userDetails;
//        }

    /**
     * Creates the hash map needed to update the cancelled list in firebase
      * @param event
     * @return
     */
    public Map<String, Object> updateFirebaseEventCancelledList(Event event) {
        Map<String, Object> updates = new HashMap<>();
        Map<String, Map<String, Object>> serializedUsersCancelled = new HashMap<>();

        for (Map.Entry<String, User> entry : event.getUsersCancelled().entrySet()) {
            serializedUsersCancelled.put(entry.getKey(), entry.getValue().toMap()); // Convert User to map
        }
        updates.put("usersCancelled", serializedUsersCancelled);
        return updates;
    }
    public Map<String, Object> updateFirebaseEventSentInvited(Event event) {
        Map<String, Object> updates = new HashMap<>();
        Map<String, Map<String, Object>> serializedUsersSentInvite = new HashMap<>();

        for (Map.Entry<String, User> entry : event.getUsersCancelled().entrySet()) {
            serializedUsersSentInvite.put(entry.getKey(), entry.getValue().toMap()); // Convert User to map
        }
        updates.put("usersSentInvite", serializedUsersSentInvite);
        return updates;
    }

    /**
     * Creates the hash map needed to update the waitlist in firebase
     *
     * @param event
     * @return
     */
    public Map<String, Object> updateFirebaseEventWaitlist(Event event) {
        Map<String, Object> updates = new HashMap<>();
        Map<String, Map<String, Object>> serializedUsersWaitlisted = new HashMap<>();

        for (Map.Entry<String, User> entry : event.getUsersWaitlisted().entrySet()) {
            serializedUsersWaitlisted.put(entry.getKey(), entry.getValue().toMap()); // Convert User to map
        }
        updates.put("usersWaitlisted", serializedUsersWaitlisted);
        return updates;
    }

    /**
     * Creates the hash map needed to update the invited list in firebase
     * @param event
     * @return
     */
    public Map<String, Object> updateFirebaseEventInvitedList(Event event) {
        Map<String, Object> updates = new HashMap<>();
        Map<String, Map<String, Object>> serializedUsersInvited = new HashMap<>();

        for (Map.Entry<String, User> entry : event.getUsersInvited().entrySet()) {
            serializedUsersInvited.put(entry.getKey(), entry.getValue().toMap()); // Convert User to map
        }
        updates.put("usersInvited", serializedUsersInvited);
        return updates;
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
                User selectedUser = usersInvited.get(userId);

            // Notify the user if they've been chosen
            if (selectedUser != null && selectedUser.hasNotificationsOn()) {
                notificationHelper.notifyUserIfChosen(selectedUser, this);
            }
        }
    }

    /**
     * This method uses the addNotificationToUser method to add a notification message to the users notification list
     * @param status The status of the user in the event list
     * @param db The firestore database
     */
    public void notifyUserByStatus(FirebaseFirestore db, String status) {
        String message;

        switch (status) {
            case "accepted":
                message = "Congratulations! You have joined the event: " + title;
                break;
            case "waitlisted":
                message = "You are currently on the waitlist for the event: " + title;
                break;
            case "cancelled":
                message = "Sorry, you have not been selected for the event: " + title;
                break;
            case "invited":
                message = "Congratulations! You have been invited to the event: " + title;
                break;
            default:
                return;
        }
        db.collection("events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, User> usersToNotify = null;
                        if ("waitlisted".equals(status)) {
                            usersToNotify = (Map<String, User>) documentSnapshot.get("usersWaitlisted");
                        } else if ("accepted".equals(status)) {
                            usersToNotify = (Map<String, User>) documentSnapshot.get("usersInvited");
                        } else if ("cancelled".equals(status)) {
                            usersToNotify = (Map<String, User>) documentSnapshot.get("usersCancelled");
                        } else if ("invited".equals(status)){
                            usersToNotify = (Map<String,User>) documentSnapshot.get("usersSentInvite");
                        }

                        if (usersToNotify != null) {
                            for (Map.Entry<String,User> entry : usersToNotify.entrySet()) {
                                String userId = entry.getKey();
                                if (userId!= null) {

                                    addNotificationToUser(db, userId, message); // Add notification to user
                                }
                            }
                        }
                        }

                })
                .addOnFailureListener(e -> {
                    Log.w("Firebase", "Error getting event data", e);
                });
    }

    /**
     * This is a method that adds a notification to the user document in firebase
     * @param db
     * @param userId
     * @param message
     */
    public void addNotificationToUser(FirebaseFirestore db, String userId, String message) {
        DocumentReference userRef = db.collection("user").document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    userRef.update("notifications", FieldValue.arrayUnion(message))
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Firebase", "Notification added to user: " + userId);
                            })
                            .addOnFailureListener(e -> {
                                Log.w("Firebase", "Error adding notification to user: " + userId, e);
                            });
                } else {
                    Log.w("Firebase", "User document does not exist: " + userId);
                }
            } else {
                Log.w("Firebase", "Error fetching user document: ", task.getException());
            }
        });
    }

    /**
     * Converts Event instance to a Map for Firestore.
     *
     * @return a Map<String, Object> representation of the Event.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("eventId", eventId);
        eventMap.put("title", title);
        eventMap.put("dateTime", dateTime);
        eventMap.put("waitlistCapacity", waitlistCapacity);
        eventMap.put("status", status);
        eventMap.put("details", details);
        eventMap.put("facility", facility != null ? facility.toMap() : null);  // Use facility's toMap method
        eventMap.put("requiresLocation", requiresLocation);

        // Converting nested User maps to Firestore-compatible Maps
        eventMap.put("usersWaitlist", convertUserMap(usersWaitlist));
        eventMap.put("usersInvited", convertUserMap(usersInvited));
        eventMap.put("usersCancelled", convertUserMap(usersCancelled));

        return eventMap;
    }

    /**
     * Helper method to convert Map<String, User> to Map<String, Map<String, Object>> for Firestore.
     */
    private Map<String, Map<String, Object>> convertUserMap(Map<String, User> userMap) {
        Map<String, Map<String, Object>> convertedMap = new HashMap<>();
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            convertedMap.put(entry.getKey(), entry.getValue().toMap());
        }
        return convertedMap;
    }


}





