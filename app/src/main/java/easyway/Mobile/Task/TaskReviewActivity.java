package easyway.Mobile.Task;

import java.util.ArrayList;
import java.util.HashMap;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.PointTask.TaskChild;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.LogUtil;
import easyway.Mobile.util.PullRefreshListView;
import easyway.Mobile.util.PullRefreshListView.OnRefreshListener;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/*
 * 到岗任务
 */
public class TaskReviewActivity extends ActivityEx {
    private PullRefreshListView tasklist;

    private int position;
    private boolean isPullRefresh = false;
    private TaskAdapter mAdapter;
    private SharedPreferences sp;
    private ArrayList<TaskChild> todoList;
    private final int MSG_GET_DATA_FAIL = 0;
    private final int MSG_GET_DATA_SUCCEED = 1;
    private final int MSG_SET_TASK_COMPLETE_SUCCEED = 4;
    private final int MSG_SET_TASK_COMPLETE_FAIL = 5;
    private final int MSG_SET_TASK_Garbage = 6;


    @SuppressLint("HandlerLeak")
    private Handler myhandle = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            closeProgressDialog();
            switch (msg.what) {
                case MSG_GET_DATA_FAIL:
                    if (isPullRefresh) {
                        isPullRefresh = false;
                        tasklist.onRefreshComplete();
                    }

                    showToast(errMsg);
                    break;
                case MSG_GET_DATA_SUCCEED:
                    if (isPullRefresh) {
                        isPullRefresh = false;
                        tasklist.onRefreshComplete();
                    }
                    todoList = (ArrayList<TaskChild>) msg.obj;
                    mAdapter.setData(todoList);
                    mAdapter.notifyDataSetChanged();
                    break;
                case MSG_SET_TASK_COMPLETE_SUCCEED:
                    todoList.remove(position); // 状态更新为已完成成功，将数据从到岗列表中删除
                    mAdapter.setData(todoList);
                    mAdapter.notifyDataSetChanged();
                    sp.edit().putBoolean("到岗状态", false).commit();

                    showToast(R.string.task_update_succeed);

                    getTaskData();
                    break;
                case MSG_SET_TASK_COMPLETE_FAIL:
                    showToast(errMsg);
                    break;
                case MSG_SET_TASK_Garbage:
                    showToast(errMsg);
                    mAdapter.setData(todoList);
                    mAdapter.notifyDataSetChanged();

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
        setContentView(R.layout.task_review);
        sp = getSharedPreferences("是否到岗", Context.MODE_PRIVATE);
        initView();

        getTaskData();
    }

    public void onPause() {
        super.onPause();
    }

    private void initView() {
        tasklist = (PullRefreshListView) findViewById(R.id.tasklist);
        mAdapter = new TaskAdapter(this, todoList, TaskAdapter.FLAG_REVIEW);
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
                LogUtil.i("index -->" + index);
                LogUtil.i("ExcSta -->" + todoList.get(index).AExcStat);
                if (todoList == null)
                    return;

                if (todoList.size() <= index)
                    return;

                if (todoList.get(index).AExcStat != TaskChild.TASK_EXCSTATE_ONDUTY)
                    return;

                // 已到岗
                position = index;

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        TaskReviewActivity.this);
                builder.setTitle(R.string.Prompt);
                builder.setMessage(R.string.task_completeconfirm);
                builder.setPositiveButton(R.string.OK,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                TaskComplete();
                            }
                        });
                builder.setNegativeButton(R.string.Cancel, null);
                builder.create().show();
            }

            @Override
            public void ItemGarbage(int index) {
                LogUtil.i("index -->" + index);
                LogUtil.i("ExcSta -->" + todoList.get(index).AExcStat);
                if (todoList == null)
                    return;

                if (todoList.size() <= index)
                    return;

                if (todoList.get(index).AExcStat <= TaskChild.TASK_EXCSTATE_UNDUTY)
                    return;

                // 已到岗
                position = index;

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        TaskReviewActivity.this);
                builder.setTitle(R.string.Prompt);
                builder.setMessage(R.string.task_garbagefirm);
                builder.setPositiveButton(R.string.OK,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                TaskGarbage();
                            }
                        });
                builder.setNegativeButton(R.string.Cancel, null);
                builder.create().show();

            }
        });
    }

    // 获取正在执行的任务（到岗）
    private void GetTaskList() {
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("deptId", Property.DeptId);
        if (Property.OwnStation != null)
            parmValues.put("stationCode", Property.OwnStation.Code);

        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_GET_RUNNING_TASK;

        WebServiceManager webServiceManager = new WebServiceManager(
                TaskReviewActivity.this, methodName, parmValues);
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

                Message message = new Message();
                message.what = MSG_GET_DATA_SUCCEED;
                message.obj = list;
                myhandle.sendMessage(message);
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
            }
        }.start();
    }

    // 完成任务
    private void TaskComplete() {
        showProgressDialog(R.string.SavingData);
        new Thread() {
            public void run() {
                setTaskComplete();
            }
        }.start();
    }

    private void TaskGarbage() {
        showProgressDialog(R.string.SavingGarbage);
        new Thread() {
            public void run() {
                setTaskGrabage();
            }
        }.start();
    }

    private void setTaskGrabage() {
        if (todoList == null || todoList.size() <= position) {
            errMsg = getString(R.string.exp_updatetaskstate);
            myhandle.sendEmptyMessage(MSG_SET_TASK_COMPLETE_FAIL);
            return;
        }

        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("saId", String.valueOf(todoList.get(position).SaId));

        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_SET_TASK_Garbage;
        WebServiceManager webServiceManager = new WebServiceManager(
                TaskReviewActivity.this, methodName, parmValues);

        String result = webServiceManager.OpenConnect(methodPath);
        if (result == null || result.equals("")) {
            errMsg = getString(R.string.exp_updatetaskstate);
            myhandle.sendEmptyMessage(MSG_SET_TASK_COMPLETE_FAIL);
            return;
        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                errMsg = JsonUtil.GetJsonString(result, "Msg");
                myhandle.sendEmptyMessage(MSG_SET_TASK_Garbage);
                break;
            case Constant.EXCEPTION:
            default:
                errMsg = JsonUtil.GetJsonString(result, "Msg");
                myhandle.sendEmptyMessage(MSG_SET_TASK_COMPLETE_FAIL);
                break;
        }

    }


    // 完成任务
    private void setTaskComplete() {
        if (todoList == null || todoList.size() <= position) {
            errMsg = getString(R.string.exp_updatetaskstate);
            myhandle.sendEmptyMessage(MSG_SET_TASK_COMPLETE_FAIL);
            return;
        }

        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("saId", String.valueOf(todoList.get(position).SaId));

        if (Property.OwnStation != null)
            parmValues.put("stationCode", Property.OwnStation.Code);
        parmValues.put("twId", String.valueOf(todoList.get(position).TwId));

        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_SET_TASK_COMPLETE;
        WebServiceManager webServiceManager = new WebServiceManager(
                TaskReviewActivity.this, methodName, parmValues);

        String result = webServiceManager.OpenConnect(methodPath);
        if (result == null || result.equals("")) {
            errMsg = getString(R.string.exp_updatetaskstate);
            myhandle.sendEmptyMessage(MSG_SET_TASK_COMPLETE_FAIL);
            return;
        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                myhandle.sendEmptyMessage(MSG_SET_TASK_COMPLETE_SUCCEED);
                break;
            case Constant.EXCEPTION:
            default:
                errMsg = JsonUtil.GetJsonString(result, "Msg");
                myhandle.sendEmptyMessage(MSG_SET_TASK_COMPLETE_FAIL);
                break;
        }
    }
}
