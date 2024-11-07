package com.example.pigeon_party_app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.util.Log;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Source;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasFocus;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;

import androidx.test.espresso.action.ViewActions;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import java.util.HashMap;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CreateEntrantProfileTest {
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

    // This string is specific to Emma's laptop and would need to be changed for tests on a different device
    private String deviceId = "192d018653d8f5a5";

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
    public void testCreateUserButton() {

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

        Task<?>[] checkDatabase = new Task[] {

            // Test connection to FireBase
            db.collection("user").document("testDoc").set(new HashMap<String, Object>())
                    .addOnSuccessListener(aVoid -> Log.d("Firestore Test", "Test write successful"))
                    .addOnFailureListener(e -> Log.w("Firestore Test", "Test write failed", e))
        };

        Task<?>[] removeData = new Task[] {
            db.collection("user").document("testDoc")
                    .delete()
                    .addOnSuccessListener(aVoid -> Log.d("Firestore Test", "Test user deleted"))
                    .addOnFailureListener(e -> Log.w("Firestore Test", "Failed to delete test user", e))
        };

        Tasks.whenAllComplete(checkDatabase);
        Tasks.whenAllComplete(removeData);

        // Check that fragment closes and MainActivity is displayed
        onView(withId(R.id.button_notifications)).check(matches(isDisplayed()));
        onView(withId(R.id.button_profile)).check(matches(isDisplayed()));
    }

    /**
     * Test to check that addUser method from CreateEntrantProfileFragment properly adds the user to firebase
     */
    @Test
    public void testCreateUserInFirebase() {

        FragmentScenario<CreateEntrantProfileFragment> scenario = FragmentScenario.launchInContainer(
                CreateEntrantProfileFragment.class,
                CreateEntrantProfileFragment.newInstance().getArguments()
        );

        scenario.onFragment(createdFragment -> {
            testUserId = "test-user-id";
            testUserName = "test-user-name";
            testUserEmail = "test@email.com";
            testUserPhone = "1234567890";
            testUserIsOrganizer = false;
            testUserIsEntrant = true;
            testUserFacility = null;
            testUserHasNotifications = true;

            testUser = new User(testUserName, testUserEmail, testUserPhone, testUserId, testUserIsOrganizer, testUserIsEntrant, testUserFacility, testUserHasNotifications);
            createdFragment.addUser(testUser);

            Task<DocumentSnapshot> checkDatabase = db.collection("user").document(testUserId).get(Source.SERVER)
                            .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("Firestore Test", "User found in Firestore!");
                                assertTrue("User was successfully added to Firestore", document.exists());
                                assertEquals("User name should match", testUserName, document.getString("name"));
                                assertEquals("User email should match", testUserEmail, document.getString("email"));
                                assertEquals("User phone should match", testUserPhone, document.getString("phone"));
                                assertEquals("User Id should match", testUserId, document.getString("id"));
                                assertEquals("User organizer status should match", testUserIsOrganizer, document.getBoolean("organizer"));
                                assertEquals("User entrant status should match", testUserIsEntrant, document.getBoolean("entrant"));
                                assertEquals("User facility should match", testUserFacility, document.get("organizer", Facility.class));
                                assertEquals("User notification status should match", testUserHasNotifications, document.getBoolean("notificationStatus"));
                            }

                            if (!document.exists()) {
                                fail("User was not found in Firestore");
                            }
                        }

                        if (!task.isSuccessful()) {
                            FirebaseFirestoreException e = (FirebaseFirestoreException) task.getException();
                            Log.w("Firestore Test", "Error verifying user", e);
                            fail("Failed to retrieve user from Firestore");
                        }

            });

            Tasks.whenAll(checkDatabase);

            removeData();

        });
    }

    /**
     * Test to check empty (and therefore invalid) user input in name editText field
     */
    @Test
    public void testEmptyUserName() {
        testInput("", "test@email.com", "1234567890");
        onView(withId(R.id.editText_create_user_name)).check(matches(hasFocus()));
    }

    /**
     * Test to check empty (and therefore invalid) user input in email editText field
     */
    @Test
    public void testEmptyEmail() {
        testInput("Test User", "", "1234567890");
        onView(withId(R.id.editText_create_user_email)).check(matches(hasFocus()));
    }

    /**
     * Test to check invalid user input in email editText field
     */
    @Test
    public void testInvalidEmail() {
        testInput("Test User", "test@email", "1234567890");
        onView(withId(R.id.editText_create_user_email)).check(matches(hasFocus()));
    }

    /**
     * Test to check invalid user input in phoneNumber editText field
     * Currently fails: Failed to retrieve user from Firestore
     */
    @Test
    public void testInvalidPhone() {
        testInput("Test User", "test@email.com", "1");
        onView(withId(R.id.editText_create_user_phone)).check(matches(hasFocus()));
    }

    /**
     * A method to be performed after the test method to remove any data that was added to the firebase
     */
    private void removeData() {
        if (testUserId != null) {

        }
    }

    /**
     * Method to verify that the user was added to the firebase
     * @param testUserId The unique id of a User
     */
    private void verifyUserInFirestore(String testUserId) {
        db.collection("user").document(testUserId).get(Source.SERVER)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("Firestore Test", "User found in Firestore!");
                            assertTrue("User was successfully added to Firestore", document.exists());
                            assertEquals("User name should match", testUserName, document.getString("name"));
                            assertEquals("User email should match", testUserEmail, document.getString("email"));
                            assertEquals("User phone should match", testUserPhone, document.getString("phone"));
                            assertEquals("User Id should match", testUserId, document.getString("id"));
                            assertEquals("User organizer status should match", testUserIsOrganizer, document.getBoolean("organizer"));
                            assertEquals("User entrant status should match", testUserIsEntrant, document.getBoolean("entrant"));
                            assertEquals("User facility should match", testUserFacility, document.get("organizer", Facility.class));
                            assertEquals("User notification status should match", testUserHasNotifications, document.getBoolean("notificationStatus"));
                        }

                        if (!document.exists()) {
                            fail("User was not found in Firestore");
                        }
                    }

                    if (!task.isSuccessful()) {
                        FirebaseFirestoreException e = (FirebaseFirestoreException) task.getException();
                        Log.w("Firestore Test", "Error verifying user", e);
                        fail("Failed to retrieve user from Firestore");
                    }
                });

    }

    /**
     * Method to fill user input fields with given parameters and click the "Create" button
     * Used as a form of data provider to test for focus set on fields with invalid inputs
     * @param name String entered into the name editText field
     * @param email String entered into the email editText field
     * @param phoneNumber String entered into the phoneNumber editText field
     */
    private void testInput(String name, String email, String phoneNumber) {
        testUserName = name;
        testUserEmail = email;
        testUserPhone = phoneNumber;

        FragmentScenario<CreateEntrantProfileFragment> scenario = FragmentScenario.launchInContainer(
                CreateEntrantProfileFragment.class,
                CreateEntrantProfileFragment.newInstance().getArguments()
        );

        onView(withId(R.id.editText_create_user_name)).perform(ViewActions.typeText(testUserName));
        onView(withId(R.id.editText_create_user_email)).perform(ViewActions.typeText(testUserEmail));
        onView(withId(R.id.editText_create_user_phone)).perform(ViewActions.typeText(testUserPhone));
        onView(withId(R.id.create_user_profile_button)).perform(click());
    }
}
