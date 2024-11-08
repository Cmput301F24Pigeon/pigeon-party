package com.example.pigeon_party_app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.util.Log;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivityTest {
    private FirebaseFirestore db;
    private String testUserId;
    private String testUserName;
    private String testUserEmail;
    private String testUserPhone;
    private boolean testUserIsOrganizer;
    private boolean testUserIsEntrant;
    private Facility testUserFacility;
    private boolean testUserHasNotifications;
    private User testUser;
    private ArrayList<Event> testUserEntrantEventList;
    private ArrayList<Event> testUserOrganizerEventList;

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    /**
     * Method to be performed before the test to ensure the necessary attributes are created first
     */
    @Before
    public void setUp() {
        FirebaseApp.clearInstancesForTest();
        Log.d("Firestore Test", "Firebase instances cleared for test");

        // Initialize Firebase with context from InstrumentationRegistry
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().getTargetContext());
        Log.d("Firestore Test", "FirebaseApp initialized");

        db = FirebaseFirestore.getInstance();
        if (db != null) {
            db.useEmulator("10.0.2.2", 8080); // Connect Firestore to the emulator
            Log.d("Firestore Test", "Connected to Firestore emulator");
        } else {
            throw new IllegalStateException("FirebaseFirestore instance is null. Firebase initialization failed.");
        }
    }

    @Test
    public void testRecieveCurrentUser(){
        testUserId = "test-user-id";
        testUserName = "test-user-name";
        testUserEmail = "test@email.com";
        testUserPhone = "1234567890";
        testUserIsOrganizer = false;
        testUserIsEntrant = true;
        testUserFacility = null;
        testUserHasNotifications = true;
        testUserOrganizerEventList = null;
        testUserEntrantEventList = null;

        testUser = new User(testUserName, testUserEmail, testUserPhone, testUserId, testUserIsOrganizer, testUserIsEntrant, testUserFacility, testUserHasNotifications, testUserOrganizerEventList, new ArrayList<Event>());
        db.collection("user").document("testDoc").set(testUser)
                .addOnSuccessListener(aVoid -> Log.d("Firestore Test", "Test write successful"))
                .addOnFailureListener(e -> Log.w("Firestore Test", "Test write failed", e));

        MainActivity.receiveCurrentUser(testUserId);

        Log.d("Firestore Test", "User found in Firestore!");
        assertEquals("User name should match", testUserName, MainActivity.currentUser.getName());
        assertEquals("User email should match", testUserEmail,  MainActivity.currentUser.getEmail());
        assertEquals("User phone should match", testUserPhone,  MainActivity.currentUser.getName());
        assertEquals("User Id should match", testUserId, MainActivity.currentUser.getUniqueId());
        assertEquals("User organizer status should match", testUserIsOrganizer, MainActivity.currentUser.isOrganizer());
        assertEquals("User entrant status should match", testUserIsEntrant,  MainActivity.currentUser.isEntrant());
        assertEquals("User facility should match", testUserFacility, MainActivity.currentUser.getFacility());
        assertEquals("User notification status should match", testUserHasNotifications, MainActivity.currentUser.hasNotificationsOn());
        assertEquals("User has entrant array list", testUserEntrantEventList, MainActivity.currentUser.getEntrantEventList());
        assertEquals("User has organizer array list", testUserOrganizerEventList, MainActivity.currentUser.getOrganizerEventList());


    }

    @Test
    public void testSetUpAddFacilityButtonAsEntrant() {
        onView(withId(R.id.button_facility)).perform(click());
        onView(withId(R.id.add_facility_name)).check(matches(isDisplayed()));
        onView(withId(R.id.add_facility_address)).check(matches(isDisplayed()));
    }

    @Test
    public void testSetUpAddFacilityButtonAsOrganizer() {
        onView(withId(R.id.button_facility)).perform(click());
        onView(withId(R.id.organizer_event_list)).check(matches(isDisplayed()));
        onView(withId(R.id.button_add_organizer_event)).check(matches(isDisplayed()));
    }

    @Test
    public void testSetUpProfileButton() {
        onView(withId(R.id.button_profile)).perform(click());
        onView(withId(R.id.textView_entrant_name)).check(matches(isDisplayed()));
        onView(withId(R.id.textView_entrant_email)).check(matches(isDisplayed()));
    }

    // Will also do tests for setUpAddEventButton, setUpNotificationButton

}
