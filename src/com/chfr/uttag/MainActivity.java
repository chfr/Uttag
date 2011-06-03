package com.chfr.uttag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;

public class MainActivity extends MapActivity {
	private Location lastLoc;
	private LocationManager lm = null;
	private LocationListener ll = null;
	private List<Address> addresses;
	private ArrayList<ATM> mAtms;
	
	final public static int ADD_ATM_REQUESTCODE = 1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        
        loadAtms();
        
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        ll = new MyLocationListener();
        //TODO more sensible values for time/distance
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
        
        final MapView mv = (MapView)findViewById(R.id.main_mapview);
		
		final MapController mc = mv.getController();
        
        final EditText edittext = (EditText) findViewById(R.id.location_search_field);
        registerForContextMenu(mv);
        edittext.setOnKeyListener(new OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                	String s = edittext.getText().toString();
                	
                	Geocoder gc = new Geocoder(MainActivity.this);
                	
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
							mv.showContextMenu();
						}
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
        
        final ImageButton mylocation = (ImageButton) findViewById(R.id.get_location_button);
        mylocation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (lastLoc != null) {
					double lat,lon;
					lat = lastLoc.getLatitude();
					lon = lastLoc.getLongitude();
					GeoPoint gp = new GeoPoint((int)(lat*1E6),(int)(lon*1E6));
					
					mc.animateTo(gp);
					mc.setZoom(15);
				}
				else {
					Toast toast = Toast.makeText(getApplicationContext(), "Waiting for location...", Toast.LENGTH_SHORT);
					toast.show();
					// TODO auto-update when location is acquired, with MyLocationOverlay?
				}
			}
		});
        
        final ImageButton addLocation = (ImageButton) findViewById(R.id.add_atm_button);
        addLocation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, AddAtmActivity.class);
				MainActivity.this.startActivityForResult(i, ADD_ATM_REQUESTCODE);				
			}
		});   
    
        // TODO write smarter overlay class?
        
		/*List<Overlay> mapOverlays = mv.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.marker);
        OverlayItem overlayitem;
        ItemizedOverlayMarker itemizedoverlay = new ItemizedOverlayMarker(drawable,getApplicationContext());
        
        for (ATM a : mAtms) {
        	overlayitem = new OverlayItem(a.getLocation(), "Nordea", a.getDescription());
        	itemizedoverlay.addOverlay(overlayitem);
        }
        mapOverlays.add(itemizedoverlay);*/
        
    }
    
    private void loadAtms() {
    	mAtms = new ArrayList<ATM>();
    	/*
		mAtms.add(new ATM(new GeoPoint(59332945, 18070379), "NK Saluhall "));
		mAtms.add(new ATM(new GeoPoint(59316827, 18074964), "Ica Mosebacke "));
		mAtms.add(new ATM(new GeoPoint(59309000, 18064431), "Ica Riddaren "));
		mAtms.add(new ATM(new GeoPoint(59307901, 18091404), "Ica Vintertullen "));
		mAtms.add(new ATM(new GeoPoint(59308367, 18081179), "Ica Lansen "));
		mAtms.add(new ATM(new GeoPoint(59313076, 18068619), "Ica Gillet "));
		mAtms.add(new ATM(new GeoPoint(59315578, 18031808), "Ica Flamman "));
		mAtms.add(new ATM(new GeoPoint(59315964, 18013301), "Ica Nära Gröndal "));
		mAtms.add(new ATM(new GeoPoint(59334369, 18074727), "Ica Östermalmstorg T-station "));
		mAtms.add(new ATM(new GeoPoint(59343026, 18098300), "Ica Värtavägen "));
		mAtms.add(new ATM(new GeoPoint(59345190, 18110542), "Ica Gärdet "));
		mAtms.add(new ATM(new GeoPoint(59338203, 18089151), "Ica Karlaplan "));
		mAtms.add(new ATM(new GeoPoint(59338568, 18084676), "Ica Nära Humlegården "));
		mAtms.add(new ATM(new GeoPoint(59370042, 18061503), "Ica Lappkärsberget "));
		mAtms.add(new ATM(new GeoPoint(59346613, 18100089), "ICA Gärdet "));
		mAtms.add(new ATM(new GeoPoint(59314223, 18074931), "ICA Supermarket Folkungagatan "));
		mAtms.add(new ATM(new GeoPoint(59315340, 18084491), "ICA Supermarket Matmäster "));
		mAtms.add(new ATM(new GeoPoint(59312277, 18057676), "ICA Supermarket Södra station "));
		mAtms.add(new ATM(new GeoPoint(59317703, 18057068), "ICA Supermarket Aptiten "));
		mAtms.add(new ATM(new GeoPoint(59310587, 18023262), "ICA Supermarket Liljeholmen "));
		mAtms.add(new ATM(new GeoPoint(59338437, 18090116), "ICA Supermarket Fältöversten "));
		mAtms.add(new ATM(new GeoPoint(59338978, 18081767), "ICA Supermarket Esplanad "));
		mAtms.add(new ATM(new GeoPoint(59332338, 18090726), "ICA Baner "));
		mAtms.add(new ATM(new GeoPoint(59334714, 18032322), "ICA Supermarket Västermalmsgallerian "));
		mAtms.add(new ATM(new GeoPoint(59328026, 18043201), "ICA Supermarket Kungsholmstorg "));
		mAtms.add(new ATM(new GeoPoint(59349178, 18059644), "ICA Supermarket Roslagstull "));
		mAtms.add(new ATM(new GeoPoint(59344639, 18057269), "ICA Supermarket Baronen "));
		mAtms.add(new ATM(new GeoPoint(59330432, 18035908), "Ica Nära Hantverkargatan "));
		mAtms.add(new ATM(new GeoPoint(59324965, 18007788), "Hulvéns Livs "));
		mAtms.add(new ATM(new GeoPoint(59332532, 18031775), "ICA Köttmästaren "));
		mAtms.add(new ATM(new GeoPoint(59340517, 18032210), "Birkahallen "));
		mAtms.add(new ATM(new GeoPoint(59339476, 18036325), "ICA S:t Eriksplan "));
		mAtms.add(new ATM(new GeoPoint(59342720, 18036749), "ICA Mathörnan "));
		mAtms.add(new ATM(new GeoPoint(59343691, 18041911), "ICA Dalastan "));
		mAtms.add(new ATM(new GeoPoint(59313608, 18092631), "Coop Konsum Bondegatan "));
		mAtms.add(new ATM(new GeoPoint(59285535, 17965437), "Coop Konsum Fruängen C "));
		mAtms.add(new ATM(new GeoPoint(59316297, 18011689), "Coop Konsum Gröndal "));
		mAtms.add(new ATM(new GeoPoint(59302685, 18103048), "Coop Konsum Hammarby Sjöstad "));
		mAtms.add(new ATM(new GeoPoint(59316753, 18033958), "Coop Konsum Hornstull "));
		mAtms.add(new ATM(new GeoPoint(59310759, 18023678), "Coop Konsum Liljeholmen "));
		mAtms.add(new ATM(new GeoPoint(59318960, 18062246), "Coop Konsum Mariatorget "));
		mAtms.add(new ATM(new GeoPoint(59345206, 18060823), "Coop Konsum Odengatan "));
		mAtms.add(new ATM(new GeoPoint(59343021, 18052151), "Coop Konsum Odenplan "));
		mAtms.add(new ATM(new GeoPoint(59331622, 18045072), "Coop Konsum Rådhuset "));
		mAtms.add(new ATM(new GeoPoint(59319646, 18072920), "Coop Konsum Slussen "));
		mAtms.add(new ATM(new GeoPoint(59339425, 18060176), "Coop Konsum Sveavägen "));
		mAtms.add(new ATM(new GeoPoint(59313574, 18065754), "Coop Konsum Södra Station "));
		mAtms.add(new ATM(new GeoPoint(59308188, 18091424), "Coop Konsum Vintertullen "));
		mAtms.add(new ATM(new GeoPoint(59336245, 18080407), "Coop Konsum Östermalmstorg "));
		*/
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == ADD_ATM_REQUESTCODE) {
    		if (resultCode == AddAtmActivity.RESULT_OK) {
    			// TODO add overlays and stuff
    		}
    		else if (resultCode == AddAtmActivity.RESULT_CANCEL) {
    			// TODO do nothing here?
    		}
    	}
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
		
		MapView mv = (MapView)findViewById(R.id.main_mapview);
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
		public void onProviderDisabled(String provider) {}

		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
		
	}
}