package com.seven.lock;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.seven.lock.util.PreferencesHelper;
import com.seven.lock.view.PageControl;
import com.seven.lock.view.SwipeView;

/**
 * 主界面
 * @author ll
 *
 */
public class LockMain extends ActivityGroup {
	
	private SwipeView swipeView;
	private PageControl pageControl;
	private PreferencesHelper helper ;
	private final String HELP = "help";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lock_main);
		
		//开启拦截服务
		Intent intent = new Intent(this, ActvityInterceptService.class);
		startService(intent);
		
		//滑动类
		swipeView = (SwipeView) findViewById(R.id.swipeView);  	//swipeView.smoothScrollToPage(page);
		pageControl = (PageControl) findViewById(R.id.pageControl);

		//程序加密
		View view1 =  getLocalActivityManager().startActivity(
				"view1",
				new Intent(this, LockEncryActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();
		//过滤列表
		View view2 =  getLocalActivityManager().startActivity(
				"view2",
				new Intent(this, LockFilterActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();
		//设置
		View view3 =  getLocalActivityManager().startActivity(
				"view3",
				new Intent(this, SettingActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();
		
		swipeView.addView(view1,0);
		swipeView.addView(view2,1);
		swipeView.addView(view3,2);
		swipeView.setPageControl(pageControl);
		
		helper = new PreferencesHelper(this);
		if(!helper.getBoolean(HELP)){
			initHelper();
		} 
	}
 
	
	private void initHelper(){
		//打开帮助界面
		Intent intent = new Intent(this,HelperActivity.class);
		startActivity(intent);
		helper.setValue(HELP, true);
	}
	
}
