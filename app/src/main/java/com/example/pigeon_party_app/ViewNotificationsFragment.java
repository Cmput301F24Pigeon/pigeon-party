package com.example.pigeon_party_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

/**
 * This class is a fragment that allows the user to view their notifications and to turn their notifications on or off
 */

public class ViewNotificationsFragment extends Fragment {

    public User user;

    public static String TAG = "ViewNotificationFragment";

    public ViewNotificationsFragment() {}

    public ViewNotificationsFragment(User user) {
        this.user = user;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_notifications, container, false);
        SwitchCompat notificationSwitch = view.findViewById(R.id.notification_switch);

        Log.d("Set switch", "Notification Status: " + user.hasNotificationsOn());
        if (user.hasNotificationsOn()) {
            notificationSwitch.setChecked(true);
        }

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                boolean currentNotificationStatus = user.hasNotificationsOn();
                user.setNotificationsOn(!currentNotificationStatus);
                updateUserNotificationStatus(user);
            }
        });

        ImageButton backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        });

        return view;
    }

    /**
     * This function updates the user's notification status (on/off) in Firebase
     * @param user The User object that has had the notification status changed through the app's UI
     */
    public void updateUserNotificationStatus(User user) {
        String userId = user.getUniqueId();

        // Update syntax from Firebase docs: https://firebase.google.com/docs/firestore/manage-data/add-data#java_10
        DocumentReference userRef = MainActivity.db.collection("user").document(userId);

        userRef.update("notificationStatus", user.hasNotificationsOn())
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
     * newInstance method creates a mock fragment to for testing
     * @param user The User object being passed to the fragment for testing
     * @return ViewNotificationsFragment the mock fragment being used for testing
     */
    public static ViewNotificationsFragment newInstance(User user) {
        ViewNotificationsFragment fragment = new ViewNotificationsFragment();
        Bundle args = new Bundle();
        args.putSerializable("current_user", user);
        fragment.setArguments(args);
        return fragment;
    }
}
