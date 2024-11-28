package com.example.pigeon_party_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class OrganizerArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;


    public OrganizerArrayAdapter(Context context, ArrayList<Event> events){
        super(context,0, events);
        this.events = events;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.organizer_content, parent,false);
        }

        Event event = events.get(position);
        ImageButton editButton = view.findViewById(R.id.button_edit);
        ImageButton mapButton = view.findViewById(R.id.button_map);
        if (event.isRequiresLocation()){
            mapButton.setVisibility(View.VISIBLE);
        } else {
            mapButton.setVisibility(View.GONE);
        }
        // Initialize TextViews
        TextView eventTitle = view.findViewById(R.id.eventTitle);
        TextView eventDetails = view.findViewById(R.id.eventDetails);
        TextView eventDate = view.findViewById(R.id.eventDate);

        // Set event title and details
        eventTitle.setText(event.getTitle());
        eventDetails.setText(event.getDetails());

        // Format the date
        Date date = event.getDateTime(); // or event.getDateTime().toDate() if using Firebase Timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault());
        String formattedDate = dateFormat.format(date);
        eventDate.setText(formattedDate);
        editButton.setOnClickListener(v -> {
            EditEventFragment editEventFragment = EditEventFragment.newInstance(event.getEventId());

            ((FragmentActivity) getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, editEventFragment)
                    .addToBackStack(null)
                    .commit();
        });

        mapButton.setOnClickListener(v -> {
            EntrantMapFragment mapFragment = EntrantMapFragment.newInstance(event.getEventId());

            ((FragmentActivity) getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, mapFragment)
                    .addToBackStack(null)
                    .commit();
        });


        return view;


    }



}