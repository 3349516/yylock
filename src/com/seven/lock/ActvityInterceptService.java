package com.seven.lock;

import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;

import com.seven.lock.util.AppsManager;
import com.seven.lock.util.PreferencesHelper;

/**
 * @author lilei
 * @Date 2012-6-19
 *  拦截服务
 */
public class ActvityInterceptService extends Service {

	public static final String RECEIVER = "com.seven.lock.SwitchReceiver";
	// 循环终止标志 
	// volatile 使flag同步，也就是说在同一时刻只能由一个线程来修改flag的值， 
	private volatile boolean flag = true;
	private PreferencesHelper helper;
	private final static int sheepTime = 50;
	private String[] apps;		//需要拦截的应用程序包 数组
	
	/**
	 * bindService() 时，调用的是这个方法，而非 onStartCommnad() 方法
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	//当前应用程序包名
	private String crrentPackage ="";
	
	/**
	 * 是否拦截程序
	 * @param packageName
	 * @return  true 拦截   false 不拦截
	 */
	private boolean isIntercept(String packageName) {
		//如果拦截应用为当前程序
		if(crrentPackage.equals(packageName)){
			return false;
		}
		for (String app : apps) {
			if (packageName.equals(app)) {
				return true;
			}
		}
		return false;

	}
	
	
	//待机 不查询
	public void stopTask() {
		flag = false;
	}

	// 后面尖括号内分别是参数（例子里是线程休息时间），进度(publishProgress用到)，返回值 类型  
	private AsyncTask taskWatcher;
	public void startTask() {
		flag = true;
		// 新建一个异步任务
		taskWatcher = new AsyncTask() {
			@Override
			protected Object doInBackground(Object... params) {

				// 一直监听
				while (flag) {
					/**
					 * 我们只需要获得1个RunningTasks,一般情况下，会从栈顶按照传入的个数来获取
					 * 一个集合，总有一个activity的，不会报空的请放心
					 */					
					String name = am.getRunningTasks(1).get(0).topActivity
							.getPackageName();
					if(!name.equals(thisPackage)){
						/**
						 * 判断当前activity的包是否是属于受保护的
						 */
						if (isIntercept(name)) {
							startActivity(mintent);
						}
						crrentPackage=name;
					}
					
					try {
						Thread.sleep(sheepTime);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				return 0;
			}
		};
		taskWatcher.execute(null);
	}
	
	
	private final String thisPackage = this.getClass().getPackage().getName();
	private ActivityManager am;
	private Intent mintent;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		helper = new PreferencesHelper(this);
		apps  = new AppsManager(this).queryIntercept();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(RECEIVER);
		filter.addAction(LockScreenService.ACT_SCREEN_OFF);
		filter.addAction(LockScreenService.ACT_SCREEN_ON);
		
		registerReceiver(switchReceiver, filter);
		// 我们将通过activity管理服务获得当前activity栈的内容
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		mintent = new Intent();
		// flag必须是new task
		mintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mintent.setClass(getApplicationContext(), Main.class);
		mintent.putExtra("index", 3); // 拦截
		boolean showTag = helper.getBoolean(LockEncryActivity.IsCloseSHOW);
		if (showTag) {
			stopTask();
		} else {
			startTask();
		}
		return START_STICKY;
	}
	
	/**
	 * 用来判断服务是否运行.
	 * 
	 * @param context
	 * @param className
	 *            判断的服务名字
	 * @return true 在运行 false 不在运行
	 * 
	 * isServiceRunning(
					ActvityObserverService.this.getApplicationContext(),
					ActvityObserverService.this.getClass().getName());
	 */
	public static boolean isServiceRunning(Context mContext, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(30);
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	private BroadcastReceiver switchReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(RECEIVER.equals(action)){
				int tag = intent.getIntExtra("tag", -1);
				switch (tag) {
				case -1:
					apps = new AppsManager(ActvityInterceptService.this).queryIntercept();
					break;
				case 0:
					stopTask();
					break;
				case 1:
					startTask();
				default:
					break;
				}
			}else if(LockScreenService.ACT_SCREEN_OFF.equals(action)){
				stopTask();
			}else if(LockScreenService.ACT_SCREEN_ON.equals(action)){
				startTask();
			}
			

		}
	};

}
