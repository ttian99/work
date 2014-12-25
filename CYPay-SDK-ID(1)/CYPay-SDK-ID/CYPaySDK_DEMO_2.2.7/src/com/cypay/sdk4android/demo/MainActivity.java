package com.cypay.sdk4android.demo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.cypay.bean.Price;
import com.cypay.errorcode.PaymentErrorCode;
import com.cypay.paysdk.CYPay;
import com.cypay.paysdk.Order;
import com.cypay.paysdk.PaymentResult;

public class MainActivity extends Activity {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
			Locale.US);
	private Button payBtn;

	// CYPAYSDK自动获取设备国家简码，判断一个商品在不同国家对应的货币类型和商品定价 。
	// CYPAY SDK get country code automatically, it can identify an item's price
	// in different countries.

	// 国家简码参数有”DEFAULT”时，会将没有单独定价的国家，选用DEFAULT对应的USD货币类型和订单金额。
	// If DEFAULT exists in country code, countries which has no specific price,
	// will use USD as currency and DEFAULT as country code.
	private String[] countryCodes = { "DEFAULT", "BR", "IQ", "IN", "ID", "TH",
			"RU", "TR", "VN", "MY", "PK", "SA", "EG" };

	private String[] currencyCodes = { "USD", "BRL", "IQD", "INR", "IDR",
			"THB", "RUB", "TRY", "VND", "MYR", "PKR", "SAR", "EGP" };

	private double[] prices = { 0.99, 2.85, 600, 20, 5000, 20, 25, 1, 5000, 1,
			10, 5, 5 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CYPay.init(getApplicationContext());
		payClickView();
	}

	private void payClickView() {
		setContentView(R.layout.test_layout);
		payBtn = (Button) findViewById(R.id.payBtn);

		payBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String orderId = UUID.randomUUID().toString()
						.replaceAll("-", "");

				Order order = new Order();

				/*
				 * Direct carrier billing payment
				 */
				for (int i = 0; i < countryCodes.length; i++) {
					Price priceTmp = new Price();
					priceTmp.setProductName("88 Gems");
					priceTmp.setCountry(countryCodes[i]);
					priceTmp.setCurrency(currencyCodes[i]);
					priceTmp.setAmount(prices[i]);
					order.addPrice(priceTmp);
				}

				order.setOrderID(orderId);
				order.setCpUserId("");
				order.setAppSecret(MainActivity.this.getResources().getString(
						R.string.cypaysdk_app_secret));
				Date curDate = new Date(System.currentTimeMillis());
				String str = sdf.format(curDate);
				order.setCpOrderTime(str);
				CYPay.payWithMobile(MainActivity.this, order);

				/*
				 * Common payment
				 */
				/*
				 * Price priceTmp = new Price();
				 * priceTmp.setProductName("88 Gems");
				 * priceTmp.setCountry("DEFAULT"); priceTmp.setCurrency("USD");
				 * priceTmp.setAmount(0.99); order.addPrice(priceTmp);
				 * CYPay.pay(MainActivity.this, order);
				 */
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CYPay.REQUEST_PAY_CODE) {
			onPaymentResult(resultCode, data);
		}
	}

	private void onPaymentResult(int resultCode, Intent data) {
		if (resultCode == CYPay.RESULT_PAY_CODE_SUCCESS) {
			PaymentResult result = (PaymentResult) data
					.getSerializableExtra(CYPay.EXTRA_PAYMENT_RESULT);
			Toast.makeText(MainActivity.this,
					"Payment Success " + result.getOrder().getProductName(),
					Toast.LENGTH_LONG).show();
		} else if (resultCode == CYPay.RESULT_PAY_CODE_ERROR) {
			PaymentErrorCode errorCode = (PaymentErrorCode) data
					.getSerializableExtra(CYPay.EXTRA_ERROR_CODE);
			Toast.makeText(MainActivity.this,
					"Payment Failed :" + errorCode.toString(),
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
