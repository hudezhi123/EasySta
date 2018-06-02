package easyway.Mobile.site_monitoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.TaskSMWC;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.HomeKey;
import easyway.Mobile.util.JsonUtil;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

/*
 * 现场监控
 */
public class SMTabActivity extends TabActivity implements
		TabHost.OnTabChangeListener {
	public final static String EXTRA_ID = "ID";

	private TabHost mTabHost; // TabActivity的TabHost对象
	private TextView station;
	public static String mStationCode = "";

	private Map<String, ArrayList<TaskSMWC>> mAllSMWCList; // 所有站
	private ArrayList<TaskSMWC> mCurrentSMWCList; // 当前站

	private final int MSG_GET_SMWC = 1;
	private final int MSG_CHANGE_STATION = 2;

	@SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
		@Override
		public void handleMessage(Message message) {
			switch (message.what) {
			case MSG_CHANGE_STATION:
				mTabHost.clearAllTabs();
				getSMWC();
				break;
			case MSG_GET_SMWC:
				if (mCurrentSMWCList == null || mCurrentSMWCList.size() == 0) {
					Toast.makeText(SMTabActivity.this,
							R.string.notifynotaskworkspacecategory,
							Toast.LENGTH_LONG).show();
				} else {
					int num = mCurrentSMWCList.size();
					for (int i = 0; i < num; i++) {
						addTab(i, num, mCurrentSMWCList.get(i));
					}
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (HomeKey.work && !HomeKey.disableHome) {
			getWindow().addFlags(HomeKey.FLAG_HOMEKEY_DISPATCHED);
		}
		super.onCreate(savedInstanceState);

		getCurrentFocus();
		setContentView(R.layout.site_tab);
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.title_sitemonitor);

		station = (TextView) findViewById(R.id.station);
		if (Property.OwnStation != null)
			mStationCode = Property.OwnStation.Code;

		if (Property.OwnStation != null)
			station.setText("(" + Property.OwnStation.Name + ")");
		station.setVisibility(View.VISIBLE);
		station.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Property.ChargeStation == null)
					return;

				if (Property.ChargeStation.size() == 0)
					return;

				showSelectStaionDlg();
			}

		});

		findViewById(R.id.btnReturn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		Button btnset = (Button) findViewById(R.id.btnset);
		btnset.setText(R.string.exception);
		btnset.setVisibility(View.VISIBLE);
		btnset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SMTabActivity.this,
						SMExceptionTaskActivity.class);
				startActivity(intent);
			}
		});

		mTabHost = getTabHost();
		mTabHost.setup();
		mTabHost.setOnTabChangedListener(this);

		// 获取任务区域类型列表
		getSMWC();
	}

	private void getSMWC() {
		new Thread() {
			public void run() {
				mCurrentSMWCList = getSMWCfromLocal();
				if (mCurrentSMWCList == null || mCurrentSMWCList.size() == 0) {
					mCurrentSMWCList = getSMWCfromServer();
					if (mCurrentSMWCList != null) {
						if (mAllSMWCList == null) {
							mAllSMWCList = new HashMap<String, ArrayList<TaskSMWC>>();
							mAllSMWCList.put(mStationCode, mCurrentSMWCList);
						} else {
							if (!mAllSMWCList.containsKey(mStationCode))
								mAllSMWCList
										.put(mStationCode, mCurrentSMWCList);
						}
					}
				}

				myhandle.sendEmptyMessage(MSG_GET_SMWC);
			}
		}.start();
	}

	// 从本地获取任务区域类型列表
	private ArrayList<TaskSMWC> getSMWCfromLocal() {
		if (mAllSMWCList == null)
			return null;

		return mAllSMWCList.get(mStationCode);
	}

	// 从服务器获取任务区域类型列表
	private ArrayList<TaskSMWC> getSMWCfromServer() {
		ArrayList<TaskSMWC> list = new ArrayList<TaskSMWC>();
		HashMap<String, String> paramValues = new HashMap<String, String>();
		paramValues.put("sessionId", Property.SessionId);
		paramValues.put("stationCode", mStationCode);
		String methodPath = Constant.MP_TASK;
		String methodName = Constant.MN_GET_MONITOR;

		WebServiceManager webServiceManager = new WebServiceManager(
				SMTabActivity.this, methodName, paramValues);
		String result = webServiceManager.OpenConnect(methodPath);

		if (result == null || result.equals("")) {
			return null;
		}

		int Code = JsonUtil.GetJsonInt(result, "Code");
		switch (Code) {
		case Constant.NORMAL:
			list = TaskSMWC.ParseFromString(result);
			break;
		case Constant.EXCEPTION:
		default:
			break;
		}

		return list;
	}

	public void addTab(int index, int total, TaskSMWC item) {
		if (item == null)
			return;
		Intent intent = null;
		if (item.MDisplayStyle == 2) {		// 一行显示两个任务区域
			intent = new Intent(this, SMChildCatgOneActivity.class);
		} else if (item.MDisplayStyle == 1){		// 一行显示一个任务区域，该任务区域显示当前任务及下一任务
			intent = new Intent(this, SMChildCatgTwoActivity.class);
		} else {
			intent = new Intent(this, SMChildCatgOneActivity.class);

		}

		intent.putExtra(EXTRA_ID, item.MId);

		TextView txt = new TextView(this);
		txt.setText(item.MName);
		txt.setTextSize(20);
		txt.setGravity(Gravity.CENTER);
		txt.setTextColor(Color.BLACK);
		if (total == 1) {
			txt.setBackgroundResource(R.drawable.btn_tab_middle_selector);
		} else if (total == 2) {
			if (index == 0) {
				txt.setBackgroundResource(R.drawable.btn_tab_left_selector);
			} else {
				txt.setBackgroundResource(R.drawable.btn_tab_right_selector);
			}
		} else if (total > 2) {
			if (index == 0) {
				txt.setBackgroundResource(R.drawable.btn_tab_left_selector);
			} else if (index == total - 1) {
				txt.setBackgroundResource(R.drawable.btn_tab_right_selector);
			} else {
				txt.setBackgroundResource(R.drawable.btn_tab_middle_selector);
			}
		}

		TabSpec spec = mTabHost.newTabSpec(item.MName);
		spec.setIndicator(txt);
		spec.setContent(intent);
		mTabHost.addTab(spec);
	}

	@Override
	public void onTabChanged(String arg0) {
		// do nothing
	}

	// 选择所属站
	private void showSelectStaionDlg() {
		if (null != Property.ChargeStation) {
			String[] m = new String[Property.ChargeStation.size()];
			for (int i = 0; i < Property.ChargeStation.size(); i++) {
				m[i] = Property.ChargeStation.get(i).Name;
			}

			AlertDialog dlg = new AlertDialog.Builder(SMTabActivity.this)
					.setTitle("")
					.setItems(m, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							if (item < Property.ChargeStation.size()) {
								mStationCode = Property.ChargeStation.get(item).Code;
								station.setText("("
										+ Property.ChargeStation.get(item).Name
										+ ")");

								myhandle.sendEmptyMessage(MSG_CHANGE_STATION);
							}
						}
					}).create();
			dlg.show();
		}
	};

}
