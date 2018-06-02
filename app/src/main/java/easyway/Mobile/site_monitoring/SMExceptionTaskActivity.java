package easyway.Mobile.site_monitoring;

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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/*
 * 异常任务
 */
public class SMExceptionTaskActivity extends ActivityEx {
	private PullRefreshListView tasklist;

	private boolean isPullRefresh = false;
	private SMExceptionTaskAdapter mAdapter;
	private ArrayList<TaskChild> todoList;

	private final int MSG_GET_DATA_FAIL = 0;
	private final int MSG_GET_DATA_SUCCEED = 1;

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
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.site_task_exception);

		initView();	
	}

	@Override
	public void onResume() {
		super.onResume();
		getTaskData();
	}

	/* 页面初始事件 */
	private void initView() {
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(getString(R.string.title_exceptiontask));
		
		findViewById(R.id.btnReturn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		tasklist = (PullRefreshListView) findViewById(R.id.tasklist);
		mAdapter = new SMExceptionTaskAdapter(this, todoList);
		tasklist.setAdapter(mAdapter);
		tasklist.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				isPullRefresh = true;
				getTaskData();
			}
		});
	}

	// 获取所有任务信息
	private void GetTaskList() {
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("stationCode", SMTabActivity.mStationCode);

		String methodPath = Constant.MP_TASK;
		String methodName = Constant.MN_GET_TASKEXCEPT;

		WebServiceManager webServiceManager = new WebServiceManager(
				SMExceptionTaskActivity.this, methodName, parmValues);
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
}
