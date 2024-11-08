package com.example.pigeon_party_app;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import androidx.fragment.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is a dialogue fragment that allows the user to change their profile information
 * This class is part of US 01.02.02 which is for the final milestone and is not complete
 */

public class EditEntrantProfileFragment extends DialogFragment {

    public User entrant;

    public static String TAG = "EditEntrantProfileFragment";

    public EditEntrantProfileFragment() {}

    @SuppressLint("ValidFragment")
    public EditEntrantProfileFragment(User entrant) {
        this.entrant = entrant;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_edit_entrant_profile, null);

        EditText editEntrantName = view.findViewById(R.id.editText_edit_user_name);
        EditText editEntrantEmail = view.findViewById(R.id.editText_edit_user_email);
        EditText editEntrantPhoneNumber = view.findViewById(R.id.editText_edit_user_phone);

        editEntrantName.setText(entrant.getName());
        editEntrantEmail.setText(entrant.getEmail());
        editEntrantPhoneNumber.setText(entrant.getPhoneNumber());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Edit Profile")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Update", (dialog, which) -> {

                    String entrantName = editEntrantName.getText().toString();
                    String entrantEmail = editEntrantEmail.getText().toString();
                    String entrantPhoneNumber = editEntrantPhoneNumber.getText().toString();

                    entrant.setName(entrantName);
                    entrant.setEmail(entrantEmail);
                    entrant.setPhoneNumber(entrantPhoneNumber);

                    updateUserProfile(entrant);
                })
                .create();
    }

    /**
     * Function updates a user's name, email and phone in their profile in Firebase
     * @param entrant The User object that has been changed through the app's UI
     */
    public void updateUserProfile(User entrant) {
        String entrantId = entrant.getUniqueId();

        // Update syntax from Firebase docs: https://firebase.google.com/docs/firestore/manage-data/add-data#java_10
        DocumentReference entrantRef = MainActivity.db.collection("user").document(entrantId);

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
}
