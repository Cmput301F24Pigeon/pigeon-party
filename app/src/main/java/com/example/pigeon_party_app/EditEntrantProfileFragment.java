package com.example.pigeon_party_app;

import static android.app.Activity.RESULT_OK;
import static com.example.pigeon_party_app.MainActivity.db;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * This class is a fragment that allows the user to change their profile information
 */

public class EditEntrantProfileFragment extends Fragment {

    public User entrant;
    private EditText editEntrantName;
    private EditText editEntrantEmail;
    private EditText editEntrantPhoneNumber;
    private AvatarView viewEntrantProfileImage;

    private Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    public static String TAG = "EditEntrantProfileFragment";

    public EditEntrantProfileFragment() {}

    @SuppressLint("ValidFragment")
    public EditEntrantProfileFragment(User entrant) {
        this.entrant = entrant;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_entrant_profile, null);

        editEntrantName = view.findViewById(R.id.editText_edit_user_name);
        editEntrantEmail = view.findViewById(R.id.editText_edit_user_email);
        editEntrantPhoneNumber = view.findViewById(R.id.editText_edit_user_phone);
        viewEntrantProfileImage = view.findViewById(R.id.entrant_profile_image);
        viewEntrantProfileImage.setUser(entrant);

        editEntrantName.setText(entrant.getName());
        editEntrantEmail.setText(entrant.getEmail());
        editEntrantPhoneNumber.setText(entrant.getPhoneNumber());

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        Button update_button = view.findViewById(R.id.update_user_profile_button);
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndSetInput();
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

        ImageButton editProfilePicButton = view.findViewById(R.id.change_profile_pic_button);
        editProfilePicButton.setOnClickListener(v -> {
            choosePicture();
        });

        ImageButton deleteProfilePicButton = view.findViewById(R.id.delete_profile_pic_button);
        deleteProfilePicButton.setOnClickListener(v -> {
            deletePicture();
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

    private void validateAndSetInput() {
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

//            uploadPicture();

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ViewEntrantProfileFragment(entrant))
                    .addToBackStack(null)
                    .commit();
        }
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
            uploadPicture();
        }
    }

    private void uploadPicture() {
        if (imageUri != null) {
            final ProgressDialog pd = new ProgressDialog(getContext());
            pd.setTitle("Uploading image. . .");
            pd.show();
            StorageReference imageRef = storageRef.child("profile_images/" + entrant.getUniqueId());
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            viewEntrantProfileImage.setUser(entrant);
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

    private void deletePicture() {
        StorageReference imageRef = storageRef.child("profile_images/" + entrant.getUniqueId());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                viewEntrantProfileImage.setImageBitmap(null);
                entrant.setProfileImagePath(null);
                viewEntrantProfileImage.setUser(entrant);

                Toast.makeText(getContext(), "Image deleted", Toast.LENGTH_LONG).show();
                Log.d("Firebase Storage", "Image delete successful");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getContext(), "Could not delete image", Toast.LENGTH_LONG).show();
                Log.d("Firebase Storage", "Image delete successful");
            }
        });
    }
}
