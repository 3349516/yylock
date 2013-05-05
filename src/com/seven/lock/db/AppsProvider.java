package com.seven.lock.db;

import java.io.IOException;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class AppsProvider extends ContentProvider {

	
	public static final String TABLE_NAME = "apps";
	private static final String PROVIDERNAME="AppsProvider";
	private DatabaseHelper openHelper;
	private SQLiteDatabase db ;
	
	public static final Uri DBProvider = UriManager.getInstance().getUri(PROVIDERNAME, TABLE_NAME);
	
	@Override
	public boolean onCreate() {
		try {
			openHelper = new DatabaseHelper(this.getContext(),TABLE_NAME);
			return true;
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	

	@Override
	public String getType(Uri uri) {
		 return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Uri insertUri = null;
		db= openHelper.getReadableDatabase();
		long rowId =db.insert(TABLE_NAME, null, values);
		if (rowId > 0) {
			//   Uri newUri = Uri.withAppendedPath(CONTENT_URI, ""+rowId);
			insertUri= ContentUris.withAppendedId(uri,rowId);
			//notifyChange()方法则用来通知注册在此URI上的观察者（observer）数据发生了改变
			getContext().getContentResolver().notifyChange(insertUri, null); 
		
		}
		return insertUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		db= openHelper.getReadableDatabase();
		int id= db.delete(TABLE_NAME, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return id;
	}

	/**
	 * projection   	要返回的columns列表
	 * selection     	SQL语句的where子句
	 * selectionArgs    selection的参数，如果包含?，?号将会被参数所替换
	 * sortOrder      	SQL的ORDER BY排序子句
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		db= openHelper.getReadableDatabase();										
		Cursor c = db.query(TABLE_NAME, projection, selection, selectionArgs,null , null, sortOrder);
		//setNotificationUri 用来为Cursor对象注册一个观察数据变化的URI
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		db= openHelper.getReadableDatabase();
		int updateCount = db.update(TABLE_NAME, values, selection, selectionArgs);
	    getContext().getContentResolver().notifyChange(uri, null);
		return updateCount;
	}

}
