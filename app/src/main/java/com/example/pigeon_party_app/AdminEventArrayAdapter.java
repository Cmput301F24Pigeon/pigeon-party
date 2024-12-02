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

/**
 * This array adapter allows the admin to view events in the browse admin fragment
 */
public class AdminEventArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;


    public AdminEventArrayAdapter(Context context, ArrayList<Event> events){
        super(context,0, events);
        this.events = events;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.admin_event_content, parent,false);
        }

        Event event = events.get(position);

        // Initialize TextViews
        TextView eventTitle = view.findViewById(R.id.title);
        TextView facility = view.findViewById(R.id.facility);
        TextView eventDate = view.findViewById(R.id.date);
        TextView eventId = view.findViewById(R.id.eventId);
        // Set event title and details
        eventTitle.setText(event.getTitle());
        String format = "Facility: %s";
        String facilityFormat = String.format(format, event.getFacility().getName());
        facility.setText(facilityFormat);


        // Format the date
        Date date = event.getDateTime(); // or event.getDateTime().toDate() if using Firebase Timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault());
        String formattedDate = dateFormat.format(date);
        eventDate.setText(formattedDate);
        eventId.setText(event.getEventId());

        return view;


    }

}
