package com.example.pigeon_party_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fragment which prompts the user for initial facility creation in order to create events
 */
public class FacilityFragment extends Fragment {
    private User current_user = MainActivity.getCurrentUser();

    public FacilityFragment() {}

    /**
     * newInstance method creates a mock fragment for testing
     * @return FacilityFragment the mock fragment being used for testing
     */
    public static FacilityFragment newInstance(User user) {
        FacilityFragment fragment = new FacilityFragment();
        Bundle args = new Bundle();
        args.putSerializable("current_user", user);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            current_user = (User) getArguments().getSerializable("current_user");
        }
    }

   /* public static FacilityFragment newInstance(String param1, String param2) {
        FacilityFragment fragment = new FacilityFragment();
        Bundle args = new Bundle();
        args.putSerializable("user",current_user);
        fragment.setArguments(args);
        return fragment;
    }*/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_facility, container, false);
        String uniqueId = Settings.Secure.getString(requireActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //add an addimage button later
        Button confirmButton = view.findViewById(R.id.button_confirm);
        Button cancelButton = view.findViewById(R.id.button_cancel);
        EditText facilityAddress = view.findViewById(R.id.add_facility_address);
        EditText facilityName = view.findViewById(R.id.add_facility_name);

        confirmButton.setOnClickListener(v -> {
            boolean isValid = true;
            if (Validator.isEmpty(facilityAddress, "Your facility must have an address.")) {
                isValid = false;
            }
            if (Validator.isEmpty(facilityName, "Your facility must have a name.")) {
                isValid = false;
            }
            if (isValid) {
                Facility facility = new Facility(uniqueId, facilityAddress.getText().toString(), facilityName.getText().toString());
                createFacility(db,facility);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new OrganizerFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        cancelButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        });



        return view;
    }

    /**
     * Function which adds a facility to the current users document on firebase
     * @param db FireBase database
     * @param facility New facility created by user
     */
    public void createFacility(FirebaseFirestore db,  Facility facility) {
        current_user.setFacility(facility);
        ArrayList<Event> organizerList = new ArrayList<>();
        current_user.setOrganizerEventList(organizerList);

        Map<String, Object> updates = new HashMap<>();
        updates.put("facility", facility);
        updates.put("organizer", true);
        updates.put("organizerEventList", organizerList);
        db.collection("user").document(facility.getOwnerId())
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User's facility successfully updated"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating user's facility", e));
    }
}