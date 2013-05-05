package com.seven.lock.obj;

import android.graphics.drawable.Drawable;

public class AppObject {

	public String name ;		//程序名
	public Drawable drawable;	//图标
	public String packageName;	//包名
	public long time;		 //安装时间
	public int show;		 //0显示			   1隐藏 
	public int type;		 //是否  系统应用		 0是系统应用 		 1 非系统应用 
	public int lock;     	 //是否加密 		 0 加密  1不加密
	
}
