package com.seven.lock.util;

import android.content.ContentValues;

import com.seven.lock.obj.AppObject;

/**
 * ����ת������
 * @author ll
 *
 */
public class ObjConverUtil {

	public static ContentValues  AppObject2ContentValues (AppObject appObj){
		ContentValues cv = new ContentValues();
		cv.put("name", appObj.name); // ��������
		cv.put("packageName", appObj.packageName); // ����
		cv.put("icon", IconBitmapUtil.getBytes(appObj.drawable)); // ͼ��
		cv.put("time", appObj.time); // ͼ��
		cv.put("show", appObj.show); 				// ����
		cv.put("type", appObj.type);			//�Ƿ�ϵͳӦ��
		cv.put("lock", appObj.lock);			//�Ƿ����
		return cv;
	}
	
}
