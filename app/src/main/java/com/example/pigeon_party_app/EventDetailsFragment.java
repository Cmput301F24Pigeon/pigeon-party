package com.example.pigeon_party_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.Format;
import java.text.SimpleDateFormat;

public class EventDetailsFragment extends AppCompatActivity {
    private TextView eventTitle;
    private TextView eventDateTime;
    private TextView eventLocation;
    private TextView eventDetails;
    private TextView eventCapacity;
    private Event event;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_details_fragment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    eventTitle = findViewById(R.id.eventName);
    eventDateTime = findViewById(R.id.eventDateTime);
    eventLocation = findViewById(R.id.eventLocation);
    eventDetails = findViewById(R.id.eventDetails);
    eventCapacity = findViewById(R.id.eventCapacity);

    Format formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm");

    event = getIntent().getParcelableExtra("event");
        assert event != null;
        eventTitle.setText(event.getTitle());
    eventDateTime.setText(formatter.format(event.getDateTime()));
    eventLocation.setText(event.getLocation());
    eventDetails.setText(event.getDetails());
    eventCapacity.setText(event.getWaitlistCapacity());

    user = getIntent().getParcelableExtra("user");

    signUpButton();
    }

    private void signUpButton(){
        Button signUpButton = findViewById(R.id.signupButton);
        if (event.getWaitlistCapacity() != 0){
            signUpButton.setText("Waitlist is Full");
        }
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (event.getWaitlistCapacity() != 0){
                    event.addUserToWaitlist(user);
                }
                finish();
            }
        });
    }
}