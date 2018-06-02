package easyway.Mobile.util;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.Data.IntentName;
import easyway.Mobile.Data.MessageType;
import easyway.Mobile.Application.ExitApplication;
import easyway.Mobile.MainFramework;
import easyway.Mobile.R;
import easyway.Mobile.Data.ZWTMessage;
import easyway.Mobile.LockScreenShow.ImMessage;
import easyway.Mobile.LockScreenShow.ShowMessage;
import easyway.Mobile.Message.MessageList;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.Task.TaskTabActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;


/**
 * Polling service
 *
 * @Author Ryan
 * @Create 2013-7-13 涓婂崍10:18:44
 */

public class PollingService extends Service {

    public static final String ACTION = "easyway.Mobile.util.PollingService";

    private Notification mNotification;
    private NotificationManager mManager;
    private final int MSG_SHOW_MSGNUM = 5; // 显示未读短消息数量
    private final int MSG_GETMSG_FAIL = 7; // 打开子模块
    static Handler myHandler;
    private ImMessage mlis;
    private static final int PLAY_VOICE = 1;

    private SoundPool voicePool = null;
    private int SoundId = 0;
    private Context mContext;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mlis = ExitApplication.getInstance().getmLisner();
        initNotifiManager();
        myHandler = new MainFramework().getMyhandle();
        voicePool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        mContext = getApplicationContext();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        new PollingThread().start();
        new PollingThreadAll().start();
    }

    private void initNotifiManager() {
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }


    @SuppressWarnings("deprecation")
    private void showNotificationMsG(int num, int dfMsgNum, int unreadNum) {
        Intent i = new Intent(this, MessageList.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_ONE_SHOT);
        mNotification = new Notification.Builder(this).
                setContentTitle(getResources().getString(R.string.app_name)).
                setContentText("您收到" + String.valueOf(num) + "条短信,请注意查收").
                setContentIntent(pendingIntent).
                setTicker("有新消息").
                setSmallIcon(R.drawable.icon).
                setDefaults(Notification.DEFAULT_ALL).
                setWhen(System.currentTimeMillis()).
                setDefaults(Notification.FLAG_AUTO_CANCEL).
                build();
        mManager.notify(0, mNotification);
        PowerManager pm = (PowerManager) this
                .getSystemService(Context.POWER_SERVICE);
        if (!pm.isScreenOn()) {
            Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
            vib.vibrate(1000);
            PowerManager.WakeLock wl = pm.newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP
                            | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wl.acquire();
            wl.release();
        }
        if (voicePool != null) {
            voicePool.play(SoundId, 1, 1, 0, 0, 1);
        }
        if (num > 0) {
            Intent alarmIntent = new Intent(this, ShowMessage.class);
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            alarmIntent.putExtra("WhereAreYouFrom", 1);
            alarmIntent.putExtra("MessageNum", num);
            alarmIntent.putExtra("dfMsgNum", dfMsgNum);
            startActivity(alarmIntent);
        }
    }


    /**
     * Polling thread
     *
     * @Author Ryan
     * @Create 2013-7-13 涓婂崍10:18:34
     */
    int count = 0;

    class PollingThread extends Thread {
        @Override
        public void run() {
            SharedPreferences sp = getSharedPreferences(CommonFunc.CONFIG, 0);
            String sessionId = sp.getString(CommonFunc.CONFIG_SessionId, "");
            if (sessionId != null) {
                if (sessionId.length() >= 16) {
                    SoundId = voicePool.load(mContext, R.raw.message_arrive, 100);
                    GetUnReadedMessage();
                }
            }


        }
    }

    class PollingThreadAll extends Thread {
        @Override
        public void run() {
            SharedPreferences sp = getSharedPreferences(CommonFunc.CONFIG, 0);
            String sessionId = sp.getString(CommonFunc.CONFIG_SessionId, "");
            if (sessionId != null) {
                if (sessionId.length() >= 16) {
                    SoundId = voicePool.load(mContext, R.raw.message_arrive, 100);
                    GetAllNotify();
                }
            }
        }

    }

    @SuppressWarnings("deprecation")
    private void showNotificationtask(String Msg) {
        Intent i = new Intent(this, TaskTabActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i,
                PendingIntent.FLAG_ONE_SHOT);
        mNotification = new Notification.Builder(this).
                setContentTitle(getResources().getString(R.string.app_name)).
                setContentText("" + Msg).
                setContentIntent(pendingIntent).
                setTicker("有新消息").
                setSmallIcon(R.drawable.icon).
                setDefaults(Notification.DEFAULT_ALL).
                setWhen(System.currentTimeMillis()).
                setDefaults(Notification.FLAG_AUTO_CANCEL).
                build();
        mManager.notify(0, mNotification);
        mlis.SetMsg(Msg + "");
        PowerManager pm = (PowerManager) this
                .getSystemService(Context.POWER_SERVICE);
        if (!pm.isScreenOn()) {
            Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
            vib.vibrate(1000);
            if (voicePool != null) {
                voicePool.play(SoundId, 1, 1, 0, 0, 1);
            }
            PowerManager.WakeLock wl = pm.newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP
                            | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wl.acquire();
            wl.release();
            Intent alarmIntent = new Intent(this, ShowMessage.class);
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            alarmIntent.putExtra("WhereAreYouFrom", 1);
            startActivity(alarmIntent);
        } else {
            if (voicePool != null) {
                voicePool.play(SoundId, 1, 1, 0, 0, 1);
            }
            Intent alarmIntent = new Intent(this, ShowMessage.class);
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(alarmIntent);
        }
    }

    private void GetAllNotify() {
        SharedPreferences sp = getSharedPreferences(CommonFunc.CONFIG, 0);
        String SessionId = sp.getString(CommonFunc.CONFIG_SessionId, "");
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", SessionId);
        parmValues.put("sourceTable", "");
        parmValues.put("limit", "20");
        String methodPath = Constant.MP_Notify;
        String methodName = Constant.MN_GET_ALL;
        WebServiceManager webServiceManager = new WebServiceManager(this,
                methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        if (result == null || result.equals("")) {
            if (myHandler != null) {

            } else {
                return;
            }

        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
                String Msg = null;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
                    Msg = JsonUtil.GetJsonObjStringValue(jsonObj, "Msg");
                }
                int total = JsonUtil.GetJsonInt(result, "Total");
                if (jsonArray.length() > 0) {
                    showNotificationtask(Msg);
                }

                break;
            case Constant.EXCEPTION:
            default:
                break;
        }

    }

    // 获取用户离线时未接收的消息条数
    private void GetUnReadedMessage() {
        SharedPreferences sp = getSharedPreferences(CommonFunc.CONFIG, 0);
        String SessionId = sp.getString(CommonFunc.CONFIG_SessionId, "");
        long StaffId = sp.getLong(CommonFunc.CONFIG_StaffId, -1000);
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", SessionId);
        parmValues.put("staffId", Long.toString(StaffId));
        String methodPath = Constant.MP_SMS;
        String methodName = Constant.MN_GET_UNREAD_MESSAGE;
        WebServiceManager webServiceManager = new WebServiceManager(this,
                methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        if (result == null || result.equals("")) {
            if (myHandler != null) {
                myHandler.sendEmptyMessage(MSG_GETMSG_FAIL);
            } else {
                return;
            }
        }
        int dfMsgNum = 0;
        int unreadNum = 0;
        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
                    ZWTMessage tbZwtMessage = new ZWTMessage();
                    tbZwtMessage.createTime = JsonUtil.GetJsonObjStringValue(
                            jsonObj, "CreateTime");
                    tbZwtMessage.Id = JsonUtil
                            .GetJsonObjLongValue(jsonObj, "Id");
                    tbZwtMessage.MsgId = JsonUtil
                            .GetJsonObjLongValue(jsonObj, "MId");
                    tbZwtMessage.content = JsonUtil.GetJsonObjStringValue(jsonObj,
                            "Context");
                    tbZwtMessage.ownerId = JsonUtil.GetJsonObjLongValue(jsonObj,
                            "UId");
                    tbZwtMessage.ownerName = JsonUtil.GetJsonObjStringValue(
                            jsonObj, "UName");
                    tbZwtMessage.attach = JsonUtil.GetJsonObjStringValue(jsonObj,
                            "AttachList");

                    // 0 在本地代表未读，1代表已读
                    tbZwtMessage.status = 0;
                    tbZwtMessage.contactId = JsonUtil.GetJsonObjLongValue(jsonObj,
                            "OpId");
                    tbZwtMessage.contactName = JsonUtil.GetJsonObjStringValue(
                            jsonObj, "OpName");
                    tbZwtMessage.receipt = JsonUtil.GetJsonObjStringValue(jsonObj,
                            "Receipt");
                    tbZwtMessage.type = JsonUtil.GetJsonObjIntValue(jsonObj,
                            "MessageArea");
                    if (tbZwtMessage.type == MessageType.TYPE_DF_NOTICE) {
                        dfMsgNum++;
                    }
                    if (tbZwtMessage.type == MessageType.TYPE_NORMAL) {
                        unreadNum++;
                    }
                    ZWTMessage.Insert(this, tbZwtMessage);
                }
                if (unreadNum > 0) {
                    ExitApplication.UnReadNum = unreadNum;
                    Intent intent = new Intent();
                    intent.setAction(IntentName.NUM_NOTIFY_RECEIVER);
                    intent.putExtra("Num", unreadNum);
                    sendBroadcast(intent);
                }
                if (jsonArray.length() > 0) {
                    showNotificationMsG(jsonArray.length(), dfMsgNum, unreadNum);
                }

                break;
            case Constant.EXCEPTION:
            default:
                break;
        }

        if (myHandler != null) {
            myHandler.sendEmptyMessage(MSG_SHOW_MSGNUM);
        } else {
            return;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Service:onDestroy");
    }

}
