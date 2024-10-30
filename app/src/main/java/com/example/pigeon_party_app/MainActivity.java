package com.example.pigeon_party_app;

import android.app.NotificationManager;
import android.content.Context;

import static java.lang.reflect.Array.get;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    ImageView facilityButton;
    ImageView profileButton;
    ArrayList<Event> eventDataList;
    EventsArrayAdapter eventArrayAdapter;

    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static User currentUser;


    public static User getCurrentUser() {
        return currentUser;
    }

    public void addUser(User user) {
        //Used https://www.youtube.com/watch?v=-w8Faojl4HI to determine unique ID
        String uniqueId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);


        Map<String, Object> Users = new HashMap<>();

        Users.put("name", user.getName());
        Users.put("email", user.getEmail());
        Users.put("phoneNumber", user.getPhoneNumber());
        Users.put("uniqueId", user.getUniqueId());
        Users.put("entrant", user.isEntrant());
        Users.put("organizer", user.isOrganizer());
        Users.put("facility", user.getFacility());

        db.collection("user").document(uniqueId)
                .set(Users)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FireStore", "Facility successfully added");
                })
                .addOnFailureListener(e ->{
                    Log.w("FireStore", "Error adding facility", e);
                });

        /*
        db.collection("User").document(uniqueId).set(user).addOnSuccessListener(aVoid -> {
                    Log.d("FireStore", "Event successfully added");
                })
                .addOnFailureListener(e -> {
                    Log.w("FireStore", "Error adding event");
                });
                */
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String uniqueId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //uncomment if you don't have a user made
        User user = new User("John","johndoe@gmail.com", "7802232", uniqueId, false, true, null);
        addUser(user);
        receiveCurrentUser();

        eventDataList = new ArrayList<>();

        eventDataList.add(new Event("dogtooth", "dog", , null, null, null, null, null));
        eventArrayAdapter = new EventsArrayAdapter(this, eventDataList);



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });






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

           // TEST NOTIFICATION
        // Create an Event where the entrant is chosen
        //Event event = new Event("Swimming Lessons", true);

        // Create NotificationHelper instance
        //NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());

        // Trigger notification for the current user if chosen
        //notificationHelper.notifyUserIfChosen(currentUser, event);

    }

    public void receiveCurrentUser(){
        String uniqueId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        DocumentReference docRef = db.collection("user").document(uniqueId);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {
                    MainActivity.currentUser = (documentSnapshot.toObject(User.class));
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

}