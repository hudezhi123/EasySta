package easyway.Mobile.PassengerTrafficLog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.SellTicktLog.SellLogListActivity;
import easyway.Mobile.util.DateLine;
import easyway.Mobile.util.DateUtil;
import easyway.Mobile.util.IDateLineListener;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.LogUtil;
import easyway.Mobile.util.PullRefreshListView;
import easyway.Mobile.util.PullRefreshListView.OnRefreshListener;

/*
 *  记事本列表
 */
public class LogListActivity extends ActivityEx implements OnClickListener {
    private PullRefreshListView mListView;
    private PtlListAdapter mAdapter;
    private DateLine dateline;
    private Button addBtn;
    private List<SearchPtlLogBean.DataBean> mList;
    private SearchPtlLogBean searchPtlLogBean;

    private boolean isPullRefresh = false;

    public static final int SUCCESS_BUT_NO_DATA = 1;
    public static final int DATE_CHANGE = 2;
    public static final int SUCCESS = 3;
    public static final int EXCEPTION = 4;
    public static final int ERROR = 5;

    @SuppressLint("HandlerLeak")
    private Handler myhandle = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            closeProgressDialog();
            switch (msg.what) {
                case SUCCESS:
                    if (isPullRefresh) {
                        isPullRefresh = false;
                        mListView.onRefreshComplete();
                    }
                    mAdapter = new PtlListAdapter(LogListActivity.this, mList);
                    mListView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();

                    break;
                case DATE_CHANGE:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getData();
                        }
                    }).start();
                    break;
                case SUCCESS_BUT_NO_DATA:
                    mListView.onRefreshComplete();
                    if (mList != null && mList.size() > 0) {
                        mList.clear();
                    }
//                    showToast((String) msg.obj);
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
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.caution_list);
        initView();


    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }).start();
    }

    private void initView() {
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("客运工作日志");
        addBtn = (Button) findViewById(R.id.btnset);
        addBtn.setVisibility(View.VISIBLE);
        addBtn.setText("新建");
        addBtn.setOnClickListener(this);
        addBtn.setText(R.string.New_sell_ticket_build);
        addBtn.setOnClickListener(this);
        dateline = (DateLine) findViewById(R.id.dateline);
        dateline.setListener(new IDateLineListener() {
            @Override
            public void DateChange() {
                if (dateline.isCurrentDay() || dateline.isYesterday()) {
                    addBtn.setVisibility(View.VISIBLE);
                    addBtn.setEnabled(true);
                } else {
                    addBtn.setVisibility(View.INVISIBLE);
                    addBtn.setEnabled(false);
                }
                myhandle.sendEmptyMessage(DATE_CHANGE); // 日期改变
            }
        });
        Button btnReturn = (Button) findViewById(R.id.btnReturn);
        if (btnReturn != null) {
            btnReturn.setOnClickListener(this);
        }


        mList = new ArrayList<>();
        mListView = (PullRefreshListView) findViewById(R.id.cautionlist);
        mAdapter = new PtlListAdapter(this, mList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean IsEditable = false;
                if (dateline.isYesterday() | dateline.isCurrentDay()) {
                    IsEditable = true;
                }
                if (dateline.isYesterday()) {
                    String time = mList.get((int) id).getWorkTime();
                    boolean isDay = DateUtil.isClassesOfDay(time);
                    if (isDay) {//今天白班时间，不能编辑昨天白班日志
                        IsEditable = false;
                    }
                }
                Intent intent = new Intent(LogListActivity.this, NewEditLogActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("LogInfo", mList.get(position - 1));
                bundle.putString("Flag", "Show");
                bundle.putBoolean("IsEditable", IsEditable);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        mListView.setonRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                isPullRefresh = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getData();
                    }
                }).start();

            }
        });


    }

    // 获取数据
    private void getData() {
        String methodPath = Constant.MP_DEVFAULT;
        String methodName = Constant.SEARCHPTT_WORKLOG;
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("date", dateline.getDate());
        WebServiceManager webServiceManager = new WebServiceManager(
                LogListActivity.this, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        if (result != null && !TextUtils.isEmpty(result)) {
            Message msg = myhandle.obtainMessage();
            boolean MsgType = JsonUtil.GetJsonBoolean(result, "MsgType");
            if (MsgType) {
                JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
                if (jsonArray == null || jsonArray.length() == 0) {
                    msg.obj = "获取的数据为空";
                    msg.what = SUCCESS_BUT_NO_DATA;
                    myhandle.sendMessage(msg);
                } else {
                    searchPtlLogBean = SearchPtlLogBean.objectFromData(result);
                    mList = searchPtlLogBean.getData();
                    msg.what = SUCCESS;
                    myhandle.sendMessage(msg);
                }
            } else {
                msg.what = EXCEPTION;
                myhandle.sendMessage(msg);
            }
        } else {
            myhandle.sendEmptyMessage(ERROR);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnset: // 添加记录
                String WeekDay = dateline.getWeekday();
                String date = dateline.getDate();
                if (mList.size() >= 2) {
                    Toast.makeText(LogListActivity.this, getResources().getString(R.string.already_two_classes), Toast.LENGTH_SHORT).show();
                } else {
                    boolean is0_8 = DateUtil.isClasses0_8();
                    boolean isNinht = DateUtil.isClassesOfNight();

                    if (dateline.isCurrentDay()) {
                        if (is0_8) {//今天0-8点，不能新建今天日志
                            Toast.makeText(LogListActivity.this, getResources().getString(R.string.no_day_classes), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else if (dateline.isYesterday()) {
                        if (!is0_8) {//非今天0-8点，不能新建昨天日志
                            Toast.makeText(LogListActivity.this, getResources().getString(R.string.no_yesterday_classes), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    if (mList.size() == 1) {
                        String alreadyLogTime = mList.get(0).getWorkDate();
                        boolean isDay = DateUtil.isClassesOfDay(alreadyLogTime);

                        if (dateline.isYesterday()) {
                            if (!isDay) {//有一条昨天夜班工作日志
                                Toast.makeText(LogListActivity.this, getResources().getString(R.string.already_night_classes), Toast.LENGTH_SHORT).show();
                                return;
                            }

                        } else {
                            if (!DateUtil.isClassesOfNight()) {//今天白班时间
                                if (isDay) {//有一条白班日志
                                    Toast.makeText(LogListActivity.this, getResources().getString(R.string.already_day_classes), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                        }
                    }
                    Intent intent = new Intent(LogListActivity.this,
                            NewEditLogActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("WeekDay", WeekDay);
                    bundle.putString("Date", date);
                    intent.putExtra("isToday", dateline.isCurrentDay());
                    bundle.putString("Flag", "New");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }


                break;
            case R.id.btnReturn: // 返回
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DateLine.FLAG_DATE = 0;
    }

}
