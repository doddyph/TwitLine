package com.example.twitline.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private static final int VERSION = 2;
	private static final String name = "twitline.db";
	
	public DBHelper(Context context) {
		super(context, name, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE status ( _id INTEGER AUTO INCREMENT PRIMARY KEY, " +
				" imageurl TEXT," +
				" name TEXT," +
				" screenname TEXT," +
				" status TEXT," +
				" date TEXT)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if (oldVersion == 1) {
			
		}
	}

}
