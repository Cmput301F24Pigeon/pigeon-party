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

public class EventDetailsFragment extends AppCompatActivity {
    private TextView eventName;
    private TextView eventDateTime;
    private TextView eventLocation;
    private TextView eventDetails;
    private TextView eventCapacity;

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
    eventName = findViewById(R.id.eventName);
    eventDateTime = findViewById(R.id.eventDateTime);
    eventLocation = findViewById(R.id.eventLocation);
    eventDetails = findViewById(R.id.eventDetails);
    eventCapacity = findViewById(R.id.eventCapacity);

    eventName.setText(getIntent().getStringExtra("eventName"));
    eventDateTime.setText("Date and Time:\n" + getIntent().getStringExtra("eventDateTime"));
    eventLocation.setText("Location:\n" + getIntent().getStringExtra("eventLocation"));
    eventDetails.setText("Details:\n" + getIntent().getStringExtra("eventDetails"));
    eventCapacity.setText("Capacity:\n" + getIntent().getStringExtra("eventCapacity"));


    }

    public interface signUpListener {
        void signUp(Boolean isSignedUp);
    }

    private signUpListener listener;

    private void signUpButton(){
        Button signUpButton = findViewById(R.id.signupButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.signUp(Boolean.TRUE);
            }
        });
    }
}