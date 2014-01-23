package com.example.twitline;

import com.example.twitline.service.TwitLineService;
import com.example.twitline.util.BackgroundDownloadScheduler;

import android.os.Bundle;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class TwitLineActivity extends ActionBarActivity {
	
	private TimelineFragment mTimelineFragment;
	private DetailsFragment mDetailsFragment;
	private BackgroundDownloadScheduler downloadScheduler;
	
	public static int CURRENT_POS = 0;
	public static Bundle DETAIL_ARGS = null;
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.v("BroadcastReceiver", "onReceive()");
			Bundle extras = intent.getExtras();
			
			if (extras.containsKey("result")) {
				boolean result = extras.getBoolean("result");
				if (result) {
					mTimelineFragment.hideProgressBar();
					mTimelineFragment.loadStatus();
				}
			}
			else if (extras.containsKey("scheduler")) {
				Log.v("BroadcastReceiver.onReceive()", "Updated new timeline from scheduler");
//				boolean scheduler = extras.getBoolean("scheduler");
//				if (scheduler) {
//					createNotification(0, "TwitLine Notification", "Updated timeline.");
//				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView();
		downloadScheduler = new BackgroundDownloadScheduler();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(receiver, new IntentFilter("TwitLine"));
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(receiver);
		
		TwitLineService.SERVICE_STATE = TwitLineService.STATE_INIT;
		CURRENT_POS = 0;
		DETAIL_ARGS = null;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_about:
			// TODO
			createNotification(0, "TwitLine Notification", "About");
//			Toast.makeText(getApplicationContext(), "About", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menu_set_schedule:
			// TODO
			downloadScheduler.set(getApplicationContext(), 1000 * 60);//1 minute
//			Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menu_cancel_schedule:
			downloadScheduler.cancel(getApplicationContext());
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void createNotification(int id, String title, String text) {
		
		Bundle extras = new Bundle();
		extras.putString("title", title);
		extras.putString("text", text);
		
		Intent notifIntent = new Intent(this, NotificationActivity.class);
		notifIntent.putExtras(extras);
		notifIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
				Intent.FLAG_ACTIVITY_CLEAR_TASK);
		
		PendingIntent pendingIntent = PendingIntent.getActivity(
				this, 
				0, 
				notifIntent, 
				PendingIntent.FLAG_UPDATE_CURRENT);
		
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
				.setContentIntent(pendingIntent)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(title)
				.setContentText(text)
				.setAutoCancel(true);
		
		NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notifManager.notify(id, builder.build());
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView();
	}
	
	private void setContentView() {
		setContentView(R.layout.activity_main);
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		if (fm.getBackStackEntryCount() > 0) {
			fm.popBackStack();
		}
		
		if (findViewById(R.id.details_container) != null) {//landscape mode
			mTimelineFragment = new TimelineFragment();
			ft.replace(R.id.timeline_container, mTimelineFragment);
			
			mDetailsFragment = new DetailsFragment();
			if (DETAIL_ARGS != null) {
				mDetailsFragment.setArguments(DETAIL_ARGS);
			}
			ft.replace(R.id.details_container, mDetailsFragment);
		}
		else {//portrait mode
			mTimelineFragment = new TimelineFragment();
			ft.replace(R.id.timeline_container, mTimelineFragment);
		}
		
		ft.commit();
	}
	
}
