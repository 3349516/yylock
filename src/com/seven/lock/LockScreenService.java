package com.seven.lock;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.seven.lock.util.KeyguardUtil;

public class LockScreenService extends Service {

	public static final String ACT_SCREEN_OFF = "android.intent.action.SCREEN_OFF";	//¹Ø±ÕËøÆÁ
	public static final String ACT_SCREEN_ON = "android.intent.action.SCREEN_ON";	//´ò¿ªËøÆÁ

	private KeyguardUtil keyUtil;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		IntentFilter intentFilter = new IntentFilter(ACT_SCREEN_OFF);
		registerReceiver(mScreenBCR, intentFilter);
		keyUtil = new KeyguardUtil(this);
		keyUtil.enableSystemKeyguard(true);
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mScreenBCR);
		keyUtil.enableSystemKeyguard(false);
		super.onDestroy();
		
	}

	private BroadcastReceiver mScreenBCR = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			{
			
					Intent i = new Intent();
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					i.setClass(context, Main.class);
					i.putExtra("index", 4);		//ËøÆÁ
					context.startActivity(i);
				
			}
		}
	};

}
