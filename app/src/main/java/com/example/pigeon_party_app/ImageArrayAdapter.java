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


public class ImageArrayAdapter extends ArrayAdapter<StorageReference> {
    private ArrayList<StorageReference> images;
    private Context context;


    public ImageArrayAdapter(Context context, ArrayList<StorageReference> images){
        super(context,0, images);
        this.images = images;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.admin_image_content, parent,false);
        }

        StorageReference currentImage = images.get(position);

        ImageView image = view.findViewById(R.id.image);
        Glide.with(context)
                .load(currentImage.getDownloadUrl())
                .into(image);



        return view;


    }
}
