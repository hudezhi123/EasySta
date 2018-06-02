package easyway.Mobile.site_monitoring;

import java.util.HashMap;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.MessageType;
import easyway.Mobile.Data.Staff;
import easyway.Mobile.PointTask.TaskChild;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.CommonFunc;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.PTTUtil;
import easyway.Mobile.util.StringUtil;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/*
 * 未到岗提醒
 */
public class UnDutyTipsActivity extends ActivityEx {
	private SMTask mtask;
	private long mStaffId = SMTask.INVALID_STAFFID;
	private String mStaffName;

	private EditText receiverEdt;
	private EditText tipsContentEdt;
	private Button tipsBtn;
	private Button callBtn;

	private final int MSG_SEND_FAIL = 0;
	private final int MSG_SEND_SUCCEED = 1;

	@SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			closeProgressDialog();
			switch (msg.what) {
			case MSG_SEND_FAIL:
				showToast(errMsg);
				break;
			case MSG_SEND_SUCCEED:
				showToast(R.string.sendsucc);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.site_tips_layout);

		Bundle extras = getIntent().getExtras();
		if (extras != null)
			mtask = (SMTask) extras.getSerializable(SMTask.KEY_TASK);
		mStaffId = extras.getLong(TaskDetailActivity.KEY_STAFFID);
		mStaffName = extras.getString(TaskDetailActivity.KEY_STAFFNAME);

		initView();
	}

	private void initView() {
		receiverEdt = (EditText) findViewById(R.id.receiver);
		receiverEdt.setText(mStaffName);
		tipsContentEdt = (EditText) findViewById(R.id.tip_content);

		String text = "";
		if (mtask.AExcStat == TaskChild.TASK_EXCSTATE_UNDUTY) {
			text = String.format(getString(R.string.tips_content),
					mtask.BeginWorkTime, mtask.Workspace, mtask.TrainNum);
		} else if (mtask.AExcStat == TaskChild.TASK_EXCSTATE_ONDUTY) {
			text = String.format(getString(R.string.tips_content_duty),
					mtask.BeginWorkTime, mtask.Workspace, mtask.TrainNum);
		} else if (mtask.AExcStat == TaskChild.TASK_EXCSTATE_COMPLETE) {
			text = String.format(getString(R.string.tips_content_done),
					mtask.BeginWorkTime, mtask.Workspace, mtask.TrainNum);
		}

		tipsContentEdt.setText(text);

		tipsBtn = (Button) findViewById(R.id.commitBtn);
		tipsBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showProgressDialog(R.string.waitforsend);
				new Thread() {
					public void run() {
						commiteTips();
					}
				}.start();
			}
		});

		callBtn = (Button) findViewById(R.id.callBtn);
		callBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PTTUtil.call(UnDutyTipsActivity.this, Staff.GetExpend1ByStaffId(UnDutyTipsActivity.this, mStaffId),
						PTTUtil.AUDIO_CALL);
			}
		});

		Button cancelBtn = (Button) findViewById(R.id.cancelBtn);
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void commiteTips() {
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("senderId", Long.toString(Property.StaffId));
		parmValues.put("senderName", Property.UserName);
		parmValues.put("senderIp", CommonFunc.getLocalIpAddress());
		parmValues.put("context",
				StringUtil.Encode(tipsContentEdt.getText().toString(), true));
		parmValues.put("staffIdList", String.valueOf(mStaffId));
		parmValues.put("staffNameList", mStaffName);
		parmValues.put("messageType", "1");
		parmValues.put("contentType",
				Integer.toString(MessageType.TYPE_TASK_NOTICE));
		parmValues.put("attachList", "");
		String methodPath = Constant.MP_SMS;
		String methodName = Constant.MN_SEND_MESSAGE;
		WebServiceManager webServiceManager = new WebServiceManager(
				UnDutyTipsActivity.this, methodName, parmValues);
		String result = webServiceManager.OpenConnect(methodPath);

		if (result == null || result.equals("")) {
			errMsg = getString(R.string.exp_sendmsg);
			return;
		} else {
			int Code = JsonUtil.GetJsonInt(result, "Code");
			switch (Code) {
			case Constant.NORMAL:
				myhandle.sendEmptyMessage(MSG_SEND_SUCCEED);
				return;
			case Constant.EXCEPTION:
				errMsg = getString(R.string.exp_sendmsg);
				break;
			default:
				errMsg = JsonUtil.GetJsonString(result, "Msg");
				break;
			}
		}

		myhandle.sendEmptyMessage(MSG_SEND_FAIL);
	}
}
