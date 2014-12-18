package com.zeusky.mm75.pay;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class IAPHandler extends Handler {

	public static final int INIT_FINISH = 10000;
	public static final int BILL_FINISH = 10001;
	public static final int QUERY_FINISH = 10002;
	public static final int UNSUB_FINISH = 10003;

	private Activity context;

	public IAPHandler(Activity context) {
		this.context = context;
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		int what = msg.what;
		switch (what) {
		case INIT_FINISH:
			initShow((String) msg.obj);
			break;
		default:
			break;
		}
	}

	private void initShow(String msg) {
		Toast.makeText(context, "初始化：" + msg, Toast.LENGTH_LONG).show();
	}
	
}