package easyway.Mobile.Shift;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Media.ExtAudioRecorder;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.IntercomCtrl;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.StringUtil;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Shift_Out_two extends ActivityEx implements OnClickListener {
	private static final String TAG = "Shift_Out";
	private ArrayList<String> trainNumList = new ArrayList<String>();
	private ArrayList<String> tempList = new ArrayList<String>();
	private int startIndex = 0;
	private int limit = 10;
	private ListView mListView;
	private Shift_Out_Adapter shift_out_adapter;
	private long teamId = 0;
	private boolean isShiftIn = true;

	private String filePath = "";
	private boolean isRecording = false;
	private ExtAudioRecorder extAudioRecorder;
	private MediaPlayer mediaplayer = new MediaPlayer();
	private String voicePath = "";
	private long shiftId = 0;
	// 员工排班的标识号
	private long TssId;

	private Button btnRecordShiftOut;

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

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			closeProgressDialog();
			switch (msg.what) {
			case 0:
				showToast("Error");
				break;

			case 1:
				ArrayList<String> list = (ArrayList<String>) msg.obj;
				for (String trainNum : list) {
					tempList.add(trainNum);
				}
				shift_out_adapter.setData(list);
				shift_out_adapter.notifyDataSetChanged();
				break;

			case 3:
				showToast((String) msg.obj);
				break;

			case 4:
				showToast(getString(R.string.Shift_Out_Success));
				upLoadVoice();
				break;
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
			case 6:
				LinearLayout layShiftOut = (LinearLayout) findViewById(R.id.layShiftOut);
				if (isShiftIn) {
					layShiftOut.setVisibility(View.VISIBLE);
				} else {
					layShiftOut.setVisibility(View.GONE);
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
		setContentView(R.layout.shift_out);
		showProgressDialog(R.string.GettingData);
		
		initView();
		new Thread(new AllTask4ShiftThread()).start();
//		new Thread(){
//			@Override
//			public void run() {
//				voicePath = getVoicePath();
//				boolean temp = hasPermission();
//				isShiftIn = temp;
//				mHandler.sendEmptyMessage(6);
//				teamId = getTeamId();
//				super.run();
//			}
//		}.start();
		

		mediaplayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {
				IntercomCtrl.open_intercom(Shift_Out_two.this);
			}
		});
	}

	private void initView() {
		TextView labTitle = (TextView) findViewById(R.id.title);
		labTitle.setText(getString(R.string.Shift_Out));

		mListView = (ListView) findViewById(R.id.gvList);
		shift_out_adapter = new Shift_Out_Adapter(this, trainNumList, teamId);
		mListView.setAdapter(shift_out_adapter);

		Button btnShiftOut = (Button) findViewById(R.id.btnShiftOut);
		// btnShiftOut.setOnClickListener(postShiftOutLis());
		btnShiftOut.setOnClickListener(this);

//		btnRecordShiftOut = (Button) findViewById(R.id.btnRecordShiftOut);
		// btnRecordShiftOut.setOnClickListener(recordShiftOutLis());
//		btnRecordShiftOut.setOnClickListener(this);
		// btnRecordShiftOut.setOnTouchListener(recordLis());

//		Button btnPlayShiftOutVoice = (Button) findViewById(R.id.btnPlayShiftOutVoice);
		// btnPlayShiftOutVoice.setOnClickListener(palyVoice());
//		btnPlayShiftOutVoice.setOnClickListener(this);

		LinearLayout layShiftOut = (LinearLayout) findViewById(R.id.layShiftOut);
		if (isShiftIn) {
			layShiftOut.setVisibility(View.VISIBLE);
		} else {
			layShiftOut.setVisibility(View.GONE);
		}
	}

	private class AllTask4ShiftThread implements Runnable {
		// ArrayList<String> list = new ArrayList<String>();

		@Override
		public void run() {
			Log.d(TAG, "voicePath = " + voicePath);
			Log.d(TAG, "isShiftIn = " + isShiftIn);
			Log.d(TAG, "teamId = " + teamId);

			trainNumList = GetAllTask4Shift(startIndex, limit);

			if (null == trainNumList) {
				Message msg = new Message();
				msg.what = 0;
				mHandler.sendMessage(msg);
			} else {
				Message msg = new Message();
				msg.what = 1;
				msg.obj = trainNumList;
				mHandler.sendMessage(msg);
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		IntercomCtrl.open_intercom(Shift_Out_two.this);
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mediaplayer != null) {
			try {
				mediaplayer.reset();
			} catch (Exception ex) {

			}
		}

		if (extAudioRecorder != null) {
			try {
				stopRecord();
			} catch (Exception ex) {

			}

		}

	}

	private void UploadVoice() {
		if (filePath.equals("")) {
			return;
		}
		if (shiftId == 0) {
			return;
		}
		if (teamId == 0) {
			return;
		}
		Shift_Out_Upload_Voice_Task shiftOutDetailPostTask = new Shift_Out_Upload_Voice_Task(
				Shift_Out_two.this, filePath, shiftId, teamId, true);
		shiftOutDetailPostTask.execute();
	}

//	private OnTouchListener recordLis() {
//		return new OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				switch (event.getAction()) {
//				case MotionEvent.ACTION_DOWN:
//
//					mediaplayer.reset();
//					// 弹出一个麦的View
//					mSoundRecorderWindow = new PopupWindow(getLayoutInflater()
//							.inflate(R.layout.media_ability, null),
//							LayoutParams.WRAP_CONTENT,
//							LayoutParams.WRAP_CONTENT);
//					mSoundRecorderWindow.showAtLocation(
//							findViewById(R.id.shift_out_layout),
//							Gravity.CENTER, 0, 0);
//					initPopupWindow();
//
//					if (!isRecording) {
//						// btnRecordShiftOut.setTextColor(Color.BLACK);
//						String fileDir = getString(R.string.Shift_Record_Path);
//						Log.d(TAG, fileDir);
//						filePath = fileDir + "ShiftOut.wav";
//
//						File fDir = new File(fileDir);
//						if (!fDir.exists()) {
//							fDir.mkdirs();
//						}
//						voicePath = filePath;
//						File fileWav = new File(filePath);
//
//						if (fileWav.exists()) {
//							fileWav.delete();
//							Log.w(TAG, "Delete File " + fileWav.getPath());
//						}
//						// extAudioRecorder = ExtAudioRecorder
//						// .getInstanse(false);
//						extAudioRecorder = ExtAudioRecorder.getInstanse(true);
//
//						extAudioRecorder.setOutputFile(filePath);
//						IntercomCtrl.close_intercom(Shift_Out.this);
//						try {
//							extAudioRecorder.prepare();
//							extAudioRecorder.start();
//							isRecording = true;
//							// 启动线程刷新音量变化
//							new Thread(mAudioPbRunnable).start();
//
//						} catch (Exception e) {
//							Log.d(TAG, e.getMessage());
//						}
//					} else {
//						stopRecord();
//						isRecording = false;
//					}
//
//					break;
//				case MotionEvent.ACTION_UP:
//					// dissmiss 麦View
//					if (mSoundRecorderWindow != null) {
//						mSoundRecorderWindow.dismiss();
//					}
//
//					stopRecord();
//					break;
//				default:
//					break;
//				}
//				return false;
//			}
//		};
//	}

	private long getTeamId() {

		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		String methodPath = "WebService/spark.asmx";
		String methodName = "GetTeamId";
		WebServiceManager webServiceManager = new WebServiceManager(
				getApplicationContext(), methodName, parmValues);
		try {
			String result = webServiceManager.OpenConnect(methodPath);
			String errMsg = JsonUtil.GetJsonString(result, "Msg");
			if (!errMsg.equals("")) {
				return 0;
			}
			teamId = JsonUtil.GetJsonLong(result, "Data");
			return teamId;
		} catch (Exception ex) {
			return 0;
		}
	}

	private void postShiftOut() {
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("shiftOut_TrainNumList", tempList.toString());
		long ss = TssId;
		parmValues.put("teamId", String.valueOf(TssId));
		String methodPath = "WebService/Shift.asmx";
		String methodName = "SaveShiftOut";
		Log.d(TAG, "TempList = " + tempList.toString());
		WebServiceManager webServiceManager = new WebServiceManager(
				getApplicationContext(), methodName, parmValues);
		try {
			String result = webServiceManager.OpenConnect(methodPath);
			String errMsg = JsonUtil.GetJsonString(result, "Msg");
			if (!StringUtil.isNullOrEmpty(errMsg)) {
				// throw new Exception(errMsg);
				Message msg = new Message();
				msg.what = 3;
				msg.obj = errMsg;
				mHandler.sendMessage(msg);
			} else {
				shiftId = JsonUtil.GetJsonLong(result, "Data");
				Message msg = new Message();
				msg.what = 4;
				mHandler.sendMessage(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean hasPermission() {
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("teamId", String.valueOf(TssId));
		parmValues.put("shiftId", "0");
		String methodPath = "WebService/Shift.asmx";
		String methodName = "CheckModify";
		WebServiceManager webServiceManager = new WebServiceManager(
				Shift_Out_two.this, methodName, parmValues);
		try {
			String result = webServiceManager.OpenConnect(methodPath);
			return JsonUtil.GetJsonString(result, "Data").equals("true");
		} catch (Exception ex) {
			return false;
		}

	}

	private String getVoicePath() {

		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("shiftId", "0");
		String methodPath = "WebService/Shift.asmx";
		String methodName = "GetShiftOut";
		WebServiceManager webServiceManager = new WebServiceManager(
				Shift_Out_two.this, methodName, parmValues);
		String result = "";
		try {
			result = webServiceManager.OpenConnect(methodPath);
			if (StringUtil.isNullOrEmpty(result)) {
				return "";
			}
			String errMsg = JsonUtil.GetJsonString(result, "Msg");
			if (!StringUtil.isNullOrEmpty(errMsg)) {
				return "";
			}

			JSONArray jsonArray = JsonUtil
					.GetJsonArray(result, "Data");
			if (null != jsonArray && jsonArray.length() > 0) {
				JSONObject jsonObj = (JSONObject) jsonArray.opt(0);
				shiftId = JsonUtil.GetJsonObjLongValue(jsonObj,
						"Shift_Id");
				return JsonUtil.GetJsonObjStringValue(jsonObj,
						"Shift_Out_Voice");
			} else {
				return "";
			}
		} catch (Exception ex) {
			return "";
		}

	}

	private ArrayList<String> GetAllTask4Shift(int startIndex, int limit)

	{
		ArrayList<String> list = new ArrayList<String>();
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		// parmValues.put("limit", String.valueOf(limit));
		parmValues.put("workType", "3");
		parmValues.put("start", String.valueOf(startIndex));
		String methodPath = "WebService/Shift.asmx";
		String methodName = "Shift2_Out_Start";
		WebServiceManager webServiceManager = new WebServiceManager(
				Shift_Out_two.this, methodName, parmValues);
		try {
			String result = webServiceManager.OpenConnect(methodPath);
			String errMsg = JsonUtil.GetJsonString(result, "Msg");
			if (!errMsg.equals("")) {
				// throw new Exception(errMsg);
				return null;
			}
			JSONArray jsonArray = JsonUtil
					.GetJsonArray(result, "Data");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
				list.add(JsonUtil.GetJsonObjStringValue(jsonObj,
						"TrainNum"));
				// TODO 员工排班的标示号。
				TssId = JsonUtil.GetJsonObjLongValue(jsonObj, "TssId");
//				list.add(JsonUtil.GetJsonObjStringValue(TssId1, "TssId"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	private class Shift_Out_Adapter extends BaseAdapter {

		private ArrayList<String> todoList;
		private LayoutInflater infl;
		private long teamId = 0;

		// private Handler handel = null;

		public Shift_Out_Adapter(Context context, ArrayList<String> todoList,
				long teamId) {
			this.todoList = todoList;
			this.teamId = teamId;
			this.infl = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			if (todoList == null)
				return 0;
			return todoList.size();
		}

		@Override
		public String getItem(int position) {
			if (getCount() == 0)
				return null;
			return todoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		private OnClickListener remarkLis(final String trainNum) {
			return new OnClickListener() {

				public void onClick(View v) {
					Intent intent = new Intent(Shift_Out_two.this,
							Shift_Out_Remark.class);
					Bundle bundle = new Bundle();
					bundle.putString("TrainNo", trainNum);
					bundle.putLong("TeamId", TssId);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			};
		}

		private boolean isChoosed(String trainNum) {
			return tempList.contains(trainNum);
		}

		public void setData(ArrayList<String> models) {
			todoList = models;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final String trainNum = getItem(position);
			final ViewHolder holder;
			if (null == convertView) {
				convertView = infl.inflate(R.layout.shift_out_train_list_item,
						null);
				holder = new ViewHolder();

				holder.itemView = (RelativeLayout) convertView
						.findViewById(R.id.item_View);
				holder.chkTrainNum = (ImageView) convertView
						.findViewById(R.id.chkTrainNum);
				holder.labTrainNum = (TextView) convertView
						.findViewById(R.id.labTrainNum);

				holder.btnRemarksButton = (Button) convertView
						.findViewById(R.id.btnRemarks);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.labTrainNum.setText(trainNum);
			holder.chkTrainNum
					.setImageResource(isChoosed(trainNum) ? R.drawable.checkbox_selected
							: R.drawable.checkbox_normal);
			holder.itemView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// holder.chkTrainNum
					// .setImageResource(R.drawable.checkbox_selected);
					if (isChoosed(trainNum)) {
						holder.chkTrainNum
								.setImageResource(R.drawable.checkbox_normal);
						tempList.remove(trainNum);

					} else {
						holder.chkTrainNum
								.setImageResource(R.drawable.checkbox_selected);

						tempList.add(trainNum);
					}
				}
			});

			holder.btnRemarksButton.setOnClickListener(remarkLis(trainNum));

			return convertView;
		}

	}

	private static class ViewHolder {
		private View itemView;
		private ImageView chkTrainNum;
		private TextView labTrainNum;
		private Button btnRemarksButton;
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
					mHandler.sendMessage(msg);
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
			}
		}
	};

	private void recordVoice() {
		mediaplayer.reset();
		// 弹出一个麦的View
		mSoundRecorderWindow = new PopupWindow(getLayoutInflater().inflate(
				R.layout.media_ability, null), LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		mSoundRecorderWindow.showAtLocation(
				findViewById(R.id.shift_out_layout), Gravity.CENTER, 0, 0);
		initPopupWindow();

		if (!isRecording) {
			// btnRecordShiftOut.setTextColor(Color.BLACK);
			String fileDir = getString(R.string.Shift_Record_Path);
			Log.d(TAG, fileDir);
			filePath = fileDir + "ShiftOut.wav";

			File fDir = new File(fileDir);
			if (!fDir.exists()) {
				fDir.mkdirs();
			}
			voicePath = filePath;
			File fileWav = new File(filePath);

			if (fileWav.exists()) {
				fileWav.delete();
				Log.w(TAG, "Delete File " + fileWav.getPath());
			}
			// extAudioRecorder = ExtAudioRecorder.getInstanse(false);
			extAudioRecorder = ExtAudioRecorder.getInstanse(true);

			extAudioRecorder.setOutputFile(filePath);
			IntercomCtrl.close_intercom(Shift_Out_two.this);
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
	}

	private void shiftOut() {
		AlertDialog.Builder builder = new Builder(Shift_Out_two.this);
		builder.setIcon(R.drawable.information);
		builder.setMessage(getString(R.string.Shift_Out_Confirm));
		builder.setTitle(getString(R.string.Prompt));
		builder.setPositiveButton(getString(R.string.Shift_Out_OK),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// ShiftOut();
						showProgressDialog(R.string.GettingData);
						new Thread(){
							@Override
							public void run() {
								postShiftOut();
								super.run();
							}
						}.start();
					}
				});
		builder.setNegativeButton(getString(R.string.Shift_Out_Cancel), null);
		builder.create().show();
	}

	private void playVoice() {
		IntercomCtrl.close_intercom(Shift_Out_two.this);
		if (extAudioRecorder != null) {
			try {
				stopRecord();
				isRecording = false;
			} catch (Exception ex) {

			}
		}

		if (!voicePath.equals("")) {
			try {
				mediaplayer.reset();
				mediaplayer.setDataSource(voicePath);
				mediaplayer.prepare();
				mediaplayer.start();
			} catch (Exception ex) {
				Builder builder = new AlertDialog.Builder(Shift_Out_two.this);
				builder.setTitle(getString(R.string.Shift_Out));
				builder.setPositiveButton(getString(R.string.OK), null);
				builder.setMessage(getString(R.string.Shift_Out_Play_Sound_Exception));
				builder.show();
			}
		}
	}

	private void upLoadVoice() {
		AlertDialog.Builder builder = new Builder(Shift_Out_two.this);
		builder.setIcon(R.drawable.information);
		if (filePath.equals("")) {
			/*
			 * builder.setMessage(getString(R.string.Shift_Out_Record_Voice_First
			 * )); builder.setTitle(getString(R.string.Shift_Out_Upload_Voice));
			 * builder.setPositiveButton(getString(R.string.OK), null);
			 */
			return;
		} else {
			File file = new File(filePath);
			if (file.isDirectory())
				return;
			if (!file.exists())
				return;
			builder.setMessage(getString(R.string.Shift_Out_Upload_Confirm));
			builder.setTitle(getString(R.string.Shift_Out_Upload_Voice));
			builder.setPositiveButton(getString(R.string.OK),
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							UploadVoice();
						}
					});
			builder.setNegativeButton(
					getString(easyway.Mobile.R.string.Cancel), null);
		}
		builder.create().show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnShiftOut:
			stopRecord();
			shiftOut();
			break;
//		case R.id.btnRecordShiftOut:
//			if (mSoundRecorderWindow != null
//					&& mSoundRecorderWindow.isShowing()) {
//				stopRecord();
//			} else {
//				recordVoice();
//			}
//			break;
//		case R.id.btnPlayShiftOutVoice:
//			stopRecord();
//			playVoice();
//			break;
		default:
			break;
		}

	}

	private void stopRecord() {
		if (mSoundRecorderWindow != null && mSoundRecorderWindow.isShowing()) {
			IntercomCtrl.open_intercom(Shift_Out_two.this);
			mSoundRecorderWindow.dismiss();
			extAudioRecorder.stop();
			extAudioRecorder.release();
		}
	}
}
