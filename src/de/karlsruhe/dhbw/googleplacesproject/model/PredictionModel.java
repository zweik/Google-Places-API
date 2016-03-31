package de.karlsruhe.dhbw.googleplacesproject.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

import de.karlsruhe.dhbw.googleplacesproject.R;
import de.karlsruhe.dhbw.googleplacesproject.util.Viewable;

/**
 * This is a POJO representing a suggestion from the Google Places Autocomplete API.
 * It uses the Serializable interface so it can be passed between activities and the Viewable interface
 * to be displayed in the CustomArrayAdapter implementation.
 */
public class PredictionModel implements Viewable, Serializable {
	private static final long serialVersionUID = 8628692845428481227L;
	private static final HashMap<String, String> TYPE_MAPPING = new HashMap<>();
	
	/* static initializer used to add some exemplary translation mappings for types */
	{
		TYPE_MAPPING.put("locality", "Gegend");
		TYPE_MAPPING.put("sublocality", "Raum");
		TYPE_MAPPING.put("postal_code", "Postleitzahl");
		TYPE_MAPPING.put("country", "Land");
		TYPE_MAPPING.put("establishment", "Unternehmen");
		TYPE_MAPPING.put("address", "Adresse");
		TYPE_MAPPING.put("geocode", "Geografischer Punkt");
	}
	
	private final String description;
	private final String id;
	private final String placeId;
	private final String reference;
	private final String[] types;
	
	/**
	 * Creates a new PredictionModel.
	 * For more information about result types: https://developers.google.com/places/web-service/autocomplete?hl=de#place_autocomplete_results
	 * @param description the name of the place
	 * @param id the place id (deprecated)
	 * @param place_id the place id which uniquely identifies this place
	 * @param reference contains a unique token that you can use to retrieve additional information about this place
	 * @param types contains an array of types that apply to this place
	 */
	public PredictionModel(String description, String id, String place_id, String reference, String[] types) {
		this.description = description;
		this.id = id;
		this.placeId = place_id;
		this.reference = reference;
		this.types = types;
		
		/* exemplary translating some types */
		for(int i=0; i < this.types.length; i++) {
			String translation = TYPE_MAPPING.get(this.types[i]);
			if(translation != null) {
				this.types[i] = translation;
			}
		}
	}

	public String getDescription() {
		return description;
	}

	public String getId() {
		return id;
	}

	public String getPlaceId() {
		return placeId;
	}

	public String getReference() {
		return reference;
	}

	public String[] getTypes() {
		return types;
	}

	@Override
	public int getImageResource() {
		return R.drawable.ic_pin_drop_black_36dp;
	}

	@Override
	public String getCaption() {
		return getDescription();
	}

	@Override
	public String getSubHeader() {
		return Arrays.toString(getTypes());
	}
}
