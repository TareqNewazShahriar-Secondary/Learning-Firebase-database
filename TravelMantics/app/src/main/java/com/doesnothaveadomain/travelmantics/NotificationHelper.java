package com.doesnothaveadomain.travelmantics;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

public class NotificationHelper
{
	String tittle = "";
	String body = "";
	Activity activityContext = null;
	
	public NotificationHelper(Activity activityContext, String title, String body)
	{
		this.tittle = title;
		this.body = body;
		this.activityContext = activityContext;
	}
	
	public void Show()
	{
		NotificationManager notificationManager =
				(NotificationManager) activityContext.getSystemService(Context.NOTIFICATION_SERVICE);
		
		String channelId = "_default_id_";
		CharSequence channelName = "Default";
		
		Notification.Builder notificationBuilderObj = new Notification.Builder(activityContext.getApplicationContext())
				.setContentTitle(tittle)
				.setContentText(body)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setAutoCancel(true);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
		{
			NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
			notificationChannel.enableLights(true);
			notificationChannel.setLightColor(Color.BLUE);
			notificationChannel.enableVibration(true);
			notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
			
			notificationManager.createNotificationChannel(notificationChannel);
			notificationBuilderObj.setChannelId(channelId);
		}
		
		Notification notificationObj = notificationBuilderObj.build();
		notificationObj.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0, notificationObj);
	}
}
