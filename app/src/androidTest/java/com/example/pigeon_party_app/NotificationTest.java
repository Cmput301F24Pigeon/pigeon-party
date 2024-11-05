package com.example.pigeon_party_app;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.os.SystemClock;
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

import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class NotificationTest {
    private String testUserId = "test-id";
    private UiDevice device;
    private Context context;
    private Facility testFacility;
    @Before
    public void setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        context = ApplicationProvider.getApplicationContext();
        testFacility = new Facility("owner-id","address","name");
    }

    @Test
    public void testNotificationIsReceived() throws UiObjectNotFoundException {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        User testUser = new User("test-name","test-email",null,testUserId,true,true,testFacility,true); // Initialize as needed
        Event testEvent = new Event("test-id","title",new Date(),50,"details",testFacility,false,null,null,null,testUser);
        notificationHelper.notifyUser(testUser, testEvent, "This is a test notification");

        device.openNotification();

        UiObject notificationText = device.findObject(new UiSelector().text("This is a test notification"));
        SystemClock.sleep(12000);
        assertTrue("Notification was not received", notificationText.exists());
    }
}
