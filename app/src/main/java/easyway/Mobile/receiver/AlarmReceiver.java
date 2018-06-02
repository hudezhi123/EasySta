package easyway.Mobile.receiver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import easyway.Mobile.Application.ExitApplication;
import easyway.Mobile.Caution.Caution;
import easyway.Mobile.Caution.CautionDetailActivity;
import easyway.Mobile.Caution.CautionEditActivity;
import easyway.Mobile.Data.IntentName;
import easyway.Mobile.Data.MessageType;
import easyway.Mobile.Data.ZWTMessage;
import easyway.Mobile.LockScreenShow.ShowMessage;
import easyway.Mobile.Message.MessageList;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.CommonFunc;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.LogUtil;

public class AlarmReceiver extends BroadcastReceiver {
    private static Context mctx;
    private static boolean bool = false; // 心跳包30s一次，短消息检测60s一次
    private ArrayList<Long> contactIds = new ArrayList<Long>();

    public static final String ACTION_NEW_MESSAGE = "android.intent.action.setMessageNum";
    private SoundPool voicePool = null;
    private int SoundId = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        mctx = context;
        voicePool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        if (Property.SessionId != null && !Property.SessionId.equals("")) {
            new Thread() {
                public void run() {
                    SoundId = voicePool.load(mctx, R.raw.message_arrive, 50);
                    SyncUserStatus(mctx); // 心跳包
                }
            }.start();

            if (bool) {
                new Thread() {
                    public void run() {
                        SoundId = voicePool.load(mctx, R.raw.message_arrive, 50);
//                        getUnReadMessgae(mctx); // 获取未读短消息
                        Backlog();        //获取待办事项的数量
                        ArrayList<Caution> cautions = Caution.LocalLoad(mctx);        //记事本提醒
                        showCautionNotify(mctx, cautions);
                    }
                }.start();

            }
        }
        bool = !bool;
    }

    // 事件提示
    protected void showCautionNotify(Context context,
                                     ArrayList<Caution> cautions) {
        if (cautions == null || cautions.size() == 0)
            return;
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Caution caution : cautions) {
            NotificationManager mNotificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            Intent intent = new Intent(context, CautionDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(CautionEditActivity.KEY_CAUTIONID, caution.ID);
            PendingIntent contentIntent = PendingIntent.getActivity(context, (int) caution.ID,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification builder = new Notification.Builder(context).
                    setContentTitle(caution.title).
                    setContentText(caution.content).
                    setContentIntent(contentIntent).
                    setSmallIcon(R.drawable.icon).
                    setDefaults(Notification.DEFAULT_ALL).
                    setWhen(System.currentTimeMillis()).
                    setDefaults(Notification.FLAG_AUTO_CANCEL).
                    build();
            mNotificationManager.notify(1, builder);
            PowerManager pm = (PowerManager) mctx.getSystemService(Context.POWER_SERVICE);
            if (pm.isScreenOn()) {
                if (voicePool != null) {
                    voicePool.play(SoundId, 1, 1, 0, 0, 1);
                }
                Intent alarmIntent = new Intent(mctx, ShowMessage.class);
                alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                alarmIntent.putExtra("WhereAreYouFrom", 2);
                alarmIntent.putExtra(CautionEditActivity.KEY_CAUTIONID, caution.ID);
                mctx.startActivity(alarmIntent);
            } else {
                Vibrator vib = (Vibrator) mctx.getSystemService(Service.VIBRATOR_SERVICE);
                vib.vibrate(1000);
                if (voicePool != null) {
                    voicePool.play(SoundId, 1, 1, 0, 0, 1);
                }
                PowerManager.WakeLock wl = pm.newWakeLock(
                        PowerManager.ACQUIRE_CAUSES_WAKEUP
                                | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
                wl.acquire();
                wl.release();
                // 设置通知跳转目标
                Intent alarmIntent = new Intent(mctx, ShowMessage.class);
                alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                alarmIntent.putExtra("WhereAreYouFrom", 2);
                alarmIntent.putExtra(CautionEditActivity.KEY_CAUTIONID, caution.ID);
                mctx.startActivity(alarmIntent);
            }

        }
    }

    // 发送心跳包
    private void SyncUserStatus(Context context) {
        String sessionId = Property.SessionId;
        if (sessionId == "" || sessionId == null) {
            return;
        }

        String ipAddress = CommonFunc.getLocalIpAddress();
        if (ipAddress.equals("0.0.0.0")) {
            return;
        }

        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("ipAddress", ipAddress);
        parmValues.put("sessionId", sessionId);

        String methodPath = Constant.MP_ISTATIONSERVICE;
        String methodName = Constant.MN_ACTIVE_STATUS;
        WebServiceManager webServiceManager = new WebServiceManager(context,
                methodName, parmValues);
        webServiceManager.OpenConnect(methodPath);
    }

    //TODO 获取未读取短消息数量
    private void getUnReadMessgae(Context context) {
        contactIds.clear();
        int mNum = getNewMsg(context);
        if (mNum != 0) { // 有新短消息
            Intent intent = new Intent();
            intent.putExtra("mNum", Integer.valueOf(mNum));
            int mBacklogNum = ExitApplication.getInstance().getmBacklog().getNum();
            intent.putExtra("mBacklogNum", mBacklogNum);
            String strIds = "";
            for (int i = 0; i < contactIds.size(); i++) {
                if (i == 0)
                    strIds += contactIds.get(i);
                else
                    strIds += ";" + contactIds.get(i);
            }
            intent.putExtra("contactIds", strIds);
            intent.setAction(ACTION_NEW_MESSAGE);
            context.sendBroadcast(intent);
            showNotify(context, mNum); // 在通知栏中提示用户有新消息
        }
    }


    // 在通知栏中提示用户有新消息
    private void showNotify(Context context, int mNum) {
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // 设置通知跳转目标
        Intent intent = new Intent(context, MessageList.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(context).
                setContentTitle(context.getString(R.string.notificationtitle)).
                setContentText("您有" + mNum + "条新的短消息").
                setContentIntent(contentIntent).
                setSmallIcon(R.drawable.icon).
                setDefaults(Notification.DEFAULT_ALL).
                setWhen(System.currentTimeMillis()).
                setDefaults(Notification.FLAG_AUTO_CANCEL).
                build();
        mNotificationManager.notify(1, notification);

        PowerManager pm = (PowerManager) mctx.getSystemService(Context.POWER_SERVICE);
        if (pm.isScreenOn()) {
            if (voicePool != null) {
                voicePool.play(SoundId, 1, 1, 0, 0, 1);
            }
        } else {
            Vibrator vib = (Vibrator) mctx.getSystemService(Service.VIBRATOR_SERVICE);
            vib.vibrate(1000);
            PowerManager.WakeLock wl = pm.newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP
                            | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wl.acquire();
            wl.release();
        }
        Intent alarmIntent = new Intent(mctx, ShowMessage.class);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        alarmIntent.putExtra("WhereAreYouFrom", 1);
        alarmIntent.putExtra("MessageNum", mNum);
        mctx.startActivity(alarmIntent);
    }

    // 下载未读取的短消息
    private int getNewMsg(Context context) {
        int mNum = 0;
        int Un_Read = 0;
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("staffId", Long.toString(Property.StaffId));
        String methodPath = Constant.MP_SMS;
        String methodName = Constant.MN_GET_UNREAD_MESSAGE;
        WebServiceManager webServiceManager = new WebServiceManager(context,
                methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);

        if (result == null || result.equals("")) {
            return mNum;
        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
                    ZWTMessage message = new ZWTMessage();
                    message.createTime = JsonUtil.GetJsonObjStringValue(jsonObj,
                            "CreateTime");
                    Log.d("woca", message.createTime + "服务器给的创建时间");
                    message.content = JsonUtil.GetJsonObjStringValue(jsonObj,
                            "Context");
                    message.ownerId = JsonUtil.GetJsonObjLongValue(jsonObj, "UId");
                    message.ownerName = JsonUtil.GetJsonObjStringValue(jsonObj,
                            "UName");
                    message.attach = JsonUtil.GetJsonObjStringValue(jsonObj,
                            "AttachList");

                    // 0 在本地代表未读，1代表已读
                    message.status = ZWTMessage.STATUS_UNREAD;
                    message.contactId = JsonUtil.GetJsonObjLongValue(jsonObj,
                            "OpId");
                    message.Id = JsonUtil.GetJsonObjLongValue(jsonObj, "Id");
                    message.MsgId = JsonUtil.GetJsonObjLongValue(jsonObj, "MsgId");
                    message.contactName = JsonUtil.GetJsonObjStringValue(jsonObj,
                            "OpName");
                    message.type = JsonUtil.GetJsonObjIntValue(jsonObj,
                            "MessageArea");
                    if (!contactIds.contains(message.contactId)) {
                        contactIds.add(message.contactId);
                    }
                    if (message.type == MessageType.TYPE_NORMAL) {
                        Un_Read++;
                    }
                }
                if (Un_Read > 0) {
                    ExitApplication.UnReadNum = Un_Read;
                    ExitApplication.UnReadNum = Un_Read;
                    Intent intent = new Intent();
                    intent.setAction(IntentName.NUM_NOTIFY_RECEIVER);
                    intent.putExtra("Num", Un_Read);
                    mctx.sendBroadcast(intent);
                }
                mNum = jsonArray.length();
                break;
            case Constant.EXCEPTION:
//			//下载失败就再次下载。不然的话，站内短信就不能实时的接收。会影响体验。
//			getNewMsg(mctx);
                LogUtil.d("下载失败！！！！！！！！！！！" + Code);
                break;
            default:
                break;
        }

        return mNum;
    }

    private void Backlog() {
        int i = 0;
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        String methodName = Constant.GetNotCompletedTaskNum;
        String methodPath = Constant.MP_TASK;
        WebServiceManager service = new WebServiceManager(mctx, methodName, parmValues);
        String result = service.OpenConnect(methodPath);

        JSONArray data = JsonUtil.GetJsonArray(result, "Data");
        if (data != null && data.length() > 0) {
            JSONObject jsonObj = (JSONObject) data.opt(0);
            i = JsonUtil.GetJsonObjIntValue(jsonObj, "Total");
        }
        ExitApplication.getInstance().getmBacklog().setNum(i);    //通过接口把待办事项的数量共享给主界面。
    }

    // TODO 系统发送指定广播，达到不断接收消息和心跳。创建警报
    public static void createAlarm(Context context) {
        AlarmManager alarmMgr = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 2秒后发送广播，然后每2秒重复发广播。广播都是直接发到AlarmReceiver的
        int triggerAtTime = (int) (SystemClock.elapsedRealtime() + 5000);
        int interval = 5000;
        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerAtTime, interval, pendIntent);
    }

    // 取消警报
    public static void cancleAlarm(Context context) {
        AlarmManager alarmMgr = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 与上面的intent匹配（filterEquals(intent)）的闹钟会被取消
        alarmMgr.cancel(pendIntent);
    }


}
