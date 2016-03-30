package de.karlsruhe.dhbw.googleplacesproject.util;

/**
 * This interface is intended to be used in conjunction with {@link CustomArrayAdapter}.
 * Each type of object which should be displayed inside the custom ListView has to implement the given methods.
 */
public interface Viewable {
	/**
	 * Returns a reference to an image resource which is used as icon inside the ListView.
	 * @return the resource identifier of the image
	 */
	public int getImageResource();
	
	/**
	 * The caption inside the ListView's element (i.e. first line).
	 * @return the first line
	 */
	public String getCaption();
	
	/**
	 * The second line inside the ListView's element.
	 * @return the second line
	 */
	public String getSubHeader();
}
