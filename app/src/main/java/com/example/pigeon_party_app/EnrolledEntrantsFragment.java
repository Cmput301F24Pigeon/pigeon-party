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
    private ListView entrantListView;
    private ArrayAdapter<User> entrantArrayAdapter;
    private ArrayList<User> entrantList = new ArrayList<>();
    private Map<String, User> usersInvited;

    public static EnrolledEntrantsFragment newInstance(Map<String, User> usersInvited) {
        EnrolledEntrantsFragment fragment = new EnrolledEntrantsFragment();
        Bundle args = new Bundle();
        args.putSerializable("usersInvited", (Serializable) usersInvited);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            usersInvited = (Map<String, User>) getArguments().getSerializable("usersInvited");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enrolled_entrants, container, false);
        entrantListView = view.findViewById(R.id.entrant_list_view);
        // Set up adapter
        entrantArrayAdapter = new EntrantArrayAdapter(getContext(), entrantList, usersInvited);
        entrantListView.setAdapter(entrantArrayAdapter);


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
}
