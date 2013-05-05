package com.seven.lock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seven.lock.base.BaseActivity;
import com.seven.lock.base.ReveiverRefreshListener;
import com.seven.lock.db.AppsProvider;
import com.seven.lock.obj.AppObject;
import com.seven.lock.util.AppsManager;
import com.seven.lock.util.PreferencesHelper;
import com.seven.lock.view.RelativeLayoutWithContextMenuInfo;
import com.seven.lock.view.SwitchView;
import com.umeng.analytics.MobclickAgent;

/**
 * 程序加密
 * @author ll
 *
 */
public class LockEncryActivity extends BaseActivity implements
		OnClickListener,ReveiverRefreshListener {

	private SwitchView password_switch; // 是否打开拦截
	private LinearLayout app_list_linear;		//程序列表
	private LayoutInflater flater;
	private PreferencesHelper helper; // 存储
	public static final String IsCloseSHOW = "IsCloseSHOW"; // 是否关闭拦截 存储 key   不拦截 true  拦截  false   
	private AppsManager appsManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lock_encry);
		initViews();
		new Thread(initListApps).start();
		registerReceiver(new RefreshReceiver(this), new IntentFilter(RefreshReceiver.RECEIVER));
	}

	/**
	 * 实例化视图
	 */
	private void initViews() {
		helper = new PreferencesHelper(this);
		flater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		password_switch = (SwitchView) findViewById(R.id.password_switch);
		password_switch.setOnClickListener(this);
		app_list_linear = (LinearLayout) findViewById(R.id.app_list_linear);

		boolean showTag = helper.getBoolean(IsCloseSHOW);
		if (showTag){
			password_switch.setChecked(false);
			app_list_linear.setVisibility(View.GONE);
		} else {
			password_switch.setChecked(true);
			app_list_linear.setVisibility(View.VISIBLE);
		}

		// 设置正在处理窗口
		dialog = new ProgressDialog(this);
		dialog.setIcon(R.drawable.switch_on_pressed);
		dialog.setTitle(R.string.app_name);
		dialog.setMessage(getResources().getString(R.string.load_info));
		dialog.setCancelable(false);
		dialog.show();

		appsManager = new AppsManager(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.password_switch: // 切换拦截开关
			SwitchView sv = (SwitchView) v;
			Animation anim;
			Intent receShow = new Intent(ActvityInterceptService.RECEIVER);
			if (sv.isChecked()) {
				anim = AnimationUtils.loadAnimation(this, R.anim.inquiry_in);
				app_list_linear.setVisibility(View.VISIBLE);
				helper.setValue(IsCloseSHOW, false); // 拦截 
				receShow.putExtra("tag", 1);
			} else {
				anim = AnimationUtils.loadAnimation(this, R.anim.inquiry_out);
				app_list_linear.setVisibility(View.GONE);
				helper.setValue(IsCloseSHOW, true); // 关闭
				receShow.putExtra("tag", 0);
			}
			sendBroadcast(receShow);
			app_list_linear.startAnimation(anim);
			MobclickAgent.onEvent(this, "Switch") ;
			break;
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
			
			Map<String, String> app_info = new HashMap<String, String>();
			app_info.put("name", ao.name);
			app_info.put("lock", itemSv.isChecked()?"加密":"不加密");
			app_info.put("packageName", ao.packageName);
			MobclickAgent.onEvent(this, "Intercept_app",app_info) ;
			break;
		default:
			break;
		}

	}

	private static final int MSG_DISMISS_PROGRESS_DIALOG = 17;
	private static final int MSG_UPDATE_LIST = 18;
	private ProgressDialog dialog; // 进度条

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
				view = flater.inflate(R.layout.lock_encry_app_item, null);
				app_name = (TextView) view.findViewById(R.id.app_name);
				app_icon = (ImageView) view.findViewById(R.id.app_icon);
				app_switch = (SwitchView) view.findViewById(R.id.has_encrypt);

				view.findViewById(R.id.has_encrypt).setOnClickListener(
						LockEncryActivity.this);
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
				// 关闭正在处理窗口
				if(dialog!=null&&dialog.isShowing()){
					dialog.dismiss();
				}
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
		menu.add(0, MENU_ID+1, 0, R.string.menu_move);    
		if(((AppObject)v.getTag()).type!=0){
			menu.add(0, MENU_ID+2, 0,  R.string.menu_uninstall);   
		}
		MobclickAgent.onEvent(this, "ContextMenu","加密");
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
			//从列表中移除
			ContentValues cv  =  new ContentValues();
			cv.put("show", 1); // 隐藏
			getContentResolver().update(AppsProvider.DBProvider, cv,"packageName = ?", new String[]{appObj.packageName});
			app_list_linear.removeView(selectView);
			
			//发送广播,通知列表更新
            Intent delIntent = new Intent(RefreshReceiver.RECEIVER);
        	delIntent.putExtra("packageName", appObj.packageName);
        	delIntent.putExtra("type", 0);
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
			ArrayList<AppObject> apps = appsManager.query(0);		//显示的列表  查询数据库
			if (apps.size() == 0) {
				appsManager.initDBApps();									//初始化数据库
				apps = appsManager.query(0);					 
				
			}
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
	public void onRefresh(String packageName,int type) {
		if(type!=0){
			app_list_linear.removeAllViews();
			new Thread(initListApps).start();
		}
		
		
	}

	 
}
