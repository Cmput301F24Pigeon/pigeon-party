package com.example.pigeon_party_app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.util.Log;

import androidx.fragment.app.testing.FragmentScenario;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

public class AdminFragmentTest {
    private FragmentScenario<AdminFragment> scenario;



    @Before
    public void setUp() {
        scenario = FragmentScenario.launchInContainer(AdminFragment.class);
    }

    @Test
    public void testBrowseProfilesNavigation() {
        onView(withId(R.id.browse_profiles)).perform(click());

        onView(withId(R.id.user_list)).check(matches(isDisplayed()));
    }

    @Test
    public void testBrowseEventsNavigation() {
        onView(withId(R.id.browse_events)).perform(click());

        onView(withId(R.id.event_list)).check(matches(isDisplayed()));
    }

    @Test
    public void testBrowseImagesNavigation() {
        onView(withId(R.id.browse_images)).perform(click());
        onView(withId(R.id.image_list)).check(matches(isDisplayed()));

    }
}

