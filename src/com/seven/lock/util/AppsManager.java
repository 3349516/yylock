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
	 * 通过包名获取安装应用程序信息
	 * @param context
	 * @param name  包名
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
				appObj.show = 0;		//显示
				appObj.lock = 1;		//不加密
				  // 是系统软件或者是系统软件更新  
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
	 * 获取所有的安装应用程序 
	 * @param context
	 * @return
	 */
	public  ArrayList<AppObject> getInstallApps(){
		// 获取已经安装程序列表
		PackageManager pm = context.getPackageManager();
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER); // LAUNCHER 存在的程序
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
			appObj.show = 0;		//显示
			appObj.lock = 1;		//不加密
			  // 是系统软件或者是系统软件更新  
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
	 * 查询数据库中安装列表
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
     * 是否是系统软件或者是系统软件的更新软件 
     * @return  如果是系统程序则返回true 
     */  
    public boolean isSystemApp(PackageInfo pInfo) {  
    	boolean flag = false;
    	 if((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0){	//系统应用
    		 flag = true;
    	 }
    	 return flag;
    }  
    
    public boolean isSystemApp(ResolveInfo rInfo) {  
    	boolean flag = false;
    	 if((rInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0){	//系统应用
    		 flag = true;
    	 }
    	 return flag;
    }
    
	/**
	 * 查询数据库中 拦截列表
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
	
	// 初始化数据库安装列表
	public void  initDBApps() {
		ArrayList<AppObject> list = getInstallApps();
		// 逐项添加程序，并发送消息更新ListView列表。
		AppObject appObj = null;
		for (int i = 0; i < list.size(); i++) {
			appObj = list.get(i);
			ContentValues cv  = ObjConverUtil.AppObject2ContentValues(appObj);
			context.getContentResolver().insert(AppsProvider.DBProvider, cv);
		}
	}
	
	// 重置数据库安装列表
	public   void  resetDBApps() {
		context.getContentResolver().delete(AppsProvider.DBProvider, null, null);
		initDBApps();
		 
	}
	
	public void startIntentByPackageName(String packageName){
		//通过包名打开应用程序
		PackageManager packageManager = context.getPackageManager();
		Intent intent=new Intent();
	    intent = packageManager.getLaunchIntentForPackage(packageName); 
	    context.startActivity(intent);
		
	}

}
