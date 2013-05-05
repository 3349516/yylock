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
			//notifyChange()����������֪ͨע���ڴ�URI�ϵĹ۲��ߣ�observer�����ݷ����˸ı�
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
	 * projection   	Ҫ���ص�columns�б�
	 * selection     	SQL����where�Ӿ�
	 * selectionArgs    selection�Ĳ������������?��?�Ž��ᱻ�������滻
	 * sortOrder      	SQL��ORDER BY�����Ӿ�
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		db= openHelper.getReadableDatabase();										
		Cursor c = db.query(TABLE_NAME, projection, selection, selectionArgs,null , null, sortOrder);
		//setNotificationUri ����ΪCursor����ע��һ���۲����ݱ仯��URI
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
