package com.example.pigeon_party_app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.util.Log;

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


@RunWith(AndroidJUnit4.class)
public class ViewNotificationsFragmentTest {
    private FirebaseFirestore db;
    private User testUser_notificationsOn;
    private User testUser_notificationsOff;
    private String testUserId;


    /**
     * Method to be performed before the test to ensure the necessary attributes are created first
     * Start with a user that has notifications turned on (default setting)
     */
    @Before
    public void setUp() {
        testUserId = "test-user-id";
        testUser_notificationsOn = new User("test-user-notifications_on", "test@email.com", null, testUserId, true, true, null, true);
        testUser_notificationsOff = new User("test-user-notifications_on", "test@email.com", null, testUserId, true, true, null, false);
    }

    /**
     * Test to check that updateUserNotificationStatus method from ViewNotificationsFragment properly updates the user's notificationStatus in firebase
     * Create a user that has notifications turned off to mimic clicking switch
     */
    @Test
    public void testUpdateUserNotificationStatus_startNotificationsOn() {

        FragmentScenario<ViewNotificationsFragment> scenario = FragmentScenario.launchInContainer(
                ViewNotificationsFragment.class,
                ViewNotificationsFragment.newInstance(testUser_notificationsOn).getArguments()
        );

        scenario.onFragment(createdFragment -> {
            onView(withId(R.id.notification_switch)).perform(ViewActions.click());
        });

        assertFalse(testUser_notificationsOn.hasNotificationsOn());
    }

    @Test
    public void testUpdateUserNotificationStatus_startNotificationsOff() {
        FragmentScenario<ViewNotificationsFragment> scenario = FragmentScenario.launchInContainer(
                ViewNotificationsFragment.class,
                ViewNotificationsFragment.newInstance(testUser_notificationsOff).getArguments()
        );

        scenario.onFragment(createdFragment -> {
            onView(withId(R.id.notification_switch)).perform(ViewActions.click());
        });

        assertTrue(testUser_notificationsOff.hasNotificationsOn());
    }
}
