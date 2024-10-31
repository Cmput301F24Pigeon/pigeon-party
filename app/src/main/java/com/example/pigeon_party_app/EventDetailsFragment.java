package com.example.pigeon_party_app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Map;

public class EventDetailsFragment extends Fragment {
    private TextView eventTitle;
    private TextView eventDateTime;
    private TextView eventLocation;
    private TextView eventDetails;
    private TextView eventCapacity;
    private Event event = MainActivity.getCurrentEvent();
    private User current_user = MainActivity.getCurrentUser();
    private Button signUpButton;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public EventDetailsFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);
        eventTitle = view.findViewById(R.id.eventName);
        eventDateTime = view.findViewById(R.id.eventDateTime);
        eventLocation = view.findViewById(R.id.eventLocation);
        eventDetails = view.findViewById(R.id.eventDetails);
        eventCapacity = view.findViewById(R.id.eventCapacity);
        signUpButton = view.findViewById(R.id.signupButton);

        Format formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm");

        eventTitle.setText(event.getTitle());
        eventDateTime.setText("Date/Time: " + formatter.format(event.getDateTime()));
        //eventLocation.setText(event.getLocation());
        eventDetails.setText("Event Details:\n" + event.getDetails());
        eventCapacity.setText("Waitlist capacity: " + String.valueOf(event.getWaitlistCapacity()));

        signUpButton();
        return view;
    }

    private void signUpButton(){
        if (event.getWaitlistCapacity() == 0){
            signUpButton.setText("Waitlist is Full");
        }
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (event.getWaitlistCapacity() != 0){
                    event.addUserToWaitlist(current_user);
                    Map<String, Object> updates = event.createUserDetails(current_user, "Waitlist");
                    db.collection("events").document(event.getEventId())
                            .update(updates)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Firestore", "Event's waitlist successfully updated");
                            })
                            .addOnFailureListener(e -> Log.w("Firestore", "Error updating event's waitlist", e));
                }
                getFragmentManager().popBackStack();
            }
        });
    }
}