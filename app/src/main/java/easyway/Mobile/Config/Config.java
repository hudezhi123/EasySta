package easyway.Mobile.Config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Application.ExitApplication;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Login.BasicDataVersion;
import easyway.Mobile.Login.ISyncData;
import easyway.Mobile.Login.LoginFrame;
import easyway.Mobile.Login.SyncDataThread;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.CommonFunc;
import easyway.Mobile.util.CommonUtils;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.PTTUtil;
import easyway.Mobile.util.PollingService;
import easyway.Mobile.util.PollingUtils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/*
 * 用户信息界面
 */
public class Config extends ActivityEx implements OnClickListener {
    private TextView txtUserName;
    private TextView txtStaffName;
    private TextView txtDepName;
    private TextView txtSizeAll;
    private TextView txtSizeAvail;
    private TextView txtMemoryAll;
    private TextView txtMemoryAvail;
    private Button btnLogout;
    private Button btnChangePsw;
    private Button btnSizeClean;
    private Button btnMemoryClean;
    private Button btnSwitchCamera;
    private String appurl;
    private Button btnAbout;
    private Button btnCheckUpDate;
    private Button btnInterPhone;
    private String filepath;

    private final int MSG_SIZE_CLEAN = 1;                // 空间清理
    private final int MSG_MEMORY_CLEAN = 2;        // 内存清理
    private final int MSG_DATA_SYNC_FAIL = 3;        // 同步基础数据失败
    private final int MSG_DATA_SYNC_SUCCEED = 4;        // 同步基础数据成功
    private final int MSG_CLIENT_NEED_UPDATE = 5; // 客户端有新版本
    private final int MSG_NO_CLIENT_NEED_UPDATE = 22;   //当前不存在改软件需要下载
    private final int MSG_CLIENT_NOTNEED_UPDATE = 6; // 客户端已是最新版本
    private final int MSG_FILE_DOWN_SUCCEED = 7; // 客户端下载成功
    private final int MSG_FILE_DOWN_FAIL = 8; // 客户端下载失败
    private final int MSG_UPDATE_CHECK_FAIL = 9; // 客户端版本检测失败
    private final int Msg_DATA_SYNC_FAIL_WHY = 14;
    @SuppressLint("HandlerLeak")
    private Handler myhandle = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MSG_SIZE_CLEAN:        // 空间清理
                    closeProgressDialog();
                    showToast(R.string.sdcard_cleandone);
                    txtSizeAll.setText(getAllSize());
                    txtSizeAvail.setText(getAvailaleSize());
                    break;
                case MSG_MEMORY_CLEAN:        // 内存清理
                    closeProgressDialog();
                    showToast(R.string.memory_cleandone);
                    txtMemoryAll.setText(getTotalMemory());
                    txtMemoryAvail.setText(getAvailMemory());
                    break;
                case MSG_DATA_SYNC_FAIL:        // 同步基础数据失败
                    closeProgressDialog();
                    showToast(R.string.exp_data_sync);
                    break;
                case MSG_DATA_SYNC_SUCCEED:        // 同步基础数据成功
                    closeProgressDialog();
                    showToast(R.string.syncbasedatasucceed);
                    break;
                case MSG_CLIENT_NEED_UPDATE: // 检测到客户端有新版本，需要更新
                    closeProgressDialog();
                    AlertDialog.Builder builderupdate = new Builder(Config.this);
                    builderupdate.setTitle(R.string.menu_checkupdate);
                    builderupdate.setMessage(R.string.needupdate);
                    builderupdate.setPositiveButton(R.string.updatenow,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    DownloadUpdateFile();
                                }
                            });
                    builderupdate.setNegativeButton(R.string.updatenexttime, null);
                    builderupdate.create().show();
                    break;
                case MSG_NO_CLIENT_NEED_UPDATE:
                    closeProgressDialog();
                    AlertDialog.Builder no_need_download = new Builder(Config.this);
                    no_need_download.setTitle(R.string.menu_checkupdate);
                    no_need_download.setMessage(R.string.need_download);
                    no_need_download.setPositiveButton(R.string.soon_download,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    DownloadUpdateFile();
                                }
                            });
                    no_need_download.setNegativeButton(R.string.next_download, null);
                    no_need_download.create().show();
                    break;
                case MSG_CLIENT_NOTNEED_UPDATE: // 检测到客户端已经是最新版本
                    closeProgressDialog();
                    AlertDialog.Builder builder = new Builder(Config.this);
                    builder.setTitle(R.string.menu_checkupdate);
                    builder.setMessage(R.string.notneedupdate);
                    builder.setPositiveButton(R.string.OK, null);
                    builder.create().show();
                    break;
                case MSG_FILE_DOWN_SUCCEED: // 新版客户端下载成功，开始安装
                    closeProgressDialog();
                    PollingUtils.stopPollingService(Config.this,
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
                    break;
                case Msg_DATA_SYNC_FAIL_WHY:
                    String errMsg = (String) message.obj;
                    showToast(errMsg + "");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.title_config);

        Button btnReturn = (Button) findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(this);

        txtUserName = (TextView) findViewById(R.id.txtUserName);
        txtStaffName = (TextView) findViewById(R.id.txtStaffName);
        txtDepName = (TextView) findViewById(R.id.txtDepName);
        txtSizeAll = (TextView) findViewById(R.id.txtSizeAll);
        txtSizeAvail = (TextView) findViewById(R.id.txtSizeAvail);
        txtMemoryAll = (TextView) findViewById(R.id.txtMemoryAll);
        txtMemoryAvail = (TextView) findViewById(R.id.txtMemoryAvail);

        if (Property.DepName != null)
            txtDepName.setText(Property.DepName);

        if (Property.StaffName != null)
            txtStaffName.setText(Property.StaffName);

        if (Property.UserName != null)
            txtUserName.setText(Property.UserName);

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);

        btnSizeClean = (Button) findViewById(R.id.btnSizeClean);
        btnSizeClean.setOnClickListener(this);

        btnMemoryClean = (Button) findViewById(R.id.btnMemoryClean);
        btnMemoryClean.setOnClickListener(this);

        btnChangePsw = (Button) findViewById(R.id.btnChangePwd);
        btnChangePsw.setOnClickListener(this);

        btnSwitchCamera = (Button) findViewById(R.id.btnSwitchCamera);
        btnSwitchCamera.setOnClickListener(this);

        btnAbout = (Button) findViewById(R.id.btnAbout);
        btnAbout.setOnClickListener(this);

        btnCheckUpDate = (Button) findViewById(R.id.btnCheckUpDate);
        btnCheckUpDate.setOnClickListener(this);

        btnInterPhone = (Button) findViewById(R.id.btnInterPhone);
        btnInterPhone.setOnClickListener(this);


        Button btnSyncData = (Button) findViewById(R.id.btnSyncData);
        btnSyncData.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnReturn:
                finish();
                break;
            case R.id.btnLogout:             // 用户注销
                AlertDialog.Builder builder = new Builder(Config.this);
                builder.setIcon(R.drawable.exit);
                builder.setMessage(R.string.ConfirmExit);
                builder.setTitle(R.string.Prompt);
                builder.setPositiveButton(R.string.Exit,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Property.Reset(Config.this);
                                SharedPreferences sp = getSharedPreferences(
                                        CommonFunc.CONFIG, 0);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putBoolean("isUserExit", true);
                                editor.commit();
                                ExitApplication.getInstance().exit();
                                finish();
                            }
                        });
                builder.setNegativeButton(R.string.Cancel, null);
                builder.setCancelable(true);
                builder.create().show();
                break;
            case R.id.btnSizeClean:        // 空间清理
                sizeClean();
                break;
            case R.id.btnMemoryClean:            // 内存清理
                memoryClean();
                break;
            case R.id.btnChangePwd:        // 修改密码
                Intent intent = new Intent(Config.this, ChangePsw.class);
                startActivity(intent);
                break;
            case R.id.btnSwitchCamera:        // 切换VOIP默认摄像头
                AlertDialog dlg = new AlertDialog.Builder(Config.this)
                        .setTitle("")
                        .setItems(
                                Config.this.getResources().getStringArray(
                                        R.array.SwitchCameraValues),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int item) {
                                        if (item == 1) { // 后置
                                            PTTUtil.SwitchCamera(Config.this,
                                                    PTTUtil.CAMERA_BACK);
                                            btnSwitchCamera
                                                    .setText(R.string.switchcameraback);
                                        } else { // 前置
                                            PTTUtil.SwitchCamera(Config.this,
                                                    PTTUtil.CAMERA_FRONT);
                                            btnSwitchCamera
                                                    .setText(R.string.switchcamerafront);
                                        }
                                    }
                                }).create();
                dlg.show();
                break;
            case R.id.btnSyncData:        // 同步基础数据
                showProgressDialog(R.string.syncdata);
                BasicDataVersion.GetVersions(Config.this);
                ISyncData iSyncData = new ISyncData() {
                    @Override
                    public void SyncEnd(boolean ret) {
                        if (ret) {
                            myhandle.sendEmptyMessage(MSG_DATA_SYNC_SUCCEED);
                        } else {
                            myhandle.sendEmptyMessage(MSG_DATA_SYNC_FAIL);
                        }
                    }

                    //配置页同步数据
                    @Override
                    public Handler getHandler() {
                        return myhandle;
                    }
                };
                SharedPreferences sp = getSharedPreferences(
                        CommonFunc.CONFIG, 0);
                boolean state = sp.getBoolean("SyncDataState", true);
                if (state) {
                    showToast("同步尚未完成，请耐心等待完成");
                    return;
                }
                SyncDataThread thread = new SyncDataThread(Config.this, iSyncData);
                thread.start();
                break;

            case R.id.btnAbout:
                showProgressDialog(R.string.menu_checkupdate);
                new Thread() {
                    public void run() {
                        checkUpdate2GQT();
                    }

                    ;
                }.start();
                break;
            case R.id.btnCheckUpDate:
                showProgressDialog(R.string.menu_checkupdate);
                new Thread() {
                    public void run() {
                        checkUpdate();
                    }

                    ;
                }.start();
                break;
            case R.id.btnInterPhone:
                PackageManager pm = this.getPackageManager();
                String versionName = "";
                try {
                    PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);
                    versionName = pi.versionName;
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                    return;
                }
                AlertDialog.Builder v_builder = new Builder(Config.this);
                v_builder.setTitle(R.string.menu_version);
                if (TextUtils.isEmpty(versionName)) {
                    v_builder.setMessage(getString(R.string.exp_checkupdate));
                } else {
                    v_builder.setMessage(getString(R.string.currversion) + versionName);
                }
                v_builder.setPositiveButton(R.string.OK, null);
                v_builder.create().show();
                break;

            default:
                break;
        }

    }

    // 清理内存，关闭其余应用
    private void memoryClean() {
        showProgressDialog(R.string.memory_cleaning);
        new Thread() {
            public void run() {
                queryRunningAppAndClose();
            }
        }.start();
    }

    // 获取当前正在运行的应用并选择性的关闭
    private void queryRunningAppAndClose() {
        PackageManager pm = getApplicationContext().getPackageManager();
        List<PackageInfo> lstPkg = pm.getInstalledPackages(0);

        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> run = am.getRunningAppProcesses();

        for (RunningAppProcessInfo ra : run) {
            if (ra.processName == null) {
                continue;
            }
            if (ra.processName.equals("system")        // 系统
                    || ra.processName.equals("com.android.phone")) {        // 手机
                continue;
            }

            if (ra.processName.equals(getPackageName())        // 客运通
                    || ra.processName.equals(Property.PACKAGE_NAME_INTERNALSPEAK)        // 对讲机
                    || ra.processName.equals(PTTUtil.SIPUA_PACKAGE_NAME)) {        // VOIP
                continue;
            }

            for (PackageInfo pkg : lstPkg) {
                if (ra.processName.equals(pkg.applicationInfo.processName)) {
                    if (((pkg.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)
                            || ((pkg.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)) {
                        // 系统应用
                    } else {
                        // 个人应用
                        am.restartPackage(ra.processName);
                    }
                    break;
                }
            }
        }

        myhandle.sendEmptyMessage(MSG_MEMORY_CLEAN);
    }

    // 清理SD卡中客运通所有附件
    private void sizeClean() {
        showProgressDialog(R.string.sdcard_cleaning);

        new Thread() {
            public void run() {
                File mediaStorageDir = new File(
                        getString(R.string.config_attach_dir));
                CommonUtils.deleteFile(mediaStorageDir);

                myhandle.sendEmptyMessage(MSG_SIZE_CLEAN);
            }
        }.start();
    }

    // 获取SD卡剩余空间大小
    private String getAvailaleSize() {
        long size = 0;
        String state = android.os.Environment.getExternalStorageState();
        if (android.os.Environment.MEDIA_MOUNTED.equals(state)) {
            if (android.os.Environment.getExternalStorageDirectory().canWrite()) {
                File path = Environment.getExternalStorageDirectory(); // 取得sdcard文件路径
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSize();
                long availableBlocks = stat.getAvailableBlocks();
                size = availableBlocks * blockSize;
            }
        }

        // return Formatter.formatFileSize(getBaseContext(), size);
        return String.valueOf(size / 1024 / 1024) + "MB";
    }

    // 获取SD卡总空间大小
    private String getAllSize() {
        long size = 0;
        String state = android.os.Environment.getExternalStorageState();
        if (android.os.Environment.MEDIA_MOUNTED.equals(state)) {
            if (android.os.Environment.getExternalStorageDirectory().canWrite()) {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSize();
                long availableBlocks = stat.getBlockCount();
                size = availableBlocks * blockSize;
            }
        }

        // return Formatter.formatFileSize(getBaseContext(), size);
        return String.valueOf(size / 1024 / 1024) + "MB";
    }

    // 获取android当前可用内存大小
    private String getAvailMemory() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);

        // return Formatter.formatFileSize(getBaseContext(), mi.availMem);
        return String.valueOf(mi.availMem / 1024 / 1024) + "MB";
    }

    private String getTotalMemory() {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取第一行，系统总内存大小

            // beginIndex
            int begin = str2.indexOf(':');
            // endIndex
            int end = str2.indexOf('k');
            // 截取字符串信息

            str2 = str2.substring(begin + 1, end).trim();
            initial_memory = Integer.valueOf(str2).intValue() * 1024;
            localBufferedReader.close();
        } catch (IOException e) {
        }
        // return Formatter.formatFileSize(getBaseContext(), initial_memory);
        return String.valueOf(initial_memory / 1024 / 1024) + "MB";
    }

    @Override
    public void onResume() {
        super.onResume();

        txtSizeAll.setText(getAllSize());
        txtSizeAvail.setText(getAvailaleSize());
        txtMemoryAll.setText(getTotalMemory());
        txtMemoryAvail.setText(getAvailMemory());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }

        return true;
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
            myhandle.sendEmptyMessage(MSG_NO_CLIENT_NEED_UPDATE);
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

                if (update == null || update.equalsIgnoreCase("NO")) {
                    myhandle.sendEmptyMessage(MSG_CLIENT_NOTNEED_UPDATE);
                } else if (update.equalsIgnoreCase("yes")) {
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

    private void checkUpdate2GQT() {
        boolean isExist = true;
        PackageManager pm = this.getPackageManager();
        String versionName;
        try {
            PackageInfo pi = pm.getPackageInfo("com.easyway.interphone", 0);
            versionName = pi.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            isExist = false;
            versionName = "1.0.1";
        }
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("versionId", versionName);
        String methodPath = Constant.MP_ISTATIONSERVICE;
        String methodName = Constant.MN_CHECK_UPDATE2GQT;
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

                if (update == null || update.equalsIgnoreCase("NO")) {
                    myhandle.sendEmptyMessage(MSG_CLIENT_NOTNEED_UPDATE);
                } else if (update.equalsIgnoreCase("yes")) {
                    appurl = JsonUtil.GetJsonObjStringValue(jsonObj, "DownAddress");
                    if (isExist) {
                        myhandle.sendEmptyMessage(MSG_CLIENT_NEED_UPDATE);
                    } else {
                        myhandle.sendEmptyMessage(MSG_NO_CLIENT_NEED_UPDATE);
                    }
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

    // 下载升级文件
    private void DownloadUpdateFile() {
        showProgressDialog(R.string.downloadingapk);

        new Thread() {
            public void run() {
                filepath = CommonUtils.getUpdateFile(Config.this, appurl);
                if (CommonUtils.getFileFromServerNew(appurl, filepath)) {
                    myhandle.sendEmptyMessage(MSG_FILE_DOWN_SUCCEED);
                } else {
                    errMsg = getString(R.string.exp_update);
                    myhandle.sendEmptyMessage(MSG_FILE_DOWN_FAIL);
                }
            }
        }.start();
    }

    ;
}
