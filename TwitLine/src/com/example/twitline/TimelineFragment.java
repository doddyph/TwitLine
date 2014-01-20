package com.example.twitline;

import java.util.ArrayList;

import com.example.twitline.adapter.StatusAdapter;
import com.example.twitline.db.TwitLineContentProvider;
import com.example.twitline.entity.TweetStatus;
import com.example.twitline.service.TwitLineService;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class TimelineFragment extends Fragment {
	
	private StatusAdapter mStatusAdapter;
	private ListView mListView;
	private ArrayList<TweetStatus> statusList;
	
	private static final String KEY = "statuslist";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.v("TimelineFragment", "onCreate() savedInstanceState: "+savedInstanceState);
		if (savedInstanceState != null) {
			statusList = (ArrayList<TweetStatus>) savedInstanceState.getSerializable(KEY);
		}
		else {
			statusList = new ArrayList<TweetStatus>();
		}
		Log.v("TimelineFragment", "onCreate() statusList size: "+statusList.size());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		setHasOptionsMenu(true);

		View view = inflater.inflate(R.layout.timeline_fragment, container, false);
		mListView = (ListView) view.findViewById(R.id.listview);
		
		Log.v("TimelineFragment", "onCreateView() statusList size: "+statusList.size());
		mStatusAdapter = new StatusAdapter(getActivity(), statusList);
		mListView.setAdapter(mStatusAdapter);
		
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.main, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			Intent i = new Intent(getActivity(), TwitLineService.class);
			getActivity().startService(i);
			return true;

		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.v("TimelineFragment", "onSaveInstanceState() statusList size: "+statusList.size());
		outState.putSerializable(KEY, statusList);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		Log.v("TimelineFragment", "onResume() statusList size: "+statusList.size());
		if (statusList.size() == 0) {
			// TODO
			loadStatus();
			Intent i = new Intent(getActivity(), TwitLineService.class);
			getActivity().startService(i);
		}
	}
	
	public void loadStatus() {
		Log.v("TimelineFragment", "loadStatus()");
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
				Log.v("LoadStatusTask", "onPostExecute() result size: "+result.size());
				statusList = result;
				Log.v("LoadStatusTask", "onPostExecute() statusList size: "+statusList.size());
				mStatusAdapter.setStatusList(result);
				mStatusAdapter.notifyDataSetChanged();
			}
		}
	}
	
}
