package com.seven.lock.db;

import android.net.Uri;

public class UriManager {

	private static final String AUTHORITY="com.seven.lock.";
	
	private volatile static UriManager uriManager;
	
	
	private UriManager() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * ����
	 * @return
	 */
	public static UriManager getInstance() {
		if (uriManager == null) {
			synchronized (UriManager.class) {
				if (uriManager == null) {
					uriManager= new UriManager();
				}
			}
		}
		return uriManager;
	}
	
	//���ݿ��� ����
	public Uri getUri(String providerName,String tableName){
		return Uri.parse("content://"+AUTHORITY+providerName+"/"+tableName);	
	}
	
	
}
