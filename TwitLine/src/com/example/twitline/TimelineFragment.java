package com.example.twitline;

import java.util.ArrayList;

import com.example.twitline.adapter.StatusAdapter;
import com.example.twitline.db.TwitLineContentProvider;
import com.example.twitline.entity.TweetStatus;
import com.example.twitline.service.TwitLineService;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

public class TimelineFragment extends Fragment implements OnItemClickListener {
	
	private StatusAdapter mStatusAdapter;
	private ListView mListView;
	private MenuItem mRefreshMenu;
	private ProgressBar mProgressBar;
	
	private static final int STATE_ACTION_BAR 	= 1;
	private static final int STATE_LAYOUT 		= 2;
	private int progressbarstate = STATE_LAYOUT;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		setHasOptionsMenu(true);

		View view = inflater.inflate(R.layout.timeline_fragment, container, false);
		mListView = (ListView) view.findViewById(R.id.listview);
		mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar); 
		
		mListView.setOnItemClickListener(this);
		mStatusAdapter = new StatusAdapter(getActivity());
		mListView.setAdapter(mStatusAdapter);
		
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		loadStatus();
		
		if (mRefreshMenu == null) {
			progressbarstate = STATE_LAYOUT;
		}
		else {
			progressbarstate = STATE_ACTION_BAR;
		}
		
		switch (TwitLineService.SERVICE_STATE) {
		case TwitLineService.STATE_INIT:
		case TwitLineService.STATE_FAILED:
			startService();
			break;
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.timeline, menu);
		setRefreshMenuIdentifier(menu.findItem(R.id.menu_refresh));
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			startService();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void setRefreshMenuIdentifier(MenuItem menuItem) {
		mRefreshMenu = menuItem;
	}
	
	public void showProgressBar() {
		switch (progressbarstate) {
		case STATE_LAYOUT:
			mProgressBar.setVisibility(View.VISIBLE);
			break;
		case STATE_ACTION_BAR:
			if (mRefreshMenu != null) {
				mRefreshMenu.setActionView(R.layout.progressbar);
				mRefreshMenu.expandActionView();
			}
			break;
		}
	}
	
	public void hideProgressBar() {
		switch (progressbarstate) {
		case STATE_LAYOUT:
			mProgressBar.setVisibility(View.GONE);
			break;
		case STATE_ACTION_BAR:
			if (mRefreshMenu != null) {
				mRefreshMenu.collapseActionView();
				mRefreshMenu.setActionView(null);
			}
			break;
		}
		updateProgressBarState();
	}
	
	private void updateProgressBarState() {
		if (mRefreshMenu == null) {
			progressbarstate = STATE_LAYOUT;
		}
		else {
			progressbarstate = STATE_ACTION_BAR;
		}
	}

	private void startService() {
		Intent i = new Intent(getActivity(), TwitLineService.class);
		getActivity().startService(i);
		showProgressBar();
	}
	
	@Override
	public void onItemClick(AdapterView<?> a, View v, int position, long id) {
		
		TwitLineActivity.CURRENT_POS = position;
		TweetStatus item = (TweetStatus) mListView.getItemAtPosition(position);
		
		Bundle args = new Bundle();
		args.putString(DetailsFragment.KEY_IMAGE_URL, item.getImageURL());
		args.putString(DetailsFragment.KEY_NAME, item.getName());
		args.putString(DetailsFragment.KEY_SCREEN_NAME, item.getScreenName());
		args.putString(DetailsFragment.KEY_STATUS, item.getStatus());
		args.putString(DetailsFragment.KEY_DATE, item.getDate());
		TwitLineActivity.DETAIL_ARGS = args;
		
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		DetailsFragment detailsFragment = new DetailsFragment();
		detailsFragment.setArguments(args);
		
		if (getActivity().findViewById(R.id.details_container) != null) {//landscape mode
			ft.replace(R.id.details_container, detailsFragment);
		}
		else {//portrait mode
			ft.replace(R.id.timeline_container, detailsFragment);
			ft.addToBackStack(null);
		}
		
		ft.commit();
	}
	
	public void loadStatus() {
		new LoadStatusTask().execute();
	}
	
	class LoadStatusTask extends AsyncTask<Void, Void, ArrayList<TweetStatus>> {
		
		@Override
		protected ArrayList<TweetStatus> doInBackground(Void... params) {
			
			Cursor cursor = getActivity().getContentResolver().query(
					TwitLineContentProvider.INFO_URI, 
					null, 
					null, 
					null, 
					null);
			
			ArrayList<TweetStatus> statusList = new ArrayList<TweetStatus>();
			
			while (cursor.moveToNext()) {
				TweetStatus tweetStatus = new TweetStatus(
						cursor.getString(cursor.getColumnIndex("imageurl")), 
						cursor.getString(cursor.getColumnIndex("name")), 
						cursor.getString(cursor.getColumnIndex("screenname")), 
						cursor.getString(cursor.getColumnIndex("status")), 
						cursor.getString(cursor.getColumnIndex("date")));
				
				statusList.add(tweetStatus);
			}
			
			return statusList;
		}
		
		@Override
		protected void onPostExecute(ArrayList<TweetStatus> result) {
			
			if (getActivity() != null && result != null) {
				mStatusAdapter.setStatusList(result);
				mStatusAdapter.notifyDataSetChanged();
				mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				mListView.setItemChecked(TwitLineActivity.CURRENT_POS, true);
			}
		}
	}
	
}
