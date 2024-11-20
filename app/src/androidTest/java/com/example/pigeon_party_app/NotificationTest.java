package com.example.pigeon_party_app;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Date;
//https://stackoverflow.com/questions/33495294/testing-notifications-in-android
/**
 * This class that the notifications are properly sent by the app and received by the user
 */
@RunWith(AndroidJUnit4.class)
public class NotificationTest {
    private String testUserId = "test-id";
    private UiDevice device;
    private Context context;
    private Facility testFacility;

    /**
     * This method is performed before the tests to set up the device and any necessary variables for UI testing
     */
    @Before
    public void setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        context = ApplicationProvider.getApplicationContext();
        testFacility = new Facility("owner-id","address","name");
    }

    /**
     * This method tests the notifyUser method by opening the app, allowing notifications, sending a notification, and then checking that it  is received.
     * @throws UiObjectNotFoundException
     */
    @Test
    public void testNotificationIsReceived() throws UiObjectNotFoundException {
        device.pressHome();
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.example.pigeon_party_app");
        context.startActivity(launchIntent);


        UiObject permissionDialog = device.findObject(new UiSelector().text("Allow"));
        if (permissionDialog.waitForExists(6000)) {
            permissionDialog.click();
        }

        NotificationHelper notificationHelper = new NotificationHelper(context);
        User testUser = new User("test-name","test-email",null,testUserId,true,true,testFacility,true, "#000000", new ArrayList<Event>(), new ArrayList<Event>());
        Event testEvent = new Event("test-id","title",new Date(),50,"details",testFacility,false,null,null,null,testUser);

        device.openNotification();

        notificationHelper.notifyUser(testUser,"This is a test notification");

        UiObject notificationText = device.findObject(new UiSelector().text("This is a test notification"));
        if (notificationText.waitForExists(4000)){
            assertTrue("Notification was received", notificationText.exists());
        }
        else{
            fail("Notification was not received");
        }
    }

    /**
     * This method tests that a user can disable their notification in app.
     * @throws UiObjectNotFoundException
     */
    @Test
    public void testNotificationIsNotReceived() throws UiObjectNotFoundException{
        device.pressHome();
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.example.pigeon_party_app");
        context.startActivity(launchIntent);


        UiObject permissionDialog = device.findObject(new UiSelector().text("Allow"));
        if (permissionDialog.waitForExists(2000)) {
            permissionDialog.click();
        }

        NotificationHelper notificationHelper = new NotificationHelper(context);
        User testUser = new User("test-name","test-email",null,testUserId,true,true,testFacility,true, "#000000", new ArrayList<Event>(), new ArrayList<Event>());
        Event testEvent = new Event("test-id","title",new Date(),50,"details",testFacility,false,null,null,null,testUser);
        testUser.setNotificationsOn(false);
        device.openNotification();


        notificationHelper.notifyUser(testUser,"This is a test notification");

        UiObject notificationText = device.findObject(new UiSelector().text("This is a test notification"));
        if (notificationText.waitForExists(2000)){
            fail("Notification was received");
        }

    }
}
