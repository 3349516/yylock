package com.seven.lock;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seven.lock.base.BaseActivity;
import com.seven.lock.base.ReveiverRefreshListener;
import com.seven.lock.db.AppsProvider;
import com.seven.lock.obj.AppObject;
import com.seven.lock.util.AppsManager;
import com.seven.lock.view.RelativeLayoutWithContextMenuInfo;
import com.seven.lock.view.SwitchView;
import com.umeng.analytics.MobclickAgent;

/**
 * 过滤列表
 * @author ll
 *
 */
public class LockFilterActivity extends BaseActivity implements OnClickListener,ReveiverRefreshListener{

	private LinearLayout app_list_linear;		//程序列表
	private AppsManager appsManager;
	private LayoutInflater flater;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lock_filter);
		initViews();
		
		registerReceiver(new RefreshReceiver(this), new IntentFilter(RefreshReceiver.RECEIVER));
		new Thread(initListApps).start();
	}
	
	private void initViews(){
		app_list_linear = (LinearLayout) findViewById(R.id.app_list_linear);
		appsManager = new AppsManager(this);
		flater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
	}
	

	public static final int MSG_DISMISS_PROGRESS_DIALOG = 17;
	public static final int MSG_UPDATE_LIST = 18;
	private View view;
	private TextView app_name;
	private ImageView app_icon;
	private SwitchView app_switch;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MSG_UPDATE_LIST:
				//添加应用程序列表视图
				AppObject obj = (AppObject) msg.obj;
				view = flater.inflate(R.layout.lock_filter_app_item, null);
				app_name = (TextView) view.findViewById(R.id.app_name);
				app_icon = (ImageView) view.findViewById(R.id.app_icon);
				app_switch = (SwitchView) view.findViewById(R.id.has_encrypt);
				
				view.findViewById(R.id.has_encrypt).setOnClickListener(
						LockFilterActivity.this);
				app_name.setText(obj.name);
				app_icon.setImageDrawable(obj.drawable);

				if ((obj.lock) == 0) {
					app_switch.setChecked(true);
				}
				view.setTag(obj);
				registerForContextMenu(view);	//注册上下文菜单
				
				app_list_linear.addView(view);
				 
				break;
			case MSG_DISMISS_PROGRESS_DIALOG:
			 
				// 去掉最后一条分隔符
				int count = app_list_linear.getChildCount();
				if (count > 0) {
					app_list_linear
							.getChildAt(app_list_linear.getChildCount() - 1)
							.findViewById(R.id.line).setVisibility(View.GONE);
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};
	
	private static final int MENU_ID=Menu.FIRST;
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);  
		//通过xml文件来配置上下文菜单选项  
	      /*  MenuInflater mInflater = getMenuInflater();  
	        mInflater.inflate(R.menu.menu_move, menu);  */
		//通过手动添加来配置上下文菜单选项 
		menu.add(0, MENU_ID, 0, R.string.menu_open);    
		menu.add(0, MENU_ID+1, 0, R.string.menu_add);    
		if(((AppObject)v.getTag()).type!=0){
			menu.add(0, MENU_ID+2, 0,  R.string.menu_uninstall);   
		}
		
		MobclickAgent.onEvent(this, "ContextMenu","过滤");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		RelativeLayoutWithContextMenuInfo.RelativeLayoutContextMenuInfo menuInfo = (RelativeLayoutWithContextMenuInfo.RelativeLayoutContextMenuInfo) item.getMenuInfo();  
		RelativeLayoutWithContextMenuInfo selectView =(RelativeLayoutWithContextMenuInfo)menuInfo.targetView;  
		AppObject appObj =  (AppObject)selectView.getTag() ;
		switch (item.getItemId()) {
		case MENU_ID:
			//通过包名打开应用程序
			appsManager.startIntentByPackageName(appObj.packageName);
		    finish();
		    MobclickAgent.onEvent(this, "ContextMenu_open");
			break;
		case MENU_ID+1:
			
			//添加到列表
			ContentValues cv  =  new ContentValues();
			cv.put("show", 0); // 显示
			getContentResolver().update(AppsProvider.DBProvider, cv,"packageName = ?", new String[]{appObj.packageName});
			app_list_linear.removeView(selectView);
			
			//发送广播,通知列表更新
            Intent delIntent = new Intent(RefreshReceiver.RECEIVER);
        	delIntent.putExtra("packageName", appObj.packageName);
        	delIntent.putExtra("type", 1);
        	sendBroadcast(delIntent);
        	
        	Intent appsReset = new Intent(ActvityInterceptService.RECEIVER);
			appsReset.putExtra("tag", -1);		//拦截列表重置
			sendBroadcast(appsReset);
			
			MobclickAgent.onEvent(this, "ContextMenu_remove");
			break;
			
		case MENU_ID+2:
			Uri packageURI = Uri.parse("package:"+appObj.packageName);     
			Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);     
			startActivity(uninstallIntent);
			
			MobclickAgent.onEvent(this, "ContextMenu_uninstall");
			break;
			
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private Runnable initListApps = new Runnable() {
		@Override
		public void run() {
			ArrayList<AppObject> apps = appsManager.query(1);		//隐藏的列表  查询数据库
			
			// 逐项添加程序，并发送消息更新ListView列表。
			AppObject appObj = null;
			for (int i = 0; i < apps.size(); i++) {
				appObj = apps.get(i);
				Message message = mHandler.obtainMessage();
				message.what = MSG_UPDATE_LIST;
				message.obj = appObj;
				mHandler.sendMessage(message);

			}
			mHandler.sendEmptyMessage(MSG_DISMISS_PROGRESS_DIALOG);
		}
	};
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.has_encrypt: // 是否拦截该程序
			SwitchView itemSv = (SwitchView) v;
			AppObject ao = (AppObject) ((RelativeLayout) itemSv.getParent())
					.getTag();
			ContentValues cv = new ContentValues();
			if (itemSv.isChecked()) {
				cv.put("lock", 0); // 加密
			} else {
				cv.put("lock", 1); // 不加密
			}
			//更新数据库
			getContentResolver().update(AppsProvider.DBProvider, cv,"packageName = ?", new String[]{ao.packageName});
			Intent appsReset = new Intent(ActvityInterceptService.RECEIVER);
			appsReset.putExtra("tag", -1);		//拦截列表重置
			sendBroadcast(appsReset);
			break;

		default:
			break;
		}
		
	}

	@Override
	public void onRefresh(String packageName,int type) {
		if(type!=1){
			app_list_linear.removeAllViews();
			new Thread(initListApps).start();
		}
		
		
	}
	
}
