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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrant_list, container, false);
        entrantListView = view.findViewById(R.id.entrant_list_view);
        // Set up adapter
        entrantArrayAdapter = new EntrantArrayAdapter(getContext(), entrantList, new HashMap<>(), new HashMap<>(), new HashMap<>());
        entrantListView.setAdapter(entrantArrayAdapter);

        // Loads entrants for the event from Firestore
        loadEntrants();

        ImageButton backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> {
            // Check if the fragment manager has items in the back stack
            if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getActivity().getSupportFragmentManager().popBackStack();
            } else {
                getActivity().finish();
            }
        });

        // Set up Draw Lottery button
        Button drawLotteryButton = view.findViewById(R.id.draw_lottery_button);
        drawLotteryButton.setOnClickListener(v -> {

            // Gets the Event object from Firestore or passed as a parameter
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("events").document(eventId).get().addOnSuccessListener(documentSnapshot -> {
                Event event = documentSnapshot.toObject(Event.class);
                if (event != null) {
                    // Show the dialog to get the number of users to draw
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Enter number of users to draw");

                    // input field
                    final EditText input = new EditText(getContext());
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    builder.setView(input);

                    builder.setPositiveButton("Draw", (dialog, which) -> {
                        int drawAmount = Integer.parseInt(input.getText().toString());

                        // Call the runLottery method on the event object
                        event.runLottery(drawAmount);
                        loadEntrants();

                    });

                    builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                    builder.show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Error retrieving event data", Toast.LENGTH_SHORT).show();
            });
        });

        // to send notifications
        Button sendNotificationsButton = view.findViewById(R.id.send_notifications_button);
        sendNotificationsButton.setOnClickListener(v -> {

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("events").document(eventId).get().addOnSuccessListener(documentSnapshot -> {
                Event event = documentSnapshot.toObject(Event.class);
                if (event != null) {
                    SendNotificationsFragment sendNotificationsFragment = SendNotificationsFragment.newInstance(event);
                    sendNotificationsFragment.show(getParentFragmentManager(), "sendNotificationsDialog");
                }

            });
        });

        // to view accepted participants
        Button viewAcceptedParticipantsButton = view.findViewById(R.id.view_participants_button);
        viewAcceptedParticipantsButton.setOnClickListener(v -> {
            EnrolledEntrantsFragment enrolledEntrantsFragment = EnrolledEntrantsFragment.newInstance(event.getEventId());
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, enrolledEntrantsFragment).addToBackStack(null).commit();
        });

        return view;
    }

    /**
     * loads entrants from firebase
     */
    private void loadEntrants() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventId).get().addOnSuccessListener(documentSnapshot -> {
            Event event = documentSnapshot.toObject(Event.class);
            if (event != null) {
                // Get the maps of users for waitlisted, invited, and cancelled
                usersWaitlist = event.getUsersWaitlisted();
                usersSentInvite = event.getUsersSentInvite();
                usersCancelled = event.getUsersCancelled();

                // Creates a combined list of users to show in the list
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
}