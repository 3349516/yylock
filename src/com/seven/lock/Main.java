package com.seven.lock;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kuguo.ad.KuguoAdsManager;
import com.seven.lock.base.BaseActivity;
import com.seven.lock.pattern.PatternMain;
import com.seven.lock.util.MD5;
import com.seven.lock.util.PreferencesHelper;
import com.seven.lock.view.LockPatternUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 输入密码界面
 * @author ll
 *
 */
public class Main extends BaseActivity implements OnClickListener {

	private TextView password1, password2, password3, password4, messageText;
	private ImageButton key1, key2, key3, key4, key5, key6, key7, key8, key9,
			key0, back;
	private StringBuffer psw;
	private PreferencesHelper helper;
	private static final String KEY = "key";		//存入密码键值
	private int tag = 0;
	private MD5 md5; 
	private Vibrator vibrator;   //震动
	//前3个的值是设置震动的大小  最后一个值是设置震动的时间
    private static final long[] pattern = {0, 1, 40, 41}; // 震动周期  
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	MobclickAgent.onError(this);
	/*
		 ＊获取PushAdsManager的唯一实例
	 */
	KuguoAdsManager paManager = KuguoAdsManager.getInstance();
	paManager.setCooId(this, "f8d4b3cc7a9f4b88b7111052aef2e78e");
	paManager.receivePushMessage(this, true);

	
    /**
     * 加入精品推荐的酷悬，传入1表示显示酷悬
     */
	paManager.showKuguoSprite(this, 1);
    
	
		helper = new PreferencesHelper(this);
		tag = this.getIntent().getIntExtra("index", tag);
		/**
		 * 如果图案解锁打开,,打开图案解锁
		 */
		if(helper.getBoolean(LockPatternUtils.PatternSwitch)){
			Intent it = new Intent(this,PatternMain.class);
			it.putExtra("index", tag);
			startActivity(it);
			finish();
		}
		initViews();
	}

	private void initViews() {
		psw = new StringBuffer();
		md5 = MD5.getInstance();
		password1 = (TextView) findViewById(R.id.password1);
		password2 = (TextView) findViewById(R.id.password2);
		password3 = (TextView) findViewById(R.id.password3);
		password4 = (TextView) findViewById(R.id.password4);
		messageText = (TextView) findViewById(R.id.messageText);
		key1 = (ImageButton) findViewById(R.id.key1);
		key1.setOnClickListener(this);
		key2 = (ImageButton) findViewById(R.id.key2);
		key2.setOnClickListener(this);
		key3 = (ImageButton) findViewById(R.id.key3);
		key3.setOnClickListener(this);
		key4 = (ImageButton) findViewById(R.id.key4);
		key4.setOnClickListener(this);
		key5 = (ImageButton) findViewById(R.id.key5);
		key5.setOnClickListener(this);
		key6 = (ImageButton) findViewById(R.id.key6);
		key6.setOnClickListener(this);
		key7 = (ImageButton) findViewById(R.id.key7);
		key7.setOnClickListener(this);
		key8 = (ImageButton) findViewById(R.id.key8);
		key8.setOnClickListener(this);
		key9 = (ImageButton) findViewById(R.id.key9);
		key9.setOnClickListener(this);
		key0 = (ImageButton) findViewById(R.id.key0);
		key0.setOnClickListener(this);
		back = (ImageButton) findViewById(R.id.back);
		back.setOnClickListener(this);
		
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);  

		if ("".equals(helper.getValue(KEY))) {
			tag = 1;
		}
		if (tag == 1) {
			messageText.setText(R.string.modify_newpassword);
		}else if(tag==4){
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);  
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);  
		}
		
		
	}
	
	@Override
	public void onClick(View v) {
		int in = -1;
		vibrator.vibrate(pattern, -1); //-1不重复，非-1为从pattern的指定下标开始重复
		switch (v.getId()) {
		case R.id.key1:
			in = 1;
			break;
		case R.id.key2:
			in = 2;
			break;
		case R.id.key3:
			in = 3;
			break;
		case R.id.key4:
			in = 4;
			break;
		case R.id.key5:
			in = 5;
			break;
		case R.id.key6:
			in = 6;
			break;
		case R.id.key7:
			in = 7;
			break;
		case R.id.key8:
			in = 8;
			break;
		case R.id.key9:
			in = 9;
			break;
		case R.id.key0:
			in = 0;
			break;
		case R.id.back:
			int length = psw.length();
			if (length > 0) {
				psw.deleteCharAt(length - 1);
				setPassword(string2Arr(psw));
			}
			break;
		default:
			break;
		}
		//按下键盘处理
		if (in != -1 && psw.length() < 4) {
			psw.append(in);
			setPassword(string2Arr(psw));
			if (psw.length() == 4) {
				check();
			}
		}

	}
	
	
	
	@Override
	protected void onStop() {
        if(null!=vibrator){  
            vibrator.cancel();  
        }  
        super.onStop();
	}



	private String input1 = null;

	private void check() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Main.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						waitInput();
						switch (tag) {
						case 0: // 打开程序
							String pw = helper.getValue(KEY);
							if (pw.equals(md5.createMD5(
									psw.toString()))) {
								Intent intent = new Intent(Main.this,
										LockMain.class);
								startActivity(intent);
								finish();
								MobclickAgent.onEvent(Main.this, "Open") ;
							} else {
								messageText
										.setTextColor(getResources().getColor(
												R.color.password_title_error));
								messageText.setText(R.string.input_error);
							}
							break;
						case 1: // 设置新密码
							input1 = md5.createMD5(psw.toString());
							messageText.setTextColor(getResources().getColor(
									R.color.password_title));
							messageText.setText(R.string.modify_newpassword2);
							tag = 2;
							break;
						case 2: // 确认密码
							String input2 = md5.createMD5(
									psw.toString());
							if (input2.equals(input1)) {
								helper.setValue(KEY, input2);
								Toast.makeText(Main.this,
										R.string.modify_success,
										Toast.LENGTH_SHORT).show();
								Intent intent = new Intent(Main.this,
										LockMain.class);
								startActivity(intent);
								finish();
							} else {
								messageText
										.setTextColor(getResources().getColor(
												R.color.password_title_error));
								messageText.setText(R.string.modify_error);
								tag = 1;
							}
							break;
						case 3:		//拦截界面
							String pw3 = helper.getValue(KEY);
							if (pw3.equals(md5.createMD5(
									psw.toString()))) {
								finish();
								MobclickAgent.onEvent(Main.this, "Pwd_unlock") ;
							} else {
								messageText
										.setTextColor(getResources().getColor(
												R.color.password_title_error));
								messageText.setText(R.string.input_error);
							}
							break;
						case 4:	//锁屏
							String pw4 = helper.getValue(KEY);
							if (pw4.equals(md5.createMD5(
									psw.toString()))) {
								finish();
								MobclickAgent.onEvent(Main.this, "LockScreen_unlock") ;
							} else {
								messageText
										.setTextColor(getResources().getColor(
												R.color.password_title_error));
								messageText.setText(R.string.input_error);
							}
							break;
						default:
							break;
						}

						clearPassword();

					}
				});

			}

		}).start();
	}

	private void waitInput() {
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private String[] string2Arr(StringBuffer buff) {
		String[] arr = new String[] { "", "", "", "" };
		for (int i = 0; i < buff.length(); i++) {
			arr[i] = String.valueOf(buff.charAt(i));
		}
		return arr;

	}

	private void setPassword(String[] buff) {
		password1.setText(buff[0]);
		password2.setText(buff[1]);
		password3.setText(buff[2]);
		password4.setText(buff[3]);
	}

	private void clearPassword() {
		psw.delete(0, psw.length());
		password1.setText("");
		password2.setText("");
		password3.setText("");
		password4.setText("");
	}
	

	/**
	 * 屏蔽掉后退按键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (tag==3&&event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
	        return false;
	    }
	    if (tag==4&&event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
	        return false;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onAttachedToWindow() {
		if(tag==4){		//锁屏
			// 屏蔽 Home
			this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		}
		super.onAttachedToWindow();
	}
	
	 @Override
	 protected void onDestroy() {
	    	super.onDestroy();
	    	//回收接口，退出酷仔及回收酷仔资源
	    	KuguoAdsManager.getInstance().recycle(this);
	    }

}