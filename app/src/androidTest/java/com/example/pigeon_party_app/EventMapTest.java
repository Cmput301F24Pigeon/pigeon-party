package com.example.pigeon_party_app;

import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This class is meant to test the entrant map fragment (testing that markers are added to the map image).
 * The tests will fail because the fragment relies on firebase during its lifecycle
 */
/*
public class EventMapTest {
    private EntrantMapFragment fragment;
    private FrameLayout markerContainer;
    private ImageView worldMap;

    @Before
    public void setUp() {
        fragment = new EntrantMapFragment();

        markerContainer = new FrameLayout(ApplicationProvider.getApplicationContext());
        worldMap = new ImageView(ApplicationProvider.getApplicationContext());

        fragment.markerContainer = markerContainer;
        fragment.worldMap = worldMap;
        fragment.eventId = "testEventId";
    }

    @Test
    public void testAddMarker() {
        FragmentScenario<EntrantMapFragment> scenario = FragmentScenario.launchInContainer(EntrantMapFragment.class);

        scenario.onFragment(fragment -> {
            fragment.markerContainer = markerContainer;
            fragment.worldMap = worldMap;
            fragment.eventId = "testEventId";

            double latitude = 37.7749;
            double longitude = -122.4194;
            String participantName = "Test User";

            fragment.addMarker(latitude, longitude, participantName);

            assertEquals(1, markerContainer.getChildCount());

            ImageView addedMarker = (ImageView) markerContainer.getChildAt(0);
            assertNotNull(addedMarker);
        });
        }
    }
*/