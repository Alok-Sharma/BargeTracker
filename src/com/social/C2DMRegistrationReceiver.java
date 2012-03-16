package com.social;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;

public class C2DMRegistrationReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.w("C2DM", "Registration Receiver called");
		if ("com.google.android.c2dm.intent.REGISTRATION".equals(action)) {
			Log.w("C2DM", "Received registration ID");
			final String registrationId = intent
			.getStringExtra("registration_id");
			String error = intent.getStringExtra("error");

			Log.d("C2DM", "dmControl: registrationId = " + registrationId
					+ ", error = " + error);
			String deviceId = Secure.getString(context.getContentResolver(),
					Secure.ANDROID_ID);
			createNotification(context, registrationId);
			//sendRegistrationIdToServer(deviceId, registrationId);
			// Also save it in the preference to be able to show it later
			saveRegistrationId(context, registrationId);
		}
	}

	private void saveRegistrationId(Context context, String registrationId) {
		SharedPreferences prefs = PreferenceManager
		.getDefaultSharedPreferences(context);
		Editor edit = prefs.edit();
	}

	public void createNotification(Context context, String registrationId) {
		NotificationManager notificationManager = (NotificationManager) context
		.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.help,
				"Registration successful "+registrationId, System.currentTimeMillis());
		// Hide the notification after its selected
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
	}


}
