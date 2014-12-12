package com.zeusky.mm75.pay;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.methodsRun.methodsRun;
import com.tendcloud.tenddata.TDGAVirtualCurrency;
import com.tendcloud.tenddata.TalkingDataGA;
import com.zeusky.star.star;

import mm.purchasesdk.Purchase;
import android.app.Activity;
import android.util.Log;

public class MMPay {

	public static Purchase purchase = null;
	private static IAPListener mListener = null;

	private static final String APPID = "300008774296";
	private static final String APPKEY = "E7E18A20B6C021A2C6E4DCB58A6C1DE3";
	public static JSONObject payArgs = null;
	public static String mOrderId = null;

	private static HashMap<String, String> payCodeDic = null;

	public static void initPay(Activity context) {

		IAPHandler iapHandler = new IAPHandler(context);

		mListener = new IAPListener(context, iapHandler);
		purchase = Purchase.getInstance();

		try {
			purchase.setAppInfo(APPID, APPKEY);
			purchase.init(context, mListener);

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		
		payCodeDic = new HashMap<String, String>();

		for (int i = 1; i <= 18; i++) {
				
				if (i < 10)
					payCodeDic.put(i + "", "3000087742960" + (i) + "");
				else
					payCodeDic.put(i + "", "300008774296" + (i) + "");
		}
		
		
		mOrderId = TalkingDataGA.getDeviceId(context) + "-" + System.currentTimeMillis() ;
	}

	public static void Pay(JSONObject obj) throws Exception {
		payArgs = obj;
		String paycode = payCodeDic.get(obj.getString("itemid"));
		Log.d("MMPay", obj.toString());
		Log.d("MMPay", "paycode = " + paycode);
		if (paycode == null) {
			throw new Exception(" pay code error ");
		}
		TDGAVirtualCurrency.onChargeRequest(mOrderId, obj.getString("subject"), Integer.parseInt(obj.getString("total")) , "CNY", Integer.parseInt(obj.getString("total")) , methodsRun.getPlatform());
		
		purchase.order(star.m_instance, paycode, mListener);
	}
}