package com.seven.lock.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.seven.lock.R;

/**
 * 开关选择控件
 * @author ll
 *
 */
public class SwitchView extends View {

	//默认为false
	private boolean isChecked= false;

	private Bitmap onBg;				//打开的背景图片
	private Bitmap offBg;				//关闭的背景图片
	private Bitmap onPointNormal;		//默认打开的圆圈
	private Bitmap onPointPressed;		//按下打开的圆圈
	private Bitmap offPointNormal;		//默认关闭的圆圈
	private Bitmap offPointPressed;		//按下关闭的圆圈
	
	private Bitmap currentPoint;		//画出的当前的圆圈
	
	//手指按下时的水平坐标X，当前的水平坐标X
	private float previousX, currentX;
	private Animation anim ;
	public SwitchView(Context context) {
		super(context);
		init();
	}

	public SwitchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init(){
		Resources res = getResources();
		onBg = BitmapFactory.decodeResource(res, R.drawable.switch_frame_on_disable);
		offBg = BitmapFactory.decodeResource(res, R.drawable.switch_frame_off_disable);
		onPointNormal = BitmapFactory.decodeResource(res,R.drawable.switch_on_normal);
		onPointPressed = BitmapFactory.decodeResource(res,R.drawable.switch_on_pressed);
		offPointNormal =  BitmapFactory.decodeResource(res,R.drawable.switch_off_normal);
		offPointPressed =  BitmapFactory.decodeResource(res,R.drawable.switch_off_pressed);
		
	}
	
	private void change(){
		if(isChecked){
			currentPoint = onPointNormal;
			anim= new TranslateAnimation ( Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, 
	                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
		}else{
			currentPoint = offPointNormal;
			anim= new TranslateAnimation (  Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, 
	                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
		}
		// 动画开始到结束的执行时间(1000 = 1 秒)
		anim. setDuration ( 500 );
		 // 动画重复次数(-1 表示一直重复)
		anim.setRepeatCount (1 );
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();
		change();
		if(isChecked){
			canvas.drawBitmap(onBg, 0,0, paint);
			canvas.drawBitmap(currentPoint, onBg.getWidth()-onPointNormal.getWidth(),0, paint);
		}else{
			canvas.drawBitmap(offBg, 0,0, paint);
			canvas.drawBitmap(currentPoint, 0,0, paint);
		}
		
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:  //滑动
			
			break;
		case MotionEvent.ACTION_DOWN:  //按下
			if(isChecked){
				currentPoint = onPointPressed;
			}else{
				currentPoint = offPointPressed;
			}
			break;
		case MotionEvent.ACTION_UP:  //抬起
			isChecked= !isChecked;
			if(isChecked){
				currentPoint = onPointNormal;
			}else{
				currentPoint = offPointNormal;
			}
			break;
		default:
			break;
		}
		//重新绘制控件
		invalidate();
		return super.onTouchEvent(event);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		setMeasuredDimension(offBg.getWidth(), offBg.getHeight());
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public boolean isChecked() {
		return isChecked;
	}
	
}
