package easyway.Mobile.VacationApply;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.Parameter;
import easyway.Mobile.Data.Vacation;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.PullRefreshListView;
import easyway.Mobile.util.PullRefreshListView.OnRefreshListener;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
 *  请假列表
 */
public class VacationApplyListActivity extends ActivityEx implements
		OnClickListener {
	private final int REQUEST_CODE_VIEW = 102;
	public static final String KEY_CODE = "key";

	private PullRefreshListView mListView;
	private VacationAdapter mAdapter;
	private LinearLayout LayoutSearch;

	private Button btnStartDate;
	private Button btnEndDate;
	private Button btnState;

	private EditText edtSearch;
	private Button btnSearch;

	private int mYear; // 年
	private int mMonth; // 月
	private int mDay; // 日
	private String beginDate; // 开始日期
	private String endDate; // 结束日期

	private ArrayList<Parameter> mStatusList = new ArrayList<Parameter>();
	private Parameter mStatus = null;

	private ArrayList<Vacation> mList;
	private boolean isPullRefresh = false;

	private final int MSG_GETVACATION_FAIL = 1; // 获取请假列表失败
	private final int MSG_GETVACATION_SUCC = 2; // 获取请假列表成功
	private final int MSG_GETVACATIONTSTATUS_FAIL = 3; // 获取请假状态列表失败

	@SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			if (msg.what != MSG_GETVACATIONTSTATUS_FAIL)
				closeProgressDialog();
			switch (msg.what) {
			case MSG_GETVACATION_FAIL:
			case MSG_GETVACATIONTSTATUS_FAIL:
				showToast(errMsg);
				break;
			case MSG_GETVACATION_SUCC:
				if (isPullRefresh) {
					isPullRefresh = false;
					mListView.onRefreshComplete();
				}
				mList = (ArrayList<Vacation>) msg.obj;
				mAdapter.setData(mList);
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
		setContentView(R.layout.vacation_applylist);
		initView();

		Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		// 所有状态
		Parameter all = new Parameter("",
				getString(R.string.Vacation_Status_All),
				Parameter.PARAM_CODE_LEAVESTATUS);
		mStatusList.add(all);

		getData();
		getStatus();
	}

	private void initView() {
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.Vacation_Applylist_Title);

		Button btnAdd = (Button) findViewById(R.id.btnset);
		btnAdd.setVisibility(View.VISIBLE);
		btnAdd.setText(R.string.Vacation_Search);
		btnAdd.setOnClickListener(this);

		LayoutSearch = (LinearLayout) findViewById(R.id.LayoutSearch);

		edtSearch = (EditText) findViewById(R.id.search_edit);
		edtSearch.setHint(R.string.Vacation_UserInput);

		btnStartDate = (Button) findViewById(R.id.btnStartDate);
		btnStartDate.setOnClickListener(this);

		btnEndDate = (Button) findViewById(R.id.btnEndDate);
		btnEndDate.setOnClickListener(this);

		btnState = (Button) findViewById(R.id.btnState);
		btnState.setOnClickListener(this);

		btnSearch = (Button) findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(this);

		mListView = (PullRefreshListView) findViewById(R.id.list);
		mAdapter = new VacationAdapter(this, null);
		mListView.setAdapter(mAdapter);

		mListView.setonRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				isPullRefresh = true;
				getData();
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int index = arg2 - 1;
				if (index >= 0 && index < mList.size()) {
					Intent intent = new Intent(VacationApplyListActivity.this,
							VacationViewActivity.class);
					Bundle extras = new Bundle();
					extras.putSerializable(Vacation.KEY_VACATION,
							mList.get(index));
					intent.putExtras(extras);
					startActivityForResult(intent, REQUEST_CODE_VIEW);
				}
			}
		});
	}

	// 获取请假状态列表
	private void getStatus() {
		new Thread() {
			public void run() {
				getVacationStatusType();
			}
		}.start();
	}

	// 获取请假状态列表
	private void getVacationStatusType() {
		ArrayList<Parameter> params = Parameter.GetParamByCode(
				Parameter.PARAM_CODE_LEAVESTATUS,
				VacationApplyListActivity.this);

		if (params == null || params.size() == 0)
			myhandle.sendEmptyMessage(MSG_GETVACATIONTSTATUS_FAIL);
		else
			mStatusList.addAll(params);
	}

	// 获取数据
	private void getData() {
		showProgressDialog(R.string.GettingData);

		new Thread() {
			public void run() {
				getVacation();
			}
		}.start();
	}

	// 获取请假列表
	private void getVacation() {
		HashMap<String, String> paramValues = new HashMap<String, String>();
		paramValues.put("sessionId", Property.SessionId);
		paramValues.put("staffId", String.valueOf(Property.StaffId));
		if (beginDate != null && beginDate.length() != 0)
			paramValues.put("startDate", beginDate);

		if (endDate != null && endDate.length() != 0)
			paramValues.put("endDate", endDate);

		String staff = edtSearch.getText().toString().trim();
		if (staff != null && staff.length() != 0)
			paramValues.put("staffName", staff);

		if (mStatus != null)
			paramValues.put("status", mStatus.value);

		if (Property.OwnStation != null)
			paramValues.put("stationCode", Property.OwnStation.Code);

		String methodPath = Constant.MP_ATTANDENCE;
		String methodName = Constant.MN_GET_VACATION;
		WebServiceManager webServiceManager = new WebServiceManager(
				getApplicationContext(), methodName, paramValues);
		String result = webServiceManager.OpenConnect(methodPath);

		if (result == null || result.equals("")) {
			errMsg = getString(R.string.Vacation_Exp_GetVacation);
			myhandle.sendEmptyMessage(MSG_GETVACATION_FAIL);
			return;
		}

		int Code = JsonUtil.GetJsonInt(result, "Code");

		switch (Code) {
		case Constant.NORMAL:
			ArrayList<Vacation> list = Vacation.ParseFromString(result);
			Message msg = new Message();
			msg.obj = list;
			msg.what = MSG_GETVACATION_SUCC;
			myhandle.sendMessage(msg);
			break;
		case Constant.EXCEPTION:
		default:
			errMsg = JsonUtil.GetJsonString(result, "Msg");
			if (errMsg == null || errMsg.length() == 0)
				errMsg = getString(R.string.Vacation_Exp_GetVacation);
			myhandle.sendEmptyMessage(MSG_GETVACATION_FAIL);
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnset:
			if (LayoutSearch.getVisibility() == View.VISIBLE)
				LayoutSearch.setVisibility(View.GONE);
			else
				LayoutSearch.setVisibility(View.VISIBLE);
			break;
		case R.id.btnStartDate: // 开始时间
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
		case R.id.btnEndDate: // 结束时间
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
		case R.id.btnState: // 状态
			showSelectStatusDlg();
			break;
		case R.id.btnSearch: // 搜索
			getData();
			break;
		default:
			break;
		}
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

	// 选择状态
	private void showSelectStatusDlg() {
		if (null != mStatusList) {
			String[] m = new String[mStatusList.size()];
			for (int i = 0; i < mStatusList.size(); i++) {
				m[i] = mStatusList.get(i).name;
			}

			AlertDialog dlg = new AlertDialog.Builder(
					VacationApplyListActivity.this).setTitle("")
					.setItems(m, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							if (item < mStatusList.size()) {
								if (item == 0)
									mStatus = null;
								else
									mStatus = mStatusList.get(item);
								btnState.setText(mStatusList.get(item).name);
							}
						}
					}).create();
			dlg.show();
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED) {
		} else if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_VIEW:
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					if (bundle.getBoolean(KEY_CODE, false)) // 刷新界面
						getData();
				}
				break;
			default:
				break;
			}
		}
	}
}
