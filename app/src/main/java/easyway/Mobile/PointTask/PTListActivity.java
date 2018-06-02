package easyway.Mobile.PointTask;

import java.util.ArrayList;
import java.util.HashMap;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.TaskMajor;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.DateLine;
import easyway.Mobile.util.DateUtil;
import easyway.Mobile.util.IDateLineListener;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.LogUtil;
import easyway.Mobile.util.PullRefreshListView;
import easyway.Mobile.util.PullRefreshListView.OnRefreshListener;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.TextView;

/*
 * 重点任务列表
 */
public class PTListActivity extends ActivityEx implements OnClickListener {
	public static final String KEY_TASKID = "TaskId";
	private PullRefreshListView mListView;
	private PTAdapter mAdapter;
	private DateLine dateline;
	
	private ArrayList<TaskMajor> mList;
	private boolean isPullRefresh = false;
	private Long mTaskId = MajorMode.TASKID_INVALID;		// 主任务ID
	
	private final int MSG_GETTASK_FAIL = 0;			// 获取任务失败
	private final int MSG_GETTASK_SUCCEED = 1;	// 获取任务成功
	// private final int MSG_UPDATETASKSTATE_SUCCEED = 2;
	// private final int MSG_UPDATETASKSTATE_FAIL = 3;
	private final int MSG_PUBLISH_TASK_SUCC = 4;		// 发布任务成功
	private final int MSG_PUBLISH_TASK_FAIL = 5;		// 发布任务失败
	private final int MSG_CANCEL_TASK_SUCC = 6;		// 取消任务成功
	private final int MSG_CANCEL_TASK_FAIL = 7;			// 取消任务失败
	private final int MSG_DATE_CHANGE = 8;			// 日期改变

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

				mList = (ArrayList<TaskMajor>) msg.obj;
				mAdapter.setData(mList);
				mAdapter.notifyDataSetChanged();
				break;
			// case MSG_UPDATETASKSTATE_SUCCEED:
			// getPointTask();
			// break;
			// case MSG_UPDATETASKSTATE_FAIL:
			// showToast(R.string.exp_updatepointtaskstate);
			// break;
			case MSG_PUBLISH_TASK_SUCC:
				showToast(R.string.task_publish_succeed);
				getPointTask();
				break;
			case MSG_PUBLISH_TASK_FAIL:
				ShowContinue2Publish();
				break;
			case MSG_CANCEL_TASK_SUCC:
				showToast(R.string.task_cancel_succeed);
				getPointTask();
				break;
			case MSG_CANCEL_TASK_FAIL:
				showToast(R.string.task_cancel_fail);
				ShowContinue2Cancel();
				break;
			case MSG_DATE_CHANGE:
				getPointTask();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pointtask_list);

		initView();
	}

	private void initView() {
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.title_pointtask);

		Button addBtn = (Button) findViewById(R.id.btnset);
		addBtn.setVisibility(View.VISIBLE);
		addBtn.setText(R.string.task_add);
		addBtn.setOnClickListener(this);

		Button btnReturn = (Button) findViewById(R.id.btnReturn);
		if (btnReturn != null) {
			btnReturn.setOnClickListener(this);
		}
		
		dateline = (DateLine) findViewById(R.id.dateline);
		dateline.setListener(new IDateLineListener() {
			@Override
			public void DateChange() {
				myhandle.sendEmptyMessage(MSG_DATE_CHANGE);
			}
		});
		
		mListView = (PullRefreshListView) findViewById(R.id.point_task_list);
		mAdapter = new PTAdapter(this, mList);
		mListView.setAdapter(mAdapter);

		mListView.setonRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				isPullRefresh = true;
				getPointTask();
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int index = arg2 - 1;	// listview  有header ，index 需 -1

				if (mList == null)
					return;

				if (index < 0 || index > (mList.size() - 1))
					return;

				if (mList.get(index).IsDraft) {		// 草稿任务
					Intent intent = new Intent(PTListActivity.this,
							PTDraftChildListActivity.class);

					MajorMode major = new MajorMode();
					major.TaskId = mList.get(index).TaskId;
					major.TaskName = mList.get(index).TaskName;
					major.TaskLevel = mList.get(index).TaskLevel;
					major.TRNO_PRO = mList.get(index).TRNO_PRO;
					major.StaffId = mList.get(index).ChargeStaffId;
					major.StaffName = mList.get(index).ChargeStaffName;
					major.PlanDate = DateUtil.formatDate(mList
							.get(index).PlanDate,DateUtil.YYYY_MM_DD);

					Bundle extras = new Bundle();
					extras.putSerializable(PTAddActivity.KEY_MAJOR, major);
					intent.putExtras(extras);
					startActivity(intent);
				} else {			// 已发布任务
					Intent intent = new Intent(PTListActivity.this,
							PTChildListActivity.class);
					intent.putExtra(KEY_TASKID, mList.get(index).TaskId);
					startActivity(intent);
				}
			}
		});

		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				LogUtil.i("onItemLongClick  Index --> " + arg2);
				int index = arg2 - 1;

				if (mList == null)
					return false;

				if (index < 0 || index > (mList.size() - 1))
					return false;

				showSelectDialog(index);
				return false;
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		getPointTask();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnset:
			Intent intent = new Intent(PTListActivity.this, PTAddActivity.class);
			startActivity(intent);
			break;
		case R.id.btnReturn:
			finish();
			break;
		default:
			break;
		}
	}
	
	private void showSelectDialog(final int index) {
		if (mList.get(index).IsDraft) {			// 任务草稿
			String[] strs = new String[] { getString(R.string.task_delete),
					getString(R.string.task_edit),
					getString(R.string.task_add_publish) };

			AlertDialog dlg = new AlertDialog.Builder(PTListActivity.this)
					.setTitle("")
					.setItems(strs, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							if (item == 2) { // 发布
								mTaskId = mList.get(index).TaskId;
								TaskPublish();
							} else if (item == 1) { // 编辑
								Intent intent = new Intent(PTListActivity.this,
										PTAddActivity.class);
								MajorMode major = new MajorMode();
								major.TaskId = mList.get(index).TaskId;
								major.TaskName = mList.get(index).TaskName;
								major.TaskLevel = mList.get(index).TaskLevel;
								major.TRNO_PRO = mList.get(index).TRNO_PRO;
								major.StaffId = mList.get(index).ChargeStaffId;
								major.StaffName = mList.get(index).ChargeStaffName;
								major.PlanDate = DateUtil.formatDate(mList.get(index).PlanDate,DateUtil.YYYY_MM_DD);

								Bundle extras = new Bundle();
								extras.putSerializable(PTAddActivity.KEY_MAJOR,
										major);
								extras.putBoolean(PTAddActivity.KEY_EDIT, true);
								intent.putExtras(extras);
								startActivity(intent);
							} else { // 取消
								mTaskId = mList.get(index).TaskId;
								TaskCancel();
							}
						}
					}).create();
			dlg.show();
		} else {		// 已发布任务
			if (mList.get(index).TaskSta == TaskMajor.TASK_STATE_CANCEL
					|| mList.get(index).TaskSta == TaskMajor.TASK_STATE_COMPLETE)
				return;

			String[] strs = new String[] { getString(R.string.task_cancel) };

			AlertDialog dlg = new AlertDialog.Builder(PTListActivity.this)
					.setTitle("")
					.setItems(strs, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							if (item == 0) { // 取消
								mTaskId = mList.get(index).TaskId;
								TaskCancel();
							}
						}
					}).create();
			dlg.show();
		}
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
		parmValues.put("taskDate", dateline.getDate());
		if (Property.OwnStation != null)
			parmValues.put("stationCode", Property.OwnStation.Code);
		String methodPath = Constant.MP_TASK;
		String methodName = Constant.MN_GET_TASKMAIN;

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
			ArrayList<TaskMajor> list = TaskMajor.ParseFromString(result);

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

	// 发布任务
	private void TaskPublish() {
		showProgressDialog(R.string.task_notify_publishing);
		new Thread() {
			public void run() {
				HashMap<String, String> paramValues = new HashMap<String, String>();
				paramValues.put("sessionId", Property.SessionId);
				paramValues.put("taskId", String.valueOf(mTaskId));

				String methodPath = Constant.MP_TASK;
				String methodName = Constant.MN_PUBLISH_TASK;
				WebServiceManager webServiceManager = new WebServiceManager(
						getApplicationContext(), methodName, paramValues);
				String result = webServiceManager.OpenConnect(methodPath);

				if (result == null || result.equals("")) {
					myhandle.sendEmptyMessage(MSG_PUBLISH_TASK_FAIL);
				}

				int Code = JsonUtil.GetJsonInt(result, "Code");

				switch (Code) {
				case Constant.NORMAL:
					myhandle.sendEmptyMessage(MSG_PUBLISH_TASK_SUCC);
					break;
				case Constant.EXCEPTION:
				default:
					myhandle.sendEmptyMessage(MSG_PUBLISH_TASK_FAIL);
					break;
				}

				return;
			}
		}.start();
	}

	// 重新发布
	private void ShowContinue2Publish() {
		AlertDialog.Builder builder = new Builder(PTListActivity.this);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.Prompt);
		builder.setCancelable(false);

		builder.setMessage(R.string.task_publish_fail);
		// 重新发布
		builder.setPositiveButton(R.string.task_publish_retry,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						TaskPublish();
					}
				});

		// 取消
		builder.setNegativeButton(R.string.task_publish_cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						finish();
					}
				});
		builder.create().show();
	}

	// 取消任务
	private void TaskCancel() {
		showProgressDialog(R.string.task_notify_canceling);
		new Thread() {
			public void run() {
				HashMap<String, String> paramValues = new HashMap<String, String>();
				paramValues.put("sessionId", Property.SessionId);
				paramValues.put("taskId", String.valueOf(mTaskId));

				String methodPath = Constant.MP_TASK;
				String methodName = Constant.MN_DELETE_TASK;
				WebServiceManager webServiceManager = new WebServiceManager(
						getApplicationContext(), methodName, paramValues);
				String result = webServiceManager.OpenConnect(methodPath);

				if (result == null || result.equals("")) {
					myhandle.sendEmptyMessage(MSG_CANCEL_TASK_FAIL);
				}

				int Code = JsonUtil.GetJsonInt(result, "Code");

				switch (Code) {
				case Constant.NORMAL:
					myhandle.sendEmptyMessage(MSG_CANCEL_TASK_SUCC);
					break;
				case Constant.EXCEPTION:
				default:
					myhandle.sendEmptyMessage(MSG_CANCEL_TASK_FAIL);
					break;
				}

				return;
			}
		}.start();
	}

	// 重新取消任务
	private void ShowContinue2Cancel() {
		AlertDialog.Builder builder = new Builder(PTListActivity.this);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.Prompt);
		builder.setCancelable(false);

		builder.setMessage(R.string.task_cancel_fail);
		// 重新取消任务
		builder.setPositiveButton(R.string.task_cancel_retry,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						TaskCancel();
					}
				});

		// 取消
		builder.setNegativeButton(R.string.task_cancel_cancel, null);
		builder.create().show();
	}
}
