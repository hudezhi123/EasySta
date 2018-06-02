package easyway.Mobile.Shift;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Login.LoginFrame;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.CommonFunc;
import easyway.Mobile.util.IntercomCtrl;
import easyway.Mobile.util.JsonUtil;

public class Shift_In_Remark extends ActivityEx {
	private String tag = "Shift_Out_Remark", trainNo = "";
	private long shiftId = 0;

	private ProgressDialog progDialog;
	private Handler handel = new Handler();
	private String voicePath = "", descriptions = "";
	private MediaPlayer mediaplayer = new MediaPlayer();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shift_out_detail);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		trainNo = bundle.getString("TrainNo");
		shiftId = bundle.getLong("ShiftId");

		TextView labTitle = (TextView) findViewById(R.id.title);
		labTitle.setText(trainNo);

		Button btnShiftPost = (Button) findViewById(R.id.btnShiftPost);
		Button btnShiftRecordVoice = (Button) findViewById(R.id.btnShiftRecordVoice);
		Button btnPlayVoice = (Button) findViewById(R.id.btnPlayVoice);

		btnShiftPost.setVisibility(View.GONE);
		btnShiftRecordVoice.setVisibility(View.GONE);
		btnPlayVoice.setOnClickListener(palyVoice());

		if (shiftId > 0) {
			btnShiftPost.setVisibility(View.GONE);
			btnShiftPost.setEnabled(false);
			btnShiftRecordVoice.setVisibility(View.GONE);
			btnShiftRecordVoice.setEnabled(false);
		}

		ShowShiftOut();

		mediaplayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {
				IntercomCtrl.open_intercom(Shift_In_Remark.this);
			}
		});
	}

	private void ShowShiftOut() {
		progDialog = ProgressDialog.show(Shift_In_Remark.this,
				getString(R.string.Waiting), getString(R.string.GettingData), true,
				false);
		progDialog.setIndeterminate(true);
		progDialog.setCancelable(true);
		progDialog.setIcon(R.drawable.waiting);

		Thread thread = new Thread(null, doBackgroundThreadProcessing,
				"getAllPlayRecords");
		thread.start();
	}

	private OnClickListener palyVoice() {
		return new OnClickListener() {

			public void onClick(View v) {
				if (!voicePath.equals("")) {
					try {
						IntercomCtrl.close_intercom(Shift_In_Remark.this);
						mediaplayer.reset();
						mediaplayer.setDataSource(voicePath);
						mediaplayer.prepare();
						mediaplayer.start();
					} catch (Exception ex) {
						Builder builder = new AlertDialog.Builder(
								Shift_In_Remark.this);
						builder.setTitle(R.string.Shift_In);
						builder.setPositiveButton(R.string.OK, null);
						builder.setMessage(R.string.Shift_Out_Play_Sound_Exception);
						builder.show();
					}
				}
			}
		};
	}

	public void onDestroy() {
		super.onDestroy();
		if (mediaplayer != null) {
			if (mediaplayer.isPlaying()) {
				mediaplayer.stop();
			}
			mediaplayer.release();
			mediaplayer = null;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		IntercomCtrl.open_intercom(Shift_In_Remark.this);
	}

	private Runnable doBackgroundThreadProcessing = new Runnable() {

		public void run() {
			String sessionId = Property.SessionId;
			if (sessionId.equals("")) {
				Log.d(tag, "Session Id is null");
				errMsg = getString(R.string.lostLoginInfo);
				handel.post(mUpdateError);
				return;
			}

			Looper.prepare();

			try {
				GetDetail();
				handel.post(mUpdateResults);
			} catch (Exception e) {
				Log.d(tag, e.getMessage());
				handel.post(mUpdateError);
			}
			Looper.loop();
		}

	};

	private void GetDetail() throws Exception {
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("trainNo", trainNo);
		parmValues.put("shiftId", String.valueOf(shiftId));
		String methodPath = "WebService/Shift.asmx";
		String methodName = "GetShiftDetail";

		try {
			WebServiceManager webServiceManager = new WebServiceManager(
					getApplicationContext(), methodName, parmValues);
			String result = webServiceManager.OpenConnect(methodPath);
			errMsg = JsonUtil.GetJsonString(result, "Msg");
			if (!errMsg.equals("")) {
				handel.post(mUpdateError);
			}
			JSONArray jsonArray = JsonUtil
					.GetJsonArray(result, "Data");
			if (jsonArray.length() > 0) {
				JSONObject jsonObj = (JSONObject) jsonArray.opt(0);
				voicePath = JsonUtil.GetJsonObjStringValue(jsonObj,
						"VoicePath");
				descriptions = JsonUtil.GetJsonObjStringValue(jsonObj,
						"Descriptions");
			}
		} catch (Exception ex) {
			throw ex;
		}
	}

	final Runnable mUpdateResults = new Runnable() {

		public void run() {
			if (progDialog != null) {
				progDialog.dismiss();
			}

			TextView txRemark = (TextView) findViewById(R.id.txRemark);
			txRemark.setText(descriptions);

			if (!voicePath.equals("")) {
				try {
					IntercomCtrl.close_intercom(Shift_In_Remark.this);
					voicePath = voicePath.substring(1);
					voicePath = CommonFunc.GetServer(Shift_In_Remark.this)
							+ voicePath;
					mediaplayer.reset();
					mediaplayer.setDataSource(voicePath);
					Log.d("Shift_In_Remark", voicePath);
					mediaplayer.prepare();
					mediaplayer.start();
				} catch (Exception ex) {
					Builder builder = new AlertDialog.Builder(
							Shift_In_Remark.this);
					builder.setTitle(R.string.Shift_Out);
					builder.setPositiveButton(R.string.OK, null);
					builder.setMessage(R.string.Shift_Out_Play_Sound_Exception);
					builder.show();
				}
			}
		}

	};

	final Runnable mUpdateError = new Runnable() {
		public void run() {
			if (progDialog != null) {
				progDialog.dismiss();
			}

//			showErrMsg(errMsg);
			Intent go2Login = new Intent(Shift_In_Remark.this,LoginFrame.class);
			startActivity(go2Login);
		}
	};
}
