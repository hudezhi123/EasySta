package easyway.Mobile.Task;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;
import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.PointTask.TaskChild;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;

/*
 * 相关任务
 */
public class RelatedTaskList extends ActivityEx {
	public static final String KEY_TRNO = "TRNO";
	public static final String KEY_DATE = "DATE";
	private String trainNo = "";
	private String taskDate = "";

	private final int MSG_GETDATA_FAIL = 0;
	private final int MSG_GETDATA_SUCCEED = 1;
	@SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			closeProgressDialog();
			switch (msg.what) {
			case MSG_GETDATA_FAIL:
				showToast(errMsg);
				break;
			case MSG_GETDATA_SUCCEED:
				ListView tasklist = (ListView) findViewById(R.id.tasklist);
				TaskAdapter adapter = new TaskAdapter(RelatedTaskList.this,
						(ArrayList<TaskChild>) msg.obj, TaskAdapter.FLAG_ALL);
				tasklist.setAdapter(adapter);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_related);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			trainNo = bundle.getString(KEY_TRNO);
			taskDate = bundle.getString(KEY_DATE);
		}

		if (trainNo == null || trainNo.equals(""))
			finish();

		getTaskData();
	}

	// 获取数据
	private void getTaskData() {
		showProgressDialog(R.string.GettingData);
		new Thread() {
			public void run() {
				GetRelativeTaskList();
			}
		}.start();
	}

	// 获取关联任务信息
	private void GetRelativeTaskList() {
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("TrainNum", trainNo);
		parmValues.put("taskDate", taskDate);
		if (Property.OwnStation != null)
			parmValues.put("stationCode", Property.OwnStation.Code);

		String methodPath = Constant.MP_TASK;
		String methodName = Constant.MN_GET_RELATED_TASK;
		WebServiceManager webServiceManager = new WebServiceManager(
				RelatedTaskList.this, methodName, parmValues);
		String result = webServiceManager.OpenConnect(methodPath);
		if (result == null || result.equals("")) {
			errMsg = getString(R.string.exp_getdata);
			myhandle.sendEmptyMessage(MSG_GETDATA_FAIL);
			return;
		}

		int Code = JsonUtil.GetJsonInt(result, "Code");
		switch (Code) {
		case Constant.NORMAL:
			ArrayList<TaskChild> list = TaskChild.ParseFromString(result);

			Message message = new Message();
			message.what = MSG_GETDATA_SUCCEED;
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
