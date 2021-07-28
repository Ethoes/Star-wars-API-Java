package com.starwars.rest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static com.starwars.rest.MergeSort.mergeSort;

/**
 * This class is used for contacting the Star wars API and filtering the results.
 */
public class RestCaller {

    private JSONArray characters;
    private JSONArray movies;
    private JSONArray queries;

    /**
     * The constructor is used for reading in the json files.
     */
    public RestCaller() {
        try {
            this.characters = new JSONArray(Files.readString(Path.of("characters.json")));
            this.queries = new JSONArray(Files.readString(Path.of("queries.json")));
            this.movies = new JSONArray(Files.readString(Path.of("movies.json")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method finds all other characters a given character has been in a movie with as a string.
     * @param name, this is query given by the user.
     * @return a string of names of all characters the given character has appeared with.
     */
    protected String getOtherCharacters(String name) {
        //First check if this query has been run before
        JSONObject prevQuery = getJSONObject(this.queries, name, "query");
        //If it has return that found result
        if(prevQuery != null) {
            //Check if it is an actual result or error message.
            if(prevQuery.get("result") instanceof JSONArray) {
                return readableNames(prevQuery.getJSONArray("result"));
            } else {
                return prevQuery.get("result").toString();
            }
        }

        //Call the API using a query given by the user.
        String queryJSON = RequestNames(name);

        //Turn response into JSON object.
        JSONObject jason = new JSONObject(queryJSON);

        //Create a new JSON Object used to save this given query after it is processed.
        JSONObject query = new JSONObject();
        query.put("query", name);

        //Check for invalid results, and if found invalid, save the error string with the query
        if(jason.getInt("count") > 1) {
            query.put("result", "Multiple results found, please specify.");
            queries.put(query);
            return "Multiple results found, please specify.";
        } else if(jason.getInt("count") <= 0) {
            query.put("result", "No matches found, please check if the name was written correctly.");
            queries.put(query);
            return "No matches found, please check if the name was written correctly.";
        }

        //If a query was successful grab the correct JSON array and then find all characters.
        JSONArray person = jason.getJSONArray("results");
        JSONArray people = this.getAllActors((JSONObject) person.get(0));

        //Sort the found array
        people = mergeSort(people);

        //Save the query
        query.put("result", people);
        queries.put(query);

        return readableNames(people);
    }

    /**
     * This method sends a request to the API and returns the result.
     * @param request is the user created query.
     * @return the string form of the JSON returned by the API
     */
    protected static String RequestNames(String request) {
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

    /**
     * This method cycles through all films a character has been in and then saves all other characters in those movies.
     * @param person is the given person asked of by the user.
     * @return a JSON array of all other characters the given character shares a movie with.
     */
    private JSONArray getAllActors(JSONObject person) {
        //Save this URL to be excluded from the list
        String thisURL = person.getString("url");

        //This array is used to save all the URL's given in the movie objects
        ArrayList<String> peopleURL = new ArrayList<>();

        //This array will contain the final result.
        JSONArray people = new JSONArray();

        //This array contains all movies a character is in.
        JSONArray films =  (JSONArray) person.get("films");

        //A RestTemplate that will be used if a call to the API needs to be made.
        RestTemplate rt = new RestTemplate();

        //Loop through each movie to find each character
        for(Object film: films) {
            //Check if the current film has been found.
            JSONObject movie = getJSONObject(this.movies, (String) film, "url");
            if(movie == null) {
                //If the film was not found, send an API call and save it.
                String movieObject = rt.getForObject((String) film, String.class);
                this.movies.put(new JSONObject(movieObject));

                //Add all character's url's to the array.
                getPeople(new JSONObject(movieObject), peopleURL);
            } else {
                //Add all character's url's to the array.
                getPeople(movie, peopleURL);
            }
        }

        //Then loop through all people found
        for(String personURL: peopleURL) {
            //Only check to add character if it is not the requested character itself.
            if (!personURL.equals(thisURL)) {
                //Check if a character is already stored
                JSONObject character = getJSONObject(this.characters, personURL, "url");
                if (character == null) {
                    //If it was not send an API call and save the character data
                    String personObject = rt.getForObject(personURL, String.class);
                    people.put(new JSONObject(personObject));
                    this.characters.put(new JSONObject(personObject));
                } else {
                    //Save the character data
                    people.put(character);
                }
            }
        }
        return people;
    }

    /**
     * This method loops through the given array and finds the given value in the given JSON field.
     * @param array is the array to loop through.
     * @param compare is the value which needs to be compared to.
     * @param field is the JSON field in which values need to be compared.
     * @return a JSON Object if a value is found, if it is not return null.
     */
    private static JSONObject getJSONObject(JSONArray array,String compare, String field) {
        //Loop through a given array.
        for(Object object: array) {
            //If the value matches in the given field, return that JSON object.
            JSONObject JSONobj = (JSONObject) object;
            if(JSONobj.get(field).equals(compare)) {
                return JSONobj;
            }
        }
        //Return null if nothing was found.
        return null;
    }

    /**
     * This method adds all people from a given movie to the given list of characters.
     * @param movieObject is the given JSON object of the movie to check.
     * @param characters is the list to which the found characters need to be added to.
     */
    private static void getPeople(JSONObject movieObject, ArrayList<String> characters) {
        //Retrieve the characters from the movie
        JSONObject movieJson = movieObject;
        JSONArray movieCharacters = (JSONArray) movieJson.get("characters");

        //Loop through all characters in the movie
        for(Object movieCharacter: movieCharacters) {
            //Only add the character if it is not yet contained in the given array.
            if(!characters.contains((String) movieCharacter)) {
                characters.add((String) movieCharacter);
            }
        }
    }

    /**
     * This method changes a given JSON array of people into a readable string of names.
     * @param people is the array of people found using the API.
     * @return a list of people in the form of a string.
     */
    protected static String readableNames(JSONArray people) {
        ArrayList<String> names = new ArrayList<String>();
        for(Object person: people) {
            JSONObject character = (JSONObject) person;
            names.add(character.getString("name"));
        }
        return names.toString();
    }

    /**
     * This method saves all data to the included files for use on next startup.
     */
    protected void saveJSONFiles() {
        try {
            //Finds the file and writes to it.
            FileWriter file = new FileWriter("queries.json");
            file.write(this.queries.toString());
            //Cleares out the file writer.
            file.flush();

            file = new FileWriter("movies.json");
            file.write(this.movies.toString());
            file.flush();

            file = new FileWriter("characters.json");
            file.write(this.characters.toString());
            file.flush();
        } catch (IOException e) {
            //If the file is not present it throws a legible error.
            e.printStackTrace();
        }
    }
}
