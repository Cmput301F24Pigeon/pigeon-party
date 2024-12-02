package com.example.pigeon_party_app;


import android.app.Activity;
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
import android.widget.ArrayAdapter;
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
import androidx.fragment.app.Fragment;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This is the main activity which serves as the home screen of the app
 */
public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> qrScannerLauncher;
    //    private ActivityResultLauncher<String> requestPermissionsLauncher;
    private ImageView facilityButton;
    private ImageView profileButton;
    private ImageView adminButton;
    private ImageButton addEventButton;
    private ListView eventListView;
    public static EventsArrayAdapter eventsArrayAdapter;
    public static ArrayList<Event> eventArrayList;
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
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        notificationHelper = new NotificationHelper(this);
        String uniqueId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        qrScannerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                IntentResult intentResult = IntentIntegrator.parseActivityResult(result.getResultCode(), result.getData());
                if (intentResult != null) {
                    String qrContent = intentResult.getContents();
                    if (qrContent != null) {
                        handleQrCode(qrContent);
                    } else {
                        Log.e("QR Scan", "QR code content is null.");
                    }
                } else {
                    Log.e("QR Scan", "Intent result is null.");
                }
            }
        });
        currentUser = receiveCurrentUser(uniqueId);

        if (currentUser != null) {
            checkUserNotifications(currentUser);


            eventArrayList = new ArrayList<>();
            loadEvents(currentUser.getEntrantEventList());
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
        setUpAdminButton();
        setUpFacilityButton();
        setUpAddEventButton();
        if (MainActivity.currentUser != null) {
            if (MainActivity.currentUser.isAdmin()) {
                adminButton.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * This method gets the current user from firebase for us to use if the current user is not displayed then we prompt the user to enter in details
     *
     * @param uniqueId a string that is our id of our device
     * @return currentuser the currentuser data from firebase
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
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CreateEntrantProfileFragment()).addToBackStack(null).commitAllowingStateLoss();
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
        db.collection("user").document(user.getUniqueId()).get().addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {
                        List<String> notifications = (List<String>) documentSnapshot.get("notifications");

                        if (notifications != null && !notifications.isEmpty()) {
                            String message = notifications.get(0);
                            notificationHelper.notifyUser(user, message);

                            user.clearNotifications();
                            db.collection("user").document(user.getUniqueId()).update("notifications", user.getNotifications()).addOnSuccessListener(aVoid -> {
                                Log.d("Firestore", "Notifications cleared for user " + user.getUniqueId());
                            }).addOnFailureListener(e -> {
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
     * @param qrContent the string which a qr code returns
     */
    private void handleQrCode(String qrContent) {
        DocumentReference docRef = db.collection("events").document(qrContent);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                currentEvent = documentSnapshot.toObject(Event.class);
                showEventDetailsFragment();
            } else {
                Toast.makeText(getApplicationContext(), "Invalid QR Code: Event not found", Toast.LENGTH_SHORT).show();
            }
        });
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
        qrScannerLauncher.launch(integrator.createScanIntent());
    }

    /**
     * This method shows the event details fragment
     */
    private void showEventDetailsFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EventDetailsFragment()).addToBackStack(null).commit();
    }

    /**
     * This method asks user for permission to receive notification
     */
    //https://www.youtube.com/watch?v=JeZJaafE5ik
    private void askNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
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
    private final ActivityResultLauncher<String> requestPermissionsLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (!isGranted) {
            MainActivity.currentUser.setNotificationsOn(false);
        } else {
            MainActivity.currentUser.setNotificationsOn(true);

        }
    });


    /**
     * This method sets up the facility button
     */
    private void setUpFacilityButton() {
        facilityButton = findViewById(R.id.button_facility);
        facilityButton.setOnClickListener(v -> {
            if (MainActivity.currentUser != null) {
                if (MainActivity.currentUser.isOrganizer()) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OrganizerFragment()).addToBackStack(null).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FacilityFragment()).addToBackStack(null).commit();
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
            if (currentUser != null && currentUser.isEntrant()) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ViewEntrantProfileFragment()).addToBackStack(null).commit();
            }
        });
    }

    /**
     * This method sets up the admin button
     */
    private void setUpAdminButton() {
        adminButton = findViewById(R.id.button_admin);
        adminButton.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AdminFragment()).addToBackStack(null).commit();
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

                    if (currentEvent.getUsersCancelled().get(userId) != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Congratulations! You have been invited to" + currentEvent.getTitle());
                        builder.setCancelable(true);

                        builder.setPositiveButton("Accept", (dialog, which) -> {
                            currentEvent.addUserToInvited(currentUser);
                            currentEvent.removeUserFromWaitlist(currentUser);

                            Map<String, Object> invitedListUpdates = currentEvent.updateFirebaseEventInvitedList(currentEvent);
                            Map<String, Object> waitlistUpdates = currentEvent.updateFirebaseEventWaitlist(currentEvent);

                            db.collection("events").document(currentEvent.getEventId()).update(invitedListUpdates).addOnSuccessListener(aVoid -> Log.d("Firestore", "Event's invited list successfully updated")).addOnFailureListener(e -> Log.w("Firestore", "Error updating event's invited list", e));

                            db.collection("events").document(currentEvent.getEventId()).update(waitlistUpdates).addOnSuccessListener(aVoid -> Log.d("Firestore", "Event's waitlist successfully updated")).addOnFailureListener(e -> Log.w("Firestore", "Error updating event's waitlist", e));
                        });

                        builder.setNegativeButton("Decline", (dialog, which) -> {
                            currentEvent.addUserToCancelled(currentUser);
                            currentEvent.removeUserFromWaitlist(currentUser);

                            Map<String, Object> cancelledListUpdates = currentEvent.updateFirebaseEventCancelledList(currentEvent);
                            Map<String, Object> waitlistUpdates = currentEvent.updateFirebaseEventWaitlist(currentEvent);

                            db.collection("events").document(currentEvent.getEventId()).update(cancelledListUpdates).addOnSuccessListener(aVoid -> Log.d("Firestore", "Event's cancelled list successfully updated")).addOnFailureListener(e -> Log.w("Firestore", "Error updating event's cancelled list", e));

                            db.collection("events").document(currentEvent.getEventId()).update(waitlistUpdates).addOnSuccessListener(aVoid -> {
                                Log.d("Firestore", "Event's waitlist successfully updated");

                                // After successful updates, redraw the lottery
                                if (currentEvent.getUsersWaitlisted() != null && !currentEvent.getUsersWaitlisted().isEmpty()) {
                                    currentEvent.redrawLottery();
                                } else {
                                    Log.d("Lottery", "No users in the waitlist for lottery redraw");
                                }
                            }).addOnFailureListener(e -> Log.w("Firestore", "Error updating event's waitlist", e));
                        });


                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                    } else if (currentEvent.getUsersWaitlisted().get(userId) != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Remove yourself from the waitlist of " + currentEvent.getTitle() + "?");
                        builder.setCancelable(true);

                        builder.setNegativeButton("Back", (dialog, which) -> dialog.cancel());

                        builder.setPositiveButton("OK", (dialog, which) -> {
                            currentEvent.removeUserFromWaitlist(currentUser);
                            currentEvent.addUserToCancelled(currentUser);
                            currentUser.removeEntrantEventList(position);
                            updateEntrantEventList(currentUser);
                            if (eventArrayList != null) {
                                eventArrayList.remove(position);
                                eventsArrayAdapter.notifyDataSetChanged();
                            }


                            Map<String, Object> waitlistUpdates = currentEvent.updateFirebaseEventWaitlist(currentEvent);
                            Map<String, Object> cancelledListUpdates = currentEvent.updateFirebaseEventCancelledList(currentEvent);

                            db.collection("events").document(currentEvent.getEventId()).update(waitlistUpdates).addOnSuccessListener(aVoid -> Log.d("Firestore", "Event's waitlist successfully updated")).addOnFailureListener(e -> Log.w("Firestore", "Error updating event's waitlist", e));

                            db.collection("events").document(currentEvent.getEventId()).update(cancelledListUpdates).addOnSuccessListener(aVoid -> Log.d("Firestore", "Event's cancelled list successfully updated")).addOnFailureListener(e -> Log.w("Firestore", "Error updating event's cancelled list", e));
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else if (currentEvent.getUsersSentInvite().get(userId) != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Remove yourself from " + currentEvent.getTitle());
                        builder.setCancelable(true);

                        builder.setNegativeButton("Back", (dialog, which) -> dialog.cancel());

                        builder.setPositiveButton("OK", (dialog, which) -> {
                            currentEvent.removeUserFromInvited(currentUser);
                            currentEvent.addUserToCancelled(currentUser);
                            currentUser.removeEntrantEventList(position);
                            updateEntrantEventList(currentUser);
                            if (eventArrayList != null) {
                                eventArrayList.remove(position);
                                eventsArrayAdapter.notifyDataSetChanged();
                            }


                            Map<String, Object> sentInvitedUpdates = currentEvent.updateFirebaseEventSentInvited(currentEvent);
                            Map<String, Object> cancelledListUpdates = currentEvent.updateFirebaseEventCancelledList(currentEvent);

                            db.collection("events").document(currentEvent.getEventId()).update(sentInvitedUpdates).addOnSuccessListener(aVoid -> Log.d("Firestore", "Event's sentInviteList successfully updated")).addOnFailureListener(e -> Log.w("Firestore", "Error updating event's waitlist", e));

                            db.collection("events").document(currentEvent.getEventId()).update(cancelledListUpdates).addOnSuccessListener(aVoid -> Log.d("Firestore", "Event's cancelled list successfully updated")).addOnFailureListener(e -> Log.w("Firestore", "Error updating event's cancelled list", e));
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else if (currentEvent.getUsersInvited().get(userId) != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Remove yourself from" + currentEvent.getEventId());
                        builder.setCancelable(true);

                        builder.setNegativeButton("Back", (dialog, which) -> dialog.cancel());

                        builder.setPositiveButton("OK", (dialog, which) -> {
                            currentEvent.removeUserFromInvited(currentUser);
                            currentEvent.addUserToCancelled(currentUser);
                            currentUser.removeEntrantEventList(position);
                            updateEntrantEventList(currentUser);
                            if (eventArrayList != null) {
                                eventArrayList.remove(position);
                                eventsArrayAdapter.notifyDataSetChanged();
                            }


                            Map<String, Object> invitedUpdates = currentEvent.updateFirebaseEventInvitedList(currentEvent);
                            Map<String, Object> cancelledListUpdates = currentEvent.updateFirebaseEventCancelledList(currentEvent);

                            db.collection("events").document(currentEvent.getEventId()).update(invitedUpdates).addOnSuccessListener(aVoid -> Log.d("Firestore", "Event's invite list successfully updated")).addOnFailureListener(e -> Log.w("Firestore", "Error updating event's waitlist", e));

                            db.collection("events").document(currentEvent.getEventId()).update(cancelledListUpdates).addOnSuccessListener(aVoid -> Log.d("Firestore", "Event's cancelled list successfully updated")).addOnFailureListener(e -> Log.w("Firestore", "Error updating event's cancelled list", e));
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
     *
     * @param user The user who has his entrant eventlist updated
     */
    public void updateEntrantEventList(User user) {
        String userId = user.getUniqueId();

        // Update syntax from Firebase docs: https://firebase.google.com/docs/firestore/manage-data/add-data#java_10
        DocumentReference userRef = db.collection("user").document(userId);

        userRef.update("entrantEventList", user.getEntrantEventList()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("firebase", "DocumentSnapshot successfully updated!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("firebase", "Error updating document", e);
            }
        });
    }

    public static void addEventToList(Event event) {
        if (eventArrayList != null) {
            eventArrayList.add(event);
            eventsArrayAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Load the events from firebase into our list so it takes the string values and converts it into events
     *
     * @param eventIds is a list of eventIds for our so it takes the strings from the list
     */
    private void loadEvents(ArrayList<String> eventIds) {

        for (String i : eventIds) {
            Log.d("blehh", i);
            DocumentReference docRef = db.collection("events").document(i);

            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        eventArrayList.add(documentSnapshot.toObject(Event.class));
                        eventsArrayAdapter.notifyDataSetChanged();

                    } else {
                        eventIds.remove(i);
                    }


                }

            });

        }

        DocumentReference userRef = db.collection("user").document(MainActivity.currentUser.getUniqueId());
        userRef.update("entrantEventList", eventIds).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("firebase", "DocumentSnapshot successfully deleted!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("firebase", "Error deleting document", e);
            }
        });
    }


}
