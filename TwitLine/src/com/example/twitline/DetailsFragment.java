package com.example.twitline;

import com.example.twitline.cache.ImageLoader;
import com.example.twitline.util.Utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DetailsFragment extends Fragment {
	
	private LinearLayout mParentLayout;
	private ImageView mDetailsUserImage;
	private TextView mDetailsUserName;
	private TextView mDetailsUserScreenName;
	private TextView mDetailsUserStatus;
	private TextView mDetailsDateFormatted;
	
	public static final String KEY_IMAGE_URL 	= "imageurl";
	public static final String KEY_NAME 		= "name";
	public static final String KEY_SCREEN_NAME 	= "screenname";
	public static final String KEY_STATUS 		= "status";
	public static final String KEY_DATE 		= "date";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.details_fragment, container, false);
		
		mParentLayout = (LinearLayout) view.findViewById(R.id.details_fragment);
		mDetailsUserImage = (ImageView) view.findViewById(R.id.details_user_image);
		mDetailsUserName = (TextView) view.findViewById(R.id.details_user_name);
		mDetailsUserScreenName = (TextView) view.findViewById(R.id.details_user_screen_name);
		mDetailsUserStatus = (TextView) view.findViewById(R.id.details_user_status);
		mDetailsDateFormatted = (TextView) view.findViewById(R.id.details_date);
		
		return view;
	}
	
	@Override
	public void onStart() {
		
		Bundle args = getArguments();
		if (args != null) {
			mParentLayout.setVisibility(View.VISIBLE);
			updateView(args);
		}
		
		super.onStart();
	}
	
	public void updateView(Bundle args) {
		String userImageURL = args.getString(KEY_IMAGE_URL, "");
		ImageLoader imageLoader = new ImageLoader(getActivity());
		imageLoader.displayImage(userImageURL, mDetailsUserImage);
		
		String userName = args.getString(KEY_NAME, "");
		mDetailsUserName.setText(userName);
		
		String userScreenName = args.getString(KEY_SCREEN_NAME, "");
		mDetailsUserScreenName.setText('@'+userScreenName);
		
		String userStatus = args.getString(KEY_STATUS, "");
		mDetailsUserStatus.setText(Utils.colorize(userStatus));
		
		String dateFormatted = args.getString(KEY_DATE, "");
		mDetailsDateFormatted.setText(dateFormatted);
	}
}
