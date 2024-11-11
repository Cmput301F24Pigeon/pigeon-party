package com.example.pigeon_party_app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.util.Log;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RunWith(AndroidJUnit4.class)
public class EventDetailsFragmentTest {
    private FirebaseFirestore db;
    private String testEventId;
    private String testEventTitle;
    private String testEventDetails;
    private Facility testFacility;
    private User testUser;
    private Event testEvent;

    /**
     * Method to be performed before the test to ensure the necessary attributes are created first
     */
    @Before
    public void setUp() {
        db = FirebaseFirestore.getInstance();
        testFacility =  new Facility("test-user-id", "test-address","test-name");
        testUser = new User("test-user-name","test@email.com",null,"test-user-id",true,true,testFacility,false,new ArrayList<Event>(), new ArrayList<Event>());
        Date testDate = new Date();
        Map<String, User> testUsersWaitlist = new HashMap<>();
        Map<String, User> testUsersInvited = new HashMap<>();
        Map<String, User> testUsersCancelled = new HashMap<>();
        testEvent = new Event("test-event-id", "test-title", testDate, 10, "test-details", testFacility, false, testUsersWaitlist, testUsersInvited, testUsersCancelled, testUser);

        db.collection("events").document(testEvent.getEventId())
                .set(testEvent)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FireStore", "Event successfully added");
                })
                .addOnFailureListener(e ->{
                    Log.w("FireStore", "Error adding event", e);
                });
        Map<String, Object> updates = new HashMap<>();

        testUser.addOrganizerEventList(testEvent);
        updates.put("organizerEventList", testUser.getOrganizerEventList());
        db.collection("user").document(testUser.getUniqueId())
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User's facility successfully updated"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating user's oranizer list", e));


    }

    /**
     * Test to check if user can sign up to the event's waitlist
     *
     */
    @Test
    public void TestSignUp(){
        FragmentScenario<EventDetailsFragment> scenario = FragmentScenario.launchInContainer(
                EventDetailsFragment.class
        );
        scenario.onFragment(createdFragment -> {
            createdFragment.event = testEvent;
            createdFragment.current_user = testUser;
        });
        onView(withId(R.id.signupButton)).perform(click());
        verifyUserInEventInFirestore(testEvent.getEventId());
    }

    /**
     * Method to verify user is in the event's waitlist in firebase
     * @param eventId
     */
    private void verifyUserInEventInFirestore(String eventId) {
        DocumentReference docRef = db.collection("events").document(eventId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    assertEquals("User ID should appear as a map key in usersWaitlisted", testUser.getUniqueId(), documentSnapshot.getString("usersWaitlisted."+testUser.getUniqueId()));
                }
            }
        });
    }

    /**
     * A method to be performed after the test method to remove any data that was added to the firebase
     */
    @After
    public void removeData() {
        if (testEventId != null) {
            db.collection("events").document(testEventId)
                    .delete()
                    .addOnSuccessListener(aVoid -> Log.d("Firestore Test", "Test event deleted"))
                    .addOnFailureListener(e -> Log.w("Firestore Test", "Failed to delete test event", e));
        }
    }
}
