package com.social;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONException;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class BargeList extends ListActivity {
	SharedPreferences mpref;
	SharedPreferences.Editor edit;
	TelephonyManager myTelephony;
	PhoneStateListener callStateListener;
	AlertDialog alertDialog;
	private Timer timer;
	Handler handler;
	Intent toBargeMap;
	ArrayAdapter<Model> myadapter;
	ListView bargeList;
	ArrayList<String> bargeNames;
	ArrayList<String> bargeStatus;
	ArrayList<String> bargeTimes;
	ArrayList<Integer> latList,lonList;
	Dialog helpcolor;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mpref=this.getSharedPreferences("mypref", 0);
		handler=new Handler();
		bargeStatus = new ArrayList<String>();
		bargeNames = new ArrayList<String>();
		bargeTimes = new ArrayList<String>();
		latList=new ArrayList<Integer>();lonList=new ArrayList<Integer>();
		toBargeMap=new Intent(BargeList.this,BargeMap.class);
		bargeList=getListView();
		bargeList.setClickable(true);
		bargeList.setItemsCanFocus(false);
    	
		helpcolor=new Dialog(BargeList.this);
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
    	Connectivity.stopListeningToConn(BargeList.this);
    	timer.cancel();
    }
    @Override
    protected void onResume(){
    	super.onResume();
    	Connectivity.checkNet(BargeList.this);
    	Log.d("333333333", "list is listening again");
    	Connectivity.LOST_CONN=0;	//doubtful about this
    	
    	
    	timer=new Timer(); // This can be made into a separate class.
    	timer.scheduleAtFixedRate(new TimerTask(){

    		@Override
    		public void run() {
    			FetchParse fp=new FetchParse();
    			try {
    				fp.fetch();
    				bargeNames.clear();bargeStatus.clear();bargeTimes.clear();
    				latList.clear();lonList.clear();
    				for(int z=0;z<fp.namelist.size();z++){
    					bargeNames.add(fp.namelist.get(z));
    					bargeStatus.add(fp.statlist.get(z));
    					bargeTimes.add(fp.timelist.get(z));
    					latList.add(fp.latlist.get(z));
    					lonList.add(fp.lonlist.get(z));
    					Log.d("3333333", "added barge");
    				}
    				handler.post(new Runnable(){

						@Override
						public void run() {
							Log.d("3333333", "inside the runnable");
							  	bargeList = getListView();
							  	bargeList.requestFocus();
							  	
						    	bargeList.setOnItemClickListener(new OnItemClickListener(){
						    		
						 			@Override
						 			public void onItemClick(AdapterView<?> parent, View view, int position,
						 					long id) {
						 				Log.d("33333333", "inside on click of list");
						 				toBargeMap.putExtra("name",bargeNames.get(position));
						 				toBargeMap.putExtra("status", bargeStatus.get(position));
						 				toBargeMap.putExtra("lat", latList.get(position));
						 				toBargeMap.putExtra("lon", lonList.get(position));
						 				startActivity(toBargeMap);
						 			}
						 		});
						    	myadapter= new BargeArrayAdapter(BargeList.this,getModel(1),getModel(2),getModel(3));
								setListAdapter(myadapter);
						}
    				});
    			} catch (IOException e) {
    				e.printStackTrace();
    			} catch (JSONException e) {
    				e.printStackTrace();
    			}
    		}
    	}, 1000, 10000); //refresh every 10 seconds
    	
    	
    }
    
    private List<Model> getModel(int i){
		if(i==2){
			List<Model> list2=new ArrayList<Model>();
			
			for(i=0;i<bargeStatus.size();i++){
				list2.add(get2(bargeStatus.get(i)));
			}
			return list2;
		}
		if(i==1){
			List<Model> list= new ArrayList<Model>();
			
			for(i=0;i<bargeNames.size();i++){
				list.add(get(bargeNames.get(i)));
			}
			return list;
		}
		if(i==3){
			List<Model> list3= new ArrayList<Model>();
			
			for(i=0;i<bargeTimes.size();i++){
				list3.add(get3(bargeTimes.get(i)));
			}
			return list3;
		}
		return null;
	}
    private Model get(String n){
		return new Model(n, null, null);
	}
	
	private Model get2(String s){
		return new Model(null, s, null);
	}

	private Model get3(String t){
		return new Model(null, null, t);
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.prefmenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        
        case R.id.mapview:
        	
        	Intent i = new Intent(this, FullMap.class);
        	i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        	startActivity(i);
            return true;
        case R.id.helpcolor:
        	helpcolor.show();
        	break;
        case R.id.exit:
        	moveTaskToBack(true);
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}