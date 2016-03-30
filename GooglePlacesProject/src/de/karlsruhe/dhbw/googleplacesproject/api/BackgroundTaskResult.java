package de.karlsruhe.dhbw.googleplacesproject.api;

import java.io.IOException;

/**
 * This class is used to pass results between an AsyncTask's doInBackground method and the onPostExecute method.
 * It provides the ability to save every type of exception which happens on the background task in order to
 * display in on UI thread.
 * 
 * @param <T> the type of result object for an ordinary request without exception
 */
public class BackgroundTaskResult<T> {
	/* Google Status Codes (https://developers.google.com/places/web-service/autocomplete?hl=de#place_autocomplete_status_codes) */
	public static final String STATUS_OK = "OK";
	public static final String STATUS_ZERO_RESULTS = "ZERO_RESULTS";
	public static final String STATUS_OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";
	public static final String STATUS_REQUEST_DENIED = "REQUEST_DENIED";
	public static final String STATUS_INVALID_REQUEST = "INVALID_REQUEST";
	
	private final Exception exception;
	private final T data;
	
	/**
	 * Creates a new result object for a successful background task with given result object.
	 * @param data the resulting data which was attained by the task
	 */
	public BackgroundTaskResult(T data) {
		this.data = data;
		this.exception = null;
	}
	
	/**
	 * Creates a new result object for a failed background task with given exception as cause.
	 * @param exception the exception which happend during background processing
	 */
	public BackgroundTaskResult(Exception exception) {
		this.exception = exception;
		this.data = null;
	}
	
	/**
	 * Returns whether the task was completed without exception.
	 * @return true if no exception happened
	 */
	public boolean isSuccessful() {
		return exception == null;
	}
	
	/**
	 * Returns the exception which happend during task processing.
	 * @return the exception if available, null otherwise
	 */
	public Exception getException() {
		return this.exception;
	}
	
	/**
	 * Returns the data obtained by the background task.
	 * @return the data or null if exception happend during processing
	 */
	public T getData() {
		return this.data;
	}

	/**
	 * Throws an exception if the given Google Status is not OK.
	 * @param status the status string obtained by Google Places API
	 * @throws IOException thrown if a non STATUS_OK status is passed
	 */
	public static void validateStatus(String status) throws IOException {
		if(!status.equals(STATUS_OK)) {
			String message = "";
			switch(status) {
				case BackgroundTaskResult.STATUS_INVALID_REQUEST:
					message = "The input parameter is missing.";
					break;
				case STATUS_OVER_QUERY_LIMIT:
					message = "You have exceeded your daily quota for the given API key.";
					break;
				case STATUS_ZERO_RESULTS:
					message = "No results, try again.";
					break;
				case STATUS_REQUEST_DENIED:
					message = "Your API key is invalid.";
					break;
			}
			
			throw new IOException(message);
		}
	}
}
