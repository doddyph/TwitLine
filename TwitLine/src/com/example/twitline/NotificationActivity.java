package com.example.twitline;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class NotificationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification);
		
		Bundle extras = getIntent().getExtras();
		
		TextView contentTitle = (TextView) findViewById(R.id.content_title);
		String text = extras.getString("title");
		contentTitle.setText(text);
		
		TextView contentText = (TextView) findViewById(R.id.content_text);
		text = extras.getString("text");
		contentText.setText(text);
	}
}
