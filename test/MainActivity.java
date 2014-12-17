package com.example.paytest;

import java.util.Random;
import com.paytest.phonefee.R;
import com.zywx.myepay.MyEPay;
import com.zywx.myepay.OnDefrayListener;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements OnDefrayListener {
	private final String TAG = getClass().getName();
	private Context context;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			defray(100);
		};
	};

	void defray(int fee) {
		MyEPay pay = MyEPay.getInstance();
		pay.startPay(MainActivity.this, fee, null, getSerial(), null,
				MainActivity.this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		MyEPay d = MyEPay.getInstance();
		// 是否开启有提示的sdk初始化z
		d.init(MainActivity.this, this);
		Button bt1 = (Button) findViewById(R.id.button1);
		bt1.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				defray(100);
			}
		});
		findViewById(R.id.button2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				defray(200);
			}
		});
		findViewById(R.id.button3).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				defray(300);
			}
		});
		findViewById(R.id.button6).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EditText mEditText = (EditText) findViewById(R.id.editText1);
				int charge = 0;
				charge = Integer.parseInt(mEditText.getText().toString());
				Log.d(TAG, "自定义计费金额====" + charge);
				defray(charge);
			}
		});
	}

	@Override
	public void onDefrayFinished(int paramInt) {
		Toast.makeText(this, "Defray Finished:" + paramInt, Toast.LENGTH_SHORT)
				.show();
		Log.d("Defray", "there is ------------------defray" + paramInt);
	}

	@Override
	public void onInitFinished(int paramInt) {
		Toast.makeText(this, "Init Finished:" + paramInt, Toast.LENGTH_SHORT)
				.show();
		Log.d("Defray", "there is ------------------InitFinished" + paramInt);
	}

	private String getSerial() {
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		int i = 0;
		while (i < 8) {
			int j = random.nextInt(10);
			sb.append(j);
			i++;
		}
		return sb.toString();
	}

}
