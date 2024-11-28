package com.example.pigeon_party_app;

import static com.example.pigeon_party_app.MainActivity.db;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * This fragment allows us to browse profiles and remove them
 */
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

        ImageButton backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AdminFragment())
                    .addToBackStack(null)
                    .commit();
        });


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
                        deleteFacility(position);
                        deleteUser(position);
                    });
                    builder.setNegativeButton("Remove facility", (dialog, which) -> {
                        deleteFacility(position);
                    });
                    builder.show();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Do you want to remove user");
                    builder.setCancelable(true);
                    builder.setPositiveButton("Remove user", (dialog, which) -> {
                        deleteUser(position);
                    });
                    builder.show();
                }

            }
        });
        return view;
    }

    /**
     *this method fills our list of users in the firebase and that have used our app
     */
    private void fill_list(){

        CollectionReference usersRef = db.collection("user");
        usersRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    users.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        users.add(doc.toObject(User.class));
                    }
                    userArrayAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * This deletes the user from firebase
     * @param i which is the position in our listview
     */
    private void deleteUser(int i){
        // Add the city to the local list
        User temp = users.get(i);
        users.remove(i);
        userArrayAdapter.notifyDataSetChanged();
        db.collection("user").document(temp.getUniqueId())
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

    /**
     * This deletes the facility and updates our firebase user
     * @param i this is the position of the user in our list
     */
    private void deleteFacility(int i){
        // Add the city to the local list
        User temp = users.get(i);
        temp.setFacility(null);
        ArrayList<Event> emptyList = new ArrayList<Event>();
        temp.setOrganizer(false);
        //remove the events the facility hosts
        for (Event event :temp.getOrganizerEventList()){
            removeEvent(event);
        }
        temp.setOrganizerEventList(emptyList);


        users.get(i).setFacility(null);
        DocumentReference userRef = db.collection("user").document(temp.getUniqueId());

        userRef.update("facility", null, "organizer", false, "organizerEventList", emptyList)
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

    /**
     * Removes events that are associated to the facility since the facility doesn't exist anymore we delete that event
     * @param event the event we want to delete
     */
    private void removeEvent(Event event){
        db.collection("events").document(event.getEventId())
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
}
