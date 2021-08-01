package com.starwars.rest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RestCallerTest {

    /**
     * This method tests this method's constructor.
     */
    @Test
    public void testConstructor() {
        RestTemplate rt = new RestTemplate();
        RestCaller rc = new RestCaller(rt);

        assertNotNull(rc.getCharacters());
        assertNotNull(rc.getMovies());
        assertNotNull(rc.getQueries());
        assertNotNull(rc.getRt());
        assertTrue(rc.getRt().equals(rt));
    }

    /**
     * Test Assures that when person doesnt exist nothing is returned
     */
    @Test
    public void testGetOtherActorsNoMatch() {
        RestCaller rc = new RestCaller(new RestTemplate());
        assertTrue(rc.getOtherCharacters("Steve from accounting")
                .equals("No matches found, please check if the name was written correctly."));
    }

    /**
     * Test Assures that when multiple matches exist
     */
    @Test
    public void testGetOtherActorsMultipleMatches() {
        RestCaller rc = new RestCaller(new RestTemplate());
        assertTrue(rc.getOtherCharacters("skywalker")
                .equals("Multiple results found, please specify."));
    }

    /**
     * Test that checks if the right string is returned when asked for. (sorry for long string :/)
     */
    @Test
    public void testGetOtherActorsMatch() {
        RestCaller rc = new RestCaller(new RestTemplate());
        assertTrue(rc.getOtherCharacters("Cliegg Lars")
                .equals("[Yoda, C-3PO, Dooku, Ki-Adi-Mundi, Palpatine, Shmi Skywalker, Mace Windu, Bail Prestor Organa, Jango Fett, Luminara Unduli, " +
                        "Obi-Wan Kenobi, Owen Lars, Jar Jar Binks, Ayla Secura, Beru Whitesun lars, Padmé Amidala, Anakin Skywalker, Barriss Offee," +
                        " R2-D2, Boba Fett, Plo Koon, Nute Gunray, Watto, Kit Fisto, Mas Amedda, Gregar Typho, Cordé, Poggle the Lesser, " +
                        "Dormé, Zam Wesell, Dexter Jettster, Lama Su, Taun We, Jocasta Nu, R4-P17, Wat Tambor, San Hill, Shaak Ti, Sly Moore]"));
    }



    /**
     * Test mocks usage of requestNames to see result is passed on properly
     */
    @Test
    public void testRequestNames() {
    RestTemplate mockTemplate = Mockito.mock(RestTemplate.class);
    RestCaller rc = new RestCaller(mockTemplate);
    Mockito.when(mockTemplate.getForObject("https://swapi.dev/api/people/?search=", String.class))
            .thenReturn("Result");

    assertTrue(rc.RequestNames("").equals("Result"));
    }


    /**
     * Test mocks usage of requestNames to if an error is handled properly
     */
    @Test
    public void testRequestNamesError() {
        RestTemplate mockTemplate = Mockito.mock(RestTemplate.class);
        RestCaller rc = new RestCaller(mockTemplate);
        Mockito.when(mockTemplate.getForObject("https://swapi.dev/api/people/?search=", String.class))
                .thenThrow(new RuntimeException());

        assertTrue(rc.RequestNames("").equals("Invalid request, server might be down"));
    }

    /**
     * Make sure that when all other actors are asked for that set of people is returned.
     * (Note that no specific check is done on all found characters as that would make this test miles long)
     */
    @Test
    public void testGetAllActors() {
        RestCaller rc = new RestCaller(new RestTemplate());
        JSONObject mock = Mockito.mock(JSONObject.class);
        Mockito.when(mock.getString("url")).thenReturn("https://swapi.dev/api/people/2/");

        JSONArray testArray = new JSONArray();
        testArray.put("https://swapi.dev/api/films/1/");

        Mockito.when(mock.get("films")).thenReturn(testArray);

        System.out.println(rc.getAllActors(mock).get(0).toString());

        assertTrue(rc.getAllActors(mock).get(0).toString().equals("{\"films\":[\"https://swapi.dev/api/films/1/\"," +
                "\"https://swapi.dev/api/films/2/\",\"https://swapi.dev/api/films/3/\",\"https://swapi.dev/api/films/6/\"]," +
                "\"homeworld\":\"https://swapi.dev/api/planets/1/\",\"gender\":\"male\",\"skin_color\":\"fair\",\"edited\"" +
                ":\"2014-12-20T21:17:56.891000Z\",\"created\":\"2014-12-09T13:50:51.644000Z\",\"mass\":\"77\",\"vehicles\":" +
                "[\"https://swapi.dev/api/vehicles/14/\",\"https://swapi.dev/api/vehicles/30/\"],\"url\":\"https://swapi.dev/api/people/1/\"," +
                "\"hair_color\":\"blond\",\"birth_year\":\"19BBY\",\"eye_color\":\"blue\",\"species\":[],\"starships\":[\"https://swapi.dev/api/starships/12/\"," +
                "\"https://swapi.dev/api/starships/22/\"],\"name\":\"Luke Skywalker\",\"height\":\"172\"}"));
    }

    /**
     * This test checks if the given value can be found in a field
     */
    @Test
    public void testGetJsonObjectFound() {
        RestCaller rc = new RestCaller(new RestTemplate());
        JSONArray testArray = new JSONArray();
        JSONObject testObject = new JSONObject();
        testObject.put("birthday", "yes");
        testArray.put(testObject);
        assertTrue(rc.getJSONObject(testArray, "yes", "birthday").equals(testObject));
    }

    /**
     * This test checks if value does not exist if it returns null.
     */
    @Test
    public void testGetJsonObjectNotFound() {
        RestCaller rc = new RestCaller(new RestTemplate());
        JSONArray testArray = new JSONArray();
        JSONObject testObject = new JSONObject();
        testObject.put("birthday", "no");
        testArray.put(testObject);
        assertTrue(rc.getJSONObject(testArray, "yes", "birthday") == null);
    }

    /**
     * This test checks if the json files that are supposed to be saved were modified in
     * the last 10 seconds since the call.
     */
    @Test
    public void testSavedJSONFiles() {
        RestCaller rc = new RestCaller(new RestTemplate());
        Date date = new Date();
        rc.saveJSONFiles();
        long finishTime = date.getTime();

        try {
            Path file;
            BasicFileAttributes attr;

            file = Paths.get("movies.json");
            attr = Files.readAttributes(file, BasicFileAttributes.class);
            assertTrue(Math.abs(finishTime - attr.lastModifiedTime().toMillis()) <= 10000);

            file = Paths.get("queries.json");
            attr = Files.readAttributes(file, BasicFileAttributes.class);
            assertTrue(Math.abs(finishTime - attr.lastModifiedTime().toMillis()) <= 10000);

            file = Paths.get("characters.json");
            attr = Files.readAttributes(file, BasicFileAttributes.class);
            assertTrue(Math.abs(finishTime - attr.lastModifiedTime().toMillis()) <= 10000);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
