package com.example.pigeon_party_app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BrowseProfilesFragment extends Fragment {
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<User> users;
    private AdminUserArrayAdapter userArrayAdapter;
    private NotificationHelper notificationHelper;
    private ListView userListView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.admin_profile_fragment, container, false);

        users = new ArrayList<>();
        userListView = view.findViewById(R.id.user_list);
        userArrayAdapter = new AdminUserArrayAdapter(getActivity(), users);
        userListView.setAdapter((ListAdapter) userArrayAdapter);


        return view;
    }


    private void fill_list(){
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                users.add(document.toObject(User.class));

                            }
                        } else {
                            Log.d("firebase", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
}
