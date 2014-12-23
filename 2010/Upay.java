package com.zeusky.star;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
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






import com.tendcloud.tenddata.TDGAAccount;
import com.tendcloud.tenddata.TalkingDataGA;
import com.tendcloud.tenddata.TDGAAccount.AccountType;
//import com.upay.billing.sdk.Upay;
//import com.upay.billing.sdk.Upay;
import com.upay.billing.sdk.UpayCallback;
//import com.upay.billing.utils.NativeUtil;
//import com.upay.billing.utils.SendSmsProgressDialog;

public class Upay {
	
	private static final String TAG = "PlanTest";
	private static HashMap<String, String> payCodeDic = null;
	public static String mOrderId = null;
	
//	this = star.m_instance;
	
	public static TDGAAccount mAccount = TDGAAccount.setAccount("10000158");
	
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
	
	public static void initPay(){
			//Upay初始化sdk
			Upay up = Upay.initInstance(star.m_instance,"10000158", "01A2F2471A421FC07AD52FEA8F5EFB79");
			Log.d(TAG, "----------------sdk初始化完成-------------test_app");
//			Toast.makeText(m_instance, "初始化完成:" , Toast.LENGTH_SHORT)
//			.show();
			//初始化哈希表
			payCodeDic = new HashMap<String, String>();

			for (int i = 1; i <= 16; i++) {
				if (i < 10)
					payCodeDic.put(i + "", "00000" + (i) + "");
				else
					payCodeDic.put(i + "", "0000" + (i) + "");
			}
			
			mOrderId = TalkingDataGA.getDeviceId(star.m_instance) + "-" + System.currentTimeMillis() ;
			
			// TalkingDataGa 初始化;
			TalkingDataGA.init(this, "A33B469D0AA02A7E15E8128A54E98261", "Upay");
			
			mAccount=TDGAAccount.setAccount(TalkingDataGA.getDeviceId(this));
	    	mAccount.setAccountType(AccountType.ANONYMOUS);
	}
		
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		setContentView(R.layout.activity_main);
//		mTextView_Payment = (TextView)findViewById(R.id.textView2);
//		mTextView_Trade = (TextView)findViewById(R.id.textView3);
//		mEditText = (EditText)findViewById(R.id.editText1);
		up = Upay.initInstance(this, "test_app", "abc");
	
		
		this.findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG, "pay");
				mTextView_Payment.setText("");
				mTextView_Trade.setText("");
				Upay up = Upay.getInstance("test_app");
				up.pay(mEditText.getText().toString(), "͸�����", new UpayCallback() {

					@Override
					public void onPaymentResult(String goodsKey, String tradeId, int resultCode, String errorMsg, String extra) {
						Log.i(TAG, "paymentResult");
						if(resultCode==200){
							request_payment = "paymentResult���صĲ���---->֧���ɹ�-----resultCode="+resultCode+"--goodsKey="+goodsKey+"--tradeId="+tradeId+"--extra="+extra;
						}else if(resultCode == 110){
							request_payment = "paymentResult���صĲ���---->֧��ȡ��-----resultCode="+resultCode+"--goodsKey="+goodsKey+"--tradeId="+tradeId+"--extra="+extra;
						}else{
							request_payment = "paymentResult���صĲ���---->֧��ʧ��-----resultCode="+resultCode+"--goodsKey="+goodsKey+"--tradeId="+tradeId+"--extra="+extra;
						}
						Log.e(TAG, request_payment);
						mHandler.sendEmptyMessage(0);
					}

					@Override
					public void onTradeProgress(String goodsKey, String tradeId, int price, int paid, String extra, int resultCode) {
						Log.i(TAG, "tradeProgress");
						if(resultCode == 200){
							request_trade = "tradeProgress���صĲ���---->�۷ѳɹ�-----resultCode="+resultCode+"--goodsKey="+goodsKey+"--tradeId="+tradeId+"--price="+price+"--paid="+paid+"--extra="+extra;
							Log.e(TAG, request_trade);
							mHandler.sendEmptyMessage(1);
						}else if(resultCode == 203){
							request_trade = "tradeProgress���صĲ���---->ֻ�Ƕ��ŷ��ͳɹ�-----resultCode="+resultCode+"--goodsKey="+goodsKey+"--tradeId="+tradeId+"--price="+price+"--paid="+paid+"--extra="+extra;
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
