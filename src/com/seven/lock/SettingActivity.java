package com.seven.lock;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.seven.lock.base.BaseActivity;
import com.seven.lock.pattern.ChooseLockPatternTutorial;
import com.seven.lock.util.AppsManager;
import com.seven.lock.util.PreferencesHelper;
import com.seven.lock.view.LockPatternUtils;
import com.seven.lock.view.SwitchView;
import com.umeng.analytics.MobclickAgent;

/**
 * ����
 * @author ll
 *
 */
public class SettingActivity extends BaseActivity implements OnClickListener{
	
	private SwitchView pattern_switch,screen_switch;		//ͼ������,��Ļ����
	private RelativeLayout resetList;	 //�б�����
	private RelativeLayout modifyPwdPane; // �޸�����
	private PreferencesHelper helper; // �洢
	private final int REQUESTCODE = 7;
	public static final String LOCK_SCREEN_ON_OFF = "lock_screen_on_off";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lock_setting);
		initViews();
	}
	
	private void initViews(){
		resetList = (RelativeLayout) findViewById(R.id.resetList);
		resetList.setOnClickListener(this);
		modifyPwdPane = (RelativeLayout) findViewById(R.id.modifyPwdPane);
		modifyPwdPane.setOnClickListener(this);
		pattern_switch  = (SwitchView) findViewById(R.id.pattern_switch);
		pattern_switch.setOnClickListener(this);
		screen_switch  = (SwitchView) findViewById(R.id.screen_switch);
		screen_switch.setOnClickListener(this);
		helper = new PreferencesHelper(this);
		pattern_switch.setChecked(helper.getBoolean(LockPatternUtils.PatternSwitch));
		screen_switch.setChecked(helper.getBoolean(LOCK_SCREEN_ON_OFF));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pattern_switch:
			SwitchView sv = (SwitchView) v;
			if (sv.isChecked()) {
				Intent patternIntent = new Intent(this,ChooseLockPatternTutorial.class);
				startActivityForResult(patternIntent,REQUESTCODE);
			}else{
				helper.setValue(LockPatternUtils.PatternSwitch,false);
			}
			MobclickAgent.onEvent(this, "LockPattern_switch");
			break;
		case R.id.screen_switch:
			SwitchView screenLock = (SwitchView) v;
			if(screenLock.isChecked()){
				startService(new Intent(this, LockScreenService.class));
			}else{
				stopService(new Intent(this, LockScreenService.class));
			}
			helper.setValue( LOCK_SCREEN_ON_OFF ,screenLock.isChecked());
			MobclickAgent.onEvent(this, "LockScreen_switch");
			break;
		case R.id.resetList:  
			handler.sendEmptyMessage(MSG_START);
			MobclickAgent.onEvent(this, "list_reset");
			break;
		case R.id.modifyPwdPane: // �޸�����
			Intent intent = new Intent(this, Main.class);
			intent.putExtra("index", 1);
			startActivity(intent);
			finish();
			MobclickAgent.onEvent(this, "Modify_pwd");
			break;
		default:
			break;
		}
		
	}
	
	private static final int MSG_START = 7;		//��Ϣ��ʼ
	private static final int MSG_END = 11;		//��Ϣ����
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_START:
				new Thread(reset).start();
				break;
			case MSG_END:
				//���͹㲥,֪ͨ�б�ˢ��
	            Intent delIntent = new Intent(RefreshReceiver.RECEIVER);
	        	delIntent.putExtra("packageName", "");
	        	delIntent.putExtra("type", -1);
	        	sendBroadcast(delIntent);
	        	
	        	Intent appsReset = new Intent(ActvityInterceptService.RECEIVER);
				appsReset.putExtra("tag", -1);		//�����б�����
				sendBroadcast(appsReset);
	        	
	        	Toast.makeText(SettingActivity.this,R.string.setting_resetlist_info, Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	
	private Runnable reset = new Runnable(){

		@Override
		public void run() {
			new AppsManager(SettingActivity.this).resetDBApps();
			handler.sendEmptyMessage(MSG_END);
			
		}
		
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUESTCODE:		//�Ƿ���������ɹ�
		  if("".equals(helper.getValue(LockPatternUtils.PatternPwd))){
				pattern_switch.setChecked(false);
	      }else{
	        	pattern_switch.setChecked(true);
	      }
		helper.setValue(LockPatternUtils.PatternSwitch,pattern_switch.isChecked());
			break;
		default:
			break;
		}
		
	}
	
}
