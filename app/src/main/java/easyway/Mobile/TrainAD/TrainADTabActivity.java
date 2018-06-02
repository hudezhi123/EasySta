package easyway.Mobile.TrainAD;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.DBHelper;
import easyway.Mobile.Data.Parameter;
import easyway.Mobile.Data.TaskWorkspace;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.HomeKey;
import easyway.Mobile.util.JsonUtil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/*
 * 到发通告
 */
public class TrainADTabActivity extends TabActivity implements
        TabHost.OnTabChangeListener, OnClickListener {
    public static final String ACTION_TRAINAD_SEARCH = "easyway.Mobile.TrainAD.Search";

    public static final String EXTRA_FLAG = "FLAG";
    public static final String EXTRA_TRAINNO = "TRAINNO";
    public static final String EXTRA_STATUS = "STATUS";
    public static final String EXTRA_CHECK_PORT = "CHECK_PORT";
    public static final String EXTRA_STARTTIME = "STARTTIME";
    public static final String EXTRA_ENDTIME = "ENDTIME";
    public static final String EXTRA_TWID = "TWID";

    public static final long TWID_INIT = -1;


    public static String mStationCode = "";
    public static String mStationName = "";
    public static final int FLAG_SEARCH = 1;
    public static final int FLAG_STATIONCHANGE = 2;

    private TabHost mTabHost;
    private ArrayList<Parameter> mParameterList = null;
    private List<CheckPortResult.DataBean> mAllCheckPortList = null;
    private ArrayList<TaskWorkspace> mAllPlatformList = null;
    private ArrayList<TaskWorkspace> mPlatformList = null;
    private EditText edtSearch;
    private Button btnSearch;
    private Button btnStatus;
    private Button btnPlatform;
    private Button btnCheckPort;
    private TextView station;

    private Button btnBegintime; // 开始时间
    private Button btnEndtime; // 结束时间
    private String beginTime = "";
    private String endTime = "";

    private int mBeginTimeHour; // 前半小时
    private int mBeginTimeMinute;
    private int mEndTimeHour; // 后两小时
    private int mEndTimeMinute;
    private String strStatus = "";
    private String strCheckPort = "";
    private long mTwId = TWID_INIT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (HomeKey.work && !HomeKey.disableHome) {
            getWindow().addFlags(HomeKey.FLAG_HOMEKEY_DISPATCHED);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trainad_tab);

        initView();
        initTabHost();

        mPlatformList = GetPlatformByStation();

        new Thread() {
            public void run() {
                mParameterList = GetAllParamValues(Parameter.PARAM_CODE_TICKET);
                mAllCheckPortList = getCheckPort();
                mAllPlatformList = GetPlatformWorkspace();
                mPlatformList = GetPlatformByStation();
            }
        }.start();
    }

    private void initView() {
        if (Property.OwnStation != null) {
            mStationCode = Property.OwnStation.Code;
            mStationName = Property.OwnStation.Name;
        }

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.title_trainad);

        station = (TextView) findViewById(R.id.station);
        if (Property.OwnStation != null)
            station.setText("(" + Property.OwnStation.Name + ")");
        station.setVisibility(View.VISIBLE);
        station.setOnClickListener(this);

        Button searchBtn = (Button) findViewById(R.id.btnset);
        searchBtn.setVisibility(View.VISIBLE);
        searchBtn.setText(R.string.search);
        searchBtn.setOnClickListener(this);

        Button btnReturn = (Button) findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(this);

        // 车次
        edtSearch = (EditText) findViewById(R.id.search_edit);
        edtSearch.setHint(R.string.search_input_train_num);

        // 搜索
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);

        // 检票状态
        btnStatus = (Button) findViewById(R.id.btnStatus);
        btnStatus.setOnClickListener(this);

        // 站台
        btnPlatform = (Button) findViewById(R.id.btnPlatform);
        btnPlatform.setOnClickListener(this);

        //检票口
        btnCheckPort = (Button) findViewById(R.id.btnCheckPort);
        btnCheckPort.setOnClickListener(this);

        // 开始时间
        btnBegintime = (Button) findViewById(R.id.btnBegintime);
        btnBegintime.setOnClickListener(this);

        // 结束时间
        btnEndtime = (Button) findViewById(R.id.btnEndtime);
        btnEndtime.setOnClickListener(this);

        int width = Property.screenwidth / 2;
        btnStatus.setWidth(width);
        btnPlatform.setWidth(width);
        btnBegintime.setWidth(width);
        btnEndtime.setWidth(width);
    }

    private void initTabHost() {
        mTabHost = getTabHost();
        mTabHost.setup();

        addOneTab();
        addTwoTab();
        addThreeTab();
        addFourTab();
        addLastTab();

        mTabHost.setOnTabChangedListener(this);

        Calendar c = Calendar.getInstance();
        mBeginTimeHour = c.get(Calendar.HOUR_OF_DAY);
        mBeginTimeMinute = c.get(Calendar.MINUTE);

        mEndTimeHour = c.get(Calendar.HOUR_OF_DAY);
        mEndTimeMinute = c.get(Calendar.MINUTE);
    }

    private void addOneTab() {
        Intent intent = new Intent();
        intent.setClass(this, TrainADAllActivity.class);

        TextView txt = new TextView(this);
        txt.setText(R.string.TrainAll);
        txt.setTextSize(20);
        txt.setGravity(Gravity.CENTER);
        txt.setTextColor(Color.BLACK);
        txt.setBackgroundResource(R.drawable.btn_tab_left_selector);
        TabSpec spec = mTabHost.newTabSpec("All");
        spec.setIndicator(txt);
        spec.setContent(intent);
        mTabHost.addTab(spec);
    }

    private void addTwoTab() {
        Intent intent = new Intent();
        intent.setClass(this, TrainADOriginActivity.class);

        TextView txt = new TextView(this);
        txt.setText(R.string.TrainOrigin);
        txt.setTextSize(20);
        txt.setGravity(Gravity.CENTER);
        txt.setTextColor(Color.BLACK);
        txt.setBackgroundResource(R.drawable.btn_tab_middle_selector);
        TabSpec spec = mTabHost.newTabSpec("Origin");
        spec.setIndicator(txt);
        spec.setContent(intent);
        mTabHost.addTab(spec);
    }

    private void addThreeTab() {
        Intent intent = new Intent();
        intent.setClass(this, TrainADTerminalActivity.class);

        TextView txt = new TextView(this);
        txt.setText(R.string.TrainTerminal);
        txt.setTextSize(20);
        txt.setGravity(Gravity.CENTER);
        txt.setTextColor(Color.BLACK);
        txt.setBackgroundResource(R.drawable.btn_tab_middle_selector);
        TabSpec spec = mTabHost.newTabSpec("Terminal");
        spec.setIndicator(txt);
        spec.setContent(intent);
        mTabHost.addTab(spec);
    }

    private void addFourTab() {
        Intent intent = new Intent();
        intent.setClass(this, TrainADVIAActivity.class);

        TextView txt = new TextView(this);
        txt.setText(R.string.TrainVIA);
        txt.setTextSize(20);
        txt.setGravity(Gravity.CENTER);
        txt.setTextColor(Color.BLACK);
        txt.setBackgroundResource(R.drawable.btn_tab_middle_selector);
        TabSpec spec = mTabHost.newTabSpec("VIA");
        spec.setIndicator(txt);
        spec.setContent(intent);
        mTabHost.addTab(spec);
    }

    private void addLastTab() {
        Intent intent = new Intent();
        intent.setClass(this, TrainADLastActivity.class);

        TextView txt = new TextView(this);
        txt.setText(R.string.Last);
        txt.setTextSize(20);
        txt.setGravity(Gravity.CENTER);
        txt.setTextColor(Color.BLACK);
        txt.setBackgroundResource(R.drawable.btn_tab_right_selector);
        TabSpec spec = mTabHost.newTabSpec("Last");
        spec.setIndicator(txt);
        spec.setContent(intent);
        mTabHost.addTab(spec);
    }


    @Override
    public void onTabChanged(String arg0) {
        // do nothing
    }

    // 检票状态
    private ArrayList<Parameter> GetAllParamValues(String paramCode) {
        ArrayList<Parameter> list = new ArrayList<Parameter>();
        Parameter paramDetail_All = new Parameter();
        paramDetail_All.name = getString(R.string.search_all_status);
        paramDetail_All.value = "";
        list.add(paramDetail_All);

        String[] columns = {DBHelper.PARAM_CODE, DBHelper.PARAM_VALUE,
                DBHelper.PARAM_NAME};

        DBHelper dbHelper = new DBHelper(TrainADTabActivity.this);
        Cursor cursor = null;
        try {
            cursor = dbHelper.exeSql(DBHelper.PARAM_TABLE_NAME, columns,
                    DBHelper.PARAM_CODE + " = '" + paramCode + "'", null, null,
                    null, null);

            if (null != cursor && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Parameter paramDetail = new Parameter();
                    paramDetail.name = cursor.getString(cursor
                            .getColumnIndex("DetailName"));
                    paramDetail.value = cursor.getString(cursor
                            .getColumnIndex("ParamValue"));

                    list.add(paramDetail);
                    cursor.moveToNext();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            dbHelper.closeCursor(cursor);
            dbHelper.close();
        }

        return list;
    }

    /**
     * 获取检票口列表
     *
     * @return
     */
    private List<CheckPortResult.DataBean> getCheckPort() {
        List<CheckPortResult.DataBean> list = new ArrayList<>();
        HashMap<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("sessionId", Property.SessionId);
        paramValues.put("stationCode", mStationCode);
        paramValues.put("pId", "JP");
        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_GET_TASKWORKSPACEBYPID;

        WebServiceManager webServiceManager = new WebServiceManager(TrainADTabActivity.this,
                methodName, paramValues);
        String result = webServiceManager.OpenConnect(methodPath);

        if (result == null || result.equals("")) {
            return null;
        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                list = CheckPortResult.parseToList(result);
                break;
            case Constant.EXCEPTION:
            default:
                break;
        }

        return list;

    }

    // 站台列表
    private ArrayList<TaskWorkspace> GetPlatformWorkspace() {
        ArrayList<TaskWorkspace> list = new ArrayList<TaskWorkspace>();
        HashMap<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("sessionId", Property.SessionId);
        paramValues.put("stationCode", mStationCode);
        paramValues.put("pId", "ZT");
        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_GET_TASKWORKSPACEBYPID;

        WebServiceManager webServiceManager = new WebServiceManager(TrainADTabActivity.this,
                methodName, paramValues);
        String result = webServiceManager.OpenConnect(methodPath);

        if (result == null || result.equals("")) {
            return null;
        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                list = TaskWorkspace.ParseFromString(result);
                break;
            case Constant.EXCEPTION:
            default:
                break;
        }

        return list;
    }

    // 获取当前站的站台列表
    private ArrayList<TaskWorkspace> GetPlatformByStation() {
        ArrayList<TaskWorkspace> list = new ArrayList<TaskWorkspace>();
        TaskWorkspace plat = new TaskWorkspace();
        plat.TwId = 0;
        plat.Workspace = getString(R.string.search_all_platform);
        plat.StationCode = mStationCode;
        list.add(plat);

        if (mAllPlatformList == null || mAllPlatformList.size() == 0)
            return list;

        for (TaskWorkspace obj : mAllPlatformList) {
            if (obj.StationCode.equals(mStationCode))
                list.add(obj);
        }

        return list;
    }

    //选择检票口
    private void selectCheckPort() {
        List<String> checkPortList = new ArrayList<>();
        checkPortList.add(getResources().getString(R.string.checkposition));
        if (mAllCheckPortList == null || mAllCheckPortList.size() <= 0) {

        } else {
            for (CheckPortResult.DataBean dataBean : mAllCheckPortList) {
                if (Property.StationCode.equals(dataBean.getStationCode())) {
                    checkPortList.add(dataBean.getWorkspace());
                }
            }
        }
        final String[] checkPortArray = new String[checkPortList.size()];
        for (int i = 0; i < checkPortList.size(); i++) {
            checkPortArray[i] = checkPortList.get(i);
        }
        AlertDialog dlg = new AlertDialog.Builder(TrainADTabActivity.this)
                .setTitle("选择检票口")
                .setItems(checkPortArray, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        btnCheckPort.setText(checkPortArray[item]);
                        if (item == 0) {
                            strCheckPort = "";
                        } else {
                            strCheckPort = checkPortArray[item];
                        }
                    }
                }).create();
        dlg.show();
    }

    // 选择检票状态
    private void showSelectStatusDlg() {
        if (null != mParameterList) {
            String[] m = new String[mParameterList.size()];
            for (int i = 0; i < mParameterList.size(); i++) {
                m[i] = mParameterList.get(i).name;
            }

            AlertDialog dlg = new AlertDialog.Builder(TrainADTabActivity.this)
                    .setTitle("选择检票状态")
                    .setItems(m, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            if (item < mParameterList.size()) {
                                btnStatus.setText(mParameterList.get(item).name);
                                if (item == 0) {
                                    strStatus = "";
                                } else {
                                    strStatus = mParameterList.get(item).value;
                                }
                            }
                        }
                    }).create();
            dlg.show();
        }
    }


    // 选择站台
    private void showSelectPlatformDlg() {
        if (null != mPlatformList) {
            String[] m = new String[mPlatformList.size()];
            for (int i = 0; i < mPlatformList.size(); i++) {
                m[i] = mPlatformList.get(i).Workspace;
            }

            final AlertDialog dlg = new AlertDialog.Builder(TrainADTabActivity.this)
                    .setTitle("选择站台")
                    .setItems(m, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            if (item < mPlatformList.size()) {
                                btnPlatform.setText(mPlatformList.get(item).Workspace);
                                if (item == 0) {
                                    mTwId = TWID_INIT;
                                } else {
                                    mTwId = mPlatformList.get(item).TwId;
                                }
                            }
                        }
                    }).create();
            dlg.show();
        }
    }

    ;

    // 选择所属站
    private void showSelectStaionDlg() {
        if (null != Property.ChargeStation) {
            String[] m = new String[Property.ChargeStation.size()];
            for (int i = 0; i < Property.ChargeStation.size(); i++) {
                m[i] = Property.ChargeStation.get(i).Name;
            }

            AlertDialog dlg = new AlertDialog.Builder(TrainADTabActivity.this)
                    .setTitle("")
                    .setItems(m, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            if (item < Property.ChargeStation.size()) {
                                mStationCode = Property.ChargeStation.get(item).Code;
                                mStationName = Property.ChargeStation.get(item).Name;
                                station.setText("("
                                        + Property.ChargeStation.get(item).Name
                                        + ")");

                                Intent intent = new Intent();
                                intent.putExtra(EXTRA_FLAG, FLAG_STATIONCHANGE);
                                intent.setAction(ACTION_TRAINAD_SEARCH);
                                sendBroadcast(intent);

                                new Thread() {
                                    public void run() {
                                        mPlatformList = GetPlatformByStation();
                                    }
                                }.start();
                            }
                        }
                    }).create();
            dlg.show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.station:
                if (Property.ChargeStation == null
                        || Property.ChargeStation.size() == 0)
                    return;

                showSelectStaionDlg();
                break;
            case R.id.btnset:
                LinearLayout layoutSearch = (LinearLayout) findViewById(R.id.layoutSearch);
                if (layoutSearch.getVisibility() == View.VISIBLE) {
                    layoutSearch.setVisibility(View.GONE);
                } else {
                    layoutSearch.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btnReturn:
                finish();
                break;
            case R.id.btnSearch:
                if (beginTime != null && beginTime.length() != 0
                        && endTime != null && endTime.length() != 0) {
                    if (beginTime.compareTo(endTime) >= 0) {
                        Toast.makeText(TrainADTabActivity.this, R.string.task_notify_invalidtime, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                String trainNo = edtSearch.getText().toString().trim();
                Intent intent = new Intent();
                intent.putExtra(EXTRA_FLAG, FLAG_SEARCH);
                intent.putExtra(EXTRA_TRAINNO, trainNo);
                intent.putExtra(EXTRA_STATUS, strStatus);
                intent.putExtra(EXTRA_CHECK_PORT, strCheckPort);
                intent.putExtra(EXTRA_TWID, mTwId);
                intent.putExtra(EXTRA_STARTTIME, beginTime);
                intent.putExtra(EXTRA_ENDTIME, endTime);
                intent.setAction(ACTION_TRAINAD_SEARCH);
                sendBroadcast(intent);
                break;
            case R.id.btnStatus:
                showSelectStatusDlg();
                break;
            case R.id.btnPlatform:
                showSelectPlatformDlg();
                break;
            case R.id.btnCheckPort:
                selectCheckPort();
                break;
            case R.id.btnBegintime:
                new TimePickerDialog(this, onTimeSetLis(btnBegintime),
                        mBeginTimeHour, mBeginTimeMinute, false).show();
                break;
            case R.id.btnEndtime:
                new TimePickerDialog(this, onTimeSetLis(btnEndtime), mEndTimeHour,
                        mEndTimeMinute, false).show();
                break;
            default:
                break;
        }
    }

    ;

    // 时间控件
    private OnTimeSetListener onTimeSetLis(final Button btn) {
        OnTimeSetListener listener = new OnTimeSetListener() {
            @SuppressLint("SimpleDateFormat")
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String str = new StringBuilder().append(pad(hourOfDay)).append(":")
                        .append(pad(minute)).toString();
                btn.setText(str);

                if (btn.getId() == R.id.btnBegintime) {
                    mBeginTimeHour = hourOfDay;
                    mBeginTimeMinute = minute;
                    beginTime = str + ":00";
                } else if (btn.getId() == R.id.btnEndtime) {
                    mEndTimeHour = hourOfDay;
                    mEndTimeMinute = minute;
                    endTime = str + ":00";
                }
            }
        };
        return listener;
    }

    private String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
}
