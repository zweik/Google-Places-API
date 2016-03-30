package de.karlsruhe.dhbw.googleplacesproject.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
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
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import de.karlsruhe.dhbw.googleplacesproject.DetailsActivity;
import de.karlsruhe.dhbw.googleplacesproject.MainActivity;
import de.karlsruhe.dhbw.googleplacesproject.R;
import de.karlsruhe.dhbw.googleplacesproject.model.PredictionModel;
import de.karlsruhe.dhbw.googleplacesproject.util.CustomArrayAdapter;

/**
 * This is an AsyncTask implementation which is used to issue a (non-blocking) query towards the Google Places API which
 * retrieves information about places with a similar name compared to the given input.
 */
public class GooglePredictionTask extends AsyncTask<String, Void, BackgroundTaskResult<List<PredictionModel>>> {	
	private final View rootView;
	private final Activity activity;
	private ProgressDialog progressDialog;
	private String input;
	
	/**
	 * Creates a new background task for given {@code activity}.
	 * @param activity the activity which created and executes this task
	 * @param rootView the view which holds the searchform and ListView for results
	 */
    public GooglePredictionTask(Activity activity, View rootView) {
    	this.rootView = rootView;
    	this.activity = activity;
	}

    @Override
	protected BackgroundTaskResult<List<PredictionModel>> doInBackground(String... params) {
		if(params.length < 1) {
			throw new IllegalArgumentException("At least input required to send an API call.");
		}
		
		this.input = params[0];
		
		/* show the progress dialog */
		publishProgress();
		
		/* create the API HTTP query and parse the response */
    	try {
    		/* create the request */
	    	HttpTransport httpTransport = new NetHttpTransport();
			HttpRequestFactory httpRequestFactory = httpTransport.createRequestFactory();
			
			GenericUrl url = new GenericUrl("https://maps.googleapis.com/maps/api/place/autocomplete/json");
			/* optional parameters */
			url.put("language", GoogleConstants.COUNTRY_DE);

			if(params.length > 1 && !params[1].equals("all")) {
				url.put("types", params[1]);
			}
			
			if(params.length > 2 && !params[2].equals("all")) {
				url.put("components", params[2]);
			}
			
			/* required parameters */
			url.put("input", params[0]);
			url.put("key", GoogleConstants.API_KEY);
			
			/* send the request as GET */
			HttpRequest httpRequest = httpRequestFactory.buildGetRequest(url);
			HttpResponse httpResponse = httpRequest.execute();
			String jsonResponseString = httpResponse.parseAsString();

			/* parse the attributes if available and check the status */
			JSONObject root = new JSONObject(jsonResponseString);
			String status = root.getString("status");
			BackgroundTaskResult.validateStatus(status);
			
			JSONArray predictions = root.getJSONArray("predictions");
			List<PredictionModel> predictionList = new ArrayList<>(predictions.length());
			
			/* parse every single prediction json object to a PredictionModel and save them to a list */
			for(int i = 0; i < predictions.length(); i++) {
				JSONObject cPrediction = predictions.getJSONObject(i);
				JSONArray typesArray = cPrediction.getJSONArray("types");
				String[] types = new String[typesArray.length()];
				for(int j = 0; j < types.length; j++) {
					types[j] = typesArray.getString(j);
				}
				
				predictionList.add(new PredictionModel(cPrediction.getString("description"),
						cPrediction.getString("id"),
						cPrediction.getString("place_id"),
						cPrediction.getString("reference"),
						types));
			}
			
			return new BackgroundTaskResult<List<PredictionModel>>(predictionList);
    	} catch(IOException | JSONException e) {
    		return new BackgroundTaskResult<List<PredictionModel>>(e);
    	}
    }
	
    /**
     * Show the given {@code predictions} in the ListView.
     * @param predictions a list containing PredictionModels to display
     */
	private void showPredictions(final List<PredictionModel> predictions) {
		final ListView listview = (ListView) this.rootView.findViewById(R.id.listview);
		
	    final CustomArrayAdapter<PredictionModel> adapter = new CustomArrayAdapter<>(this.activity, predictions);
	    listview.setAdapter(adapter);

	    /* create the on click handler for the user to request details for a particular place */
	    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	      @Override
	      public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
	    	  final PredictionModel data = (PredictionModel) parent.getItemAtPosition(position);
	    	  
	    	  /* create intent and use it to start the DetailsActivity */
	    	  Intent intent = new Intent(activity, DetailsActivity.class);
	    	  intent.putExtra("EXTRA_PREDICTION_MODEL", data);
	    	  activity.startActivity(intent);
	      }
	    });
	}
		
	@Override
    protected void onProgressUpdate(Void... args) {
		/* show a progress dialog */
    	progressDialog = ProgressDialog.show(activity, "API-Request",
    		    "Fetching data from Google Servers in background...", true);
    }

	@Override
    protected void onPostExecute(BackgroundTaskResult<List<PredictionModel>> result) {
		/* hide and destroy the progress dialog */
    	progressDialog.dismiss();
    	
    	/* check if the query was successful and populate the result view if it was so */
    	if(result.isSuccessful()) {
    		MainActivity.setSearchControlsVisibility(rootView, false);
    		showPredictions(result.getData());
    		activity.setTitle(activity.getTitle() + " (" + this.input + ")");
    		MainActivity.setListviewVisibility(rootView, true);
    	} else {
    		/* get the causing exception */
    		Exception exception = result.getException();
    		exception.printStackTrace();
    		
    		/* revert to searchform */
    		MainActivity.setListviewVisibility(rootView, false);
    		MainActivity.setSearchControlsVisibility(rootView, true);

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
}