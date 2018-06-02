package easyway.Mobile.Patrol;

import java.util.ArrayList;
import java.util.HashMap;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.DateLine;
import easyway.Mobile.util.IDateLineListener;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.PullRefreshListView;
import easyway.Mobile.util.PullRefreshListView.OnRefreshListener;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/*
 *  客运巡检 列表
 */
public class PatrolBrowseActivity extends ActivityEx implements OnClickListener {
    private boolean isPullRefresh;
    private PullRefreshListView mListView;
    private PatrolBrowseAdapter mAdapter;
    private DateLine dateline;

    private final int MSG_GETDATA_FAIL = 0;
    private final int MSG_GETDATA_SUCC = 1;
    private final int MSG_DATE_CHANGE = 2;

    @SuppressLint("HandlerLeak")
    private Handler myhandle = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            closeProgressDialog();

            if (isPullRefresh) {
                isPullRefresh = false;
                mListView.onRefreshComplete();
            }

            switch (msg.what) {
                case MSG_GETDATA_FAIL:
                    showToast(errMsg);
                    mAdapter.setData(null);
                    break;
                case MSG_GETDATA_SUCC:
                    ArrayList<Patrol> list = (ArrayList<Patrol>) msg.obj;
                    mAdapter.setData(list);
                    break;
                case MSG_DATE_CHANGE:
                    getData();
                    break;
                case Constant.TEST_CODE:
                    Toast.makeText(PatrolBrowseActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patrol_browse);

        initView();
        getData();
    }

    private void initView() {
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.Patrol_Title);

        Button btnReturn = (Button) findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(this);

        dateline = (DateLine) findViewById(R.id.dateline);
        dateline.setListener(new IDateLineListener() {
            @Override
            public void DateChange() {
                myhandle.sendEmptyMessage(MSG_DATE_CHANGE);
            }
        });

        mListView = (PullRefreshListView) findViewById(R.id.patrollist);
        mAdapter = new PatrolBrowseAdapter(this, null);
        mListView.setAdapter(mAdapter);

        mListView.setonRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                isPullRefresh = true;
                getData();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnReturn:
                finish();
                break;
            default:
                break;
        }
    }

    // 获取数据
    private void getData() {
        showProgressDialog(R.string.GettingData);

        new Thread() {
            public void run() {
                getPatrol();
            }
        }.start();
    }

    // 获取巡检数据
    private void getPatrol() {
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        if (Property.OwnStation != null)
            parmValues.put("stationCode", Property.OwnStation.Code);
        parmValues.put("patrolDate", dateline.getDate());

        if (Property.OwnStation != null)
            parmValues.put("stationCode", Property.OwnStation.Code);
        String methodPath = Constant.MP_PATROL;
        String methodName = Constant.MN_GET_PATROL;

        WebServiceManager webServiceManager = new WebServiceManager(this,
                methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);

        if (result == null || result.equals("")) {
            errMsg = getString(R.string.exp_getdata);
            myhandle.sendEmptyMessage(MSG_GETDATA_FAIL);
            return;
        }
        // HDZ_LOG
//        Message msg = myhandle.obtainMessage();
//        msg.obj = result;
//        msg.what = Constant.TEST_CODE;
//        myhandle.sendMessage(msg);
        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL_ZERO:
            case Constant.NORMAL:
                ArrayList<Patrol> list = Patrol.ParseFromString(result);

                Message message = new Message();
                message.what = MSG_GETDATA_SUCC;
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
}
