package com.example.twitline.service;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import com.example.twitline.R;
import com.example.twitline.entity.TweetStatus;
import com.example.twitline.util.Utils;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class TwitLineService extends IntentService {

	public TwitLineService() {
		super("TwitLine Service");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		new LoadTweetTask().execute();
	}

	class LoadTweetTask extends AsyncTask<Void, Void, List<TweetStatus>> {
		
		@Override
		protected List<TweetStatus> doInBackground(Void... params) {
			
			List<TweetStatus> tweetStatusList = new ArrayList<TweetStatus>();
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
					
					tweetStatusList.add(tweetStatus);
				}
			} catch (Exception e) {
				tweetStatus = new TweetStatus(
						"", "", "", 
						"Twitter query failed: "+e.getMessage(), 
						"");
				
				tweetStatusList.add(tweetStatus);
			}
			
			return tweetStatusList;
		}
		
		@Override
		protected void onPostExecute(List<TweetStatus> result) {
			boolean success = false;
			
			if (result != null) {
				ContentValues[] values = new ContentValues[result.size()];
				int i = 0;
				
				for (TweetStatus tweetStatus : result) {
					values[i] = generateTweetStatus(tweetStatus);
					i++;
				}
				
//				getContentResolver().bulkInsert(url, values);
				success = true;
			}
			
			if (success) {
				sendBroadcast(new Intent("TwitLine").putExtra("result", "success"));
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
		
		private ContentValues generateTweetStatus(TweetStatus tweetStatus) {
			ContentValues values = new ContentValues();
			
			String imageURL = tweetStatus.getImageURL();
			Log.v("generateTweetStatus()", "imageURL: " + imageURL);
			values.put("imageurl", imageURL);
			
			String name = tweetStatus.getName();
			Log.v("generateTweetStatus()", "name: " + name);
			values.put("name", name);
			
			String screenName = tweetStatus.getScreenName();
			Log.v("generateTweetStatus()", "screenName: " + screenName);
			values.put("screenname", screenName);
			
			String status = tweetStatus.getStatus();
			Log.v("generateTweetStatus()", "status: " + status);
			values.put("status", status);
			
			String date = tweetStatus.getDate();
			Log.v("generateTweetStatus()", "date: " + date);
			values.put("date", date);
			
			return values;
		}
	}
}
