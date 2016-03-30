package de.karlsruhe.dhbw.googleplacesproject.api;

/**
 * This class contains some configuration data for Google Place API to work.
 */
public class GoogleConstants {
	/**
	 * The Google Places API Key.
	 * Information on how to obtain a key: https://developers.google.com/places/web-service/get-api-key?hl=de
	 * The following is a development key with a daily quota of 1000 requests.
	 */
	public static final String API_KEY = "AIzaSyAU5BR9W-_pdRmYN-weHoAQ7tK2vSuIZ_c";
	
	/**
	 * The language of the queries results.
	 * See "supported languages": https://developers.google.com/maps/faq?hl=de#using-google-maps-apis
	 */
	public static final String COUNTRY_DE = "de";
}
