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
 *  ���ط���
 */
public class ActvityInterceptService extends Service {

	public static final String RECEIVER = "com.seven.lock.SwitchReceiver";
	// ѭ����ֹ��־ 
	// volatile ʹflagͬ����Ҳ����˵��ͬһʱ��ֻ����һ���߳����޸�flag��ֵ�� 
	private volatile boolean flag = true;
	private PreferencesHelper helper;
	private final static int sheepTime = 50;
	private String[] apps;		//��Ҫ���ص�Ӧ�ó���� ����
	
	/**
	 * bindService() ʱ�����õ���������������� onStartCommnad() ����
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	//��ǰӦ�ó������
	private String crrentPackage ="";
	
	/**
	 * �Ƿ����س���
	 * @param packageName
	 * @return  true ����   false ������
	 */
	private boolean isIntercept(String packageName) {
		//�������Ӧ��Ϊ��ǰ����
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
	
	
	//���� ����ѯ
	public void stopTask() {
		flag = false;
	}

	// ����������ڷֱ��ǲ��������������߳���Ϣʱ�䣩������(publishProgress�õ�)������ֵ ����  
	private AsyncTask taskWatcher;
	public void startTask() {
		flag = true;
		// �½�һ���첽����
		taskWatcher = new AsyncTask() {
			@Override
			protected Object doInBackground(Object... params) {

				// һֱ����
				while (flag) {
					/**
					 * ����ֻ��Ҫ���1��RunningTasks,һ������£����ջ�����մ���ĸ�������ȡ
					 * һ�����ϣ�����һ��activity�ģ����ᱨ�յ������
					 */					
					String name = am.getRunningTasks(1).get(0).topActivity
							.getPackageName();
					if(!name.equals(thisPackage)){
						/**
						 * �жϵ�ǰactivity�İ��Ƿ��������ܱ�����
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
		// ���ǽ�ͨ��activity��������õ�ǰactivityջ������
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		mintent = new Intent();
		// flag������new task
		mintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mintent.setClass(getApplicationContext(), Main.class);
		mintent.putExtra("index", 3); // ����
		boolean showTag = helper.getBoolean(LockEncryActivity.IsCloseSHOW);
		if (showTag) {
			stopTask();
		} else {
			startTask();
		}
		return START_STICKY;
	}
	
	/**
	 * �����жϷ����Ƿ�����.
	 * 
	 * @param context
	 * @param className
	 *            �жϵķ�������
	 * @return true ������ false ��������
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
