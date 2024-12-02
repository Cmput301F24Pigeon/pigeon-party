package com.example.pigeon_party_app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

/**
 * The fragment which allows the user to manage their facility
 */
public class EditFacilityFragment extends Fragment {
    private User current_user = MainActivity.getCurrentUser();
    private boolean isEditing;

    public EditFacilityFragment() {
    }

    /**
     * newInstance method creates a mock fragment for testing
     *
     * @return EditFacilityFragment the mock fragment being used for testing
     */
    public static EditFacilityFragment newInstance(User user) {
        EditFacilityFragment fragment = new EditFacilityFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.fragment_edit_facility, container, false);
        EditText editFacilityName = view.findViewById(R.id.editText_edit_facility_name);
        EditText editFacilityAddress = view.findViewById(R.id.editText_edit_facility_address);
        Facility currentFacility = current_user.getFacility();
        String currentFacilityAddress = current_user.getFacility().getAddress();
        String currentFacilityName = current_user.getFacility().getName();
        editFacilityAddress.setText(currentFacilityAddress);
        editFacilityName.setText(currentFacilityName);
        String uniqueId = current_user.getUniqueId();

        Button updateProfileButton = view.findViewById(R.id.edit_facility_button);
        ImageButton backButton = view.findViewById(R.id.button_back);


        updateProfileButton.setOnClickListener(v -> {
            if (!isEditing) {
                // Switch to editing mode
                editFacilityAddress.setFocusableInTouchMode(true);
                editFacilityAddress.setFocusable(true);
                editFacilityName.setFocusableInTouchMode(true);
                editFacilityName.setFocusable(true);

                updateProfileButton.setText("Confirm");
                isEditing = true; // Update state to editing
            } else {
                // Confirm changes
                boolean isValid = true;
                if (Validator.isEmpty(editFacilityAddress, "Your facility must have an address.")) {
                    isValid = false;
                }
                if (Validator.isEmpty(editFacilityName, "Your facility must have a name.")) {
                    isValid = false;
                }
                if (isValid) {
                    String facilityAddress = editFacilityAddress.getText().toString();
                    String facilityName = editFacilityName.getText().toString();
                    editFacility(db, currentFacility, facilityName, facilityAddress);
                    // Change the button text back to "Edit Facility" after a successful update
                    updateProfileButton.setText("Edit Facility");

                    // Disable editing after confirming
                    editFacilityAddress.setFocusable(false);
                    editFacilityAddress.setFocusableInTouchMode(false);
                    editFacilityName.setFocusable(false);
                    editFacilityName.setFocusableInTouchMode(false);
                    isEditing = false; // Reset state

                }
            }
        });

        backButton.setOnClickListener(v2 -> {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new OrganizerFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    /**
     * The method is used to edit a users facility in firebase
     *
     * @param db              The firebase database to store the user
     * @param facility        The facility object which is being updated
     * @param facilityName    The new facility name
     * @param facilityAddress The new facility address
     */
    public void editFacility(FirebaseFirestore db, Facility facility, String facilityName, String facilityAddress) {
        facility.setAddress(facilityAddress);
        facility.setName(facilityName);
        Map<String, Object> facilityUpdates = new HashMap<>();
        facilityUpdates.put("facility.name", facility.getName());
        facilityUpdates.put("facility.address", facility.getAddress());

        db.collection("user").document(facility.getOwnerId())
                .update(facilityUpdates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "User's facility successfully updated");
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating user's facility", e));

    }
}