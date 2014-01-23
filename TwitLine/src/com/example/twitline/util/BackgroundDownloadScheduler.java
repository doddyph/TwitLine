package com.example.twitline.util;

import com.example.twitline.service.TwitLineService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class BackgroundDownloadScheduler extends BroadcastReceiver {
	
	private AlarmManager alarmManager;
	private PendingIntent pendingIntent;

	@Override
	public void onReceive(Context context, Intent arg1) {
		Intent intent = new Intent(context, TwitLineService.class);
		intent.putExtra("scheduler", true);
		context.startService(intent);
	}
	
	public void set(Context context, long intervalMillis) {
		Intent intent = new Intent(context, BackgroundDownloadScheduler.class);
		pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		
		alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(
				AlarmManager.ELAPSED_REALTIME_WAKEUP, 
				SystemClock.elapsedRealtime()/* + intervalMillis*/, 
				intervalMillis, 
				pendingIntent);
	}
	
	public void cancel(Context context) {
		if (alarmManager != null) {
			alarmManager.cancel(pendingIntent);
		}
	}

}
