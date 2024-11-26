package com.example.pigeon_party_app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasFocus;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertEquals;

import android.util.Log;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * This class tests US 01-02-02: Editing the entrant's profile
 * If there is an existing document in Firebase, it must be deleted before these tests are run
 */

public class EditEntrantProfileTest {

    private FirebaseFirestore db;
    private String testUserName;
    private String testUserEmail;
    private String testUserPhone;
    private String newUserName = "User Name";
    private String newUserEmail = "new@email.com";
    private String newUserPhone = "1111111111";
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
            try {
                permissionDialog.click();
            } catch (UiObjectNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        onView(withId(R.id.button_profile)).perform(click());
        onView(withId(R.id.edit_entrant_profile_button)).perform(click());
    }

    /**
     * This method tests editing all the fields in the entrant's profile
     */
    @Test
    public void testEditUserInfo() {
        testInput(newUserName, newUserEmail, newUserPhone);

        onView(withId(R.id.textView_entrant_name)).check(matches(withText(newUserName)));
        onView(withId(R.id.textView_entrant_email)).check(matches(withText(newUserEmail)));
        onView(withId(R.id.textView_entrant_phone)).check(matches(withText(newUserPhone)));
    }

    /**
     * This method tests that the initial on the user's avatar is updated if they change their name in their profile
     */
    @Test public void testAvatarUpdate() {
        onView(withId(R.id.editText_edit_user_name)).perform(clearText());
        onView(withId(R.id.editText_edit_user_name)).perform(ViewActions.typeText(newUserName), closeSoftKeyboard());
        onView(withId(R.id.update_user_profile_button)).perform(click());

        assertEquals(newUserName.substring(0, 1), AvatarView.getInitial());
    }

    /**
     * Test to check empty (and therefore invalid) user input in name editText field
     */
    @Test
    public void testEmptyUserName() {
        testInput("", newUserEmail, newUserPhone);
        onView(withId(R.id.editText_edit_user_name)).check(matches(hasFocus()));
    }

    /**
     * Test to check empty (and therefore invalid) user input in email editText field
     */
    @Test
    public void testEmptyEmail() {
        testInput(newUserName, "", newUserEmail);
        onView(withId(R.id.editText_edit_user_email)).check(matches(hasFocus()));
    }

    /**
     * Test to check invalid user input in email editText field
     */
    @Test
    public void testInvalidEmail() {
        testInput(newUserName, "test@email", newUserPhone);
        onView(withId(R.id.editText_edit_user_email)).check(matches(hasFocus()));
    }

    /**
     * Test to check invalid user input in phoneNumber editText field
     */
    @Test
    public void testInvalidPhone() {
        testInput(newUserName, newUserEmail, "1");
        onView(withId(R.id.editText_edit_user_phone)).check(matches(hasFocus()));
    }

    /**
     * Test to check that changes are undone if fragment is closed with back button
     */
    @Test
    public void testBackButton() {
        onView(withId(R.id.editText_edit_user_name)).perform(clearText());
        onView(withId(R.id.editText_edit_user_name)).perform(ViewActions.typeText(newUserName), closeSoftKeyboard());
        onView(withId(R.id.editText_edit_user_email)).perform(clearText());
        onView(withId(R.id.editText_edit_user_email)).perform(ViewActions.typeText(newUserEmail), closeSoftKeyboard());
        onView(withId(R.id.editText_edit_user_phone)).perform(clearText());
        onView(withId(R.id.editText_edit_user_phone)).perform(ViewActions.typeText(newUserPhone), closeSoftKeyboard());

        onView(withId(R.id.button_back)).perform(click());

        onView(withId(R.id.textView_entrant_name)).check(matches(withText(testUserName)));
        onView(withId(R.id.textView_entrant_email)).check(matches(withText(testUserEmail)));
        onView(withId(R.id.textView_entrant_phone)).check(matches(withText(testUserPhone)));
        assertEquals(testUserName.substring(0, 1), AvatarView.getInitial());
    }

    /**
     * Helper method to fill user input fields with given parameters and click the "Create" button
     * Used as a form of data provider to test for focus set on fields with invalid inputs
     * @param name String entered into the name editText field
     * @param email String entered into the email editText field
     * @param phoneNumber String entered into the phoneNumber editText field
     */
    private void testInput(String name, String email, String phoneNumber) {
        onView(withId(R.id.editText_edit_user_name)).perform(clearText());
        onView(withId(R.id.editText_edit_user_name)).perform(ViewActions.typeText(name), closeSoftKeyboard());
        onView(withId(R.id.editText_edit_user_email)).perform(clearText());
        onView(withId(R.id.editText_edit_user_email)).perform(ViewActions.typeText(email), closeSoftKeyboard());
        onView(withId(R.id.editText_edit_user_phone)).perform(clearText());
        onView(withId(R.id.editText_edit_user_phone)).perform(ViewActions.typeText(phoneNumber), closeSoftKeyboard());
        onView(withId(R.id.update_user_profile_button)).perform(click());
    }
}