package com.example.pigeon_party_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * This class is a fragment that allows the user to view their profile and has a button to take them to an edit fragment
 */

public class ViewEntrantProfileFragment extends Fragment {

    public static User entrant;

    public ViewEntrantProfileFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        entrant = MainActivity.getCurrentUser();
        View view = inflater.inflate(R.layout.fragment_view_entrant_profile, container, false);
        AvatarView viewEntrantProfileImage = view.findViewById(R.id.entrant_profile_image);
        viewEntrantProfileImage.setUser(entrant);

        TextView entrantName = view.findViewById(R.id.textView_entrant_name);
        TextView entrantEmail = view.findViewById(R.id.textView_entrant_email);
        TextView entrantPhoneNumber = view.findViewById(R.id.textView_entrant_phone);

        entrantName.setText(entrant.getName());
        entrantEmail.setText(entrant.getEmail());
        entrantPhoneNumber.setText(entrant.getPhoneNumber());

        Button edit_button = view.findViewById(R.id.edit_entrant_profile_button);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new EditEntrantProfileFragment(entrant))
                        .addToBackStack(null)
                        .commit();
            }
        });

        ImageButton backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        });

        return view;
    }

    /**
     * newInstance method creates a mock fragment for testing
     *
     * @return ViewEntrantProfileFragment the mock fragment being used for testing
     */
    public static ViewEntrantProfileFragment newInstance() {
        ViewEntrantProfileFragment fragment = new ViewEntrantProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
}
