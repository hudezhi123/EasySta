package easyway.Mobile.Shift;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.TB_Shift_Out_Train;
import easyway.Mobile.Login.LoginFrame;
import easyway.Mobile.Media.ExtAudioRecorder;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.CommonFunc;
import easyway.Mobile.util.IntercomCtrl;
import easyway.Mobile.util.JsonUtil;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class Shift_In_Train extends ActivityEx implements OnClickListener {
	private static final String TAG = "Shift_In_Train";
	private Handler handel = new Handler() {
		public void handleMessage(Message msg) {
			closeProgressDialog();
			switch (msg.what) {
			case 5:
				int i = (Integer) msg.obj;
				if (i <= 100) {
					mMicImageView.setBackgroundResource(R.drawable.mic_one);
				} else if (100 < i && i <= 200) {
					mMicImageView.setBackgroundResource(R.drawable.mic_two);
				} else if (200 < i && i <= 300) {
					mMicImageView.setBackgroundResource(R.drawable.mic_three);
				} else if (300 < i && i <= 500) {
					mMicImageView.setBackgroundResource(R.drawable.mic_four);
				}
				break;
			default:
				break;
			}
		}
	};
	private ProgressDialog progDialog;
	private ArrayList<TB_Shift_Out_Train> trainList = new ArrayList<TB_Shift_Out_Train>();
	private int startIndex = 0;
	private int limit = 10;

	private String funcName = "";
	private long teamId = 0;
	private long shift_out_Id = 0;
	private String shiftOutVoice = "";
	private String shiftInVoice = "";
	private String filePath = "";

	private boolean isRecording = false;
	private ExtAudioRecorder extAudioRecorder;
	private MediaPlayer mediaplayer = new MediaPlayer();
	private Button btnRecordShiftIn;
	private Button btnUploadShiftInVoice;

	/**
	 * 按下录音提示窗口
	 * */
	private PopupWindow mSoundRecorderWindow;

	/**
	 * 录制音频时麦克风的Image
	 */
	private ImageView mMicImageView;

	/**
	 * 判断是否停止线程
	 */
	private boolean mProgressBarEable = true;

	/**
	 * 获取当前音量
	 */
	private int mCurrentVoice;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shift_in_train_list);

		TextView labTitle = (TextView) findViewById(R.id.title);
		labTitle.setText(R.string.Shift_In);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		shift_out_Id = bundle.getLong("Shift_Id");

		ShowAllTask4Shift();
		Button btnShiftIn = (Button) findViewById(R.id.btnShiftIn);
		// btnShiftIn.setOnClickListener(postShiftInLis());
		btnShiftIn.setOnClickListener(this);

		btnRecordShiftIn = (Button) findViewById(R.id.btnRecordShiftIn);

		// btnRecordShiftIn.setOnClickListener(recordShiftOutLis());
		// btnRecordShiftIn.setOnTouchListener(recordLis());
		btnRecordShiftIn.setOnClickListener(this);

		btnUploadShiftInVoice = (Button) findViewById(R.id.btnUploadShiftInVoice);
		// btnUploadShiftInVoice.setOnClickListener(uploadVoice());
		btnUploadShiftInVoice.setOnClickListener(this);

		mediaplayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {
				IntercomCtrl.open_intercom(Shift_In_Train.this);
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mediaplayer != null) {
			if (mediaplayer.isPlaying()) {
				mediaplayer.stop();
				mediaplayer.release();
			}

		}

		IntercomCtrl.open_intercom(Shift_In_Train.this);
	}

	private void UploadVoice() {
		if (filePath.equals("")) {
			return;
		}
		if (shift_out_Id == 0) {
			return;
		}
		if (teamId == 0) {
			return;
		}
		Shift_Out_Upload_Voice_Task shiftOutDetailPostTask = new Shift_Out_Upload_Voice_Task(
				Shift_In_Train.this, filePath, shift_out_Id, teamId, false);
		shiftOutDetailPostTask.execute();
	}

	private void playVoice(String path) {
		if (extAudioRecorder != null) {
			try {
				stopRecord();
			} catch (Exception ex) {

			}
		}

		if (!path.equals("")) {
			try {
				IntercomCtrl.close_intercom(Shift_In_Train.this);
				mediaplayer.reset();
				mediaplayer.setDataSource(path);
				mediaplayer.prepare();
				mediaplayer.start();
			} catch (Exception ex) {
				Builder builder = new AlertDialog.Builder(Shift_In_Train.this);
				builder.setTitle(R.string.Shift_Out);
				builder.setPositiveButton(R.string.OK, null);
				builder.setMessage(R.string.exp_play_voice);
				builder.show();
			}
		}
	}

	public void setPlayLis() {
		Button btnPlayShiftVoice = (Button) findViewById(R.id.btnPlayShiftVoice);
		if (shiftOutVoice.equals("") && shiftInVoice.equals("")) {
			btnPlayShiftVoice.setEnabled(false);
		} else {
			btnPlayShiftVoice.setEnabled(true);

			btnPlayShiftVoice.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					stopRecord();
					Builder builder = new AlertDialog.Builder(
							Shift_In_Train.this);
					builder.setIcon(android.R.drawable.ic_dialog_info);
					builder.setTitle(R.string.Shift_PlaySound);
					if (!shiftOutVoice.equals("") && !shiftInVoice.equals("")) {
						builder.setItems(new String[] {
								getString(R.string.Shift_In_PlayShiftIn),
								getString(R.string.Shift_In_PlayShiftOut) },
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										if (which == 0) {
											playVoice(shiftInVoice);
										} else {
											playVoice(shiftOutVoice);
										}
										dialog.dismiss();

									}

								});
					} else if (!shiftOutVoice.equals("")) {
						builder.setItems(
								new String[] { getString(R.string.Shift_In_PlayShiftOut) },
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										playVoice(shiftOutVoice);
										dialog.dismiss();

									}

								});
					} else {
						builder.setItems(
								new String[] { getString(R.string.Shift_In_PlayShiftIn) },
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										playVoice(shiftInVoice);
										dialog.dismiss();

									}

								});
					}

					builder.show();

				}
			});

		}
	}

	private void getShift() {

		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("shiftId", "0");
		String methodPath = "WebService/Shift.asmx";
		String methodName = "GetShiftOut";
		WebServiceManager webServiceManager = new WebServiceManager(
				Shift_In_Train.this, methodName, parmValues);
		String result = "";
		try {
			result = webServiceManager.OpenConnect(methodPath);
		} catch (Exception ex) {
			return;
		}
		errMsg = JsonUtil.GetJsonString(result, "Msg");
		if (!errMsg.equals("")) {
			return;
		}
		if (result.equals("")) {
			return;
		}
		JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
		if (jsonArray.length() == 0) {
			return;
		} else {
			JSONObject jsonObj = (JSONObject) jsonArray.opt(0);

			shiftOutVoice = JsonUtil.GetJsonObjStringValue(jsonObj,
					"Shift_Out_Voice");
			shiftInVoice = JsonUtil.GetJsonObjStringValue(jsonObj,
					"Shift_In_Voice");
			if (!shiftOutVoice.equals("")) {
				shiftOutVoice = CommonFunc.GetServer(Shift_In_Train.this)
						+ shiftOutVoice.substring(1);
			}
			if (!shiftInVoice.equals("")) {
				shiftInVoice = CommonFunc.GetServer(Shift_In_Train.this)
						+ shiftInVoice.substring(1);
			}
		}

	}

	final Runnable mUpdateResults = new Runnable() {

		public void run() {
			if (progDialog != null) {
				progDialog.dismiss();
			}

			if (funcName.equals("ShowAllTask4Shift")) {
				ListView gvList = (ListView) findViewById(R.id.gvList);

				gvList.setAdapter(new Shift_In_TrainAdapter(
						Shift_In_Train.this, trainList, shift_out_Id));

				setPlayLis();
			}

			if (funcName.equals("ShiftIn")) {
				errMsg = getString(R.string.Shift_In_Success);
				Intent data = new Intent(Shift_In_Train.this,
						Shift_In_Pool.class);
				Bundle bundle = new Bundle();
				bundle.putLong("ShiftId", shift_out_Id);
				data.putExtras(bundle);
				setResult(200, data);
				showErrMsg(errMsg);
			}

		}

	};

	final Runnable mUpdateError = new Runnable() {
		public void run() {
			if (progDialog != null) {
				progDialog.dismiss();
			}

//			showErrMsg(errMsg);
			Intent go2Login = new Intent(Shift_In_Train.this,LoginFrame.class);
			startActivity(go2Login);
		}
	};

	private void ShiftIn() {
		progDialog = ProgressDialog.show(Shift_In_Train.this,
				getString(R.string.Waiting), getString(R.string.GettingData), true,
				false);
		progDialog.setIndeterminate(true);
		progDialog.setCancelable(true);
		progDialog.setIcon(R.drawable.waiting);
		funcName = "ShiftIn";
		Thread thread = new Thread(null, doBackgroundThreadProcessing,
				"getAllPlayRecords");
		thread.start();
	}

	private void ShowAllTask4Shift() {
		progDialog = ProgressDialog.show(Shift_In_Train.this,
				getString(R.string.Waiting), getString(R.string.GettingData), true,
				false);
		progDialog.setIndeterminate(true);
		progDialog.setCancelable(true);
		progDialog.setIcon(R.drawable.waiting);
		funcName = "ShowAllTask4Shift";

		Thread thread = new Thread(null, doBackgroundThreadProcessing,
				"getAllPlayRecords");
		thread.start();
	}

	private long getTeamId() {

		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		String methodPath = "WebService/spark.asmx";
		String methodName = "GetTeamId";
		WebServiceManager webServiceManager = new WebServiceManager(
				getApplicationContext(), methodName, parmValues);
		try {
			String result = webServiceManager.OpenConnect(methodPath);
			errMsg = JsonUtil.GetJsonString(result, "Msg");
			if (!errMsg.equals("")) {
				return 0;
			}
			teamId = JsonUtil.GetJsonLong(result, "Data");
			return teamId;
		} catch (Exception ex) {
			return 0;
		}

	}

	private Runnable doBackgroundThreadProcessing = new Runnable() {

		public void run() {
			String sessionId = Property.SessionId;
			if (sessionId.equals("")) {
				Log.d(TAG, "Session Id is null");
				errMsg = getString(R.string.lostLoginInfo);
				handel.post(mUpdateError);
				return;
			}

			Looper.prepare();

			try {
				if (funcName.equals("ShowAllTask4Shift")) {
					trainList = GetAllTask4Shift(startIndex, limit);
					teamId = getTeamId();
					getShift();
				}

				if (funcName.equals("ShiftIn")) {
					postShiftIn();
				}

				handel.post(mUpdateResults);
			} catch (Exception e) {
				errMsg = e.getMessage();
				Log.d(TAG, e.getMessage());
				handel.post(mUpdateError);
			}
			Looper.loop();
		}

	};

	private void postShiftIn() throws Exception {
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("shift_Id", String.valueOf(shift_out_Id));
		String methodPath = "WebService/Shift.asmx";
		String methodName = "SaveShiftIn";
		WebServiceManager webServiceManager = new WebServiceManager(
				getApplicationContext(), methodName, parmValues);
		String result = webServiceManager.OpenConnect(methodPath);
		errMsg = JsonUtil.GetJsonString(result, "Msg");
		if (!errMsg.equals("")) {
			throw new Exception(errMsg);
		}
	}

	private ArrayList<TB_Shift_Out_Train> GetAllTask4Shift(int startIndex,
			int limit) throws Exception {
		ArrayList<TB_Shift_Out_Train> list = new ArrayList<TB_Shift_Out_Train>();
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("shiftId", String.valueOf(shift_out_Id));
		String methodPath = "WebService/Shift.asmx";
		String methodName = "GetShiftOutTrainNums";
		WebServiceManager webServiceManager = new WebServiceManager(
				Shift_In_Train.this, methodName, parmValues);
		String result = webServiceManager.OpenConnect(methodPath);
		errMsg = JsonUtil.GetJsonString(result, "Msg");
		if (!errMsg.equals("")) {
			throw new Exception(errMsg);
		}
		JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
			TB_Shift_Out_Train shift_out_train = new TB_Shift_Out_Train();
			shift_out_train.Shift_Out_Train_Id = JsonUtil
					.GetJsonObjLongValue(jsonObj, "Shift_Out_Train_Id");
			shift_out_train.ShiftId = JsonUtil.GetJsonObjLongValue(
					jsonObj, "ShiftId");
			shift_out_train.TrainNum = JsonUtil.GetJsonObjStringValue(
					jsonObj, "TrainNum");
			shift_out_train.TeamId = JsonUtil.GetJsonObjLongValue(
					jsonObj, "TeamId");
			shift_out_train.AddDate = JsonUtil.GetJsonObjDateValue(
					jsonObj, "AddDate");
			shift_out_train.Shift_Detail_Id = JsonUtil
					.GetJsonObjLongValue(jsonObj, "Shift_Detail_Id");
			list.add(shift_out_train);
		}
		return list;
	}

	/**
	 * 初始化popupWindow上的控件
	 */
	private void initPopupWindow() {
		mMicImageView = (ImageView) mSoundRecorderWindow.getContentView()
				.findViewById(R.id.img_mic);
	}

	/**
	 * 音频子线程
	 */
	private Runnable mAudioPbRunnable = new Runnable() {
		@Override
		public void run() {
			while (mProgressBarEable) {
				try {
					mCurrentVoice = extAudioRecorder.getMaxAmplitude();
				} catch (IllegalStateException e) {
					mCurrentVoice = 0;
				} catch (Exception e) {
					mCurrentVoice = 0;
				}
				// 得到当前音量
				if (extAudioRecorder != null) {
					Message msg = new Message();
					msg.what = 5;
					msg.obj = mCurrentVoice / 100;
					handel.sendMessage(msg);
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
			}
		}
	};

	private void postShiftInList() {
		AlertDialog.Builder builder = new Builder(Shift_In_Train.this);
		builder.setIcon(R.drawable.information);
		builder.setMessage(R.string.Shift_In_Confirm);
		builder.setTitle(R.string.Prompt);
		builder.setPositiveButton(R.string.Shift_In_OK,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						ShiftIn();
					}
				});
		builder.setNegativeButton(R.string.Shift_In_Cancel, null);
		builder.create().show();
	}

	private void recordVoice() {
		mediaplayer.reset();
		// 弹出一个麦的View
		mSoundRecorderWindow = new PopupWindow(getLayoutInflater().inflate(
				R.layout.media_ability, null), LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		mSoundRecorderWindow.showAtLocation(findViewById(R.id.shift_in_layout),
				Gravity.CENTER, 0, 0);
		btnUploadShiftInVoice.setEnabled(true);
		initPopupWindow();
		if (!isRecording) {
			String fileDir = getString(R.string.Shift_Record_Path);
			Log.d(TAG, fileDir);
			filePath = fileDir + "ShiftIn.wav";

			File fDir = new File(fileDir);
			if (!fDir.exists()) {
				fDir.mkdirs();
			}
			shiftInVoice = filePath;
			File fileWav = new File(filePath);

			if (fileWav.exists()) {
				fileWav.delete();
				Log.w(TAG, "Delete File " + fileWav.getPath());
			}

			// extAudioRecorder = ExtAudioRecorder.getInstanse(false);
			extAudioRecorder = ExtAudioRecorder.getInstanse(true);

			extAudioRecorder.setOutputFile(filePath);
			IntercomCtrl.close_intercom(Shift_In_Train.this);
			try {
				extAudioRecorder.prepare();
				extAudioRecorder.start();
				isRecording = true;
				// 启动线程刷新音量变化
				new Thread(mAudioPbRunnable).start();
			} catch (Exception e) {
				Log.d(TAG, e.getMessage());
			}
		} else {
			stopRecord();
			isRecording = false;
		}
		setPlayLis();
	}

	private void uploadVoice() {
		AlertDialog.Builder builder = new Builder(Shift_In_Train.this);
		builder.setIcon(R.drawable.information);
		builder.setTitle(R.string.Shift_In_Upload_Voice);
		if (filePath.equals("")) {
			builder.setMessage(R.string.Shift_In_Record_Voice_First);
			builder.setPositiveButton(R.string.OK, null);
		} else {
			builder.setMessage(R.string.Shift_In_Upload_Confirm);
			builder.setPositiveButton(R.string.OK,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							UploadVoice();
						}
					});
		}
		builder.create().show();
	}

	private void stopRecord() {
		// dissmiss 麦View
		if (mSoundRecorderWindow != null) {
			IntercomCtrl.open_intercom(Shift_In_Train.this);
			extAudioRecorder.stop();
			extAudioRecorder.release();
			mSoundRecorderWindow.dismiss();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnShiftIn:
			stopRecord();
			postShiftInList();
			break;
		case R.id.btnRecordShiftIn:
			if (mSoundRecorderWindow != null
					&& mSoundRecorderWindow.isShowing()) {
				stopRecord();
			} else {
				recordVoice();
			}
			break;
		case R.id.btnUploadShiftInVoice:
			stopRecord();
			uploadVoice();
			break;
		default:
			break;
		}

	}
}
