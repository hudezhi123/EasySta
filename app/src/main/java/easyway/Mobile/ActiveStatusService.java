package easyway.Mobile;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.CommonFunc;

import static android.content.ContentValues.TAG;

/**
 * Created by xiaomenghua on 2017/2/14.
 */

public class ActiveStatusService extends Service{

    private Timer timer;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (timer == null)
           timer = new Timer();
    }

    class RequestTimerTask extends TimerTask {
        public void run() {
            Log.d(TAG,"timer on schedule");
            new Thread(){
                public void run() {
                    activityStatus();
                };
            }.start();
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        timer.schedule(new RequestTimerTask(), 0, 180000);
    }

    private void activityStatus(){
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        String ip = CommonFunc.getLocalIpAddress();
        parmValues.put("ipAddress", CommonFunc.getLocalIpAddress());
        parmValues.put ("mac",CommonFunc.GetMacAddr(this));
        String methodPath = Constant.MP_ISTATIONSERVICE;
        String methodName = Constant.MN_GET_ACTIVITYSTATUS;
        WebServiceManager webServiceManager = new WebServiceManager(this, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        Gson gson = new Gson();
//        StationCodeResult mResult = gson.fromJson(result, StationCodeResult.class);
//        int code = mResult.getCode();
//        if (code == 1000) {
//                myhandle.sendEmptyMessage(Msg_GetAllStationCode_ok);
//            }else{
//                myhandle.sendEmptyMessage(Msg_GetAllStationCode_isNull);
//            }
//        }else{
//            myhandle.sendEmptyMessage(Msg_GetAllStationCode_isFail);
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null){
            timer.cancel();
            timer = null;
        }
    }
}
