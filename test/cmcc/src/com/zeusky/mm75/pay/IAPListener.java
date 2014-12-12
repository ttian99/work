package com.zeusky.mm75.pay;

import java.util.HashMap;

import org.json.JSONException;

import com.methodsRun.methodsRun;
import com.tendcloud.tenddata.TDGAVirtualCurrency;
import com.zeusky.star.star;

import mm.purchasesdk.OnPurchaseListener;
import mm.purchasesdk.Purchase;
import mm.purchasesdk.core.PurchaseCode;
import android.app.Activity;
import android.os.Message;
import android.util.Log;

public class IAPListener implements OnPurchaseListener {

	private final String TAG = "IAPListener";

	public IAPListener(Activity context, IAPHandler iapHandler) {
	}

	public void onAfterApply() {

	}

	public void onAfterDownload() {

	}

	public void onBeforeApply() {

	}

	public void onBeforeDownload() {

	}

	public void onInitFinish(int code) {
		String result = Purchase.getReason(code);
		Log.d(TAG, result);
	}

	public void onBillingFinish(int code, @SuppressWarnings("rawtypes") HashMap arg1) {

		Log.d(TAG, "billing finish, status code = " + code);
		int returnCode = code;
		String msg = "购买失败";

		if (code == PurchaseCode.ORDER_OK || (code == PurchaseCode.AUTH_OK) || (code == PurchaseCode.WEAK_ORDER_OK)) {
			returnCode = 9000;
			msg = "购买成功";
			
			TDGAVirtualCurrency.onChargeSuccess(MMPay.mOrderId);
		} else {
			returnCode = code;
			msg = "购买失败";
		}

		try {
			MMPay.payArgs.put("code", returnCode + "");
			MMPay.payArgs.put("msg", msg);
			methodsRun.buyDiamondCallBack(MMPay.payArgs.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("rawtypes")
	public void onQueryFinish(int code, HashMap arg1) {
	}

	public void onUnsubscribeFinish(int code) {
	}
}
