package easyway.Mobile.TrainAD;

import java.util.ArrayList;
import java.util.HashMap;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.DBHelper;
import easyway.Mobile.Data.TB_OCS_MTMInfo;
import easyway.Mobile.Data.VIAStation;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.bean.MPSLog;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.StringUtil;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/*
 * 列车时刻表
 */
public class TrainADSchedule extends ActivityEx {
    private ArrayList<VIAStation> mlist;
    private ArrayList<MPSLog> mMpsLoglist;

    private TextView txtOrigin;
    private TextView txtTerminal;
    private TextView txtTrainNo;
    private TextView txtTotalTime;
    private Button btnView;
    private Button btnDrviers;

    private ListView lstSchedule;
    private ListView lstDrviers;
    private ListView lstMpsChang;

    private String mTrainNo;
    private String mPlanDate;
    private String stationCode;
    private TrainADScheduleAdapter mAdapter;
    private TrainADScheduleMTMAdapter mMtmAdapter;
    private TrainChangeAdapter mpsAdapter;

    private final int MSG_GET_VIASTATION = 0;
    private final int MSG_OCS_DATA_FAILED = 1;
    private final int MSG_OCS_DATA_SUCCESS = 2;
    public static final int MSG_OCS_MTM_DATA_FAILED = 3;
    private final int MSG_OCS_MTM_DATA_SUCCESS = 4;
    private  final int MSG_GET_MPS_Change=5;
    private final int MSG_GET_MPS_ChangeF=6;
    @SuppressLint("HandlerLeak")
    private Handler myhandle = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GET_VIASTATION:
                    if(mlist!=null&&mlist.size()>0){
                        mlist.clear();
                    }
                    mlist = (ArrayList<VIAStation>) msg.obj;
                    mAdapter.setData(mlist);
                    mAdapter.notifyDataSetChanged();
                    if (mlist==null) {
                        showToast("同步基础数据失败或者未完成基础数据同步，请稍后再试");
                        break;
                    }
                    if (mlist.size()==0) {
                        showToast("同步基础数据失败或者未完成基础数据同步，请稍后再试");
                        break;
                    }
                    setTrainInfo();
                    break;
                case MSG_OCS_DATA_FAILED:
                case MSG_OCS_MTM_DATA_FAILED:
                    showToast(errMsg);
                    break;
                case MSG_OCS_DATA_SUCCESS:
                    mAdapter.setData(mlist);
                    mAdapter.notifyDataSetChanged();
                    break;
                case MSG_OCS_MTM_DATA_SUCCESS:
                    mMtmAdapter.setData((ArrayList<TB_OCS_MTMInfo>) msg.obj);
                    mMtmAdapter.notifyDataSetChanged();
                    break;
                case MSG_GET_MPS_Change:
                    mpsAdapter.setData((ArrayList<MPSLog>) msg.obj);
                    mpsAdapter.notifyDataSetChanged();
                    break;
                case MSG_GET_MPS_ChangeF:
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trainad_schedule);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.title_schedule);

        mTrainNo = getIntent().getStringExtra("TrainNo");

        txtTrainNo = (TextView) findViewById(R.id.txtTrainNo);
        txtTrainNo.setText(mTrainNo);

        mPlanDate = getIntent().getStringExtra("PlanDate");
        stationCode=getIntent().getStringExtra("StationCode");

        txtOrigin = (TextView) findViewById(R.id.txtOrigin);
        txtTerminal = (TextView) findViewById(R.id.txtTerminal);
        txtTotalTime = (TextView) findViewById(R.id.txtTotalTime);

        txtOrigin.getPaint().setFakeBoldText(true);
        txtTerminal.getPaint().setFakeBoldText(true);

        btnView = (Button) findViewById(R.id.btnView);
        btnDrviers = (Button) findViewById(R.id.btnDrviers);
        btnView.setOnClickListener(viewScheLis());
        btnDrviers.setOnClickListener(viewDriversLis());

        lstSchedule = (ListView) findViewById(R.id.lstSchedule);
        mAdapter = new TrainADScheduleAdapter(this, mlist);
        lstSchedule.setAdapter(mAdapter);

        lstDrviers = (ListView) findViewById(R.id.lstDrviers);
        mMtmAdapter = new TrainADScheduleMTMAdapter(this, null);
        lstDrviers.setAdapter(mMtmAdapter);

        lstMpsChang=(ListView)findViewById(R.id.lstMpsChange);
        mpsAdapter=new TrainChangeAdapter(this, null);
        lstMpsChang.setAdapter(mpsAdapter);

        loadChangeData();

        btnView.getPaint().setFakeBoldText(true);
        btnDrviers.getPaint().setFakeBoldText(false);

        new Thread() {
            public void run() {
                LoadVIAInfo(mTrainNo);
            }
        }.start();
    }



    private void loadChangeData(){
        new Thread(){
            @Override
            public void run() {
                HashMap<String, String> parmValues = new HashMap<String, String>();
                parmValues.put("sessionId", Property.SessionId);
                parmValues.put("trnoPro", mTrainNo);
                parmValues.put("planDate", "2017-02-03");
                parmValues.put("stationCode", stationCode);

                String methodPath = Constant.MP_TRAININFO;
                String methodName = Constant.MN_GET_MPSLog;
                WebServiceManager webServiceManager = new WebServiceManager(
                        getApplicationContext(), methodName, parmValues);
                String result = webServiceManager.OpenConnect(methodPath);

                if (StringUtil.isNullOrEmpty(result)) {
                    errMsg = getString(R.string.exp_getdata);
                    myhandle.sendEmptyMessage(MSG_GET_MPS_ChangeF);
                    return;
                }

                int Code = JsonUtil.GetJsonInt(result, "Code");
                switch (Code) {
                    case Constant.NORMAL:

                        ArrayList<MPSLog> list = MPSLog.ParseFromString(result);

                        Message message = new Message();
                        message.what = MSG_GET_MPS_Change;
                        message.obj = list;
                        myhandle.sendMessage(message);
                        break;
                    case Constant.EXCEPTION:
                    default:
                        errMsg = JsonUtil.GetJsonString(result, "Msg");
                        myhandle.sendEmptyMessage(MSG_GET_MPS_ChangeF);
                        break;
                }

            }
        }.start();

    }

    // 设置时刻表信息
    private void setTrainInfo() {
        if (mlist == null || mlist.size() == 0)
            return;
        int firstDeptDate = mlist.get(0).DepaDate;
        int lastDeptDate = mlist.get(mlist.size() - 1).ArrDate;
        String firstDeptTime = mlist.get(0).DepaTime;
        String lastDeptTime = mlist.get(mlist.size() - 1).ArrTime;

        int firstDateValue = firstDeptDate * 24 * 60
                + Integer.valueOf(firstDeptTime.split(":")[0]) * 60
                + Integer.valueOf(firstDeptTime.split(":")[1]);
        int lastDateValue = lastDeptDate * 24 * 60
                + Integer.valueOf(lastDeptTime.split(":")[0]) * 60
                + Integer.valueOf(lastDeptTime.split(":")[1]);
        int duringDateValue = lastDateValue - firstDateValue;
        int duringDay = duringDateValue / (24 * 60);
        duringDateValue = duringDateValue - duringDay * 24 * 60;
        int duringH = duringDateValue / 60;
        duringDateValue = duringDateValue - duringH * 60;
        int duringM = duringDateValue;

        txtTotalTime.setText(duringDay + getString(R.string.Day) + duringH
                + getString(R.string.Hour) + duringM + getString(R.string.Min));
        txtOrigin.setText(mlist.get(0).Station);
        txtTerminal.setText(mlist.get(mlist.size() - 1).Station);
    }

    // 获取车次途经站信息信息
    private void LoadVIAInfo(String trainNo) {
        if (trainNo == null)
            return;

        if (trainNo.length() == 0)
            return;

        ArrayList<VIAStation> list = new ArrayList<VIAStation>();
        DBHelper dbHelper = new DBHelper(TrainADSchedule.this);
        String[] columns = {DBHelper.VIASTATION_TRNO, DBHelper.VIASTATION_ID,
                DBHelper.VIASTATION_STRTSTN, DBHelper.VIASTATION_TILSTN,
                DBHelper.VIASTATION_STRTTIME, DBHelper.VIASTATION_TILTIME,
                DBHelper.VIASTATION_ORDER, DBHelper.VIASTATION_STATION,
                DBHelper.VIASTATION_ARRTIME, DBHelper.VIASTATION_ARRDATE,
                DBHelper.VIASTATION_DEPATIME, DBHelper.VIASTATION_DEPADATE,
                DBHelper.VIASTATION_STATIONATTR, DBHelper.VIASTATION_MILES};
        Cursor cursor = null;

        try {
            cursor = dbHelper.exeSql(DBHelper.VIASTATION_TABLE_NAME, columns,
                    DBHelper.VIASTATION_TRNO + " = '" + trainNo + "'", null,
                    null, null, DBHelper.VIASTATION_ORDER);

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    VIAStation station = new VIAStation();
                    station.ID = cursor.getInt(cursor
                            .getColumnIndex(DBHelper.VIASTATION_ID));
                    station.TRNO_TT = cursor.getString(cursor
                            .getColumnIndex(DBHelper.VIASTATION_TRNO));
                    station.STRTSTN_TT = cursor.getString(cursor
                            .getColumnIndex(DBHelper.VIASTATION_STRTSTN));
                    station.TILSTN_TT = cursor.getString(cursor
                            .getColumnIndex(DBHelper.VIASTATION_TILSTN));
                    station.STRTTIME_TT = cursor.getString(cursor
                            .getColumnIndex(DBHelper.VIASTATION_STRTTIME));
                    station.TILTIME_TT = cursor.getString(cursor
                            .getColumnIndex(DBHelper.VIASTATION_TILTIME));
                    station.StationOrder = cursor.getInt(cursor
                            .getColumnIndex(DBHelper.VIASTATION_ORDER));
                    station.Station = cursor.getString(cursor
                            .getColumnIndex(DBHelper.VIASTATION_STATION));
                    station.ArrTime = cursor.getString(cursor
                            .getColumnIndex(DBHelper.VIASTATION_ARRTIME));
                    station.ArrDate = cursor.getInt(cursor
                            .getColumnIndex(DBHelper.VIASTATION_ARRDATE));
                    station.DepaTime = cursor.getString(cursor
                            .getColumnIndex(DBHelper.VIASTATION_DEPATIME));
                    station.DepaDate = cursor.getInt(cursor
                            .getColumnIndex(DBHelper.VIASTATION_DEPADATE));
                    station.StationAttr = cursor.getString(cursor
                            .getColumnIndex(DBHelper.VIASTATION_STATIONATTR));
                    station.Miles = cursor.getInt(cursor
                            .getColumnIndex(DBHelper.VIASTATION_MILES));

                    list.add(station);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            dbHelper.closeCursor(cursor);
            dbHelper.close();
        }

        Message msg = new Message();
        msg.obj = list;
        msg.what = MSG_GET_VIASTATION;
        myhandle.sendMessage(msg);
    }

    private OnClickListener viewScheLis() {
        return new OnClickListener() {
            public void onClick(View v) {
                lstSchedule.setVisibility(View.VISIBLE);
                lstMpsChang.setVisibility(View.GONE);

                btnView.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.btn_tab_left_selected));
                btnDrviers.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.btn_tab_right_normal));
                btnView.getPaint().setFakeBoldText(true);
                btnDrviers.getPaint().setFakeBoldText(false);
            }
        };
    }

    private OnClickListener viewDriversLis() {
        return new OnClickListener() {
            public void onClick(View v) {
                lstSchedule.setVisibility(View.GONE);
                lstMpsChang.setVisibility(View.VISIBLE);
                btnDrviers.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.btn_tab_right_selected));
                btnView.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.btn_tab_left_normal));

                btnView.getPaint().setFakeBoldText(false);
                btnDrviers.getPaint().setFakeBoldText(true);
            }
        };
    }
}
