package com.example.pigeon_party_app;

import static java.security.AccessController.getContext;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class EditEntrantProfileFragment extends DialogFragment {

    public User entrant;

    public EditEntrantProfileFragment() {}

    public EditEntrantProfileFragment(User entrant) {
        this.entrant = entrant;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_edit_entrant_profile, null);

        EditText editEntrantName = view.findViewById(R.id.editText_edit_user_name);
        EditText editEntrantEmail = view.findViewById(R.id.editText_edit_user_email);
        EditText editEntrantPhoneNumber = view.findViewById(R.id.editText_edit_user_phone);

        editEntrantName.setText(entrant.getId());
        editEntrantEmail.setText(entrant.getEmail());
        editEntrantPhoneNumber.setText(entrant.getPhoneNumber());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Edit Profile")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Update", (dialog, which) -> {

                    String entrantName = editEntrantName.getText().toString();
                    String entrantEmail = editEntrantEmail.getText().toString();
                    String entrantPhoneNumber = editEntrantPhoneNumber.getText().toString();

                    entrant.setName(entrantName);
                    entrant.setEmail(entrantEmail);
                    entrant.setPhoneNumber(entrantPhoneNumber);
                })
                .create();
    }
}
