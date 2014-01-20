package com.example.twitline.entity;

import java.io.Serializable;

public class TweetStatus implements Serializable {

	private String mImageURL;
	private String mName;
	private String mScreenName;
	private String mStatus;
	private String mDate;
	
	public TweetStatus(
			String mImageURL,
			String mName,
			String mScreenName,
			String mStatus,
			String mDate) {
		this.mImageURL = mImageURL;
		this.mName = mName;
		this.mScreenName = mScreenName;
		this.mStatus = mStatus;
		this.mDate = mDate;
	}

	public String getImageURL() {
		return mImageURL;
	}

	public String getName() {
		return mName;
	}

	public String getScreenName() {
		return mScreenName;
	}

	public String getStatus() {
		return mStatus;
	}

	public String getDate() {
		return mDate;
	}

	@Override
	public String toString() {
		return mName + " | " + mStatus + " | " + mDate;
	}
}
