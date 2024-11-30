
package com.example.pigeon_party_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is a fragment that shows the user event details after the user scans the QR Code, also
 * allows the user to sign up for the event if the waitlist is not full
 */
public class EventDetailsFragment extends Fragment {
    private TextView eventTitle;
    private TextView eventDateTime;
    //private TextView eventLocation;
    private TextView eventDetails;
    private TextView eventCapacity;
    Event event = MainActivity.getCurrentEvent();
    User current_user = MainActivity.getCurrentUser();
    private Button signUpButton;
    private Button drawParticipantsButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private LocationManager locationManager;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ImageView eventPoster;


    public EventDetailsFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        eventTitle = view.findViewById(R.id.eventName);
        eventDateTime = view.findViewById(R.id.eventDateTime);
        //eventLocation = view.findViewById(R.id.eventLocation);
        eventDetails = view.findViewById(R.id.eventDetails);
        eventCapacity = view.findViewById(R.id.eventCapacity);
        signUpButton = view.findViewById(R.id.signupButton);
        eventPoster = view.findViewById(R.id.eventPoster);
        drawParticipantsButton = view.findViewById(R.id.drawParticipantsButton);
        drawParticipantsButton.setEnabled(false);
        Format formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm");

        eventTitle.setText(event.getTitle());
        eventDateTime.setText("Date/Time: " + formatter.format(event.getDateTime()));
        //eventLocation.setText(event.getLocation());
        eventDetails.setText("Event Details:\n" + event.getDetails());
        eventCapacity.setText("Waitlist capacity: " + String.valueOf(event.getWaitlistCapacity()));
        String imageUrl = event.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .into(eventPoster);
        }

        Buttons();
        ImageButton backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new OrganizerFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    /**
     * This method handles everything to do with the buttons: for signing up: setting the text,firebase
     * integration, getting the users location, and updating the event's waitlist
     * for drawing participants: setting the visibility of the button, handling click, and sending
     * info to the next fragment
     */
    // https://stackoverflow.com/questions/51737667/since-the-android-getfragmentmanager-api-is-deprecated-is-there-any-alternati
    private void Buttons() {

        if(current_user.getUniqueId().equals(event.getFacility().getOwnerId())){
            signUpButton.setVisibility(View.INVISIBLE);
            signUpButton.setEnabled(false);

            drawParticipantsButton.setVisibility(View.VISIBLE);
            drawParticipantsButton.setEnabled(true);
            drawParticipantsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EntrantListFragment entrantListFragment = EntrantListFragment.newInstance(event.getEventId());

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, entrantListFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }

        DocumentReference eventRef = FirebaseFirestore.getInstance()
                .collection("events")
                .document(event.getEventId());

        eventRef.collection("usersWaitlisted").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int currentSize = task.getResult().size(); // Get the number of users in the waitlist
                int waitlistCapacity = event.getWaitlistCapacity();

                if (waitlistCapacity > 0 && currentSize >= waitlistCapacity) {
                    signUpButton.setText("Waitlist is Full");
                    signUpButton.setEnabled(false);
                } else {
                    signUpButton.setOnClickListener(v -> {
                        if (event.isRequiresLocation()) {
                            requestLocationAndSignUp();
                            } else {
                                addToWaitlist();
                            }


                        getActivity().getSupportFragmentManager().popBackStack();

                    });
                }

            } else {
                System.err.println("Error getting waitlist size: " + task.getException());
            }
        });
    }

    /**
     * This method is used to request the users location and then sign up
     */
    private void requestLocationAndSignUp() {
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            getLocationAndSignUp();
        }
    }
    /**
     * This method gets the users current location
     */
    private void getLocationAndSignUp() {
        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null) {

            if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Location permissions are required to sign up.", Toast.LENGTH_SHORT).show();
                return;
            }
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Log.d("Location", "Latitude: " + latitude + ", Longitude: " + longitude);
                    GeoPoint geoPoint = new GeoPoint(latitude,longitude);

                    // Update the Firestore database
                    db.collection("events").document(event.getEventId())
                            .update("entrantsLocation." + current_user.getName(), geoPoint)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Firestore", "Location updated successfully");
                            })
                            .addOnFailureListener(e -> Log.w("Firestore", "Error updating event's waitlist", e));

                    addToWaitlist();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}
                @Override
                public void onProviderEnabled(String provider) {}
                @Override
                public void onProviderDisabled(String provider) {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Location Disabled")
                            .setMessage("Please enable location services to sign up for this event.")
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .show();
                }

            }, Looper.getMainLooper());
        }
    }

    /**
     * This method creates an alert dialog requiring location from user
     * @param requestCode The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationAndSignUp();
            } else {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Permission Required")
                        .setMessage("You must allow location access to sign up for this event.")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        }
    }
    /**
     * Adds the user to the corresponding event's waitlist in firebase
     * @param updates
     * @param list
     */
    public void updateFirebase(Map<String, Object> updates, String list){
        String msg = String.format("Event's %s successfully updated", list);
        db.collection("events").document(event.getEventId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", msg);
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating event's waitlist", e));
        updates.put("entrantEventList", current_user.getEntrantEventList());
        db.collection("user").document(current_user.getUniqueId())
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User's facility successfully updated"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating user's entrant list", e));
    }


    /**
     * A method to add a user to the events waitlist upon signing up for the event
     */
    private void addToWaitlist() {
        handleUserCancellation();
        event.addUserToWaitlist(current_user);
        current_user.addEntrantEventList(event.getEventId());
        MainActivity.addEventToList(event);
        Map<String, Object> updates = event.updateFirebaseEventWaitlist(event);
        updateFirebase(updates, "waitlist");
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
        Toast.makeText(requireContext(), "Sign-up successful!", Toast.LENGTH_SHORT).show();
    }

    /**
     * A method that will remove a user from the cancelled list if they choose to resign up for an event
     */
    private void handleUserCancellation() {
        if (event.getUsersCancelled().containsKey(current_user.getUniqueId())) {
            event.removeUserFromCancelledList(current_user);
            Map<String, Object> updates = event.updateFirebaseEventCancelledList(event);
            updateFirebase(updates, "cancelled list");
        }
    }
}
