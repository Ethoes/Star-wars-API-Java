package com.starwars.rest;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class contains the merging algorithm used to sort the names.
 */
public class MergeSort {

    /**
     * This is the method which sorts the entire array.
     * @param arr is the given array to sort, which only includes star wars characters.
     * @return a JSON list of star wars characters sorted by birth year.
     */
    public static JSONArray mergeSort(JSONArray arr) {
        //This is the base case, if the array is of size 1 or less, return it.
        if(arr.length() <= 1) {
            return arr;
        }

        //Divide the given array into two separate arrays divided on the half point.
        JSONArray L = getInRange(arr, 0, arr.length()/2);
        JSONArray R = getInRange(arr, (arr.length()/2), arr.length());

        //Make a recursive call for both sides
        L = mergeSort(L);
        R = mergeSort(R);

        //initialize counters to see where both arrays are.
        int countL = 0;
        int countR = 0;

        //Initialize the new array which both will be combined in.
        JSONArray temp = new JSONArray();
        for(int i = 0; i < L.length() + R.length(); i++) {
            //If the left side is fully added, add from the right side.
            if(countL == L.length()) {
                temp.put(R.getJSONObject(countR));
                countR ++;

            //If the Left side is fully added, add from the right side.
            } else if(countR == R.length()) {
                temp.put(L.getJSONObject(countL));
                countL++;

            //If neither are done yet, compare the first next birthday of both sides and add the smallest one.
            } else if(getBirthDayInteger(L.getJSONObject(countL)) > getBirthDayInteger(R.getJSONObject(countR))) {
                temp.put(R.getJSONObject(countR));
                countR ++;
            } else {
                temp.put(L.getJSONObject(countL));
                countL ++;
            }
        }
        //return the sorted array.
        return temp;
    }

    /**
     * This method creates a subarray based on the given indexes.
     * @param array is the given array of which a subarray needs to be created.
     * @param from is the starting index.
     * @param to is the ending index.
     * @return a sub JSON array using the given indexes.
     */
    private static JSONArray getInRange(JSONArray array, int from, int to) {
        //Initialize the new array.
        JSONArray temp = new JSONArray();
        //Loop through the array from the given index and add from there.
        for(int i = from; i < to; i++) {
            temp.put(array.getJSONObject(i));
        }
        //return the new array.
        return temp;
    }

    /**
     * This method converts the birth year string in a person object to a usable float.
     * @param person is the person of which the birth year needs to be returned.
     * @return the birth year based on the person.
     */
    private static float getBirthDayInteger(JSONObject person)  {
        //Retrieve the string for the birth year.
        String birthYear = person.getString("birth_year");

        //If it is unknown set the year to 9999 to set them to the end of the list.
        if(birthYear.equals("unknown")) {
            return 9999;
        }

        //Separate the suffix from the number (so BBY from 8BBY for instance)
        String suffix = birthYear.substring(birthYear.length() - 3, birthYear.length());
        String numberString = birthYear.substring(0, birthYear.length() - 3);
        //change the string to a float.
        float birthday = Float.parseFloat(numberString);
        //If the suffix BBY is included set it to - of itself.
        if(suffix.equals("BBY")) {
            birthday = -birthday;
        }

        //Return the found float.
        return birthday;
    }
}
