package com.example.pigeon_party_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class EnrolledEntrantsFragment extends Fragment {
    private static final String ARG_EVENT_ID = "eventId";
    private String eventId;
    private ListView entrantListView;
    private ArrayAdapter<User> entrantArrayAdapter;
    private ArrayList<User> entrantList = new ArrayList<>();

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

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enrolled_entrants, container, false);
        return view;
    }

}
