package com.example.pigeon_party_app;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    ImageView facilityButton;

    public void addUser(User user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("User").document(user.getName()).set(user).addOnSuccessListener(aVoid -> {
                    Log.d("FireStore", "Event successfully added");
                })
                .addOnFailureListener(e -> {
                    Log.w("FireStore", "Error adding event", e);
                });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //Used https://www.youtube.com/watch?v=-w8Faojl4HI to determine unique ID
        String uniqueId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        User user = new User("id2","paul", "780", true, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        addUser(user);
        User currentUser = new User("John Doe", "johndoe@gmail.com"); //need to pass user to different fragments
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