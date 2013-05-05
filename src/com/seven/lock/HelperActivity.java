package com.seven.lock;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.seven.lock.base.BaseActivity;

public class HelperActivity extends BaseActivity implements View.OnClickListener {
	private ImageView imageView1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.helper);
		initViews();
	}
	
	private void initViews(){
		imageView1 = (ImageView) findViewById(R.id.image1);
		imageView1.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.image1:
			finish();
			break;
		default:
			break;
		}
		
	}
}
