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
	private ProgressBar mProgressBar;
	
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
//			startService();
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
		
		switch (TwitLineService.SERVICE_STATE) {
		case TwitLineService.STATE_INIT:
		case TwitLineService.STATE_FAILED:
			startService();
			break;
		}
	}
	
	private void startService() {
		Intent i = new Intent(getActivity(), TwitLineService.class);
		getActivity().startService(i);
		setProgressBarVisibility(View.VISIBLE);
	}
	
	public void setProgressBarVisibility(int visible) {
		mProgressBar.setVisibility(visible);
	}
	
	public void loadStatus() {
		new LoadStatusTask().execute();
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
