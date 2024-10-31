package com.example.pigeon_party_app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.common.aliasing.qual.Unique;

import java.util.HashMap;
import java.util.Map;

public class CreateEntrantProfileFragment extends DialogFragment {

    public User entrant;

    private EditText createEntrantName;
    private EditText createEntrantEmail;
    private EditText createEntrantPhone;
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CreateEntrantProfileDialogListener listener;

    public void addUser(User user) {
        //Used https://www.youtube.com/watch?v=-w8Faojl4HI to determine unique ID
        String uniqueId = Settings.Secure.getString(requireActivity().getContentResolver(), Settings.Secure.ANDROID_ID);


        Map<String, Object> Users = new HashMap<>();

        Users.put("name", user.getName());
        Users.put("email", user.getEmail());
        Users.put("phoneNumber", user.getPhoneNumber());
        Users.put("uniqueId", user.getUniqueId());
        Users.put("entrant", user.isEntrant());
        Users.put("organizer", user.isOrganizer());
        Users.put("facility", user.getFacility());
        Users.put("notificationStatus", user.hasNotificationsOn());

        db.collection("user").document(uniqueId)
                .set(Users)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FireStore", "Facility successfully added");
                })
                .addOnFailureListener(e ->{
                    Log.w("FireStore", "Error adding facility", e);
                });


    }

    public CreateEntrantProfileFragment(User entrant) {
        this.entrant = entrant;
    }

    public CreateEntrantProfileFragment() {}

    interface CreateEntrantProfileDialogListener {
        void createEntrantProfile(User entrant);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CreateEntrantProfileDialogListener) {
            listener = (CreateEntrantProfileDialogListener) context;
        } else {
            throw new RuntimeException(context + " must implement CreateEntrantProfileDialogListener");
        }
    }


    @NonNull
    public Dialog OnCreateDialog( Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_create_entrant_profile, null);
        createEntrantName = view.findViewById(R.id.editText_create_user_name);
        createEntrantEmail = view.findViewById(R.id.editText_create_user_email);
        createEntrantPhone = view.findViewById(R.id.editText_create_user_phone);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        return builder
                .setView(view)
                .setTitle("Create profile")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Create", (dialog, which) -> {
                    String entrantName = createEntrantName.getText().toString();
                    String entrantEmail = createEntrantEmail.getText().toString();
                    String entrantPhone = createEntrantPhone.getText().toString();
                    String Id = Settings.Secure.getString(requireActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
                    listener.createEntrantProfile(new User(entrantName, entrantEmail, entrantPhone, Id, false, true, null, true));
                    addUser(new User(entrantName, entrantEmail, entrantPhone, Id, false, true, null, true));
                })
                .create();
    }
}
