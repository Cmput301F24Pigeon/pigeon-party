package com.example.pigeon_party_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

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

        TextView date = view.findViewById(R.id.date);
        TextView eventName = view.findViewById(R.id.eventName);
        TextView waitlist = view.findViewById(R.id.waitlist);
        //address.setText(event.getFacility.getAddress);
        date.setText(event.getDateTime().toString());
        eventName.setText(event.getTitle());

        date.setText(event.getStatus());

        return view;


    }



}
