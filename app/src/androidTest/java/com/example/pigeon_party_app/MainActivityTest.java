package com.example.pigeon_party_app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
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
    private String testUserName;
    private String testUserEmail;
    private String testUserPhone;
    private UiDevice device;

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

        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @Test
    public void testSetUpAddFacilityButtonAsEntrant() throws UiObjectNotFoundException {
        testUserName = "test-user-name";
        testUserEmail = "test@email.com";
        testUserPhone = "1234567890";
        FragmentScenario<CreateEntrantProfileFragment> scenario = FragmentScenario.launchInContainer(
                CreateEntrantProfileFragment.class,
                CreateEntrantProfileFragment.newInstance().getArguments()
        );
        onView(withId(R.id.editText_create_user_name)).perform(ViewActions.typeText(testUserName), closeSoftKeyboard());
        onView(withId(R.id.editText_create_user_email)).perform(ViewActions.typeText(testUserEmail), closeSoftKeyboard());
        onView(withId(R.id.editText_create_user_phone)).perform(ViewActions.typeText(testUserPhone), closeSoftKeyboard());
        onView(withId(R.id.create_user_profile_button)).perform(click());

        UiObject permissionDialog = device.findObject(new UiSelector().text("Allow"));
        if (permissionDialog.waitForExists(2000)) {
            permissionDialog.click();
        }

        onView(withId(R.id.button_facility)).perform(click());
        onView(withId(R.id.add_facility_name)).check(matches(isDisplayed()));
        onView(withId(R.id.add_facility_address)).check(matches(isDisplayed()));
    }

    @Test
    public void testSetUpAddFacilityButtonAsOrganizer() throws UiObjectNotFoundException {
        testUserName = "test-user-name";
        testUserEmail = "test@email.com";
        testUserPhone = "1234567890";
        FragmentScenario<CreateEntrantProfileFragment> scenario = FragmentScenario.launchInContainer(
                CreateEntrantProfileFragment.class,
                CreateEntrantProfileFragment.newInstance().getArguments()
        );
        onView(withId(R.id.editText_create_user_name)).perform(ViewActions.typeText(testUserName), closeSoftKeyboard());
        onView(withId(R.id.editText_create_user_email)).perform(ViewActions.typeText(testUserEmail), closeSoftKeyboard());
        onView(withId(R.id.editText_create_user_phone)).perform(ViewActions.typeText(testUserPhone), closeSoftKeyboard());
        onView(withId(R.id.create_user_profile_button)).perform(click());

        UiObject permissionDialog = device.findObject(new UiSelector().text("Allow"));
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
        testUserName = "test-user-name";
        testUserEmail = "test@email.com";
        testUserPhone = "1234567890";
        FragmentScenario<CreateEntrantProfileFragment> scenario = FragmentScenario.launchInContainer(
                CreateEntrantProfileFragment.class,
                CreateEntrantProfileFragment.newInstance().getArguments()
        );
        onView(withId(R.id.editText_create_user_name)).perform(ViewActions.typeText(testUserName), closeSoftKeyboard());
        onView(withId(R.id.editText_create_user_email)).perform(ViewActions.typeText(testUserEmail), closeSoftKeyboard());
        onView(withId(R.id.editText_create_user_phone)).perform(ViewActions.typeText(testUserPhone), closeSoftKeyboard());
        onView(withId(R.id.create_user_profile_button)).perform(click());

        UiObject permissionDialog = device.findObject(new UiSelector().text("Allow"));
        if (permissionDialog.waitForExists(2000)) {
            permissionDialog.click();
        }

        onView(withId(R.id.button_profile)).perform(click());
        onView(withId(R.id.textView_entrant_name)).check(matches(isDisplayed()));
        onView(withId(R.id.textView_entrant_email)).check(matches(isDisplayed()));
    }
}
