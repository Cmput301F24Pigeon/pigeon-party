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
        // Required empty public constructor
    }

    /**
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return view The view of the fragment
     */
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
        User current_user = MainActivity.getCurrentUser();
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
                    currentFacility.setAddress(editFacilityAddress.getText().toString());
                    currentFacility.setName(editFacilityName.getText().toString());
                    Map<String, Object> facilityUpdates = new HashMap<>();
                    facilityUpdates.put("facility.name", currentFacility.getName());
                    facilityUpdates.put("facility.address", currentFacility.getAddress());

                    db.collection("user").document(uniqueId)
                            .update(facilityUpdates)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Firestore", "User's facility successfully updated");

                                // Change the button text back to "Edit Facility" after a successful update
                                updateProfileButton.setText("Edit Facility");

                                // Disable editing after confirming
                                editFacilityAddress.setFocusable(false);
                                editFacilityAddress.setFocusableInTouchMode(false);
                                editFacilityName.setFocusable(false);
                                editFacilityName.setFocusableInTouchMode(false);
                                isEditing = false; // Reset state
                            })
                            .addOnFailureListener(e -> Log.w("Firestore", "Error updating user's facility", e));
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
}