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
 * �������
 * @author ll
 *
 */
public class LockEncryActivity extends BaseActivity implements
		OnClickListener,ReveiverRefreshListener {

	private SwitchView password_switch; // �Ƿ������
	private LinearLayout app_list_linear;		//�����б�
	private LayoutInflater flater;
	private PreferencesHelper helper; // �洢
	public static final String IsCloseSHOW = "IsCloseSHOW"; // �Ƿ�ر����� �洢 key   ������ true  ����  false   
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
	 * ʵ������ͼ
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

		// �������ڴ�����
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
		case R.id.password_switch: // �л����ؿ���
			SwitchView sv = (SwitchView) v;
			Animation anim;
			Intent receShow = new Intent(ActvityInterceptService.RECEIVER);
			if (sv.isChecked()) {
				anim = AnimationUtils.loadAnimation(this, R.anim.inquiry_in);
				app_list_linear.setVisibility(View.VISIBLE);
				helper.setValue(IsCloseSHOW, false); // ���� 
				receShow.putExtra("tag", 1);
			} else {
				anim = AnimationUtils.loadAnimation(this, R.anim.inquiry_out);
				app_list_linear.setVisibility(View.GONE);
				helper.setValue(IsCloseSHOW, true); // �ر�
				receShow.putExtra("tag", 0);
			}
			sendBroadcast(receShow);
			app_list_linear.startAnimation(anim);
			MobclickAgent.onEvent(this, "Switch") ;
			break;
		case R.id.has_encrypt: // �Ƿ����ظó���
			SwitchView itemSv = (SwitchView) v;
			AppObject ao = (AppObject) ((RelativeLayout) itemSv.getParent())
					.getTag();
			ContentValues cv = new ContentValues();
			if (itemSv.isChecked()) {
				cv.put("lock", 0); // ����
			} else {
				cv.put("lock", 1); // ������
			}
			//�������ݿ�
			getContentResolver().update(AppsProvider.DBProvider, cv,"packageName = ?", new String[]{ao.packageName});
			Intent appsReset = new Intent(ActvityInterceptService.RECEIVER);
			appsReset.putExtra("tag", -1);		//�����б�����
			sendBroadcast(appsReset);
			
			Map<String, String> app_info = new HashMap<String, String>();
			app_info.put("name", ao.name);
			app_info.put("lock", itemSv.isChecked()?"����":"������");
			app_info.put("packageName", ao.packageName);
			MobclickAgent.onEvent(this, "Intercept_app",app_info) ;
			break;
		default:
			break;
		}

	}

	private static final int MSG_DISMISS_PROGRESS_DIALOG = 17;
	private static final int MSG_UPDATE_LIST = 18;
	private ProgressDialog dialog; // ������

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
				//���Ӧ�ó����б���ͼ
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
				registerForContextMenu(view);	//ע�������Ĳ˵�
				
				app_list_linear.addView(view);
				 
				break;
			case MSG_DISMISS_PROGRESS_DIALOG:
				// �ر����ڴ�����
				if(dialog!=null&&dialog.isShowing()){
					dialog.dismiss();
				}
				// ȥ�����һ���ָ���
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
		//ͨ��xml�ļ������������Ĳ˵�ѡ��  
	      /*  MenuInflater mInflater = getMenuInflater();  
	        mInflater.inflate(R.menu.menu_move, menu);  */
		//ͨ���ֶ���������������Ĳ˵�ѡ�� 
		menu.add(0, MENU_ID, 0, R.string.menu_open);    
		menu.add(0, MENU_ID+1, 0, R.string.menu_move);    
		if(((AppObject)v.getTag()).type!=0){
			menu.add(0, MENU_ID+2, 0,  R.string.menu_uninstall);   
		}
		MobclickAgent.onEvent(this, "ContextMenu","����");
	}

	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		RelativeLayoutWithContextMenuInfo.RelativeLayoutContextMenuInfo menuInfo = (RelativeLayoutWithContextMenuInfo.RelativeLayoutContextMenuInfo) item.getMenuInfo();  
		RelativeLayoutWithContextMenuInfo selectView =(RelativeLayoutWithContextMenuInfo)menuInfo.targetView;  
		AppObject appObj =  (AppObject)selectView.getTag() ;
		switch (item.getItemId()) {
		case MENU_ID:
			//ͨ��������Ӧ�ó���
			appsManager.startIntentByPackageName(appObj.packageName);
		    finish();
		    MobclickAgent.onEvent(this, "ContextMenu_open");
			break;
		case MENU_ID+1:
			//���б����Ƴ�
			ContentValues cv  =  new ContentValues();
			cv.put("show", 1); // ����
			getContentResolver().update(AppsProvider.DBProvider, cv,"packageName = ?", new String[]{appObj.packageName});
			app_list_linear.removeView(selectView);
			
			//���͹㲥,֪ͨ�б����
            Intent delIntent = new Intent(RefreshReceiver.RECEIVER);
        	delIntent.putExtra("packageName", appObj.packageName);
        	delIntent.putExtra("type", 0);
        	sendBroadcast(delIntent);
        	
        	Intent appsReset = new Intent(ActvityInterceptService.RECEIVER);
			appsReset.putExtra("tag", -1);		//�����б�����
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
			ArrayList<AppObject> apps = appsManager.query(0);		//��ʾ���б�  ��ѯ���ݿ�
			if (apps.size() == 0) {
				appsManager.initDBApps();									//��ʼ�����ݿ�
				apps = appsManager.query(0);					 
				
			}
			// ������ӳ��򣬲�������Ϣ����ListView�б�
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
