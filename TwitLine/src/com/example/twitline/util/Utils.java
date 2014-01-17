package com.example.twitline.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

	public static String getFormattedDate(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm, MMM dd, yyyy");
		return dateFormat.format(date);
	}
}
