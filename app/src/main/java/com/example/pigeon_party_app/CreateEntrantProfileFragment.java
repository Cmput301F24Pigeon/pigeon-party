package com.example.pigeon_party_app;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;

public class CreateEntrantProfileFragment extends Fragment {

    public CreateEntrantProfileFragment() {}

    public static CreateEntrantProfileFragment newInstance() {
        CreateEntrantProfileFragment fragment = new CreateEntrantProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_entrant_profile, container, false);
        EditText createEntrantName = view.findViewById(R.id.editText_create_user_name);
        EditText createEntrantEmail = view.findViewById(R.id.editText_create_user_email);
        EditText createEntrantPhone = view.findViewById(R.id.editText_create_user_phone);
        Button createProfileButton = view.findViewById(R.id.create_user_profile_button);

        createProfileButton.setOnClickListener(v -> {
            createEntrantName.setFocusableInTouchMode(true);
            createEntrantName.setFocusable(true);
            createEntrantEmail.setFocusableInTouchMode(true);
            createEntrantEmail.setFocusable(true);
            createEntrantPhone.setFocusableInTouchMode(true);
            createEntrantPhone.setFocusable(true);

            boolean isValid = false;
            if (Validator.isName(createEntrantName, "Your profile must include a name.")) {
                isValid = true;
            }
            if (Validator.isEmail(createEntrantEmail, "Your profile must have a valid email.")) {
                isValid = true;
            }
            if (Validator.isPhoneNumber(createEntrantPhone, "Your phone number must be 10 digits or empty.")) {
                isValid = true;
            }
            if (isValid) {
                User user = new User(createEntrantName.getText().toString(), createEntrantEmail.getText().toString(), createEntrantPhone.getText().toString(), null, false, true, null, true);
                addUser(user);
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }

    public void addUser(User user) {
        //Used https://www.youtube.com/watch?v=-w8Faojl4HI to determine unique ID
        String uniqueId = Settings.Secure.getString(requireActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

        user.setUniqueId(uniqueId);

        Map<String, Object> Users = new HashMap<>();

        Users.put("name", user.getName());
        Users.put("email", user.getEmail());
        Users.put("phoneNumber", user.getPhoneNumber());
        Users.put("uniqueId", user.getUniqueId());
        Users.put("entrant", user.isEntrant());
        Users.put("organizer", user.isOrganizer());
        Users.put("facility", user.getFacility());
        Users.put("notificationStatus", user.hasNotificationsOn());

        MainActivity.db.collection("user").document(uniqueId)
                .set(Users)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FireStore", "User successfully added");
                })
                .addOnFailureListener(e ->{
                    Log.w("FireStore", "Error adding user", e);
                });

    }
}
