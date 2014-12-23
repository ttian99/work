/****************************************************************************
Copyright (c) 2010-2012 cocos2d-x.org

http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 ****************************************************************************/
package com.zeusky.star;

import java.io.File;
import java.util.HashMap;

import org.cocos2dx.lib.Cocos2dxActivity;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.ShareSDKUtils;

import com.methodsRun.methodsRun;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatService;
import com.tendcloud.tenddata.TDGAAccount;
import com.tendcloud.tenddata.TDGAAccount.AccountType;
import com.tendcloud.tenddata.TDGAVirtualCurrency;
import com.tendcloud.tenddata.TalkingDataGA;
//import com.upay.billing.sdk.Upay;
//import com.upay.billing.sdk.UpayCallback;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class star extends Cocos2dxActivity {

	private static final String TAG = "PlanTest";
	public static Activity m_instance = null;
	public static boolean test_version = false;
	public static JSONObject payArgs = null;
	
	public static String mOrderId = null;
	public static TDGAAccount mAccount = TDGAAccount.setAccount("10000158");
	
	private static Handler mHandler = new Handler(){
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
	

	final static TextView mTextView_Payment = null;
	final static TextView mTextView_Trade = null;
	static String request_payment = null;
	static String request_trade = null;
	
	
	private static HashMap<String, String> payCodeDic = null;
	
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// /
		m_instance = this;
		
		Upay.initPay(this);
		
//		//Upay初始化sdk
//		Upay up = Upay.initInstance(this,"10000158", "01A2F2471A421FC07AD52FEA8F5EFB79");
//		Log.d(TAG, "----------------sdk初始化完成-------------test_app");
////		Toast.makeText(m_instance, "初始化完成:" , Toast.LENGTH_SHORT)
////		.show();
//		//初始化哈希表
//		payCodeDic = new HashMap<String, String>();
//
//		for (int i = 1; i <= 16; i++) {
//			if (i < 10)
//				payCodeDic.put(i + "", "00000" + (i) + "");
//			else
//				payCodeDic.put(i + "", "0000" + (i) + "");
//		}
//		
//		mOrderId = TalkingDataGA.getDeviceId(star.m_instance) + "-" + System.currentTimeMillis() ;

		StatConfig.setDebugEnable(true);
		StatService.trackCustomEvent(this, "onCreate", "");

		ShareSDKUtils.prepare(); // share sdk
		methodsRun.injectIMEI(Util.getOpenid(this));

		createSharePicDir();

		this.registerReceiver(Util.receiver, new IntentFilter( // 注册下载接收事件
				DownloadManager.ACTION_DOWNLOAD_COMPLETE));

		// Util.openAPK();
//		TalkingDataGA.init(this, "A33B469D0AA02A7E15E8128A54E98261", "Upay");
//		
//		mAccount=TDGAAccount.setAccount(TalkingDataGA.getDeviceId(this));
//    	mAccount.setAccountType(AccountType.ANONYMOUS);
	}

	@Override
	protected void onResume() {
	     super.onResume();
	     TalkingDataGA.onResume(this);       
	 }

	@Override
	 protected void onPause() {
	     super.onPause();
	     TalkingDataGA.onPause(this);
	 }
	
	public static void buyDiamond(final String jsonStr) {
		star.m_instance.runOnUiThread(new Runnable() {
			public void run() {
				try {
//					Toast.makeText(m_instance, "buyDiamond" , Toast.LENGTH_SHORT)
//					.show();
					JSONObject obj = new JSONObject(jsonStr);
					String  goodsKey= payCodeDic.get(obj.getString("itemid"));
					String extra = obj.getString("subject");
					String tradeId = null;
					final int price = Integer.parseInt(obj.getString("total"));
					
					TDGAVirtualCurrency.onChargeRequest(mOrderId, obj.getString("subject"), Integer.parseInt(obj.getString("total")) , "CNY", Integer.parseInt(obj.getString("total")) , methodsRun.getPlatform());
					
					Upay up = Upay.getInstance("10000158");
					up.pay(goodsKey, extra, new UpayCallback() {
						int code;
						int successCode;
						String msg = "购买失败";
						
						
//						@Override
//						public void onPaymentResult(String goodsKey, String tradeId, int resultCode, String errorMsg, String extra) {
//							Log.i(TAG, "paymentResult");
//							if(resultCode==200){
//								request_payment = "paymentResult返回的参数---->支付成功-----resultCode="+resultCode+"--goodsKey="+goodsKey+"--tradeId="+tradeId+"--extra="+extra;
//							}else if(resultCode == 110){
//								request_payment = "paymentResult返回的参数---->支付取消-----resultCode="+resultCode+"--goodsKey="+goodsKey+"--tradeId="+tradeId+"--extra="+extra;
//							}else{
//								request_payment = "paymentResult返回的参数---->支付失败-----resultCode="+resultCode+"--goodsKey="+goodsKey+"--tradeId="+tradeId+"--extra="+extra;
//							}
//							Log.e(TAG, request_payment);
//							mHandler.sendEmptyMessage(0);
//						}
//
//						@Override
//						public void onTradeProgress(String goodsKey, String tradeId, int price, int paid, String extra, int resultCode) {
//							Log.i(TAG, "tradeProgress");
//							if(resultCode == 200){
//								request_trade = "tradeProgress返回的参数---->扣费成功-----resultCode="+resultCode+"--goodsKey="+goodsKey+"--tradeId="+tradeId+"--price="+price+"--paid="+paid+"--extra="+extra;
//								Log.e(TAG, request_trade);
//								mHandler.sendEmptyMessage(1);
//							}else if(resultCode == 203){
//								request_trade = "tradeProgress返回的参数---->只是短信发送成功-----resultCode="+resultCode+"--goodsKey="+goodsKey+"--tradeId="+tradeId+"--price="+price+"--paid="+paid+"--extra="+extra;
//								Log.e(TAG, request_trade);
//								mHandler.sendEmptyMessage(1);
//							}
//						}
						
						
						
						
						@Override
						public void onPaymentResult(String goodsKey, String tradeId, int resultCode, String errorMsg, String extra) {
							Log.i(TAG, "paymentResult");
							Log.i(TAG, "onPaymentResult返回码" + resultCode + "———" + goodsKey +"========"+ price +"++++++++++++"+ tradeId +">>>>>>>>>"+ extra);
//							Toast.makeText(m_instance, "tradeProgress返回码" + resultCode + "———" + goodsKey + price + tradeId + extra, Toast.LENGTH_SHORT)
//							.show();
							if(resultCode==200){
								Toast.makeText(m_instance, "支付成功" /*+ resultCode + goodsKey + price + tradeId + extra*/, Toast.LENGTH_SHORT)
								.show();
								code = successCode;
								msg = "购买成功";
							}else if(resultCode == 110){
								Toast.makeText(m_instance, "支付取消"/* + resultCode + goodsKey + price + tradeId + extra*/, Toast.LENGTH_SHORT)
								.show();
								Log.i(TAG, "onPaymentResult支付取消" + resultCode + "———" + goodsKey +"========"+ price +"++++++++++++"+ tradeId +">>>>>>>>>"+ extra);
								code = 1001;
								msg = "支付取消";
								setCode(code,msg);
//							    return;
							}else{
								Toast.makeText(m_instance, "支付失败"/* + resultCode + goodsKey + price + tradeId + extra*/, Toast.LENGTH_SHORT)
								.show();
								code = 1002;
								msg = "支付失败";
								setCode(code,msg);
//								return;
							}
							Log.i(TAG, request_payment);
						}

						public void onTradeProgress(String goodsKey, String tradeId, int price, int paid, String extra, int resultCode) {
							Log.i(TAG, "tradeProgress");
							Log.i(TAG, "tradeProgress" + resultCode + "———" + goodsKey +"========"+ price +"++++++++++++"+ tradeId +">>>>>>>>>"+ extra);
							if(resultCode == 200){
								Log.e(TAG, request_trade);
								//Toast.makeText(m_instance, "tradeProgress返回码" + resultCode + "———" + goodsKey + price + tradeId + extra, Toast.LENGTH_SHORT)
								//.show();
								successCode = 9000;
								code = successCode;
								msg = "购买成功";
								setCode(code,msg);
								
								TDGAVirtualCurrency.onChargeSuccess(mOrderId);

							}else if(resultCode == 203){
								Log.e(TAG, request_trade);
								successCode = resultCode;
								code = successCode;
								msg = "购买成功";
								setCode(code,msg);
								//Toast.makeText(m_instance, "tradeProgress返回码" + resultCode + "———" + goodsKey + price + tradeId + extra, Toast.LENGTH_SHORT)
								//.show();
//								successCode = resultCode;
							}
						}
					});
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});								
	}
	
	
	public static void setCode(int code,String msg){
		try {
			star.payArgs.put("code", code + "");
			star.payArgs.put("msg", msg);
			methodsRun.buyDiamondCallBack(payArgs.toString());

	} catch (JSONException e) {
		e.printStackTrace();
		}
	}
	
	
	
	
	public static void createSharePicDir() {
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

		if (!sdCardExist) {
			Toast.makeText(m_instance, "无外部存储卡，炫耀功能将不可用", Toast.LENGTH_SHORT).show();
		} else {
			String dir = Environment.getExternalStorageDirectory() + File.separator + "zeusky.popstar";
			Log.d("shareSDK", "I am need this dir " + dir);
			File snapShotDir = new File(dir);
			if (!snapShotDir.exists()) {
				snapShotDir.mkdir();
			}
			methodsRun.injectOtherDir(dir + File.separator);
		}
	}
	

	static {
		System.loadLibrary("cocos2djs");
	}
}