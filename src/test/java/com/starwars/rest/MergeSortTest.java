package com.starwars.rest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static com.starwars.rest.MergeSort.mergeSort;

public class MergeSortTest {

    @Test
    public void smallArrayTest() {
        try {
            //Create three mock characters for the array
            JSONObject personOne = new JSONObject();
            personOne.put("birth_year", "200BBY");
            JSONObject personTwo = new JSONObject();
            personTwo.put("birth_year", "12BBY");
            JSONObject personThree = new JSONObject();
            personThree.put("birth_year", "120ABY");

            //create test array (12BBY, 120ABY, 200BBY)
            JSONArray test = new JSONArray();
            test.put(personTwo);
            test.put(personThree);
            test.put(personOne);

            test = mergeSort(test);

            //create comparing array (200BBY, 12BBY, 120BBY)
            JSONArray compare = new JSONArray();
            compare.put(personOne);
            compare.put(personTwo);
            compare.put(personThree);

            assertTrue(test.equals(compare));
        } catch(org.json.JSONException e) {
            assert false;
        }
    }

    @Test
    public void emptyArrayTest() {
        JSONArray empty = new JSONArray();

        empty = mergeSort(empty);

        assertTrue(empty.equals(new JSONArray()));
    }

    @Test
    public void noBirthField() {
        try {
            //Create three mock characters for the array
            JSONObject personOne = new JSONObject();
            personOne.put("birth", "200BBY");
            JSONObject personTwo = new JSONObject();
            personTwo.put("birth_year", "12BBY");

            //create test array (12BBY, 120ABY)
            JSONArray test = new JSONArray();
            test.put(personTwo);
            test.put(personOne);

            test = mergeSort(test);

            assert false;
        } catch(org.json.JSONException e) {
            //If an error is thrown then that is as desired.
            assert true;
        }
    }
}
