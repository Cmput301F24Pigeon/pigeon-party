package com.example.pigeon_party_app;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Map;

public class FacilityTest {

    private Facility facility;

    /**
     * Method to be performed before the test to ensure the necessary attributes are created first
     */
    @Before
    public void setUp() {
        facility = new Facility("test-user-id", "123 Anywhere Street", "Test Location");
    }

    /**
     * Tests creating a Facility using the Facility constructor
     */
    @Test
    public void testFacilityConstructor() {
        assertEquals("test-user-id", facility.getOwnerId());
        assertEquals("123 Anywhere Street", facility.getAddress());
        assertEquals("Test Location", facility.getName());
    }

    /**
     * Tests Facility class setters and getters
     */
    @Test
    public void testSettersAndGetters() {
        facility.setName("New Name");
        facility.setAddress("456 New Place");

        assertEquals("New Name", facility.getName());
        assertEquals("456 New Place", facility.getAddress());
        assertEquals("test-user-id", facility.getOwnerId());
    }

    /**
     * Tests creating a Map from a Facility object
     */
    @Test
    public void testToMap() {
        Map<String, Object> facilityMap = facility.toMap();

        assertTrue(facilityMap.containsKey("address"));
        assertTrue(facilityMap.containsKey("name"));
        assertTrue(facilityMap.containsKey("ownerId"));

        assertEquals("123 Anywhere Street", facilityMap.get("address"));
        assertEquals("Test Location", facilityMap.get("name"));
        assertEquals("test-user-id", facilityMap.get("ownerId"));
    }

    /**
     * Tests creating a Map from an empty Facility object
     */
    @Test
    public void testToMapEmptyValues() {
        Facility emptyFacility = new Facility("", "", "");

        Map<String, Object> emptyFacilityMap = emptyFacility.toMap();

        assertTrue(emptyFacilityMap.containsKey("address"));
        assertTrue(emptyFacilityMap.containsKey("name"));
        assertTrue(emptyFacilityMap.containsKey("ownerId"));

        assertEquals("", emptyFacilityMap.get("address"));
        assertEquals("", emptyFacilityMap.get("name"));
        assertEquals("", emptyFacilityMap.get("ownerId"));
    }
}
