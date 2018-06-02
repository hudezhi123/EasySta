package easyway.Mobile;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import easyway.Mobile.Application.ExitApplication;
import easyway.Mobile.Broadcast.BroadcastTabActivity;
import easyway.Mobile.Caution.CautionListActivity;
import easyway.Mobile.Config.Config;
import easyway.Mobile.Contacts.Contacts;
import easyway.Mobile.DangerousGoods.DangerousFormActivity;
import easyway.Mobile.Data.IntentName;
import easyway.Mobile.receiver.AlarmReceiver;
import easyway.Mobile.util.DBHelper;
import easyway.Mobile.Data.MessageType;
import easyway.Mobile.Data.Permission;
import easyway.Mobile.Data.ZWTMessage;
import easyway.Mobile.DefaultReportToRepair.DevReportToRepairActivity;
import easyway.Mobile.DevFault.DeviceFaultActivity;
import easyway.Mobile.DevFault.TaskDevUnusual;
import easyway.Mobile.DevFault.TaskExtractSwageActivity;
import easyway.Mobile.LightingControl.LightingControlActivity;
import easyway.Mobile.LiveCase.LiveCase;
import easyway.Mobile.Login.LoginFrame;
import easyway.Mobile.Message.MessageList;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.PassengerTrafficLog.LogListActivity;
import easyway.Mobile.Patrol.PatrolBrowseActivity;
import easyway.Mobile.PatrolTask.PatrolTaskActivity2;
import easyway.Mobile.PointTask.PTListActivity;
import easyway.Mobile.ReportChart.ReportsActivity;
import easyway.Mobile.SellTicktLog.SellLogListActivity;
import easyway.Mobile.Shift.ShiftInHomeActivity;
import easyway.Mobile.Shift.Shift_Out;
import easyway.Mobile.ShiftNew.ShiftTabActivity;
import easyway.Mobile.SiteRules.SRTabActivity;
import easyway.Mobile.StationSchedule.StationSchedule;
import easyway.Mobile.Task.TaskTabActivity;
import easyway.Mobile.TrainAD.TrainADTabActivity;
import easyway.Mobile.TrainSearch.TSTabActivity;
import easyway.Mobile.TrainTrack.UnitRoadEncroachment;
import easyway.Mobile.VacationApply.VacationApplyActivity;
import easyway.Mobile.VacationAudit.VacationAuditListActivity;
import easyway.Mobile.Watering.WateringTabActivity;
import easyway.Mobile.DangerousGoods.Flags;
import easyway.Mobile.site_monitoring.SMTabActivity;
import easyway.Mobile.util.CommonFunc;
import easyway.Mobile.util.HomeKey;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.LogUtil;
import easyway.Mobile.util.PTTUtil;
import easyway.Mobile.util.PollingService;
import easyway.Mobile.util.PollingUtils;

/*
 * 主界面
 */
public class MainFramework extends ActivityEx {
    private boolean exitApp = false;
    private MessageReceiver receiver;
    private int unReadMsgLocal = 0;
    private MainFrameworkAdapter frameworkAdapter = null;
    private ArrayList<Permission> permissionList = null;
    private HashMap<String, Integer> listFuncTotal = null;

    private final int MSG_OPEN_MODULE = 1; // 打开子模块
    private final int MSG_MSGNUM_CHANGE = 2; // 未读短消息数量更新
    private final int MSG_DF_CHANGE = 9; // 未读短消息数量更新
    private final int MSG_OPEN_RETHOME = 3; // RetHome功能开启
    private final int MSG_OPEN_SIP = 4; // SIP登录
    private final int MSG_SHOW_MSGNUM = 5; // 显示未读短消息数量
    private final int MSG_PERMISSION_EXP = 6; // 获取用户权限列表失败
    private final int MSG_GETMSG_FAIL = 7; // 打开子模块
    private final int MSG_BACKLOG_CHANG = 8;    //待办事项变化
    private final int MSG_PATROL_VALID = 110;
    private final int MSG_UNREAD_MESSAGE_NUMBER = 119;
    private Context context;
    private boolean isAppRunning = false;

    @SuppressLint("HandlerLeak")
    private Handler myhandle = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MSG_OPEN_MODULE: // 打开子模块
                    OpenActivity(message.obj.toString());
                    break;
                case MSG_MSGNUM_CHANGE: // 未读短消息数量更新
                    String key = "MessageList";
                    String _total = message.obj.toString();
                    int total = Integer.valueOf(_total);
                    listFuncTotal.put(key, total);
                    if (frameworkAdapter != null) {
                        frameworkAdapter.notifyDataSetChanged();
                    }
                    break;
                case MSG_DF_CHANGE: // 未读短消息数量更新
                    key = "CaptureActivity";
                    _total = message.obj.toString();
                    total = Integer.valueOf(_total);
                    listFuncTotal.put(key, total);
                    if (frameworkAdapter != null) {
                        frameworkAdapter.notifyDataSetChanged();
                    }
                    break;
                case MSG_OPEN_RETHOME: // RetHome功能开启
                    HomeKey.enableLauncher(MainFramework.this, true);
                    if (HomeKey.work && HomeKey.disableHome) {
                        getWindow().clearFlags(HomeKey.FLAG_HOMEKEY_DISPATCHED);
                    }
                    break;
                // add for SIP start
                case MSG_OPEN_SIP: // SIP登录
                    Property.IsSIPON = true;
                    PTTUtil.Login(MainFramework.this, Property.VOIPId,
                            Property.VOIPPwd, Property.VOIPServiceAddress,
                            Property.VOIPServicePort);
                    break;
                case MSG_SHOW_MSGNUM: // 显示未读短消息数量
                    break;
                case MSG_PERMISSION_EXP: // 获取用户权限列表失败
                    AlertDialog.Builder builder = new Builder(MainFramework.this);
                    builder.setMessage(R.string.exp_permission);
                    builder.setTitle(R.string.Prompt);
                    builder.setPositiveButton(R.string.OK,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    SharedPreferences sp = getSharedPreferences(
                                            CommonFunc.CONFIG, 0);
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putBoolean("isUserExit", true);
                                    editor.commit();
                                    // 注销
                                    Property.Reset(MainFramework.this);
                                    Intent intent = new Intent(MainFramework.this,
                                            LoginFrame.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    builder.create().show();

                    break;
                case MSG_GETMSG_FAIL:
                    break;
                case MSG_BACKLOG_CHANG:
                    int mBacklogNum = (Integer) message.obj;
                    // TODO 这里也暂时改为0
                    listFuncTotal.put("MyTaskList", mBacklogNum);
                    if (frameworkAdapter != null) {
                        frameworkAdapter.notifyDataSetChanged();
                    }
                    break;
                case MSG_PATROL_VALID:
                    boolean isValid = (boolean) message.obj;
                    if (isValid) {
                        Intent intent = new Intent(MainFramework.this, PatrolTaskActivity2.class);
                        startActivityForResult(intent, 1);
                    } else {
                        createDialog();
                    }
                    break;
                case MSG_UNREAD_MESSAGE_NUMBER:
                    String k = "MessageList";
                    int num = message.arg1;
                    if (num == 0) {
                        if (ExitApplication.UnReadNum != 0) {
                            listFuncTotal.put(k, ExitApplication.UnReadNum);
                        }
                    }
                    listFuncTotal.put(k, num);
                    if (frameworkAdapter != null) {
                        frameworkAdapter.notifyDataSetChanged();
                    }
                    break;
                case 22001:
                    break;
                default:
                    break;
            }
        }
    };

    private void createDialog() {
        final AlertDialog mDialog = new AlertDialog.Builder(MainFramework.this).create();
        mDialog.setTitle("提示");
        mDialog.setMessage("非巡检人员，无法巡检！");
        mDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    public Handler getMyhandle() {
        return myhandle;
    }

    public void setMyhandle(Handler myhandle) {
        this.myhandle = myhandle;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainframe);
        context = this;
        ExitApplication.getInstance().addActivity(this);
        //注册广播
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
        Button btnset = (Button) findViewById(R.id.btnset);
        btnset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainFramework.this, Config.class);
                startActivity(intent);
            }
        });
        listFuncTotal = new HashMap<String, Integer>();
        // 获取用户权限
        new Thread() {
            public void run() {
                LoadUIPermission();
            }
        }.start();
        PollingUtils.startPollingService(MainFramework.this, 15,
                PollingService.class, PollingService.ACTION);
    }

    @Override
    public void onResume() {
        if (HomeKey.work && !HomeKey.disableHome) {
            getWindow().addFlags(HomeKey.FLAG_HOMEKEY_DISPATCHED);
        }
        super.onResume();
        regReceiver();
        int temp = ExitApplication.getInstance().getmBacklog().getNum();
        listFuncTotal.put("MyTaskList", temp);
        if (frameworkAdapter != null) {
            frameworkAdapter.notifyDataSetChanged();
        }

        listFuncTotal.put("MessageList", ExitApplication.UnReadNum);
        if (frameworkAdapter != null) {
            frameworkAdapter.notifyDataSetChanged();
        }

//        if (LoginFrame.finish) {
//            if (permissionList != null) {
//                for (Permission permission : permissionList) {
//                    if (permission.FuncCode.equals("MessageList")) {
//                        new Thread() {
//                            public void run() {
//                                GetUnReadedMessage();
//                            }
//                        }.start();
//                        break;
//                    }
//                }
//            }
//        }
    }

    // TODO 获取未读短消息数量
    public int GetTotalUnReadMsg(Context context) {


        DBHelper dbHelper = new DBHelper(context);
        String sql = "select Count(" + DBHelper.MESSAGE_ID
                + ") as MessageCount from " + DBHelper.MESSAGE_TABLE_NAME
                + " where " + DBHelper.MESSAGE_STATUS + "=? and "
                + DBHelper.MESSAGE_OWNERID + "=?";

        String[] countSelectionArgs = {
                String.valueOf(ZWTMessage.STATUS_UNREAD),
                String.valueOf(Property.StaffId)};

        SQLiteDatabase dbse = dbHelper.getReadableDatabase();
        if (dbse.isDbLockedByCurrentThread() || dbse.isDbLockedByOtherThreads()) {
            dbse.endTransaction();
        }
        Cursor cursorMessageCount = dbse.rawQuery(sql, countSelectionArgs);
        try {
            cursorMessageCount.moveToFirst();
            int retValue = cursorMessageCount.getInt(0);
            dbHelper.close();
            return retValue;
        } catch (Exception ex) {
            LogUtil.d(ex.toString());
            if (dbse != null) {
                dbse.endTransaction();
            }
            return 0;
        }

    }

    // TODO 获取未读短消息数量
    public int GetDFUnReadMsg(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        String sql = "select Count(" + DBHelper.MESSAGE_ID
                + ") as MessageCount from " + DBHelper.MESSAGE_TABLE_NAME
                + " where " + DBHelper.MESSAGE_STATUS + "=? and "
                + DBHelper.MESSAGE_OWNERID + "=? and " + DBHelper.MESSAGE_TYPE + "=?";

        String[] countSelectionArgs = {
                String.valueOf(ZWTMessage.STATUS_UNREAD),
                String.valueOf(Property.StaffId)
                , String.valueOf(MessageType.TYPE_DF_NOTICE)};
        SQLiteDatabase dbse = dbHelper.getReadableDatabase();
        if (dbse.isDbLockedByCurrentThread() || dbse.isDbLockedByOtherThreads()) {
            dbse.endTransaction();
        }
        Cursor cursorMessageCount = dbse.rawQuery(sql, countSelectionArgs);
        try {
            cursorMessageCount.moveToFirst();
            int retValue = cursorMessageCount.getInt(0);
            dbHelper.close();
            return retValue;
        } catch (Exception ex) {
            LogUtil.d(ex.toString());
            if (dbse != null) {
                dbse.endTransaction();
            }
            return 0;
        }
    }

    // 用户权限呈现
    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            FrameLayout layTopTitle = (FrameLayout) findViewById(R.id.layTopTitle);
            WindowManager windowManager = getWindowManager();
            android.view.Display display = windowManager.getDefaultDisplay();
            DisplayMetrics dm = new DisplayMetrics();
            display.getMetrics(dm);
            int screenWidth = dm.widthPixels - 20;
            int screenHeight = dm.heightPixels;
            float scale = (float) screenWidth / 480;
            int chgValue = 50;
            switch (dm.heightPixels) {
                case 800:
                    chgValue = 50;
                    break;
                case 1184:
                    chgValue = 46;
                    break;
            }

            int height = screenHeight - layTopTitle.getHeight()
                    - (int) (chgValue * scale);

            if (permissionList.size() > 0) {
                for (Permission permissionItem : permissionList) {
                    if (permissionItem.Title.equals("easyway.Mobile")) {
                        permissionList.remove(permissionItem);
                        break;
                    }
                }
                if (permissionList.get(permissionList.size() - 1).Title
                        .equals("easyway.Mobile")) {
                    permissionList.remove(permissionList.size() - 1);
                }
            }

            GridView gvFunction = (GridView) findViewById(R.id.gvFunction);
            frameworkAdapter = new MainFrameworkAdapter(MainFramework.this,
                    permissionList, myhandle, screenWidth, height,
                    listFuncTotal);
            gvFunction.setAdapter(frameworkAdapter);
        }
    };

    // 获取用户权限
    private void LoadUIPermission() {
        if (permissionList == null) {
            permissionList = new ArrayList<Permission>();
        } else {
            permissionList.clear();
        }
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        String methodPath = Constant.MP_PERMISSION;
        String methodName = Constant.MN_GET_PERMISSION;
        WebServiceManager webServiceManager = new WebServiceManager(
                MainFramework.this, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);

        if (result == null || result.equals("")) {
            myhandle.sendEmptyMessage(MSG_PERMISSION_EXP);
            return;
        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
                    if ("DevicePatrol".equals(JsonUtil.GetJsonObjStringValue(jsonObj, "FuncCode"))) {
                        continue;
                    }
                    Permission permission = new Permission();
                    permission.FuncCode = JsonUtil.GetJsonObjStringValue(jsonObj,
                            "FuncCode");
                    permission.Title = JsonUtil.GetJsonObjStringValue(jsonObj,
                            "Title");
                    permission.ImageUrl = JsonUtil.GetJsonObjStringValue(jsonObj,
                            "ImageUrl");
                    permission.ImgName = JsonUtil.GetJsonObjStringValue(jsonObj,
                            "ImgName");
                    if (permission.FuncCode.equals("MessageList")) {
                        listFuncTotal.put("MessageList", unReadMsgLocal);
                        myhandle.sendEmptyMessage(MSG_SHOW_MSGNUM);
                    }
                    // TODO 给待办任务添加代办数量
                    if (permission.FuncCode.equals("MyTaskList")) {
                        int temp = ExitApplication.getInstance().getmBacklog().getNum();
                        listFuncTotal.put("MyTaskList", temp);
                    }
                    if (permission.Title.equals("easyway.Mobile"))
                        Constant.isPartolDevices = true;
                    if (permission.FuncCode.equals("RetHome")) { // 屏蔽桌面功能
                        myhandle.sendEmptyMessage(MSG_OPEN_RETHOME);
                    } else {
                        if (permission.FuncCode.equals("Internal_talk")) { // 站内通话功能
                            myhandle.sendEmptyMessage(MSG_OPEN_SIP);
                        }
                        permissionList.add(permission);
                    }
                }
                myhandle.post(mUpdateResults);
                break;
            case Constant.EXCEPTION:
            default:
                myhandle.sendEmptyMessage(MSG_PERMISSION_EXP);
                break;
        }
    }

    // 获取用户离线时未接收的消息条数
    private void GetUnReadedMessage() {
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("staffId", Long.toString(Property.StaffId));
        String methodPath = Constant.MP_SMS;
        String methodName = Constant.MN_GET_UNREAD_MESSAGE;
        WebServiceManager webServiceManager = new WebServiceManager(
                MainFramework.this, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);

        if (result == null || result.equals("")) {
            myhandle.sendEmptyMessage(MSG_GETMSG_FAIL);
            return;
        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
                int dfMsgNum = 0;
                int message_num = 0;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
                    ZWTMessage tbZwtMessage = new ZWTMessage();
                    tbZwtMessage.createTime = JsonUtil.GetJsonObjStringValue(
                            jsonObj, "CreateTime");
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
                        message_num++;
                    }
                }
                if (dfMsgNum != 0) {
                    Message mes = myhandle.obtainMessage();
                    mes.what = MSG_DF_CHANGE;
                    mes.obj = dfMsgNum;
                    myhandle.sendMessage(mes);
                }
                if (message_num != 0) {
                    ExitApplication.UnReadNum = message_num;
                    Message msg = myhandle.obtainMessage();
                    msg.arg1 = message_num;
                    msg.what = MSG_UNREAD_MESSAGE_NUMBER;
                    myhandle.sendMessage(msg);
                }
                break;
            case Constant.EXCEPTION:
            default:
                myhandle.sendEmptyMessage(MSG_GETMSG_FAIL);
                break;
        }

        myhandle.sendEmptyMessage(MSG_SHOW_MSGNUM);
    }

    // 注册
    private void regReceiver() {
        if (receiver == null) {
            receiver = new MessageReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(AlarmReceiver.ACTION_NEW_MESSAGE);
        filter.addAction(IntentName.NUM_NOTIFY_RECEIVER);
        this.registerReceiver(receiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {
        // 自定义一个广播接收器
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (IntentName.NUM_NOTIFY_RECEIVER.equals(action)) {
                    int num = intent.getIntExtra("Num", 0);
                    Message msg = myhandle.obtainMessage();
                    msg.arg1 = num;
                    msg.what = MSG_UNREAD_MESSAGE_NUMBER;
                    myhandle.sendMessage(msg);
                    frameworkAdapter.notifyDataSetChanged();
                } else {
                    Bundle bundle = intent.getExtras();
                    int mNum = bundle.getInt("mNum");
                    int mBacklogNum = bundle.getInt("mBacklogNum");
                    Message mes = myhandle.obtainMessage();
                    mes.what = MSG_BACKLOG_CHANG;
                    mes.obj = mBacklogNum;
                    myhandle.sendMessage(mes);
                    ShowNotification(0, mNum);
                }
            }
        }

        public MessageReceiver() {
        }
    }

    public void ShowNotification(int cateCode, int total) {

        try {
            if (!listFuncTotal.containsKey("MessageList"))
                return;

            unReadMsgLocal = GetTotalUnReadMsg(MainFramework.this);

            Message message = new Message();
            message.what = MSG_MSGNUM_CHANGE;
            message.obj = unReadMsgLocal;
            myhandle.sendMessage(message);

            unReadMsgLocal = GetDFUnReadMsg(MainFramework.this);
            Message message1 = new Message();
            message1.what = MSG_DF_CHANGE;
            message1.obj = unReadMsgLocal;
            myhandle.sendMessage(message1);

            if (unReadMsgLocal > 0) {
                Toast.makeText(MainFramework.this, R.string.messageArrived,
                        Toast.LENGTH_LONG).show();
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(1000);

                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.reset();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                mediaPlayer
                        .setDataSource(
                                getApplicationContext(),
                                RingtoneManager
                                        .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (Exception ex) {

            ex.printStackTrace();
        }

    }

    public void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    /**
     * 检测当前人员是否具有巡检权限
     */
    private void checkIsValidStaff() {
        HashMap<String, String> paramValue = new HashMap<>();
        paramValue.put("sessionId", Property.SessionId);
        paramValue.put("stationCode", Property.StationCode);
        String methodName = Constant.PATROL_VALID_STAFF;
        String methodPath = Constant.MP_PATROL;
        WebServiceManager service = new WebServiceManager(MainFramework.this, methodName, paramValue);
        String jsonResult = service.OpenConnect(methodPath);
        Message msg = myhandle.obtainMessage();
        msg.what = MSG_PATROL_VALID;
        msg.obj = JsonUtil.GetJsonBoolean(jsonResult, "MsgType");
        myhandle.sendMessage(msg);
    }


    public void OpenActivity(String intentName) {

        // 巡检任务
        if (intentName.equals("PatrolTask")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    checkIsValidStaff();
                }
            }).start();
            return;
        }

        //客运日志
        if (intentName.equals("IStationWorkLog")) {
            Intent intent = new Intent(MainFramework.this, LogListActivity.class);
            startActivityForResult(intent, 1);
            return;
        }

        // 售票日志
        if (intentName.equals("TicketSellWorkLog")) {
            Intent intent = new Intent(MainFramework.this, SellLogListActivity.class);
            startActivityForResult(intent, 1);
            return;
        }

        //故障报修
        if (intentName.equals("FaultReportToRepair")) {
            Intent intent = new Intent(MainFramework.this, DevReportToRepairActivity.class);
            startActivityForResult(intent, 1);
        }

        // 待办工作
        if (intentName.equals("MyTaskList")) {
            Intent intent = new Intent(MainFramework.this,
                    TaskTabActivity.class);
            startActivityForResult(intent, 1);
            return;
        }

        // 到发通告
        if (intentName.equals("QueryTrainArrLeave")) {
            Intent intent = new Intent(MainFramework.this,
                    TrainADTabActivity.class);
            startActivityForResult(intent, 1);
            return;
        }

        // 股道占用
        if (intentName.equals("QueryUnitRoadStatus")) {
            Intent intent = new Intent(MainFramework.this,
                    UnitRoadEncroachment.class);
            startActivityForResult(intent, 1);
            return;
        }

        // 站内短信
        if (intentName.equals("MessageList")) {
            Intent intent = new Intent(getApplicationContext(),
                    MessageList.class);
            startActivityForResult(intent, 1);
            return;
        }

        // 联系人
        if (intentName.equals("Contact")) {
            Intent intent = new Intent(MainFramework.this, Contacts.class);
            startActivityForResult(intent, 1);
            return;
        }

        // 设备报障
        if (intentName.equals("CaptureActivity")) {
            listFuncTotal.put("CaptureActivity", 0);
            if (frameworkAdapter != null) {
                frameworkAdapter.notifyDataSetChanged();
            }
            Intent intent = new Intent(MainFramework.this, DeviceFaultActivity.class);
            startActivityForResult(intent, 1);
            return;
        }

        // 交班    undo
        if (intentName.equals("Shift_Out")) {
            Intent intent = new Intent(MainFramework.this, Shift_Out.class);
            startActivityForResult(intent, 1);
            return;
        }

        //交接班  undo
        if (intentName.equals("Shift_Out_In")) {
            Intent intent = new Intent(MainFramework.this,
                    ShiftTabActivity.class);
            startActivityForResult(intent, 1);
            return;
        }

        // 接班    undo
        if (intentName.equals("Shift_In")) {
//			 Intent intent = new Intent(MainFramework.this, New_Shift_In.class);
            Intent intent = new Intent(MainFramework.this, ShiftInHomeActivity.class);
            startActivityForResult(intent, 1);
            return;
        }


        // 移动广播     undo
        if (intentName.equals("MobileRadio")) {
            Intent intent = new Intent(MainFramework.this,
                    BroadcastTabActivity.class);
            startActivityForResult(intent, 1);
            return;
        }

        // 情况上报
        if (intentName.equals("LiveCaseReport")) {
            Intent intent = new Intent(MainFramework.this, LiveCase.class);
            startActivityForResult(intent, 1);
            return;
        }

        // 现场监控   undo
        if (intentName.equals("siteMonitor")) {
            Intent intent = new Intent(MainFramework.this, SMTabActivity.class);
            startActivityForResult(intent, 1);
            return;
        }

        // 小工具   undo
        if (intentName.equals("Tools")) {
            try {
                Intent intent = getPackageManager().getLaunchIntentForPackage(
                        "com.android.flashlight");
                startActivityForResult(intent, 1);
            } catch (Exception ex) {
                Toast.makeText(MainFramework.this, R.string.noToolClient,
                        Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
            return;
        }

        // 站内集群通   undo
        if (intentName.equals("InternalSpeak")) {
            try {
                //去打开集群通应用。
                Intent intent = getPackageManager().getLaunchIntentForPackage(
                        PTTUtil.SIPUA_PACKAGE_NAME);
                startActivityForResult(intent, 1);
            } catch (Exception ex) {
                Toast.makeText(MainFramework.this, R.string.noPTTClient,
                        Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
            return;
        }

        // 站内对讲    undo
        if (intentName.equals("zwt_Permission_xChat")) {  //zwt_Permission_xChat
            try {
                ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo info : list) {
                    if (info.processName.equals("com.easyway.interphone")) {
                        isAppRunning = true;
                    }
                }
                Intent intent = getPackageManager().getLaunchIntentForPackage(
                        PTTUtil.XCHAT_PACKAGE_NAME);
                Bundle bundle = new Bundle();
                bundle.putString("sessionID", Property.SessionId);
                bundle.putString("ServerSocket", Property.VOIPServiceAddress);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            } catch (Exception ex) {
                Toast.makeText(MainFramework.this, R.string.noPTTClient,
                        Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
            return;
        }

        // 站内通话    undo
        if (intentName.equals("Internal_talk")) {   //Internal_talk
            try {
                Intent intent = getPackageManager().getLaunchIntentForPackage(
                        PTTUtil.SIPUA_PACKAGE_NAME);
                startActivityForResult(intent, 1);
            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(MainFramework.this,
                        getString(R.string.noSIPClient), Toast.LENGTH_LONG)
                        .show();
                return;
            }
            return;
        }

        // 现场指挥    undo
        if (intentName.equals("LiveControl")) {
            Intent intent = new Intent(MainFramework.this, Contacts.class);
            Bundle bundle = new Bundle();
            bundle.putInt(Contacts.KEY_FLAG, Contacts.FLAG_LIVECONTROL);
            intent.putExtras(bundle);
            startActivityForResult(intent, 1);
            return;
        }

        // 列车时刻表
        if (intentName.equals("StationSchedule")) {   // StationSchedule
            Intent intent = new Intent(MainFramework.this,
                    StationSchedule.class);
            startActivityForResult(intent, 1);
            return;
        }

        // 时刻查询
        if (intentName.equals("TimerSearch")) {  //TimerSearch
            Intent intent = new Intent(MainFramework.this, TSTabActivity.class);
            startActivityForResult(intent, 1);
            return;
        }

        // 重点任务
        if (intentName.equals("PointTask")) {
            Intent intent = new Intent(MainFramework.this, PTListActivity.class);
            startActivityForResult(intent, 1);
            return;
        }

        // 图形报表   undo
        if (intentName.equals("ReportChart")) {
            Intent intent = new Intent(MainFramework.this,
                    ReportsActivity.class);
            startActivityForResult(intent, 1);
            return;
        }

        // 站内规章
        if (intentName.equals("SiteRules")) {   //SiteRules
            Intent intent = new Intent(MainFramework.this, SRTabActivity.class);
            startActivityForResult(intent, 1);
            return;
        }

        // 客运巡检
        if (intentName.equals("PassengerPatrol")) {
            Intent intent = new Intent(MainFramework.this,
                    PatrolBrowseActivity.class);
            startActivityForResult(intent, 1);
            return;
        }

        // 记事本
        if (intentName.equals("Notebook")) {
            Intent intent = new Intent(MainFramework.this,
                    CautionListActivity.class);
            startActivityForResult(intent, 1);
            return;
        }

        //危险品
        if (intentName.equals("ZWT_Dangerous")) {
            Intent intent = new Intent(MainFramework.this,
                    DangerousFormActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(Flags.TO_DANGER_FLAG, Flags.FLAG_FROM_MAIN_TO_DANGER);
            intent.putExtras(bundle);
            startActivityForResult(intent, 1);
        }

        // 上水管理
        if (intentName.equals("Watering")) {
            Intent intent = new Intent(MainFramework.this,
                    WateringTabActivity.class);
            startActivityForResult(intent, 1);
            return;
        }

        // 请假申请     undo
        if (intentName.equals("VacationApply")) {
            Intent intent = new Intent(MainFramework.this,
                    VacationApplyActivity.class);
            startActivityForResult(intent, 1);
            return;
        }

        // 请假审核    undo
        if (intentName.equals("VacationAudit")) {
            Intent intent = new Intent(MainFramework.this,
                    VacationAuditListActivity.class);
            startActivityForResult(intent, 1);
            return;
        }

        // 照明控制     undo
        if (intentName.equals("LinghtControl")) {
            Intent intent = new Intent(MainFramework.this,
                    LightingControlActivity.class);
            startActivityForResult(intent, 1);
            return;
        }

        //设备异常     undo
        if (intentName.equals("TaskDevFault")) {
            Intent intent = new Intent(MainFramework.this,
                    TaskDevUnusual.class);
            startActivityForResult(intent, 1);
            return;
        }

        //吸污功能
        if (intentName.equals("Grabage")) {
            Intent intent = new Intent(MainFramework.this, TaskExtractSwageActivity.class);
            startActivityForResult(intent, 1);
            return;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        finishActivity(1);
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        exitApp = false;
        if (keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_HOME) {

            showExitAlert();
        } else {
            exitApp = super.onKeyDown(keyCode, event);
        }
        return exitApp;
    }

    // 用户退出
    public void showExitAlert() {
        AlertDialog.Builder builder = new Builder(this);
        builder.setIcon(R.drawable.exit);
        builder.setMessage(R.string.ConfirmExit);
        builder.setTitle(R.string.Prompt);
        builder.setPositiveButton(R.string.Exit,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        stopService(new Intent(context, ActiveStatusService.class));
                        exitApp = true;

                        SharedPreferences sp = getSharedPreferences(
                                CommonFunc.CONFIG, 0);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString(CommonFunc.CONFIG_SessionId, "-1");
                        editor.putLong(CommonFunc.CONFIG_StaffId, -1000);
                        editor.putBoolean("isUserExit", true);
                        editor.commit();

                        Property.Reset(MainFramework.this);
                        ExitApplication.getInstance().exit();
                        finish();
                    }
                });

        builder.setNegativeButton(R.string.Cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        exitApp = false;
                    }
                });
        builder.create().show();
    }

}
