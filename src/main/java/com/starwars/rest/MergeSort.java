package com.starwars.rest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Random;

public class MergeSort {
    public static JSONArray mergeSort(JSONArray arr) {
        if(arr.length() <= 1) {
            return arr;
        }

        JSONArray L = getInRange(arr, 0, arr.length()/2);
        JSONArray R = getInRange(arr, (arr.length()/2), arr.length());
        L = mergeSort(L);
        R = mergeSort(R);

        int countL = 0;
        int countR = 0;
        JSONArray temp = new JSONArray();
        for(int i = 0; i < L.length() + R.length(); i++) {
            if(countL == L.length()) {
                temp.put(R.getJSONObject(countR));
                countR ++;
            } else if(countR == R.length()) {
                temp.put(L.getJSONObject(countL));
                countL++;
            } else if(getBirthDayInteger(L.getJSONObject(countL)) > getBirthDayInteger(R.getJSONObject(countR))) {
                temp.put(R.getJSONObject(countR));
                countR ++;
            } else {
                temp.put(L.getJSONObject(countL));
                countL ++;
            }
        }
        return temp;
    }

    private static JSONArray getInRange(JSONArray array, int from, int to) {
        JSONArray temp = new JSONArray();
        for(int i = 0; i < (to - from); i++) {
            temp.put(array.getJSONObject(from + i));
        }
        return temp;
    }

    private static float getBirthDayInteger(JSONObject person)  {
        String birthYear = person.getString("birth_year");
        if(birthYear.equals("unknown")) {
            return 9999;
        }
        String suffix = birthYear.substring(birthYear.length() - 3, birthYear.length());
        String numberString = birthYear.substring(0, birthYear.length() - 3);
        float birthday = Float.parseFloat(numberString);
        if(suffix.equals("BBY")) {
            birthday = -birthday;
        }
        return birthday;
    }
}
