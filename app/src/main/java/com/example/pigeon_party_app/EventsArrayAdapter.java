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
import java.util.Locale;

public class EventsArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;


    public EventsArrayAdapter(Context context, ArrayList<Event> events){
        super(context,0, events);
        this.events = events;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content, parent,false);
        }

        Event event = events.get(position);

        TextView date = view.findViewById(R.id.date);
        TextView eventName = view.findViewById(R.id.eventName);
        TextView status = view.findViewById(R.id.status);
        //address.setText(event.getFacility.getAddress);
        if(event.getDateTime() != null){
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
            String formattedDate = formatter.format(event.getDateTime());
            date.setText("Date/Time: " + formattedDate);;
        }
        else{
            date.setText("Date/Time: ");
        }

        if(event.getTitle() != null){
            eventName.setText(event.getTitle());
        }
        else{
            eventName.setText("");
        }
        if(event.getUsersWaitlisted().get(MainActivity.currentUser.getUniqueId()) != null){
            status.setText("Status (Waitlisted): " + event.getUsersWaitlisted().get(MainActivity.currentUser.getUniqueId()));
        }
        else if (event.getUsersCancelled().get(MainActivity.currentUser.getUniqueId()) != null){
            status.setText("Status (Cancelled): " + event.getUsersCancelled().get(MainActivity.currentUser.getUniqueId()));
        }
        else if (event.getUsersInvited().get(MainActivity.currentUser.getUniqueId()) != null){
            status.setText("Status (Invited): " + event.getUsersInvited().get(MainActivity.currentUser.getUniqueId()));
        }
        else{
            status.setText("Status: ");
        }

        return view;

    }



}
