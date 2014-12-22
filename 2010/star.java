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
import com.upay.billing.sdk.Upay;
import com.upay.billing.sdk.UpayCallback;

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

	final static TextView mTextView_Payment = null;
	final static TextView mTextView_Trade = null;
	static String request_payment = null;
	static String request_trade = null;
	final static Handler mHandler = new Handler(){
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
	
	
	private static HashMap<String, String> payCodeDic = null;
	
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// /
		m_instance = this;
		
		//Upay初始化sdk
		Upay up = Upay.initInstance(this,"test_app", "abc");
		Log.d(TAG, "----------------sdk初始化完成-------------test_app");
		Toast.makeText(m_instance, "初始化完成:" , Toast.LENGTH_SHORT)
		.show();
		//初始化哈希表
		payCodeDic = new HashMap<String, String>();

		for (int i = 1; i <= 15; i++) {
			if (i < 10)
				payCodeDic.put(i + "", "test_app_g0" + (i) + "");
			else
				payCodeDic.put(i + "", "test_app_g" + (i) + "");
		}

		StatConfig.setDebugEnable(true);
		StatService.trackCustomEvent(this, "onCreate", "");

		ShareSDKUtils.prepare(); // share sdk
		methodsRun.injectIMEI(Util.getOpenid(this));

		createSharePicDir();

		this.registerReceiver(Util.receiver, new IntentFilter( // 注册下载接收事件
				DownloadManager.ACTION_DOWNLOAD_COMPLETE));

		// Util.openAPK();
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
					Toast.makeText(m_instance, "buyDiamond" + extra + goodsKey + price, Toast.LENGTH_SHORT)
					.show();
					
					
					Upay up = Upay.getInstance("test_app");
					up.pay("test_app_g20", "透传数据", new UpayCallback() {

						@Override
						public void onPaymentResult(String goodsKey, String tradeId, int resultCode, String errorMsg, String extra) {
							Log.i(TAG, "paymentResult");
							if(resultCode==200){
								Toast.makeText(m_instance, "buyDiamond" + resultCode + goodsKey + price + tradeId + extra, Toast.LENGTH_SHORT)
								.show();
								//request_payment = "paymentResult返回的参数---->支付成功-----resultCode="+resultCode+"--goodsKey="+goodsKey+"--tradeId="+tradeId+"--extra="+extra;
							}else if(resultCode == 110){
								Toast.makeText(m_instance, "buyDiamond" + resultCode + goodsKey + price + tradeId + extra, Toast.LENGTH_SHORT)
								.show();
								//request_payment = "paymentResult返回的参数---->支付取消-----resultCode="+resultCode+"--goodsKey="+goodsKey+"--tradeId="+tradeId+"--extra="+extra;
							}else{
								Toast.makeText(m_instance, "buyDiamond" + resultCode + goodsKey + price + tradeId + extra, Toast.LENGTH_SHORT)
								.show();
								//request_payment = "paymentResult返回的参数---->支付失败-----resultCode="+resultCode+"--goodsKey="+goodsKey+"--tradeId="+tradeId+"--extra="+extra;
							}
							Log.e(TAG, request_payment);
							//mHandler.sendEmptyMessage(0);
						}

						@Override
						public void onTradeProgress(String goodsKey, String tradeId, int price, int paid, String extra, int resultCode) {
							Log.i(TAG, "tradeProgress");
							if(resultCode == 200){
								//request_trade = "tradeProgress返回的参数---->扣费成功-----resultCode="+resultCode+"--goodsKey="+goodsKey+"--tradeId="+tradeId+"--price="+price+"--paid="+paid+"--extra="+extra;
								Log.e(TAG, request_trade);
								Toast.makeText(m_instance, "buyDiamond" + resultCode + goodsKey + price + tradeId + extra, Toast.LENGTH_SHORT)
								.show();
								//mHandler.sendEmptyMessage(1);
							}else if(resultCode == 203){
								//request_trade = "tradeProgress返回的参数---->只是短信发送成功-----resultCode="+resultCode+"--goodsKey="+goodsKey+"--tradeId="+tradeId+"--price="+price+"--paid="+paid+"--extra="+extra;
								Log.e(TAG, request_trade);
								Toast.makeText(m_instance, "buyDiamond" + resultCode + goodsKey + price + tradeId + extra, Toast.LENGTH_SHORT)
								.show();
								//mHandler.sendEmptyMessage(1);
							}
						}
					});
					
					
					//Upay调用短信支付接口
//					Upay up = Upay.getInstance("test_app");
//					up.pay(goodsKey, extra, new UpayCallback() {
//					@Override
//					public void onPaymentResult(String goodsKey, String tradeId, int resultCode, String errorMsg, String extra) {
//						Log.i(TAG, "paymentResult");
//						Toast.makeText(m_instance, "paymentresult" + extra + goodsKey + price, Toast.LENGTH_SHORT)
//						.show();
//						//扣费成功
//						int code;
//						String msg = "购买失败";
//						if(resultCode==200){
//						//支付操作成功
//							code = 9000;
//							msg = "购买成功";
//						}else if(resultCode == 110){
//						//支付取消
//							code = resultCode;
//							msg = "购买取消";
//						}else{
//						//支付失败
//							code = resultCode;
//							msg = "购买失败";	
//						}
//						try {
//							payArgs.put("code", code + "");
//							payArgs.put("msg", msg);
//							methodsRun.buyDiamondCallBack(payArgs.toString());
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//					}
//					@Override
//					public void onTradeProgress(String goodsKey, String tradeId, int price, int paid, String extra, int resultCode) {
//						Log.i(TAG, "tradeProgress");
//						Log.i(TAG, "tradeProgress" + resultCode);
//						Toast.makeText(m_instance, "onTradeProgress" + extra + goodsKey + price, Toast.LENGTH_SHORT)
//						.show();
//						//扣费成功
//						int code;
//						String msg = "购买失败";
//						if(resultCode==200){
//						//支付操作成功
//							code = 9000;
//							msg = "购买成功";
//						}else if(resultCode == 110){
//						//支付取消
//							code = resultCode;
//							msg = "购买取消";
//						}else{
//						//支付失败
//							code = resultCode;
//							msg = "购买失败";	
//						}
//						try {
//							payArgs.put("code", code + "");
//							payArgs.put("msg", msg);
//							methodsRun.buyDiamondCallBack(payArgs.toString());
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//					}		
//					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});								
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