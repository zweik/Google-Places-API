package de.karlsruhe.dhbw.googleplacesproject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import de.karlsruhe.dhbw.googleplacesproject.api.GooglePlaceDetailTask;
import de.karlsruhe.dhbw.googleplacesproject.model.PredictionModel;

/**
 * An activity with style "Theme.Holo.Dialog".
 * It is used to display the details for a given place on top of the list with autocomplete predictions.
 * The place to examine is passed by an intent with serialized data.
 */
public class DetailsActivity extends Activity {
	private View rootView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/* setup the content view */
		setContentView(R.layout.detail_layout);
		this.rootView = findViewById(android.R.id.content);
		
		/* retrieve the PredictionModel from autocomplete selection by deserializing the activities's start-intent */
		PredictionModel predictionModel = (PredictionModel) getIntent().getSerializableExtra("EXTRA_PREDICTION_MODEL");
		
		/* start the api query and display details when completed */
		new GooglePlaceDetailTask(this, rootView).execute(predictionModel.getPlaceId());
	}
}
