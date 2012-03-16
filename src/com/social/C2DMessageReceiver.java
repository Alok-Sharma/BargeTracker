package com.social;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class C2DMessageReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if ("com.google.android.c2dm.intent.RECEIVE".equals(action)) {
			Log.w("C2DM", "Received message");
			final String payload = intent.getStringExtra("payload");
			Log.d("C2DM", "dmControl: payload = " + payload);
			createNotification(context, payload);

		}
	}

	public void createNotification(Context context, String payload) {
		NotificationManager notificationManager = (NotificationManager) context
		.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.green,
				"Message received", System.currentTimeMillis());

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
