package easyway.Mobile.VacationApply;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.Vacation;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;

/*
 *请假单预览
 */
public class VacationViewActivity extends ActivityEx implements OnClickListener {
	private Vacation mVacation;		// 请假单

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
				showToast(R.string.Vacation_CancelSucceed);
				
				Intent intent = new Intent();
				intent.putExtra(VacationApplyListActivity.KEY_CODE, true);
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
		setContentView(R.layout.vacation_view);

		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.Vacation_View_Title);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mVacation = (Vacation) bundle.getSerializable(Vacation.KEY_VACATION);
		}
		
		initView();
	}

	private void initView() {
		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);
		
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
		case R.id.btnCancel: // 取消
			if (mVacation  != null) {
				Cancel();
			}

			break;
		case R.id.btnReturn: // 返回
			finish();
			break;
		default:
			break;
		}
	}
	
	
	private void Cancel() {
		showProgressDialog(R.string.Vacation_Cancel_Uploading);
		new Thread() {
			public void run() {
				ApplyCancel();
			}
		}.start();
	}
	
	// 提交撤销申请
	private void ApplyCancel() {
		HashMap<String, String> paramValues = new HashMap<String, String>();
		paramValues.put("sessionId", Property.SessionId);
		paramValues.put("vacationId", String.valueOf(mVacation.Id));
		
		String methodPath = Constant.MP_ATTANDENCE;
		String methodName = Constant.MN_CANCEL_VACATION;
		WebServiceManager webServiceManager = new WebServiceManager(
				getApplicationContext(), methodName, paramValues);
		String result = webServiceManager.OpenConnect(methodPath);

		if (result == null || result.equals("")) {
			errMsg = getString(R.string.Vacation_Exp_CancelUploadFail);
			myhandle.sendEmptyMessage(MSG_UPLOAD_FAIL);
			return;
		}

		int Code = JsonUtil.GetJsonInt(result, "Code");

		switch (Code) {
		case Constant.NORMAL:
			myhandle.sendEmptyMessage(MSG_UPLOAD_SUCCEED);
			break;
		case Constant.EXCEPTION:
		default:
			errMsg = JsonUtil.GetJsonString(result, "Msg");
			if (errMsg == null || errMsg.length() == 0)
				errMsg = getString(R.string.Vacation_Exp_CancelUploadFail);
			myhandle.sendEmptyMessage(MSG_UPLOAD_FAIL);
			break;
		}
	}
}
