package com.social;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Config;
import android.util.Log;

public class C2DMessageReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		//String accountName = intent.getExtras().getString(Config.C2DM_ACCOUNT_EXTRA);
	    //String message = intent.getExtras().getString(Config.C2DM_MESSAGE_EXTRA);
		Log.w("C2DM", "Message Receiver called");
		if ("com.google.android.c2dm.intent.RECEIVE".equals(action)) {
			Log.w("C2DM", "Received message");
			final String payload = intent.getStringExtra("payload");
			Log.d("C2DM", "dmControl: payload = " + payload);
			// TODO Send this to my application server to get the real data
			// Lets make something visible to show that we received the message
			createNotification(context, payload);

		}
	}

	public void createNotification(Context context, String payload) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.green,
				"Message received", System.currentTimeMillis());
		// Hide the notification after its selected
		//notification.flags |= Notification.FLAG_AUTO_CANCEL;

		Intent intent = new Intent(context, FullMap.class);
		intent.putExtra("payload", payload);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		notification.setLatestEventInfo(context, "Message from BargeProject",
				payload, pendingIntent);
		Log.d("payload",payload);
		notificationManager.notify(0, notification);

	}


}
