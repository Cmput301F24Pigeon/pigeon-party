package com.example.pigeon_party_app;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ViewEntrantProfileFragment extends DialogFragment {

    public User entrant;

    public ViewEntrantProfileFragment() {}

    public ViewEntrantProfileFragment(User entrant) {
        this.entrant = entrant;
    }


    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_view_entrant_profile, null);

        TextView entrantName = view.findViewById(R.id.textView_entrant_name);
        TextView entrantEmail = view.findViewById(R.id.textView_entrant_email);
        TextView entrantPhoneNumber = view.findViewById(R.id.textView_entrant_phone);

        entrantName.setText(entrant.getId());
        entrantEmail.setText(entrant.getEmail());
        entrantPhoneNumber.setText(entrant.getPhoneNumber());

        final Button edit_button = view.findViewById(R.id.edit_entrant_profile_button);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EditEntrantProfileFragment().show(getFragmentManager(), "Edit Profile");
            }
        });

        return null;
    }
}
