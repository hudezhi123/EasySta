package easyway.Mobile.ShiftNew;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.PullRefreshListView;
import easyway.Mobile.util.PullRefreshListView.OnRefreshListener;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

/*
 * 交接班-出勤情况
 */
public class ShiftAttendanceActivity extends ActivityEx {
	private ShiftAttendanceAdapter mAdpater;
	private boolean isPullRefresh = false;
	private PullRefreshListView lstAtte;
	private TextView txtSummary;

	private String mDate = "";
	private DateChangeReceiver receiver;

	private long mTotal = 0;			// 总数
	private long mActual = 0;			
	private long mLeave = 0;
	private long mAbsence = 0;

	private final int MSG_GETDATA_FAIL = 0;
	private final int MSG_GETDATA_SUCCEED = 1;
	private final int MSG_DATE_CHANGE = 2;

	@SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			closeProgressDialog();
			if (isPullRefresh) {
				lstAtte.onRefreshComplete();
				isPullRefresh = false;
			}

			switch (msg.what) {
			case MSG_GETDATA_FAIL:
				showToast(errMsg);
				
				txtSummary.setVisibility(View.GONE);
				mAdpater.setData(null);
				mAdpater.notifyDataSetChanged();
				break;
			case MSG_GETDATA_SUCCEED:
				mAdpater.setData((ArrayList<ShiftAttendance>) msg.obj);
				mAdpater.notifyDataSetChanged();

				if (mTotal != 0) {
					txtSummary.setVisibility(View.VISIBLE);
					txtSummary.setText(String.format(
							getString(R.string.Shift_Atte_Summary), mTotal,
							mActual, mLeave, mAbsence));
				} else {
					txtSummary.setVisibility(View.GONE);
				}
				break;
			case MSG_DATE_CHANGE:
				mDate = ShiftTabActivity.mDate;
				getData();
				break;
			default:
				break;
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shift_attendance);

		initView();
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mDate == null || mDate.equals("")) {
			mDate = ShiftTabActivity.mDate;
			getData();
		} else if (!mDate.equals(ShiftTabActivity.mDate)) {
			mDate = ShiftTabActivity.mDate;
			getData();
		}

		regReceiver();
	}

	@Override
	public void onPause() {
		super.onPause();

		if (receiver != null) {
			try {
				unregisterReceiver(receiver);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void initView() {
		lstAtte = (PullRefreshListView) findViewById(R.id.attelist);
		mAdpater = new ShiftAttendanceAdapter(this, null);
		lstAtte.setAdapter(mAdpater);
		lstAtte.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				isPullRefresh = true;
				getData();
			}
		});

		txtSummary = (TextView) findViewById(R.id.txtSummary);

		TextView txtNo = (TextView) findViewById(R.id.txtNo);
		TextView txtName = (TextView) findViewById(R.id.txtName);
		TextView txtStatus = (TextView) findViewById(R.id.txtStatus);
		TextView txtDep = (TextView) findViewById(R.id.txtDep);

		txtNo.setWidth(Property.screenwidth / 8);
		txtName.setWidth(Property.screenwidth / 4);
		txtStatus.setWidth(Property.screenwidth / 4);
		txtDep.setWidth(Property.screenwidth * 3 / 8);

		txtNo.setText(R.string.Shift_Atte_No);
		txtName.setText(R.string.Shift_Atte_Name);
		txtStatus.setText(R.string.Shift_Atte_Status);
		txtDep.setText(R.string.Shift_Atte_Dep);
		
		txtNo.getPaint().setFakeBoldText(true);
		txtName.getPaint().setFakeBoldText(true);
		txtStatus.getPaint().setFakeBoldText(true);
		txtDep.getPaint().setFakeBoldText(true);
	}

	// 获取出勤情况
	private void GetAttendance() {
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("searchDate", mDate);
		if (Property.OwnStation != null)
			parmValues.put("stationCode", Property.OwnStation.Code);

		String methodPath = Constant.MP_SHIFT;
		String methodName = Constant.MN_GET_ATTENDANCE;
		WebServiceManager webServiceManager = new WebServiceManager(
				getApplicationContext(), methodName, parmValues);
		String result = webServiceManager.OpenConnect(methodPath);

		if (result == null || result.equals("")) {
			errMsg = getString(R.string.exp_getdata);
			myhandle.sendEmptyMessage(MSG_GETDATA_FAIL);
			return;
		}

		int Code = JsonUtil.GetJsonInt(result, "Code");
		switch (Code) {
		case Constant.NORMAL:
			ArrayList<ShiftAttendance> list = new ArrayList<ShiftAttendance>();

			JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
			if (jsonArray != null && jsonArray.length() != 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
					ShiftAttendance obj = new ShiftAttendance();
					obj.Name = JsonUtil.GetJsonObjStringValue(jsonObj,
							"StaffName");
					obj.Status = JsonUtil.GetJsonObjStringValue(jsonObj,
							"Status");
					obj.Dep = JsonUtil.GetJsonObjStringValue(jsonObj,
							"DeptName");
					list.add(obj);
				}
			}

			JSONObject jsonObjSum = JsonUtil.GetJsonObj(result, "Summary");
			mTotal = JsonUtil.GetJsonObjLongValue(jsonObjSum, "Total");
			mActual = JsonUtil.GetJsonObjLongValue(jsonObjSum, "Actual");
			mLeave = JsonUtil.GetJsonObjLongValue(jsonObjSum, "Leave");
			mAbsence = JsonUtil.GetJsonObjLongValue(jsonObjSum, "Absence");

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

	// 获取数据
	private void getData() {
		showProgressDialog(R.string.GettingData);
		new Thread() {
			public void run() {
				GetAttendance();
			}
		}.start();
	}

	private void regReceiver() {
		receiver = new DateChangeReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ShiftTabActivity.ACTION_DATE_CHANGE);
		registerReceiver(receiver, filter);
	}

	// 自定义一个广播接收器
	public class DateChangeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			myhandle.sendEmptyMessage(MSG_DATE_CHANGE);
		}
	}
}
