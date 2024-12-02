package com.example.pigeon_party_app;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * This fragment allows the admin to browse events and remove them
 */
public class BrowseImagesFragment extends Fragment {
    private ArrayList<String> images;
    private ImageArrayAdapter imageArrayAdapter;
    private ListView imageListView;
    FirebaseStorage storage = FirebaseStorage.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.browse_images_fragment, container, false);

        ImageButton backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AdminFragment())
                    .addToBackStack(null)
                    .commit();
        });
        images = new ArrayList<>();
        imageListView = view.findViewById(R.id.image_list);
        imageArrayAdapter = new ImageArrayAdapter(getActivity(), images);
        imageListView.setAdapter(imageArrayAdapter);
        fill_images();

        imageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Do you want to remove this image");
                builder.setCancelable(true);
                builder.setPositiveButton("Remove image", (dialog, which) -> {
                    removeImage(position);
                });
                builder.show();


            }
        });
        return view;
    }

    /**
     * This fills the list view with images
     */
    private void fill_images() {
        StorageReference storageRef = storage.getReference();

        StorageReference listRef = storageRef.child("event_posters");


        listRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference file : listResult.getItems()) {
                    file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            images.add(uri.toString());
                            Log.e("Itemvalue", uri.toString());
                            imageArrayAdapter.notifyDataSetChanged();

                        }
                    });
                }
            }
        });
        //for profile images
        listRef = storageRef.child("profile_images");

        listRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {

                for (StorageReference file : listResult.getItems()) {
                    file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            images.add(uri.toString());
                            Log.e("Itemvalue", uri.toString());
                            imageArrayAdapter.notifyDataSetChanged();

                        }
                    });
                }
            }
        });

    }

    /**
     * this removes the images from firebase
     */
    private void removeImage(int i) {
        String temp = images.get(i);
        StorageReference imageRef = storage.getReferenceFromUrl(temp);
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
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
        images.remove(i);

        imageArrayAdapter.notifyDataSetChanged();


    }

}

