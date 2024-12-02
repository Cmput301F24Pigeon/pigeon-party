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

/**
 * This class is  used to show a list of users events on main activity
 */
public class EventsArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;

    /**
     * Constructor for the EventsArrayAdapter class with arguments needed for arrayAdapter constructor
     *
     * @param context context of app
     * @param events  arrayList of events to be used in the arrayAdapter
     */
    public EventsArrayAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
        this.events = events;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content, parent, false);
        }

        Event event = events.get(position);

        TextView date = view.findViewById(R.id.date);
        TextView eventName = view.findViewById(R.id.eventName);
        TextView status = view.findViewById(R.id.status);
        TextView address = view.findViewById(R.id.address);
        address.setText(event.getFacility().getAddress());

        if (event.getDateTime() != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy 'at' HH:mm", Locale.getDefault());
            String formattedDate = formatter.format(event.getDateTime());
            date.setText(formattedDate);
        } else {
            date.setText(" ");
        }

        if (event.getTitle() != null) {
            eventName.setText(event.getTitle());
        } else {
            eventName.setText("");
        }
        if (event.getUsersWaitlisted().get(MainActivity.currentUser.getUniqueId()) != null) {
            status.setText("Status: Waitlist");
        } else if (event.getUsersCancelled().get(MainActivity.currentUser.getUniqueId()) != null) {
            status.setText("Status: Cancelled");
        } else if (event.getUsersInvited().get(MainActivity.currentUser.getUniqueId()) != null) {
            status.setText("Status: Joined");
        } else if (event.getUsersSentInvite().get(MainActivity.currentUser.getUniqueId()) != null) {
            status.setText("Status: Invited (Click to Accept)");
        } else {
            status.setText("");
        }

        return view;

    }
}
