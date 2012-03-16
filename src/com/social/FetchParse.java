package com.social;

import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

public class FetchParse {
	ArrayList<String> namelist=new ArrayList<String>();
	ArrayList<Integer> latlist=new ArrayList<Integer>();
	ArrayList<Integer> lonlist=new ArrayList<Integer>();
	ArrayList<String> statlist=new ArrayList<String>();
	ArrayList<String> timelist=new ArrayList<String>();
	
	public void fetch() throws IOException, JSONException{
//		URL url=new URL("http://10.0.2.2:8084/test.json");
//		URLConnection con=url.openConnection();
//		BufferedReader str=new BufferedReader(new InputStreamReader(con.getInputStream()));
//		Log.d("4444444", "connected");
//		String ans=new String("");
//		String build=new String("");
//		while((ans=str.readLine())!=null){
//			build=build+ans;
			
//		}
//		process(build.toString());
		//hard-coding for demo purposes
		namelist.add("Demo 1");
		latlist.add(28632777);
		lonlist.add(77219722);
		statlist.add("Transporting");
		timelist.add("0:10");
		
		namelist.add("Demo 2");
		latlist.add(28732777);
		lonlist.add(77119722);
		statlist.add("Docked");
		timelist.add("1:10");
		
		namelist.add("Demo 3");
		latlist.add(28432777);
		lonlist.add(77019722);
		statlist.add("Stopped");
		timelist.add("0:37");
		
		namelist.add("Demo 4");
		latlist.add(28532777);
		lonlist.add(77019722);
		statlist.add("Transporting");
		timelist.add("1:27");
		
		namelist.add("Demo 5");
		latlist.add(28562777);
		lonlist.add(77119722);
		statlist.add("Docked");
		timelist.add("0:27");
		
		namelist.add("Demo 6");
		latlist.add(28632777);
		lonlist.add(77119722);
		statlist.add("Stopped");
		timelist.add("0:40");
	}
	
	public void process(String str) throws JSONException{
		int i=0;
		Integer deviceTime;
		Integer timeDiff;
		JSONArray arr=new JSONArray(str);
		for(i=0;i<arr.length();i++){
			Log.d("4444444", "parsing json");
			JSONObject ob=arr.getJSONObject(i);
			deviceTime = (int) (System.currentTimeMillis()/60000);
			Log.d("Device time", String.valueOf(deviceTime));
			String bargeName=ob.getString("name");
			Integer lat=ob.getInt("lat");
			Integer lon=ob.getInt("long");
			String status=ob.getString("stat");
			Integer timestamp = ob.getInt("time");
			Log.d("Server time Stamp", String.valueOf(timestamp));
			namelist.add(bargeName);
			latlist.add(lat);
			lonlist.add(lon);
			statlist.add(status);
			timeDiff = deviceTime - timestamp;
			Log.d("Time Diff", String.valueOf(timeDiff));
			Float timeHours = Float.valueOf((float)timeDiff/60);
			Integer timeHrs = timeDiff/60;
			Log.d("Time Hours float", String.valueOf(timeHours));
			Integer timeMins = (int) ((timeHours - timeHrs)*60);
			Log.d("Time hours", String.valueOf(timeHrs));
			Log.d("Time mins", String.valueOf(timeMins));
			timelist.add(timeHrs+":"+timeMins);
			
			//process here
		}
		
	}
	public void writeJSON(){
		JSONObject object=new JSONObject();
		try{
			object.put("name", "alok was here");
			Log.d("444444444", "tried putting now");
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
}
