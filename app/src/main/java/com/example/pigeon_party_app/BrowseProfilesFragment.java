package com.example.pigeon_party_app;

import static com.example.pigeon_party_app.MainActivity.db;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
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
        fill_list();
        userArrayAdapter = new AdminUserArrayAdapter(getActivity(), users);
        userListView.setAdapter(userArrayAdapter);

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User current = userArrayAdapter.getItem(position);
                if (current.isOrganizer()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Do you want to remove user or remove it's facility");
                    builder.setCancelable(true);
                    builder.setPositiveButton("Remove user", (dialog, which) -> {
                        deleteUser(position);
                    });
                    builder.setNegativeButton("Remove facility", (dialog, which) -> {
                        deleteFacility(position);
                    });
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Do you want to remove user");
                    builder.setCancelable(true);
                    builder.setPositiveButton("Remove user", (dialog, which) -> {
                        deleteUser(position);
                    });
                }
            }
        });
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

    private void deleteUser(int i){
        // Add the city to the local list
        User temp = users.get(i);
        users.remove(i);
        userArrayAdapter.notifyDataSetChanged();
        db.collection("users").document(temp.getUniqueId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("firebase", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("firebase", "Error deleting document", e);
                    }
                });
    }

    private void deleteFacility(int i){
        // Add the city to the local list
        User temp = users.get(i);
        temp.setFacility(null);
        users.get(i).setFacility(null);
        DocumentReference entrantRef = db.collection("user").document(temp.getUniqueId());

        entrantRef.update("facility", temp.getFacility())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("firebase", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("firebase", "Error deleting document", e);
                    }
                });
        userArrayAdapter.notifyDataSetChanged();

    }
}
