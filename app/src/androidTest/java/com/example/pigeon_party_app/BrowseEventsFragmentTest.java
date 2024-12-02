package com.example.pigeon_party_app;


import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

@RunWith(AndroidJUnit4.class)
public class BrowseEventsFragmentTest {


    class CityListTest {
        private Eventlist mockEventList() {
            EventList eventList = new CityList();
            cityList.add(mockCity());
            return cityList;
        }
        private City mockCity() {
            return new City("Edmonton", "Alberta");
        }

        @Test
        void testAdd() {
            CityList cityList = mockCityList();
            assertEquals(1, cityList.getCities().size());
            City city = new City("Regina", "Saskatchewan");
            cityList.add(city);
            assertEquals(2, cityList.getCities().size());
            assertTrue(cityList.getCities().contains(city));
        }
        @Test
        void testAddException() {
            CityList cityList = mockCityList();
            City city = new City("Yellowknife", "Northwest Territories");
            cityList.add(city);
            assertThrows(IllegalArgumentException.class, () -> {
                cityList.add(city);
            });
        }
        @Test
        void testGetCities() {
            CityList cityList = mockCityList();
// This line checks if the first city in the cityList (retrieved by cityList.getCities().get(0))
// is the same as the city returned by mockCity()
            assertEquals(0, mockCity().compareTo(cityList.getCities().get(0)));
// This pushes down the original city
            City city = new City("Charlottetown", "Prince Edward Island");
            cityList.add(city);
// Now the original city should be at position 1
            assertEquals(0, city.compareTo(cityList.getCities().get(0)));
            assertEquals(0, mockCity().compareTo(cityList.getCities().get(1)));
        }

        @Test
        void testHasCities(){
            CityList cityList = mockCityList();
//checks to see if the size is 1 for our city
            assertEquals(1, cityList.getCities().size());
            // now runs has city
            City city = new City("Edmonton", "Alberta");
// Checks to see if the size is now 0
            assertEquals(true, cityList.hasCity(city));
            assertEquals(false, cityList.hasCity(city));
            // now to check the error
            cityList.hasCity(city);
        }

        void testDeleteCities() {
            CityList cityList = mockCityList();
            City city = mockCity();
            //tests to see if city is deleted
            cityList.deleteCity(city);
            assertEquals(0, cityList.getCities().size());
            // now test the error
            assertThrows(IllegalArgumentException.class, () -> {
                cityList.deleteCity(city);
            });
        }

        void testCount() {
            //tests to see if countCity returns the count for our city
            CityList cityList = mockCityList();
            assertEquals(1, cityList.getCities().size());
            assertEquals(1, cityList.countCity());
        }

    }

}
