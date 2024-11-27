package com.example.pigeon_party_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AdminFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_main_page, container, false);
        Button browseProfiles = view.findViewById(R.id.browse_profiles);
        Button browseEvents = view.findViewById(R.id.browse_events);
        Button browseImages = view.findViewById(R.id.browse_images);
        Button manageHashdata = view.findViewById(R.id.manage_hash_data);

        browseProfiles.setOnClickListener( v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new BrowseProfilesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }


}
