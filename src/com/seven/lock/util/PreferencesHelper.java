package com.seven.lock.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * ��ѡ���ļ����������
 * @author ll
 *
 */
public class PreferencesHelper {
	
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	
	public PreferencesHelper(Context c){
		sp = c.getSharedPreferences("lock", 0);		//lock �ļ���
		editor=sp.edit();
	}
	
	public void setValue(String key,String value){
		editor=sp.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	
	public String getValue(String key){
		return sp.getString(key, "");
	}
	
	public void setValue(String key,boolean value){
		editor=sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public boolean getBoolean(String key){
		return sp.getBoolean(key, false);
	}

}
