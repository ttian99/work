package com.zeusky.star;

import java.io.File;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.stat.StatService;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class Util {

	private static String TAG = "native-star-util";
	public static Context _context = null;

	public static void openAPK() {

		try {

			JSONObject event = new JSONObject();
			event.put("name", "gameStartup");
			JSONObject args = new JSONObject();
			args.put("key", "mode");
			args.put("value", "normal");

			if (Intent.ACTION_VIEW.equals(star.m_instance.getIntent().getAction())) {
				Log.d(TAG, "open by js -- ");
				Uri uri = star.m_instance.getIntent().getData();
				String mode = uri.getQueryParameter("mode");
				args.put("value", mode);
			} else {
				Log.d(TAG, "open by native -- ");
				args.put("value", "native");
			}
			event.put("args", new JSONArray().put(args));
			Log.d(TAG, event.toString());
			commitEventTo(event.toString());

		} catch (Exception e) {

		}
	}

	public static void deleteDIR(String dir) {
		Log.d(TAG, "start delete dir" + dir);
		deleteDIR(new File(dir));
	}

	private static void deleteDIR(File file) {
		if (file.isFile()) {
			file.delete();
			Log.d(TAG, "delete file " + file.getPath());
			return;
		}

		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				deleteDIR(childFiles[i]);
			}
			file.delete();
		}
	}

	/**
	 * get openid
	 */
	private static String uniqueID = null;
	private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

	public synchronized static String getOpenid(Context context) {

		String imei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId(); // 先获取imei
		if (!imei.equals(null))
			return imei;

		if (uniqueID == null) {
			SharedPreferences sharedPrefs = context.getSharedPreferences(PREF_UNIQUE_ID, Context.MODE_PRIVATE);
			uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
			if (uniqueID == null) {

				uniqueID = UUID.randomUUID().toString();
				Editor editor = sharedPrefs.edit();
				editor.putString(PREF_UNIQUE_ID, uniqueID);
				editor.commit();
			}
		}

		return uniqueID;
	}

	/**
	 * down load new version
	 */
	private static long __downloadID = 0;
	private static DownloadManager downloadManager = (DownloadManager) star.m_instance.getSystemService(Context.DOWNLOAD_SERVICE);
	public static BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, " -- down load success --" + __downloadID + " " + intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0));

			if (__downloadID == intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)) {

				Query myDownloadQuery = new Query();
				myDownloadQuery.setFilterById(__downloadID);

				Cursor myDownload = downloadManager.query(myDownloadQuery);
				if (myDownload.moveToFirst()) {

					int fileUriIdx = myDownload.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);

					String fileUri = myDownload.getString(fileUriIdx);

					Log.d(TAG, " download file uri " + fileUri);
					Intent installIntent = new Intent();
					installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					installIntent.setAction(android.content.Intent.ACTION_VIEW);
					installIntent.setDataAndType(Uri.parse(fileUri), "application/vnd.android.package-archive");
					star.m_instance.startActivity(installIntent);
				}
				myDownload.close();

			}
		}
	};

	public static String downloadApkAndInstall(String args) {
		try {
			final JSONObject obj = new JSONObject(args);

			star.m_instance.runOnUiThread(new Runnable() {
				public void run() {
					try {

						Request req = new Request(Uri.parse(obj.getString("url")));
						String destDir = "zeusky.popstar";
						String destFileName = "star" + new Date(System.currentTimeMillis()).getTime() + ".apk";

						req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
						req.setTitle("来消星星的你");
						req.setDescription("正在下载来消星星的你最新版");
						Log.d(TAG, " start download  " + destDir + destFileName);
						req.setDestinationInExternalPublicDir(destDir, destFileName);

						__downloadID = downloadManager.enqueue(req);
						Toast.makeText(star.m_instance, "开始下载更新包", Toast.LENGTH_LONG).show();

					} catch (Exception e) {

					}
				}
			});

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return "{}";

	}

	public static String commitEventTo(String args) {
		if (star.test_version) // 。。。
			return "{}";

		try {
			JSONObject obj = new JSONObject(args);
			String eventName = obj.getString("name");
			JSONArray eventArgs = obj.getJSONArray("args");
			Properties prop = new Properties();
			for (int i = 0; i < eventArgs.length(); i++) {
				String key = eventArgs.getJSONObject(i).getString("key");
				String value = eventArgs.getJSONObject(i).getString("value");
				prop.put(key, value);
			}

			StatService.trackCustomKVEvent(star.m_instance, eventName, prop);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return "{}";
	}

	public static String exitDialog(String args) {

		try {
			final JSONObject obj = new JSONObject(args);
			Log.d(TAG, obj.toString());

			star.m_instance.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						new AlertDialog.Builder(star.m_instance).setTitle(obj.getString("title"))
								.setPositiveButton(obj.getString("sure"), new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										System.exit(0);
									}
								})

								.setNegativeButton(obj.getString("cancel"), new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										Log.d(TAG, " you cancel exit dialog !");
									}
								}).show();
					} catch (JSONException e) {
						e.printStackTrace();
					
					}
				
				}
			});
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return "{}";
	}

	public static String getBaseVersion() {
		try {
			PackageManager manager = star.m_instance.getPackageManager();
			PackageInfo info = manager.getPackageInfo(star.m_instance.getPackageName(), 0);
			String version = info.versionName;
			Log.d(TAG, "getBase Version from java , version = " + version);
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "2.0.0";
		}

	}
	
}
