package com.example.pigeon_party_app;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class SendNotificationsFragmentTest {
    private FirebaseFirestore db;
    private HashMap<String,Object> testUserMap;
    private Facility testFacility;
    private User testUser;
    private Event testEvent;
    private UiDevice device;
    private Context context;
    private Map<String, Object> userDetails;

    @Before
    public void setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        context = ApplicationProvider.getApplicationContext();
        db = FirebaseFirestore.getInstance();
        testUserMap = new HashMap<>();
        userDetails = testEvent.createUserDetails(testUser,"invited");

        testFacility =  new Facility("test-user-id", "test-address","test-name");
        testUser = new User("test-user-name","test@email.com",null,"test-user-id",true,true,testFacility,false);
        testEvent = new Event("testEventId","testEventTitle", new Date(), 50, "testEventDetails",testFacility,false,testUserMap, testUserMap, testUserMap, testUser);
        testEvent.addUserToCancelled(testUser);
        testEvent.addUserToInvited(testUser);
        testEvent.addUserToWaitlist(testUser);
        db.collection("events").document(testEvent.getEventId())
                .set(testEvent)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FireStore", "Event successfully added");
                })
                .addOnFailureListener(e ->{
                    Log.w("FireStore", "Error adding event", e);
                });

    }

    @Test
    public void testConfirmButtonSendsNotifications() throws UiObjectNotFoundException {
        FragmentScenario<SendNotificationsFragment> scenario = FragmentScenario.launchInContainer(SendNotificationsFragment.class);

        scenario.onFragment(fragment -> {

            View view = fragment.getView();
            CheckBox waitlistedCheckBox = view.findViewById(R.id.check_waitlist);
            CheckBox selectedCheckBox = view.findViewById(R.id.check_selected);
            CheckBox cancelledCheckBox = view.findViewById(R.id.check_cancelled);

            waitlistedCheckBox.setChecked(false);
            selectedCheckBox.setChecked(true);
            cancelledCheckBox.setChecked(false);

            Button positiveButton = ((AlertDialog) fragment.getDialog()).getButton(DialogInterface.BUTTON_POSITIVE);
            positiveButton.performClick();
        });

        device.openNotification();
        UiObject notificationText = device.findObject(new UiSelector().text("Congratulations! You have been selected for the event: testEventTitle"));
        if (notificationText.waitForExists(4000)){
            assertTrue("Notification was received", notificationText.exists());
        }
        else{
            fail("Notification was not received");
        }
    }
}
