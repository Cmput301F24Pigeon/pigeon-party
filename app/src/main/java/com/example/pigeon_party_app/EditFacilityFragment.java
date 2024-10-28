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
 * A simple {@link Fragment} subclass.
 * Use the {@link EditFacilityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditFacilityFragment extends Fragment {
    private User current_user = MainActivity.getCurrentUser();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditFacilityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditFacilityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditFacilityFragment newInstance(String param1, String param2) {
        EditFacilityFragment fragment = new EditFacilityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
        User current_user = MainActivity.getCurrentUser();
        String uniqueId = current_user.getUniqueId();

        Button updateProfileButton = view.findViewById(R.id.edit_facility_button);
        ImageButton backButton = view.findViewById(R.id.button_back);
        updateProfileButton.setOnClickListener(v->{
            currentFacility.setAddress(editFacilityAddress.getText().toString());
            currentFacility.setName(editFacilityName.getText().toString());
            Map<String, Object> facilityUpdates = new HashMap<>();
            facilityUpdates.put("facility.name", currentFacility.getName());
            facilityUpdates.put("facility.address", currentFacility.getAddress());

            db.collection("user").document(uniqueId)
                    .update(facilityUpdates)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "User's facility successfully updated"))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error updating user's facility", e));
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new OrganizerFragment()) // Change fragment_container to your actual container
                    .addToBackStack(null)
                    .commit();
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