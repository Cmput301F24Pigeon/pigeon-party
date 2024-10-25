package com.example.pigeon_party_app;


import static java.lang.reflect.Array.get;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    ImageView facilityButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static User currentUser;

    // how to access in other classes
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
        Users.put("entrant", user.isEntrant());
        Users.put("organizer", user.isOrganizer());
        Users.put("facility", user.getFacility());

        db.collection("user")
                .add(Users)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>(){
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("firebase", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                        public void onFailure(@NonNull Exception e) {
                        Log.w("firebase", "Error adding document", e);
                    }
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


        User user = new User("was","yo", "780", true, false, null);
        addUser(user);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //firebase getting currentUser

        DocumentReference docRef = db.collection("User").document(uniqueId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject(User.class);


                /*
                String name = documentSnapshot.getString("name");
                String email = documentSnapshot.getString("email");
                String phone = documentSnapshot.getString("phoneNumber");
                Boolean entrant = documentSnapshot.getBoolean("entrant");
                Boolean organizer = documentSnapshot.getBoolean("organizer");

                currentUser = new User(name, email, phone, entrant, organizer);

                 */
            }
        });



        facilityButton = findViewById(R.id.button_facility);
        facilityButton.setOnClickListener(v -> {
            if (currentUser.isOrganizer()){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new OrganizerFragment())
                        .addToBackStack(null)
                        .commit();
            }
            else {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new FacilityFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

    }
}
