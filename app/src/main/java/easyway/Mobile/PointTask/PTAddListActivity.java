package easyway.Mobile.PointTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Attach.FileUploadTask;
import easyway.Mobile.Media.FileAccessEx;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.LogUtil;
import easyway.Mobile.util.StringUtil;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/*
 * 重点任务子任务列表
 */
public class PTAddListActivity extends ActivityEx implements OnClickListener {
	private final int REQUEST_CODE_ADDCHILD = 100;
	private final int REQUEST_CODE_MODIFYCHILD = 101;
	private ListView mListView;
	private PTAddAdapter mAdapter;
	private ArrayList<ChildMode> mList;

	private MajorMode mMajor = new MajorMode();;

	private boolean isPublish = false;
	private int mIndex = PTAddActivity.INDEX_FROM_LIST;
	private final int MSG_ADD_CHILD = 1;
	private final int MSG_MODIFY_CHILD = 2;
	private final int MSG_DELETE_CHILD = 3;
	private final int MSG_UPLOAD_TASK = 4;
	private final int MSG_PUBLISH_TASK_SUCC = 5;
	private final int MSG_PUBLISH_TASK_FAIL = 6;
	@SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what != MSG_UPLOAD_TASK)
				closeProgressDialog();
			switch (msg.what) {
			case MSG_ADD_CHILD:
				ChildMode child = (ChildMode) msg.obj;
				if (mList == null)
					mList = new ArrayList<ChildMode>();
				mList.add(child);
				mAdapter.setData(mList);
				mAdapter.notifyDataSetChanged();
				break;
			case MSG_MODIFY_CHILD:
				ChildMode child_modify = (ChildMode) msg.obj;
				LogUtil.i("child_modify.AttachList.size()  -->" + child_modify.AttachList.size());
				LogUtil.i("mIndex -->" + mIndex);
				
				if (mList == null)
					return;

				if (mIndex < 0 || mIndex > (mList.size() - 1))
					return;

				mList.set(mIndex, child_modify);
				mAdapter.setData(mList);
				mAdapter.notifyDataSetChanged();
				break;
			case MSG_DELETE_CHILD:
				if (mList == null)
					return;

				if (mIndex < 0 || mIndex > (mList.size() - 1))
					return;

				mList.remove(mIndex);
				mAdapter.setData(mList);
				mAdapter.notifyDataSetChanged();
				break;
			case MSG_UPLOAD_TASK:
				ChildMode child_upload = (ChildMode) msg.obj;
				if (mList == null)
					return;

				if (child_upload.index < 0
						|| child_upload.index > (mList.size() - 1))
					return;

				mList.set(child_upload.index, child_upload);
				mAdapter.setData(mList);
				mAdapter.notifyDataSetChanged();

				if (child_upload.index == mList.size() - 1) {
					LogUtil.d("upload task end !!!!");
					boolean ret = true;
					for (ChildMode childcheck : mList) {
						if (childcheck.UpdateState != ChildMode.UPDATESTATE_ALL)
							ret = false;
					}

					if (ret) {
						if (isPublish)		// 发布
							TaskPublish();
						else					// 保存
							finish();
					} else {
						closeProgressDialog();
						ShowContinue2Upload();
					}
				}
				break;
			case MSG_PUBLISH_TASK_SUCC:
				showToast(R.string.task_publish_succeed);
				finish();
				break;
			case MSG_PUBLISH_TASK_FAIL:
				ShowContinue2Publish();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pointtask_addlist);

		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.task_title_childlist);
		
		initView();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mMajor = (MajorMode) bundle
					.getSerializable(PTAddActivity.KEY_MAJOR);
			ChildMode child = (ChildMode) bundle
					.getSerializable(PTAddActivity.KEY_CHILD);
			if (child != null) {
				Message msg = new Message();
				msg.obj = child;
				msg.what = MSG_ADD_CHILD;
				myhandle.sendMessage(msg);
			}
		}
	}

	private void initView() {
		mListView = (ListView) findViewById(R.id.childtasklist);
		mAdapter = new PTAddAdapter(this, mList);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (mList == null)
					return;

				if (arg2 < 0 || arg2 > (mList.size() - 1))
					return;

				mIndex = arg2;

				if (mList.get(arg2).UpdateState == ChildMode.UPDATESTATE_NOT) {
					Intent intent = new Intent(PTAddListActivity.this,
							PTAddChildActivity.class);
					Bundle extras = new Bundle();
					extras.putSerializable(PTAddActivity.KEY_CHILD, mList.get(arg2));
					extras.putInt(PTAddActivity.KEY_INDEX, arg2);
					intent.putExtras(extras);
					startActivityForResult(intent, REQUEST_CODE_MODIFYCHILD);
				} else {
					showToast(R.string.task_notify_couldnotedit);
				}
			}
		});

		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (mList == null)
					return false;

				if (arg2 < 0 || arg2 > (mList.size() - 1))
					return false;

				mIndex = arg2;
				if (mList.get(arg2).UpdateState == ChildMode.UPDATESTATE_NOT) {
					showDeleteDialog();
				} else {
					showToast(R.string.task_notify_couldnotedit);
				}

				return false;
			}
		});

		// 发布
		Button btnPulish = (Button) findViewById(R.id.btnPulish);
		btnPulish.setOnClickListener(this);

		Button btnPreview = (Button) findViewById(R.id.btnPreview);
		btnPreview.setOnClickListener(this);
		
		Button btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(this);

		Button btnReturn = (Button) findViewById(R.id.btnReturn);
		btnReturn.setVisibility(View.INVISIBLE);

		Button addBtn = (Button) findViewById(R.id.btnset);
		addBtn.setVisibility(View.VISIBLE);
		addBtn.setText(R.string.task_add);
		addBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnPulish:
			if (mList == null || mList.size() == 0) {
				showToast(R.string.task_notify_inputchildtask);
				return;
			}

			ShowPublishConfirm();
			break;
		case R.id.btnPreview:
			Intent intentPre = new Intent(PTAddListActivity.this,
					PTAddActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable(PTAddActivity.KEY_MAJOR, mMajor);
			bundle.putBoolean(PTAddActivity.KEY_EDIT, true);
			intentPre.putExtras(bundle);
			startActivity(intentPre);
//
//			finish();
			break;
		case R.id.btnset:
			Intent intent = new Intent(PTAddListActivity.this,
					PTAddChildActivity.class);
			intent.putExtra(PTAddActivity.KEY_INDEX,
					PTAddActivity.INDEX_FROM_LIST);
			startActivityForResult(intent, REQUEST_CODE_ADDCHILD);
			break;
		case R.id.btnSave:
			if (mList == null || mList.size() == 0) {
				showToast(R.string.task_notify_inputchildtask);
				return;
			}

			isPublish = false;
			Post2Server();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED) {
			// do nothing
		} else if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_ADDCHILD: // 添加子任务
			case REQUEST_CODE_MODIFYCHILD: // 修改子任务
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					ChildMode child = (ChildMode) bundle
							.getSerializable(PTAddActivity.KEY_CHILD);
					mIndex = bundle.getInt(PTAddActivity.KEY_INDEX,
							PTAddActivity.INDEX_FROM_LIST);
					if (child != null) {
						Message msg = new Message();
						msg.obj = child;
						if (mIndex < 0)
							msg.what = MSG_ADD_CHILD;
						else
							msg.what = MSG_MODIFY_CHILD;
						myhandle.sendMessage(msg);
					}
				}
				break;
			default:
				break;
			}
		}
	}

	private void showDeleteDialog() {
		AlertDialog.Builder builder = new Builder(PTAddListActivity.this);
		builder.setMessage(R.string.task_confirm_deletechild);
		builder.setPositiveButton(R.string.OK,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						myhandle.sendEmptyMessage(MSG_DELETE_CHILD);
					}
				});

		builder.setNegativeButton(R.string.Cancel, null);
		builder.setCancelable(true);
		builder.create().show();
	}

	// 上传任务
	private void Post2Server() {
		showProgressDialog(R.string.task_notify_uploading);
		new Thread() {
			public void run() {
				for (int index = 0; index < mList.size(); index++) {
					ChildMode child = mList.get(index);
					if (child.UpdateState == ChildMode.UPDATESTATE_ALL) { // 已经成功上传
						// do nothing
					}

					if (child.UpdateState == ChildMode.UPDATESTATE_PART) { // 部分成功上传
						if (child.SaId == ChildMode.SAID_INVALID) { // 若SaId无效，则判定为未上传
							child.UpdateState = ChildMode.UPDATESTATE_NOT;
						} else {
							child.IsAttachUpdateList = UpdateAttach(child); // 上传附件

							if (child.IsAttachUpdateList == null
									|| child.IsAttachUpdateList.size() == 0) { // 若无附件则判定为上传成功
								child.UpdateState = ChildMode.UPDATESTATE_ALL;
							} else if (!child.IsAttachUpdateList
									.contains(false)) { // 附件全部上传成功
								child.UpdateState = ChildMode.UPDATESTATE_ALL;
							}
						}
					}

					if (child.UpdateState == ChildMode.UPDATESTATE_NOT) { // 还未上传\
						if (child.SaId == ChildMode.SAID_INVALID)
							child.SaId = UpdateTask(child); // 上传任务
						else
							UpdateTask(child); // 编辑任务

						if (child.SaId != ChildMode.SAID_INVALID) {
							child.UpdateState = ChildMode.UPDATESTATE_PART;
							if (child.AttachList == null
									|| child.AttachList.size() == 0) { // 不存在附件，则判定为上传成功
								child.UpdateState = ChildMode.UPDATESTATE_ALL;
							} else {
								child.IsAttachUpdateList = UpdateAttach(child);
								if (child.IsAttachUpdateList == null
										|| child.IsAttachUpdateList.size() == 0) { // 若无附件则判定为上传成功
									child.UpdateState = ChildMode.UPDATESTATE_ALL;
								} else if (!child.IsAttachUpdateList
										.contains(false)) {// 附件全部上传成功
									child.UpdateState = ChildMode.UPDATESTATE_ALL;
								}
							}
						}
					}

					child.index = index;
					Message msg = new Message();
					msg.obj = child;
					msg.what = MSG_UPLOAD_TASK;
					myhandle.sendMessage(msg);
				}
			}
		}.start();
	}

	private String setTaskData(ChildMode child) {
		if (child == null)
			return "";

		JSONObject object = new JSONObject();
		try {
			object.put("BeginWorkTime", child.BeginWorkTime);
			object.put("EndWorkTime", child.EndWorkTime);
			object.put("StaffId", child.StaffId);
			object.put("StaffName", child.StaffName);
			object.put("PId", child.PId);
			object.put("PositionName", child.PositionName);
			object.put("TwId", child.TwId);
			object.put("Workspace", child.Workspace);
			object.put("TaskRemark", child.TaskRemark);
		} catch (Exception e) {

		}
		return object.toString();
	}

	// 上传子任务
	private long UpdateTask(ChildMode child) {
		String valueStr = setTaskData(child);

		HashMap<String, String> paramValues = new HashMap<String, String>();
		paramValues.put("sessionId", Property.SessionId);
		paramValues.put("valueStr", StringUtil.Encode(valueStr, true));
		paramValues.put("taskId", String.valueOf(mMajor.TaskId));
		paramValues.put("saId", String.valueOf(child.SaId));
		if (Property.OwnStation != null)
			paramValues.put("stationCode", Property.OwnStation.Code);
		String methodPath = Constant.MP_TASK;
		String methodName = Constant.MN_SAVE_TASKITEM;
		WebServiceManager webServiceManager = new WebServiceManager(
				getApplicationContext(), methodName, paramValues);
		String result = webServiceManager.OpenConnect(methodPath);

		if (result == null || result.equals("")) {
			return ChildMode.SAID_INVALID;
		}

		int Code = JsonUtil.GetJsonInt(result, "Code");

		long ret = ChildMode.SAID_INVALID;
		switch (Code) {
		case Constant.NORMAL:
			JSONObject jsonObj = (JSONObject) JsonUtil.GetJsonObj(result,
					"Data");
			if (jsonObj == null)
				break;

			ret = JsonUtil.GetJsonObjLongValue(jsonObj, "SaId");
			break;
		case Constant.EXCEPTION:
		default:
			ret = ChildMode.SAID_INVALID;
			break;
		}

		return ret;
	}

	// 上传附件
	private ArrayList<Boolean> UpdateAttach(ChildMode child) {
		if (child == null)
			return null;

		if (child.AttachList == null || child.AttachList.size() == 0)
			return null;

		ArrayList<Boolean> list;
		if (child.IsAttachUpdateList == null)
			list = new ArrayList<Boolean>();
		else
			list = child.IsAttachUpdateList;

		for (int i = 0; i < child.AttachList.size(); i++) {
			String filePath = child.AttachList.get(i);
			if (i < list.size()) {

			} else {
				list.add(false);
			}

			if (list.get(i))
				continue;

			FileAccessEx fileAccessEx;
			try {
				fileAccessEx = new FileAccessEx(filePath, 0);
				Long fileLength = fileAccessEx.getFileLength();

				byte[] buffer = new byte[FileAccessEx.PIECE_LENGHT];
				FileAccessEx.Detail detail;
				long nRead = 0l;
				long nStart = 0l;
				boolean completed = false;

				File file = new File(filePath);
				String fileName = file.getName();

				int postTimes = 0;
				JSONObject jsonP = new JSONObject();
				try {
					jsonP.put("Category", FileUploadTask.CATEGORY_TASK);
					jsonP.put("Id", child.SaId);
					jsonP.put("FileName", fileName);
					jsonP.put("DeptId", Property.DeptId);
					jsonP.put("StaffId", Property.StaffId);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				while (nStart < fileLength) {
					detail = fileAccessEx.getContent(nStart);
					nRead = detail.length;
					buffer = detail.b;

					nStart += nRead;

					if (nStart < fileLength) {
						completed = false;
					} else {
						completed = true;
					}

					HashMap<String, String> parmValues = new HashMap<String, String>();
					parmValues.put("sessionId", Property.SessionId);
					parmValues.put("position", String.valueOf(postTimes));
					parmValues.put("completed", String.valueOf(completed));
					parmValues.put("jsonValues", jsonP.toString());
					parmValues.put("fileStreamString", Base64.encodeToString(
							buffer, 0, (int) nRead, Base64.DEFAULT));

					String methodPath = Constant.MP_ATTACHMENT;
					String methodName = Constant.MN_POST_ATTACH;

					WebServiceManager webServiceManager = new WebServiceManager(
							PTAddListActivity.this, methodName, parmValues);
					String result = webServiceManager.OpenConnect(methodPath);
					if (result == null || result.equals("")) {
						break;
					}

					int code = JsonUtil.GetJsonInt(result, "Code");
					if (code != Constant.NORMAL) {
						break;
					}

					postTimes++;
					buffer = null;

					if (completed) {
						list.set(i, true);
						LogUtil.i(fileName + " upload succeed!!!");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
		return list;
	}

	// 重传
	private void ShowContinue2Upload() {
		if (null == mList || mList.size() == 0) {
			return;
		}

		AlertDialog.Builder builder = new Builder(PTAddListActivity.this);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.Prompt);
		builder.setCancelable(false);

		builder.setMessage(R.string.task_update_fail);
		// 重传
		builder.setPositiveButton(R.string.task_update_retry,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Post2Server();
					}
				});

		// 取消
		builder.setNegativeButton(R.string.task_update_cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						finish();
					}
				});
		builder.create().show();
	}

	// 发布任务
	private void TaskPublish() {
		new Thread() {
			public void run() {
				HashMap<String, String> paramValues = new HashMap<String, String>();
				paramValues.put("sessionId", Property.SessionId);
				paramValues.put("taskId", String.valueOf(mMajor.TaskId));

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
		AlertDialog.Builder builder = new Builder(PTAddListActivity.this);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.Prompt);
		builder.setCancelable(false);

		builder.setMessage(R.string.task_publish_fail);
		// 重新发布
		builder.setPositiveButton(R.string.task_publish_retry,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						showProgressDialog(R.string.task_notify_publishing);
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
	
	// 发布确认
	private void ShowPublishConfirm() {
		AlertDialog.Builder builder = new Builder(PTAddListActivity.this);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.Prompt);
		builder.setCancelable(false);

		builder.setMessage(R.string.task_confirm_publish);
		// 重新发布
		builder.setPositiveButton(R.string.OK,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						isPublish = true;
						Post2Server();
					}
				});

		// 取消
		builder.setNegativeButton(R.string.Cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						finish();
					}
				});
		builder.create().show();
	}
}
