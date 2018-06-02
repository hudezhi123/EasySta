package easyway.Mobile.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.TB_Duty_Staff;
import easyway.Mobile.PointTask.TaskChild;
import easyway.Mobile.Data.TaskMajor;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.PullRefreshListView;
import easyway.Mobile.util.PullRefreshListView.OnRefreshListener;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import static android.content.ContentValues.TAG;

/*
 * 锟斤拷锟届工锟斤拷
 */
public class TaskTodoActivity extends ActivityEx {
    private PullRefreshListView tasklist;

    private int position;
    private String staffIds;
    private String staffNames;
    private long twId;
    private String workspace;
    private boolean isPullRefresh = false;
    private Adapter_TakToDo mAdapter;
    boolean isOk;
    private ArrayList<TaskChild> todoList;
    private ArrayList<TB_Duty_Staff> dutystaff;

    private String IsWork;

    private final int MSG_GET_DATA_FAIL = 0;
    private final int MSG_GET_DATA_SUCCEED = 1;
    private final int MSG_GET_DUTY_STAFF_SUCCEED = 6;
    private final int MSG_GET_DUTY_STAFF_FAIL = 7;
    private final int MSG_SET_TASK_ONDUTY_SUCCEED = 8;
    private final int MSG_SET_TASK_ONDUTY_FAIL = 9;

    private int recLen = 61;
    private Timer timer = new Timer();

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
                    timer = new Timer();
                    timer.schedule(new RequestTimerTask(), 1000, 1000);
                    showToast(errMsg);
                    ShowErrorAlert();
                    break;
                case MSG_GET_DATA_SUCCEED:
                    timer = new Timer();
                    timer.schedule(new RequestTimerTask(), 1000, 1000);
                    todoList = (ArrayList<TaskChild>) msg.obj;
                    mAdapter.setData(todoList);
                    mAdapter.notifyDataSetChanged();

                    if (todoList == null || todoList.size() == 0) {
                        ShowNoDataAlert();
                    }

                    break;
                case MSG_GET_DUTY_STAFF_SUCCEED:
                    dutystaff = (ArrayList<TB_Duty_Staff>) msg.obj;
                    SelectStaffOnduty();
                    break;
                case MSG_SET_TASK_ONDUTY_SUCCEED:
                    showToast(R.string.task_onduty_succeed);

                    Intent intent = new Intent(TaskTodoActivity.this,
                            TaskTabActivity.class);
                    startActivity(intent);

                    finish();
                    break;
                case MSG_GET_DUTY_STAFF_FAIL:
                case MSG_SET_TASK_ONDUTY_FAIL:
                    showToast(errMsg);
                    break;

                default:
                    break;
            }
        }
    };

    class RequestTimerTask extends TimerTask {
        public void run() {
            Log.d(TAG, "timer on schedule");
            recLen--;
            if (recLen < 0) {
                timer.cancel();
                finish();
            }

        }
    }

//	TimerTask task = new TimerTask() {
//		@Override
//		public void run() {
//
//			runOnUiThread(new Runnable() {      // UI thread
//				@Override
//				public void run() {
//					recLen--;
////					txtView.setText(""+recLen);
//					if(recLen < 0){
//						timer.cancel();
//						finish();
////						txtView.setVisibility(View.GONE);
//					}
//				}
//			});
//		}
//	};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_todo);
        initView();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            twId = bundle.getLong("taskareaid");
            workspace = bundle.getString("workspace");
        }
//        ShowEnquireWindow();
        getTaskData();
        timer.schedule(new RequestTimerTask(), 1000, 1000);
    }

    public void onPause() {
        super.onPause();
    }


    private void ShowEnquireWindow() {
        AlertDialog.Builder builder = new Builder(TaskTodoActivity.this);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle(R.string.Prompt);
        builder.setMessage(R.string.AllEquipmentIsOk);
        // 关闭页面
        builder.setPositiveButton(R.string.Yse,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        IsWork = "True";
                        getTaskData();
                        timer.cancel();
                        timer.purge();
                        timer = null;
                    }
                });
        builder.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                IsWork = "False";
                getTaskData();
                timer.cancel();
                timer.purge();
                timer = null;
            }
        });

        builder.create().show();
    }


    private void initView() {
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.title_task);

        tasklist = (PullRefreshListView) findViewById(R.id.tasklist);
        mAdapter = new Adapter_TakToDo(this, todoList, TaskAdapter.FLAG_WORKSPACE, timer);
        mAdapter.setITaskReview(new ITaskReview() {
            @Override
            public void ItemClicked(int index) {
                if (todoList == null)
                    return;

                if (todoList.size() <= index)
                    return;

                if (todoList.get(index).TaskSta == TaskMajor.TASK_STATE_CANCEL)
                    return;

                /**
                 * 	ExcSta = 0;未到岗
                 * 	ExcSta = 1;已到岗
                 *  ExcSta = 2;已完成
                 */
                if (todoList.get(index).AExcStat == 1) {
                    ShowTaskOK();
                }
                if (todoList.get(index).AExcStat != 0) {
                    return;
                }
                if (todoList.get(index).IsMajor && !todoList.get(index).IsAccepted) {
                    showToast(R.string.task_noticenotaccept);
                    return;
                }

                position = index;

                if (todoList.get(index).StaffId == Property.StaffId) {
                    staffIds = String.valueOf(Property.StaffId);
                    staffNames = Property.StaffName;
                    TaskOnduty();
                } else {
                    GetOnDutyStaffList();
                }
            }

            @Override
            public void ItemGarbage(int index) {
                System.out.println("Garbage");

            }
        });

        tasklist.setAdapter(mAdapter);
        tasklist.setonRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                isPullRefresh = true;
                getTaskData();
            }
        });

    }

    // 获得任务列表
    private void GetTaskList() {

        HashMap<String, String> parmValues = new HashMap<String, String>();

        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("twId", String.valueOf(twId));
        if (Property.OwnStation != null)
            parmValues.put("stationCode", Property.OwnStation.Code);
        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_GET_TASKBYWORKSPACE;
        Log.e("MyErrorLog", "请求参数" + parmValues.toString());
        WebServiceManager webServiceManager = new WebServiceManager(
                TaskTodoActivity.this, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        if (result == null || result.equals("")) {
            errMsg = getString(R.string.exp_getdata);
            myhandle.sendEmptyMessage(MSG_GET_DATA_FAIL);
            return;
        }
        Log.e("MyErrorLog", "返回结果" + result);
        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                ArrayList<TaskChild> list = TaskChild.ParseFromString(result);
                Log.e("list", "返回结果" + list.toString());
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

    // 获得任务数据
    private void getTaskData() {
        showProgressDialog(R.string.GettingData);
        new Thread() {
            public void run() {
                GetTaskList();
            }
        }.start();
    }

    private void TaskOnduty() {
        showProgressDialog(R.string.SavingData);
        new Thread() {
            public void run() {
                setTaskOnduty();
            }
        }.start();
    }

    // 设置责任任务
    private void setTaskOnduty() {
        if (todoList == null) {
            errMsg = getString(R.string.exp_onduty);
            myhandle.sendEmptyMessage(MSG_SET_TASK_ONDUTY_FAIL);
            return;
        }

        if (todoList.size() <= position) {
            errMsg = getString(R.string.exp_onduty);
            myhandle.sendEmptyMessage(MSG_SET_TASK_ONDUTY_FAIL);
            return;
        }

        if (staffIds == null || staffIds.equals("")) {
            errMsg = getString(R.string.exp_onduty);
            myhandle.sendEmptyMessage(MSG_SET_TASK_ONDUTY_FAIL);
            return;
        }
// TODO 设置到岗任务。
        SetStartTask();
    }

    private void SetStartTask() {
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("saId", String.valueOf(todoList.get(position).SaId));
        parmValues.put("staffIds", staffIds);
        parmValues.put("staffNames", staffNames);
        parmValues.put("twId", String.valueOf(twId));
        parmValues.put("IsWork", IsWork);
        if (Property.OwnStation != null)
            parmValues.put("stationCode", Property.OwnStation.Code);
        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_SET_TASK_STARTING;

        WebServiceManager webServiceManager = new WebServiceManager(
                TaskTodoActivity.this, methodName, parmValues);

        String result = webServiceManager.OpenConnect(methodPath);
        if (result == null || result.equals("")) {
            errMsg = getString(R.string.exp_onduty);
            myhandle.sendEmptyMessage(MSG_SET_TASK_ONDUTY_FAIL);
            return;
        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                myhandle.sendEmptyMessage(MSG_SET_TASK_ONDUTY_SUCCEED);
                break;
            case Constant.EXCEPTION:
            default:
                errMsg = JsonUtil.GetJsonString(result, "Msg");
                myhandle.sendEmptyMessage(MSG_SET_TASK_ONDUTY_FAIL);
                break;
        }
    }

    // 获得职员任务列表
    private void GetOnDutyStaffList() {
        showProgressDialog(R.string.GettingData);
        new Thread() {
            public void run() {
                if (todoList == null || todoList.size() <= position)
                    return;

                GetStaffList();
            }
        }.start();
    }

    // 获得职员列表
    private void GetStaffList() {
        ArrayList<TB_Duty_Staff> tb_Duty_Staffs = new ArrayList<TB_Duty_Staff>();

        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("teamId", Property.DeptId);
        if (Property.OwnStation != null)
            parmValues.put("stationCode", Property.OwnStation.Code);

        String methodPath = Constant.MP_TASK;
        String methodName = Constant.MN_GET_DUTYSTAFF_TEAM;

        WebServiceManager webServiceManager = new WebServiceManager(
                TaskTodoActivity.this, methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        if (result == null || result.equals("")) {
            errMsg = getString(R.string.exp_getdutystaff);
            myhandle.sendEmptyMessage(MSG_GET_DUTY_STAFF_FAIL);
            return;
        }

        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                JSONArray jsonArray = JsonUtil
                        .GetJsonArray(result, "Data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
                    TB_Duty_Staff tb_Duty_Staff = new TB_Duty_Staff();
                    tb_Duty_Staff.staffId = JsonUtil.GetJsonObjIntValue(
                            jsonObj, "StaffId");
                    tb_Duty_Staff.staffName = JsonUtil
                            .GetJsonObjStringValue(jsonObj, "StaffName");
                    tb_Duty_Staffs.add(tb_Duty_Staff);
                }

                Message message = new Message();
                message.what = MSG_GET_DUTY_STAFF_SUCCEED;
                message.obj = tb_Duty_Staffs;
                myhandle.sendMessage(message);
                break;
            case Constant.EXCEPTION:
            default:
                errMsg = JsonUtil.GetJsonString(result, "errMsg");
                myhandle.sendEmptyMessage(MSG_GET_DUTY_STAFF_FAIL);
                break;
        }
    }

    // I Don't Know.
    private void SelectStaffOnduty() {
        String[] m = new String[dutystaff.size()];
        for (int i = 0; i < dutystaff.size(); i++) {
            m[i] = dutystaff.get(i).staffName;
        }

        final boolean[] checkedItems = new boolean[dutystaff.size()];
        for (int i = 0; i < checkedItems.length; i++) {
            if (dutystaff.get(i).staffId == Property.StaffId)
                checkedItems[i] = true;
            else
                checkedItems[i] = false;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(
                TaskTodoActivity.this);

        builder.setTitle(todoList.get(position).TaskName);

        builder.setMultiChoiceItems(m, checkedItems,
                new OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which, boolean isChecked) {
                        checkedItems[which] = isChecked;
                    }
                });
        builder.setPositiveButton(R.string.OK,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        staffIds = "";
                        staffNames = "";
                        for (int i = 0; i < dutystaff.size(); i++) {
                            if (checkedItems[i]) {
                                if (staffIds == "") {
                                    staffIds += String.valueOf(dutystaff
                                            .get(i).staffId);
                                    staffNames += dutystaff.get(i).staffName;
                                } else {
                                    staffIds += ","
                                            + String.valueOf(dutystaff
                                            .get(i).staffId);
                                    staffNames += "," + dutystaff.get(i).staffName;
                                }
                            }
                        }

                        if (staffIds.equals("")) {
                            showToast(R.string.task_staff_select);
                            return;
                        }

                        TaskOnduty();
                    }
                });

        builder.setNegativeButton(R.string.Cancel, null);
        builder.show();
    }

    // 显示已经到岗
    private void ShowTaskOK() {

        AlertDialog.Builder builder = new Builder(TaskTodoActivity.this);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle(R.string.Prompt);
        builder.setCancelable(false);
        builder.setMessage(R.string.task_ok);
        // 关闭页面
        builder.setPositiveButton(R.string.OK,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                });

        builder.create().show();

    }

    // 锟斤拷取锟斤拷锟斤拷失锟斤拷锟斤拷示
    private void ShowErrorAlert() {
        AlertDialog.Builder builder = new Builder(TaskTodoActivity.this);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle(R.string.Prompt);
        builder.setCancelable(true);

        builder.setMessage(R.string.task_get_retry);
        // 锟斤拷锟斤拷
        builder.setPositiveButton(R.string.OK,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        timer.cancel();
                        timer.purge();
                        timer = null;
                        getTaskData();
                    }
                });

        // 取锟斤拷
        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }

    // 无可到岗任务
    private void ShowNoDataAlert() {
        AlertDialog.Builder builder = new Builder(TaskTodoActivity.this);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle(R.string.Prompt);
        builder.setCancelable(false);
        String noTask = getResources().getString(R.string.task_no_data);
        builder.setMessage(workspace + "\n \n" + noTask);
        // 关闭页面
        builder.setPositiveButton(R.string.OK,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                });

        builder.create().show();
    }

}
