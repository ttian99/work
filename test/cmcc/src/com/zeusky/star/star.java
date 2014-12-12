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

import org.cocos2dx.lib.Cocos2dxActivity;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.ShareSDKUtils;

import com.methodsRun.methodsRun;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatService;
import com.tendcloud.tenddata.TDGAAccount;
import com.tendcloud.tenddata.TalkingDataGA;
import com.tendcloud.tenddata.TDGAAccount.AccountType;
import com.zeusky.mm75.pay.MMPay;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class star extends Cocos2dxActivity {

	public static Activity m_instance = null;

	public static boolean test_version = false;
	
	public static TDGAAccount mAccount = TDGAAccount.setAccount("10138728");

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// /
		m_instance = this;

		MMPay.initPay(this);

		StatConfig.setDebugEnable(true);
		StatService.trackCustomEvent(this, "onCreate", "");

		ShareSDKUtils.prepare(); // share sdk
		methodsRun.injectIMEI(Util.getOpenid(this));

		createSharePicDir();

		this.registerReceiver(Util.receiver, new IntentFilter( // 注册下载接收事件
				DownloadManager.ACTION_DOWNLOAD_COMPLETE));

		// Util.openAPK();
		TalkingDataGA.init(this, "A33B469D0AA02A7E15E8128A54E98261", "cmcc");
		
		mAccount=TDGAAccount.setAccount(TalkingDataGA.getDeviceId(this));
    	mAccount.setAccountType(AccountType.ANONYMOUS);
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
		try {
			if (test_version) {
				JSONObject obj = new JSONObject(jsonStr);
				obj.put("code", "9000");
				obj.put("msg", "success");
				methodsRun.buyDiamondCallBack(obj.toString());
			} else {
				star.m_instance.runOnUiThread(new Runnable() {
					public void run() {
						JSONObject obj;
						try {
							obj = new JSONObject(jsonStr);
							MMPay.Pay(obj);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();

			try {
				methodsRun.buyDiamondCallBack(new JSONObject().put("code", "8001").put("msg", "程序内部错误").toString());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
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