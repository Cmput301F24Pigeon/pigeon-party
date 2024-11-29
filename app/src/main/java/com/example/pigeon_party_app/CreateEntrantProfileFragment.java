package com.example.pigeon_party_app;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Firebase;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * This class is a fragment that allows app users to create a profile with their information
 */
public class CreateEntrantProfileFragment extends Fragment {

    private ShapeableImageView profilePic;
    private Uri imageUri;
    private String uniqueId;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private final ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), result -> {
                if (result != null) {
                    imageUri = result;
                } else {
                    Log.e("PickMedia", "No image selected");
                }
            });

    public CreateEntrantProfileFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Used https://www.youtube.com/watch?v=-w8Faojl4HI to determine unique ID
        uniqueId = Settings.Secure.getString(requireActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

        View view = inflater.inflate(R.layout.fragment_create_entrant_profile, container, false);
        profilePic = view.findViewById(R.id.entrant_profile_image);
        EditText createEntrantName = view.findViewById(R.id.editText_create_user_name);
        EditText createEntrantEmail = view.findViewById(R.id.editText_create_user_email);
        EditText createEntrantPhone = view.findViewById(R.id.editText_create_user_phone);
        Button createProfileButton = view.findViewById(R.id.create_user_profile_button);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });

        createProfileButton.setOnClickListener(v -> {
            createEntrantName.setFocusableInTouchMode(true);
            createEntrantName.setFocusable(true);
            createEntrantEmail.setFocusableInTouchMode(true);
            createEntrantEmail.setFocusable(true);
            createEntrantPhone.setFocusableInTouchMode(true);
            createEntrantPhone.setFocusable(true);

            boolean isValid = true;
            if (!Validator.isName(createEntrantName, "Your profile must include a name.")) {
                isValid = false;
            }
            if (!Validator.isEmail(createEntrantEmail, "Your profile must have a valid email.")) {
                isValid = false;
            }
            if (!Validator.isPhoneNumber(createEntrantPhone, "Your phone number must be 10 digits or empty.")) {
                isValid = false;
            }
            if (isValid) {
                ArrayList<String> emptyList = new ArrayList<>();
                String colour = pickColour();

                User user = new User(createEntrantName.getText().toString(), createEntrantEmail.getText().toString(), createEntrantPhone.getText().toString(), null, false, true, null, true, colour, emptyList, emptyList, false);

                addUser(user);
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }

    /**
     * Method adds a user object to firebase
     * @param user the user being added
     */
    public void addUser(User user) {
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
        Users.put("colour", user.getColour());
        Users.put("notifications", user.getNotifications());
        Users.put("entrantEventList", user.getEntrantEventList());
        Users.put("organizerEventList", user.getOrganizerEventList());
        Users.put("admin", user.isAdmin());

        MainActivity.db.collection("user").document(uniqueId)
                .set(Users)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FireStore", "User successfully added");
                })
                .addOnFailureListener(e ->{
                    Log.w("FireStore", "Error adding user", e);
                });

    }

    /**
     * newInstance method creates a mock fragment for testing
     * @return CreateEntrantProfileFragment the mock fragment being used for testing
     */
    public static CreateEntrantProfileFragment newInstance() {
        CreateEntrantProfileFragment fragment = new CreateEntrantProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Method randomly selects a colour to use in user's default profile avatar
     * @return String instance of hexadecimal colour
     */
    public String pickColour() {
        String[] colours = {"#30BFA0", "#BF3064", "#8928A1",  "#5228A1", "#4B2DB5", "#2D7CB5", "#2DB55D"};
        Random rand = new Random();
        int i = rand.nextInt(colours.length + 1);
        return colours[i];
    }

    /**
     * Function runs an activity to upload an image from user's phone
     */
    private void choosePicture() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profilePic.setImageURI(imageUri);
            uploadPicture();
        }
    }

    private void uploadPicture() {
        if (imageUri != null) {
            final ProgressDialog pd = new ProgressDialog(getContext());
            pd.setTitle("Uploading image. . .");
            pd.show();
            StorageReference imageRef = storageRef.child("profile_images/" + uniqueId);
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_LONG).show();
                            Log.d("Firebase Storage", "Image upload successful");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getContext(), "Image upload failed", Toast.LENGTH_LONG).show();
                            //Log.d("Firebase Storage", "Image upload failed", e);
                            Log.e("Upload Error", "Error: " + e.getMessage(), e);
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount() );
                            pd.setMessage("Progress: " + (int) progressPercent + "%");
                            if (progressPercent == 100.0) {
                                pd.dismiss();
                            }
                        }
                    });
        }
    }
}
