package easyway.Mobile.VacationAudit;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.Vacation;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;

/*
 *请假审批
 */
public class VacationAuditActivity extends ActivityEx implements OnClickListener {
	private Vacation mVacation;	// 请假单

	private EditText edtAudit;	// 请假审批

	private final int MSG_UPLOAD_FAIL = 1;
	private final int MSG_UPLOAD_SUCCEED = 2;
	
	@SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			closeProgressDialog();
			switch (msg.what) {
			case MSG_UPLOAD_FAIL:
				showToast(errMsg);
				break;
			case MSG_UPLOAD_SUCCEED:
				Boolean bAudit = (Boolean) msg.obj;
				if (bAudit)
					showToast(R.string.Vacation_AuditVacationSucceed);
				else
					showToast(R.string.Vacation_RejectVacationSucceed);
				
				Intent intent = new Intent();
				intent.putExtra(VacationAuditListActivity.KEY_CODE, true);
				setResult(RESULT_OK, intent);
				
				finish();
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vacation_audit);

		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.Vacation_Audit_Title);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mVacation = (Vacation) bundle.getSerializable(Vacation.KEY_VACATION);
		}
		
		initView();
	}

	private void initView() {
		Button btnAudit = (Button) findViewById(R.id.btnAudit);
		btnAudit.setOnClickListener(this);
		Button btnReject = (Button) findViewById(R.id.btnReject);
		btnReject.setOnClickListener(this);
		
		Button btnReturn = (Button) findViewById(R.id.btnReturn);
		btnReturn.setOnClickListener(this);
		
		TextView txtID = (TextView) findViewById(R.id.txtID);	// 请假单号
		TextView txtUser = (TextView) findViewById(R.id.txtUser);	// 请假人
		TextView txtType = (TextView) findViewById(R.id.txtType);	 // 假别
		TextView txtStartTime = (TextView) findViewById(R.id.txtStartTime);	// 开始时间
		TextView txtEndTime = (TextView) findViewById(R.id.txtEndTime);	// 结束时间
		TextView txtDays = (TextView) findViewById(R.id.txtDays);	// 周期：日
		TextView txttHours = (TextView) findViewById(R.id.txttHours);	// 周期：小时
		TextView txtRemark = (TextView) findViewById(R.id.txtRemark); // 备注
		
		edtAudit = (EditText) findViewById(R.id.edtAudit);
		if (mVacation != null) {
			txtID.setText(String.valueOf(mVacation.Id));
			txtUser.setText(mVacation.UserName + "(" + mVacation.DeptName + ")");
			txtType.setText(mVacation.Type);
			txtStartTime.setText(mVacation.StartTime);
			txtEndTime.setText(mVacation.EndTime);
			txtDays.setText(String.valueOf(mVacation.Days));
			txttHours.setText(String.valueOf(mVacation.Hours));
			txtRemark.setText(mVacation.Remark);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnAudit: // 审核
			if (mVacation  != null) {
				ApplyAudit(true);
			}
			break;
		case R.id.btnReject:	// 驳回
			if (mVacation  != null) {
				ApplyAudit(false);
			}
			break;
		case R.id.btnReturn: // 返回
			finish();
			break;
		default:
			break;
		}
	}
	
	// 提交审批
	private void ApplyAudit(final boolean bAudit) {
		if (bAudit)
			showProgressDialog(R.string.Vacation_Audit_Uploading);
		else
			showProgressDialog(R.string.Vacation_Reject_Uploading);
		
		new Thread() {
			public void run() {
				Audit(bAudit);
			}
		}.start();
	}
	
	// 审批
	private void Audit(boolean bAudit) {
		HashMap<String, String> paramValues = new HashMap<String, String>();
		paramValues.put("sessionId", Property.SessionId);
		paramValues.put("vacationId", String.valueOf(mVacation.Id));
		paramValues.put("IsAgree", String.valueOf(bAudit));
		String remark = edtAudit.getText().toString().trim();
		if (remark != null && remark.length() != 0)
			paramValues.put("remark", remark);
		
		String methodPath = Constant.MP_ATTANDENCE;
		String methodName = Constant.MN_AUDIT_VACATION;
		WebServiceManager webServiceManager = new WebServiceManager(
				getApplicationContext(), methodName, paramValues);
		String result = webServiceManager.OpenConnect(methodPath);

		if (result == null || result.equals("")) {
			if (bAudit)
				errMsg = getString(R.string.Vacation_Exp_AuditVacationFail);
			else
				errMsg = getString(R.string.Vacation_Exp_RejectVacationFail);
			myhandle.sendEmptyMessage(MSG_UPLOAD_FAIL);
			return;
		}

		int Code = JsonUtil.GetJsonInt(result, "Code");

		switch (Code) {
		case Constant.NORMAL:
			Message msg = new Message();
			msg.what = MSG_UPLOAD_SUCCEED;
			msg.obj = bAudit;
			myhandle.sendMessage(msg);
			break;
		case Constant.EXCEPTION:
		default:
			errMsg = JsonUtil.GetJsonString(result, "Msg");
			if (errMsg == null || errMsg.length() == 0) {
				if (bAudit)
					errMsg = getString(R.string.Vacation_Exp_AuditVacationFail);
				else
					errMsg = getString(R.string.Vacation_Exp_RejectVacationFail);
			}

			myhandle.sendEmptyMessage(MSG_UPLOAD_FAIL);
			break;
		}
	}
}
