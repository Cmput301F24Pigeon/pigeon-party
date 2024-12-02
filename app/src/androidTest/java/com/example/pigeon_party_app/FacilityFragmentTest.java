package com.example.pigeon_party_app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasFocus;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.util.Log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Source;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

/**
 * This tests our facility fragment when we make a facility
 */
@RunWith(AndroidJUnit4.class)
public class FacilityFragmentTest {
    private FirebaseFirestore db;
    private String testFacilityName;
    private String testFacilityAddress;
    private Facility testFacility;
    private String userId;
    private User testUser;

    /**
     * Method to be performed before the test to ensure the necessary attributes are created first
     */
    @Before
    public void setUp() {
        db = FirebaseFirestore.getInstance();
        userId = "test-user-id";
        testUser = new User("test-user-name", "test@email.com", null, userId, true, true, testFacility, false, "#000000", new ArrayList<String>(), new ArrayList<String>(), false);
        db.collection("user").document(userId).set(testUser)
                .addOnSuccessListener(aVoid -> Log.d("Firestore Test", "Test user with facility added"))
                .addOnFailureListener(e -> fail("Failed to add test user with facility"));
    }

    /**
     * Test to check that CreateEventFragment properly adds the event to firebase
     */
    @Test
    public void testCreateFacility() {
        // add user to firebase without facility
        db.collection("user").document(testUser.getUniqueId()).set(testUser)
                .addOnSuccessListener(aVoid -> Log.d("Firestore Test", "Test user with facility added"))
                .addOnFailureListener(e -> fail("Failed to add test user with facility"));
        FragmentScenario<FacilityFragment> scenario = FragmentScenario.launchInContainer(
                FacilityFragment.class,
                FacilityFragment.newInstance(testUser).getArguments()
        );
        scenario.onFragment(createdFragment -> {
            testFacilityName = "test-facility-name";
            testFacilityAddress = "test-facility-address";
            Facility testFacility = new Facility(testUser.getUniqueId(), testFacilityAddress, testFacilityName);
            createdFragment.createFacility(db, testFacility);

            verifyFacilityInFirestore(testFacility.getOwnerId());
        });
    }

    /**
     * Method to test that the EditFacilityFragment properly edits a facility in firebase
     */
    @Test
    public void testEditFacility() {
        testFacilityName = "test-facility-name";
        testFacilityAddress = "test-facility-address";
        Facility testFacility = new Facility(testUser.getUniqueId(), testFacilityAddress, testFacilityName);
        testUser.setFacility(testFacility);
        // add user with facility to firebase
        db.collection("user").document(testUser.getUniqueId()).set(testUser)
                .addOnSuccessListener(aVoid -> Log.d("Firestore Test", "Test user with facility added"))
                .addOnFailureListener(e -> fail("Failed to add test user with facility"));
        FragmentScenario<EditFacilityFragment> scenario = FragmentScenario.launchInContainer(
                EditFacilityFragment.class,
                EditFacilityFragment.newInstance(testUser).getArguments()
        );

        scenario.onFragment(createdFragment -> {
            String newTestFacilityName = "new-test-facility-name";
            String newTestFacilityAddress = "new-test-facility-address";
            createdFragment.editFacility(db, testFacility, newTestFacilityName, newTestFacilityAddress);
            verifyFacilityInFirestore(testFacility.getOwnerId());
        });


    }

    /**
     * This method tests input validation for facility name
     */
    @Test
    public void testEmptyName() {
        testFacilityName = "";
        testFacilityAddress = "test-address";
        FragmentScenario<FacilityFragment> scenario = FragmentScenario.launchInContainer(
                FacilityFragment.class,
                FacilityFragment.newInstance(testUser).getArguments()
        );
        onView(withId(R.id.add_facility_name)).perform(ViewActions.typeText(testFacilityName));
        onView(withId(R.id.add_facility_address)).perform(ViewActions.typeText(testFacilityAddress));
        onView(withId(R.id.button_confirm)).perform(click());
        onView(withId(R.id.add_facility_name)).check(matches(hasFocus()));
    }

    /**
     * This method tests the input validation for facility address
     */
    @Test
    public void testEmptyAddress() {
        testFacilityAddress = "";
        testFacilityName = "test-address";
        FragmentScenario<FacilityFragment> scenario = FragmentScenario.launchInContainer(
                FacilityFragment.class,
                FacilityFragment.newInstance(testUser).getArguments()
        );
        onView(withId(R.id.add_facility_address)).perform(ViewActions.typeText(testFacilityAddress));
        onView(withId(R.id.add_facility_name)).perform(ViewActions.typeText(testFacilityName));
        onView(withId(R.id.button_confirm)).perform(click());
        onView(withId(R.id.add_facility_address)).check(matches(hasFocus()));
    }


    /**
     * Method to verify that the facility was added to the test user in firebase
     *
     * @param userId The unique id of the user who owns the facility
     */
    private void verifyFacilityInFirestore(String userId) {
        db.collection("user").document(userId).get(Source.SERVER)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("Firestore Test", "User found in Firestore!");
                            assertTrue("User was successfully added to Firestore", document.exists());
                            assertEquals("Facility should match", testFacility, document.toObject(Facility.class));

                        } else {
                            fail("User was not found in Firestore");
                        }
                    } else {
                        FirebaseFirestoreException e = (FirebaseFirestoreException) task.getException();
                        Log.w("Firestore Test", "Error verifying user", e);
                        fail("Failed to retrieve facility from Firestore");
                    }
                });

    }

    /**
     * A method to be performed after the test method to remove any data that was added to the firebase
     */
    @After
    public void tearDown() {
        if (testUser != null) {
            db.collection("user").document(testUser.getUniqueId())
                    .delete()
                    .addOnSuccessListener(aVoid -> Log.d("Firestore Test", "Test user deleted"))
                    .addOnFailureListener(e -> Log.w("Firestore Test", "Failed to delete test user", e));
        }
    }

}
