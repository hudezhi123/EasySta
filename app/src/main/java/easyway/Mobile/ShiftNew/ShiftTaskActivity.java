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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

/*
 * 交接班-重要事项
 */
public class ShiftTaskActivity extends ActivityEx {
	private ShiftTaskAdapter mAdpater;
	private boolean isPullRefresh = false;
	private PullRefreshListView lstTask;
	private LinearLayout LayoutBottom;

	private boolean BFlag = false;			// 是否可接收
	private String HId = "";			// 接班ID
	private String mDate = "";			// 日期
	private DateChangeReceiver receiver;

	private final int MSG_GETDATA_FAIL = 0;		// 获取接班信息失败
	private final int MSG_GETDATA_SUCCEED = 1;		// 获取接班信息成功
	private final int MSG_SETDATA_FAIL = 2;		// 设置接收失败
	private final int MSG_SETDATA_SUCCEED = 3;		// 设置接收成功
	private final int MSG_DATE_CHANGE = 4;		// 日期改变

	@SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			closeProgressDialog();
			if (isPullRefresh) {
				lstTask.onRefreshComplete();
				isPullRefresh = false;
			}

			switch (msg.what) {
			case MSG_GETDATA_FAIL:		// 获取接班信息失败
				mAdpater.setData(null);
				mAdpater.notifyDataSetChanged();
				
				showToast(errMsg);
				break;
			case MSG_GETDATA_SUCCEED:		// 获取接班信息成功
				mAdpater.setData((ArrayList<ShiftTask>) msg.obj);
				mAdpater.notifyDataSetChanged();

				ShowButtom();
				break;
			case MSG_SETDATA_FAIL:		// 设置接收失败
				showToast(errMsg);
//				mAdpater.setData(null);
//				mAdpater.notifyDataSetChanged();
				break;
			case MSG_SETDATA_SUCCEED:		// 设置接收成功
				BFlag = false;
				ShowButtom();
				showToast(R.string.Shift_Task_SetSucc);
				break;
			case MSG_DATE_CHANGE:		// 日期改变
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
		setContentView(R.layout.shift_task);

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
		lstTask = (PullRefreshListView) findViewById(R.id.tasklist);
		mAdpater = new ShiftTaskAdapter(this, null);
		lstTask.setAdapter(mAdpater);
		lstTask.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				isPullRefresh = true;
				getData();
			}
		});

		LayoutBottom = (LinearLayout) findViewById(R.id.LayoutBottom);
		ShowButtom();

		Button btnConfirm = (Button) findViewById(R.id.btnConfirm);
		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SetTaskHandOver();
			}

		});
	}

	// 获取交接班重要事项
	private void GetTask() {
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("searchDate", mDate);
		
		if (Property.OwnStation != null)
			parmValues.put("stationCode", Property.OwnStation.Code);

		String methodPath = Constant.MP_SHIFT;
		String methodName = Constant.MN_GET_HANDOVER;
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
			ArrayList<ShiftTask> list = new ArrayList<ShiftTask>();
			BFlag = JsonUtil.GetJsonBoolean(result, "ManageFlag");
			HId = JsonUtil.GetJsonString(result, "HId");

			JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
			if (jsonArray != null && jsonArray.length() != 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
					ShiftTask task = new ShiftTask();
					task.Level = JsonUtil.GetJsonObjIntValue(jsonObj,
							"Priority");
					task.Publisher = JsonUtil.GetJsonObjStringValue(jsonObj,
							"CreaterName");
					task.PublishDep = JsonUtil.GetJsonObjStringValue(jsonObj,
							"CreaterDeptName");
					task.Content = JsonUtil.GetJsonObjStringValue(jsonObj,
							"Context");
					list.add(task);
				}
			}
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
				GetTask();
			}
		}.start();
	}

	// 接收交接班重要事项
	private void AcceptTask() {
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("hId", HId);
		if (Property.OwnStation != null)
			parmValues.put("stationCode", Property.OwnStation.Code);

		String methodPath = Constant.MP_SHIFT;
		String methodName = Constant.MN_SET_HANDOVER;
		WebServiceManager webServiceManager = new WebServiceManager(
				getApplicationContext(), methodName, parmValues);
		String result = webServiceManager.OpenConnect(methodPath);

		if (result == null || result.equals("")) {
			errMsg = getString(R.string.Shift_Task_SetFail);
			myhandle.sendEmptyMessage(MSG_SETDATA_FAIL);
			return;
		}

		int Code = JsonUtil.GetJsonInt(result, "Code");
		switch (Code) {
		case Constant.NORMAL:
			myhandle.sendEmptyMessage(MSG_SETDATA_SUCCEED);
			break;
		case Constant.EXCEPTION:
		default:
			errMsg = JsonUtil.GetJsonString(result, "Msg");
			myhandle.sendEmptyMessage(MSG_SETDATA_FAIL);
			break;
		}
	}

	// 设置接收
	private void SetTaskHandOver() {
		showProgressDialog(R.string.Save);
		new Thread() {
			public void run() {
				AcceptTask();
			}
		}.start();
	}

	// 设置是否显示“确认”按钮
	private void ShowButtom() {
		if (BFlag)
			LayoutBottom.setVisibility(View.VISIBLE);
		else
			LayoutBottom.setVisibility(View.GONE);
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
