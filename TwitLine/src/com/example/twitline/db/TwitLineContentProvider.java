package com.example.twitline.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class TwitLineContentProvider extends ContentProvider {
	
	private DBHelper dbHelper;
	
	private static final String AUTHORITY = "com.example.twitline";
	private static final String INFO_TABLE = "status";
	private static final int INFO_LIST = 1;
	private static final int INFO_DETAIL = 2;
	
	public static Uri INFO_URI = Uri.parse("content://" + AUTHORITY + "/" + INFO_TABLE);
	private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static {
		// content://com.example.twitline/info
		uriMatcher.addURI(AUTHORITY, INFO_TABLE, INFO_LIST);

		// content://com.example.twitline/info/1
		uriMatcher.addURI(AUTHORITY, INFO_TABLE + "/#", INFO_DETAIL);
	}
	
	@Override
	public boolean onCreate() {
		dbHelper = new DBHelper(getContext());
		return true;
	}

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		switch (uriMatcher.match(uri)) {
		case INFO_LIST:
			db.insert(INFO_TABLE, null, values);

			break;

		default:
			break;
		}
		return null;
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		switch (uriMatcher.match(uri)) {
		case INFO_LIST:
			db.beginTransaction();
			for (int i = 0; i < values.length; i++) {
				db.insert(INFO_TABLE, null, values[i]);
			}
			db.setTransactionSuccessful();
			db.endTransaction();
			return values.length;

		default:
			break;
		}
		
		return 0;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
		String param = null;
		String[] args = null;
		
		switch (uriMatcher.match(uri)) {
		case INFO_LIST:
			sqLiteQueryBuilder.setTables(INFO_TABLE);
			break;
		case INFO_DETAIL:
			param = "id = ?";
			String id = uri.getLastPathSegment();
			args = new String[]{ id };
			sqLiteQueryBuilder.setTables(INFO_TABLE);
			break;

		default:
			break;
		}
		
		return sqLiteQueryBuilder
				.query(db, null, param, args, null, null, null);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}
	
}
