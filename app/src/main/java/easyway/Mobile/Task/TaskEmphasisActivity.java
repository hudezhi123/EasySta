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
import easyway.Mobile.util.PullRefreshListView;
import easyway.Mobile.util.PullRefreshListView.OnRefreshListener;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/*
 * 重点任务
 */
public class TaskEmphasisActivity extends ActivityEx {
	private final String TASK_FLAG_ALL = "AllTask";
	private long taskId;

	private final int FLAG_TASK_ACCEPT = 1;
	private final int FLAG_TASK_COMPLETE = 2;
	private PullRefreshListView tasklist;

	private boolean isPullRefresh = false;
	private TaskAdapter mAdapter;
	private ArrayList<TaskChild> todoList;
	private SearchReceiver receiver;
	
	private final int MSG_GET_DATA_FAIL = 0;
	private final int MSG_GET_DATA_SUCCEED = 1;
	private final int MSG_UPDATE_POINT_TASK_SUCCEED = 2;
	private final int MSG_UPDATE_POINT_TASK_FAIL = 3;
	private final int MSG_RECEIVE_BROADCAST = 4;

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
			case MSG_UPDATE_POINT_TASK_SUCCEED:
				getTaskData();
				break;
			case MSG_UPDATE_POINT_TASK_FAIL:
				showToast(R.string.exp_updatepointtaskstate);
				break;
			case MSG_RECEIVE_BROADCAST:
				getTaskData();
				break;
			default:
				break;
			}
		}
	};

	private String sqlExists = ""; // where条件
	private String taskFlag = ""; // 任务标识
	private int dateFlag = TaskTabActivity.DATEFLAG_TODAY;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_emphasis);

		initView();

		taskFlag = TASK_FLAG_ALL;
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
		mAdapter = new TaskAdapter(this, todoList, TaskAdapter.FLAG_EMPHASIS);
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
				taskId = task.SaId;

				if (task.IsAccepted) {
					if (task.AExcStat == TaskChild.TASK_EXCSTATE_ONDUTY) {
						showCompleteDialog();
					}
				} else {
					showAcceptDialog();
				}
			}

			@Override
			public void  ItemGarbage(int index){
				System.out.println("Garbage");

			}
		});
	}

	// 获取所有任务信息
	private void GetTaskList() {
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("staffId", String.valueOf(Property.StaffId));
		parmValues.put("taskFlag", taskFlag);
		parmValues.put("taskName", sqlExists);
		parmValues.put("dateFlag", String.valueOf(dateFlag));
		parmValues.put("majorTask", String.valueOf(1));
		if (Property.OwnStation != null)
			parmValues.put("stationCode", Property.OwnStation.Code);

		String methodPath = Constant.MP_TASK;
		String methodName = Constant.MN_GET_TASK;

		WebServiceManager webServiceManager = new WebServiceManager(
				TaskEmphasisActivity.this, methodName, parmValues);
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

	// 接收任务确认项
	private void showAcceptDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				TaskEmphasisActivity.this);
		builder.setTitle(R.string.Prompt);
		builder.setMessage(R.string.task_acceptconfirm);
		builder.setPositiveButton(R.string.OK,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						UpdateEmphasisTask(FLAG_TASK_ACCEPT, taskId);
					}
				});
		builder.setNegativeButton(R.string.Cancel, null);
		builder.create().show();
	}

	// 重点任务完成确认窗口
	private void showCompleteDialog() {
		Builder builder = new AlertDialog.Builder(TaskEmphasisActivity.this);
		builder.setTitle(R.string.Notify);
		builder.setMessage(R.string.task_completeconfirm);
		builder.setPositiveButton(R.string.OK,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						UpdateEmphasisTask(FLAG_TASK_COMPLETE, taskId);
					}
				});

		builder.setNegativeButton(R.string.Cancel, null);
		builder.show();
	}

	// 更新重点任务状态
	private void UpdateEmphasisTask(int flag, final long taskID) {
		showProgressDialog(R.string.task_update);
		if (flag == FLAG_TASK_ACCEPT) {
			new Thread() {
				public void run() {
					StartEmphasisTask(taskID); // 接收重点任务
				}
			}.start();
		} else {
			new Thread() {
				public void run() {
					CompleteEmphasisTask(taskID); // 完成重点任务
				}
			}.start();
		}
	}

	// 接收重点任务
	private void StartEmphasisTask(long taskID) {
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("saId", String.valueOf(taskID));
		String methodPath = Constant.MP_TASK;
		String methodName = Constant.MN_ACCEPT_MAJORTASK;
		if (Property.OwnStation != null)
			parmValues.put("stationCode", Property.OwnStation.Code);

		WebServiceManager webServiceManager = new WebServiceManager(
				TaskEmphasisActivity.this, methodName, parmValues);
		String result = webServiceManager.OpenConnect(methodPath);

		if (result == null || result.equals("")) {
			myhandle.sendEmptyMessage(MSG_UPDATE_POINT_TASK_FAIL);
			return;
		}

		int Code = JsonUtil.GetJsonInt(result, "Code");
		switch (Code) {
		case Constant.NORMAL:
			myhandle.sendEmptyMessage(MSG_UPDATE_POINT_TASK_SUCCEED);
			break;
		case Constant.EXCEPTION:
		default:
			myhandle.sendEmptyMessage(MSG_UPDATE_POINT_TASK_FAIL);
			break;
		}
	}

	// 完成重点任务
	private void CompleteEmphasisTask(long taskID) {
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("saId", String.valueOf(taskID));
		String methodPath = Constant.MP_TASK;
		String methodName = Constant.MN_SET_TASK_COMPLETE;
		if (Property.OwnStation != null)
			parmValues.put("stationCode", Property.OwnStation.Code);

		WebServiceManager webServiceManager = new WebServiceManager(
				TaskEmphasisActivity.this, methodName, parmValues);
		String result = webServiceManager.OpenConnect(methodPath);

		if (result == null || result.equals("")) {
			myhandle.sendEmptyMessage(MSG_UPDATE_POINT_TASK_FAIL);
			return;
		}

		int Code = JsonUtil.GetJsonInt(result, "Code");
		switch (Code) {
		case Constant.NORMAL:
			myhandle.sendEmptyMessage(MSG_UPDATE_POINT_TASK_SUCCEED);
			break;
		case Constant.EXCEPTION:
		default:
			myhandle.sendEmptyMessage(MSG_UPDATE_POINT_TASK_FAIL);
			break;
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
