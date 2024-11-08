package com.example.pigeon_party_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EntrantListFragment extends Fragment {
    private static final String ARG_EVENT_ID = "eventId";
    private String eventId;
    private ListView entrantListView;
    private ArrayAdapter<String> entrantArrayAdapter; // Use a custom adapter if needed
    private ArrayList<String> entrantList = new ArrayList<>();

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
        entrantArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, entrantList);
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

        return view;
    }

    private void loadEntrants() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Event event = documentSnapshot.toObject(Event.class);
                    if (event != null) {
                        // Assuming `getUsersWaitlisted()` returns a Map<String, String> where keys are user IDs
                        entrantList.addAll(event.getUsersWaitlisted().keySet());
                        entrantArrayAdapter.notifyDataSetChanged();
                    }
                });
    }
}

