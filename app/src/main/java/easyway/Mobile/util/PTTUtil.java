package easyway.Mobile.util;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;

public class PTTUtil {
	public static final String SIPUA_PACKAGE_NAME = "com.zed3.sipua";
	public static final String XCHAT_PACKAGE_NAME = "com.easyway.interphone";

	public static final String WPSOffice_Package_Name = "cn.wps.moffice_eng";
	
	public static final String ACTION_LOGIN = "com.zed3.sipua.login";
	public static final String ACTION_LOGOUT = "com.zed3.sipua.logout";
	public static final String ACTION_CONTACT_DELETE_ALL = "com.zed3.sipua.contact_deleteall";
	public static final String ACTION_CONTACT_UPDATE = "com.zed3.sipua.contact_update";
	public static final String ACTION_CALL = "com.zed3.sipua.call";
	 public static final String ACTION_SWITCH_CAMERA = "com.zed3.sipua.settings.switch_camera";
	
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String NUMBER = "number";
	public static final String PROXY = "proxy";
	public static final String PORT = "port";
	public static final String NEW_CONTACTS = "contacts";
	public static final String CALL_TYPE = "call_type";
	public static final int AUDIO_CALL = 1;
	public static final int VIDEO_CALL = 2;
	
	public static final String CAMERA_TYPE = "camera_type";
	public static final int CAMERA_FRONT = 1;
	public static final int CAMERA_BACK = 2;


	// 登录
	public static void Login(Context ctx, String username, String password, String proxy, String port) {
		LogUtil.i("proxy : port -->" +  proxy + ":" + port);
		
		Intent intent = new Intent();
		intent.putExtra(USERNAME, username);
		intent.putExtra(PASSWORD, password);
		intent.putExtra(PROXY, proxy);
		intent.putExtra(PORT, port);
		intent.setAction(ACTION_LOGIN);
		ctx.sendBroadcast(intent);
		
		LogUtil.i("PTT login " + username);
	}
	
	// 登出
	public static void Logout(Context ctx) {
		Intent intent = new Intent();
		intent.setAction(ACTION_LOGOUT);
		ctx.sendBroadcast(intent);
		
		LogUtil.i("PTT logout");
	}
	
	// 同步联系人
	public static void updateContact(Context ctx, ArrayList<CharSequence> contacts) {
		Intent intent = new Intent();
		intent.putCharSequenceArrayListExtra(NEW_CONTACTS, contacts);
		intent.setAction(ACTION_CONTACT_UPDATE);
		ctx.sendBroadcast(intent);
		
		LogUtil.i("PTT update contacts");
	}
	
	// 拨打电话
	public static void call(Context ctx, String number, int type) {
		Intent intent = new Intent();
		intent.putExtra(NUMBER, number);
		intent.putExtra(CALL_TYPE, type);
		intent.setAction(ACTION_CALL);
		ctx.sendBroadcast(intent);
		
		LogUtil.i("PTT call " + number + " by " + type);
	}

	// 选择摄像头
	public static void SwitchCamera(Context ctx, int type) {
		Intent intent = new Intent();
		intent.putExtra(CAMERA_TYPE, type);
		intent.setAction(ACTION_SWITCH_CAMERA);
		ctx.sendBroadcast(intent);
		
		LogUtil.i("PTT SwitchCamera " + type);
	}
}
