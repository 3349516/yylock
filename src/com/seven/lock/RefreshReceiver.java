package com.seven.lock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.seven.lock.base.ReveiverRefreshListener;

/**
 * ˢ�¹㲥
 * @author ll
 *
 */
public class RefreshReceiver extends BroadcastReceiver {

	public static final String RECEIVER = "com.seven.lock.RefreshReceiver";
	private ReveiverRefreshListener listener;
	
	public RefreshReceiver(ReveiverRefreshListener listener) {
		super();
		this.listener = listener;
	}


	@Override
	public void onReceive(Context context, Intent intent) {
		String packageName = intent.getStringExtra("packageName");
		int type = intent.getIntExtra("type",-1);
		listener.onRefresh(packageName,type);

	}

}
