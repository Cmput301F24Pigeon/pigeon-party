package com.example.pigeon_party_app;

import static java.lang.String.valueOf;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditEventFragment extends Fragment {
    private String eventId;
    private Calendar selectedDateTime = Calendar.getInstance();
    private ImageButton dateButton;
    private TextView eventCreatedMessage;
    private EditText dateText;
    private ImageButton uploadImage;
    private ImageView updateImagePoster;
    private Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), result -> {
                if (result != null) {
                    imageUri = result;
                    updateImagePoster.setImageURI(imageUri);
                    Log.d("Image URI", "Selected image URI: " + imageUri.toString());
                } else {
                    Log.e("PickMedia", "No image selected");
                }
            });

    public static EditEventFragment newInstance(String eventId) {
        EditEventFragment fragment = new EditEventFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
        }
    }


    private DatePickerDialog datePickerDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.fragment_edit_event, container, false);
        dateButton = view.findViewById(R.id.button_date_picker);
        dateText = view.findViewById(R.id.text_event_date);
        updateImagePoster = view.findViewById(R.id.updatedImageView);


        Button confirmButton = view.findViewById(R.id.button_confirm_edit);
        ImageButton backButton = view.findViewById(R.id.button_back);
        EditText editEventTitle = view.findViewById(R.id.edit_event_title);
        EditText editEventDetails = view.findViewById(R.id.edit_event_details);
        EditText editWaitlistCap = view.findViewById(R.id.edit_waitlist_cap);
        Switch editRequiresLocation = view.findViewById(R.id.switch_require_location);
        uploadImage = view.findViewById(R.id.button_upload_poster);
        if (eventId != null) {
            db.collection("events").document(eventId).get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            Event event = document.toObject(Event.class);
                            if (event != null) {
                                editEventTitle.setText(document.getString("title"));
                                editEventDetails.setText(document.getString("details"));
                                Long waitlistCapacity = document.getLong("waitlistCapacity");
                                if (waitlistCapacity != null && waitlistCapacity != -1) {
                                    editWaitlistCap.setText(valueOf(document.getLong("waitlistCapacity")));
                                }
                                editRequiresLocation.setChecked(document.getBoolean("requiresLocation"));


                                if (event.getDateTime() != null) {
                                    selectedDateTime.setTime(event.getDateTime());
                                    displaySelectedDateTime();
                                }


                                String imageUrl = document.getString("imageUrl");
                                if (imageUrl != null && !imageUrl.isEmpty()) {
                                    Glide.with(this)
                                            .load(imageUrl)
                                            .into(updateImagePoster);


                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e("EditEventFragment", "Error fetching event details", e));
        }

        uploadImage.setOnClickListener(v ->{
            pickMediaLauncher.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });
        dateButton.setOnClickListener(v -> showDateTimePicker());



        confirmButton.setOnClickListener(v -> {
            boolean isValid = true;
            if (Validator.isEmpty(dateText, "Please select a date and time.")) {
                isValid = false;
            }
            if (Validator.isEmpty(editEventDetails, "Event details cannot be empty")) {
                isValid = false;
            }
            if (Validator.isEmpty(editEventTitle, "Event title cannot be empty")){
                isValid = false;
            }
            if (isValid) {

                Map<String, Object> updatedFields = new HashMap<>();
                updatedFields.put("title", editEventTitle.getText().toString());
                updatedFields.put("details", editEventDetails.getText().toString());
                updatedFields.put("waitlistCapacity", Integer.parseInt(editWaitlistCap.getText().toString()));
                updatedFields.put("requiresLocation", editRequiresLocation.isChecked());
                updatedFields.put("dateTime", selectedDateTime.getTime());

                if (imageUri != null) {
                    String storagePath = "event_posters/" + eventId + ".jpg";
                    FirebaseStorage.getInstance().getReference(storagePath)
                            .putFile(imageUri)
                            .addOnSuccessListener(taskSnapshot -> {

                                taskSnapshot.getStorage().getDownloadUrl()
                                        .addOnSuccessListener(downloadUri -> {

                                            updatedFields.put("imageUrl", downloadUri.toString());

                                            db.collection("events").document(eventId)
                                                    .update(updatedFields)
                                                    .addOnSuccessListener(aVoid -> {
                                                        Log.d("EditEventFragment", "Event updated successfully!");

                                                        // Navigate to OrganizerFragment
                                                        getActivity().getSupportFragmentManager()
                                                                .beginTransaction()
                                                                .replace(R.id.fragment_container, new OrganizerFragment())
                                                                .addToBackStack(null)
                                                                .commit();
                                                    })
                                                    .addOnFailureListener(e -> Log.e("EditEventFragment", "Error updating Firestore", e));
                                        })
                                        .addOnFailureListener(e -> Log.e("EditEventFragment", "Error fetching download URL", e));
                            })
                            .addOnFailureListener(e -> Log.e("EditEventFragment", "Error uploading image", e));
                }
                else{
                    // If no new image is selected, update Firestore directly
                    db.collection("events").document(eventId)
                            .update(updatedFields)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("EditEventFragment", "Event updated successfully!");

                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.fragment_container, new OrganizerFragment())
                                        .addToBackStack(null)
                                        .commit();
                            })
                            .addOnFailureListener(e -> Log.e("EditEventFragment", "Error updating Firestore", e));
                }


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



}