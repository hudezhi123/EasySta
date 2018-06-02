package easyway.Mobile.util;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Created by admin on 2016/6/3.
 */
public class MyUtils {
    public static void LogI(Class clazz, String str){
        Log.i(clazz.getSimpleName(), str);
    }

    public static void LogE(Class clazz, String str){
        Log.e(clazz.getClass().getSimpleName(), str);
    }

    public static void LogD(Class clazz, String str){
        Log.d(clazz.getClass().getSimpleName(), str);
    }

    public static int dp2px(Context context, int dp){
        float scale = context.getResources().getDisplayMetrics().density;
        return  (int) (dp*scale + 0.5f);
    }

    public static int px2dp(Context context, int px){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px/scale + 0.5f);
    }
    
    public static int getStatusBarHeight(Context context){
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        } 
        return statusBarHeight;
    }
    
    public static int getTitleBarHeight(View view){
    	int width = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);

		int height = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);

		view.measure(width, height);
		int titleHeight = view.getMeasuredHeight();
		return titleHeight;
    }
    
    public static int getScreenHeight(Activity context){
    	DisplayMetrics sm = new DisplayMetrics();
    	context.getWindowManager().getDefaultDisplay().getMetrics(sm);
		int screenHeight = sm.heightPixels;
		return screenHeight;
    }
    
    public static int getScreenWide(Activity context){
    	DisplayMetrics wd = new DisplayMetrics();
    	context.getWindowManager().getDefaultDisplay().getMetrics(wd);
    	int screenWide = wd.widthPixels;
    	return screenWide;
    }
    
   public static String getSdkPaht(){
	   File sdDir = null;
	   boolean sdCardExist = Environment.getExternalStorageState()
			   .equals(Environment.MEDIA_MOUNTED);
	   
	   if(sdCardExist){
		   sdDir = Environment.getExternalStorageDirectory();
	   }
	   return sdDir.toString();
   }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     * @param context
     * @return true 表示开启
     */
    public static final boolean isOPen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }

        return false;
    }

    /**
     * 强制帮用户打开GPS
     * @param context
     */
    public static final void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

}
