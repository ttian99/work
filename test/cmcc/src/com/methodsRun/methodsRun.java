package com.methodsRun;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zeusky.star.Util;
import com.zeusky.star.star;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;


public class methodsRun{
	private static String TAG = "methodsRun";
	
	
	public static void deleteDIR(String dir) {
		Util.deleteDIR(dir);
	}
	
	
	public static void buyDiamond(final String jsonStr){
		star.buyDiamond(jsonStr);
	}
	
	public static void showAd() {
		
	}
	
	public static String getBaseVersion(){
		return Util.getBaseVersion();
	}
	
	public static String commitEventTo(String args){
		return Util.commitEventTo(args);
	}
	
	public static String downloadApkAndInstall(String args) {
		return Util.downloadApkAndInstall(args);
	}
	
	public static String exitDialog(String args) {
		return Util.exitDialog(args);
	}
	
	public static String getPlatform(){
		Context context = star.m_instance;
		Bundle metaData = null;
   	 	String  metaValue = "0";
   	 	if (context == null) {
   	 		return "0";
   	 	}
   	 	try {
   	 		ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
   	 		if (null != ai) {
   	 			metaData = ai.metaData;
  	 		}
   	 		if (null != metaData) {
   	 			String str = "InstallChannel";
   	 			metaValue = metaData.getString(str);
   	 			System.out.println("*********Platform:************"+ metaValue);
   	 		}
   	 	} catch (NameNotFoundException e) {
   	 		return "0";
   	 	}
   	 	return metaValue;
	}
	
	public static String getChannelID(){
		Context context = star.m_instance;
		Bundle metaData = null;
   	 	String  metaValue = "0";
   	 	if (context == null) {
   	 		return "0";
   	 	}
   	 	try {
   	 		ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
   	 		if (null != ai) {
   	 			metaData = ai.metaData;
  	 		}
   	 		if (null != metaData) {
   	 			String str = "CHANNELID";
   	 			metaValue = metaData.getString(str);
   	 			System.out.println(TAG+"*********channel_id:************"+ metaValue);
   	 		}
   	 	} catch (NameNotFoundException e) {
   	 		return "0";
   	 	}
   	 	return metaValue;
	}
	
	public static String language="chinese";
	public static String getLanguage(){
		return language;
	}
	
	public static String getMethodReturn(final String methodName , final JSONArray argv) throws JSONException{
		String str = "undefined";
		String ret = "undefined";
		String desc = "undefined";
		String value = "undefined";
		if ( methodName.equals("getPlatform") ){
			value = getPlatform();
			ret   = "0";
			desc  = "success";
		}else if ( methodName.equals("getChannelID") ){
			value = getChannelID();
			ret   = "0";
			desc  = "success";
		}else if ( methodName.equals("getLanguage") ){
			value = getLanguage();
			ret   = "0";
			desc  = "success";
		}else{
			ret   = "1";
			desc  = "not found the method:" + methodName;
		}
		
		JSONObject obj = new JSONObject();
		obj.put("cmd", methodName);
		obj.put("ret", ret);
		obj.put("value", value);
		obj.put("desc", desc);
		
		str = obj.toString();
		
		return str;
	}
	
	//the method is js cmd get java method
	public static String getCommonValue(final String jsonStr){
		String str = "undefined";
		try{
			JSONObject obj = new JSONObject(jsonStr);
			String cmd  = obj.getString("cmd");
			JSONArray array = obj.getJSONArray("argv");
			
			str = getMethodReturn(cmd,array);
		}catch(Exception e){
			e.printStackTrace();
			str = "stack error!";
		}
		
		return str;
	}
	
	public native static void injectIMEI(String imei);

	public native static void buyDiamondCallBack(String backData);

	public native static void injectOtherDir(String path);
}