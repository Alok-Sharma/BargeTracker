package com.social;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class Connectivity {
	static TelephonyManager myTelephony;
	static PhoneStateListener callStateListener;
	static AlertDialog alertDialog;
	static SharedPreferences.Editor edit;


	static int LOST_CONN=0;	//initially network error dialog isnt up and connection hasnt been lost.
	static int DIALOG_UP=0;

	public static void checkNet(final Context context){
		Log.d("FINAL", "Inside checknet with initial lost_conn=0");
		final SharedPreferences mpref=context.getSharedPreferences("mypref", Context.MODE_WORLD_WRITEABLE);
		edit=mpref.edit();
		myTelephony=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

		callStateListener=new PhoneStateListener(){ // Listen for any connectivity changes.
			@Override
			public void onDataConnectionStateChanged(int state){
				switch(state){
				case TelephonyManager.DATA_DISCONNECTED:
					LOST_CONN=1;
					Log.d("FINAL", "set lost_conn=1");
					if(mpref.getBoolean("offline", false)==false){
						DIALOG_UP=1;
						alertDialog=new AlertDialog.Builder(context).create();
						alertDialog.setTitle("Network Error");
						alertDialog.setMessage("There is no network available");
						alertDialog.setButton("Change Network Settings", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent intent=new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
								ComponentName cName=new ComponentName("com.android.phone","com.android.phone.Settings");
								DIALOG_UP=0;
								intent.setComponent(cName);
								context.startActivity(intent);
							}
						});

						alertDialog.setButton2("Work Offline", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								edit=mpref.edit();
								edit.putBoolean("offline", true);
								edit.commit();
								DIALOG_UP=0;
							}
						});
						alertDialog.show();
					}
					else{
						Log.d("111111", "still no net and already in offline mode");
					}
					break;
				case TelephonyManager.DATA_CONNECTED:
					if(DIALOG_UP==1){
						alertDialog.hide();
					}
					if(LOST_CONN==1 ){
						Toast.makeText(context, "Successfully connected to network", Toast.LENGTH_SHORT).show();
						LOST_CONN=0;
						Log.d("FINAL", "set lost_conn=0");
					}
					edit=mpref.edit();
					edit.putBoolean("offline", false);
					edit.commit();
					break;
				case TelephonyManager.DATA_SUSPENDED:
					break;
				}
			}
		};
		myTelephony.listen(callStateListener, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
	}

	public void listenToConn(Context context){
		// not needed. Checknet automatically handles that.
	}

	public static void stopListeningToConn(Context context){
		myTelephony=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		myTelephony.listen(callStateListener, callStateListener.LISTEN_NONE);
	}
}
