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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class TaskCompleteActivity extends ActivityEx {

	private PullRefreshListView tasklist;

	private boolean isPullRefresh = false;
	private TaskAdapter mAdapter;
	private ArrayList<TaskChild> todoList;
	private int position;
	
	private String sqlExists = ""; // where条件
	private String taskFlag = ""; // 任务标识
	
	private SearchReceiver receiver;

	private int dateFlag = TaskTabActivity.DATEFLAG_TODAY;

	private final int MSG_GET_DATA_FAIL = 0;
	private final int MSG_GET_DATA_SUCCEED = 1;
	private final int MSG_RECEIVE_BROADCAST = 2;
	private final int MSG_SET_TASK_COMPLETE_FAIL = 5;
	private final int MSG_SET_TASK_Garbage = 6;

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
			case MSG_GET_DATA_SUCCEED:
				todoList = (ArrayList<TaskChild>) msg.obj;
				mAdapter.setData(todoList);
				mAdapter.notifyDataSetChanged();

				break;
			case MSG_RECEIVE_BROADCAST:
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
		setContentView(R.layout.task_complete);

		initView();

		getTaskData();
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

			}

			@Override
			public void  ItemGarbage(int index){
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
						TaskCompleteActivity.this);
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

	private void TaskGarbage()
	{
		showProgressDialog(R.string.SavingGarbage);
		new Thread() {
			public void run() {
				setTaskGrabage();
			}
		}.start();
	}

	private void setTaskGrabage()
	{
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
				TaskCompleteActivity.this, methodName, parmValues);

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

	// 获取所有任务信息
	private void GetTaskList() {
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("taskFlag", "CompletedTask");
		parmValues.put("taskName", sqlExists);
		parmValues.put("dateFlag", String.valueOf(dateFlag));
		parmValues.put("majorTask", String.valueOf(0));
		if (Property.OwnStation != null)
			parmValues.put("stationCode", Property.OwnStation.Code);

		String methodPath = Constant.MP_TASK;
		String methodName = Constant.MN_GET_TASK;

		WebServiceManager webServiceManager = new WebServiceManager(
				TaskCompleteActivity.this, methodName, parmValues);
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
				sqlExists = "";
				dateFlag = TaskTabActivity.DATEFLAG_TODAY;
			}
		}.start();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		regReceiver();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (receiver != null) {
			try {
				unregisterReceiver(receiver);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
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
