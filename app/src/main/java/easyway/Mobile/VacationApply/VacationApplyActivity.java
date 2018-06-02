package easyway.Mobile.VacationApply;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Contacts.Contacts;
import easyway.Mobile.Data.Parameter;
import easyway.Mobile.Data.Staff;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;

/*
 * 请假申请
 */
public class VacationApplyActivity extends ActivityEx implements
		OnClickListener {
	private final int REQUEST_CODE_SELECTUSER = 100;

	private Button btnUser; // 申请人
	private Button btnType; // 假别
	private Button btnStartDate; // 开始日期
	private Button btnStartTime; // 开始时间
	private Button btnEndDate; // 结束日期
	private Button btnEndTime; // 结束时间

	private EditText edtDays; // 周期：日
	private EditText edtHours; // 周日：小时
	private EditText edtRemark; // 备注

	private Button btnConfirm; // 申请

	private int mYear; // 年
	private int mMonth; // 月
	private int mDay; // 日
	private int mHour; // 时
	private int mMinute; // 分

	private String beginDate; // 开始日期
	private String beginTime; // 开始时间
	private String endDate; // 结束日期
	private String endTime; // 结束时间
	private Staff mStaff;
	private String remark;
	private String days;
	private String hours;
	private ArrayList<Parameter> mTypeList;
	private Parameter mType;
	
	private final int MSG_UPLOAD_FAIL = 1;
	private final int MSG_UPLOAD_SUCCEED = 2;
	private final int MSG_GETTYPE_FAIL = 3;

	@SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what != MSG_GETTYPE_FAIL)
				closeProgressDialog();
			switch (msg.what) {
			case MSG_UPLOAD_FAIL:
				showToast(errMsg);
				break;
			case MSG_UPLOAD_SUCCEED:
				showToast(R.string.Vacation_ApplyUploadSucceed);
				finish();
				break;
			case MSG_GETTYPE_FAIL:
				showToast(R.string.Vacation_Exp_GetVacationType);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vacation_add);

		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.Vacation_Add_Title);

		initView();

		getType(); // 获取假别列表

		Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinute = c.get(Calendar.MINUTE);
	}

	private void initView() {
		Button btnset = (Button) findViewById(R.id.btnset);
		btnset.setVisibility(View.VISIBLE);
		btnset.setText(R.string.Vacation_List);
		btnset.setOnClickListener(this);

		// 申请人
		btnUser = (Button) findViewById(R.id.btnUser);
		btnUser.setOnClickListener(this);
		// 假别
		btnType = (Button) findViewById(R.id.btnType);
		btnType.setOnClickListener(this);
		// 开始日期
		btnStartDate = (Button) findViewById(R.id.btnStartDate);
		btnStartDate.setOnClickListener(this);
		// 开始时间
		btnStartTime = (Button) findViewById(R.id.btnStartTime);
		btnStartTime.setOnClickListener(this);
		// 结束日期
		btnEndDate = (Button) findViewById(R.id.btnEndDate);
		btnEndDate.setOnClickListener(this);
		// 结束时间
		btnEndTime = (Button) findViewById(R.id.btnEndTime);
		btnEndTime.setOnClickListener(this);
		// 申请
		btnConfirm = (Button) findViewById(R.id.btnConfirm);
		btnConfirm.setOnClickListener(this);

		Button btnReturn = (Button) findViewById(R.id.btnReturn);
		btnReturn.setOnClickListener(this);

		edtDays = (EditText) findViewById(R.id.edtDays);
		edtHours = (EditText) findViewById(R.id.edtHours);
		edtRemark = (EditText) findViewById(R.id.edtRemark);
	}

	// 日期控件
	private OnDateSetListener onDateSetLis(final Button btn) {
		OnDateSetListener listener = new OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				int mYear = year;
				int mMonth = monthOfYear;
				int mDay = dayOfMonth;
				String str = new StringBuilder()
						.append(mYear)
						.append("-")
						.append((mMonth + 1) < 10 ? "0" + (mMonth + 1)
								: (mMonth + 1)).append("-")
						.append((mDay < 10) ? "0" + mDay : mDay).toString();
				btn.setText(str);

				if (btn.getId() == R.id.btnStartDate) {
					beginDate = str;
				} else if (btn.getId() == R.id.btnEndDate) {
					endDate = str;
				}
			}
		};
		return listener;
	}

	// 时间控件
	private OnTimeSetListener onTimeSetLis(final Button btn) {
		OnTimeSetListener listener = new OnTimeSetListener() {
			@SuppressLint("SimpleDateFormat")
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				int mHour = hourOfDay;
				int mMinute = minute;
				String str = new StringBuilder().append(pad(mHour)).append(":")
						.append(pad(mMinute)).toString();
				btn.setText(str);

				if (btn.getId() == R.id.btnStartTime) {
					beginTime = str + ":00";
				} else if (btn.getId() == R.id.btnEndTime) {
					endTime = str + ":00";
				}
			}
		};
		return listener;
	}

	private String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.btnUser: // 申请人
			intent = new Intent(VacationApplyActivity.this, Contacts.class);
			intent.putExtra(Contacts.KEY_FLAG, Contacts.FLAG_POINTTASK);
			startActivityForResult(intent, REQUEST_CODE_SELECTUSER);
			break;
		case R.id.btnType: // 假别
			showSelectTypeDlg();
			break;
		case R.id.btnStartDate: // 开始日期
			if (beginDate != null && beginDate.length() != 0) {
				String[] strs = beginDate.split("-");
				if (strs != null && strs.length == 3) {
					int mYear = Integer.parseInt(strs[0]);
					int mMonth = Integer.parseInt(strs[1]) - 1;
					int mDay = Integer.parseInt(strs[2]);
				}
			}

			new DatePickerDialog(this, onDateSetLis(btnStartDate), mYear,
					mMonth, mDay).show();
			break;
		case R.id.btnStartTime: // 开始时间
			if (beginTime != null && beginTime.length() != 0) {
				String[] strs = beginTime.split(":");
				if (strs != null && strs.length == 3) {
					int mHour = Integer.parseInt(strs[0]);
					int mMinute = Integer.parseInt(strs[1]);
				}
			}

			new TimePickerDialog(this, onTimeSetLis(btnStartTime), mHour,
					mMinute, false).show();
			break;
		case R.id.btnEndDate: // 结束日期
			if (endDate != null && endDate.length() != 0) {
				String[] strs = endDate.split("-");
				if (strs != null && strs.length == 3) {
					int mYear = Integer.parseInt(strs[0]);
					int mMonth = Integer.parseInt(strs[1]) - 1;
					int mDay = Integer.parseInt(strs[2]);
				}
			}
			new DatePickerDialog(this, onDateSetLis(btnEndDate), mYear, mMonth,
					mDay).show();
			break;
		case R.id.btnEndTime: // 结束时间
			if (endTime != null && endTime.length() != 0) {
				String[] strs = endTime.split(":");
				if (strs != null && strs.length == 3) {
					int mHour = Integer.parseInt(strs[0]);
					int mMinute = Integer.parseInt(strs[1]);
				}
			}
			new TimePickerDialog(this, onTimeSetLis(btnEndTime), mHour,
					mMinute, false).show();
			break;
		case R.id.btnConfirm: // 申请
			if (checkData()) {
				Apply();
			}
			break;
		case R.id.btnReturn: // 返回
			finish();
			break;
		case R.id.btnset:
			intent = new Intent(VacationApplyActivity.this,
					VacationApplyListActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	// 选择假別
	private void showSelectTypeDlg() {
		if (null != mTypeList) {
			String[] m = new String[mTypeList.size()];
			for (int i = 0; i < mTypeList.size(); i++) {
				m[i] = mTypeList.get(i).name;
			}

			AlertDialog dlg = new AlertDialog.Builder(
					VacationApplyActivity.this).setTitle("")
					.setItems(m, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							if (item < mTypeList.size()) {

								mType = mTypeList.get(item);
								btnType.setText(mType.name);
							}
						}
					}).create();
			dlg.show();
		} else {
			showToast(R.string.Vacation_Exp_GetVacationTypeing);
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED) {
		} else if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_SELECTUSER:
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					mStaff = (Staff) bundle.getSerializable(Contacts.KEY_STAFF);
					if (mStaff != null) {
						btnUser.setText(mStaff.StaffName);
					}
				}
				break;
			default:
				break;
			}
		}
	}

	// 数据校验
	private boolean checkData() {
		remark = edtRemark.getText().toString().trim();
		// 申请人
		if (mStaff == null) {
			showToast(R.string.Vacation_Notify_InputUser);
			return false;
		}

		// 假别
		if (mType == null) {
			showToast(R.string.Vacation_Notify_InputType);
			return false;
		}

		// 开始日期
		if (beginDate == null || beginDate.length() == 0) {
			showToast(R.string.Vacation_Notify_InputStartDate);
			return false;
		}

		// 开始时间
		if (beginTime == null || beginTime.length() == 0) {
			showToast(R.string.Vacation_Notify_InputStartTime);
			return false;
		}

		// 结束日期
		if (endDate == null || endDate.length() == 0) {
			showToast(R.string.Vacation_Notify_InputEndDate);
			return false;
		}

		// 结束时间
		if (endTime == null || endTime.length() == 0) {
			showToast(R.string.Vacation_Notify_InputEndTime);
			return false;
		}

		// 假期天数
		days = edtDays.getText().toString().trim();
		if (days == null || days.length() == 0) {
			showToast(R.string.Vacation_Notify_InputDays);
			return false;
		}

		// 假期小时数
		hours = edtHours.getText().toString().trim();
		if (hours == null || hours.length() == 0) {
			showToast(R.string.Vacation_Notify_InputHours);
			return false;
		}

		return true;
	}

	// 获取假别列表
	private void getType() {
		new Thread() {
			public void run() {
				getVacationType();
			}
		}.start();
	}

	// 获取假别列表
	private void getVacationType() {
		mTypeList = Parameter.GetParamByCode(Parameter.PARAM_CODE_LEAVETYPE, VacationApplyActivity.this);
		
		if (mTypeList == null || mTypeList.size() == 0)
			myhandle.sendEmptyMessage(MSG_GETTYPE_FAIL);
	}

	// 提交申请
	private void Apply() {
		showProgressDialog(R.string.Vacation_Apply_Uploading);
		new Thread() {
			public void run() {
				ApplyUpload();
			}
		}.start();
	}

	// 提交申请
	private void ApplyUpload() {
		HashMap<String, String> paramValues = new HashMap<String, String>();
		paramValues.put("sessionId", Property.SessionId);
		paramValues.put("staffId", String.valueOf(mStaff.StaffId));
		paramValues.put("type", mType.value);
		paramValues.put("beginTime", beginDate + " " + beginTime);
		paramValues.put("endTime", endDate + " " + endTime);
		paramValues.put("days", days);
		paramValues.put("hours", hours);
		if (remark == null)
			paramValues.put("remark", "");
		else
			paramValues.put("remark", remark);

		if (Property.OwnStation != null)
			paramValues.put("stationCode", Property.OwnStation.Code);

		String methodPath = Constant.MP_ATTANDENCE;
		String methodName = Constant.MN_APPLY_VACATION;
		WebServiceManager webServiceManager = new WebServiceManager(
				getApplicationContext(), methodName, paramValues);
		String result = webServiceManager.OpenConnect(methodPath);

		if (result == null || result.equals("")) {
			errMsg = getString(R.string.Vacation_Exp_ApplyUploadFail);
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
				errMsg = getString(R.string.Vacation_Exp_ApplyUploadFail);
			myhandle.sendEmptyMessage(MSG_UPLOAD_FAIL);
			break;
		}
	}
}
