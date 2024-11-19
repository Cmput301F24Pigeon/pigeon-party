package com.example.pigeon_party_app;

import static com.example.pigeon_party_app.MainActivity.db;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

/**
 * This class is a fragment that allows the user to change their profile information
 */

public class EditEntrantProfileFragment extends Fragment {

    public User entrant;

    public static String TAG = "EditEntrantProfileFragment";

    public EditEntrantProfileFragment() {}

    @SuppressLint("ValidFragment")
    public EditEntrantProfileFragment(User entrant) {
        this.entrant = entrant;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_entrant_profile, null);

        EditText editEntrantName = view.findViewById(R.id.editText_edit_user_name);
        EditText editEntrantEmail = view.findViewById(R.id.editText_edit_user_email);
        EditText editEntrantPhoneNumber = view.findViewById(R.id.editText_edit_user_phone);

        editEntrantName.setText(entrant.getName());
        editEntrantEmail.setText(entrant.getEmail());
        editEntrantPhoneNumber.setText(entrant.getPhoneNumber());

        Button edit_button = view.findViewById(R.id.update_user_profile_button);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean isValid = true;
                if (!Validator.isName(editEntrantName, "Your profile must include a name.")) {
                    isValid = false;
                }
                if (!Validator.isEmail(editEntrantEmail, "Your profile must have a valid email.")) {
                    isValid = false;
                }
                if (!Validator.isPhoneNumber(editEntrantPhoneNumber, "Your phone number must be 10 digits or empty.")) {
                    isValid = false;
                }
                if (isValid) {
                    String entrantName = editEntrantName.getText().toString();
                    String entrantEmail = editEntrantEmail.getText().toString();
                    String entrantPhoneNumber = editEntrantPhoneNumber.getText().toString();

                    entrant.setName(entrantName);
                    entrant.setEmail(entrantEmail);
                    entrant.setPhoneNumber(entrantPhoneNumber);
                    updateUserProfile(entrant);

                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new ViewEntrantProfileFragment(entrant))
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        ImageButton backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ViewEntrantProfileFragment(entrant))
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    /**
     * Function updates a user's name, email and phone in their profile in Firebase
     * @param entrant The User object that has been changed through the app's UI
     */
    public void updateUserProfile(User entrant) {
        String entrantId = entrant.getUniqueId();

        // Update syntax from Firebase docs: https://firebase.google.com/docs/firestore/manage-data/add-data#java_10
        DocumentReference entrantRef = db.collection("user").document(entrantId);

        entrantRef.update("name", entrant.getName(), "email", entrant.getEmail(), "phoneNumber", entrant.getPhoneNumber())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    /**
     * newInstance method creates a mock fragment for testing
     * @return CreateEntrantProfileFragment the mock fragment being used for testing
     */
    public static EditEntrantProfileFragment newInstance(User testUser) {
        EditEntrantProfileFragment fragment = new EditEntrantProfileFragment(testUser);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
}
