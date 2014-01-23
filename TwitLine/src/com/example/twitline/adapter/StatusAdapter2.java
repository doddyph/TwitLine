package com.example.twitline.adapter;

import com.example.twitline.R;
import com.example.twitline.cache.ImageLoader;
import com.example.twitline.util.Utils;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class StatusAdapter2 extends CursorAdapter {
	
	private LayoutInflater mLayoutInflater;
	private ImageLoader imageLoader;

	public StatusAdapter2(Context context) {
		super(context, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
//		mLayoutInflater = LayoutInflater.from(context);
		mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(context);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		
		String imageurl = cursor.getString(cursor.getColumnIndex("imageurl"));
		imageLoader.displayImage(imageurl, holder.imageView);
		
		String name = cursor.getString(cursor.getColumnIndex("name"));
		holder.nameView.setText(name);
		
		String status = cursor.getString(cursor.getColumnIndex("status"));
		holder.statusView.setText(Utils.colorize(status));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mLayoutInflater.inflate(R.layout.list_item, null);
		
		ViewHolder holder = new ViewHolder();
		holder.imageView = (ImageView) view.findViewById(R.id.user_image);
		holder.nameView = (TextView) view.findViewById(R.id.user_name);
		holder.statusView = (TextView) view.findViewById(R.id.user_status);
		view.setTag(holder);
		
		return view;
	}

	static class ViewHolder {
		TextView nameView;
		TextView statusView;
		ImageView imageView;
	}
	
}
