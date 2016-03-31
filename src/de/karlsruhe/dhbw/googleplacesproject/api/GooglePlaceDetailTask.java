package de.karlsruhe.dhbw.googleplacesproject.api;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import de.karlsruhe.dhbw.googleplacesproject.R;
import de.karlsruhe.dhbw.googleplacesproject.model.PlaceModel;

/**
 * This is an AsyncTask implementation which is used to issue a (non-blocking) query towards the Google Places API which
 * retrieves information about a particular place.
 */
public class GooglePlaceDetailTask extends AsyncTask<String, Void, BackgroundTaskResult<PlaceModel>> {	
	private final View rootView;
	private final Activity activity;
	private ProgressDialog progressDialog;
	
	/**
	 * Creates a new background task for given {@code activity}.
	 * @param activity the activity which created and executes this task
	 * @param rootView the view which holds the response fields
	 */
    public GooglePlaceDetailTask(Activity activity, View rootView) {
    	this.rootView = rootView;
    	this.activity = activity;
	}

    @Override
	protected BackgroundTaskResult<PlaceModel> doInBackground(String... params) {
    	if(params.length < 1) {
			throw new IllegalArgumentException("At least the place's id is required to send an API call.");
		}
    	
    	/* show a loader */
		publishProgress();
		
		/* create the API HTTP query and parse the response */
    	try {
    		/* create the request */
	    	HttpTransport httpTransport = new NetHttpTransport();
			HttpRequestFactory httpRequestFactory = httpTransport.createRequestFactory();
			
			GenericUrl url = new GenericUrl("https://maps.googleapis.com/maps/api/place/details/json");
			/* optional parameters */
			url.put("language", GoogleConstants.COUNTRY_DE);
			
			/* required parameters */
			url.put("placeid", params[0]);
			url.put("key", GoogleConstants.API_KEY);
			
			/* send the request as GET */
			HttpRequest httpRequest = httpRequestFactory.buildGetRequest(url);
			HttpResponse httpResponse = httpRequest.execute();
			String jsonResponseString = httpResponse.parseAsString();

			/* parse the attributes if available and check the status */
			JSONObject root = new JSONObject(jsonResponseString);
			String status = root.getString("status");
			BackgroundTaskResult.validateStatus(status);
			
			JSONObject data = root.getJSONObject("result");
			String name = (data.has("name")) ? data.getString("name") : null;
			String gmapsurl = (data.has("url")) ? data.getString("url") : null;
			String website = (data.has("website")) ? data.getString("website") : null;
			String address = (data.has("formatted_address")) ? data.getString("formatted_address") : null;
			String phoneNumber = (data.has("formatted_phone_number")) ? data.getString("formatted_phone_number") : null;
			String icon = (data.has("icon")) ? data.getString("icon") : null;
			Double lat = null;
			Double lng = null;
			if(data.has("geometry")) {
				JSONObject geometry = data.getJSONObject("geometry");
				if(geometry.has("location")) {
					JSONObject location = geometry.getJSONObject("location");
					if(location.has("lat")) {
						lat = location.getDouble("lat");
					}
					
					if(location.has("lng")) {
						lng = location.getDouble("lng");
					}
				}
			}
			
			/* if an icon is provided, download it */
			Bitmap iconBitmap = null;
			if(icon != null) {
				iconBitmap = getImageBitmap(icon);
			}
			
			/* populate the model */
			PlaceModel placeModel = new PlaceModel(name, gmapsurl, website, address, phoneNumber, iconBitmap, lat, lng);
			
			return new BackgroundTaskResult<PlaceModel>(placeModel);
    	} catch(IOException | JSONException e) {
    		return new BackgroundTaskResult<PlaceModel>(e);
    	}
    }
		
    @Override
    protected void onProgressUpdate(Void... args) {
    	/* show a progress dialog */
    	progressDialog = ProgressDialog.show(activity, "API-Request",
    		    "Fetching data from Google Servers in background...", true);
    }

    @Override
    protected void onPostExecute(BackgroundTaskResult<PlaceModel> result) {
    	/* hide and destroy the progress dialog */
    	progressDialog.dismiss();
    	
    	/* check if the query was successful and populate the result view if it was so */
    	if(result.isSuccessful()) {
    		final PlaceModel placeModel = result.getData();
    		if(placeModel.getName() != null) {
    			TextView tv = ((TextView) rootView.findViewById(R.id.place_name));
    			tv.setText(placeModel.getName());
    			tv.setVisibility(View.VISIBLE);
    		}
    		
    		if(placeModel.getAddress() != null) {
    			TextView tv = ((TextView) rootView.findViewById(R.id.place_address));
    			tv.setText(placeModel.getAddress());
    			tv.setVisibility(View.VISIBLE);
    		}
    		
    		if(placeModel.getLat() != null && placeModel.getLng() != null) {
    			TextView tv = ((TextView) rootView.findViewById(R.id.place_location));
    			tv.setText("Lat/Lng: " + placeModel.getLat() + ", " + placeModel.getLng());
    			tv.setVisibility(View.VISIBLE);
    		}
    		
    		if(placeModel.getWebsite() != null) {
    			Button btn = ((Button) rootView.findViewById(R.id.place_website));
    			btn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						openInBrowser(placeModel.getWebsite());
					}
				});
    			btn.setVisibility(View.VISIBLE);
    		}
    		
    		if(placeModel.getUrl() != null) {
    			Button btn = ((Button) rootView.findViewById(R.id.place_gmapsurl));
    			btn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						openInBrowser(placeModel.getUrl());
					}
				});
    			btn.setVisibility(View.VISIBLE);
    		}
    		
    		if(placeModel.getIcon() != null) {
    			ImageView iv = ((ImageView) rootView.findViewById(R.id.place_icon));
    			iv.setImageBitmap(placeModel.getIcon());
    			iv.setVisibility(View.VISIBLE);
    		}
    	} else {
    		/* close the activity and thus effectively return to result selection */
    		activity.finish();
    		
    		/* get the causing exception */
    		Exception exception = result.getException();
    		exception.printStackTrace();
    		
    		/* display the exception's message as Toast */
    		if(exception.getClass().equals(JSONException.class)) {
    			Toast.makeText(activity, "Error parsing the json response.", Toast.LENGTH_LONG).show();
    		} else {
    			/* catch all other exceptions such as IOException (which happens when network unreachable) */
    			Toast.makeText(activity, exception.getClass().getSimpleName() + ": " + exception.getMessage(),
    					Toast.LENGTH_LONG).show();
    		}
    	}
    }
    
	private void openInBrowser(String url) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		activity.startActivity(i);
	}
    
    private Bitmap getImageBitmap(String url) throws IOException {
        URL aURL = new URL(url);
        URLConnection conn = aURL.openConnection();
        conn.connect();
        InputStream is = conn.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(is);
        Bitmap bm = BitmapFactory.decodeStream(bis);
        bis.close();
        is.close();
        
        return bm;
    } 
}
