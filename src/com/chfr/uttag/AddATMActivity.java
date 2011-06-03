package com.chfr.uttag;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.maps.GeoPoint;


public class AddAtmActivity extends Activity {
	
	public final static int RESULT_OK = 1;
	public final static int RESULT_CANCEL = 2;
	public final static int LOCATION_REQUEST_CODE = 2;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_atm_activity);
        
        
        
        Button mapButton = (Button)findViewById(R.id.choose_map_button);
        mapButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO if a location has already been chosen, send it back to
				// LocationChooserActivity to display the overlay from the start
				Intent i = new Intent(AddAtmActivity.this, LocationChooserActivity.class);
				AddAtmActivity.this.startActivityForResult(i, LOCATION_REQUEST_CODE);				
			}
		});
        
        Button saveButton = (Button)findViewById(R.id.save_atm_button);
        saveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO add to database or something
				Toast t = Toast.makeText(getApplicationContext(), "Saved! (not really)", Toast.LENGTH_SHORT);
				t.show();
				setResult(RESULT_OK);
				AddAtmActivity.this.finish();
			}
		});
        
        Button cancelButton = (Button)findViewById(R.id.cancel_atm_button);
        cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO nothing?
				setResult(RESULT_CANCEL);
				AddAtmActivity.this.finish();
			}
		});

        ImageButton locationButton = (ImageButton)findViewById(R.id.my_location_button);
        locationButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO acquire location, update text field
				Toast t = Toast.makeText(getApplicationContext(), "Acquiring location (not really)", Toast.LENGTH_SHORT);
				t.show();
			}
		});
        
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == LOCATION_REQUEST_CODE) {
			if (resultCode == LocationChooserActivity.RESULT_OK) {
				int lat = data.getIntExtra("com.chfr.Uttag.tapLat", 0);
				int lon = data.getIntExtra("com.chfr.Uttag.tapLon", 0);
				
				if (lat != 0 && lon != 0) {
					GeoPoint gp = new GeoPoint(lat, lon);
					TextView tv = (TextView)findViewById(R.id.textview_location_result);
					tv.setText(gp.toString());
				}

			}
		}
		
		
		super.onActivityResult(requestCode, resultCode, data);
	}
}
