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
import com.zeusky.unipayoutline.MainActivity;

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

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// /
		m_instance = this;
		
		MainActivity.init(this);
		
		

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