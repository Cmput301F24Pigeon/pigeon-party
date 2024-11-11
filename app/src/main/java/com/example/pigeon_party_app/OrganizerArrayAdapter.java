package com.example.pigeon_party_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

        return view;


    }



}