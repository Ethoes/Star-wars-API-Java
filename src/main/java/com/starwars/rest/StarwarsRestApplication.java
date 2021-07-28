package com.starwars.rest;

import java.util.Scanner;


public class StarwarsRestApplication {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		RestCaller rc = new RestCaller();
		while(true) {
			System.out.println("Enter name of a star wars character \n (type quit to quit)");
			String str = sc.nextLine();
			if(str.equals("quit")) {
				break;
			}
			System.out.println(rc.getNames(str));
		}

		rc.saveJSONFiles();
		sc.close();
	}
}
