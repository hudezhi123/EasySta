package easyway.Mobile.TrainAD;

import java.util.ArrayList;
import java.util.HashMap;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.TrainGoTo;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.PullRefreshListView;
import easyway.Mobile.util.PullRefreshListView.OnRefreshListener;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/*
 * 到发通告 - 所有
 */
public class TrainADAllActivity extends ActivityEx {
    private ArrayList<TrainGoTo> trainGotoList = new ArrayList<TrainGoTo>();
    private final int limit = 100;
    private int startIndex = 0;
    private long totalItems = 0;
    private TrainADAllAdapter mAdpater;
    private boolean isPullRefresh = false;
    private PullRefreshListView gv_queryList;
    private String mStationCode = "";

    private SearchReceiver receiver;

    private String landName = "";
    private String inticketst_ptti_init = "";
    private String checkPort = "";
    private String startTime = "";
    private String endTime = "";
    private String trainNo = "";
    private long TwId = TrainADTabActivity.TWID_INIT;

    private Button btnShowMore = null;
    private boolean addedFoot = false;

    private final int MSG_GETDATA_FAIL = 0;
    private final int MSG_GETDATA_SUCCEED = 1;
    private final int MSG_RECEIVE_BROADCAST = 2;
    private final int MSG_GETDATA_SUCCEED_ZERO = 3;

    @SuppressLint("HandlerLeak")
    private Handler myhandle = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            closeProgressDialog();
            if (isPullRefresh) {
                gv_queryList.onRefreshComplete();
                isPullRefresh = false;
            }

            switch (msg.what) {
                case MSG_GETDATA_FAIL:
                    showToast(errMsg);
                    break;
                case MSG_GETDATA_SUCCEED_ZERO:
//                    Toast.makeText(TrainADAllActivity.this, "Code = 0", Toast.LENGTH_SHORT).show();
                case MSG_GETDATA_SUCCEED:
                    if (trainGotoList == null)
                        trainGotoList = new ArrayList<TrainGoTo>();

                    if (startIndex == 0)
                        trainGotoList.clear();

                    trainGotoList.addAll((ArrayList<TrainGoTo>) msg.obj);
                    mAdpater.setData(trainGotoList);
                    mAdpater.notifyDataSetChanged();
                    showHideFoot();
                    break;

                case MSG_RECEIVE_BROADCAST:
                    startIndex = 0;
                    getData();
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trainad_all);
        mStationCode = TrainADTabActivity.mStationCode;

        initView();
        startIndex = 0;
        getData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mStationCode.equals(TrainADTabActivity.mStationCode)) {
            mStationCode = TrainADTabActivity.mStationCode;
            myhandle.sendEmptyMessage(MSG_RECEIVE_BROADCAST);
        }
        regReceiver();
    }

    public void onPause() {
        super.onPause();

        if (receiver != null) {
            try {
                unregisterReceiver(receiver);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void initView() {
        gv_queryList = (PullRefreshListView) findViewById(R.id.lstAll);
        mAdpater = new TrainADAllAdapter(this, trainGotoList);
        gv_queryList.setAdapter(mAdpater);
        gv_queryList.setonRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                isPullRefresh = true;
                startIndex = 0;
                getData();
            }
        });

        btnShowMore = new Button(TrainADAllActivity.this);
        btnShowMore.setText(R.string.search_show_more);
        btnShowMore.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (trainGotoList == null)
                    startIndex = 0;
                else
                    startIndex = trainGotoList.size();

                getData();
            }
        });
    }

    // 获取股道占用信息
    private void GetTrainAll() {
        String searchValues = "TrainNo=" + trainNo + ";LaneName=" + landName
                + ";INTICKETST_PTTI_Init=" + inticketst_ptti_init
                + ";INTICKET_PTTI=" + checkPort
                + ";startTime=" + startTime
                + ";endTime=" + endTime;
        if (TwId != TrainADTabActivity.TWID_INIT) {
            searchValues += ";TwId=" + TwId;
        }

        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("limit", String.valueOf(limit));
        parmValues.put("start", String.valueOf(startIndex));
        parmValues.put("dir", "");
        parmValues.put("sort", "");
        parmValues.put("searchValues", searchValues);
        if (mStationCode != null)
            parmValues.put("stationCode", mStationCode);
        String methodPath = Constant.MP_TRAININFO;
        String methodName = Constant.MN_GET_TRAINGO;
        WebServiceManager webServiceManager = new WebServiceManager(
                getApplicationContext(), methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);

        if (result == null || result.equals("")) {
            errMsg = getString(R.string.exp_getdata);
            myhandle.sendEmptyMessage(MSG_GETDATA_FAIL);
            return;
        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL_ZERO:

            case Constant.NORMAL:
                ArrayList<TrainGoTo> list = TrainGoTo.ParseFromString(result);
                totalItems = JsonUtil.GetJsonLong(result, "total");

                Message message = new Message();
                message.what = MSG_GETDATA_SUCCEED;
                message.obj = list;
                myhandle.sendMessage(message);
                break;
            case Constant.EXCEPTION:
            default:
                errMsg = JsonUtil.GetJsonString(result, "Msg");
                myhandle.sendEmptyMessage(MSG_GETDATA_FAIL);
                break;
        }
    }

    // 设置“显示更多”按钮
    private void showHideFoot() {
        if (trainGotoList.size() == 0) {
            if (addedFoot) {
                addedFoot = false;
                gv_queryList.removeFooterView(btnShowMore);
            }
            return;
        }

        if (trainGotoList.size() >= totalItems) {
            if (addedFoot) {
                addedFoot = false;
                gv_queryList.removeFooterView(btnShowMore);
            }
        } else {
            if (!addedFoot) {
                addedFoot = true;
                gv_queryList.addFooterView(btnShowMore);
            }
        }
    }

    // 获取数据
    private void getData() {
        showProgressDialog(R.string.GettingData);
        new Thread() {
            public void run() {
                GetTrainAll();
                trainNo = "";
                inticketst_ptti_init = "";
                checkPort = "";
                TwId = TrainADTabActivity.TWID_INIT;
                startTime = "";
                endTime = "";
            }
        }.start();
    }

    private void regReceiver() {
        receiver = new SearchReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(TrainADTabActivity.ACTION_TRAINAD_SEARCH);
        registerReceiver(receiver, filter);
    }

    // 自定义一个广播接收器
    public class SearchReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            int flag = bundle.getInt(TrainADTabActivity.EXTRA_FLAG);
            if (flag == TrainADTabActivity.FLAG_SEARCH) {
                trainNo = bundle.getString(TrainADTabActivity.EXTRA_TRAINNO);
                inticketst_ptti_init = bundle
                        .getString(TrainADTabActivity.EXTRA_STATUS);
                checkPort = bundle.getString(TrainADTabActivity.EXTRA_CHECK_PORT);
                TwId = bundle.getLong(TrainADTabActivity.EXTRA_TWID);
                startTime = bundle.getString(TrainADTabActivity.EXTRA_STARTTIME);
                endTime = bundle.getString(TrainADTabActivity.EXTRA_ENDTIME);
            } else if (flag == TrainADTabActivity.FLAG_STATIONCHANGE) {
                mStationCode = TrainADTabActivity.mStationCode;
            }

            myhandle.sendEmptyMessage(MSG_RECEIVE_BROADCAST);
        }
    }
}
