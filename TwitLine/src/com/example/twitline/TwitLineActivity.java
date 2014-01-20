package com.example.twitline;

import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

public class TwitLineActivity extends Activity {
	
	private TimelineFragment mTimelineFragment;
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extras = intent.getExtras();
			boolean success = extras.getBoolean("result");
			
			if (success) {
				Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
				mTimelineFragment.loadStatus();
			}
			
//			String text = extras.getString("result");
//			Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
		
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		mTimelineFragment = new TimelineFragment();
		ft.replace(android.R.id.content, mTimelineFragment);
		ft.commit();
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
	}

}
