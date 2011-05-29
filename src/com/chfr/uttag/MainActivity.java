package com.chfr.uttag;

import java.io.IOException;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.view.KeyEvent;
import android.content.Context;
import android.location.Geocoder;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;

public class MainActivity extends MapActivity {
	public Location lastLoc;
	public LocationManager lm = null;
	LocationListener ll = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        ll = new MyLocationListener();
        //TODO more sensible values for time/distance
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
        
        MapView mv = (MapView)findViewById(R.id.mapview);
		
		final MapController mc = mv.getController();
        
        final EditText edittext = (EditText) findViewById(R.id.editText1);
        edittext.setOnKeyListener(new OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                	String s = edittext.getText().toString();
                	
                	Geocoder gc = new Geocoder(MainActivity.this);
                	
                	List<Address> matches = null;
                	
                	try {
						matches = gc.getFromLocationName(s, 5);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (matches != null && matches.size() > 0) {
						Toast toast = Toast.makeText(MainActivity.this, 
								"got " + String.valueOf(matches.size()) + " matches", Toast.LENGTH_SHORT);
						toast.show();
						double lat, lon;
						lat = matches.get(0).getLatitude();
						lon = matches.get(0).getLongitude();
						
						GeoPoint gp = new GeoPoint((int)(lat*1000000), (int)(lon*1000000));
						
						mc.animateTo(gp);
					}
					else if (matches != null && matches.size() == 0) {
						Toast toast = Toast.makeText(MainActivity.this, "no matches!", Toast.LENGTH_SHORT);
						toast.show();
					}
                  
                  	return true;
                }
                return false;
            }
        });
        
        final ImageButton mylocation = (ImageButton) findViewById(R.id.imageButton2);
        mylocation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (lastLoc != null) {
					double lat,lon;
					lat = lastLoc.getLatitude();
					lon = lastLoc.getLongitude();
					GeoPoint gp = new GeoPoint((int)(lat*1E6),(int)(lon*1E6));
					
					mc.animateTo(gp);
					mc.setZoom(17);
				}
				else {
					Toast toast = Toast.makeText(getApplicationContext(), "Waiting for location...", Toast.LENGTH_SHORT);
					toast.show();
					// TODO auto-update when location is aquired
				}
			}
		});
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onPause()
	{
		super.onPause();
		if (lm != null && ll != null) {
			lm.removeUpdates(ll);
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (lm != null && ll != null) {
			//TODO more sensible values for time/distance
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
		}
		
	}
	
	public class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			lastLoc = location;
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
	}
}