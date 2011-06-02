package com.chfr.uttag;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class LocationOverlay extends Overlay {
	
	private GeoPoint point;
	private Context mContext;
	private float overlayWidth;
	private float overlayHeight;
	
	private final int WPADDING = 40;
	private final int HPADDING = 60;
	
	public LocationOverlay(Context c) {
		mContext = c;
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
		String s = mContext.getString(R.string.tap_select);
		
		Rect r = new Rect();
		p1.getTextBounds(s,0,s.length(), r);
		float w = Math.abs(r.left-r.right);
		float h = Math.abs(r.top-r.bottom);
		
		if (w % 2 != 0)
			w += 1;
		
		// pad w and h to fully contain the text
		w += WPADDING;
		h += HPADDING;
		overlayWidth = w+30;
		overlayHeight = h;
		
			
		Paint p2 = new Paint();
		p2.setColor(Color.WHITE);
		
		Path path = new Path();
		path.moveTo(pt.x, pt.y);
		// left edge of tip
		path.lineTo(pt.x-15, pt.y-15);
		// left bottom flat
		path.lineTo(pt.x-(w/2), pt.y-15);
		// left vertical
		path.lineTo(pt.x-(w/2), pt.y-h);
		// top flat
		path.lineTo(pt.x+(w/2)+30, pt.y-h);
		// right vertical
		path.lineTo(pt.x+(w/2)+30, pt.y-15);
		// right bottom flat
		path.lineTo(pt.x+15, pt.y-15);
		// right edge of tip
		path.lineTo(pt.x, pt.y);
		
		p2.setStyle(Paint.Style.FILL);
		canvas.drawPath(path, p2);
		canvas.drawPath(path, p1);
		
		p1.setAntiAlias(true);
		canvas.drawText(mContext.getString(R.string.tap_select), pt.x-(w/2)+(WPADDING/2), pt.y-15-(HPADDING/2), p1);
		
		
		super.draw(canvas, mapView, shadow);
	}


	@Override
	public boolean onTap(GeoPoint p, MapView mapView)  {
		if (point == null) {
			point = p;
			return true;
		}
		// where the user tapped
		Point tap = mapView.getProjection().toPixels(p, null);
		// where the overlay is drawn
		Point op = mapView.getProjection().toPixels(point, null);
			
		if (tap.x > op.x-(overlayWidth/2) && tap.x < op.x+(overlayWidth/2)
				&& tap.y > op.y-overlayHeight+15 && tap.y < op.y+15) {
			// TODO return result to LocationChooserActivity
			return true;
		}
		
		point = p;
  		  
		return super.onTap(p, mapView);
	}

}
