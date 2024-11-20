package com.example.pigeon_party_app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


import android.util.Log;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.UiDevice;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.ArrayList;

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
    private String testUserColour;
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
    public void testRecieveCurrentUser() {
        testUserId = "test-user-id";
        testUserName = "test-user-name";
        testUserEmail = "test@email.com";
        testUserPhone = "1234567890";
        testUserIsOrganizer = false;
        testUserIsEntrant = true;
        testUserFacility = null;
        testUserHasNotifications = true;
        testUserColour = "#000000";
        testUserOrganizerEventList = new ArrayList<>();
        testUserEntrantEventList = new ArrayList<>();

        testUser = new User(testUserName, testUserEmail, testUserPhone, testUserId, testUserIsOrganizer, testUserIsEntrant, testUserFacility, testUserHasNotifications, testUserColour, testUserOrganizerEventList);
        db.collection("user").document(testUserId).set(testUser)
                .addOnSuccessListener(aVoid -> Log.d("Firestore Test", "Test write successful"))
                .addOnFailureListener(e -> Log.w("Firestore Test", "Test write failed", e));


        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                User firebaseUser = activity.receiveCurrentUser(testUserId);
                assertEquals("User name should match", testUserName, firebaseUser.getName());
                assertEquals("User email should match", testUserEmail, firebaseUser.getEmail());
                assertEquals("User phone should match", testUserPhone, firebaseUser.getPhoneNumber());
                assertEquals("User Id should match", testUserId, firebaseUser.getUniqueId());
                assertEquals("User organizer status should match", testUserIsOrganizer, firebaseUser.isOrganizer());
                assertEquals("User entrant status should match", testUserIsEntrant, firebaseUser.isEntrant());
                assertEquals("User facility should match", testUserFacility, firebaseUser.getFacility());
                assertEquals("User notification status should match", testUserHasNotifications, firebaseUser.hasNotificationsOn());
                assertEquals("User colour should match", testUserColour, firebaseUser.getColour());
                assertEquals("User has entrant array list", testUserEntrantEventList, firebaseUser.getEntrantEventList());
                assertEquals("User has organizer array list", testUserOrganizerEventList, firebaseUser.getOrganizerEventList());
            });
        }




    }
    @Test
    public void testSetUpAddFacilityButtonAsEntrant() throws UiObjectNotFoundException {
        // Note: this test will pass if the code that creates the notification permission dialogue is commented out in main.
        // I can't find an automated way to click on either "Allow" or "Don't allow", so can't get past the pop-up
        testUserName = "test-user-name";
        testUserEmail = "test@email.com";
        testUserPhone = "1234567890";
        FragmentScenario<CreateEntrantProfileFragment> scenario = FragmentScenario.launchInContainer(
                CreateEntrantProfileFragment.class,
                CreateEntrantProfileFragment.newInstance().getArguments()
        );
        onView(withId(R.id.editText_create_user_name)).perform(ViewActions.typeText(testUserName));
        onView(withId(R.id.editText_create_user_email)).perform(ViewActions.typeText(testUserEmail));
        onView(withId(R.id.editText_create_user_phone)).perform(ViewActions.typeText(testUserPhone));
        onView(withId(R.id.create_user_profile_button)).perform(click());

        // Test stops here because of pop up
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject permissionDialog = device.findObject(new UiSelector().textContains("Don't"));
        if (permissionDialog.waitForExists(2000)) {
            permissionDialog.click();
        }

        onView(withId(R.id.button_facility)).perform(click());
        onView(withId(R.id.add_facility_name)).check(matches(isDisplayed()));
        onView(withId(R.id.add_facility_address)).check(matches(isDisplayed()));
    }

    @Test
    public void testSetUpAddFacilityButtonAsOrganizer() throws UiObjectNotFoundException {
        // Note: this test will pass if the code that creates the notification permission dialogue is commented out in main.
        // I can't find an automated way to click on either "Allow" or "Don't allow", so can't get past the pop-up
        testUserName = "test-user-name";
        testUserEmail = "test@email.com";
        testUserPhone = "1234567890";
        FragmentScenario<CreateEntrantProfileFragment> scenario = FragmentScenario.launchInContainer(
                CreateEntrantProfileFragment.class,
                CreateEntrantProfileFragment.newInstance().getArguments()
        );
        onView(withId(R.id.editText_create_user_name)).perform(ViewActions.typeText(testUserName));
        onView(withId(R.id.editText_create_user_email)).perform(ViewActions.typeText(testUserEmail));
        onView(withId(R.id.editText_create_user_phone)).perform(ViewActions.typeText(testUserPhone));
        onView(withId(R.id.create_user_profile_button)).perform(click());

        // Test stops here because of pop up
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject permissionDialog = device.findObject(new UiSelector().textContains("Don't"));
        if (permissionDialog.waitForExists(2000)) {
            permissionDialog.click();
        }

        MainActivity.getCurrentUser().setOrganizer(true);
        onView(withId(R.id.button_facility)).perform(click());

        onView(withId(R.id.organizer_event_list)).check(matches(isDisplayed()));
        onView(withId(R.id.button_add_organizer_event)).check(matches(isDisplayed()));
    }

    @Test
    public void testSetUpProfileButton() throws UiObjectNotFoundException {
        // Note: this test will pass if the code that creates the notification permission dialogue is commented out in main.
        // I can't find an automated way to click on either "Allow" or "Don't allow", so can't get past the pop-up in this test
        testUserName = "test-user-name";
        testUserEmail = "test@email.com";
        testUserPhone = "1234567890";
        FragmentScenario<CreateEntrantProfileFragment> scenario = FragmentScenario.launchInContainer(
                CreateEntrantProfileFragment.class,
                CreateEntrantProfileFragment.newInstance().getArguments()
        );
        onView(withId(R.id.editText_create_user_name)).perform(ViewActions.typeText(testUserName));
        onView(withId(R.id.editText_create_user_email)).perform(ViewActions.typeText(testUserEmail));
        onView(withId(R.id.editText_create_user_phone)).perform(ViewActions.typeText(testUserPhone));
        onView(withId(R.id.create_user_profile_button)).perform(click());

        // Test stops here because of pop up
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject permissionDialog = device.findObject(new UiSelector().textContains("Don't"));
        if (permissionDialog.waitForExists(2000)) {
            permissionDialog.click();
        }

        onView(withId(R.id.button_profile)).perform(click());
        onView(withId(R.id.textView_entrant_name)).check(matches(isDisplayed()));
        onView(withId(R.id.textView_entrant_email)).check(matches(isDisplayed()));
    }


    @Test
    public void testSetUpNotificationButton() throws UiObjectNotFoundException {
        // This Fragment is probably going to be removed, it and any tests are just along for the ride right now
        // Note: this test will pass if the code that creates the notification permission dialogue is commented out in main.
        // I can't find an automated way to click on either "Allow" or "Don't allow", so can't get past the pop-up in this test
        testUserName = "test-user-name";
        testUserEmail = "test@email.com";
        testUserPhone = "1234567890";
        FragmentScenario<CreateEntrantProfileFragment> scenario = FragmentScenario.launchInContainer(
                CreateEntrantProfileFragment.class,
                CreateEntrantProfileFragment.newInstance().getArguments()
        );
        onView(withId(R.id.editText_create_user_name)).perform(ViewActions.typeText(testUserName));
        onView(withId(R.id.editText_create_user_email)).perform(ViewActions.typeText(testUserEmail));
        onView(withId(R.id.editText_create_user_phone)).perform(ViewActions.typeText(testUserPhone));
        onView(withId(R.id.create_user_profile_button)).perform(click());

        // Test stops here because of pop up
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject permissionDialog = device.findObject(new UiSelector().textContains("Don't"));
        if (permissionDialog.waitForExists(2000)) {
            permissionDialog.click();
        }

        onView(withId(R.id.button_notifications)).perform(click());
        onView(withId(R.id.notification_switch)).check(matches(isDisplayed()));
    }

}
