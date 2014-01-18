package com.example.twitline;

import java.util.ArrayList;
import java.util.List;

import com.example.twitline.adapter.StatusAdapter;
import com.example.twitline.db.TwitLineContentProvider;
import com.example.twitline.entity.TweetStatus;

import android.app.Fragment;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class TimelineFragment extends Fragment {
	
	private StatusAdapter mStatusAdapter;
	private ListView mListView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
//		setHasOptionsMenu(true);

		View view = inflater.inflate(R.layout.timeline_fragment, container, false);
		mListView = (ListView) view.findViewById(R.id.listview);
		
		mStatusAdapter = new StatusAdapter(getActivity(), new ArrayList<TweetStatus>());
		mListView.setAdapter(mStatusAdapter);
		
		return view;
	}
	
//	@Override
//	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		super.onCreateOptionsMenu(menu, inflater);
//		inflater.inflate(R.menu.main, menu);
//	}
	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		
//		switch (item.getItemId()) {
//		case R.id.menu_refresh:
//			Intent i = new Intent(getActivity(), TwitLineService.class);
//			getActivity().startService(i);
//			return true;
//
//		default:
//			break;
//		}
//		
//		return super.onOptionsItemSelected(item);
//	}
	
	@Override
	public void onResume() {
		super.onResume();
		loadStatus();
	}
	
	public void loadStatus() {
		new LoadStatusTask().execute();
	}
	
	class LoadStatusTask extends AsyncTask<Void, Void, List<TweetStatus>> {
		
		@Override
		protected List<TweetStatus> doInBackground(Void... params) {
			
			Cursor cursor = getActivity().getContentResolver().query(
					TwitLineContentProvider.INFO_URI, 
					null, 
					null, 
					null, 
					null);
			
			List<TweetStatus> statusList = new ArrayList<TweetStatus>();
			
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
		protected void onPostExecute(List<TweetStatus> result) {
			
			if (getActivity() != null && result != null) {
				mStatusAdapter.setStatusList(result);
				mStatusAdapter.notifyDataSetChanged();
			}
		}
	}
	
}
