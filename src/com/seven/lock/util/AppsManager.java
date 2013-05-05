package com.seven.lock.util;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;

import com.seven.lock.db.AppsProvider;
import com.seven.lock.obj.AppObject;

public class AppsManager {
	
	private Context context;
	
	public AppsManager(Context context) {
		super();
		this.context = context;
	}

	/**
	 * ͨ��������ȡ��װӦ�ó�����Ϣ
	 * @param context
	 * @param name  ����
	 * @return
	 */
	public  AppObject getInstalledAppInfo(String name) {
		List<PackageInfo> packages = context.getPackageManager()
				.getInstalledPackages(0);
		PackageManager pm = context.getPackageManager();
		AppObject appObj = null ;
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo pInfo = packages.get(i);
			if(name.equals(pInfo.packageName)){
				appObj = new AppObject();
				appObj.name = pInfo.applicationInfo.loadLabel(pm).toString();
				appObj.packageName  = pInfo.packageName;
				appObj.drawable = pInfo.applicationInfo.loadIcon(pm);
				appObj.time = System.currentTimeMillis();
				appObj.show = 0;		//��ʾ
				appObj.lock = 1;		//������
				  // ��ϵͳ���������ϵͳ�������  
	            if (isSystemApp(pInfo)) {  
	            	appObj.type = 0;
	            } else {  
	            	appObj.type = 1;
	            }  
				 return appObj;
			}
		}
		return appObj;
	}
	
	/**
	 * ��ȡ���еİ�װӦ�ó��� 
	 * @param context
	 * @return
	 */
	public  ArrayList<AppObject> getInstallApps(){
		// ��ȡ�Ѿ���װ�����б�
		PackageManager pm = context.getPackageManager();
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER); // LAUNCHER ���ڵĳ���
		List<ResolveInfo> list = pm.queryIntentActivities(mainIntent, 0);
		ArrayList<AppObject> apps = new ArrayList<AppObject>();
		AppObject appObj ;
		for (int i = 0; i < list.size(); i++) {
			ResolveInfo rInfo = list.get(i);
			appObj = new AppObject();
			appObj.name = rInfo.loadLabel(pm).toString();
			appObj.drawable = rInfo.loadIcon(pm);
			appObj.packageName = rInfo.activityInfo.packageName;
			appObj.drawable = rInfo.activityInfo.loadIcon(pm);
			appObj.time = System.currentTimeMillis();
			appObj.show = 0;		//��ʾ
			appObj.lock = 1;		//������
			  // ��ϵͳ���������ϵͳ�������  
            if (isSystemApp(rInfo)) {  
            	appObj.type = 0;
            } else {  
            	appObj.type = 1;
            }  
			apps.add(appObj);
		}
		return apps;
	}
	
	/**
	 * ��ѯ���ݿ��а�װ�б�
	 * @param context
	 * @return
	 */
	public ArrayList<AppObject>  query(int show){
		ArrayList<AppObject> apps = new ArrayList<AppObject>();
		Cursor mCursor= null;
		 try {
			mCursor = context.getContentResolver().query(AppsProvider.DBProvider, null, "show = "+show, null, "time desc");
			if(mCursor!=null){
				AppObject appObj ;
				while (mCursor.moveToNext()) {
					appObj = new AppObject();
					appObj.name =  mCursor.getString(1);
					appObj.packageName =  mCursor.getString(2);
					appObj.drawable = IconBitmapUtil.getDrawable(mCursor.getBlob(3));
					appObj.time = mCursor.getLong(4);
					appObj.show = mCursor.getInt(5);
					appObj.type = mCursor.getInt(6);
					appObj.lock = mCursor.getInt(7);
					apps.add(appObj);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(mCursor!=null){
				mCursor.close();
			}
		}
		
		return apps;
		
	}
	
	 /** 
     * �Ƿ���ϵͳ���������ϵͳ����ĸ������ 
     * @return  �����ϵͳ�����򷵻�true 
     */  
    public boolean isSystemApp(PackageInfo pInfo) {  
    	boolean flag = false;
    	 if((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0){	//ϵͳӦ��
    		 flag = true;
    	 }
    	 return flag;
    }  
    
    public boolean isSystemApp(ResolveInfo rInfo) {  
    	boolean flag = false;
    	 if((rInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0){	//ϵͳӦ��
    		 flag = true;
    	 }
    	 return flag;
    }
    
	/**
	 * ��ѯ���ݿ��� �����б�
	 * @param context
	 * @return
	 */
	public String[]  queryIntercept(){
		ArrayList<String> arr = new ArrayList<String>();
		Cursor mCursor= null;
		 try {
			mCursor = context.getContentResolver().query(AppsProvider.DBProvider,new String[]{"packageName"}, "show = 0 and lock = 0", null, "time desc");
			if(mCursor!=null){
				while (mCursor.moveToNext()) {
					arr.add(mCursor.getString(0));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(mCursor!=null){
				mCursor.close();
			}
		}
		return   arr.toArray(new String[arr.size()]);
		
	}
	
	// ��ʼ�����ݿⰲװ�б�
	public void  initDBApps() {
		ArrayList<AppObject> list = getInstallApps();
		// ������ӳ��򣬲�������Ϣ����ListView�б�
		AppObject appObj = null;
		for (int i = 0; i < list.size(); i++) {
			appObj = list.get(i);
			ContentValues cv  = ObjConverUtil.AppObject2ContentValues(appObj);
			context.getContentResolver().insert(AppsProvider.DBProvider, cv);
		}
	}
	
	// �������ݿⰲװ�б�
	public   void  resetDBApps() {
		context.getContentResolver().delete(AppsProvider.DBProvider, null, null);
		initDBApps();
		 
	}
	
	public void startIntentByPackageName(String packageName){
		//ͨ��������Ӧ�ó���
		PackageManager packageManager = context.getPackageManager();
		Intent intent=new Intent();
	    intent = packageManager.getLaunchIntentForPackage(packageName); 
	    context.startActivity(intent);
		
	}

}
