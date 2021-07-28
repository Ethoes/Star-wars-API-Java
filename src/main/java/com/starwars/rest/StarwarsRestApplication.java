package com.starwars.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;

@SpringBootApplication
public class StarwarsRestApplication {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		while(true) {
			System.out.println("Enter name of a star wars character \n (type quit to quit)");
			String str = sc.nextLine();
			if(str.equals("quit")) {
				break;
			}
			System.out.println(getNames(str));
		}
		sc.close();
	}

	private static String getNames(String name) {
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
		getAllActors((JSONObject) person.get(0));

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

	private static String getAllActors(JSONObject person) {
		ArrayList<String> peopleURL = new ArrayList<>();
		JSONArray people = new JSONArray();
		JSONArray films =  (JSONArray) person.get("films");
		RestTemplate rt = new RestTemplate();
		for(Object film: films) {
			String movieObject = rt.getForObject((String) film, String.class);
			getPeople(movieObject, peopleURL);
		}

		for(String personURL: peopleURL) {
			String personObject = rt.getForObject(personURL, String.class);
			people.put(new JSONObject(personObject));
		}

		System.out.println(people.toString());
		return "pp";
	}

	private static void getPeople(String movieObject, ArrayList<String> characters) {
		JSONObject movieJson = new JSONObject(movieObject);
		JSONArray movieCharacters = (JSONArray) movieJson.get("characters");
		for(Object movieCharacter: movieCharacters) {
			if(!characters.contains((String) movieCharacter)) {
				characters.add((String) movieCharacter);
			}
		}
	}
}
