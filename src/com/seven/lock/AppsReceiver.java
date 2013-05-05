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
		//���չ㲥��ϵͳ������ɺ����г���
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
        	PreferencesHelper helper = new PreferencesHelper(context);
        	boolean showTag = helper.getBoolean(LockEncryActivity.IsCloseSHOW);
    		if (!showTag){
        	//��������
    			context.startService(new Intent(context, ActvityInterceptService.class));
    		}
    		showTag = helper.getBoolean(SettingActivity.LOCK_SCREEN_ON_OFF);
    		if(showTag){
    			//��������
        		context.startService(new Intent(context, LockScreenService.class));
    		}
    		
        }
        //���չ㲥���豸���°�װ��һ��Ӧ�ó�������Զ������°�װӦ�ó���
        else if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
        	//��ȡ��װ����  package:com.seven.lock
            String packageName = intent.getDataString().substring(8);
            //ͨ�������ҵ�Ӧ�ó�����Ϣ
            AppObject appObj =  appsManager.getInstalledAppInfo(packageName);
            if(appObj!=null){
            	  ContentValues cv =  ObjConverUtil.AppObject2ContentValues(appObj);
                  context.getContentResolver().insert(AppsProvider.DBProvider, cv);
            }
        }
        //���չ㲥���豸��ɾ����һ��Ӧ�ó������
        else if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
        	   String packageName = intent.getDataString().substring(8);
        	   //context.getContentResolver().delete(AppsProvider.DBProvider, "packageName="+packageName,null );
        	   
        	//Ԥ����
            context.getContentResolver().delete(AppsProvider.DBProvider, "packageName=?", new String[]{packageName});

       	 	//���͹㲥,֪ͨ�б�ɾ��
            Intent delIntent = new Intent(RefreshReceiver.RECEIVER);
        	delIntent.putExtra("packageName", packageName);
        	delIntent.putExtra("type", -1);
        	context.sendBroadcast(delIntent);
                
           
        }

	}

}
