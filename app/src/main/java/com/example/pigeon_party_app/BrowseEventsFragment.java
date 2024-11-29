package com.example.pigeon_party_app;

import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class BrowseEventsFragment extends Fragment {
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Event> events;
    private AdminEventArrayAdapter ArrayAdapter;
    private ListView eventsListView;







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
