
package com.example.pigeon_party_app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * This class is a fragment that shows the user event details after the user scans the QR Code, also
 * allows the user to sign up for the event if the waitlist is not full
 */
public class EventDetailsFragment extends Fragment {
    private TextView eventTitle;
    private TextView eventDateTime;
    //private TextView eventLocation;
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
        //eventLocation = view.findViewById(R.id.eventLocation);
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

    /**
     * This method handles everything to do with the signup button: setting the text, firebase
     * integration, and updating the event's waitlist
     */
    // https://stackoverflow.com/questions/51737667/since-the-android-getfragmentmanager-api-is-deprecated-is-there-any-alternati
    private void signUpButton(){
        if (event.getWaitlistCapacity() == 0){
            signUpButton.setText("Waitlist is Full");
        }
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (event.getWaitlistCapacity() != 0){
                    if(event.getUsersCancelled().get(MainActivity.currentUser.getUniqueId()) != null){
                        event.removeUserFromCancelledList(MainActivity.currentUser);
                        Map<String, Object> cancelledListUpdates = event.updateFirebaseEventCancelledList(event);
                        updateFirebase(cancelledListUpdates, "cancelled list");
                    }
                    event.addUserToWaitlist(current_user);
                    Map<String, Object> updates = event.updateFirebaseEventWaitlist(event);
                    updateFirebase(updates, "waitlist");
                }
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    public void updateFirebase(Map<String, Object> updates, String list){
        String msg = String.format("Event's %s successfully updated", list);
        db.collection("events").document(event.getEventId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", msg);
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating event's waitlist", e));
    }
}
