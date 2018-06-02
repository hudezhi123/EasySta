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
 * 上水-已完成
 */
public class WateringCompletedActivity extends ActivityEx {
	private WateringAdapter mAdpater;
	private boolean isPullRefresh = false;
	private PullRefreshListView list;

	private String mDate = "";
	private DateChangeReceiver receiver;
	
	private Button btnSwitch;
	private final int SHOW_ALL = 1;
	private final int SHOW_NOW = 2;
	private int showflag = SHOW_NOW;
	
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
				mAdpater.setData((ArrayList<Watering>) msg.obj);
				mAdpater.notifyDataSetChanged();
				
				if (btnSwitch.isEnabled()) {
					if (showflag == SHOW_NOW)
						btnSwitch.setText(R.string.task_show_all);
					else
						btnSwitch.setText(R.string.task_show_open);
				}
				break;
			case MSG_DATE_CHANGE:
				mDate = WateringTabActivity.mDate;
				SetFlag();
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
		txtControl.setText(R.string.Watering_CompleteTime);
		
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

	// 获取已完成上水任务
	private void GetCompleted() {
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("flag", String.valueOf(Watering.FLAG_COMPLETED));
		if (showflag == SHOW_ALL) {
			parmValues.put("beginDateTime", mDate + " " + "00:00:00");
			parmValues.put("endDateTime", mDate + " " + "23:59:59");
		}
		
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
				GetCompleted();
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
