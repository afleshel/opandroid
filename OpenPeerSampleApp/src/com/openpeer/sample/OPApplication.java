package com.openpeer.sample;

import android.app.Application;
import android.content.Context;

import com.openpeer.sdk.app.OPHelper;

public class OPApplication extends Application {
	private static OPApplication instance;
	static {
		try {
			System.loadLibrary("z_shared");
			System.loadLibrary("openpeer");

		} catch (UnsatisfiedLinkError use) {
			use.printStackTrace();
		}
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance = this;
		OPHelper.getInstance().init(this, null);
		OPHelper.getInstance().setChatGroupMode(OPHelper.MODE_CONTACTS_BASED);
		OPSessionManager.getInstance().init();
	}


	public static Context getInstance() {
		// TODO Auto-generated method stub
		return instance;
	}

}
