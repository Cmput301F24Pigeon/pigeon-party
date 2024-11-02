package com.example.pigeon_party_app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.util.Log;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Source;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


@RunWith(AndroidJUnit4.class)
public class CreateEventFragmentTest {
    private FirebaseFirestore db;
    private String testEventId;
    private String testEventTitle;
    private String testEventDetails;
    private Facility testFacility;
    private User testUser;


    @Before
    public void setUp() {
        db = FirebaseFirestore.getInstance();
        testFacility =  new Facility("test-user-id", "test-address","test-name");
        testUser = new User("test-user-name","test@email.com",null,"test-user-id",true,true,testFacility,false);
    }

    @Test
    public void testAddEvent() {

        FragmentScenario<CreateEventFragment> scenario = FragmentScenario.launchInContainer(
                CreateEventFragment.class,
                CreateEventFragment.newInstance(testUser).getArguments()
        );

        scenario.onFragment(createdFragment -> {
            testEventId = "test-event-id";
            testEventTitle= "test-event-title";
            testEventDetails = "test-details";
            Event testEvent = new Event(testEventId,testEventTitle, new Date(), 50, testEventDetails,testFacility,false,null, null, null, testUser);
            createdFragment.addEvent(db, testEventId, testEvent);

            verifyEventInFirestore(testEventId);
        });
    }

    private void verifyEventInFirestore(String eventId) {
        db.collection("events").document(eventId).get(Source.SERVER)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("Firestore Test", "Event found in Firestore!");
                            assertTrue("Event was successfully added to Firestore", document.exists());
                            assertEquals("Event title should match", testEventTitle, document.getString("title"));
                            assertEquals("Event details should match", testEventDetails, document.getString("details"));
                            assertEquals("Event Id should match", testEventId, document.getString("eventID"));
                            assertEquals("Event Id should match", testFacility, document.getString("facility"));

                        } else {
                            fail("Event was not found in Firestore");
                        }
                    } else {
                        FirebaseFirestoreException e = (FirebaseFirestoreException) task.getException();
                        Log.w("Firestore Test", "Error verifying event", e);
                        fail("Failed to retrieve event from Firestore");
                    }
                });

    }

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