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

    // Empty constructor required for firebase
    public Event() {

    }

    /**
     * Constructor for an event.
     *
     * @param eventId
     * @param title
     * @param dateTime
     * @param waitlistCapacity
     * @param details
     * @param facility
     * @param requiresLocation
     * @param usersWaitlist
     * @param usersInvited
     * @param usersCancelled
     * @param organizer
     */
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

    /**
     * Constructor for an event including the sentInvite map
     *
     * @param eventId
     * @param title
     * @param dateTime
     * @param waitlistCapacity
     * @param imageUrl
     * @param details
     * @param facility
     * @param requiresLocation
     * @param usersWaitlist
     * @param usersInvited
     * @param usersCancelled
     * @param usersSentInvite
     * @param organizer
     */
    public Event(String eventId, String title, Date dateTime, int waitlistCapacity, String imageUrl, String
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

    /**
     * Retrieves the title of the event.
     *
     * @return the event title as a String.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Retrieves the date and time of the event.
     *
     * @return the event's date and time as a Date object.
     */
    public Date getDateTime() {
        return dateTime;
    }

    /**
     * Retrieves the capacity of the waitlist for the event.
     *
     * @return the waitlist capacity as an integer.
     */
    public int getWaitlistCapacity() {
        return waitlistCapacity;
    }

    /**
     * Retrieves the status of the event.
     *
     * @return the event status as a String.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Retrieves the URL of the event image.
     *
     * @return the image URL as a String.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Retrieves the details of the event.
     *
     * @return the event details as a String.
     */
    public String getDetails() {
        return details;
    }

    /**
     * Retrieves the facility associated with the event.
     *
     * @return the facility as a Facility object.
     */
    public Facility getFacility() {
        return facility;
    }

    /**
     * Retrieves the organizer of the event.
     *
     * @return the organizer as a User object.
     */
    public User getOrganizer() {
        return organizer;
    }

    /**
     * Checks if the event requires location verification.
     *
     * @return true if the event requires location verification, false otherwise.
     */
    public boolean isRequiresLocation() {
        return requiresLocation;
    }

    /**
     * Retrieves the unique ID of the event.
     *
     * @return the event ID as a String.
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the details of the event.
     *
     * @param details the details to be set as a String.
     */
    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * Sets the unique ID for the event.
     *
     * @param eventId the event ID to be set as a String.
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Sets the title of the event.
     *
     * @param title the title to be set as a String.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets Date object
     *
     * @param dateTime
     */
    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * Sets the waitlist capacity integer
     *
     * @param waitlistCapacity
     */
    public void setWaitlistCapacity(int waitlistCapacity) {
        this.waitlistCapacity = waitlistCapacity;
    }

    /**
     * Sets the requiresLocation bool
     *
     * @param requiresLocation
     */
    public void setRequiresLocation(boolean requiresLocation) {
        this.requiresLocation = requiresLocation;
    }

    /**
     * Sets the imageURL
     *
     * @param imageUrl
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Sets a facility
     *
     * @param facility
     */
    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    /**
     * Sets usersWaitlisted
     *
     * @param usersWaitlist
     */
    public void setUsersWaitlisted(Map<String, User> usersWaitlist) {
        this.usersWaitlist = usersWaitlist;
    }

    /**
     * Sets usersCancelled
     *
     * @param usersCancelled
     */
    public void setUsersCancelled(Map<String, User> usersCancelled) {
        this.usersCancelled = usersCancelled;
    }

    /**
     * Sets usersInvited
     *
     * @param usersInvited
     */
    public void setUsersInvited(Map<String, User> usersInvited) {
        this.usersInvited = usersInvited;
    }

    /**
     * Removes a user from waitlist
     *
     * @param user
     */
    public void removeUserFromWaitlist(User user) {
        if (usersWaitlist.get(user.getUniqueId()) != null) {
            usersWaitlist.remove(user.getUniqueId());
        }
    }

    /**
     * Removes a user from cancelled list
     *
     * @param user
     */
    public void removeUserFromCancelledList(User user) {
        if (usersCancelled.get(user.getUniqueId()) != null) {
            usersCancelled.remove(user.getUniqueId());
        }
    }

    /**
     * Removes a user from invited
     *
     * @param user
     */
    public void removeUserFromInvited(User user) {
        if (usersInvited.get(user.getUniqueId()) != null) {
            usersInvited.remove(user.getUniqueId());
        }
    }

    /**
     * Removes a user from sentInvite
     *
     * @param user
     */
    public void removeUserFromSentInvite(User user) {
        if (usersSentInvite.get(user.getUniqueId()) != null) {
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
     *
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

        for (Map.Entry<String, User> entry : event.getUsersSentInvite().entrySet()) {
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
     *
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
     *
     * @param drawAmount
     */
    public void runLottery(int drawAmount) {
        // Ensure the draw amount does not exceed the size of the waitlist
        if (usersWaitlist.size() < drawAmount) {
            drawAmount = usersWaitlist.size();
        }

        // Create a list of user IDs from the waitlist
        List<String> waitlistUserIds = new ArrayList<>(usersWaitlist.keySet());

        // Shuffle the list of user IDs to randomize selection
        Collections.shuffle(waitlistUserIds);

        // Select the specified number of users from the shuffled list
        List<String> selectedUserIds = waitlistUserIds.subList(0, drawAmount);

        // Move the selected users from the waitlist to the invited list
        for (String userId : selectedUserIds) {
            // Get the User object from the waitlist
            User user = usersWaitlist.get(userId);

            if (user != null) {
                // Add the user to the invited list
                usersSentInvite.put(userId, user);

                // Remove the user from the waitlist
                usersWaitlist.remove(userId);

                // Notify the user if notifications are enabled
                if (user.hasNotificationsOn()) {
                    notificationHelper.notifyUserIfChosen(user, this);
                }
            }
        }

        // Update Firestore with the modified event
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventId)
                .set(this) // Update the Firestore document with the current event object
                .addOnSuccessListener(aVoid -> {
                    Log.d("runLottery", "Event successfully updated in Firestore.");
                })
                .addOnFailureListener(e -> {
                    Log.e("runLottery", "Error updating event in Firestore.", e);
                });
    }

    /**
     * Similar to the drawLottery function, only this time the first Entrant
     * within usersWaitlist is chosen when another Entrant declines their invite
     */
    public void redrawLottery() {
        if (usersWaitlist == null || usersWaitlist.isEmpty()) {
            Log.d("replaceFromWaitlist", "No users in the waitlist to replace");
            return;
        }

        // Gets the key, value pair for the first User in the waitlist
        Map.Entry<String, User> nextUser = usersWaitlist.entrySet().iterator().next();
        String userId = nextUser.getKey();
        User user = nextUser.getValue();

        usersWaitlist.remove(userId);
        usersSentInvite.put(userId, user);

        // Notifies the user
        if (user.hasNotificationsOn()) {
            notificationHelper.notifyUserIfChosen(user, this);
        }

        // Update Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventId).set(this)
                .addOnSuccessListener(aVoid -> Log.d("redrawLottery", "Event updated after replacement"))
                .addOnFailureListener(e -> Log.e("redrawLottery", "Failed to update event", e));
    }

    /**
     * This method uses the addNotificationToUser method to add a notification message to the users notification list
     *
     * @param status The status of the user in the event list
     * @param db     The firestore database
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
                        } else if ("invited".equals(status)) {
                            usersToNotify = (Map<String, User>) documentSnapshot.get("usersSentInvite");
                        }

                        if (usersToNotify != null) {
                            for (Map.Entry<String, User> entry : usersToNotify.entrySet()) {
                                String userId = entry.getKey();
                                if (userId != null) {

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





