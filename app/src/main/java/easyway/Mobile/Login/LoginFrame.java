package easyway.Mobile.Login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import easyway.Mobile.ActiveStatusService;
import easyway.Mobile.ActivityEx;
import easyway.Mobile.TestActivity;
import easyway.Mobile.receiver.AlarmReceiver;
import easyway.Mobile.Data.LoginData;
import easyway.Mobile.Data.LoginDataDetail;
import easyway.Mobile.Data.LoginResult;
import easyway.Mobile.Data.OStation;
import easyway.Mobile.Data.Station;
import easyway.Mobile.Data.StationBean;
import easyway.Mobile.Data.StationCodeResult;
import easyway.Mobile.MainFramework;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.ACache;
import easyway.Mobile.util.CommonFunc;
import easyway.Mobile.util.CommonUtils;
import easyway.Mobile.util.DateUtil;
import easyway.Mobile.util.HomeKey;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.MyUtils;
import easyway.Mobile.util.PollingService;
import easyway.Mobile.util.PollingUtils;
import easyway.Mobile.util.StringUtil;

//import easyway.Mobile.StationSchedule.StationSchedule;
//import easyway.Mobile.TrainSearch.TSTabActivity;

/*
 * 用户登录
 */
public class LoginFrame extends ActivityEx {
    private EditText txUserName;
    private EditText txUserPassword;
    private Button btnLogin;
    private String appurl;
    private String filepath;
    private Button btnConfig;
    private final int MSG_LOGIN_SUCCEED = 1; // 登陆成功
    private final int MSG_LOGIN_FAIL = 2; // 登陆失败
    private final int MSG_CLIENT_NEED_UPDATE = 3; // 客户端有新版本
    private final int MSG_CLIENT_NOTNEED_UPDATE = 4; // 客户端已是最新版本
    private final int MSG_FILE_DOWN_SUCCEED = 5; // 客户端下载成功
    private final int MSG_FILE_DOWN_FAIL = 6; // 客户端下载失败
    private final int MSG_UPDATE_CHECK_FAIL = 7; // 客户端版本检测失败
    private final int MSG_DATA_SYNC_FAIL = 8; // 基础数据同步失败
    private final int MSG_DATA_SYNC_SUCCEED = 10; // 基础数据同步成功
    private final int Msg_GetAllStationCode_isFail = 11;
    private final int Msg_GetAllStationCode_ok = 12;
    private final int Msg_GetAllStationCode_isNull = 13;
    private final int Msg_DATA_SYNC_FAIL_WHY = 14;
    private final int Msg_Open_DateSetting = 15;
    private final int Msg_MAC_Empty = 16;
    private ACache mainCache;
    private String devStatus;

    private final static String DateTimeAction = "android.settime.action";

    //站名和站码
    ArrayList<StationCodeBase> list1;

    boolean islogin = true;
    public static boolean finish = false;

    private Context context;

    @SuppressLint("HandlerLeak")
    private Handler myhandle = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MSG_LOGIN_SUCCEED: // 登陆成功，开始同步数据
                    showProgressDialog(R.string.syncdata);
                    if (!DateUtil.isServiceRunning(context, "ActiveStatusService")) {
                        startService(new Intent(context, ActiveStatusService.class));
                    }
                    //获取站名和站码信息
                    getStationCodeInfo();
                    break;
                case MSG_CLIENT_NEED_UPDATE: // 检测到客户端有新版本，需要更新
                    closeProgressDialog();
                    AlertDialog.Builder builderupdate = new Builder(LoginFrame.this);
                    builderupdate.setTitle(R.string.menu_checkupdate);
                    builderupdate.setMessage(R.string.needupdate);
                    builderupdate.setPositiveButton(R.string.updatenow,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    DownloadUpdateFile();
                                }
                            });
                    builderupdate.setNegativeButton(R.string.updatenexttime, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            checkLogin();
                        }
                    });
                    builderupdate.create().show();
                    break;
                case MSG_CLIENT_NOTNEED_UPDATE: // 检测到客户端已经是最新版本
                    closeProgressDialog();
//				AlertDialog.Builder builder = new Builder(LoginFrame.this);
//				builder.setTitle(R.string.menu_checkupdate);
//				builder.setMessage(R.string.notneedupdate);
//				builder.setPositiveButton(R.string.OK, null);
//				builder.create().show();
                    checkLogin();
                    break;
                case MSG_FILE_DOWN_SUCCEED: // 新版客户端下载成功，开始安装
                    closeProgressDialog();
                    PollingUtils.stopPollingService(LoginFrame.this,
                            PollingService.class, PollingService.ACTION);

                    Intent updateIntent = new Intent("android.intent.action.VIEW");
                    updateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    updateIntent.setDataAndType(Uri.fromFile(new File(filepath)),
                            "application/vnd.android.package-archive");

                    startActivity(updateIntent);

                    break;
                case MSG_FILE_DOWN_FAIL: // 客户端下载失败
                    closeProgressDialog();
                    showToast("下载失败，稍后再试");
                    checkLogin();
                    break;
                case Msg_MAC_Empty:
                    closeProgressDialog();
                    showToast(errMsg);
                    break;
                case MSG_LOGIN_FAIL: // 登陆失败
                    closeProgressDialog();
                    String s = (String) message.obj;
                    if (s != null && s != "") {
                        showToast(s);
                    }
                    showToast("登录失败");
                    btnConfig.setVisibility(ViewGroup.VISIBLE);
                    break;
                case MSG_UPDATE_CHECK_FAIL: // 版本检测失败
                    closeProgressDialog();
                    showToast(errMsg);
                    break;
                case MSG_DATA_SYNC_FAIL: // 数据同步版本检测失败
//				if (message.what == MSG_DATA_SYNC_FAIL){
                    showToast(errMsg);

//				}
                    break;
                case MSG_DATA_SYNC_SUCCEED: // 数据同步成功，进入主界面
                    showToast("数据同步成功！");
                    // 建立警报，定时发送心跳包及获取短消息
                    AlarmReceiver.createAlarm(LoginFrame.this);
                    if (islogin) {
                        islogin = false;
                        Intent intent = new Intent(LoginFrame.this,
                                MainFramework.class);
                        startActivity(intent);

                    }
                    break;
                case Msg_GetAllStationCode_isFail:
                    showToast("服务器返回站名和站码信息失败");
                    getStationCodeInfoPrepare();
                    break;
                case Msg_GetAllStationCode_ok:
                    BasicDataVersion.GetVersions(LoginFrame.this);

                    ISyncData iSyncData = new ISyncData() {
                        @Override
                        public void SyncEnd(boolean ret) {
                            SharedPreferences sp = getSharedPreferences(
                                    CommonFunc.CONFIG, 0);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putBoolean("SyncDataState", false);
                            editor.commit();

                            if (ret) {
                                finish = true;
                                myhandle.sendEmptyMessage(MSG_DATA_SYNC_SUCCEED);
                            } else {
                                finish = true;
                                errMsg = getString(R.string.exp_data_sync);
                                myhandle.sendEmptyMessage(MSG_DATA_SYNC_FAIL);
                            }
                        }

                        @Override
                        public Handler getHandler() {
                            return myhandle;
                        }
                    };
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if (islogin) {
                                islogin = false;

                                Intent intent = new Intent(LoginFrame.this,
                                        MainFramework.class);
                                startActivity(intent);

                            }

                        }

                    }, 1000);

                    SyncDataThread thread = new SyncDataThread(LoginFrame.this,
                            iSyncData);
                    thread.start();

                    SharedPreferences sp = getSharedPreferences(
                            CommonFunc.CONFIG, 0);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("SyncDataState", true);
                    editor.commit();
                    break;
                case Msg_GetAllStationCode_isNull:
                    showToast("服务器返回站名和站码信息为空");
                    getStationCodeInfoPrepare();
                    break;
                case Msg_DATA_SYNC_FAIL_WHY:
                    String errMsg = (String) message.obj;
                    showToast(errMsg + "");
                    break;
                case Msg_Open_DateSetting:
                    Intent setTime = new Intent(Settings.ACTION_DATE_SETTINGS);
                    startActivity(setTime);
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        context = this;
        WindowManager windowManager = getWindowManager();
        android.view.Display display = windowManager.getDefaultDisplay();
        Property.screenheight = display.getHeight();
        Property.screenwidth = display.getWidth();

        txUserName = (EditText) findViewById(R.id.txUserName);
        txUserPassword = (EditText) findViewById(R.id.txUserPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // 用户名不能为空
                String userName = txUserName.getText().toString();
                if (userName == null || userName.trim().length() == 0) {
                    showToast(R.string.exp_usernameempty);
                    return;
                }

                // 密码不能为空
                String userPassword = txUserPassword.getText().toString();
                if (userPassword == null || userPassword.trim().length() == 0) {
                    showToast(R.string.exp_passwrodempty);
                    return;
                }

                showProgressDialog(R.string.logining);
                new Thread() {
                    public void run() {
                        Login(); // 登陆
                    }
                }.start();
                SharedPreferences sp = getSharedPreferences(CommonFunc.CONFIG, 0);
                sp.edit().putBoolean("isUserExit", false).commit();

            }
        });

        btnConfig = (Button) findViewById(R.id.btnConfig);
        String srt = CommonFunc.GetServer(LoginFrame.this);
        if (!TextUtils.isEmpty(CommonFunc.GetServer(LoginFrame.this))) {
            btnConfig.setVisibility(ViewGroup.GONE);
        }

        btnConfig.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        Configuration.class);
                startActivity(intent);
            }
        });

        // 用户名、密码
        SharedPreferences sp = getSharedPreferences(CommonFunc.CONFIG, 0);
        String username = sp.getString(CommonFunc.CONFIG_USERNAME, "");
        String password = sp.getString(CommonFunc.CONFIG_PASSWORD, "");
        txUserName.setText(username);
        txUserPassword.setText(password);

//        showProgressDialog(R.string.menu_checkupdate);

        //TODO 卸载旧版本的APP因为包名大小写不一样。
//        DeleteOldApp();
    }

    private void checkPerm() {

        boolean bCam = false;
        boolean bSD = false;
        boolean bWiFi = false;
        boolean bMac = false;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            bCam = true;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            bSD = true;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            bMac = true;
        }

        List<String> permissionsList = new ArrayList<>();

        if (bCam && bSD && bWiFi && bMac) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    100);
        }
//        ,
//        Manifest.permission.ACCESS_NETWORK_STATE,
//                Manifest.permission.CHANGE_WIFI_STATE,
//                Manifest.permission.CHANGE_NETWORK_STATE
        else if (bCam) {
            //申请wifi权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_WIFI_STATE},
                    100);
        } else if (bSD) {
            //申请SDCARD权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    100);
        } else if (bWiFi) {
            //申请SDCARD权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_SECURE_SETTINGS},
                    100);
        } else if (bMac) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CHANGE_WIFI_STATE},
                    100);
        }

        if (Build.VERSION.SDK_INT >= 23 && !MyUtils.isOPen(this)) {
            Settings.Secure.putInt(getContentResolver(), Settings.Secure.LOCATION_MODE, 1);
        }

        return;
    }


    public void onResume() {

        HomeKey.enableLauncher(LoginFrame.this, false);
        new Thread() {
            public void run() {
                checkUpdate();
            }

            ;
        }.start();
        // 取消警报
        AlarmReceiver.cancleAlarm(LoginFrame.this);


        super.onResume();
    }

    private void checkLogin() {
        islogin = true;
        //		//判断联网后再去登陆
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            SharedPreferences sp = getSharedPreferences(
                    CommonFunc.CONFIG, 0);
            //自动登陆。
            boolean isExit = sp.getBoolean("isUserExit", false);
            if (isExit) {
                //用户退出时，置空账号和密码。
                txUserName.setText("");
                txUserPassword.setText("");
            } else {
                // 用户名不能为空
                String userName = txUserName.getText().toString();
                if (userName == null || userName.trim().length() == 0) {
                    showToast(R.string.exp_usernameempty);
                } else {
                    // 密码不能为空
                    String userPassword = txUserPassword.getText().toString();
                    if (userPassword == null || userPassword.trim().length() == 0) {
                        showToast(R.string.exp_passwrodempty);
                    } else {
                        showProgressDialog(R.string.logining);
                        new Thread() {
                            public void run() {
                                Login(); // 登陆
                            }

                            ;
                        }.start();
                    }
                }
                sp.edit().putBoolean("isUserExit", false).commit();
            }
        } else {
            Toast.makeText(this, "未连接网络，请连接网络后再试。", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean exitApp = false;
        if (keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_HOME) {
            // do nothing
        } else {
            exitApp = super.onKeyDown(keyCode, event);
        }
        return exitApp;
    }

    // 下载升级文件
    private void DownloadUpdateFile() {
        showProgressDialog(R.string.downloadingapk);

        new Thread() {
            public void run() {
                filepath = CommonUtils.getUpdateFile(LoginFrame.this, appurl);
                if (CommonUtils.getFileFromServerNew(appurl, filepath)) {
                    myhandle.sendEmptyMessage(MSG_FILE_DOWN_SUCCEED);
                } else {
                    errMsg = getString(R.string.exp_update);
                    myhandle.sendEmptyMessage(MSG_FILE_DOWN_FAIL);
                }
            }
        }.start();
    }

    private void Login(int i) {
        Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);
    }

    // 登录
    private void Login() {
        String userName = txUserName.getText().toString().trim();
        String userPassword = txUserPassword.getText().toString().trim();

        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("username", userName);
        parmValues.put("password", userPassword);
        String tem = CommonFunc.getLocalIpAddress();
        parmValues.put("IpAddress", CommonFunc.getLocalIpAddress());
        String temp;
        mainCache = ACache.get(context, Constant.MAC_ADDR);
        devStatus = mainCache.getAsString("macAddress");
        if (StringUtil.isNullOrEmpty(devStatus)) {
            temp = CommonFunc.GetMac(this, mainCache);
            if (!StringUtil.isNullOrEmpty(temp)) {
                mainCache.put("macAddress", temp);
            }
        } else {
            temp = devStatus;
        }
        if (StringUtil.isNullOrEmpty(temp)) {
            errMsg = "MAC地址为空，不允许登陆";
            myhandle.sendEmptyMessage(Msg_MAC_Empty);
            return;
        }
        parmValues.put("mac", temp);

        String methodPath = Constant.MP_ISTATIONSERVICE;
        String methodName = Constant.MN_LOGIN;
        WebServiceManager webServiceManager = new WebServiceManager(
                getApplicationContext(), methodName, parmValues);

        String result = webServiceManager.OpenConnect(methodPath);

        if (result == null || result.equals("")) {
            errMsg = getString(R.string.exp_login);
            myhandle.sendEmptyMessage(MSG_LOGIN_FAIL);
            return;
        }
        Gson gson = new Gson();
        LoginResult resultJson = gson.fromJson(result, LoginResult.class);
        int Code = resultJson.getCode();
        switch (Code) {
            case Constant.NORMAL:
                ArrayList<LoginData> dataList = (ArrayList<LoginData>) resultJson.getData();
                if (dataList.size() == 0) {
                    errMsg = getString(R.string.exp_login);
                    myhandle.sendEmptyMessage(MSG_LOGIN_FAIL);
                    return;
                }
                LoginData data = dataList.get(0);

                Property.SessionId = data.getSessionID();
                Property.UserId = Long.valueOf(data.getUserId());
                Property.UserName = userName;
                Property.StaffId = Long.valueOf(data.getStaffId());
                Property.DeptId = data.getDeptId();
                Property.StaffName = data.getStaffName();
                Property.DepName = data.getDeptName();
                Property.VOIPServiceAddress = data.getVOIPServiceAddress();
                Property.VOIPServicePort = data.getVOIPServicePort();
                Property.VOIPId = data.getVOIPId();
                Property.VOIPPwd = data.getVOIPPwd();
                Property.IsTeamLeader = data.getIsTeamLeader();

                String SOwnStation = data.getOwnStation();
                SOwnStation.replace("", "/");
                LoginDataDetail OwnStation = gson.fromJson(SOwnStation, LoginDataDetail.class);
                String time = data.getServerTime();
                Date phoneDate = new Date(System.currentTimeMillis());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time1 = format.format(phoneDate);
                try {
                    Date d = format.parse(time);
                    long ServerTime = d.getTime();
                    int year = Integer.valueOf(time.substring(0, 4));
                    int month = Integer.valueOf(time.substring(5, 7));
                    int day = Integer.valueOf(time.substring(8, 10));
                    int hour = Integer.valueOf(time.substring(11, 13));
                    int minute = Integer.valueOf(time.substring(14, 16));
                    setDateTime(year, month, day, hour, minute);
                } catch (ParseException e) {
                    Message mes = myhandle.obtainMessage();
                    mes.obj = e.toString();
                    mes.what = Msg_DATA_SYNC_FAIL_WHY;
                    myhandle.sendMessage(mes);
                    e.printStackTrace();
                }
                // 保存用户名、密码
                SharedPreferences sp = getSharedPreferences(CommonFunc.CONFIG, 0);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(CommonFunc.CONFIG_USERNAME, userName);
                editor.putString(CommonFunc.CONFIG_PASSWORD, userPassword);
                editor.putString(CommonFunc.CONFIG_SessionId, data.getSessionID());
                editor.putLong(CommonFunc.CONFIG_StaffId, Long.valueOf(data.getStaffId()));
                editor.commit();

                if (OwnStation != null && OwnStation.getData().size() > 0) {
                    OStation sta = OwnStation.getData().get(0);

                    Property.OwnStation = new Station();
                    Property.OwnStation.Code = sta.getOStationValue();
                    if (Property.OwnStation.Code.equals("XAB")) {
//					Intent start = new Intent(LoginFrame.this,WIFiServer.class);
//					startService(start);
                    }
                    Property.OwnStation.Name = sta.getOStationName();
                    Property.StationCode = Property.OwnStation.Code;

                }

                String SChargeStation = data.getChargeStation();
                SChargeStation.replace("", "/");
                LoginDataDetail ChargeStation = gson.fromJson(SOwnStation, LoginDataDetail.class);
                if (ChargeStation != null && ChargeStation.getData().size() > 0) {
                    Property.ChargeStation = new ArrayList<Station>();
                    ArrayList<OStation> tempData = (ArrayList<OStation>) ChargeStation.getData();
                    int size = tempData.size();
                    for (int i = 0; i < size; i++) {
                        Station station = new Station();
                        OStation base = tempData.get(i);
                        station.Code = base.getOStationValue();
                        station.Name = base.getOStationName();
                        Property.ChargeStation.add(station);
                    }
                }
                myhandle.sendEmptyMessage(MSG_LOGIN_SUCCEED);
                break;
            case Constant.EXCEPTION:
                errMsg = JsonUtil.GetJsonString(result, "Msg");
                Message ms = myhandle.obtainMessage();
                ms.obj = errMsg;
                ms.what = MSG_LOGIN_FAIL;
                myhandle.sendMessage(ms);
                break;
            default:
                errMsg = JsonUtil.GetJsonString(result, "errMsg");
                if (TextUtils.isEmpty(errMsg)) {
                    myhandle.sendEmptyMessage(MSG_LOGIN_FAIL);
                } else {
                    Message msg = new Message();
                    msg.obj = errMsg;
                    msg.what = MSG_LOGIN_FAIL;
                    myhandle.sendMessage(msg);
                }

                break;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, R.string.menu_about); // 关于
        menu.add(0, 1, 1, R.string.menu_checkupdate); // 版本检测
        menu.add(0, 2, 2, R.string.menu_interphone); // 对讲机
        // menu.add(0, 3, 3, R.string.title_stationschedule); // 列车时刻
        // menu.add(0, 4, 4, R.string.title_trainsearch); // 时刻查询

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0: // 关于
                showAboutInfo();
                break;
            case 1: // 版本检测
                showProgressDialog(R.string.menu_checkupdate);
                new Thread() {
                    public void run() {
                        checkUpdate();
                    }

                }.start();
                break;
            case 2: // 对讲机
                try {
                    Intent intent = getPackageManager().getLaunchIntentForPackage(
                            Property.PACKAGE_NAME_INTERNALSPEAK);
                    startActivity(intent);
                } catch (Exception ex) {
                    showToast(R.string.noPTTClient);
                    ex.printStackTrace();
                }
                break;
            // case 3: // 列车时刻
            // Intent intent = new Intent(LoginFrame.this, StationSchedule.class);
            // startActivity(intent);
            // break;
            // case 4: // 时刻查询
            // Intent intentTS = new Intent(LoginFrame.this, TSTabActivity.class);
            // startActivity(intentTS);
            // break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //TODO 获取站名和站码信息。
    private void getStationCodeInfoPrepare() {
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        String methodPath = Constant.MP_SPARK;
        String methodName = Constant.MN_GET_STATIONCODE;
        WebServiceManager webServiceManager = new WebServiceManager(this, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        Gson gson = new Gson();
        StationCodeResult mResult = gson.fromJson(result, StationCodeResult.class);
        int code = -1;
        if (mResult == null) {
            code = 1;
        } else {
            code = mResult.getCode();
        }
        if (code == 1000) {
            ArrayList<StationBean> dataList = (ArrayList<StationBean>) mResult.getData();
            if (dataList != null && dataList.size() > 0) {
                int k = dataList.size();
                ArrayList<StationCodeBase> list = new ArrayList<StationCodeBase>();
                for (int i = 0; i < k; i++) {
                    StationCodeBase base = new StationCodeBase();
                    StationBean sta = dataList.get(i);
                    base.StationCode = sta.getStationCode();
                    base.StationName = sta.getStationName();
                    list.add(base);
                }
                list1 = list;
                myhandle.sendEmptyMessage(Msg_GetAllStationCode_ok);
            } else {
                myhandle.sendEmptyMessage(Msg_GetAllStationCode_isNull);
            }
        } else {
            myhandle.sendEmptyMessage(Msg_GetAllStationCode_isFail);
        }
    }

    private void getStationCodeInfo() {
        new Thread() {
            public void run() {
                getStationCodeInfoPrepare();
            }

            ;
        }.start();
    }

    //TODO 对子线程提供站名和站码信息
    public String[] getStationCode2SyncThread() {
        int k = list1.size();
        String[] name = new String[k];
        for (int i = 0; i < k; i++) {
            name[i] = list1.get(i).StationName;
        }
        return name;
    }

    /*
     * 关于
     */
    private void showAboutInfo() {
        PackageManager manager = this.getPackageManager();
        String version = "";
        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            version = info.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        AlertDialog.Builder builder = new Builder(this);
        builder.setIcon(R.drawable.icon);
        builder.setTitle(R.string.menu_about);
        builder.setMessage(getString(R.string.app_name) + "\n"
                + getString(R.string.version) + version);
        builder.setPositiveButton(R.string.OK, null);
        builder.create().show();
    }

    /*
     * 版本检测
     */
    private void checkUpdate() {
        PackageManager pm = this.getPackageManager();
        String versionName;

        try {
            PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            errMsg = getString(R.string.exp_checkupdate);
            myhandle.sendEmptyMessage(MSG_UPDATE_CHECK_FAIL);
            return;
        }

        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("versionId", versionName);

        String methodPath = Constant.MP_ISTATIONSERVICE;
        String methodName = Constant.MN_CHECK_UPDATE;
        WebServiceManager webServiceManager = new WebServiceManager(
                getApplicationContext(), methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);

        if (result == null || result.equals("")) {
            errMsg = getString(R.string.exp_checkupdate);
            myhandle.sendEmptyMessage(MSG_UPDATE_CHECK_FAIL);
            return;
        }

        int Code = JsonUtil.GetJsonInt(result, "code");
        switch (Code) {
            case Constant.NORMAL:
                JSONArray jsonArray = JsonUtil.GetJsonArray(result, "data");
                if (jsonArray.length() == 0) {
                    errMsg = getString(R.string.exp_checkupdate);
                    myhandle.sendEmptyMessage(MSG_UPDATE_CHECK_FAIL);
                    return;
                }

                JSONObject jsonObj = (JSONObject) jsonArray.opt(0);
                String update = JsonUtil.GetJsonObjStringValue(jsonObj, "Update");
                String newVersion = JsonUtil.GetJsonObjStringValue(jsonObj, "VersionCode");
                if (update == null || update.equalsIgnoreCase("NO")) {
                    myhandle.sendEmptyMessage(MSG_CLIENT_NOTNEED_UPDATE);
                } else if (update.equalsIgnoreCase("yes") && !versionName.equals(newVersion)) {
                    appurl = JsonUtil.GetJsonObjStringValue(jsonObj, "DownAddress");
                    myhandle.sendEmptyMessage(MSG_CLIENT_NEED_UPDATE);
                } else {
                    myhandle.sendEmptyMessage(MSG_CLIENT_NOTNEED_UPDATE);
                }
                break;
            case Constant.EXCEPTION:
            default:
                errMsg = JsonUtil.GetJsonString(result, "Msg");
                myhandle.sendEmptyMessage(MSG_UPDATE_CHECK_FAIL);
                break;
        }
    }

    private boolean compareVersionCode(String oldVersion, String newVersion) {
        String[] oldVerstionPart = oldVersion.split("\\.");
        String[] newVersionPart = newVersion.split("\\.");
        int[] old = new int[3];
        int[] news = new int[3];
        for (int i = 0; i < 3; i++) {
            old[i] = Integer.parseInt(oldVerstionPart[i]);
            news[i] = Integer.parseInt(newVersionPart[i]);
            if (news[i] > old[i]) {
                return true;
            } else if (news[i] == old[i]) {
                continue;
            } else {
                break;
            }
        }
        return false;
    }


    public void setDateTime(int year, int month, int day, int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        long when = c.getTimeInMillis();
        sendBroadcastTime(when);
    }

    public void sendBroadcastTime(long when) {
        Intent intent = new Intent("android.settime.action");
        intent.putExtra("datetime", when);
        sendBroadcast(intent);
    }

    private SharedPreferences share;

    private void DeleteOldApp() {
        share = getSharedPreferences("deleteLog", Context.MODE_PRIVATE);
        boolean flag = share.getBoolean("ISDELETED", false);
        if (!flag) {
            Uri uri = Uri.parse("package:easyway.Mobile");//获取删除包名的URI
            if (uri == null) {
                return;
            }
            Intent delete = new Intent();
            delete.setAction(Intent.ACTION_DELETE);//设置我们要执行的卸载动作
            delete.setData(uri);//设置获取到的URI
            startActivity(delete);
            SharedPreferences.Editor editor = share.edit();
            editor.putBoolean("ISDELETED", true);
            editor.commit();
        } else {
            return;
        }


    }

}
