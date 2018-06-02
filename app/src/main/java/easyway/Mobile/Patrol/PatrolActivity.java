package easyway.Mobile.Patrol;

import java.util.HashMap;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

/*
 * 客运巡检
 */
public class PatrolActivity extends ActivityEx {
	private long areaId;

	private final int MSG_CHECK_FAIL = 0; // 巡检失败
	private final int MSG_CHECK_SUCC = 1; // 巡检成功
	private final int MSG_AUTO_CLOSE = 2; // 自动关闭
	
	@SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			closeProgressDialog();

			switch (msg.what) {
			case MSG_CHECK_FAIL:
				ShowDialog(false);
				break;
			case MSG_CHECK_SUCC:
				ShowDialog(true);
				AutoFinish();
				break;
			case MSG_AUTO_CLOSE:
				finish();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patrol_check);

		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.Patrol_Title);
		
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			areaId = bundle.getLong("AreaId");
		}

		Patrol();
	}

	public void onPause() {
		super.onPause();
	}

	// 设置巡检
	private void Patrol() {
		showProgressDialog(R.string.Patrol_Checking);
		new Thread() {
			public void run() {
				CheckPatrol();
			}
		}.start();
	}

	// 设置巡检
	private void CheckPatrol() {
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("areaId", String.valueOf(areaId));
		String methodPath = Constant.MP_PATROL;
		String methodName = Constant.MN_CHECK_PATROL;

		WebServiceManager webServiceManager = new WebServiceManager(
				PatrolActivity.this, methodName, parmValues);

		String result = webServiceManager.OpenConnect(methodPath);
		if (result == null || result.equals("")) {
			errMsg = getString(R.string.Patrol_CheckFail);
			myhandle.sendEmptyMessage(MSG_CHECK_FAIL);
			return;
		}

		int Code = JsonUtil.GetJsonInt(result, "Code");
		switch (Code) {
		case Constant.NORMAL:
			myhandle.sendEmptyMessage(MSG_CHECK_SUCC);
			break;
		case Constant.EXCEPTION:
		default:
			errMsg = JsonUtil.GetJsonString(result, "Msg");
			myhandle.sendEmptyMessage(MSG_CHECK_FAIL);
			break;
		}
	}

	// 提示
	private void ShowDialog(boolean bSucc) {
		AlertDialog.Builder builder = new Builder(PatrolActivity.this);
		if (!bSucc)
			builder.setIcon(R.drawable.error);
		
		builder.setTitle(R.string.Prompt);
		builder.setCancelable(false);

		if (bSucc)
			builder.setMessage(R.string.Patrol_CheckSucc);
		else
			builder.setMessage(errMsg);
		
		// 关闭
		builder.setPositiveButton(R.string.OK,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						finish();
					}
				});

		builder.create().show();
	}
	
	// 自动关闭界面
	private void AutoFinish() {
		myhandle.sendEmptyMessageDelayed(MSG_AUTO_CLOSE, 5000);
	}
}
