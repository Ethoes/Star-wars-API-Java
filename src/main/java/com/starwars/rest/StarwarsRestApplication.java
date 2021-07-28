package com.starwars.rest;

import java.util.Scanner;

/**
 * This class contains the main function for the application and is used to start it.
 */
public class StarwarsRestApplication {

	public static void main(String[] args) {
		//Create a scanner to read the command line
		Scanner sc = new Scanner(System.in);
		//Create a new RestCaller object so it reads in the found files
		RestCaller rc = new RestCaller();

		//Continuously ask for input until user wants to quit.
		while(true) {
			System.out.println("Enter name of a star wars character \n (type quit to quit)");

			//Turn written line into string after entered.
			String str = sc.nextLine();
			if(str.equals("quit")) {
				break;
			}

			//Print the result found running the getOtherCharacters method.
			System.out.println(rc.getOtherCharacters(str));
		}

		//Save the Json files when the application is quit.
		rc.saveJSONFiles();

		//Close the scanner
		sc.close();
	}
}
