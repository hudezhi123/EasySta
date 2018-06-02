package easyway.Mobile.LightingControl;

import java.util.ArrayList;

import easyway.Mobile.R;
import easyway.Mobile.Data.LampArea;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class LightingControlActivity extends TabActivity implements
		TabHost.TabContentFactory {
	protected String errMsg = "";
	private ProgressDialog mProDialog; // 进度显示框
	private LightingControlAdapter mAdpater;
	private ArrayList<LampArea> mAreaNamelist;
	private ArrayList<LightingControl> mlist = new ArrayList<LightingControl>();;
	private Context mContext;
	private final int MSG_GETLAMPAREA_FAIL = 0;
	private final int MSG_GETLAMPAREA_SUCCEED = 1;
	private final int MSG_GETLAMPSTATUS_FAIL = 2;
	private final int MSG_GETLAMPSTATUS_SUCCEED = 3;
	private final int MSG_SETLAMPSTATUS_FAIL = 4;
	private final int MSG_SETLAMPSTATUS_SUCCEED = 5;
	private static String tabItem;

	private Handler myhandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			closeProgressDialog();
			switch (msg.what) {
			case MSG_GETLAMPSTATUS_FAIL:
				if (mlist != null && mlist.size() > 0) {
					// mAdpater.setData(null);
					// mAdpater.notifyDataSetChanged();
					mAdpater.setData(mlist);
					mAdpater.notifyDataSetChanged();
				} else {
					Toast.makeText(LightingControlActivity.this, "获取数据失败",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case MSG_GETLAMPSTATUS_SUCCEED:
				if (mlist != null && mlist.size() > 0) {
					mAdpater.setData(mlist);
					mAdpater.notifyDataSetChanged();

				}
				break;
			case MSG_GETLAMPAREA_FAIL:
				break;
			case MSG_GETLAMPAREA_SUCCEED:

				initView(mAreaNamelist);
				break;
			case MSG_SETLAMPSTATUS_FAIL:
				break;
			case MSG_SETLAMPSTATUS_SUCCEED:

				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lighting_control_tab);
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.lighting_control_title);

		Button btnReturn = (Button) findViewById(R.id.btnReturn);
		btnReturn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mContext = this;
		GetAllLampArea();
		//getStatusData(tabItem);

		
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (null != tabItem) {
			getStatusData(tabItem);
			// GetAllLampStatus(tabItem);
		}

	}

	// @Override
	// protected void onResume() {
	// // TODO Auto-generated method stub
	// super.onResume();
	//
	// getStatusData(tabItem);
	// // GetAllLampStatus(tabItem);
	// }

	private void initView(ArrayList<LampArea> list) {
		final TabHost tabHost = getTabHost();
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				tabItem = tabId;
				getStatusData(tabId);
			}
		});
		for (int i = 0; i < list.size(); i++) {

			int total = list.size();
			TextView txt = new TextView(this);
			txt.setText(list.get(i).AreaName);
			txt.setTextSize(20);
			txt.setGravity(Gravity.CENTER);
			txt.setTextColor(Color.BLACK);
			if (total == 1) {
				txt.setBackgroundResource(R.drawable.btn_tab_middle_selector);
			} else if (total == 2) {
				if (i == 0) {
					txt.setBackgroundResource(R.drawable.btn_tab_left_selector);
				} else {
					txt.setBackgroundResource(R.drawable.btn_tab_right_selector);
				}
			} else if (total > 2) {
				if (i == 0) {
					txt.setBackgroundResource(R.drawable.btn_tab_left_selector);
				} else if (i == total - 1) {
					txt.setBackgroundResource(R.drawable.btn_tab_right_selector);
				} else {
					txt.setBackgroundResource(R.drawable.btn_tab_middle_selector);
				}
			}
			txt.setLayoutParams(new LayoutParams(getWindowManager()
					.getDefaultDisplay().getWidth() / 3,
					LayoutParams.WRAP_CONTENT));
			tabHost.addTab(tabHost.newTabSpec(String.valueOf(list.get(i).AreaName))
					.setIndicator(txt).setContent(this));
		}
	}

	@Override
	public View createTabContent(String tag) {
		ListView lv = new ListView(this);

		mAdpater = new LightingControlAdapter(mContext, mlist);
		mAdpater.setILightingControl(new ILightingControl() {

			@Override
			void onProgressChanged(final String lampId, final String value) {
				// TODO Auto-generated method stub
				new Thread() {
					public void run() {
						SetLampStatus(mContext, lampId, value);
					}
				}.start();
			}

		});

		lv.setAdapter(mAdpater);

		return lv;
	}

	// 设置回路数据
	private void SetLampStatus(Context ctx, String lampId, String setVal) {

		boolean result = LightingControl.SetLampStatus(ctx, lampId, setVal);

		if (result == false) {
			myhandle.sendEmptyMessage(MSG_SETLAMPSTATUS_FAIL);
			return;
		}

		myhandle.sendEmptyMessage(MSG_SETLAMPSTATUS_SUCCEED);
	}

	// 获取所有区域类型
	private void GetAllLampArea() {

		mAreaNamelist = LampArea.GetAllLampArea(mContext);

		if (mAreaNamelist == null || mAreaNamelist.size() == 0) {
			myhandle.sendEmptyMessage(MSG_GETLAMPAREA_FAIL);
			return;
		}

		myhandle.sendEmptyMessage(MSG_GETLAMPAREA_SUCCEED);
	}

	// 获取回路数据
	private void GetAllLampStatus(String tabId) {

		mlist = LightingControl.GetLampStatus(mContext, tabId);

		if (mlist == null || mlist.size() == 0) {
			myhandle.sendEmptyMessage(MSG_GETLAMPSTATUS_FAIL);
			return;
		}

		myhandle.sendEmptyMessage(MSG_GETLAMPSTATUS_SUCCEED);
	}

	// 获取所有区域数据
	private void getAreaData() {
		showProgressDialog(R.string.GettingData);
		new Thread() {
			public void run() {
				GetAllLampArea();
			}
		}.start();
	}

	// 获取数据
	private void getStatusData(final String tabId) {
		showProgressDialog(R.string.GettingData);
		new Thread() {
			public void run() {
				GetAllLampStatus(tabId);
			}
		}.start();
	}

	public void closeProgressDialog() {
		if (mProDialog != null) {
			mProDialog.dismiss();
			mProDialog = null;
		}
	}

	public void showProgressDialog(int msgResId) {
		showProgressDialog(getString(msgResId));
	}

	public void showProgressDialog(String message) {
		showProgressDialog(message, false);
	}

	public void showProgressDialog(String message, boolean flag) {
		if (mProDialog == null) {
			if (getParent() != null) {
				mProDialog = new ProgressDialog(getParent());
			} else {
				mProDialog = new ProgressDialog(this);
			}
		}

		if (!mProDialog.isShowing()) {
			mProDialog.dismiss();
			mProDialog.setMessage(message);
			mProDialog.setIndeterminate(false);
			mProDialog.setCancelable(flag);
			mProDialog.setIcon(R.drawable.waiting);
			mProDialog.show();
		} else {
			mProDialog.setMessage(message);
		}
	}
}