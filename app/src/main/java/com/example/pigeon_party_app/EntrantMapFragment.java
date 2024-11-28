package com.example.pigeon_party_app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EntrantMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EntrantMapFragment extends Fragment {
    private ImageView worldMap;
    private FrameLayout markerContainer;
    private String eventId;

    public EntrantMapFragment() {
        // Required empty public constructor
    }

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

        worldMap = view.findViewById(R.id.imageViewBackground);
        markerContainer = view.findViewById(R.id.marker_container);
        worldMap.getViewTreeObserver().addOnGlobalLayoutListener(() -> {

            addMarker(53, -113, "Jax");
            addMarker(50,-100,"Random");
        });

        return view;

    }
    private void addMarker(double latitude, double longitude, String participantName) {

        int x = (int) ((longitude + 180) / 360 * worldMap.getWidth());
        int y = (int) ((90 - latitude) / 180 * worldMap.getHeight());

        ImageView marker = new ImageView(requireContext());
        marker.setImageResource(R.drawable.vector_marker);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(30, 30);  // Marker size (width, height)
        params.leftMargin = x - 15;
        params.topMargin = y - 15;
        marker.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Participant: " + participantName, Toast.LENGTH_LONG).show()
        );
        markerContainer.addView(marker, params);
    }
}