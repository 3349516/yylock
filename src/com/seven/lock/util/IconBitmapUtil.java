package com.seven.lock.util;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * ����ʵ��Bitmap��byte[]����໥ת��
 * @author ll
 *
 */
public class IconBitmapUtil {

	/**
	 * �ֽ�ת��λͼ
	 * @param data
	 * @return
	 */
	public static Bitmap getBitmap(byte[] data) {
		 if(data.length!=0){  
             return BitmapFactory.decodeByteArray(data, 0, data.length);  
         }  
         else {  
             return null;  
         }  
    }
	
	/**
	 * �ֽ�ת��ͼƬ
	 * @param data
	 * @return
	 */
	public static Drawable getDrawable(byte[] data){
		return new BitmapDrawable(getBitmap(data));  
	}
 
	/**
	 * ͼƬת���ֽ�
	 * @param drawable
	 * @return
	 */
    public static byte[] getBytes(Drawable drawable) {
    	//�Ƚ�Drawable ת��  Bitmap
    	  BitmapDrawable bd = (BitmapDrawable)drawable;
      	Bitmap bm = bd.getBitmap();
      	
        ByteArrayOutputStream baops = new ByteArrayOutputStream();
    	bm.compress(CompressFormat.PNG, 100, baops);
        return baops.toByteArray();
    }
    
    /**
     * ͼƬת��λͼ
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {  
        
        Bitmap bitmap = Bitmap  
                        .createBitmap(  
                                        drawable.getIntrinsicWidth(),  
                                        drawable.getIntrinsicHeight(),  
                                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888  
                                                        : Bitmap.Config.RGB_565);  
        Canvas canvas = new Canvas(bitmap);  
        //canvas.setBitmap(bitmap);  
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());  
        drawable.draw(canvas);  
        return bitmap;  
  
}  
}
