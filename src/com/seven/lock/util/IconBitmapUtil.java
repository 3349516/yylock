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
 * 用于实现Bitmap和byte[]间的相互转换
 * @author ll
 *
 */
public class IconBitmapUtil {

	/**
	 * 字节转成位图
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
	 * 字节转成图片
	 * @param data
	 * @return
	 */
	public static Drawable getDrawable(byte[] data){
		return new BitmapDrawable(getBitmap(data));  
	}
 
	/**
	 * 图片转成字节
	 * @param drawable
	 * @return
	 */
    public static byte[] getBytes(Drawable drawable) {
    	//先将Drawable 转成  Bitmap
    	  BitmapDrawable bd = (BitmapDrawable)drawable;
      	Bitmap bm = bd.getBitmap();
      	
        ByteArrayOutputStream baops = new ByteArrayOutputStream();
    	bm.compress(CompressFormat.PNG, 100, baops);
        return baops.toByteArray();
    }
    
    /**
     * 图片转成位图
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
