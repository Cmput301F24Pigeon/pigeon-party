package com.example.pigeon_party_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * This class adapts our array for users when we want to browse profiles
 */
public class AdminUserArrayAdapter extends ArrayAdapter<User> {
    private ArrayList<User> users;
    private Context context;


    public AdminUserArrayAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
        this.users = users;
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.profile_content, parent, false);
        }

        User user = users.get(position);


        // Initialize TextViews
        TextView name = view.findViewById(R.id.name);
        TextView id = view.findViewById(R.id.id);
        TextView email = view.findViewById(R.id.email);
        TextView phoneNumber = view.findViewById(R.id.phonenumber);
        TextView facility = view.findViewById(R.id.Facility);


        // Set user info
        name.setText(user.getName());
        id.setText(user.getUniqueId());
        email.setText(user.getEmail());
        phoneNumber.setText(user.getPhoneNumber());
        if (user.isOrganizer()) {
            facility.setText("Facility: " + user.getFacility().getName());
        } else {
            facility.setText("No facilities");
        }

        return view;


    }


}
