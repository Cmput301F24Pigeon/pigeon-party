package com.example.pigeon_party_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class FacilityFragment extends Fragment {

    private User current_user = new User("john doe", "johndoe@gmail.com"); //will need to add like current_user = phone id


    public FacilityFragment() {
        // Required empty public constructor
    }


   /* public static FacilityFragment newInstance(String param1, String param2) {
        FacilityFragment fragment = new FacilityFragment();
        Bundle args = new Bundle();
        args.putSerializable("user",current_user);
        fragment.setArguments(args);
        return fragment;
    }*/


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            current_user = (User) getArguments().getSerializable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_facility, container, false);

        //add an addimage button later
        Button confirmButton = view.findViewById(R.id.button_confirm);
        Button cancelButton = view.findViewById(R.id.button_cancel);
        EditText facilityAddress = view.findViewById(R.id.add_facility_address);
        EditText facilityName = view.findViewById(R.id.add_facility_name);


        confirmButton.setOnClickListener(v -> {
            //need to add validation functionality (ensure fields arent empty)
            current_user.setOrganizer(true);
            current_user.setFacilityAddress(facilityAddress.getText().toString()); //might be incorrecy
            current_user.setFacilityName(facilityName.getText().toString());

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.organizer_fragment_container, new OrganizerFragment())
                    .addToBackStack(null)
                    .commit();
        });

        cancelButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        });

        return view;
    }


}