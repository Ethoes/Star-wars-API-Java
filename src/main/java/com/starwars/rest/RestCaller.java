package com.starwars.rest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

public class RestCaller {

    private JSONArray characters;
    private JSONArray movies;
    private JSONArray queries;

    public RestCaller() {
        //TODO grab characterURL and Query from file
        this.characters = new JSONArray();
        this.movies = new JSONArray();
        this.queries = new JSONArray();
    }

    protected String getNames(String name) {
        JSONObject prevQuery = getJSONObject(this.queries, name, "query");
        if(prevQuery != null) {
            return prevQuery.get("result").toString();
        }

        //Grab result via get request
        String result = RequestNames(name);

        //Turn response into JSON object
        JSONObject jason = new JSONObject(result);

        //Check for invalid results
        if(jason.getInt("count") > 1) {
            return "Multiple results found, please specify.";
        } else if(jason.getInt("count") <= 0) {
            return "No matches found, please check if the name was written correctly.";
        }

        JSONArray person = jason.getJSONArray("results");
        JSONArray people = this.getAllActors((JSONObject) person.get(0));

        JSONObject query = new JSONObject();
        query.put("query", name);
        query.put("result", people);
        queries.put(query);

        return name;
    }

    private static String RequestNames(String request) {
        //Create rest template and empty string for get request
        RestTemplate rt = new RestTemplate();
        String result = "";

        //Create try catch block in case of error
        try {
            result = rt.getForObject("https://swapi.dev/api/people/?search=" + request, String.class);
        } catch (Exception e) {
            return "Invalid request, server might be down";
        }
        return result;
    }

    private JSONArray getAllActors(JSONObject person) {
        ArrayList<String> peopleURL = new ArrayList<>();
        JSONArray people = new JSONArray();
        JSONArray films =  (JSONArray) person.get("films");
        RestTemplate rt = new RestTemplate();
        for(Object film: films) {
            JSONObject movie = getJSONObject(this.movies, (String) film, "url");
            if(movie == null) {
                String movieObject = rt.getForObject((String) film, String.class);
                getPeople(new JSONObject(movieObject), peopleURL);
                this.movies.put(new JSONObject(movieObject));
            } else {
                getPeople(movie, peopleURL);
            }
        }

        for(String personURL: peopleURL) {
            JSONObject character = getJSONObject(this.characters, personURL, "url");
            if(character == null) {
                String personObject = rt.getForObject(personURL, String.class);
                people.put(new JSONObject(personObject));
                this.characters.put(new JSONObject(personObject));
            } else {
                people.put(character);
            }
        }

        //TODO remove this line
        System.out.println(people.toString());
        return people;
    }

    private JSONObject getJSONObject(JSONArray array,String compare, String field) {
        for(Object character: array) {
            JSONObject person = (JSONObject) character;
            if(person.get(field).equals(compare)) {
                return person;
            }
        }
        return null;
    }

    private static void getPeople(JSONObject movieObject, ArrayList<String> characters) {
        JSONObject movieJson = movieObject;
        JSONArray movieCharacters = (JSONArray) movieJson.get("characters");
        for(Object movieCharacter: movieCharacters) {
            if(!characters.contains((String) movieCharacter)) {
                characters.add((String) movieCharacter);
            }
        }
    }
}
