package com.example.pigeon_party_app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import android.content.Context;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class SendNotificationsFragmentTest {
    private FirebaseFirestore db;
    private Facility testFacility;
    private User testUser;
    private String testUserID;
    private Event testEvent;
    private UiDevice device;
    Context context;
    private Map<String,User> testUserMap;

    /**
     * Method which creates the necessary variables to carry out the test
     */
    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        testUserID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        context = ApplicationProvider.getApplicationContext();
        db = FirebaseFirestore.getInstance();
        testUserMap = new HashMap<>();
        testFacility =  new Facility("test-user-id", "test-address","test-name");
        testUser = new User("test-user-name","test@email.com",null,testUserID,true,true,testFacility,false, "#000000", new ArrayList<Event>(), new ArrayList<Event>());
        testEvent = new Event("testEventId","testEventTitle", new Date(), 50, "testEventDetails",testFacility,false,testUserMap, testUserMap, testUserMap, testUserMap, testUser);
        testEvent.addUserToInvited(testUser);
        db.collection("events").document(testEvent.getEventId())
                .set(testEvent)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FireStore", "Event successfully added");
                })
                .addOnFailureListener(e ->{
                    Log.w("FireStore", "Error adding event", e);
                });

    }

    /**
     * Method to test that SendNotificationFragment works properly by sending notification to a test user
     * @throws UiObjectNotFoundException
     */
    @Test
    public void testSendsAcceptedNotifications() throws UiObjectNotFoundException {
        device.pressHome();
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        scenario.onActivity(activity  -> {

            SendNotificationsFragment dialogFragment = SendNotificationsFragment.newInstance(testEvent);
            dialogFragment.show(activity.getSupportFragmentManager(), "sendNotificationsDialog");
        });
        SystemClock.sleep(4000);
        UiObject permissionDialog = device.findObject(new UiSelector().text("Allow"));
        if (permissionDialog.waitForExists(4000)) {
            permissionDialog.click();
        }
        onView(withId(R.id.check_accepted)).perform(click());
        onView(withText("Confirm")).perform(click());
        scenario.close();

        ActivityScenario<MainActivity> relaunchScenario = ActivityScenario.launch(MainActivity.class);

        device.openNotification();
        UiObject notificationText = device.findObject(new UiSelector().text("Congratulations! You have joined the event: testEventTitle"));
        if (notificationText.waitForExists(4000)){
            assertTrue("Notification was received", notificationText.exists());
        }
        else{
            fail("Notification was not received");
        }
    }

    /**
     * This method tests that organizers can send a notification to users who are on the waitlist.
     * @throws UiObjectNotFoundException
     */
    @Test
    public void testWaitlistNotifications() throws UiObjectNotFoundException {
        device.pressHome();
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        scenario.onActivity(activity  -> {

            SendNotificationsFragment dialogFragment = SendNotificationsFragment.newInstance(testEvent);
            dialogFragment.show(activity.getSupportFragmentManager(), "sendNotificationsDialog");
        });
        SystemClock.sleep(4000);
        UiObject permissionDialog = device.findObject(new UiSelector().text("Allow"));
        if (permissionDialog.waitForExists(4000)) {
            permissionDialog.click();
        }
        onView(withId(R.id.check_waitlist)).perform(click());
        onView(withText("Confirm")).perform(click());
        scenario.close();

        ActivityScenario<MainActivity> relaunchScenario = ActivityScenario.launch(MainActivity.class);

        device.openNotification();
        UiObject notificationText = device.findObject(new UiSelector().text("You are currently on the waitlist for the event: testEventTitle"));
        if (notificationText.waitForExists(4000)){
            assertTrue("Notification was received", notificationText.exists());
        }
        else{
            fail("Notification was not received");
        }
    }
    @Test
    public void testInvitedNotifications() throws UiObjectNotFoundException {
        device.pressHome();
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        scenario.onActivity(activity  -> {

            SendNotificationsFragment dialogFragment = SendNotificationsFragment.newInstance(testEvent);
            dialogFragment.show(activity.getSupportFragmentManager(), "sendNotificationsDialog");
        });
        SystemClock.sleep(4000);
        UiObject permissionDialog = device.findObject(new UiSelector().text("Allow"));
        if (permissionDialog.waitForExists(4000)) {
            permissionDialog.click();
        }
        onView(withId(R.id.check_invited)).perform(click());
        onView(withText("Confirm")).perform(click());
        scenario.close();

        ActivityScenario<MainActivity> relaunchScenario = ActivityScenario.launch(MainActivity.class);

        device.openNotification();
        UiObject notificationText = device.findObject(new UiSelector().text("Congratulations! You have been invited to the event: testEventTitle"));
        if (notificationText.waitForExists(4000)){
            assertTrue("Notification was received", notificationText.exists());
        }
        else{
            fail("Notification was not received");
        }
    }

    /**
     * This method tests that the organizer can send notifications to users who "lost" the lottery.
     * @throws UiObjectNotFoundException
     */
    @Test
    public void testCancelledNotifications() throws UiObjectNotFoundException {
        device.pressHome();
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        scenario.onActivity(activity  -> {

            SendNotificationsFragment dialogFragment = SendNotificationsFragment.newInstance(testEvent);
            dialogFragment.show(activity.getSupportFragmentManager(), "sendNotificationsDialog");
        });
        SystemClock.sleep(4000);
        UiObject permissionDialog = device.findObject(new UiSelector().text("Allow"));
        if (permissionDialog.waitForExists(4000)) {
            permissionDialog.click();
        }
        onView(withId(R.id.check_cancelled)).perform(click());
        onView(withText("Confirm")).perform(click());
        scenario.close();

        ActivityScenario<MainActivity> relaunchScenario = ActivityScenario.launch(MainActivity.class);

        device.openNotification();
        UiObject notificationText = device.findObject(new UiSelector().text("Sorry, you have not been selected for the event: testEventTitle"));
        if (notificationText.waitForExists(4000)){
            assertTrue("Notification was received", notificationText.exists());
        }
        else{
            fail("Notification was not received");
        }
    }
    /**
     * Method to remove the event data that was added to the firebase for the test
     */
    @After
    public void tearDown() {
        if (testEvent != null) {
            db.collection("events").document(testEvent.getEventId())
                    .delete()
                    .addOnSuccessListener(aVoid -> Log.d("Firestore Test", "Test event deleted"))
                    .addOnFailureListener(e -> Log.w("Firestore Test", "Failed to delete test event", e));
        }
    }
}
