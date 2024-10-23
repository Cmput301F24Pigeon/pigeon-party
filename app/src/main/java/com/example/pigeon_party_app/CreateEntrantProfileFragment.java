package com.example.pigeon_party_app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class CreateEntrantProfileFragment extends DialogFragment {

    public User entrant;

    private EditText createEntrantName;
    private EditText createEntrantEmail;
    private EditText createEntrantPhone;

    private CreateEntrantProfileDialogListener listener;

    public CreateEntrantProfileFragment(User entrant) {
        this.entrant = entrant;
    }

    public CreateEntrantProfileFragment() {}

    interface CreateEntrantProfileDialogListener {
        void createEntrantProfile(User entrant);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CreateEntrantProfileDialogListener) {
            listener = (CreateEntrantProfileDialogListener) context;
        } else {
            throw new RuntimeException(context + " must implement CreateEntrantProfileDialogListener");
        }
    }

    @NonNull
    public Dialog OnCreateDialog( Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_create_entrant_profile, null);
        createEntrantName = view.findViewById(R.id.editText_create_user_name);
        createEntrantEmail = view.findViewById(R.id.editText_create_user_email);
        createEntrantPhone = view.findViewById(R.id.editText_create_user_phone);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        return builder
                .setView(view)
                .setTitle("Create profile")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Create", (dialog, which) -> {
                    String entrantName = createEntrantName.getText().toString();
                    String entrantEmail = createEntrantEmail.getText().toString();
                    String entrantPhone = createEntrantPhone.getText().toString();
                    listener.createEntrantProfile(new User(entrantName, entrantEmail, entrantPhone, false, true));
                })
                .create();
    }
}
