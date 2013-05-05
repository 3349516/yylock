package com.seven.lock.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * 自己定义的View，响应长按菜单。重写该View的getContextMenuInfo()。
 * @author ll
 *
 */
public class RelativeLayoutWithContextMenuInfo extends RelativeLayout {

	public RelativeLayoutWithContextMenuInfo(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public RelativeLayoutWithContextMenuInfo(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	@Override  
    protected ContextMenuInfo getContextMenuInfo() {  
        return new RelativeLayoutContextMenuInfo(this);
    }
  
	//弹出菜单的生成
    public static class RelativeLayoutContextMenuInfo implements ContextMenu.ContextMenuInfo {  
        public RelativeLayoutContextMenuInfo(View targetView) {  
            this.targetView = (RelativeLayout) targetView;  
        }
        public RelativeLayout targetView;  
    }  

}
