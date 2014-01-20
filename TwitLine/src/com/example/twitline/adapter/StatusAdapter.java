package com.example.twitline.adapter;

import java.util.ArrayList;

import com.example.twitline.R;
import com.example.twitline.cache.ImageLoader;
import com.example.twitline.entity.TweetStatus;
import com.example.twitline.util.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StatusAdapter extends BaseAdapter {
	
	private ArrayList<TweetStatus> statusList;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;
	
	public StatusAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		imageLoader = new ImageLoader(context);
	}
	
	public StatusAdapter(Context context, ArrayList<TweetStatus> statusList) {
		this(context);
		this.statusList = statusList;
	}
	
	public void setStatusList(ArrayList<TweetStatus> statusList) {
		this.statusList = statusList;
	}
	
	@Override
	public int getCount() {
		return statusList == null? 0:statusList.size();
	}

	@Override
	public Object getItem(int position) {
		return statusList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item, null);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView.findViewById(R.id.user_image);
			holder.nameView = (TextView) convertView.findViewById(R.id.user_name);
			holder.statusView = (TextView) convertView.findViewById(R.id.user_status);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		TweetStatus tweetStatus = statusList.get(position);
		imageLoader.displayImage(tweetStatus.getImageURL(), holder.imageView);
		holder.nameView.setText(tweetStatus.getName());
		holder.statusView.setText(Utils.colorize(tweetStatus.getStatus()));
		
		return convertView;
	}
	
	static class ViewHolder {
		TextView nameView;
		TextView statusView;
		ImageView imageView;
	}
	
}
