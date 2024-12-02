package com.example.pigeon_party_app;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EntrantListFragment extends Fragment {
    private static final String ARG_EVENT_ID = "eventId";
    private String eventId;
    Event event = MainActivity.getCurrentEvent();
    private ListView entrantListView;
    //private ArrayAdapter<User> entrantArrayAdapter;
    private EntrantArrayAdapter entrantArrayAdapter;
    private ArrayList<User> entrantList = new ArrayList<>();
    private Map<String, User> usersWaitlist;
    private Map<String, User> usersSentInvite;
    private Map<String, User> usersCancelled;

    public static EntrantListFragment newInstance(String eventId) {
        EntrantListFragment fragment = new EntrantListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrant_list, container, false);
        entrantListView = view.findViewById(R.id.entrant_list_view);
        // Set up adapter
        entrantArrayAdapter = new EntrantArrayAdapter(getContext(), entrantList, new HashMap<>(), new HashMap<>(), new HashMap<>());
        entrantListView.setAdapter(entrantArrayAdapter);

        // Load entrants for the event from Firestore
        loadEntrants();

        ImageButton backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> {
            // Check if the fragment manager has items in the back stack
            if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                // Pop the current fragment to return to the previous one
                getActivity().getSupportFragmentManager().popBackStack();
            } else {
                // If no fragments in the back stack, finish the activity or handle appropriately
                getActivity().finish();
            }
        });

        // Set up Draw Lottery button
        Button drawLotteryButton = view.findViewById(R.id.draw_lottery_button);
        drawLotteryButton.setOnClickListener(v -> {

            // Gets the Event object from Firestore or passed as a parameter
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("events").document(eventId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Event event = documentSnapshot.toObject(Event.class);
                        if (event != null) {
                            // Show the dialog to get the number of users to draw
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Enter number of users to draw");

                            // Set up the input field
                            final EditText input = new EditText(getContext());
                            input.setInputType(InputType.TYPE_CLASS_NUMBER);
                            builder.setView(input);

                            // Set up the dialog buttons
                            builder.setPositiveButton("Draw", (dialog, which) -> {
                                // Get the draw amount from the input
                                int drawAmount = Integer.parseInt(input.getText().toString());

                                // Call the runLottery method on the event object
                                event.runLottery(drawAmount);
                                loadEntrants();

                            });

                            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                            builder.show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle any errors when fetching the event data
                        Toast.makeText(getContext(), "Error retrieving event data", Toast.LENGTH_SHORT).show();
                    });
        });

        // Initialize the send notifications button
        Button sendNotificationsButton = view.findViewById(R.id.send_notifications_button);
        sendNotificationsButton.setOnClickListener(v -> {

            // Send notifications based on user status
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("events").document(eventId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Event event = documentSnapshot.toObject(Event.class);
                        if (event != null) {
                            // sendNotifications(db, event);
                            SendNotificationsFragment sendNotificationsFragment = SendNotificationsFragment.newInstance(event);
                            sendNotificationsFragment.show(getParentFragmentManager(), "sendNotificationsDialog");
                        }

                    });
        });

        Button viewAcceptedParticipantsButton = view.findViewById(R.id.view_participants_button);
        viewAcceptedParticipantsButton.setOnClickListener(v -> {
            EnrolledEntrantsFragment enrolledEntrantsFragment = EnrolledEntrantsFragment.newInstance(event.getEventId());
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, enrolledEntrantsFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    /**
     * loads entrants from firebase
     */
    private void loadEntrants() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Event event = documentSnapshot.toObject(Event.class);
                    if (event != null) {
                        // Get the maps of users for waitlisted, invited, and cancelled
                        usersWaitlist = event.getUsersWaitlisted();
                        usersSentInvite = event.getUsersInvited();
                        usersCancelled = event.getUsersCancelled();

                        // Create a combined list of users to show in the list
                        entrantList.clear();
                        entrantList.addAll(usersWaitlist.values());
                        entrantList.addAll(usersSentInvite.values());
                        entrantList.addAll(usersCancelled.values());

                        // Update the adapter's maps and refresh
                        entrantArrayAdapter.updateMaps(usersWaitlist, usersSentInvite, usersCancelled);
                        entrantArrayAdapter.notifyDataSetChanged();
                    }
                });
    }

    /**
     * This method sends notifications to users based on their status in the event.
     * @param db The firestore database
     * @param event The event being used
     */
    private void sendNotifications(FirebaseFirestore db, Event event) {
        if (event == null) {
            Log.e("EntrantListFragment", "Event object is null");
            return;
        }

        // Loop through different user status categories (waitlisted, invited, cancelled)
        event.notifyUserByStatus(db, "waitlisted");
        event.notifyUserByStatus(db, "invited");
        event.notifyUserByStatus(db, "cancelled");

        // You can optionally show a toast message after notifications are sent
        Toast.makeText(getContext(), "Notifications sent!", Toast.LENGTH_SHORT).show();
    }
}