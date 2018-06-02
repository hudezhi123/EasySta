package easyway.Mobile.Broadcast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.R;
import easyway.Mobile.Media.ExtAudioRecorder;
import easyway.Mobile.Media.ExtAudioRecorder.State;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.CommonUtils;
import easyway.Mobile.util.IntercomCtrl;
import easyway.Mobile.util.JsonUtil;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;

/*
 * 人工广播 - 添加
 */
public class BroadcastManualAddActivity extends ActivityEx implements
		OnClickListener {
	private ArrayList<BroadcastInfo> listBroadcastArea;
	private Button btnRecord;

	private Uri fileUri;
	private ExtAudioRecorder extAudioRecorder;
	private PopupWindow mSoundRecorderWindow; // 按下录音提示窗口
	private ImageView mMicImageView; // 录制音频时麦克风的Image
	private boolean mProgressBarEable = true; // 判断是否停止线程
	private int mCurrentVoice; // 获取当前音量

	private MediaPlayer mediaplayer = new MediaPlayer();
	private static final int MSG_SOUND_AMPLITUDE = 110;
	private final int MSG_GETDATA_FAIL = 0;
	private final int MSG_GETDATA_SUCC = 1;

	@SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			closeProgressDialog();
			switch (msg.what) {
			case MSG_SOUND_AMPLITUDE:
				int volume = (Integer) msg.obj;
				if (volume <= 100) {
					mMicImageView.setBackgroundResource(R.drawable.mic_one);
				} else if (100 < volume && volume <= 200) {
					mMicImageView.setBackgroundResource(R.drawable.mic_two);
				} else if (200 < volume && volume <= 300) {
					mMicImageView.setBackgroundResource(R.drawable.mic_three);
				} else if (300 < volume && volume <= 500) {
					mMicImageView.setBackgroundResource(R.drawable.mic_four);
				}
				break;
			case MSG_GETDATA_SUCC:
				listBroadcastArea = (ArrayList<BroadcastInfo>) msg.obj;

				if (listBroadcastArea == null || listBroadcastArea.size() == 0)
					return;

				for (int i = 0; i < listBroadcastArea.size(); i++) {
					listBroadcastArea.get(i).IsSelected = false;
				}
				break;
			case MSG_GETDATA_FAIL:
				if (errMsg != null)
					showErrMsg(errMsg);
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.broadcast_manual_add);

		initView();

		showProgressDialog(R.string.GettingData);

		new Thread() {
			public void run() {
				GetAllBroadcastArea();
			}
		}.start();

		mediaplayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {
				IntercomCtrl.open_intercom(BroadcastManualAddActivity.this);
			}
		});

		mediaplayer.setOnErrorListener(new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				IntercomCtrl.open_intercom(BroadcastManualAddActivity.this);
				return false;
			}
		});
	}

	private void initView() {
		Button btnSelArea = (Button) findViewById(R.id.btnSelArea);
		btnSelArea.setOnClickListener(this);

		Button btnPost = (Button) findViewById(R.id.btnPost);
		btnPost.setOnClickListener(this);

		Button btnPlay = (Button) findViewById(R.id.btnPlay);
		btnPlay.setOnClickListener(this);

		btnRecord = (Button) findViewById(R.id.btnRecord);
		btnRecord.setOnClickListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			if (mediaplayer != null && mediaplayer.isPlaying()) {
				mediaplayer.reset();
				IntercomCtrl.open_intercom(BroadcastManualAddActivity.this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void postList() {
		ArrayList<BroadcastInfo> listBroadcastAreaSelect = new ArrayList<BroadcastInfo>();
		String broadArea = "", broadAreaIds = "";
		for (int i = 0; i < listBroadcastArea.size(); i++) {
			if (listBroadcastArea.get(i).IsSelected) {
				listBroadcastAreaSelect.add(listBroadcastArea.get(i));
				if (broadArea.equals("")) {
					broadAreaIds = String
							.valueOf(listBroadcastArea.get(i).IDENO_EQTs);
					broadArea = listBroadcastArea.get(i).Area;
				} else {
					broadAreaIds += ","
							+ String.valueOf(listBroadcastArea.get(i).IDENO_EQTs);
					broadArea += "," + listBroadcastArea.get(i).Area;
				}
			}
		}

		if (listBroadcastAreaSelect.size() == 0) {
			Builder builder = new AlertDialog.Builder(
					BroadcastManualAddActivity.this);
			builder.setTitle(R.string.broad_manual);
			builder.setPositiveButton(R.string.OK, null);
			builder.setMessage(R.string.broad_area_Empty);
			builder.show();
			return;
		}

		String fileName = fileUri.getPath();
		Builder builder = new AlertDialog.Builder(
				BroadcastManualAddActivity.this);
		builder.setTitle(R.string.broad_manual);
		builder.setPositiveButton(R.string.OK, null);
		if (fileName.equals("")) {
			builder.setMessage(R.string.broad_record_Empty);
			builder.show();
			return;
		}

		File fileAudio = new File(fileName);
		if (!fileAudio.exists()) {
			builder.setMessage(R.string.broad_record_Empty);
			builder.show();
			return;
		}

		postBroadcast(fileName, broadArea, broadAreaIds);
	}

	private void playAudio() {
		if (fileUri == null) {
			showToast(R.string.broad_record_Empty);
			return;
		}
		String fileName = fileUri.getPath();
		Builder builder = new AlertDialog.Builder(
				BroadcastManualAddActivity.this);
		builder.setTitle(R.string.broad_manual);
		builder.setPositiveButton(R.string.OK, null);
		if (fileName.equals("")) {
			builder.setMessage(R.string.broad_record_Empty);
			builder.show();
			return;
		}

		File fileAudio = new File(fileName);
		if (!fileAudio.exists()) {
			builder.setMessage(R.string.broad_record_Empty);
			builder.show();
			return;
		}

		try {
			IntercomCtrl.close_intercom(BroadcastManualAddActivity.this);
			mediaplayer.reset();
			mediaplayer.setDataSource(fileName);
			mediaplayer.prepare();
			mediaplayer.start();
		} catch (Exception ex) {
			ex.printStackTrace();
			builder.setMessage(getString(R.string.broad_record_play_exception));
			builder.show();
		}

	}

	private void selectArea() {
		if (listBroadcastArea == null) {
			return;
		}
		if (listBroadcastArea.size() == 0) {
			return;
		}
		String[] m = new String[listBroadcastArea.size()];
		boolean[] checkedItems = new boolean[listBroadcastArea.size()];
		for (int i = 0; i < listBroadcastArea.size(); i++) {
			m[i] = listBroadcastArea.get(i).Area;
			checkedItems[i] = listBroadcastArea.get(i).IsSelected;
		}

		Builder builder = new AlertDialog.Builder(
				BroadcastManualAddActivity.this);
		builder.setTitle(R.string.broad_area);
		builder.setMultiChoiceItems(m, checkedItems, multiClick);
		builder.setPositiveButton(R.string.OK, ShowArea());

		builder.setNegativeButton(R.string.Cancel, null);
		builder.show();
	}

	private DialogInterface.OnClickListener ShowArea() {
		return new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (listBroadcastArea == null)
					return;

				ListView gvAreaList = (ListView) findViewById(R.id.lstArea);
				ArrayList<BroadcastInfo> listBroadcastAreaSelect = new ArrayList<BroadcastInfo>();

				for (int i = 0; i < listBroadcastArea.size(); i++) {
					boolean areaStatus = listBroadcastArea.get(i).IsSelected;
					if (areaStatus) {
						listBroadcastAreaSelect.add(listBroadcastArea.get(i));
					}
				}

				BroadAreaAdapter boardAreaAdapter = new BroadAreaAdapter(
						BroadcastManualAddActivity.this,
						listBroadcastAreaSelect);
				gvAreaList.setAdapter(boardAreaAdapter);
			}
		};
	}

	private OnMultiChoiceClickListener multiClick = new OnMultiChoiceClickListener() {
		public void onClick(DialogInterface dialog, int which, boolean isChecked) {
			if (listBroadcastArea == null)
				return;

			if (listBroadcastArea.size() > which)
				listBroadcastArea.get(which).IsSelected = isChecked;
		}
	};

	private void GetAllBroadcastArea() {
		ArrayList<BroadcastInfo> list = new ArrayList<BroadcastInfo>();
		HashMap<String, String> parmValues = new HashMap<String, String>();
		String methodPath = Constant.MP_BROADCAST;
		String methodName = "GetAllBroadcastArea";
		WebServiceManager webServiceManager = new WebServiceManager(
				getApplicationContext(), methodName, parmValues);
		String result = webServiceManager.OpenConnect(methodPath);
		if (result == null || result.length() == 0) {
			errMsg = getString(R.string.broad_getdata_fail);
			myhandle.sendEmptyMessage(MSG_GETDATA_FAIL);
			return;
		}
		
		int Code = JsonUtil.GetJsonInt(result, "Code");

		switch (Code) {
		case Constant.NORMAL:
			JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
				BroadcastInfo broadcastInfo = new BroadcastInfo();
				broadcastInfo.Area = JsonUtil.GetJsonObjStringValue(jsonObj,
						"BAName");
				broadcastInfo.IDENO_EQTs = JsonUtil.GetJsonObjStringValue(jsonObj,
						"IDENO_EQT");
				list.add(broadcastInfo);
			}
			
			Message msg = new Message();
			msg.obj = list;
			msg.what = MSG_GETDATA_SUCC;
			myhandle.sendMessage(msg);
			break;
		case Constant.EXCEPTION:
		default:
			errMsg = JsonUtil.GetJsonString(result, "Msg");
			myhandle.sendEmptyMessage(MSG_GETDATA_FAIL);
			break;
		}
	}

	private void postBroadcast(String filePath, String broadArea,
			String broadAreaIds) {
		BroadcastPostHandler handler = new BroadcastPostHandler(
				BroadcastManualAddActivity.this);
		BroadcastPost fileUploadTask = new BroadcastPost();
		fileUploadTask.context = BroadcastManualAddActivity.this;
		fileUploadTask.filePath = filePath;
		fileUploadTask.broadArea = broadArea;
		fileUploadTask.handler = handler;
		fileUploadTask.broadAreaIds = broadAreaIds;
		fileUploadTask.execute();
	}

	public void CloseForm() {
		setResult(2);
		finish();
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
	// 系统音量变化是否会发出广播，按广播接收方式进行处理
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
					msg.what = MSG_SOUND_AMPLITUDE;
					msg.obj = mCurrentVoice / 100;
					myhandle.sendMessage(msg);
				}

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
			}
		}
	};

	private void recordVoice() {
		((Button) findViewById(R.id.btnPlay)).setEnabled(false);
		((Button) findViewById(R.id.btnPost)).setEnabled(false);

		fileUri = CommonUtils.getOutputMediaFileUri(
				BroadcastManualAddActivity.this, CommonUtils.MEDIA_TYPE_AUDIO);

		// 弹出一个麦的View
		mSoundRecorderWindow = new PopupWindow(getLayoutInflater().inflate(
				R.layout.media_ability, null), LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		mSoundRecorderWindow.showAtLocation(findViewById(R.id.LayoutMain),
				Gravity.CENTER, 0, 0);
		initPopupWindow();

		if (fileUri != null) {
			extAudioRecorder = ExtAudioRecorder.getInstanse(true); // compressed
			extAudioRecorder.setOutputFile(fileUri.getPath());
			IntercomCtrl.close_intercom(BroadcastManualAddActivity.this);
			try {
				extAudioRecorder.prepare();
				extAudioRecorder.start();
				mProgressBarEable = true;
				// 启动线程刷新音量变化
				new Thread(mAudioPbRunnable).start();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private void stopRecord() {
		// dissmiss 麦View
		if (extAudioRecorder == null)
			return;

		if (extAudioRecorder.getState().equals(State.RECORDING)) {
			IntercomCtrl.open_intercom(BroadcastManualAddActivity.this);
			extAudioRecorder.stop();
			extAudioRecorder.release();
			mSoundRecorderWindow.dismiss();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSelArea:
			stopRecord();
			selectArea();
			break;
		case R.id.btnPost:
			stopRecord();
			postList();
			break;
		case R.id.btnPlay:
			stopRecord();
			playAudio();
			break;
		case R.id.btnRecord:
			// dissmiss 麦View
			if (mSoundRecorderWindow != null
					&& mSoundRecorderWindow.isShowing()) {
				stopRecord();
				((Button) findViewById(R.id.btnPlay)).setEnabled(true);
				((Button) findViewById(R.id.btnPost)).setEnabled(true);
			} else {
				recordVoice();
			}
			break;
		default:
			break;
		}

	}
}
