package de.karlsruhe.dhbw.googleplacesproject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import de.karlsruhe.dhbw.googleplacesproject.api.GooglePredictionTask;

/**
 * This is the application's main activity.
 * It basically contains two views. The searchform which is composed of an EditText,
 * two Spinners and a Button. Secondly a ListView which contains the query's results.
 * 
 * The applications queries the Google Places (REST webservice) API (https://developers.google.com/places/web-service) as follows:
 * <ul>
 * 	<li>An autocomplete query matches the user input with some similar places.</li>
 * 	<li>A second query gets some detailed information about a particular place</li>
 * </ul>
 * The search can be restricted by some specific type of place or some specific country (via the spinners).
 * A working Internet connection is required for the HTTP queries to work.
 * Each exception results in a Toast message displayed at the bottom of the UI.
 */
public class MainActivity extends Activity {
	/**
	 * An exemplary list of supported search types. They will be presented to the
	 * user via a spinner on the main UI window.
	 * more information: https://developers.google.com/places/web-service/autocomplete#place_types
	 */
	public static final String[] SEARCHTYPES = new String[] {
			"(cities)",
			"(regions)",
			"establishment",
			"address",
			"geocode",
			"all"
	};
	
	/**
	 * An exemplary list of search regions. They will be presented to the
	 * user via a spinner on the main UI window.
	 * you can add any ISO 3166-1 Alpha-2 compatible country code
	 */
	public static final String[] SEARCHREGIONS = new String[] {
			"country:de",
			"country:us",
			"country:fr",
			"all"
	};
	
	private EditText searchfield;
	private Button searchbutton;
	private Spinner spinnerSearchType;
	private Spinner spinnerRegion;
	private View rootView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/* setup the content view and save references to main components */
		setContentView(R.layout.activity_main);
		this.rootView = findViewById(android.R.id.content);
		this.searchfield = (EditText) findViewById(R.id.searchfield);
		this.searchbutton = (Button) findViewById(R.id.searchbutton);
		this.spinnerSearchType = (Spinner) findViewById(R.id.searchtype);
		this.spinnerRegion = (Spinner) findViewById(R.id.searchregion);
		
		/* setup search types spinner */
		ArrayAdapter<String> typesadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, SEARCHTYPES);
		typesadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerSearchType.setAdapter(typesadapter);
		spinnerSearchType.setSelection(0);
		
		/* setup search regions spinner */
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, SEARCHREGIONS);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerRegion.setAdapter(adapter);
		spinnerRegion.setSelection(0);
		
		/* setup the searchfield's capability to respond to the enter key */
		searchfield.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				 if (actionId == KeyEvent.KEYCODE_ENDCALL) { 
					 startQuery();
					 return true;
				 }
				 
				return false;
			}
		});
		
		/* setup the searchbutton to start query on click */
		searchbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startQuery();
			}
		});
	}
	
	/**
	 * Sets the searchview's main components visibility to the given value.
	 * @param rootView the root view which holds the searchform
	 * @param visible true for View.VISIBLE, false for View.GONE
	 */
	public static void setSearchControlsVisibility(View rootView, boolean visible) {
		rootView.findViewById(R.id.searchbutton).setVisibility(((visible) ? View.VISIBLE : View.GONE));
		rootView.findViewById(R.id.searchfield).setVisibility(((visible) ? View.VISIBLE : View.GONE));
		rootView.findViewById(R.id.searchtype).setVisibility(((visible) ? View.VISIBLE : View.GONE));
		rootView.findViewById(R.id.searchregion).setVisibility(((visible) ? View.VISIBLE : View.GONE));
	}
	
	/**
	 * Sets the search result view's visibility to the given value.
	 * @param rootView the root view which holds the list view used to display the results
	 * @param visible true for View.VISIBLE, false for View.GONE
	 */
	public static void setListviewVisibility(View rootView, boolean visible) {
		rootView.findViewById(R.id.listview).setVisibility(((visible) ? View.VISIBLE : View.GONE));
	}
	
	/**
	 * Starts the API query in a background thread and handles all possible types of exceptions
	 * as well as providing the results to UI.
	 */
	private void startQuery() {
		/* hide the soft keyboard if visible */
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(searchfield.getWindowToken(), 0);
	    
	    /* remove the focus from searchfield */
	    searchfield.clearFocus();
	    
	    /* get the searchquery and other search options and pass them all to the AsyncTask  */
		String searchquery = searchfield.getText().toString();
		new GooglePredictionTask(MainActivity.this, rootView).execute(searchquery, 
				SEARCHTYPES[spinnerSearchType.getSelectedItemPosition()],
				SEARCHREGIONS[spinnerRegion.getSelectedItemPosition()]);
	}

	/* The following is used to provide some kind of intuitive navigation feeling inside
	 * an activity which is actually only available between activities.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		/* check if user pressed (soft) back button */
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	/* check if main activity is currently displaying result */
	        if(rootView.findViewById(R.id.searchfield).getVisibility() == View.GONE) {
	        	/* reverting title */
	        	setTitle(getResources().getString(R.string.main_activity_name));
	        	
	        	/* revert the UI to searchform view */
	        	setSearchControlsVisibility(rootView, true);
	        	setListviewVisibility(rootView, false);
	        	
	        	/* consume event */
		        return true;
	        }
	    }

	    return super.onKeyDown(keyCode, event);
	}
}
