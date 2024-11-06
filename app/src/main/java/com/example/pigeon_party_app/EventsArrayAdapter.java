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
            date.setText("Date/Time: " + event.getDateTime().toString());
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
            status.setText("Status: " + (CharSequence) event.getUsersWaitlisted().get(MainActivity.currentUser.getUniqueId()).get("status"));
        }
        else if (event.getUsersCancelled().get(MainActivity.currentUser.getUniqueId()) != null){
            status.setText("Status: " + (CharSequence) event.getUsersCancelled().get(MainActivity.currentUser.getUniqueId()).get("status"));
        }
        else if (event.getUsersInvited().get(MainActivity.currentUser.getUniqueId()) != null){
            status.setText("Status: " + (CharSequence) event.getUsersInvited().get(MainActivity.currentUser.getUniqueId()).get("status"));
        }
        else{
            status.setText("Status: ");
        }

        return view;

    }



}
