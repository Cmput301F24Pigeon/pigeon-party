package com.example.pigeon_party_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This fragment is the main page for all organizer functions (creating events, viewing/editing events)
 */
public class OrganizerFragment extends Fragment {
    private ListView organizerListView;
    private OrganizerArrayAdapter organizerArrayAdapter;
    public ArrayList<Event> organizerArrayList;
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();

    /*if (user.isOrganizer == False){
        open facility profile page
        once they enter facility user.isOrganizer = True
        cannot go to facility events page unless they create a facility profile

     */


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrganizerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrganizerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrganizerFragment newInstance(String param1, String param2) {
        OrganizerFragment fragment = new OrganizerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



   /* @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer, container, false);
        ImageButton newEventButton = view.findViewById(R.id.button_add_organizer_event);
        ImageButton editFacilityButton = view.findViewById(R.id.button_facility_profile);

        newEventButton.setOnClickListener( v-> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new CreateEventFragment())
                    .addToBackStack(null)
                    .commit();
        });

        ImageButton backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        });

        editFacilityButton.setOnClickListener( v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new EditFacilityFragment())
                    .addToBackStack(null)
                    .commit();
        });

        organizerArrayList = new ArrayList<>();
        organizerListView = view.findViewById(R.id.organizer_event_list);
        organizerArrayAdapter = new OrganizerArrayAdapter(getActivity(), organizerArrayList);
        organizerListView.setAdapter(organizerArrayAdapter);

        loadEvents(MainActivity.currentUser.getOrganizerEventList());

        //loadEventsFromFirebase();

        // Set item click listener to open entrant list
        organizerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.currentEvent = organizerArrayAdapter.getItem(position);
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new EventDetailsFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;

    }

    private void loadEventsFromFirebase() {
        if (MainActivity.currentUser == null) {
            Log.e("OrganizerFragment", "Error: currentUser is null");
            return;
        }

        Log.d("OrganizerFragment", "Loading events for user: " + MainActivity.currentUser.getUniqueId());

        // Fetch user document from Firestore
        db.collection("user").document(MainActivity.currentUser.getUniqueId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> eventMaps = (List<Map<String, Object>>) documentSnapshot.get("organizerEventList");

                        if (eventMaps != null) {
                            organizerArrayList.clear(); // Clear existing data

                            // Track the number of successfully fetched events
                            int totalEvents = eventMaps.size();
                            final int[] loadedEventsCount = {0};

                            for (Map<String, Object> eventMap : eventMaps) {
                                String eventId = (String) eventMap.get("eventId");

                                // Fetch each event by ID
                                db.collection("events").document(eventId)
                                        .get()
                                        .addOnSuccessListener(eventSnapshot -> {
                                            if (eventSnapshot.exists()) {
                                                Event event = eventSnapshot.toObject(Event.class);
                                                if (event != null) {
                                                    organizerArrayList.add(event);
                                                }
                                            } else {
                                                Log.w("OrganizerFragment", "Event document does not exist for ID: " + eventId);
                                            }

                                            // Increment loaded events count
                                            loadedEventsCount[0]++;

                                            // Check if all events have been loaded
                                            if (loadedEventsCount[0] == totalEvents) {
                                                // Notify the adapter once all events are loaded without problems
                                                organizerArrayAdapter.notifyDataSetChanged();
                                                Log.d("OrganizerFragment", "All events loaded and adapter notified.");
                                            }
                                        })
                                        .addOnFailureListener(e -> Log.e("OrganizerFragment", "Error fetching event", e));
                            }
                        } else {
                            Log.w("OrganizerFragment", "No events found for user.");
                        }
                    } else {
                        Log.w("OrganizerFragment", "User document not found.");
                    }
                })
                .addOnFailureListener(e -> Log.e("OrganizerFragment", "Error loading user data", e));
    }

    /**
     * Load the events from firebase into our list so it takes the string values and converts it into events
     * @param eventIds is a list of eventIds for our so it takes the strings from the list
     */
    private void loadEvents(ArrayList<String> eventIds){
        for (String i: eventIds){
            DocumentReference docRef = db.collection("events").document(i);

            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        organizerArrayList.add(documentSnapshot.toObject(Event.class));
                        organizerArrayAdapter.notifyDataSetChanged();
                    } else {
                        eventIds.remove(i);


                    }


                }

            });
            MainActivity.currentUser.setOrganizerEventList(eventIds);
            DocumentReference userRef = db.collection("user").document(MainActivity.currentUser.getUniqueId());
            userRef.update("organizerEventList", eventIds )
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("firebase", "DocumentSnapshot successfully deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("firebase", "Error deleting document", e);
                        }
                    });
        }

    }

}