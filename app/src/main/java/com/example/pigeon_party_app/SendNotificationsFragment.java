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
 * A simple dialog fragment that allows an organizer to choose which participants to send a notification to for a certain event.
 */
public class SendNotificationsFragment extends DialogFragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Event event;

    public SendNotificationsFragment() {
    }

    /**
     * Creates a SendNotificationsFragment for testing
     *
     * @param event the Event object that the notifications are for
     */
    public static SendNotificationsFragment newInstance(Event event) {
        SendNotificationsFragment fragment = new SendNotificationsFragment();
        Bundle args = new Bundle();
        args.putSerializable("event", event);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("event");
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
                    CheckBox invited = view.findViewById(R.id.check_invited);
                    CheckBox accepted = view.findViewById(R.id.check_accepted);
                    CheckBox cancelled = view.findViewById(R.id.check_cancelled);
                    if (waitlisted.isChecked()) {
                        event.notifyUserByStatus(db, "waitlisted");
                    }
                    if (invited.isChecked()) {
                        event.notifyUserByStatus(db, "invited");
                    }
                    if (accepted.isChecked()) {
                        event.notifyUserByStatus(db, "accepted");
                    }
                    if (cancelled.isChecked()) {
                        event.notifyUserByStatus(db, "cancelled");
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    dialog.dismiss();
                });

        return builder.create();
    }

}