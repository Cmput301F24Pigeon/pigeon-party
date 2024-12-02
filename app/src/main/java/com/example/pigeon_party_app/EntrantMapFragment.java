package com.example.pigeon_party_app;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Map;

/**
 * This fragment is used to show a map of entrants sign up location
 */
public class EntrantMapFragment extends Fragment {
    public ImageView worldMap;
    public FrameLayout markerContainer;
    public String eventId;

    public EntrantMapFragment() {
        // Required empty public constructor
    }

    /**
     * newInstance method creates a mock fragment for testing
     *
     * @return EntrantMapFragment the mock fragment being used for testing
     */
    public static EntrantMapFragment newInstance(String eventId) {
        EntrantMapFragment fragment = new EntrantMapFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrant_map, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ImageButton backButton = view.findViewById(R.id.button_back);

        worldMap = view.findViewById(R.id.imageViewBackground);
        markerContainer = view.findViewById(R.id.marker_container);

        worldMap.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            // Fetch the entrantsLocation map from Firestore
            db.collection("events").document(eventId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists() && documentSnapshot.contains("entrantsLocation")) {
                            // Get the entrantsLocation map
                            Map<String, Object> entrantsLocation = (Map<String, Object>) documentSnapshot.get("entrantsLocation");

                            if (entrantsLocation != null) {
                                for (Map.Entry<String, Object> entry : entrantsLocation.entrySet()) {
                                    String userName = entry.getKey(); // User ID or name
                                    GeoPoint location = (GeoPoint) entry.getValue(); // GeoPoint value

                                    if (location != null) {
                                        double latitude = location.getLatitude();
                                        double longitude = location.getLongitude();

                                        // Add marker for this user
                                        addMarker(latitude, longitude, userName);
                                    }
                                }
                            }
                        } else {
                            Log.d("Firestore", "No entrantsLocation data found.");
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Firestore", "Error fetching event data", e));
        });
        backButton.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new OrganizerFragment())
                    .addToBackStack(null)
                    .commit();
        });
        return view;

    }

    /**
     * This method puts markers on the map image to show users locations
     *
     * @param latitude        The users latitude
     * @param longitude       The users longitude
     * @param participantName The name of the user
     */
    public void addMarker(double latitude, double longitude, String participantName) {

        int x = (int) ((longitude + 180) / 360 * worldMap.getWidth());
        int y = (int) ((90 - latitude) / 180 * worldMap.getHeight());

        ImageView marker = new ImageView(requireContext());
        marker.setImageResource(R.drawable.vector_marker);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(20, 20);  // Marker size (width, height)
        params.leftMargin = x - 15;
        params.topMargin = y - 15;
        marker.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Participant: " + participantName, Toast.LENGTH_SHORT).show()
        );
        markerContainer.addView(marker, params);
    }
}