package easyway.Mobile.Watering;

import java.util.ArrayList;
import java.util.HashMap;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.CommonUtils;
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
import android.widget.TextView;

/*
 * 上水-未完成
 */
public class WateringProcessingActivity extends ActivityEx {
	private WateringAdapter mAdpater;
	private boolean isPullRefresh = false;
	private PullRefreshListView list;
	private ArrayList<Watering> mlist;

	private String mDate = "";
	private DateChangeReceiver receiver;

	private Button btnSwitch;
	private final int SHOW_ALL = 1;
	private final int SHOW_NOW = 2;
	private int showflag = SHOW_NOW;
	
	private final int MSG_GETDATA_FAIL = 0;
	private final int MSG_GETDATA_SUCCEED = 1;
	private final int MSG_DATE_CHANGE = 2;
	private final int MSG_SETDATA_FAIL = 3;
	private final int MSG_SETDATA_SUCCEED = 4;

	@SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			closeProgressDialog();
			if (isPullRefresh) {
				list.onRefreshComplete();
				isPullRefresh = false;
			}

			switch (msg.what) {
			case MSG_GETDATA_FAIL:
				showToast(errMsg);

				mAdpater.setData(null);
				mAdpater.notifyDataSetChanged();
				break;
			case MSG_GETDATA_SUCCEED:
				mlist = (ArrayList<Watering>) msg.obj;

				mAdpater.setData(mlist);
				mAdpater.notifyDataSetChanged();
				break;
			case MSG_DATE_CHANGE:
				mDate = WateringTabActivity.mDate;
				SetFlag();
				getData();
				break;
			case MSG_SETDATA_FAIL:
				showToast(errMsg);
				break;
			case MSG_SETDATA_SUCCEED:
				int index = (Integer) msg.obj;
				if (mlist == null)
					return;
				
				if (index < mlist.size()) {
					switch (mlist.get(index).AppStatus) {
					case Watering.STATUS_NOT_REPUEST: // 未申请
						mlist.get(index).AppStatus = Watering.STATUS_APPROVALING;
						break;
					case Watering.STATUS_APPROVAL_PASS: // 审核通过
						mlist.remove(index);
						break;
					}
					
					mAdpater.setData(mlist);
					mAdpater.notifyDataSetChanged();
				}
				break;
			default:
				break;
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.water_completed);

		initView();
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mDate == null || mDate.equals("")) {
			mDate = WateringTabActivity.mDate;
			SetFlag();
			getData();
		} else if (!mDate.equals(WateringTabActivity.mDate)) {
			mDate = WateringTabActivity.mDate;
			SetFlag();
			getData();
		}

		regReceiver();
	}

	private void SetFlag() {
		if (CommonUtils.isToday(mDate)) {
			showflag = SHOW_NOW;
			btnSwitch.setText(R.string.task_show_all);
			btnSwitch.setEnabled(true);
		} else {
			showflag = SHOW_ALL;
			btnSwitch.setText(R.string.task_show_open);
			btnSwitch.setEnabled(false);
		}
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
		list = (PullRefreshListView) findViewById(R.id.list);
		mAdpater = new WateringAdapter(this, null);
		mAdpater.setIWatering(new IWatering() {

			@Override
			public void ItemClicked(int index) {
				if (mlist == null)
					return;

				if (index < mlist.size()) {
					switch (mlist.get(index).AppStatus) {
					case Watering.STATUS_NOT_REPUEST: // 未申请
					case Watering.STATUS_APPROVAL_PASS: // 审核通过
						setData(index);
						break;
					}
				}
			}
		});

		list.setAdapter(mAdpater);
		list.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				isPullRefresh = true;
				getData();
			}
		});

		TextView txtTrainNo = (TextView) findViewById(R.id.txtTrainNo);
		TextView txtTrack = (TextView) findViewById(R.id.txtTrack);
		TextView txtArriveTime = (TextView) findViewById(R.id.txtArriveTime);
		TextView txtLeaveTime = (TextView) findViewById(R.id.txtLeaveTime);
		TextView txtControl = (TextView) findViewById(R.id.txtControl);

		txtTrainNo.setWidth(Property.screenwidth / 6);
		txtTrack.setWidth(Property.screenwidth / 6);
		txtArriveTime.setWidth(Property.screenwidth / 6);
		txtLeaveTime.setWidth(Property.screenwidth / 6);
		txtControl.setWidth(Property.screenwidth / 3);

		txtTrainNo.setText(R.string.Watering_TrainNo);
		txtTrack.setText(R.string.Watering_Track);
		txtArriveTime.setText(R.string.Watering_Arrive);
		txtLeaveTime.setText(R.string.Watering_Leave);
		txtControl.setText(R.string.Watering_Control);

		txtTrainNo.getPaint().setFakeBoldText(true);
		txtTrack.getPaint().setFakeBoldText(true);
		txtArriveTime.getPaint().setFakeBoldText(true);
		txtLeaveTime.getPaint().setFakeBoldText(true);
		txtControl.getPaint().setFakeBoldText(true);
		
		btnSwitch = (Button) findViewById(R.id.btnSwitch);
		btnSwitch.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if (showflag == SHOW_NOW) {		// 显示当前
					showflag = SHOW_ALL;
					btnSwitch.setText(R.string.task_show_open);
				} else {		// 显示全部
					showflag = SHOW_NOW;
					btnSwitch.setText(R.string.task_show_all);
				}
					
				getData();
			}
		});
	}

	// 获取未完成上水任务
	private void GetProcessing() {
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("flag", String.valueOf(Watering.FLAG_NOTCOMPLETE));
		parmValues.put("beginDateTime", mDate + " " + "00:00:00");
		parmValues.put("endDateTime", mDate + " " + "23:59:59");
		if (Property.OwnStation != null)
			parmValues.put("stationCode", Property.OwnStation.Code);

		String methodPath = Constant.MP_TASK;
		String methodName = Constant.MN_GET_WATERTASK;
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
			ArrayList<Watering> list = Watering.ParseFromString(result);

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
				GetProcessing();
			}
		}.start();
	}

	// 申请/完成
	private void Process(int index) {
		Watering water = mlist.get(index);
		
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("saId", String.valueOf(water.SaId));
		int ProcessStauts = Watering.PROCESS_REQUEST;
		switch (water.AppStatus) {
		case Watering.STATUS_NOT_REPUEST: // 未申请
			ProcessStauts = Watering.PROCESS_REQUEST;
			break;
		case Watering.STATUS_APPROVAL_PASS: // 审核通过
			ProcessStauts = Watering.PROCESS_COMPLETE;
			break;
		}
		parmValues.put("appStatus", String.valueOf(ProcessStauts));
		
		String methodPath = Constant.MP_TASK;
		String methodName = Constant.MN_APPLYWATER;
		WebServiceManager webServiceManager = new WebServiceManager(
				getApplicationContext(), methodName, parmValues);
		String result = webServiceManager.OpenConnect(methodPath);

		if (result == null || result.equals("")) {
			errMsg = getString(R.string.Watering_ExpProcessing);
			myhandle.sendEmptyMessage(MSG_SETDATA_FAIL);
			return;
		}

		int Code = JsonUtil.GetJsonInt(result, "Code");
		switch (Code) {
		case Constant.NORMAL:
			Message message = new Message();
			message.what = MSG_SETDATA_SUCCEED;
			message.obj = index;
			myhandle.sendMessage(message);
			break;
		case Constant.EXCEPTION:
		default:
			errMsg = JsonUtil.GetJsonString(result, "Msg");
			myhandle.sendEmptyMessage(MSG_SETDATA_FAIL);
			break;
		}
	}

	// 获取数据
	private void setData(final int index) {
		showProgressDialog(R.string.SavingData);
		new Thread() {
			public void run() {
				Process(index);
			}
		}.start();
	}

	private void regReceiver() {
		receiver = new DateChangeReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(WateringTabActivity.ACTION_DATE_CHANGE);
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
