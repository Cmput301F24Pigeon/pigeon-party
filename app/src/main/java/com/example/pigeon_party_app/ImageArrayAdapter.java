package com.example.pigeon_party_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * This class is used to put images in the listview for admins
 */
public class ImageArrayAdapter extends ArrayAdapter<String> {
    private ArrayList<String> images;
    private Context context;

    /**
     * Constructor for ImageArrayAdapter, with needed parameters for ArrayAdapter constructor
     *
     * @param context context of app for parent constructor
     * @param images  ArrayList containing strings representing paths to images to be displayed in arrayAdapter
     */
    public ImageArrayAdapter(Context context, ArrayList<String> images) {
        super(context, 0, images);
        this.images = images;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.admin_image_content, parent, false);
        }

        String imageUri = images.get(position);

        ImageView image = view.findViewById(R.id.image);
        Glide.with(context)
                .load(imageUri)
                .into(image);

        return view;
    }
}
