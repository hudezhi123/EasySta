package easyway.Mobile.PointTask;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.Staff;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.PTTUtil;
import easyway.Mobile.util.PullRefreshListView;
import easyway.Mobile.util.PullRefreshListView.OnRefreshListener;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/*
 * 重点任务子任务列表
 */
public class PTChildListActivity extends ActivityEx {
	private PullRefreshListView mListView;
	private PTChildAdapter mAdapter;
	private ArrayList<TaskChild> mList;
	private boolean isPullRefresh = false;
	private String ManagerStaffId;
	private String ManagerName;

	private long TaskId = -1;

	private final int MSG_GETTASK_FAIL = 0;
	private final int MSG_GETTASK_SUCCEED = 1;
	private final int MSG_TASK_TEAM = 2;
	private final int MSG_GETSTAFF_SUCCEED = 4;
	private final int MSG_GETSTAFF_FAIL = 5;
	private final int MSG_GETSTAFF_NOBODY = 6;

	@SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			closeProgressDialog();
			switch (msg.what) {
			case MSG_GETTASK_FAIL:
				if (isPullRefresh) {
					isPullRefresh = false;
					mListView.onRefreshComplete();
				}
				showToast(errMsg);
				break;
			case MSG_GETTASK_SUCCEED:
				if (isPullRefresh) {
					isPullRefresh = false;
					mListView.onRefreshComplete();
				}

				mList = (ArrayList<TaskChild>) msg.obj;
				mAdapter.setData(mList);
				mAdapter.notifyDataSetChanged();
				break;
			case MSG_TASK_TEAM:
				final long teamId = (Long) msg.obj;
				showProgressDialog(R.string.GettingData);

				new Thread() {
					public void run() {
						getTeamMgr(teamId);
					}
				}.start();
				break;
			case MSG_GETSTAFF_SUCCEED:
				PTTUtil.call(PTChildListActivity.this, Staff
						.GetExpend1ByStaffId(PTChildListActivity.this,
								Long.parseLong(ManagerStaffId)),
						PTTUtil.AUDIO_CALL);
				showToast(ManagerName);
				break;
			case MSG_GETSTAFF_FAIL:
				showToast(R.string.exp_getteamheader);
				break;
			case MSG_GETSTAFF_NOBODY:
				AlertDialog.Builder builder = new AlertDialog.Builder(
						PTChildListActivity.this);
				builder.setTitle(R.string.Prompt);
				builder.setMessage(R.string.nobodyonline);
				builder.setPositiveButton(R.string.OK, null);
				builder.create().show();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pointtask_childlist);

		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.task_title_childlist);

		mListView = (PullRefreshListView) findViewById(R.id.tasklist);
		mAdapter = new PTChildAdapter(this, mList);
		mListView.setAdapter(mAdapter);
		IOnItemClick iClick = new IOnItemClick() {

			@Override
			public void onClick(int index) {
				if (mList == null)
					return;
				if (index < 0 || (index + 1) > mList.size())
					return;

				if (mList.get(index).AExcStat == TaskChild.TASK_EXCSTATE_COMPLETE
						|| mList.get(index).AExcStat == TaskChild.TASK_EXCSTATE_ONDUTY) { // 已到岗
					if (mList.get(index).RStaffIds == null
							|| mList.get(index).RStaffIds == "") { // 实际执行人为空
						PTTUtil.call(PTChildListActivity.this, Staff
								.GetExpend1ByStaffId(PTChildListActivity.this,
										mList.get(index).StaffId),
								PTTUtil.AUDIO_CALL);
						showToast(mList.get(index).StaffName);
					} else {
						String[] ids = mList.get(index).RStaffIds.split(",");
						String[] names = mList.get(index).RStaffNames
								.split(",");
						if (ids == null || ids.length == 0)
							return;
						else
							PTTUtil.call(PTChildListActivity.this, Staff
									.GetExpend1ByStaffId(
											PTChildListActivity.this,
											Long.parseLong(ids[0])),
									PTTUtil.AUDIO_CALL);

						if (names == null || names.length == 0)
							return;
						else
							showToast(names[0]);
					}

				} else { // 未到岗
					if (mList.get(index).StaffId == -1) { // 执行组
						Message msg = new Message();
						msg.what = MSG_TASK_TEAM;
						msg.obj = mList.get(index).DeptId;
						myhandle.sendMessage(msg);
					} else { // 执行人
						PTTUtil.call(PTChildListActivity.this, Staff
								.GetExpend1ByStaffId(PTChildListActivity.this,
										mList.get(index).StaffId),
								PTTUtil.AUDIO_CALL);
						showToast(mList.get(index).StaffName);
					}
				}
			}
		};
		mAdapter.setIOnItemClick(iClick);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			TaskId = bundle.getLong(PTListActivity.KEY_TASKID);
		}

		Button btnReturn = (Button) findViewById(R.id.btnReturn);
		if (btnReturn != null) {
			btnReturn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					finish();
				}
			});
		}

		mListView.setonRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				isPullRefresh = true;
				getPointTask();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		getPointTask();
	}

	// 获取重点任务信息
	private void getPointTask() {
		showProgressDialog(R.string.GettingData);

		new Thread() {
			public void run() {
				GetPublisherEmphasisTask();
			}
		}.start();
	}

	// 获取重点任务信息
	private void GetPublisherEmphasisTask() {
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("taskId", String.valueOf(TaskId));
		if (Property.OwnStation != null)
			parmValues.put("stationCode", Property.OwnStation.Code);
		String methodPath = Constant.MP_TASK;
		String methodName = Constant.MN_GET_TASKACTOR;

		WebServiceManager webServiceManager = new WebServiceManager(this,
				methodName, parmValues);
		String result = webServiceManager.OpenConnect(methodPath);

		if (result == null || result.equals("")) {
			errMsg = getString(R.string.exp_getdata);
			myhandle.sendEmptyMessage(MSG_GETTASK_FAIL);
			return;
		}

		int Code = JsonUtil.GetJsonInt(result, "Code");
		switch (Code) {
		case Constant.NORMAL:
			ArrayList<TaskChild> list = TaskChild.ParseFromString(result);

			Message message = new Message();
			message.what = MSG_GETTASK_SUCCEED;
			message.obj = list;
			myhandle.sendMessage(message);
			break;
		case Constant.EXCEPTION:
		default:
			errMsg = JsonUtil.GetJsonString(result, "Msg");
			myhandle.sendEmptyMessage(MSG_GETTASK_FAIL);
			break;
		}
	}

	// 获取执行组在线负责人信息
	private void getTeamMgr(long teamId) {
		HashMap<String, String> paramValues = new HashMap<String, String>();
		paramValues.put("sessionId", Property.SessionId);
		paramValues.put("teamId", String.valueOf(teamId));
		if (Property.OwnStation != null)
			paramValues.put("stationCode", Property.OwnStation.Code);

		String methodPath = Constant.MP_TASK;
		String methodName = Constant.MN_GET_TEAMHEAD_INFO;

		WebServiceManager webServiceManager = new WebServiceManager(
				getApplicationContext(), methodName, paramValues);

		String result = webServiceManager.OpenConnect(methodPath);

		if (result == null || result.equals("")) {
			myhandle.sendEmptyMessage(MSG_GETSTAFF_FAIL);
			return;
		}

		int Code = JsonUtil.GetJsonInt(result, "Code");
		switch (Code) {
		case Constant.NORMAL:
			JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
			if (jsonArray == null || jsonArray.length() == 0) {
				myhandle.sendEmptyMessage(MSG_GETSTAFF_NOBODY);
			} else {
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
					ManagerStaffId = JsonUtil.GetJsonObjStringValue(jsonObj,
							"ManagerStaffId");
					ManagerName = JsonUtil.GetJsonObjStringValue(jsonObj,
							"Manager");
				}

				myhandle.sendEmptyMessage(MSG_GETSTAFF_SUCCEED);
			}

			break;
		case Constant.EXCEPTION:
		default:
			myhandle.sendEmptyMessage(MSG_GETSTAFF_FAIL);
			break;
		}
	}
}
