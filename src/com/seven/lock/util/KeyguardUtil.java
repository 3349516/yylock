package com.seven.lock.util;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;

public class KeyguardUtil {
 
	private KeyguardLock mKeyguardLock ;
	private Context context;
	
	public KeyguardUtil(Context context){
		this.context = context;
		KeyguardManager mKeyguardManager = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);  
    	mKeyguardLock = mKeyguardManager.newKeyguardLock(""); 
	} 
	
	/**
	 * ��������  
	 * @param bEnable  true ʹ������  false  Ĭ������ 
	 */ 
	public void enableSystemKeyguard(boolean bEnable){

    	if(bEnable){
    		//ʹ������
    		mKeyguardLock.disableKeyguard();	
    	}
    	else{
    		//�ָ�����
    		mKeyguardLock.reenableKeyguard();
    	}
    		
    }
	
	/**
	 * �Ƿ�����
	 * @return
	 */
	public boolean isLockScreen(){
		KeyguardManager mKeyguardManager = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);   
	      
	    return mKeyguardManager.inKeyguardRestrictedInputMode();  
	}
	 
	
}
