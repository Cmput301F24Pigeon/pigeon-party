package com.example.pigeon_party_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;


import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


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


public class MainActivity extends AppCompatActivity{

    private ImageView facilityButton;
    private ImageView profileButton;
    private ImageView notificationButton;
    private ImageButton addEventButton;
    private ListView eventListView;
    private EventsArrayAdapter eventsArrayAdapter;
    private ArrayList<Event> eventArrayList;

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
        String uniqueId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);



        receiveCurrentUser();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        eventArrayList = new ArrayList<>();

        eventListView = findViewById(R.id.event_list);
        receiveEvents();

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
                // Handle the case where currentUser is null, e.g., show a toast or log an error
                Log.e("MainActivity", "Current user is null. Cannot determine organizer status.");
            }
        });

        profileButton = findViewById(R.id.button_profile);
        profileButton.setOnClickListener(v -> {
            User currentUser = MainActivity.getCurrentUser();
            if (currentUser.isEntrant()){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ViewEntrantProfileFragment(currentUser))
                        .addToBackStack(null)
                        .commit();
            }
        });

        notificationButton = findViewById(R.id.button_notifications);
        notificationButton.setOnClickListener(v -> {
            currentUser = getCurrentUser();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ViewNotificationsFragment(currentUser))
                    .addToBackStack(null)
                    .commit();
        });

        // TEST NOTIFICATION
        // Create an Event where the entrant is chosen
        //Event event = new Event("Swimming Lessons", true);

        // Create NotificationHelper instance
        //NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());

        // Trigger notification for the current user if chosen
        // notificationHelper.notifyUserIfChosen(currentUser, event);

        addEventButton = findViewById(R.id.button_add_event);
        addEventButton.setOnClickListener(v->startQRScanner());

        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Remove yourself from this event?");
                builder.setCancelable(true);
                builder.setNegativeButton("Back", (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.cancel();
                });
                builder.setPositiveButton("OK", (DialogInterface.OnClickListener) (dialog, which) -> {
                    User currentUser = MainActivity.getCurrentUser();
                    currentEvent = eventsArrayAdapter.getItem(position);
                    currentEvent.removeUserFromWaitlist(currentUser);
                    currentEvent.addUserToCancelled(currentUser);
                    Map<String, Object> waitlistUpdates = currentEvent.updateFirebaseEventWaitlist(currentEvent);
                    Map<String, Object> cancelledListUpdates = currentEvent.updateFirebaseEventCancelledList(currentEvent);
                    db.collection("events").document(currentEvent.getEventId())
                            .update(waitlistUpdates)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Firestore", "Event's waitlist successfully updated");
                            })
                            .addOnFailureListener(e -> Log.w("Firestore", "Error updating event's waitlist", e));
                    db.collection("events").document(currentEvent.getEventId())
                            .update(cancelledListUpdates)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Firestore", "Event's cancelled list successfully updated");
                            })
                            .addOnFailureListener(e -> Log.w("Firestore", "Error updating event's waitlist", e));
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                receiveEvents();
            }
        });
    }

    public void receiveCurrentUser(){
        String uniqueId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        DocumentReference docRef = db.collection("user").document(uniqueId);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    //MainActivity.currentUser = getUserFromFirebase(documentSnapshot);
                    MainActivity.currentUser = documentSnapshot.toObject(User.class);
                }
                else{
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new CreateEntrantProfileFragment())
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

    }

    //https://www.geeksforgeeks.org/how-to-read-qr-code-using-zxing-library-in-android/
    private void startQRScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan the event QR code");
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.initiateScan();
    }
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
                }
            });
        }
        else {
            finish();
        }
        receiveEvents();
    }
    //uncomment once eventdetails can accept eventid
    private void showEventDetailsFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new EventDetailsFragment())
                .addToBackStack(null)
                .commit();
    }

    //https://www.geeksforgeeks.org/how-to-create-dynamic-listview-in-android-using-firebase-firestore/

    /**
     * Receives events user is associated with and adapts them to the ListView
     */
    private void receiveEvents(){
        String uniqueId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        eventArrayList.clear();
        db.collection("events").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list){
                                Event event = d.toObject(Event.class);
                                if((!event.getUsersWaitlisted().isEmpty() && event.getUsersWaitlisted().containsKey(uniqueId))
                                || (!event.getUsersInvited().isEmpty() && event.getUsersInvited().containsKey(uniqueId))
                                || (!event.getUsersCancelled().isEmpty() && event.getUsersCancelled().containsKey(uniqueId))){
                                    eventArrayList.add(event);
                                }
                            }
                            eventsArrayAdapter = new EventsArrayAdapter(MainActivity.this, eventArrayList);
                            eventListView.setAdapter(eventsArrayAdapter);
                        }
                    }
                });
    }

    private User getUserFromFirebase(DocumentSnapshot documentSnapshot) {
        String userName = (documentSnapshot.get("name")).toString();
        String userEmail = (documentSnapshot.get("email")).toString();
        String userPhoneNumber = (documentSnapshot.get("phoneNumber")).toString();
        String userId = (documentSnapshot.get("uniqueId")).toString();
        boolean isOrganizer = (documentSnapshot.getBoolean("organizer"));
        boolean isEntrant = (documentSnapshot.getBoolean("entrant"));
        Facility facility = (Facility)documentSnapshot.get("facility");
        boolean notificationStatus = (documentSnapshot.getBoolean("notificationStatus"));
        User user = new User(userName, userEmail, userPhoneNumber, userId, isOrganizer, isEntrant, facility, notificationStatus);

        return user;
    }


}