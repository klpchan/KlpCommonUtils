package com.klpchan.commonutils.demo;

import com.klpchan.commonutils.CommonUtilsEnv;

import android.app.Application;

public class MainApplication extends Application{
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		CommonUtilsEnv.init(this);
		
		CommonUtilsEnv.setDebug(true);
		
		CommonUtilsEnv.setCustomePerfix("KlpCommonUtils");
	}
}
