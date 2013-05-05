package com.seven.lock.util;

import android.content.ContentValues;

import com.seven.lock.obj.AppObject;

/**
 * 对象转换工具
 * @author ll
 *
 */
public class ObjConverUtil {

	public static ContentValues  AppObject2ContentValues (AppObject appObj){
		ContentValues cv = new ContentValues();
		cv.put("name", appObj.name); // 程序名称
		cv.put("packageName", appObj.packageName); // 包名
		cv.put("icon", IconBitmapUtil.getBytes(appObj.drawable)); // 图标
		cv.put("time", appObj.time); // 图标
		cv.put("show", appObj.show); 				// 隐藏
		cv.put("type", appObj.type);			//是否系统应用
		cv.put("lock", appObj.lock);			//是否加密
		return cv;
	}
	
}
