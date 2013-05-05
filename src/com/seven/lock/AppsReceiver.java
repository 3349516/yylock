package com.seven.lock;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import com.seven.lock.db.AppsProvider;
import com.seven.lock.obj.AppObject;
import com.seven.lock.util.AppsManager;
import com.seven.lock.util.ObjConverUtil;
import com.seven.lock.util.PreferencesHelper;

public class AppsReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		AppsManager appsManager = new AppsManager(context);
		//接收广播：系统启动完成后运行程序
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
        	PreferencesHelper helper = new PreferencesHelper(context);
        	boolean showTag = helper.getBoolean(LockEncryActivity.IsCloseSHOW);
    		if (!showTag){
        	//开启拦截
    			context.startService(new Intent(context, ActvityInterceptService.class));
    		}
    		showTag = helper.getBoolean(SettingActivity.LOCK_SCREEN_ON_OFF);
    		if(showTag){
    			//开启锁屏
        		context.startService(new Intent(context, LockScreenService.class));
    		}
    		
        }
        //接收广播：设备上新安装了一个应用程序包后自动启动新安装应用程序。
        else if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
        	//获取安装包名  package:com.seven.lock
            String packageName = intent.getDataString().substring(8);
            //通过包名找到应用程序信息
            AppObject appObj =  appsManager.getInstalledAppInfo(packageName);
            if(appObj!=null){
            	  ContentValues cv =  ObjConverUtil.AppObject2ContentValues(appObj);
                  context.getContentResolver().insert(AppsProvider.DBProvider, cv);
            }
        }
        //接收广播：设备上删除了一个应用程序包。
        else if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
        	   String packageName = intent.getDataString().substring(8);
        	   //context.getContentResolver().delete(AppsProvider.DBProvider, "packageName="+packageName,null );
        	   
        	//预编译
            context.getContentResolver().delete(AppsProvider.DBProvider, "packageName=?", new String[]{packageName});

       	 	//发送广播,通知列表删除
            Intent delIntent = new Intent(RefreshReceiver.RECEIVER);
        	delIntent.putExtra("packageName", packageName);
        	delIntent.putExtra("type", -1);
        	context.sendBroadcast(delIntent);
                
           
        }

	}

}
