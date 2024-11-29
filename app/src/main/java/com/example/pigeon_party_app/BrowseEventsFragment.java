package com.example.pigeon_party_app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BrowseEventsFragment extends Fragment {
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Event> events;
    private AdminEventArrayAdapter eventArrayAdapter;
    private ListView eventsListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.browse_events_fragment, container, false);

        ImageButton backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AdminFragment())
                    .addToBackStack(null)
                    .commit();
        });
        events = new ArrayList<>();
        eventsListView = view.findViewById(R.id.event_list);
        fill_list();
        eventArrayAdapter = new AdminEventArrayAdapter(getActivity(), events);

        eventsListView.setAdapter(eventArrayAdapter);

        eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event current = eventArrayAdapter.getItem(position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Do you want to remove event");
                    builder.setCancelable(true);
                    builder.setPositiveButton("Remove event", (dialog, which) -> {
                        removeEvent(position);
                    });
                    builder.show();


            }
        });
        return view;
    }







    private void removeEvent(int i){
        Event temp = events.get(i);
        events.remove(i);
        eventArrayAdapter.notifyDataSetChanged();
        db.collection("events").document(temp.getEventId())
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
     *this method fills our list of events from the firebase into our app
     */
    private void fill_list(){

        CollectionReference usersRef = db.collection("events");
        usersRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    events.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        events.add(doc.toObject(Event.class));
                    }
                    eventArrayAdapter.notifyDataSetChanged();
                }
            }
        });
    }

}
