
package com.example.pigeon_party_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentReference;
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
    Event event = MainActivity.getCurrentEvent();
    User current_user = MainActivity.getCurrentUser();
    private Button signUpButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        ImageButton backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        });

        return view;
    }

    /**
     * This method handles everything to do with the signup button: setting the text, firebase
     * integration, and updating the event's waitlist
     */
    // https://stackoverflow.com/questions/51737667/since-the-android-getfragmentmanager-api-is-deprecated-is-there-any-alternati
    private void signUpButton(){

        DocumentReference eventRef = FirebaseFirestore.getInstance()
                .collection("events")
                .document(event.getEventId());

        eventRef.collection("usersWaitlisted").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int currentSize = task.getResult().size(); // Get the number of users in the waitlist
                int waitlistCapacity = event.getWaitlistCapacity();

                if (waitlistCapacity > 0 && currentSize >= waitlistCapacity) {
                    signUpButton.setText("Waitlist is Full");
                    signUpButton.setEnabled(false);
                } else {
                    signUpButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (event.isRequiresLocation()) {
                                showLocationVerificationDialog();
                            }
                            else {
                                addToWaitlist();
                            }
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    });

                }
            } else {
                System.err.println("Error getting waitlist size: " + task.getException());
            }
        });
    }

    /**
     * Adds the user to the corresponding event's waitlist in firebase
     * @param updates
     * @param list
     */
    public void updateFirebase(Map<String, Object> updates, String list){
        String msg = String.format("Event's %s successfully updated", list);
        db.collection("events").document(event.getEventId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", msg);
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating event's waitlist", e));
        updates.put("entrantEventList", current_user.getEntrantEventList());
        db.collection("user").document(current_user.getUniqueId())
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User's facility successfully updated"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating user's entrant list", e));
    }

    /**
     * An alert dialog that warns the user if an event requires location verification
     */
    private void showLocationVerificationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Warning")
                .setMessage("This event requires location verification.")
                .setPositiveButton("Continue", (dialog, which) -> addToWaitlist())
                .setNegativeButton("Back", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * A method to add a user to the events waitlist upon signing up for the event
     */
    private void addToWaitlist() {
        handleUserCancellation();
        event.addUserToWaitlist(current_user);
        current_user.addEntrantEventList(event);
        MainActivity.addEventToList(event);
        Map<String, Object> updates = event.updateFirebaseEventWaitlist(event);
        updateFirebase(updates, "waitlist");
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    /**
     * A method that will remove a user from the cancelled list if they choose to resign up for an event
     */
    private void handleUserCancellation() {
        if (event.getUsersCancelled().containsKey(current_user.getUniqueId())) {
            event.removeUserFromCancelledList(current_user);
            Map<String, Object> updates = event.updateFirebaseEventCancelledList(event);
            updateFirebase(updates, "cancelled list");
        }
    }
}
