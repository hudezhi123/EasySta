package easyway.Mobile.LightingControl;

import java.util.ArrayList;

import easyway.Mobile.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LightingControlCustumActivity extends Activity {
	private ProgressDialog mProDialog; // 进度显示框
	private Button btn_now, btn_default;
	private ListView lv_light;
	private CustumAdapter custumAdapter;
	ArrayList<LightingControl> list = new ArrayList<LightingControl>();
	int lampId;

	private final int MSG_GETLAMPSTATUS_SUCCEED = 3;
	private final int MSG_NO_DATA = 1;
	private final int MSG_LAMPPOINTS_FAIL = 4;
	private final int MSG_LAMPPOINTS_SUCCEED = 5;
	private Handler myhandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			closeProgressDialog();
			switch (msg.what) {

			case MSG_NO_DATA:

				Toast.makeText(LightingControlCustumActivity.this,
						R.string.Lighting_Control_NO_DATA, Toast.LENGTH_SHORT)
						.show();

				break;
			case MSG_GETLAMPSTATUS_SUCCEED:
				if (list != null && list.size() > 0) {
					custumAdapter = new CustumAdapter(
							LightingControlCustumActivity.this, list);
					lv_light.setAdapter(custumAdapter);
				}
				break;
			case MSG_LAMPPOINTS_FAIL:
				Toast.makeText(LightingControlCustumActivity.this,
						R.string.Lighting_Control_FAIL, Toast.LENGTH_SHORT)
						.show();
				break;
			case MSG_LAMPPOINTS_SUCCEED:
				Toast.makeText(LightingControlCustumActivity.this,
						R.string.Lighting_Control_SUCCEED, Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lighting_control_custum);
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.lighting_control_title);
		Button btnReturn = (Button) findViewById(R.id.btnReturn);
		lampId = getIntent().getIntExtra("lampId", -1);
		btnReturn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		init();

		GetLampPointsData();

	}

	private void GetLampPointsData() {
		showProgressDialog(R.string.GettingData);
		new Thread() {
			public void run() {
				list = LightingControl.GetLampPoints(
						LightingControlCustumActivity.this, lampId + "");

				if (list == null || list.size() == 0) {

					myhandle.sendEmptyMessage(MSG_NO_DATA);
					return;
				}

				myhandle.sendEmptyMessage(MSG_GETLAMPSTATUS_SUCCEED);
			}
		}.start();
	}

	private void init() {
		btn_default = (Button) findViewById(R.id.btn_default);
		btn_now = (Button) findViewById(R.id.btn_now);
		lv_light = (ListView) findViewById(R.id.lv_light);

		custumAdapter = new CustumAdapter(LightingControlCustumActivity.this,
				list);
		btn_now.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder builder = new Builder(
						LightingControlCustumActivity.this);
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				builder.setCancelable(false);
				builder.setTitle(R.string.alert_dialog_reset_value_title);
				builder.setPositiveButton(R.string.alert_dialog_ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								SetLampPointsData(1 + "");
							}
						});
				builder.setNeutralButton(R.string.alert_dialog_cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						});

				builder.create().show();

			}

		});
		btn_default.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(
						LightingControlCustumActivity.this);
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				builder.setCancelable(false);
				builder.setTitle(R.string.alert_dialog_reset_value_title);
				builder.setPositiveButton(R.string.alert_dialog_ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								SetLampPointsData(0 + "");
							}
						});
				builder.setNeutralButton(R.string.alert_dialog_cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						});

				builder.create().show();

			}

		});

	}

	private void SetLampPointsData(final String num) {
		showProgressDialog(R.string.GettingData);
		new Thread() {
			public void run() {
				if (null == custumAdapter.getRememberlist()) {
					myhandle.sendEmptyMessage(MSG_NO_DATA);
					return;
				}
				ArrayList<LightingControl> rememberlist = custumAdapter
						.getRememberlist();
				if (rememberlist == null || rememberlist.size() == 0) {
					myhandle.sendEmptyMessage(MSG_NO_DATA);
					return;
				}

				StringBuffer str = new StringBuffer();
				for (int i = 0; i < rememberlist.size(); i++) {
					str.append(rememberlist.get(i).id + ","
							+ rememberlist.get(i).DefaultValue);
					if (i < rememberlist.size() - 1) {
						str.append("|");
					}
				}
				Log.e("345436", str.toString());

				if (!LightingControl
						.SetLampPoints(LightingControlCustumActivity.this, num,
								str.toString())) {
					myhandle.sendEmptyMessage(MSG_LAMPPOINTS_FAIL);
					return;
				}

				if (num.equals("1")) {
					if (!LightingControl.SetLampStatus(
							LightingControlCustumActivity.this, lampId + "",
							4 + "")) {
						myhandle.sendEmptyMessage(MSG_LAMPPOINTS_FAIL);
						return;
					}
				}

				myhandle.sendEmptyMessage(MSG_LAMPPOINTS_SUCCEED);
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
