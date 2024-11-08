package com.example.pigeon_party_app;

import android.content.DialogInterface;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This is the main activity which serves as the home screen of the app
 */
public class MainActivity extends AppCompatActivity {

    private ImageView facilityButton;
    private ImageView profileButton;
    private ImageView notificationButton;
    private ImageButton addEventButton;
    private ListView eventListView;
    private static EventsArrayAdapter eventsArrayAdapter;
    private static ArrayList<Event> eventArrayList;
    private NotificationHelper notificationHelper;
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static User currentUser;
    public static Event currentEvent;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static Event getCurrentEvent() {
        return currentEvent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        notificationHelper = new NotificationHelper(this);
        String uniqueId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        currentUser = receiveCurrentUser(uniqueId);
        if (currentUser != null) {
            NotificationHelper notificationHelper = new NotificationHelper(this);
            checkUserNotifications(currentUser);


            eventArrayList = new ArrayList<>();
            eventArrayList = MainActivity.currentUser.getEntrantEventList();
            eventListView = findViewById(R.id.event_list);
            eventsArrayAdapter = new EventsArrayAdapter(MainActivity.this, eventArrayList);
            eventListView.setAdapter(eventsArrayAdapter);

            //receiveEvents();

        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setUpEventClickListener();
        setUpProfileButton();
        setUpNotificationButton();
        setUpFacilityButton();
        setUpAddEventButton();
    }

    /**
     * This method gets the current user from firebase for us to use if the current user is not displayed then we prompt the user to enter in details
     */
    public User receiveCurrentUser(String uniqueId) {

        DocumentReference docRef = db.collection("user").document(uniqueId);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    MainActivity.currentUser = documentSnapshot.toObject(User.class);
                    askNotificationPermission();
                    if (currentUser.getNotifications() == null) {
                        currentUser.setNotifications(new ArrayList<>());
                    }
                } else {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new CreateEntrantProfileFragment())
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                }
            }
        });
        return currentUser;
    }

    /**
     * This method checks if a user has any notifications in firebase, if they do then the notifications will be shown to the user and cleared from the database
     *
     * @param user The user which the method checks notifications
     */
    public void checkUserNotifications(User user) {
        notificationHelper = new NotificationHelper(this);
        db.collection("user").document(user.getUniqueId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {
                        List<String> notifications = (List<String>) documentSnapshot.get("notifications");

                        if (notifications != null && !notifications.isEmpty()) {
                            String message = notifications.get(0);
                            notificationHelper.notifyUser(user, message);

                            user.clearNotifications();
                            db.collection("user").document(user.getUniqueId())
                                    .update("notifications", user.getNotifications())
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "Notifications cleared for user " + user.getUniqueId());
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("Firestore", "Error updating notifications", e);
                                    });
                        }

                    } else {
                        Log.w("Firestore", "User document not found");
                    }
                })

                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error retrieving user document", e);
                });
    }

    /**
     * This method gets the results from the QR scanner
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null && result.getContents() != null) {
            String qrContent = result.getContents();
            DocumentReference docRef = db.collection("events").document(qrContent);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        MainActivity.currentEvent = (documentSnapshot.toObject(Event.class));
                        showEventDetailsFragment();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Invalid QR Code: Event not found", Toast.LENGTH_SHORT).show();

                    }
                }

            });
        } else {
            Toast.makeText(getApplicationContext(), "Invalid QR Code", Toast.LENGTH_SHORT).show();
            return;
        }

        //receiveEvents();
    }

    /**
     * This method initializes the QR scanner
     */
    //https://www.geeksforgeeks.org/how-to-read-qr-code-using-zxing-library-in-android/
    private void startQRScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan the event QR code");
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.initiateScan();
    }

    /**
     * This method shows the event details fragment
     */
    private void showEventDetailsFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new EventDetailsFragment())
                .addToBackStack(null)
                .commit();
    }

    /**
     * This method asks user for permission to receive notification
     */
    //https://www.youtube.com/watch?v=JeZJaafE5ik
    private void askNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED) {
                currentUser.setNotificationsOn(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionsLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    /**
     * This method sets users in app notification preferences to false
     */
    private final ActivityResultLauncher<String> requestPermissionsLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    MainActivity.currentUser.setNotificationsOn(false);
                }
                else {
                    MainActivity.currentUser.setNotificationsOn(true);

                }
            });


    //https://www.geeksforgeeks.org/how-to-create-dynamic-listview-in-android-using-firebase-firestore/

    /**
     * Receives events user is associated with and adapts them to the ListView
     * was used before but not needed
     */
    private void receiveEvents() {
        String uniqueId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        eventArrayList.clear();
        db.collection("events").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                Event event = d.toObject(Event.class);
                                if((!event.getUsersWaitlisted().isEmpty() && event.getUsersWaitlisted().containsKey(uniqueId))
                                || (!event.getUsersInvited().isEmpty() && event.getUsersInvited().containsKey(uniqueId))
                                || (!event.getUsersCancelled().isEmpty() && event.getUsersCancelled().containsKey(uniqueId))) {
                                    eventArrayList.add(event);
                                }

                                eventsArrayAdapter = new EventsArrayAdapter(MainActivity.this, eventArrayList);
                                eventListView.setAdapter(eventsArrayAdapter);
                            }
                        }
                    }

                });

    }

    /**
     * This method gets a user from Firebase was needed before but not needed anymore
     * @param documentSnapshot the data from the user document in Firebase
     * @return a User object
     */
    private User getUserFromFirebase(DocumentSnapshot documentSnapshot) {
        User user = null;
        String userName = (documentSnapshot.get("name")).toString();
        String userEmail = (documentSnapshot.get("email")).toString();
        String userPhoneNumber = (documentSnapshot.get("phoneNumber")).toString();
        String userId = (documentSnapshot.get("uniqueId")).toString();
        boolean isOrganizer = (documentSnapshot.getBoolean("organizer"));
        boolean isEntrant = (documentSnapshot.getBoolean("entrant"));
        boolean notificationStatus = (documentSnapshot.getBoolean("notificationStatus"));
        ArrayList<Event> entrantList = (ArrayList<Event>) documentSnapshot.get("entrantList");
        ArrayList<Event> organizerList = (ArrayList<Event>) documentSnapshot.get("organizerList");


        if ((documentSnapshot.get("facility")) != null) {
            String facilityAddress = (documentSnapshot.get("facility.address")).toString();
            String facilityName = (documentSnapshot.get("facility.name")).toString();
            String facilityOwner = (documentSnapshot.get("facility.ownerId")).toString();

            Facility userFacility = new Facility(facilityOwner, facilityAddress, facilityName);

            user = new User(userName, userEmail, userPhoneNumber, userId, isOrganizer, isEntrant, userFacility, notificationStatus, entrantList, organizerList);
        }

        if ((documentSnapshot.get("facility")) == null) {
            organizerList = new ArrayList<>();
            user = new User(userName, userEmail, userPhoneNumber, userId, isOrganizer, isEntrant, null, notificationStatus, entrantList, organizerList);
        }
        
        return user;
    }

    /**
     * This method sets up the facility button
     */
    private void setUpFacilityButton() {
        facilityButton = findViewById(R.id.button_facility);
        facilityButton.setOnClickListener(v -> {
            if (MainActivity.currentUser != null) {
                if (MainActivity.currentUser.isOrganizer()) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new OrganizerFragment())
                            .addToBackStack(null)
                            .commit();
                } else {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new FacilityFragment())
                            .addToBackStack(null)
                            .commit();
                }
            } else {
                Log.e("MainActivity", "Current user is null. Cannot determine organizer status.");
            }
        });
    }

    /**
     * This method sets up the profile button
     */
    private void setUpProfileButton() {
        profileButton = findViewById(R.id.button_profile);
        profileButton.setOnClickListener(v -> {
            User currentUser = MainActivity.getCurrentUser();

            if (currentUser.isEntrant()) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ViewEntrantProfileFragment(currentUser))
                        .addToBackStack(null)
                        .commit();
            }

        });
    }

    /**
     * This method sets up the notification button
     */
    private void setUpNotificationButton() {
        notificationButton = findViewById(R.id.button_notifications);
        notificationButton.setOnClickListener(v -> {
            currentUser = getCurrentUser();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ViewNotificationsFragment(currentUser))
                    .addToBackStack(null)
                    .commit();
        });
    }

    /**
     * This method sets up the add event button
     */
    private void setUpAddEventButton() {
        addEventButton = findViewById(R.id.button_add_event);
        addEventButton.setOnClickListener(v -> startQRScanner());
    }

    /**
     * This method allows users to click on their event and accept or decline invites
     */
    private void setUpEventClickListener() {
        if (currentUser != null) {
            eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    User currentUser = MainActivity.getCurrentUser();
                    Event currentEvent = eventsArrayAdapter.getItem(position);
                    String userId = currentUser.getUniqueId();

                    if (currentEvent.getUsersSentInvite().get(userId) != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Do you want to accept the invitation?");
                        builder.setCancelable(true);

                        builder.setPositiveButton("Accept", (dialog, which) -> {
                            currentEvent.addUserToInvited(currentUser);
                            currentEvent.removeUserFromWaitlist(currentUser);

                            Map<String, Object> invitedListUpdates = currentEvent.updateFirebaseEventInvitedList(currentEvent);
                            Map<String, Object> waitlistUpdates = currentEvent.updateFirebaseEventWaitlist(currentEvent);

                            db.collection("events").document(currentEvent.getEventId())
                                    .update(invitedListUpdates)
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Event's invited list successfully updated"))
                                    .addOnFailureListener(e -> Log.w("Firestore", "Error updating event's invited list", e));

                            db.collection("events").document(currentEvent.getEventId())
                                    .update(waitlistUpdates)
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Event's waitlist successfully updated"))
                                    .addOnFailureListener(e -> Log.w("Firestore", "Error updating event's waitlist", e));
                        });

                        builder.setNegativeButton("Decline", (dialog, which) -> {
                            currentEvent.addUserToCancelled(currentUser);
                            currentEvent.removeUserFromWaitlist(currentUser);

                            Map<String, Object> cancelledListUpdates = currentEvent.updateFirebaseEventCancelledList(currentEvent);
                            Map<String, Object> waitlistUpdates = currentEvent.updateFirebaseEventWaitlist(currentEvent);

                            db.collection("events").document(currentEvent.getEventId())
                                    .update(cancelledListUpdates)
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Event's cancelled list successfully updated"))
                                    .addOnFailureListener(e -> Log.w("Firestore", "Error updating event's cancelled list", e));

                            db.collection("events").document(currentEvent.getEventId())
                                    .update(waitlistUpdates)
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Event's waitlist successfully updated"))
                                    .addOnFailureListener(e -> Log.w("Firestore", "Error updating event's waitlist", e));
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                    } else {
                        // For second half of project, we can add to this for user story which allows entrant to stay on waiting list in case spot opens
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Remove yourself from this event?");
                        builder.setCancelable(true);

                        builder.setNegativeButton("Back", (dialog, which) -> dialog.cancel());

                        builder.setPositiveButton("OK", (dialog, which) -> {
                            currentEvent.removeUserFromWaitlist(currentUser);
                            currentEvent.addUserToCancelled(currentUser);
                            currentUser.removeEntrantEventList(position);
                            updateEntrantEventList(currentUser);
                            eventArrayList.remove(position);
                            eventsArrayAdapter.notifyDataSetChanged();


                            Map<String, Object> waitlistUpdates = currentEvent.updateFirebaseEventWaitlist(currentEvent);
                            Map<String, Object> cancelledListUpdates = currentEvent.updateFirebaseEventCancelledList(currentEvent);

                            db.collection("events").document(currentEvent.getEventId())
                                    .update(waitlistUpdates)
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Event's waitlist successfully updated"))
                                    .addOnFailureListener(e -> Log.w("Firestore", "Error updating event's waitlist", e));

                            db.collection("events").document(currentEvent.getEventId())
                                    .update(cancelledListUpdates)
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Event's cancelled list successfully updated"))
                                    .addOnFailureListener(e -> Log.w("Firestore", "Error updating event's cancelled list", e));
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }

                    //receiveEvents();
                }

            });
        }
    }

    /**
     * This function updates the user's eventlist in Firebase
     * @param user The user who has his entrant eventlist updated
     */
    public void updateEntrantEventList(User user) {
        String userId = user.getUniqueId();

        // Update syntax from Firebase docs: https://firebase.google.com/docs/firestore/manage-data/add-data#java_10
        DocumentReference userRef = db.collection("user").document(userId);

        userRef.update("entrantEventList", user.getEntrantEventList())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("firebase", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("firebase", "Error updating document", e);
                    }
                });
    }

    public static void addEventToList(Event event){
        eventArrayList.add(event);
        eventsArrayAdapter.notifyDataSetChanged();
    }

}
