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
import java.util.Map;

public class EntrantArrayAdapter extends ArrayAdapter<User> {
    private ArrayList<User> users;
    private Map<String, User> usersWaitlisted;
    private Map<String, User> usersInvited;
    private Map<String, User> usersCancelled;
    private Context context;

    public EntrantArrayAdapter(Context context, ArrayList<User> users,
                               Map<String, User> usersWaitlisted,
                               Map<String, User> usersInvited,
                               Map<String, User> usersCancelled) {
        super(context, 0, users);
        this.users = users;
        this.context = context;
        this.usersWaitlisted = usersWaitlisted;
        this.usersInvited = usersInvited;
        this.usersCancelled = usersCancelled;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.organizer_content, parent, false);
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
        if (usersWaitlisted.containsKey(user.getUniqueId())) {
            entrantStatus.setText("Waitlisted");
        } else if (usersInvited.containsKey(user.getUniqueId())) {
            entrantStatus.setText("Invited");
        } else if (usersCancelled.containsKey(user.getUniqueId())) {
            entrantStatus.setText("Cancelled");
        }

        return view;
    }
}
