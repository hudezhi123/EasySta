package easyway.Mobile.util;

import android.content.Context;
import android.content.pm.PackageManager;

public class HomeKey {
	private final static String LAUNCHER_PKG_NAME = "com.android.launcher";
	public static boolean work = false;
	public static boolean disableHome = true; 
    public static final int  FLAG_HOMEKEY_DISPATCHED = 0x80000000;
	/**
	 * enable the app , max excute count times
	 * 
	 * @param ctx
	 * @param pName
	 * @param enable
	 * @param count
	 * @return
	 */
	public static boolean enableLauncher(Context ctx, boolean enable) {
		boolean exctRslt = false;
		
		if (work) {
			disableHome = enable;
			int count = 5;
			for (int j = 0; j < count; j++) {
				if (enableApp(ctx, LAUNCHER_PKG_NAME, enable)) {
					exctRslt = true;
					break;
				}
			}
		}
		
		return exctRslt;
	}
	
    /**
     * enable/disable a app by package name
     * @param ctx
     * @param pName	
     * @param enable ture will be enable a app, false will be disable a app	
     * @describe enable/disable some application
     */
    private static boolean enableApp(Context ctx, String pName, boolean enable){
    	boolean rslt = false;
    	try {
	    	PackageManager pm = ctx.getPackageManager();
	    	int targetState;
			if (enable) {
				targetState = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
			}else{
				targetState = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
			}
			pm.setApplicationEnabledSetting(pName, targetState, PackageManager.DONT_KILL_APP);
			
			int newState = pm.getApplicationEnabledSetting(pName);
			LogUtil.i("app: " + pName + "-->" + newState);
			if (newState == targetState) {
				rslt = true;
			}
    	} catch (Exception e) {
    		e.printStackTrace();
		}
    	
		return rslt;
    }
}
