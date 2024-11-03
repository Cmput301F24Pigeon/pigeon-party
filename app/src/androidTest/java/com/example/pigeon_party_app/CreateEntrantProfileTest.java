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

    /**
     * Method to be performed before the test to ensure the necessary attributes are created first
     */
    @Before
    public void setUp() {
        db = FirebaseFirestore.getInstance();
        testUser = new User("test-user-name","test@email.com","1234567890","test-user-id",false,true,null,true);
    }

    /**
     * Test to check that addUser method from CreateEntrantProfileFragment properly adds the user to firebase
     */
    @Test
    public void testCreateUser() {

        FragmentScenario<CreateEntrantProfileFragment> scenario = FragmentScenario.launchInContainer(
                CreateEntrantProfileFragment.class,
                CreateEntrantProfileFragment.newInstance().getArguments()
        );

        scenario.onFragment(createdFragment -> {
            testUserId = "test-user-id";
            testUserName = "test-user-name";
            testUserEmail = "test-user-email";
            testUserPhone = "";
            testUserIsOrganizer = false;
            testUserIsEntrant = true;
            testUserFacility = null;
            testUserHasNotifications = true;

            User testUser = new User(testUserName, testUserEmail, testUserPhone, testUserId, testUserIsOrganizer, testUserIsEntrant, testUserFacility, testUserHasNotifications);
            createdFragment.addUser(testUser);

            verifyEventInFirestore(testUserId);
        });
    }

    /**
     * Method to verify that the user was added to the firebase
     * @param testUserId The unique id of a User
     */
    private void verifyEventInFirestore(String testUserId) {
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
     * A method to be performed after the test method to remove any data that was added to the firebase
     */
    @After
    public void removeData() {
        if (testUserId != null) {
            db.collection("user").document(testUserId)
                    .delete()
                    .addOnSuccessListener(aVoid -> Log.d("Firestore Test", "Test user deleted"))
                    .addOnFailureListener(e -> Log.w("Firestore Test", "Failed to delete test user", e));
        }
    }
}
