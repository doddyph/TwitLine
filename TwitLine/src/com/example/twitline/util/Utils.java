package com.example.twitline.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

public class Utils {
	
	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		
		try {
			byte[] bytes = new byte[buffer_size];
			
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	public static String getFormattedDate(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm, MMM dd, yyyy");
		return dateFormat.format(date);
	}
	
	/*
	 * http://www.joelifernandes.com/android/find-color-hashtags-in-textview/
	 */
	public static SpannableString colorize(String text) {
		SpannableString spannableString = new SpannableString(text);
		Matcher matcher = Pattern.compile("#([A-Za-z0-9_-]+)").matcher(spannableString);
		
		while (matcher.find()) {
			spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), matcher.start(), matcher.end(), 0);
		}
		
		return spannableString;
	}
	
}
