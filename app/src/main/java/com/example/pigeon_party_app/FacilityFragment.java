package com.example.pigeon_party_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;


public class FacilityFragment extends Fragment {

    private User current_user = new User("john doe", "johndoe@gmail.com", null, true, true); //will need to add like current_user = phone id


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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //add an addimage button later
        Button confirmButton = view.findViewById(R.id.button_confirm);
        Button cancelButton = view.findViewById(R.id.button_cancel);
        EditText facilityAddress = view.findViewById(R.id.add_facility_address);
        EditText facilityName = view.findViewById(R.id.add_facility_name);


        confirmButton.setOnClickListener(v -> {
            //need to add validation functionality (ensure fields arent empty)
            current_user.setOrganizer(true);
            Facility facility = new Facility(current_user,facilityAddress.getText().toString(),facilityName.getText().toString());
            current_user.setFacility(facility);
            db.collection("facilities").document("usertest")
                    .set(facility)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("FireStore", "Facility successfully added");
                    })
                    .addOnFailureListener(e ->{
                        Log.w("FireStore", "Error adding facility", e);
                    });

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new OrganizerFragment())
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