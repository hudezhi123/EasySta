package easyway.Mobile.util;

import android.app.Activity;
import android.view.WindowManager;

public class ViewUtil {
	/** 
     * 设置添加屏幕的背景透明度 
     * @param bgAlpha 
     */  
    public static void backgroundAlpha(Activity activity,float bgAlpha)  
    {  
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();  
        lp.alpha = bgAlpha; //0.0-1.0  
        activity.getWindow().setAttributes(lp);  
    }  
}
