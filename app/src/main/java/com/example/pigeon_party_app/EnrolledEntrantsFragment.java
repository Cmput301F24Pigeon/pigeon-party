package com.example.pigeon_party_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * This fragment shows the final list of participants selected for an event
 */
public class EnrolledEntrantsFragment extends Fragment {
    private static final String ARG_EVENT_ID = "eventId";
    private String eventId;
    private ListView entrantListView;
    private EntrantArrayAdapter entrantArrayAdapter;
    private ArrayList<User> entrantList = new ArrayList<>();
    private Map<String, User> usersSentInvite;

    public static EnrolledEntrantsFragment newInstance(String eventId) {
        EnrolledEntrantsFragment fragment = new EnrolledEntrantsFragment();
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

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enrolled_entrants, container, false);
        entrantListView = view.findViewById(R.id.entrant_list_view);
        // Set up adapter
        entrantArrayAdapter = new EntrantArrayAdapter(requireContext(), entrantList, usersSentInvite);
        entrantListView.setAdapter(entrantArrayAdapter);
        // Loads entrants who have officially joined an event
        loadJoinedEntrants();

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

        return view;
    }

    /**
     * loads entrants who have joined an event from firebase
     */
    private void loadJoinedEntrants() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Event event = documentSnapshot.toObject(Event.class);
                    if (event != null) {
                        // Get the maps of users for waitlisted, invited, and cancelled
                        usersSentInvite = event.getUsersSentInvite();

                        // Create a combined list of users to show in the list
                        entrantList.clear();
                        entrantList.addAll(usersSentInvite.values());

                        // Update the adapter's maps and refresh
                        entrantArrayAdapter.updateUsersSentInvite(usersSentInvite);
                        entrantArrayAdapter.notifyDataSetChanged();
                    }
                });
    }

}
