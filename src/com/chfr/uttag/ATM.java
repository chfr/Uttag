package com.chfr.uttag;

import com.google.android.maps.GeoPoint;

public class ATM {
	private GeoPoint mLocation;
	private String mDescription;
	
	public ATM(GeoPoint loc, String desc) {
		mLocation = loc;
		mDescription = desc;
	}
	
	
	public GeoPoint getLocation() {return mLocation;}
	public void setLocation(GeoPoint mLocation) {this.mLocation = mLocation;}
	public String getDescription() {return mDescription;}
	public void setDescription(String mDescription) { this.mDescription = mDescription; }
}
