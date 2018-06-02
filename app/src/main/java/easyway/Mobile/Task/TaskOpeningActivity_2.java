package easyway.Mobile.Task;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.PointTask.TaskChild;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.JsonUtil;

/*
 * 当前/所有任务
 */
public class TaskOpeningActivity_2 extends ActivityEx {
    private final String TASK_FLAG_OPEN = "OpeningTask";
    private final String TASK_FLAG_ALL = "AllTask";
    private PullToRefreshExpandableListView pullToRefreshExpandableListView;
    private Button btnSwitch, btnzuyuan;
//    private PullRefreshListView tasklist;

    private boolean isPullRefresh = false;
    //	private TaskAdapter mAdapter;
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
    private final int MSG_SET_TASK_COMPLETE_SUCCEED = 4;
    private final int MSG_SET_TASK_COMPLETE_FAIL = 5;
    private final int MSG_SET_TASK_Garbage = 6;
    private final int MSG_TEST = 3838;

    private int position;
    private ExpandableListView mExpandableListView;
    private AllTaskAdapter mAdapter;

    @SuppressLint("HandlerLeak")
    private Handler myhandle = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            closeProgressDialog();
            switch (msg.what) {
                case 1001:
                    Toast.makeText(TaskOpeningActivity_2.this, "" + msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case MSG_GET_DATA_FAIL:
                    showToast(errMsg);
                    break;
                case MSG_GET_DATA_NO:
                    showToast("没有数据");
//				mAdapter.setDatas(getGroupList(),null);
                    break;
                case MSG_GET_DATA_SUCCEED:
                    todoList = (ArrayList<TaskChild>) msg.obj;
                    List<String> groupList = getGroupList();
                    List<List<TaskChild>> childList = getChildList(todoList, groupList);
                    mAdapter.setDatas(groupList, childList);
                    break;
                case MSG_RECEIVE_BROADCAST:
                    getTaskData();
                    break;
                case MSG_SET_TASK_COMPLETE_SUCCEED:
                    showToast(R.string.task_update_succeed);
                    getTaskData();
                    break;
                case MSG_SET_TASK_COMPLETE_FAIL:
                    showToast(errMsg);
                    break;
                case MSG_SET_TASK_Garbage:
                    showToast(errMsg);
                    getTaskData();
                    break;
                case MSG_TEST:
                    String testStr = (String) msg.obj;
                    Toast.makeText(TaskOpeningActivity_2.this, "" + testStr, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    private List<String> getGroupList() {
        List<String> list = new ArrayList<>();
        list.add("上水");
        list.add("站台");
        list.add("候车厅");
        list.add("检票");
        list.add("出站口");
        list.add("固定任务");
        return list;
    }

    private List<List<TaskChild>> getChildList(List<TaskChild> list, List<String> groupList) {
        List<List<TaskChild>> childList = new ArrayList<>();
        List<TaskChild> shangshui = new ArrayList<>();
        List<TaskChild> zhantai = new ArrayList<>();
        List<TaskChild> houtingche = new ArrayList<>();
        List<TaskChild> jianpiao = new ArrayList<>();
        List<TaskChild> chuzhankou = new ArrayList<>();
        List<TaskChild> fixTask = new ArrayList<>();
        for (TaskChild bean : list) {
            switch (bean.WorkType) {
                case 0:
                    shangshui.add(bean);
                    break;
                case 1:
                    zhantai.add(bean);
                    break;
                case 2:
                    houtingche.add(bean);
                    break;
                case 3:
                    jianpiao.add(bean);
                    break;
                case 4:
                    chuzhankou.add(bean);
                    break;
                case -1:
                    fixTask.add(bean);
            }
        }

        if (shangshui.size() == 0) {
            groupList.remove("上水");
        } else {
            childList.add(shangshui);
        }

        if (zhantai.size() == 0) {
            groupList.remove("站台");
        } else {
            childList.add(zhantai);
        }

        if (houtingche.size() == 0) {
            groupList.remove("候车厅");
        } else {
            childList.add(houtingche);
        }

        if (jianpiao.size() == 0) {
            groupList.remove("检票");
        } else {
            childList.add(jianpiao);
        }

        if (chuzhankou.size() == 0) {
            groupList.remove("出站口");
        } else {
            childList.add(chuzhankou);
        }

        if (fixTask.size() == 0) {
            groupList.remove("固定任务");
        } else {
            childList.add(fixTask);
        }
        return childList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_task_expandable_listview);
        initView();
        taskFlag = TASK_FLAG_OPEN;
        getTaskData();
    }


    /* 页面初始事件 */
    private void initView() {

        pullToRefreshExpandableListView = (PullToRefreshExpandableListView) findViewById(R.id.ptr_expandblelistview_all_task);
        mExpandableListView = pullToRefreshExpandableListView.getRefreshableView();
        mExpandableListView.setGroupIndicator(null);
        mAdapter = new AllTaskAdapter(this);
        mExpandableListView.setAdapter(mAdapter);
//        pullToRefreshExpandableListView.setOnR
//		---------------------------------------

//		tasklist = (PullRefreshListView) findViewById(R.id.tasklist);
//		mAdapter = new TaskAdapter(this, todoList, TaskAdapter.FLAG_OPEN);
//		tasklist.setAdapter(mAdapter);
//		tasklist.setonRefreshListener(new OnRefreshListener() {
//
//			@Override
//			public void onRefresh() {
//				isPullRefresh = true;
//				getTaskData();
//			}
//		});
//
//		mAdapter.setITaskReview(new ITaskReview() {
//			@Override
//			public void ItemClicked(int index) {
//				if (todoList == null)
//					return;
//
//				if (todoList.size() <= index)
//					return;
//
//				TaskChild task = todoList.get(index);
//				if (task.TRNO_PRO == null || task.TRNO_PRO.equals(""))
//					return;
//
//				Intent intent = new Intent(TaskOpeningActivity_2.this,
//						RelatedTaskList.class);
//				intent.putExtra(RelatedTaskList.KEY_TRNO, task.TRNO_PRO);
//				intent.putExtra(RelatedTaskList.KEY_DATE,
//						CommonUtils.date2Stringyyyymmdd(task.PlanDate));
//				startActivity(intent);
//			}
//		});
//
//		btnSwitch = (Button) findViewById(R.id.btnSwitch);
//
//		btnzuyuan = (Button) findViewById(R.id.btnzuyuan);
//		if (!Property.IsTeamLeader.isEmpty()){
//			if (Property.IsTeamLeader.equals("False")) {
//				btnzuyuan.setVisibility(ViewGroup.GONE);
//			} else {
//				btnzuyuan.setVisibility(ViewGroup.VISIBLE);
//			}
//		}
//
//
//		btnzuyuan.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
////				taskFlag = TASK_FLAG_ALL;
////				sqlExists = "";
////				dateFlag = TaskTabActivity.DATEFLAG_TODAY;
////
////				getTaskData();
//				LastSeePosition = tasklist.getLastVisiblePosition();
//				isPullRefresh = true;
//				getTaskData();
//			}
//
//		});
//
//		btnSwitch.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				//这个按钮，把记住的位置归0；
//				LastSeePosition = 0;
//				if (taskFlag.equals(TASK_FLAG_ALL)) {
//					taskFlag = TASK_FLAG_OPEN;
//					sqlExists = "";
//					dateFlag = TaskTabActivity.DATEFLAG_TODAY;
//
//					getTaskData();
//				} else if (taskFlag.equals(TASK_FLAG_OPEN)) {
//					taskFlag = TASK_FLAG_ALL;
//					sqlExists = "";
//					dateFlag = TaskTabActivity.DATEFLAG_TODAY;
//
//					getTaskData();
//				}
//			}
//		});
    }


//    private void TaskGarbage() {
//        showProgressDialog(R.string.SavingGarbage);
//        new Thread() {
//            public void run() {
//                setTaskGrabage();
//            }
//        }.start();
//    }

//    private void setTaskGrabage() {
//        if (todoList == null || todoList.size() <= position) {
//            errMsg = getString(R.string.exp_updatetaskstate);
//            myhandle.sendEmptyMessage(MSG_SET_TASK_COMPLETE_FAIL);
//            return;
//        }
//
//        HashMap<String, String> parmValues = new HashMap<String, String>();
//        parmValues.put("sessionId", Property.SessionId);
//        parmValues.put("saId", String.valueOf(todoList.get(position).SaId));
//
//        String methodPath = Constant.MP_TASK;
//        String methodName = Constant.MN_SET_TASK_Garbage;
//        WebServiceManager webServiceManager = new WebServiceManager(
//                TaskOpeningActivity_2.this, methodName, parmValues);
//
//        String result = webServiceManager.OpenConnect(methodPath);
//        if (result == null || result.equals("")) {
//            errMsg = getString(R.string.exp_updatetaskstate);
//            myhandle.sendEmptyMessage(MSG_SET_TASK_COMPLETE_FAIL);
//            return;
//        }
//        int Code = JsonUtil.GetJsonInt(result, "Code");
//        switch (Code) {
//            case Constant.NORMAL:
//                errMsg = JsonUtil.GetJsonString(result, "Msg");
//                myhandle.sendEmptyMessage(MSG_SET_TASK_Garbage);
//                break;
//            case Constant.EXCEPTION:
//            default:
//                errMsg = JsonUtil.GetJsonString(result, "Msg");
//                myhandle.sendEmptyMessage(MSG_SET_TASK_COMPLETE_FAIL);
//                break;
//        }
//
//    }

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
                TaskOpeningActivity_2.this, methodName, parmValues);
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
        showProgressDialog(R.string.GettingData, true);
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
}
