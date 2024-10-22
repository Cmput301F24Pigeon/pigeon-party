package com.example.pigeon_party_app;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

public class EventArrayAdapter  extends ArrayAdapter<Event> {
    public EventArrayAdapter(Context context, List<Event> events, boolean isOrganizer) {
        super(context, 0, events);
        this.context = context;
        this.events = events;
        this.isOrganizer = isOrganizer;
    }

}
