package de.karlsruhe.dhbw.googleplacesproject.util;

import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.karlsruhe.dhbw.googleplacesproject.R;

/**
 * This is a custom implementation of the ArrayAdapter for the custom ListView.
 * 
 * @param <T> the concrete type of object to display
 */
public class CustomArrayAdapter<T extends Viewable> extends ArrayAdapter<T> {
	  private final Context context;
	  private final List<T> values;

	  /**
	   * Creates an adapter which is capable of populating a ListView with custom cell-layout.
	   * @param context the context of the activity which holds the ListView
	   * @param list a list of elements to display inside the ListView
	   */
	  public CustomArrayAdapter(Context context, List<T> list) {
		  super(context, -1, list);
		  this.context = context;
		  this.values = list;
	  }

	@SuppressLint("ViewHolder")
	@Override
	  public View getView(int position, View convertView, ViewGroup parent) {
		  /* load the layout for each cell inside the ListView */
		  LayoutInflater inflater = (LayoutInflater) context
				  .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		  View rowView = inflater.inflate(R.layout.list_layout, parent, false);
		  
		  /* get references to the elements inside each ListView's cell */
		  TextView textView = (TextView) rowView.findViewById(R.id.firstLine);
		  TextView textView2 = (TextView) rowView.findViewById(R.id.secondLine);
		  ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		  
		  /* set the ListView cell's contents */
		  textView.setText(values.get(position).getCaption());
		  textView2.setText(values.get(position).getSubHeader());
		  imageView.setImageResource(values.get(position).getImageResource());

	      return rowView;
	  }
} 
