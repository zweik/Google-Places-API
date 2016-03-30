package de.karlsruhe.dhbw.googleplacesproject.model;

import android.graphics.Bitmap;

/**
 * This is a POJO representing a place within Google Places API.
 * Only some interesting fields are exemplary used.
 */
public class PlaceModel {
	private final String name;
	private final String url;
	private final String website;
	private final String address;
	private final String phoneNumber;
	private final Bitmap icon;
	private final Double lat;
	private final Double lng;
	
	/**
	 * Creates a new place model. All attributes are only provided if available - otherwise null is passed.
	 * @param name the name of the place
	 * @param url the url of the given place in Google Maps
	 * @param website the url of a place's own website - if available
	 * @param address the address (human readable geo-location) of the place
	 * @param phoneNumber the phone number of the place - if available
	 * @param iconBitmap the icon for the type of place in Google Maps
	 * @param lat the latitude
	 * @param lng the longitude
	 */
	public PlaceModel(String name, String url, String website, String address, String phoneNumber, Bitmap iconBitmap, Double lat, Double lng) {
		this.name = name;
		this.url = url;
		this.website = website;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.icon = iconBitmap;
		this.lat = lat;
		this.lng = lng;
	}
	
	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public String getWebsite() {
		return website;
	}

	public String getAddress() {
		return address;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public Bitmap getIcon() {
		return icon;
	}

	public Double getLat() {
		return lat;
	}

	public Double getLng() {
		return lng;
	}
}
