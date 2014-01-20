package com.example.twitline.service;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import com.example.twitline.R;
import com.example.twitline.db.TwitLineContentProvider;
import com.example.twitline.entity.TweetStatus;
import com.example.twitline.util.Utils;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;

public class TwitLineService extends IntentService {

	public TwitLineService() {
		super("TwitLine Service");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		getContentResolver().delete(TwitLineContentProvider.INFO_URI, null, null);
		new LoadTweetTask().execute();
	}

	class LoadTweetTask extends AsyncTask<Void, Void, List<TweetStatus>> {
		
		@Override
		protected List<TweetStatus> doInBackground(Void... params) {
			
			List<TweetStatus> statusList = new ArrayList<TweetStatus>();
			TweetStatus tweetStatus = null;
			
			try {
				Twitter mTwitter = getTwitter();
				List<twitter4j.Status> statuses  = mTwitter.getHomeTimeline();
				
				for (twitter4j.Status status : statuses) {
					tweetStatus = new TweetStatus(
							status.getUser().getProfileImageURL(), 
							status.getUser().getName(), 
							status.getUser().getScreenName(), 
							status.getText(), 
							Utils.getFormattedDate(status.getCreatedAt()));
					
					statusList.add(tweetStatus);
				}
			} catch (Exception e) {
				tweetStatus = new TweetStatus(
						"", "", "", 
						"Twitter query failed: "+e.getMessage(), 
						"");
				
				statusList.add(tweetStatus);
			}
			
			return statusList;
		}
		
		@Override
		protected void onPostExecute(List<TweetStatus> result) {
			boolean success = false;
			
			if (result != null) {
				ContentValues[] values = new ContentValues[result.size()];
				int i = 0;
				
				for (TweetStatus tweetStatus : result) {
					values[i] = generateStatus(tweetStatus);
					i++;
				}
				
				getContentResolver().bulkInsert(TwitLineContentProvider.INFO_URI, values);
				success = true;
			}
			
			if (success) {
				sendBroadcast(new Intent("TwitLine").putExtra("result", true));
			}
		}
		
		private Twitter getTwitter() {
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(getString(R.string.twitter_consumer_key));
			builder.setOAuthConsumerSecret(getString(R.string.twitter_consumer_secret));
			builder.setOAuthAccessToken(getString(R.string.twitter_access_token));
			builder.setOAuthAccessTokenSecret(getString(R.string.twitter_access_token_secret));
			
			Configuration conf = builder.build();
			TwitterFactory factory = new TwitterFactory(conf);
			Twitter twitter = factory.getInstance();
			
			return twitter;
		}
		
		private ContentValues generateStatus(TweetStatus tweetStatus) {
			ContentValues values = new ContentValues();
			
			String imageURL = tweetStatus.getImageURL();
			values.put("imageurl", imageURL);
			
			String name = tweetStatus.getName();
			values.put("name", name);
			
			String screenName = tweetStatus.getScreenName();
			values.put("screenname", screenName);
			
			String status = tweetStatus.getStatus();
			values.put("status", status);
			
			String date = tweetStatus.getDate();
			values.put("date", date);
			
			return values;
		}
	}
}
