package com.chfr.uttag;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class LocationChooserActivity extends MapActivity {
	
	public final static int RESULT_OK = 1; 
	public static GeoPoint mPoint;
	
	private List<Address> addresses;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_chooser_activity);
        
        final MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        registerForContextMenu(mapView);
        final MapController mc = mapView.getController();
               
        List<Overlay> overlays = mapView.getOverlays();
        overlays.add(new LocationOverlay(getApplicationContext(), this));
        
        final EditText edittext = (EditText) findViewById(R.id.location_search_field);
        registerForContextMenu(mapView);
        edittext.setOnKeyListener(new OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                	doSearch(mapView, mc, edittext);
                  
                  	return true;
                }
                return false;
            }
        });
        
        final ImageButton searchButton = (ImageButton)findViewById(R.id.add_atm_button);
        searchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doSearch(mapView, mc, edittext);				
			}
		});
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (addresses == null)
			return;
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.search_menu, menu);
		
		
		for (int i=0;i<addresses.size();i++) {
			Address a = addresses.get(i);
			
			String s = a.getAddressLine(0) + ", " + a.getLocality() + ", " + " (" + a.getCountryName() + ")";
			menu.add(Menu.NONE, i, i, s);			
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		MapView mv = (MapView)findViewById(R.id.mapview);
		final MapController mc = mv.getController();
		
		double lat, lon;
		Address a = addresses.get(id);
		lat = a.getLatitude();
		lon = a.getLongitude();
		
		GeoPoint gp = new GeoPoint((int)(lat*1000000), (int)(lon*1000000));
		
		mc.animateTo(gp);
		mc.setZoom(15);
		
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(((EditText)findViewById(R.id.location_search_field)).getWindowToken(), 0);
		
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private void doSearch(final MapView mapView, final MapController mc,
			final EditText edittext) {
		String s = edittext.getText().toString();
		
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		Geocoder gc = new Geocoder(LocationChooserActivity.this);
		
		List<Address> matches = null;
		
		try {
			matches = gc.getFromLocationName(s, 10);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (matches != null && matches.size() > 0) {
			
			if (matches.size() == 1) {
				double lat, lon;
				lat = matches.get(0).getLatitude();
				lon = matches.get(0).getLongitude();
				
				GeoPoint gp = new GeoPoint((int)(lat*1000000), (int)(lon*1000000));
				
				mc.animateTo(gp);
				mc.setZoom(15);
			}
			else {
				addresses = matches;
				mapView.showContextMenu();
			}
		}
		else if (matches != null && matches.size() == 0) {
			Toast toast = Toast.makeText(LocationChooserActivity.this, "No matches!", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
}
