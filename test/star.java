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
import java.util.Random;

import org.cocos2dx.lib.Cocos2dxActivity;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.ShareSDKUtils;



//import com.example.paytest.MainActivity;
//import com.example.paytest.MainActivity;
import com.methodsRun.methodsRun;
//import com.paytest.phonefee.R;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatService;
import com.zywx.myepay.MyEPay;
import com.zywx.myepay.OnDefrayListener;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class star extends Cocos2dxActivity {

	private final String TAG = getClass().getName();
	private Context context;
	public static Handler mHandler = new Handler() {
		public String msg = "测试sdk初始化";
		public void handleMessage(android.os.Message msg) {
			defray(100);
		};
	};
	
//	public static void initpay(){
//		MyEPay pay = MyEPay.getInstance();
//	}
	
	
	public static void defray(int fee) {
		MyEPay pay = MyEPay.getInstance();
		pay.startPay(m_instance, fee, null, getSerial(), null,(OnDefrayListener) m_instance);
	}
	
	public static Activity m_instance = null;
	public static boolean test_version = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// /
		m_instance = this;
		
		StatConfig.setDebugEnable(true);
		StatService.trackCustomEvent(this, "onCreate", "");

		ShareSDKUtils.prepare(); // share sdk
		methodsRun.injectIMEI(Util.getOpenid(this));

		createSharePicDir();

		this.registerReceiver(Util.receiver, new IntentFilter( // 注册下载接收事件
				DownloadManager.ACTION_DOWNLOAD_COMPLETE));

		// Util.openAPK();
		
		  //MyEPay的sdk初始化
//	       MyEPay pay = MyEPay.getInstance();
		Log.d(" sdk 初始化", "--------------------------------？？开始");
		initMyEPay(this);
			
	}

	public static void buyDiamond(final String jsonStr) {
		JSONObject obj;
		try {
			obj = new JSONObject(jsonStr);
			String paycode = obj.getString("itemid");
			Log.d("MMPay", obj.toString());
			Log.d("MMPay", "paycode = " + paycode);
			if (paycode == null) {
				throw new Exception(" pay code error ");
			}
			
			startMyEPay();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void initMyEPay(Activity context){
		MyEPay def = MyEPay.getInstance();
		// 是否开启有提示的sdk初始化z
		def.init(star.m_instance, new OnDefrayListener () {
			
//			public void onDefrayFinished(int paramInt) {
//				defray(100);
//				Log.d(" sdk 初始化", "--------------------------------DefrayFinished2"+paramInt);
//			}
//			public void onInitFinished(int paramInt) {
//			     if(100 == paramInt){
//			        //初始化成功
////			    			Toast.makeText(m_instance, "Init Finished:" + paramInt, Toast.LENGTH_SHORT)
////			    					.show();
//			    			Log.d(" sdk 初始化", "--------------------------------是的成功");
//			    			return;
//			     } else{
//			        //初始化失败
//			    	 Log.d(" sdk 初始化", "--------------------------------？？失败"+paramInt);
////			    	 Toast.makeText(m_instance, "Init Finished:" + paramInt, Toast.LENGTH_SHORT)
////			 		.show();
//			    	 return;
//			     }
//			}
			});
//		
	}
	
	
	
	
	
	
	public static void startMyEPay(){
		MyEPay	 def = MyEPay.getInstance();
		def. startPay(star.m_instance,200, null,"00", null, new OnDefrayListener (){
			public void onDefrayFinished(int paramInt) {
			       if(0 == paramInt){
			           //计费流程执行成功
			    	   Toast.makeText(m_instance, "Defray Finished:" + paramInt, Toast.LENGTH_SHORT)
	    				.show();
			       }else{
			           //计费流程执行失败
			       }
			    }
			    public void onInitFinished(int paramInt) {
			    	
			}
			});
	}
	
//	@Override
	public void onDefrayFinished(int paramInt) {
		Toast.makeText(this, "Defray Finished:" + paramInt, Toast.LENGTH_SHORT)
				.show();
		Log.d("Defray", "there is ------------------defray" + paramInt);
	}

//	@Override
	public void onInitFinished(int paramInt) {
		Toast.makeText(this, "Init Finished:" + paramInt, Toast.LENGTH_SHORT)
				.show();
		Log.d("Defray", "there is ------------------InitFinished" + paramInt);
	}
	
	private static String getSerial() {
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
