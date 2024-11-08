package com.example.pigeon_party_app;

import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@RunWith(AndroidJUnit4.class)
public class InvitationTest {


        private FirebaseFirestore db;
        private Facility testFacility;
        private User testUser;
        private String testUserID;
        private Event testAcceptEvent;
        private Event testDeclineEvent;
        private UiDevice device;
        Context context;
        private Map<String, Map<String, Object>> testUserMap;

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
            testFacility = new Facility("test-user-id", "test-address", "test-name");
            testUser = new User("test-user-name", "test@email.com", null, testUserID, true, true, testFacility, false, new ArrayList<Event>(), new ArrayList<Event>());
            testAcceptEvent = new Event("testEventId", "testEventTitle", new Date(), 50, "testEventDetails", testFacility, false, testUserMap, testUserMap, testUserMap, testUserMap, testUser);
            testAcceptEvent.addUserToSentInvite(testUser);
            db.collection("events").document(testAcceptEvent.getEventId())
                    .set(testAcceptEvent)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("FireStore", "Event successfully added");
                    })
                    .addOnFailureListener(e -> {
                        Log.w("FireStore", "Error adding event", e);
                    });

        }
        @Test
        public void acceptInviteTest(){
            device.pressHome();
            Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);

            UiObject2 eventList = device.wait(Until.findObject(By.res("com.pigeon_party_app:id/event_list")), 5000);


            UiObject2 eventItem = eventList.findObject(By.text("testEventTitle"));
            eventItem.click();

            // Wait for event details screen to open
            UiObject2 acceptButton = device.wait(Until.findObject(By.desc("acceptButton")), 5000); // Replace with the actual description, text, or ID
            acceptButton.click();

            // Verify if the user was added (this might depend on your app's state, i.e., checking if the button becomes disabled or if the status updates)
            // Example: Verify the event's status or that the "Accept" button changes
            UiObject2 acceptedStatus = device.wait(Until.findObject(By.text("Accepted")), 5000); // Change text to match your app's "accepted" state
            assertNotNull("Event acceptance failed", acceptedStatus);
        }


    }


