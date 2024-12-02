package com.example.pigeon_party_app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.util.Log;
import android.view.View;

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
import org.junit.Ignore;
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
    private Facility testFacility2;
    private User testUser;
    private User testUser2;
    private Event testEvent;

    /**
     * Method to be performed before the test to ensure the necessary attributes are created first
     */
    @Before
    public void setUp() {
        db = FirebaseFirestore.getInstance();
        testFacility =  new Facility("test-user-id", "test-address","test-name");
        testFacility2 =  new Facility("test-user-id2", "test-address2","test-name2");
        testUser = new User("test-user-name","test@email.com",null,"test-user-id",true,true,testFacility,false,"#000000", new ArrayList<String>(), new ArrayList<String>(),false);
        testUser2 = new User("test-user-name2","test2@email.com",null,"test-user-id2",true,true,testFacility2,false,"#000000", new ArrayList<String>(), new ArrayList<String>(),false);
        Date testDate = new Date();
        Map<String, User> testUsersWaitlist = new HashMap<>();
        Map<String, User> testUsersInvited = new HashMap<>();
        Map<String, User> testUsersCancelled = new HashMap<>();
        testEvent = new Event("test-event-id", "test-title", testDate, 10, "test-details", testFacility, false, testUsersWaitlist, testUsersInvited, testUsersCancelled, testUser);
    }

    /**
     * Test to check if the draw participants button is visible if the user is also the organizer
     */
    @Test
    public void TestDrawButtonVisibility(){
        MainActivity.currentEvent = testEvent;
        MainActivity.currentUser = testUser;
        FragmentScenario<EventDetailsFragment> scenario = FragmentScenario.launchInContainer(
                EventDetailsFragment.class
        );
        scenario.onFragment(createdFragment -> {
            assertEquals(View.VISIBLE, createdFragment.drawParticipantsButton.getVisibility());
        });
    }
}
