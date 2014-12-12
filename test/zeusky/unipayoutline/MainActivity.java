package com.zeusky.unipayoutline;

import com.unicom.dcLoader.Utils;
import com.unicom.dcLoader.Utils.UnipayPayResultListener;
import com.zeusky.star.star;

import android.os.Bundle;
import android.R.layout;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	String[] values = new String[]{
		"001","002","003","004"	
	};

	public static void init(Activity context) {
		//super.onCreate(context);
		Utils.getInstances().initSDK(star.m_instance, 0);
		
	}
	
	
	public static void getPay(){
		
	}

}
