package easyway.Mobile.SellTicktLog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.PassengerTrafficLog.LogListActivity;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.DateLine;
import easyway.Mobile.util.DateUtil;
import easyway.Mobile.util.IDateLineListener;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.PullRefreshListView;

public class SellLogListActivity extends ActivityEx implements View.OnClickListener {

    public static final int SUCCESS_BUT_NO_DATA = 1;
    public static final int DATE_CHANGE = 2;
    public static final int SUCCESS = 3;
    public static final int EXCEPTION = 4;
    public static final int ERROR = 5;
    private PullRefreshListView mListView;
    private DateLine dateline;
    private SellTicketAdapter mAdapter;
    private List<SellTicketLog> mList;
    private String now_date = "";

    private Button addBtn;
    private boolean isOnCreate = false;

    @SuppressLint("HandlerLeak")
    private Handler myhandle = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            closeProgressDialog();
            switch (msg.what) {
                case SUCCESS_BUT_NO_DATA:
                    mListView.onRefreshComplete();
                    if (mList != null && mList.size() > 0) {
                        mList.clear();
                    }
                    mAdapter.setData(mList);
//                    showToast((String) msg.obj);
                    break;
                case SUCCESS:
                    if (mList != null && mList.size() > 0) {
                        mList.clear();
                    }
                    mList = (List<SellTicketLog>) msg.obj;
                    mAdapter.setData(mList);
                    mListView.onRefreshComplete();
                    break;
                case ERROR:
                    mListView.onRefreshComplete();
                    showErrMsg("获取数据失败！");
                    break;
                case EXCEPTION:
                    mListView.onRefreshComplete();
                    String ExceptionMsg = (String) msg.obj;
                    showErrMsg(ExceptionMsg);
                    break;
                case DATE_CHANGE:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getData();
                        }
                    }).start();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_log_list);
        DateLine.FLAG_DATE = 0;
        isOnCreate = true;
        init();
        getDataFromNet();
    }

    private void getDataFromNet() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isOnCreate) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getData();
                }
            }).start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isOnCreate = false;
    }

    private void init() {
        mList = new ArrayList<>();
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.Sell_Ticket_Log);
        addBtn = (Button) findViewById(R.id.btnset);
        addBtn.setVisibility(View.VISIBLE);
        addBtn.setText(R.string.New_sell_ticket_build);
        addBtn.setOnClickListener(this);
        Button btnReturn = (Button) findViewById(R.id.btnReturn);
        if (btnReturn != null) {
            btnReturn.setOnClickListener(this);
        }
        dateline = (DateLine) findViewById(R.id.dateline_sell_ticket_log);
        dateline.setListener(new IDateLineListener() {
            @Override
            public void DateChange() {
                if (dateline.isCurrentDay() || DateLine.FLAG_DATE == -1) {
                    addBtn.setVisibility(View.VISIBLE);
                    addBtn.setEnabled(true);
                } else {
                    addBtn.setVisibility(View.INVISIBLE);
                    addBtn.setEnabled(false);

                }
                now_date = dateline.getDate();
                myhandle.sendEmptyMessage(DATE_CHANGE); // 日期改变
            }
        });

        mListView = (PullRefreshListView) findViewById(R.id.listview_sell_ticket_log_list);
        mAdapter = new SellTicketAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setonRefreshListener(new PullRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getData();
                    }
                }).start();
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SellTicketLog log = mList.get((int) id);
                Intent intent = new Intent();
                intent.setClass(SellLogListActivity.this, SellTicketLogBuildActivity.class);
                Bundle bundle = new Bundle();
                if (dateline.isCurrentDay()) {
                    bundle.putBoolean("IsEditable", true);
                } else {
                    if (DateUtil.NIGHT == DateUtil.getWhiteOrNight(log.WorkDate)) {
                        Calendar ca = Calendar.getInstance();
                        int hour = ca.get(Calendar.HOUR_OF_DAY);
                        if (hour < 8) {
                            bundle.putBoolean("IsEditable", true);
                        } else {
                            bundle.putBoolean("IsEditable", false);
                        }
                    } else {
                        bundle.putBoolean("IsEditable", false);
                    }
                }
                bundle.putString("Flag", "Show");
                bundle.putSerializable("LogItem", log);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        });
    }

    // 获取数据
    private void getData() {
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        if (TextUtils.isEmpty(now_date)) {
            now_date = dateline.getDate();
        }
        parmValues.put("date", now_date);
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.SEARCH_TICKET_LOG;
        WebServiceManager webServiceManager = new WebServiceManager(
                SellLogListActivity.this, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        if (TextUtils.isEmpty(result)) {
            myhandle.sendEmptyMessage(ERROR);
            return;
        }
        Message msg = myhandle.obtainMessage();
        boolean MsgType = JsonUtil.GetJsonBoolean(result, "MsgType");
        if (MsgType) {
            JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
            if (jsonArray == null || jsonArray.length() == 0) {
                msg.what = SUCCESS_BUT_NO_DATA;
                myhandle.sendMessage(msg);
            } else {
                msg.obj = SellTicketLog.JsonArray2List(jsonArray);
                msg.what = SUCCESS;
                myhandle.sendMessage(msg);
            }
        } else {
            msg.what = EXCEPTION;
            myhandle.sendMessage(msg);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnset: // 添加记录
                ableToBuild();
                break;
            case R.id.btnReturn: // 返回
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 判断是否可以新建
     *
     * @return
     */
    private void ableToBuild() {

        Calendar ca = Calendar.getInstance();
        int hour = ca.get(Calendar.HOUR_OF_DAY);
        switch (DateLine.FLAG_DATE) {
            case 0:
                if (mList == null || mList.size() == 0) {
                    if (!DateUtil.IsDaytime()) {
                        if (hour < 8) {
                            showToast(R.string.day_not_reach_day);
                        } else {
                            newLog();
                        }
                    } else {
                        newLog();
                    }
                } else if (mList.size() == 1) {
                    if (DateUtil.DAYTIME == DateUtil.getWhiteOrNight(mList.get(0).WorkDate)) {
                        if (DateUtil.IsDaytime()) {
                            showToast(R.string.already_day_classes);
                        } else {
                            newLog();
                        }
                    } else if (DateUtil.NIGHT == DateUtil.getWhiteOrNight(mList.get(0).WorkDate)) {
                        showToast(R.string.already_night_classes);
                    }
                } else if (mList.size() == 2) {
                    showToast(R.string.day_night_already_has);
                }
                break;
            case -1:
                if (hour < 8) {
                    if (mList == null || mList.size() == 0) {
                        newLog();
                    } else if (mList.size() == 1) {
                        if (DateUtil.DAYTIME == DateUtil.getWhiteOrNight(mList.get(0).WorkDate)) {
                            newLog();
                        } else {
                            showToast(R.string.already_night_classes);
                        }
                    } else if (mList.size() >= 2) {
                        showToast(R.string.day_night_already_has);
                    }
                } else {
                    showToast(R.string.no_yesterday_classes);
                }
                break;
        }
    }

    private void newLog() {
        String WeekDay = dateline.getWeekday();
        String date = dateline.getDate();
        Intent intent = new Intent(SellLogListActivity.this,
                SellTicketLogBuildActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("Flag", "New");
        intent.putExtra("WeekDay", WeekDay);
        intent.putExtra("Date", date);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DateLine.FLAG_DATE = 0;
    }
}
