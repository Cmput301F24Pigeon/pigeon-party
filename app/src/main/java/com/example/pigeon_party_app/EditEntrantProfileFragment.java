package com.example.pigeon_party_app;

import static java.security.AccessController.getContext;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class EditEntrantProfileFragment extends Fragment {

    public User entrant;

    public EditEntrantProfileFragment() {}

    public EditEntrantProfileFragment(User entrant) {
        this.entrant = entrant;
    }

    @NonNull
    public Dialog onCreateView(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_edit_entrant_profile, null);

        EditText editEntrantName = view.findViewById(R.id.editText_edit_user_name);
        EditText editEntrantEmail = view.findViewById(R.id.editText_edit_user_email);
        EditText editEntrantPhoneNumber = view.findViewById(R.id.editText_edit_user_phone);

        editEntrantName.setText(entrant.getName());
        editEntrantEmail.setText(entrant.getEmail());
        editEntrantPhoneNumber.setText(entrant.getPhoneNumber());

        ImageButton backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ViewEntrantProfileFragment.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        });

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
