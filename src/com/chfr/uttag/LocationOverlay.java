package com.chfr.uttag;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;


public class LocationOverlay extends Overlay {

	private GeoPoint point;
	private Context mContext;
	private Activity mActivity;
	private float overlayWidth;
	private float overlayHeight;
	private int mapWidth;
	private int mapHeight;
	private String mDrawString;

	private final int WPADDING = 20;
	private final int HPADDING = 40;
	private final int TIPWIDTH = 15;
	private final int TIPHEIGHT = 15;

	// TODO check if overlay is visible before drawing?

	public LocationOverlay(Context c, Activity a) {
		mContext = c;
		mActivity = a;
		mDrawString = mContext.getString(R.string.tap_select);
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if (point == null)
			return;

		Point pt = mapView.getProjection().toPixels(point, null);

		Paint p1 = new Paint();
		p1.setColor(Color.BLACK);
		p1.setAntiAlias(false);
		p1.setStyle(Paint.Style.STROKE);
		p1.setTextSize(40);

		Rect r = new Rect();
		p1.getTextBounds(mDrawString, 0, mDrawString.length(), r);
		float w = Math.abs(r.width());
		float h = Math.abs(r.height());
		
		if (w % 2 != 0)
			w += 1;

		// pad w and h to fully contain the text
		w += WPADDING;
		h += HPADDING;
		// account for the width/height of the tip
		overlayWidth = w + TIPWIDTH*2;
		overlayHeight = h + TIPHEIGHT;

		mapWidth = mapView.getWidth();
		mapHeight = mapView.getHeight();

		// determine of the overlay will be completely outside the viewing area
		if (pt.x - overlayWidth / 2 > mapWidth || // left edge of overlay beyond right edge of map
				pt.x + overlayWidth / 2 < 0 || // right overlay edge beyond left map edge
				pt.y < 0 || // bottom of overlay above the upper map edge
				pt.y - overlayHeight > mapHeight) {// top of overlay under lower map edge

			return;
		}

		Paint p2 = new Paint();
		p2.setColor(Color.WHITE);

		// drawing the overlay, shaped like so:
		//  __________
		// |____  ____|
		//      \/
		// with text inside. drawing starts from the bottom center, moving clockwise

		Path path = new Path();
		path.moveTo(pt.x, pt.y);
		// left edge of tip
		path.lineTo(pt.x - TIPWIDTH, pt.y - TIPHEIGHT);
		// left bottom flat
		path.lineTo(pt.x - (w / 2) - TIPWIDTH, pt.y - TIPHEIGHT);
		// left vertical
		path.lineTo(pt.x - (w / 2) - TIPWIDTH, pt.y - h);
		// top flat
		path.lineTo(pt.x + (w / 2) + TIPWIDTH, pt.y - h);
		// right vertical
		path.lineTo(pt.x + (w / 2) + TIPWIDTH, pt.y - TIPHEIGHT);
		// right bottom flat
		path.lineTo(pt.x + TIPWIDTH, pt.y - TIPHEIGHT);
		// right edge of tip
		path.lineTo(pt.x, pt.y);

		p2.setStyle(Paint.Style.FILL);
		canvas.drawPath(path, p2);
		canvas.drawPath(path, p1);
		
		r.offsetTo((int)(pt.x-overlayWidth/2), (int)(pt.y-overlayHeight/2));

		p1.setAntiAlias(true);
		
		int centerOverlayX = pt.x;
		int centerOverlayY = pt.y - (int)overlayHeight/2 - TIPHEIGHT/2;	
		int startX = centerOverlayX - r.width()/2;
		int startY = centerOverlayY + r.height()/2;
		
		canvas.drawText(mDrawString, startX, startY, p1);

		super.draw(canvas, mapView, shadow);
	}

	@Override
	public boolean onTap(GeoPoint p, MapView mapView) {
		if (point == null) {
			point = p;
			return true;
		}
		// where the user tapped
		Point tap = mapView.getProjection().toPixels(p, null);
		// where the overlay is drawn
		Point op = mapView.getProjection().toPixels(point, null);

		// TODO use Rect.contains(x,y) instead if silly manual arithmetic
		if (tap.x > op.x - (overlayWidth / 2) && tap.x < op.x + (overlayWidth / 2)
				&& tap.y > op.y - overlayHeight + 15 && tap.y < op.y + 15) {
			
			Intent i = new Intent();
			i.putExtra("com.chfr.Uttag.tapLat", point.getLatitudeE6());
			i.putExtra("com.chfr.Uttag.tapLon", point.getLongitudeE6());

			mActivity = (LocationChooserActivity) mActivity;
			mActivity.setResult(LocationChooserActivity.RESULT_OK, i);
			mActivity.finish();

			return true;
		}

		point = p;

		return super.onTap(p, mapView);
	}

}
