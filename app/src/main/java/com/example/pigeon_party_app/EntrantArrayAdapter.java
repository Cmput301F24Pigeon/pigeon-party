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
import java.util.HashMap;
import java.util.Map;

public class EntrantArrayAdapter extends ArrayAdapter<User> {
    private ArrayList<User> users;
    private Map<String, User> waitlist = new HashMap<>();
    private Map<String, User> invited = new HashMap<>();;
    private Map<String, User> cancelled = new HashMap<>();;
    private Map<String, User> sentInvite = new HashMap<>();;

    private Context context;

    public EntrantArrayAdapter(Context context, ArrayList<User> users,
                               Map<String, User> waitlist,
                               Map<String, User> sentInvite,
                               Map<String, User> cancelled) {
        super(context, 0, users);
        this.users = users;
        this.context = context;
        this.waitlist = waitlist;
        this.sentInvite = sentInvite;
        this.cancelled = cancelled;
    }

    public EntrantArrayAdapter(Context context, ArrayList<User> users, Map<String, User> invited) {
        super(context, 0, users);
        this.context = context;
        this.users = users;
        this.invited = invited;
    }

    // Method to update maps
    public void updateMaps(Map<String, User> waitlist, Map<String, User> sentInvite, Map<String, User> cancelled) {
        this.waitlist = waitlist;
        this.sentInvite = sentInvite;
        this.cancelled = cancelled;
    }

    public void updateUsersInvited(Map<String, User> invited){
        this.invited = invited;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.entrant_content, parent, false);
        }

        User user = users.get(position);

        // Initialize avatars
        AvatarView avatar = view.findViewById(R.id.entrant_profile_image);
        avatar.setUser(user);

        // Initialize TextViews
        TextView name = view.findViewById(R.id.name);
        TextView entrantStatus = view.findViewById(R.id.entrantStatus);

        // Set the user's name
        name.setText(user.getName());

        // Determine the user's status based on the map they belong to
        if (waitlist.containsKey(user.getUniqueId())) {
            entrantStatus.setText("Waitlisted");
        } else if (sentInvite.containsKey(user.getUniqueId())) {
            entrantStatus.setText("Invited");
        } else if (cancelled.containsKey(user.getUniqueId())) {
            entrantStatus.setText("Cancelled");
        }
         if (invited.containsKey(user.getUniqueId())) {
             entrantStatus.setText("Joined");
        }

        return view;
    }
}
