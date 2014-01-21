package com.example.twitline;

import com.example.twitline.service.TwitLineService;

import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.view.View;

public class TwitLineActivity extends Activity {
	
	private TimelineFragment mTimelineFragment;
	private DetailsFragment mDetailsFragment;
	
	public static int TWITLINE_POS = 0;
	public static Bundle DETAIL_ARGS = null;
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extras = intent.getExtras();
			boolean success = extras.getBoolean("result");
			
			if (success) {
				mTimelineFragment.setProgressBarVisibility(View.GONE);
				mTimelineFragment.loadStatus();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView();
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
		
		TwitLineService.SERVIVE_STATE = TwitLineService.STATE_INIT;
		TWITLINE_POS = 0;
		DETAIL_ARGS = null;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView();
	}
	
	private void setContentView() {
		setContentView(R.layout.activity_main);
		
		FragmentManager fm = getFragmentManager();
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
