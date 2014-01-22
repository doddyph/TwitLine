package com.example.twitline;

import com.example.twitline.service.TwitLineService;

import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class TwitLineActivity extends ActionBarActivity {
	
	private TimelineFragment mTimelineFragment;
	private DetailsFragment mDetailsFragment;
	
	public static int CURRENT_POS = 0;
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
		
		TwitLineService.SERVICE_STATE = TwitLineService.STATE_INIT;
		CURRENT_POS = 0;
		DETAIL_ARGS = null;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			Intent i = new Intent(this, TwitLineService.class);
			startService(i);
			mTimelineFragment.setProgressBarVisibility(View.VISIBLE);
			return true;

		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
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
