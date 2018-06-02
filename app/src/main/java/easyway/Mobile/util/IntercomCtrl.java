package easyway.Mobile.util;

import java.util.List;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class IntercomCtrl {
	public static final String INTERCOM_SERVICE = "com.dfl.ptt.PttService";
//	public static final int INTERCOM_WAIT_TIME = 3000; // 3000ms
	private static boolean isWorked = false;

	// 关闭对讲机
	public static boolean close_intercom(Context ctx) {
		isWorked = isWorked(ctx);
		if (isWorked) {
			int op = 1;
			ComponentName componentName = new ComponentName("com.dfl.ptt", "com.dfl.ptt.PttService");
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putInt("op", op);
			intent.putExtras(bundle);
			intent.setComponent(componentName);
			ctx.stopService(intent);

			LogUtil.i("close com.dfl.ptt.PttService");
		}

		return isWorked;
	}

	// 打开对讲机
	public static void open_intercom(Context ctx) {
		if (isWorked) {

			int op = 1;
			ComponentName componentName = new ComponentName("com.dfl.ptt", "com.dfl.ptt.PttService");
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putInt("op", op);
			intent.putExtras(bundle);
			intent.setComponent(componentName);
			ctx.startService(intent);

			LogUtil.i("open com.dfl.ptt.PttService");
		}
	}

	// 获取对讲机状态
	public static boolean isWorked(Context ctx) {
		ActivityManager myManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = myManager.getRunningServices(Integer.MAX_VALUE);

		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().toString().equals("com.dfl.ptt.PttService")) {
				return false;
			}
		}
		return true;
	}

}
