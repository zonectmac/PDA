package com.sfcservice.pda;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

import com.sfcservice.log.CrashHandler;

public class App extends Application {
	private static App app;
	private List<Activity> activities = new ArrayList<Activity>();

	@Override
	public void onCreate() {
		super.onCreate();
		app = this;
		// 在这里为应用设置异常处理程序，然后我们的程序才能捕获未处理的异常
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(this);
		System.out.println("================tyy==");
	}

	public static App getAppInstance() {
		return app;
	}

	public void addActivity(Activity a) {
		if (!activities.contains(a)) {
			activities.add(a);
		}
	}

	public void finishActivity() {
		for (Activity activity : activities) {
			if (!activity.isFinishing()) {
				activity.finish();
			}
		}
	}
}
