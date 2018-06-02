package easyway.Mobile.Task;

import java.util.ArrayList;
import java.util.HashMap;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.PointTask.TaskChild;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.DateUtil;
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
import android.view.ViewGroup;
import android.widget.Button;

/*
 * 当前/所有任务
 */
public class TaskOpeningActivity extends ActivityEx {
    private final String TASK_FLAG_OPEN = "OpeningTask";
    private final String TASK_FLAG_ALL = "AllTask";

    private Button btnSwitch, btnzuyuan;
    private PullRefreshListView tasklist;

    private boolean isPullRefresh = false;
    private TaskAdapter mAdapter;
    private ArrayList<TaskChild> todoList;
    private SearchReceiver receiver;

    private String sqlExists = ""; // where条件
    private String taskFlag = ""; // 任务标识
    private int dateFlag = TaskTabActivity.DATEFLAG_TODAY;

    private int LastSeePosition;

    private final int MSG_GET_DATA_FAIL = 0;
    private final int MSG_GET_DATA_SUCCEED = 1;
    private final int MSG_RECEIVE_BROADCAST = 2;
    private final int MSG_GET_DATA_NO = 3;

    @SuppressLint("HandlerLeak")
    private Handler myhandle = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            closeProgressDialog();

            if (isPullRefresh) {
                isPullRefresh = false;
                tasklist.onRefreshComplete();
            }

            switch (msg.what) {
                case MSG_GET_DATA_FAIL:
                    showToast(errMsg);
                    break;

                case MSG_GET_DATA_NO:
                    showToast("没有数据");
                    break;
                case MSG_GET_DATA_SUCCEED:
                    todoList = (ArrayList<TaskChild>) msg.obj;
                    mAdapter.setData(todoList);
                    mAdapter.notifyDataSetChanged();
                    if (LastSeePosition != 0) {
                        tasklist.setSelection(LastSeePosition);
                    }

                    if (taskFlag.equals(TASK_FLAG_ALL)) {
                        btnSwitch.setText(R.string.task_show_open);
                    } else if (taskFlag.equals(TASK_FLAG_OPEN)) {
                        btnSwitch.setText(R.string.task_show_all);
                    }
                    break;
                case MSG_RECEIVE_BROADCAST:
                    getTaskData();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_opening);

        initView();

        taskFlag = TASK_FLAG_OPEN;
        getTaskData();
    }

    @Override
    public void onResume() {
        super.onResume();
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


    /* 页面初始事件 */
    private void initView() {

        tasklist = (PullRefreshListView) findViewById(R.id.tasklist);
        mAdapter = new TaskAdapter(this, todoList, TaskAdapter.FLAG_OPEN);
        tasklist.setAdapter(mAdapter);
        tasklist.setonRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                isPullRefresh = true;
                getTaskData();
            }
        });

        mAdapter.setITaskReview(new ITaskReview() {
            @Override
            public void ItemClicked(int index) {
                if (todoList == null)
                    return;

                if (todoList.size() <= index)
                    return;

                TaskChild task = todoList.get(index);
                if (task.TRNO_PRO == null || task.TRNO_PRO.equals(""))
                    return;

                Intent intent = new Intent(TaskOpeningActivity.this,
                        RelatedTaskList.class);
                intent.putExtra(RelatedTaskList.KEY_TRNO, task.TRNO_PRO);
                intent.putExtra(RelatedTaskList.KEY_DATE,
                        DateUtil.formatDate(task.PlanDate, DateUtil.YYYY_MM_DD));
                startActivity(intent);
            }

            @Override
            public void ItemGarbage(int index) {
                System.out.println("Garbage");

            }
        });

        btnSwitch = (Button) findViewById(R.id.btnSwitch);

        btnzuyuan = (Button) findViewById(R.id.btnzuyuan);
        if (!Property.IsTeamLeader.isEmpty()) {
            if (Property.IsTeamLeader.equals("False")) {
                btnzuyuan.setVisibility(ViewGroup.GONE);
            } else {
                btnzuyuan.setVisibility(ViewGroup.VISIBLE);
            }
        }


        btnzuyuan.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//				taskFlag = TASK_FLAG_ALL;
//				sqlExists = "";
//				dateFlag = TaskTabActivity.DATEFLAG_TODAY;
//
//				getTaskData();
                LastSeePosition = tasklist.getLastVisiblePosition();
                isPullRefresh = true;
                getTaskData();
            }

        });

        btnSwitch.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //这个按钮，把记住的位置归0；
                LastSeePosition = 0;
                if (taskFlag.equals(TASK_FLAG_ALL)) {
                    taskFlag = TASK_FLAG_OPEN;
                    sqlExists = "";
                    dateFlag = TaskTabActivity.DATEFLAG_TODAY;

                    getTaskData();
                } else if (taskFlag.equals(TASK_FLAG_OPEN)) {
                    taskFlag = TASK_FLAG_ALL;
                    sqlExists = "";
                    dateFlag = TaskTabActivity.DATEFLAG_TODAY;

                    getTaskData();
                }
            }
        });
    }

    // 获取所有任务信息
    private void GetTaskList() {
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("taskFlag", taskFlag);
        parmValues.put("taskName", sqlExists);
        parmValues.put("dateFlag", String.valueOf(dateFlag));
        parmValues.put("majorTask", String.valueOf(0));
        if (Property.OwnStation != null)
            parmValues.put("stationCode", Property.OwnStation.Code);

        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_GET_TASK;

        WebServiceManager webServiceManager = new WebServiceManager(
                TaskOpeningActivity.this, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        if (result == null || result.equals("")) {
            errMsg = getString(R.string.exp_getdata);
            myhandle.sendEmptyMessage(MSG_GET_DATA_FAIL);
            return;
        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:

                ArrayList<TaskChild> list = TaskChild.ParseFromString(result);

                if (list.size() > 0) {
                    Message message = new Message();
                    message.what = MSG_GET_DATA_SUCCEED;
                    message.obj = list;
                    myhandle.sendMessage(message);
                } else {
                    Message message = new Message();
                    message.what = MSG_GET_DATA_NO;

                    myhandle.sendMessage(message);
                }

                break;
            case Constant.EXCEPTION:
            default:
                errMsg = JsonUtil.GetJsonString(result, "Msg");
                myhandle.sendEmptyMessage(MSG_GET_DATA_FAIL);
                break;
        }
    }

    // 获取数据
    private void getTaskData() {
        showProgressDialog(R.string.GettingData);
        new Thread() {
            public void run() {
                GetTaskList();
                sqlExists = "";
                dateFlag = TaskTabActivity.DATEFLAG_TODAY;
            }
        }.start();
    }

    private void regReceiver() {
        receiver = new SearchReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(TaskTabActivity.ACTION_TASK_SEARCH);
        registerReceiver(receiver, filter);
    }


    // 自定义一个广播接收器
    public class SearchReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            sqlExists = bundle.getString(TaskTabActivity.EXTRA_TRAINNO);
            dateFlag = bundle.getInt(TaskTabActivity.EXTRA_DATEFLAG);

            myhandle.sendEmptyMessage(MSG_RECEIVE_BROADCAST);
        }
    }
}
