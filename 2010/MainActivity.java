package com.youge.upay_sdk_demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.upay.billing.sdk.Upay;
import com.upay.billing.sdk.UpayCallback;
//import com.upay.billing.utils.NativeUtil;
//import com.upay.billing.utils.SendSmsProgressDialog;

public class MainActivity extends Activity {
	
	private static final String TAG = "PlanTest";
	
	private Upay up;
	private TextView mTextView_Payment,mTextView_Trade;
	private EditText mEditText;
	private String request_payment,request_trade;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg){
			switch (msg.what) {
			case 0:
				mTextView_Payment.setText(request_payment);
				break;
            case 1:
            	mTextView_Trade.setText(request_trade);
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		mTextView_Payment = (TextView)findViewById(R.id.textView2);
		mTextView_Trade = (TextView)findViewById(R.id.textView3);
		mEditText = (EditText)findViewById(R.id.editText1);
		up = Upay.initInstance(this, "test_app", "abc");
	
		
		this.findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG, "pay");
				mTextView_Payment.setText("");
				mTextView_Trade.setText("");
				Upay up = Upay.getInstance("test_app");
				up.pay(mEditText.getText().toString(), "透传数据", new UpayCallback() {

					@Override
					public void onPaymentResult(String goodsKey, String tradeId, int resultCode, String errorMsg, String extra) {
						Log.i(TAG, "paymentResult");
						if(resultCode==200){
							request_payment = "paymentResult返回的参数---->支付成功-----resultCode="+resultCode+"--goodsKey="+goodsKey+"--tradeId="+tradeId+"--extra="+extra;
						}else if(resultCode == 110){
							request_payment = "paymentResult返回的参数---->支付取消-----resultCode="+resultCode+"--goodsKey="+goodsKey+"--tradeId="+tradeId+"--extra="+extra;
						}else{
							request_payment = "paymentResult返回的参数---->支付失败-----resultCode="+resultCode+"--goodsKey="+goodsKey+"--tradeId="+tradeId+"--extra="+extra;
						}
						Log.e(TAG, request_payment);
						mHandler.sendEmptyMessage(0);
					}

					@Override
					public void onTradeProgress(String goodsKey, String tradeId, int price, int paid, String extra, int resultCode) {
						Log.i(TAG, "tradeProgress");
						if(resultCode == 200){
							request_trade = "tradeProgress返回的参数---->扣费成功-----resultCode="+resultCode+"--goodsKey="+goodsKey+"--tradeId="+tradeId+"--price="+price+"--paid="+paid+"--extra="+extra;
							Log.e(TAG, request_trade);
							mHandler.sendEmptyMessage(1);
						}else if(resultCode == 203){
							request_trade = "tradeProgress返回的参数---->只是短信发送成功-----resultCode="+resultCode+"--goodsKey="+goodsKey+"--tradeId="+tradeId+"--price="+price+"--paid="+paid+"--extra="+extra;
							Log.e(TAG, request_trade);
							mHandler.sendEmptyMessage(1);
						}
					}
				});
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Upay up = Upay.getInstance("test_app");
		up.exit();
	}
	
}
