package com.example.pigeon_party_app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple dialog fragment that allows an organizer to choose which pparticipants to send a notification to for a certain event.
 * Use the {@link SendNotificationsFragment} factory method to
 * create an instance of this fragment.
 */
public class SendNotificationsFragment extends DialogFragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Event event;
    public SendNotificationsFragment() {
        // Required empty public constructor
    }


    public static SendNotificationsFragment newInstance(Event event) {
        SendNotificationsFragment fragment = new SendNotificationsFragment();
        Bundle args = new Bundle();
        args.putSerializable("event", event); // Ensure Event implements Parcelable
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("event"); // Retrieve the Event object
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_send_notifications, null);

        builder.setView(view)
                .setTitle("Send Notifications")
                .setPositiveButton("Confirm", (dialog, id) -> {
                    CheckBox waitlisted = view.findViewById(R.id.check_waitlist);
                    CheckBox selected = view.findViewById(R.id.check_selected);
                    CheckBox cancelled = view.findViewById(R.id.check_cancelled);
                    if (waitlisted.isChecked()) {
                        event.notifyUserByStatus("waitlisted");
                    }
                    if (selected.isChecked()) {
                        event.notifyUserByStatus("selected");
                    }
                    if (cancelled.isChecked()) {
                        event.notifyUserByStatus("cancelled");
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    dialog.dismiss();
                });

        return builder.create();
    }

}