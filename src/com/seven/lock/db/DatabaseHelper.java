package com.seven.lock.db;

import java.io.IOException;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * @author lilei
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "apps.db";
	private static final int DATABASE_VERSION = 1;
	private Context context;
	
	public DatabaseHelper(Context context,String tableKey) throws NotFoundException, IOException {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		 db.execSQL("CREATE TABLE "+AppsProvider.TABLE_NAME+" ( _id INTEGER PRIMARY KEY, name TEXT, packageName TEXT, icon BLOB,time INTEGER,show INTEGER,type INTEGER,lock  INTEGER);");
		 db.beginTransaction();        //手动设置开始事务
		 
		/* BufferedReader  buffer = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.map)));
		String s = new String();	
		 try {
			while((s=buffer.readLine())!=null){
					db.execSQL(s);
			}
			buffer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	
		
		 db.setTransactionSuccessful();        //设置事务处理成功，不设置会自动回滚不提交
		 db.endTransaction();        //处理完成 
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
