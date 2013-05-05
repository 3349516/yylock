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
	 * ÉèÖÃËøÆÁ  
	 * @param bEnable  true Ê¹ÓÃËøÆÁ  false  Ä¬ÈÏËøÆÁ 
	 */ 
	public void enableSystemKeyguard(boolean bEnable){

    	if(bEnable){
    		//Ê¹ÓÃËøÆÁ
    		mKeyguardLock.disableKeyguard();	
    	}
    	else{
    		//»Ö¸´ËøÆÁ
    		mKeyguardLock.reenableKeyguard();
    	}
    		
    }
	
	/**
	 * ÊÇ·ñËøÆÁ
	 * @return
	 */
	public boolean isLockScreen(){
		KeyguardManager mKeyguardManager = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);   
	      
	    return mKeyguardManager.inKeyguardRestrictedInputMode();  
	}
	 
	
}
