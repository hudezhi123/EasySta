package easyway.Mobile.DefaultReportToRepair;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.AnnotateUtil;
import easyway.Mobile.util.BindView;
import easyway.Mobile.util.LogUtil;
import easyway.Mobile.util.PullRefreshListView;

/**
 * Created by JSC on 2017/12/4.
 */

public class RepairPicActivity extends ActivityEx {

    @BindView(id = R.id.btnReturn)
    Button btnReturn;
    @BindView(id = R.id.title)
    TextView title;
    @BindView(id = R.id.devreport_list)
    PullRefreshListView devreportList;

    private List<RepairPicBean.DataBean> list;
    private RepairPicAdapter repairPicAdapter;
    private RepairPicBean repairPicBean;

    public static final int SUCCESS_BUT_NO_DATA = 1;
    public static final int SUCCESS = 2;
    public static final int EXCEPTION = 3;
    public static final int ERROR = 4;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS:
                    devreportList.onRefreshComplete();
                    LogUtil.e("list==" + list);
                    repairPicAdapter = new RepairPicAdapter(RepairPicActivity.this, list);
                    devreportList.setAdapter(repairPicAdapter);
                    repairPicAdapter.notifyDataSetChanged();
                    break;
                case SUCCESS_BUT_NO_DATA:
                    devreportList.onRefreshComplete();
                    if (list != null && list.size() > 0) {
                        list.clear();
                    }
                    showToast((String) msg.obj);
                    break;
                case EXCEPTION:
                    devreportList.onRefreshComplete();
                    String ExceptionMsg = (String) msg.obj;
                    showErrMsg(ExceptionMsg);
                    break;
                case ERROR:
                    devreportList.onRefreshComplete();
                    showErrMsg("获取数据失败！");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_report_list);
        AnnotateUtil.initBindView(this);
        initView();
        getData();
    }

    private void getData() {


        Intent intent = getIntent();
        String repairId = intent.getStringExtra("repairId");
        if (null != repairId && !repairId.isEmpty()) {
            final HashMap<String, String> paramValue = new HashMap<>();
            paramValue.put("sessionId", Property.SessionId);
            paramValue.put("stationCode", Property.StationCode);
            paramValue.put("repairId", repairId);
            final String methodName = Constant.QUERY_REPAIR_PIC_BY_REPAIRID;
            final String methodPath = Constant.MP_REPAIR;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    WebServiceManager webServiceManager = new WebServiceManager(
                            RepairPicActivity.this, methodName, paramValue);
                    String result = webServiceManager.OpenConnect(methodPath);
                    if (null != result && !result.isEmpty()) {
                        LogUtil.e("返回结果result==" + result);
                        Message msg = new Message();
                        try {
                            repairPicBean = RepairPicBean.objectFromData(result);
                            if (repairPicBean.isMsgType()) {
                                if (repairPicBean.getCode() == 1000) {
                                    list = repairPicBean.getData();
                                    if (null != list && !list.isEmpty()) {
                                        msg.what = SUCCESS;
                                        handler.sendMessage(msg);
                                    } else {
                                        msg.obj = "没有设备报障图片";
                                        msg.what = SUCCESS_BUT_NO_DATA;
                                        handler.sendMessage(msg);
                                    }
                                } else {
                                    msg.obj = repairPicBean.getMsg();
                                    msg.what = EXCEPTION;
                                    handler.sendMessage(msg);
                                }
                            } else {
                                msg.obj = repairPicBean.getMsg();
                                msg.what = EXCEPTION;
                                handler.sendMessage(msg);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        Message msg = new Message();
                        msg.what = ERROR;
                        handler.sendMessage(msg);
                    }
                }
            }).start();

        }
    }

    private void initView() {
        title.setText("设备报障图片");
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        list = new ArrayList<>();
        repairPicAdapter = new RepairPicAdapter(this, list);
        devreportList.setAdapter(repairPicAdapter);
        devreportList.setonRefreshListener(new PullRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
    }
}
