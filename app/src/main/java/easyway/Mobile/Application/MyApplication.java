package easyway.Mobile.Application;

import android.app.Application;

import easyway.Mobile.util.CrashHandler;

/**
 * Created by shsl on 2017/1/10.
 */

public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crash = CrashHandler.getInstance();
        crash.init(this);
    }


}
