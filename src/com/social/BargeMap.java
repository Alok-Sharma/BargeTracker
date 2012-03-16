package com.social;

import java.util.ArrayList;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

public class BargeMap extends MapActivity {
	String BName;
	String status;
	GeoPoint point;
	Bundle extras;
	private MyLocationOverlay me;
	MapView map;
	SharedPreferences mpref;
	SharedPreferences.Editor edit;
	TelephonyManager myTelephony;
	PhoneStateListener callStateListener;
	AlertDialog alertDialog;
	Dialog helpcolor;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.barge_map);
		map=(MapView)findViewById(R.id.map2);
		map.setBuiltInZoomControls(true);
		MapController mapcontr=map.getController();
		mapcontr.setZoom(12);

		mpref=this.getSharedPreferences("mypref", Context.MODE_WORLD_WRITEABLE);

		extras=getIntent().getExtras();
		BName=extras.getString("name");
		status=extras.getString("status");
		int lat=extras.getInt("lat");
		int lon=extras.getInt("lon");

		point=new GeoPoint(lat,lon);
		mapcontr.setCenter(point);

		Drawable blank_marker = getResources().getDrawable(R.drawable.blank_space);
		me=new MyLocationOverlay(this,map);
		map.getOverlays().add(me);
		map.getOverlays().add(new singleOverlay(blank_marker));

		TextView title=(TextView)findViewById(R.id.title);
		TextView text1=(TextView)findViewById(R.id.text1);

		title.setText(BName+" Details");
		text1.setText(status);

		helpcolor=new Dialog(BargeMap.this);
		helpcolor.setContentView(R.layout.helpcolor);
		helpcolor.setTitle("Help");
		Button helpok=(Button)helpcolor.findViewById(R.id.helpok);
		helpok.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				helpcolor.dismiss();
			}

		});
	}
	 
	@Override
	protected void onPause(){
		super.onPause();
		Connectivity.stopListeningToConn(BargeMap.this);

	}
	@Override
	protected void onResume(){
		super.onResume();
		Connectivity.checkNet(BargeMap.this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater=getMenuInflater();
		inflater.inflate(R.menu.options2, menu);
		return true;
	}
	 
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.mapview:
			Intent i= new Intent(BargeMap.this,FullMap.class );
			i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(i);
			return true;
		case R.id.listview:
			Intent i1= new Intent(BargeMap.this,BargeList.class );
			i1.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(i1);
			return true;
		case R.id.helpcolor:
			helpcolor.show();
			return true;
		case R.id.exit:
			moveTaskToBack(true);
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private ArrayList<OverlayItem> singleOverlay=new ArrayList<OverlayItem>();
	
	private class singleOverlay extends ItemizedOverlay<OverlayItem>{

		private Drawable blank_marker=null;
		public singleOverlay(Drawable blank_marker) {
			super(blank_marker);
			this.blank_marker=blank_marker;
			singleOverlay.add(new OverlayItem(point,BName,status));
			populate();
		}

		@Override
		protected OverlayItem createItem(int i) {
			return singleOverlay.get(i);
		}

		@Override
		public int size() {
			return singleOverlay.size();
		}
		
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);
			Paint blackText = new Paint();
			Paint strokePaint=new Paint();
			Point myScreenCoords = new Point();
			Point myScreenCoords1 = new Point();
			Point myScreenCoords2 = new Point();
			blackText.setARGB(255,0,0, 0);
			blackText.setStyle(Paint.Style.FILL);
			blackText.setTextSize(19);
			blackText.setTextAlign(Paint.Align.CENTER);
			blackText.setStrokeWidth(7);
			blackText.setFakeBoldText(true);
			strokePaint.setARGB(255, 255,255,255);
			strokePaint.setAntiAlias(true);
			strokePaint.setStrokeWidth(5);
			strokePaint.setStyle(Paint.Style.STROKE);
			strokePaint.setTextAlign(Paint.Align.CENTER);
			strokePaint.setTextSize(19); 
			Bitmap greenflag=BitmapFactory.decodeResource(getResources(), R.drawable.green);
			Bitmap redflag=BitmapFactory.decodeResource(getResources(), R.drawable.red);
			Bitmap yellowflag=BitmapFactory.decodeResource(getResources(), R.drawable.yellow);
			
			for(int i=0;i<singleOverlay.size();i++){
				GeoPoint someplace=singleOverlay.get(i).getPoint();		
				map.getProjection().toPixels(someplace, myScreenCoords1);
				if(singleOverlay.get(i).getSnippet().equals("Transporting")){	//Green light
					canvas.drawBitmap(greenflag, myScreenCoords1.x-18,myScreenCoords1.y-20, null);
				}
				else if(singleOverlay.get(i).getSnippet().equals("Stopped")){		//red
					canvas.drawBitmap(redflag, myScreenCoords1.x-18,myScreenCoords1.y-20, null);
				}
				else{														//yellow
					canvas.drawBitmap(yellowflag, myScreenCoords1.x-18,myScreenCoords1.y-20, null);
				}

				GeoPoint point=singleOverlay.get(i).getPoint();		//Upper Text
				map.getProjection().toPixels(point, myScreenCoords);
				canvas.drawText(singleOverlay.get(i).getTitle(),myScreenCoords.x+8,
						myScreenCoords.y - 30, strokePaint);
				canvas.drawText(singleOverlay.get(i).getTitle(),myScreenCoords.x+8,
						myScreenCoords.y - 30, blackText);
			}
		}
		
	}

}
