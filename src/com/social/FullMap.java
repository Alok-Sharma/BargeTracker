package com.social;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONException;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

/*
 * 	BUG ALERT: WHEN THERE IS NO CONNECTION TO SERVER AND NO BARGE ARE DISPLAYED, THEN IF YOU GO TO THE FILTER OPTION AND
 * 				CLICK ON ANY OF THE FILTERING OPTIONS, APP WILL FORCE CLOSE. NULL POINTER AT `mo.refillOverlays()` IN `OnClick` OF THE RADIO BUTTONS.
 *	BUG ALERT: OVERLAYS CAN STILL BE CLICKED(OnTap) EVEN AFTER          *	REMOVING THEM USING THE FILTER.
 */

public class FullMap extends MapActivity{
	public final static String AUTH = "authentication";
	Intent toBargeMap=null;
	MapView map;
	GeoPoint point1;
	GeoPoint point2;
	GeoPoint point3;
	ArrayList<GeoPoint> geoList;
	public static ExecutorService threadPool = Executors.newCachedThreadPool();
	Handler handler;
	MyOverlays greenOverlays,redOverlays,yellowOverlays;
	private MyLocationOverlay me=null;
	int i=1;
	int APP_BEGIN=1;
	SharedPreferences mpref, bargedata;
	SharedPreferences.Editor edit, bargedataedit;
	AlertDialog alertDialog;
	Dialog viewOnly,helpcolor;
	private Timer timer;
	TelephonyManager myTelephony;
	PhoneStateListener callStateListener;
	ArrayList<String> BNameList,statList;
	ArrayList<Integer> latList=new ArrayList<Integer>(),lonList=new ArrayList<Integer>();
	Drawable blank_marker;
	MyOverlays mo;
	RadioButton radio1,radio2, radio3, radio4;
	TextView filtertext;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main1);
		map=(MapView)findViewById(R.id.map);
		map.setBuiltInZoomControls(true);
		filtertext=(TextView)findViewById(R.id.filtertext);

        handler = new Handler();

        Intent intent = new Intent("com.google.android.c2dm.intent.REGISTER"); // Intent for C2DM sending
        intent.putExtra("app",PendingIntent.getBroadcast(this, 0, new Intent(), 0));
        intent.putExtra("sender", "sdpdbargeproject@gmail.com");
        startService(intent);

        SharedPreferences prefsc2dm = PreferenceManager
        .getDefaultSharedPreferences(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	String message = extras.getString("payload"); // The C2DM message.
        	if (message != null && message.length() > 0) {
        		Log.d("C2dM from server", message);
        	}
        }

        mpref=FullMap.this.getSharedPreferences("mypref", Context.MODE_WORLD_WRITEABLE); 
        bargedata=FullMap.this.getSharedPreferences("bargedata", Context.MODE_WORLD_WRITEABLE);
        edit=mpref.edit();
        edit.putString("filter", "Show All"); // SharedPref storing state of the 'filter' option.
        edit.putBoolean("offline", false);
        edit.commit();
        bargedataedit=bargedata.edit();

        toBargeMap=new Intent(FullMap.this,BargeMap.class);

        MapController mapcontr=map.getController();
        mapcontr.setZoom(12);
        mapcontr.setCenter(new GeoPoint(28632888, 77119900));

        viewOnly=new Dialog(FullMap.this);// Dialog for the 'Filter' option
        viewOnly.setContentView(R.layout.viewonly);
        viewOnly.setTitle("View Only:");

        helpcolor=new Dialog(FullMap.this);	// Dialog for the 'Help' option
        helpcolor.setContentView(R.layout.helpcolor);
        helpcolor.setTitle("Help");
	}
    
    
	@Override
	protected void onPause(){
		super.onPause();
		Connectivity.stopListeningToConn(FullMap.this); // Stop listening to the connection when paused.
		timer.cancel();
	}
    @Override
    protected void onResume(){
    	super.onResume();
    	Connectivity.checkNet(FullMap.this); // Start checking the connection when resumed.

    	timer=new Timer();	// Refresh map via timer task.
    	timer.scheduleAtFixedRate(new TimerTask(){
    		@Override
    		public void run() {
    			final FetchParse fp=new FetchParse();
    			try {
    				blank_marker = getResources().getDrawable(R.drawable.green);
    				fp.fetch();		//fetch and parse from server				
    				BNameList=new ArrayList<String>();
    				statList=new ArrayList<String>();
    				lonList=new ArrayList<Integer>();latList=new ArrayList<Integer>();
    				mo=new MyOverlays(blank_marker);
    				if(!bargeOverlay.isEmpty()){
    					mo.removeAll();
    				}
    				handler.post(new Runnable()	// Change the UI via handler
    				{
    					public void run()
    					{
    						Log.d("Timer","Inside UI thread");
    						BNameList.clear();statList.clear();lonList.clear();bargeOverlay.clear();
    						allOverlay.clear();
    						for(int x=0;x<fp.namelist.size();x++){
    							Log.d("11111111", "added "+fp.namelist.get(x));
    							BNameList.add(fp.namelist.get(x));
    							statList.add(fp.statlist.get(x));
    							lonList.add(fp.lonlist.get(x));latList.add(fp.latlist.get(x));
    							mo.addOverlay(new OverlayItem(new GeoPoint(latList.get(x),lonList.get(x)),BNameList.get(x),statList.get(x)));
    						}
    						map.getOverlays().clear();
    						String filter=mpref.getString("filter", "Show All");
    						if(filter.equals("Stopped")){
    							mo.removeAllOverlay("Transporting");mo.removeAllOverlay("Docked");
    						}else if(filter.equals("Transporting")){
    							mo.removeAllOverlay("Docked");mo.removeAllOverlay("Stopped");
    						}else if(filter.equals("Docked")){
    							mo.removeAllOverlay("Transporting");mo.removeAllOverlay("Stopped");
    						}else if(filter.equals("Show All")){
    							map.getOverlays().add(mo);
    							map.postInvalidate();
    						}else{
    							Log.d("1111111", "crap");
    						}
    						map.getOverlays().add(mo);
    						map.postInvalidate();
    					}
    				});
    			} catch (IOException e) {
    				e.printStackTrace();
    			} catch (JSONException e) {
    				e.printStackTrace();
    			}

    		}

    	}, 0, 10000);	// refreshing after every 10 seconds
    	
    	map.getOverlays().clear();
    	OnClickListener radio_listener=new OnClickListener(){
    		public void onClick(View v){
    			RadioButton rb=(RadioButton) v;
    			String filter=rb.getText().toString();
    			if(filter.equals("Stopped")){
    				filtertext.setText("Displaying Stopped Barges");
    				mo.refillOverlays();
    				mo.removeAllOverlay("Transporting");
    				mo.removeAllOverlay("Docked");
    				Log.d("$$$$$$$", "called remove for trnsprt and docked");
    				edit.putString("filter", "Stopped");
    			}else if(filter.equals("Transporting")){
    				filtertext.setText("Displaying Transporting Barges");
    				mo.refillOverlays();
    				mo.removeAllOverlay("Docked");
    				mo.removeAllOverlay("Stopped");
    				Log.d("$$$$$$$", "called remove for docked and stopped");
    				edit.putString("filter", "Transporting");
    			}else if(filter.equals("Docked")){
    				filtertext.setText("Displaying Docked Barges");
    				mo.refillOverlays();
    				mo.removeAllOverlay("Transporting");
    				mo.removeAllOverlay("Stopped");
    				Log.d("$$$$$$$$", "called remove for trnsprt and stopped");
    				edit.putString("filter", "Docked");
    			}else if(filter.equals("Show All")){
    				filtertext.setText("Displaying All Barges");
    				mo.refillOverlays();
    				map.getOverlays().add(mo);
    				map.postInvalidate();
    				Log.d("$$$$$$$$", "not removing anything");
    				edit.putString("filter", "Show All");
    			}else{
    				Log.d("Problem", "o oh");
    			}
    			edit.commit();
    			map.getOverlays().add(mo);
    			map.postInvalidate();	//post the changes to the map.
    		}
    	};
    	Button helpok=(Button)helpcolor.findViewById(R.id.helpok);
    	helpok.setOnClickListener(new OnClickListener(){

    		@Override
    		public void onClick(View arg0) {
    			helpcolor.dismiss();
    		}

    	});
    	Button okbutton=(Button)viewOnly.findViewById(R.id.Ok);
    	okbutton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				viewOnly.dismiss();
			}
    		
    	});
    	
    	// Radio Buttons for the filter Barge dialog.
    	radio1=(RadioButton)viewOnly.findViewById(R.id.radio1);
    	radio2=(RadioButton)viewOnly.findViewById(R.id.radio2);
    	radio3=(RadioButton)viewOnly.findViewById(R.id.radio3);
    	radio4=(RadioButton)viewOnly.findViewById(R.id.radio4);
    	radio1.setOnClickListener(radio_listener);
    	radio2.setOnClickListener(radio_listener);
    	radio3.setOnClickListener(radio_listener);
    	radio4.setOnClickListener(radio_listener);
    }
    
    /*
     * Method will show only particualr status' Barge. 
     */
    public void showOnly(String filter){
	    map.getOverlays().add(mo);
	    map.postInvalidate();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	MenuInflater inflater=getMenuInflater();
    	inflater.inflate(R.menu.options, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch(item.getItemId()){
    	case R.id.listview:
    		Intent i= new Intent(FullMap.this,BargeList.class );
    		i.putExtra("size", bargeOverlay.size());
    		for(int j=0;j<bargeOverlay.size();j++){
        		i.putExtra("name"+j, bargeOverlay.get(j).getTitle());
        		i.putExtra("status"+j, bargeOverlay.get(j).getSnippet());
        		i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        	}
    		startActivity(i);
    		return true;
		case R.id.refine:
			Log.d("@@@@@@@", "refine");
    		String filter=mpref.getString("filter", "Show All");
    		if(filter.equals("Show All")){
    			radio4.setSelected(true);
    		}else if(filter.equals("Stopped")){
    			radio3.setSelected(true);
    		}else if(filter.equals("Transporting")){
    			radio1.setSelected(true);
    		}else if(filter.equals("Docked")){
    			radio2.setSelected(true);
    		}else{
    			Log.d("111111111", "whoops");
    		}
			viewOnly.show();
			break;
		case R.id.helpcolor:
			helpcolor.show();
			break;
    	case R.id.exit:
    		Log.d("@@@@@@", "exit");
    		moveTaskToBack(true);
    		break;
    	default:
            return super.onOptionsItemSelected(item);
    	}
    	return super.onOptionsItemSelected(item);
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	
	private ArrayList<OverlayItem> bargeOverlay=new ArrayList<OverlayItem>();
	private ArrayList<OverlayItem> allOverlay=new ArrayList<OverlayItem>();
	
	private class MyOverlays extends ItemizedOverlay<OverlayItem>{
		private Drawable blank_marker=null;
		public MyOverlays(Drawable blank_marker) {
			super(blank_marker);
			this.blank_marker=blank_marker;
			setLastFocusedIndex(-1);
			populate();
		}
		
		@Override
		protected OverlayItem createItem(int i) {
			return bargeOverlay.get(i);
		}

		@Override
		public int size() {
			Log.d("1111111", "size:"+bargeOverlay.size());
			return bargeOverlay.size();
		}
		
		/*
		 * Given an OverlayItem as argument, this adds it to 
		 * the arrayList of Overlays and re-populates the map.
		 */
		public void addOverlay(OverlayItem barge){
			bargeOverlay.add(barge);
			allOverlay.add(barge);
			setLastFocusedIndex(-1);
			populate();
		}
		
		/*
		 * String s: Either of "Transporting", "Docked" or "Stopped"
		 * The method removes all overlays that have their status as the argument provided.
		 */
		public void removeAllOverlay(String s){
			int remover=0;
			for(remover=0;remover<bargeOverlay.size();remover++){
				if(bargeOverlay.get(remover).getSnippet().equals(s)){
					Log.d("$$$$$$$", "removing "+bargeOverlay.get(remover).getSnippet()+bargeOverlay.get(remover).getTitle());
					bargeOverlay.remove(remover);
					remover=remover-1;
				}else{
					Log.d("$$$$$$", "not removing: "+bargeOverlay.get(remover).getTitle());
				}
			}
			setLastFocusedIndex(-1);
		}
		
		/*
		 * Remove all the overlays from the arraylist.
		 */
		public void removeAll(){
			if(!bargeOverlay.isEmpty()){
				bargeOverlay.clear();
			}
			setLastFocusedIndex(-1);
		}
		
		/*
		 * Add all overlay items in `allOverlay` into `bargeOverlay`
		 * This is useful when we need to refresh the list of Barge and update the map.
		 */
		public void refillOverlays(){
			removeAll();
			for(int y=0;y<allOverlay.size();y++){
				bargeOverlay.add(allOverlay.get(y));
			}
			setLastFocusedIndex(-1);
		}
		
		@Override
		public boolean onTap(int index){
			refillOverlays(); 
			toBargeMap.putExtra("name", bargeOverlay.get(index).getTitle());
			toBargeMap.putExtra("status", bargeOverlay.get(index).getSnippet());
			toBargeMap.putExtra("lat", bargeOverlay.get(index).getPoint().getLatitudeE6());
			toBargeMap.putExtra("lon", bargeOverlay.get(index).getPoint().getLongitudeE6());
			toBargeMap.putExtra("size", bargeOverlay.size());
			startActivity(toBargeMap);
			return true;
		}
		
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) 
		{
			Log.d("111111111", "ondraw");
			super.draw(canvas, mapView, shadow);
			Paint blackText = new Paint();
			Paint strokePaint=new Paint();
			Point myScreenCoords = new Point();
			Point myScreenCoords1 = new Point();
			Point myScreenCoords2 = new Point();
			blackText.setARGB(255,0,0, 0);
			blackText.setStyle(Paint.Style.FILL);
			Resources res=getResources();
			float fontSize=res.getDimension(R.dimen.font_size);
			blackText.setTextSize(fontSize);
			blackText.setTextAlign(Paint.Align.CENTER);
			blackText.setStrokeWidth(7);
			blackText.setFakeBoldText(true);
			strokePaint.setARGB(255, 255,255,255);
			strokePaint.setAntiAlias(true);
			strokePaint.setStrokeWidth(3);
			strokePaint.setStyle(Paint.Style.STROKE);
			strokePaint.setTextAlign(Paint.Align.CENTER);
			strokePaint.setTextSize(fontSize); 
			Bitmap greenflag=BitmapFactory.decodeResource(getResources(), R.drawable.green);
			Bitmap redflag=BitmapFactory.decodeResource(getResources(), R.drawable.red);
			Bitmap yellowflag=BitmapFactory.decodeResource(getResources(), R.drawable.yellow);
			
			for(int i=0;i<bargeOverlay.size();i++){
				GeoPoint someplace=bargeOverlay.get(i).getPoint();		
				map.getProjection().toPixels(someplace, myScreenCoords1);
				if(bargeOverlay.get(i).getSnippet().equals("Transporting")){	//Green light
					canvas.drawBitmap(greenflag, myScreenCoords1.x-18,myScreenCoords1.y-20, null);
				}
				else if(bargeOverlay.get(i).getSnippet().equals("Stopped")){		//red
					canvas.drawBitmap(redflag, myScreenCoords1.x-18,myScreenCoords1.y-20, null);
				}
				else{														//yellow
					canvas.drawBitmap(yellowflag, myScreenCoords1.x-18,myScreenCoords1.y-20, null);
				}

				GeoPoint point=bargeOverlay.get(i).getPoint();		//Upper Text
				map.getProjection().toPixels(point, myScreenCoords);
				canvas.drawText(bargeOverlay.get(i).getTitle(),myScreenCoords.x+8,
						myScreenCoords.y - 30, strokePaint);
				canvas.drawText(bargeOverlay.get(i).getTitle(),myScreenCoords.x+8,
						myScreenCoords.y - 30, blackText);
			}
		}
	}
}