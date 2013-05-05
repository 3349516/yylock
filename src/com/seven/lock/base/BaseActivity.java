package com.seven.lock.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//»•±ÍÃ‚
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	 @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
	 
	@Override  
	protected void onPause() {
	    super.onPause();
	    MobclickAgent.onPause(this);
	}
}
 