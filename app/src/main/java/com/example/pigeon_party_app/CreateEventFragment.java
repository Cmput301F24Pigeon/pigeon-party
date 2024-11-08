package com.example.pigeon_party_app;

import static java.lang.Integer.parseInt;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * This is a fragment in which the user can create an event
 */
public class CreateEventFragment extends Fragment {
    private User current_user = MainActivity.getCurrentUser();
    private Calendar selectedDateTime = Calendar.getInstance();
    private ImageView qrCode;
    private ImageButton dateButton;
    private View qrBackground;
    private TextView eventCreatedMessage;
    private EditText dateText;

    public CreateEventFragment() {}

    /**
     * newInstance method creates a mock fragment for testing
     * @return CreateEventFragment the mock fragment being used for testing
     */
    public static CreateEventFragment newInstance(User user) {
        CreateEventFragment fragment = new CreateEventFragment();
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

    private DatePickerDialog datePickerDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);
        dateButton = view.findViewById(R.id.button_date_picker);
        dateText = view.findViewById(R.id.text_event_date);

        //add an addimage button later
        Button createEventButton = view.findViewById(R.id.button_create_event);
        ImageButton backButton = view.findViewById(R.id.button_back);
        EditText eventTitle = view.findViewById(R.id.edit_event_title);
        String eventAddress = current_user.getFacility().getAddress();
        EditText eventDetails = view.findViewById(R.id.edit_event_details);
        EditText waitlistCap = view.findViewById(R.id.edit_waitlist_cap);
        Switch requiresLocation = view.findViewById(R.id.switch_require_location);
        qrCode = view.findViewById(R.id.eventQrcode);
        qrBackground= view.findViewById(R.id.background_view);
        eventCreatedMessage = view.findViewById(R.id.text_event_created);

        dateButton.setOnClickListener(v -> showDateTimePicker());



        createEventButton.setOnClickListener(v -> {
            boolean isValid = true;
            if (Validator.isEmpty(dateText, "Please select a date and time.")) {
                isValid = false;
            }
            if (Validator.isEmpty(eventDetails, "Event details cannot be empty")) {
                isValid = false;
            }
            if (Validator.isEmpty(eventTitle, "Event title cannot be empty")){
                isValid = false;
            }
            if (isValid) {
                String eventId = UUID.randomUUID().toString();



                if (waitlistCap.getText().toString().isEmpty()){
                    waitlistCap.setText("-1");
                }
                Date eventDateTime = selectedDateTime.getTime();

                //Event event = new Event(eventId,eventTitle.getText().toString(),eventDateTime,Integer.parseInt(waitlistCap.getText().toString()),eventDetails.getText().toString(),eventFacility, requiresLocation.isChecked());
                Map<String, Map<String, Object>> usersWaitlist = new HashMap<>();
                Map<String, Map<String, Object>> usersInvited = new HashMap<>();
                Map<String, Map<String, Object>> usersCancelled = new HashMap<>();
                Event event = new Event(eventId,eventTitle.getText().toString(),eventDateTime,Integer.parseInt(waitlistCap.getText().toString()),eventDetails.getText().toString(),current_user.getFacility(), requiresLocation.isChecked(), usersWaitlist, usersInvited, usersCancelled, current_user);
                qrBackground.setVisibility(View.VISIBLE);
                eventCreatedMessage.setVisibility(View.VISIBLE);
                generateQRCode(eventId);

                addEvent(db,event);
                createEventButton.setText("Finish");
                createEventButton.setOnClickListener(v2->{
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new OrganizerFragment()) // Change fragment_container to your actual container
                            .addToBackStack(null)
                            .commit();

                });

            }
        });

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
     * This is a method to show the date and time to be chosen by the user
     */
    private void showDateTimePicker(){
        showDatePicker();
    }

    /**
     * This is a method that formats the selected date and time to be displayed to the user
     */
    private void displaySelectedDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String dateString = dateFormat.format(selectedDateTime.getTime());
        dateText.setText(dateString);
    }

    /**
     * This is a method to show the calendar to the user to select the date
     */
    private void showDatePicker(){
        int year = selectedDateTime.get(Calendar.YEAR);
        int month = selectedDateTime.get(Calendar.MONTH);
        int day = selectedDateTime.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDateTime.set(selectedYear, selectedMonth, selectedDay);
                    showTimePicker();
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    /**
     * This is a method to show the clock to the user to select the time
     */
    private void showTimePicker() {
        int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY);
        int minute = selectedDateTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                (view, selectedHour, selectedMinute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                    selectedDateTime.set(Calendar.MINUTE, selectedMinute);
                    displaySelectedDateTime();
                },
                hour, minute, true
        );
        timePickerDialog.show();
    }

    /**
     * This generates a qr code based on a string
     * @param text The text which the qr code will represent (in this case event id)
     */
    //possibly add option to save qr code to phone (implement later)
    private void generateQRCode(String text){
    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
        Bitmap bitmap = barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 500, 500);
        qrCode.setImageBitmap(bitmap);
    } catch (WriterException e) {
        e.printStackTrace();
    }
}

    /**
     * This adds an event to the firebase
     * @param db The firebase database
     * @param event The event
     */
    public void addEvent(@NonNull FirebaseFirestore db, Event event){
        db.collection("events").document(event.getEventId())
                .set(event)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FireStore", "Event successfully added");
                })
                .addOnFailureListener(e ->{
                    Log.w("FireStore", "Error adding event", e);
                });
        Map<String, Object> updates = new HashMap<>();

        current_user.addOrganizerEventList(event);
        updates.put("organizerEventList", current_user.getOrganizerEventList());
        db.collection("user").document(current_user.getUniqueId())
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User's facility successfully updated"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating user's oranizer list", e));
    }


}