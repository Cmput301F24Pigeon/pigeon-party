package com.example.pigeon_party_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements EventDetailsFragment.signUpListener{
    Event event;
    User user;
    ArrayList<User> userList;
    ArrayAdapter<User> userAdapter;
    Date d1 = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        user = new User("Paul Blart");
        event = new Event("beans", "Dodge ball Tournament", d1, 50, "A Dodge ball tournament", "Gym", Boolean.FALSE);
        Intent intent = new Intent(MainActivity.this, EventDetailsFragment.class);
        intent.putExtra("eventName", event.getTitle());
        intent.putExtra("eventDateTime", event.getDateTime());
        intent.putExtra("eventLocation", event.getLocation());
        intent.putExtra("eventDetails", event.getDetails());
        intent.putExtra("eventCapacity", String.valueOf(event.getWaitlistCapacity()));
        startActivity(intent);
    }

    @Override
    public void signUp(Boolean isSignedUp) {

    }
}