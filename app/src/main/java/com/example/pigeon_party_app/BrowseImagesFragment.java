package com.example.pigeon_party_app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

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

public class BrowseImagesFragment extends Fragment {
    private ArrayList<StorageReference> images;
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
        fill_images();
        imageArrayAdapter = new ImageArrayAdapter(getActivity(), images);
        imageListView.setAdapter(imageArrayAdapter);

        return view;
    }
    private void fill_images(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference listRef = storageRef.child("event_posters/" );

        //for events
        listRef.listAll()
                .addOnSuccessListener(listResult -> {
                    images.addAll(listResult.getItems());
                })
                .addOnFailureListener(e -> {
                    Log.d("firebase", "error loading images");
                });

        //for profile images
        listRef = storageRef.child("profile_images/" );

        listRef.listAll()
                .addOnSuccessListener(listResult -> {
                    images.addAll(listResult.getItems());
                })
                .addOnFailureListener(e -> {
                    Log.d("firebase", "error loading images");
                });
    }


}

