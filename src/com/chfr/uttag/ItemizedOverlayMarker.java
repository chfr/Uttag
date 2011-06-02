package com.chfr.uttag;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class ItemizedOverlayMarker extends ItemizedOverlay<OverlayItem> {
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;

	public ItemizedOverlayMarker(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}
	
	public ItemizedOverlayMarker(Drawable defaultMarker, Context context) {
		  super(boundCenterBottom(defaultMarker));
		  mContext = context;
	}
	
	@Override
	protected boolean onTap(int index) {
		  OverlayItem item = mOverlays.get(index);
		  Toast toast = Toast.makeText(this.mContext, item.getTitle() + " " +  item.getSnippet(),Toast.LENGTH_SHORT);
		  toast.show();
		  return true;
	}

	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return mOverlays.get(i);
	}
	
	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return mOverlays.size();
	}

}
