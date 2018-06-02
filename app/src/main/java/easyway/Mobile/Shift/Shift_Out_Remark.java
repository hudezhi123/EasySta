package easyway.Mobile.Shift;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.Result;
import easyway.Mobile.Media.ExtAudioRecorder;
import easyway.Mobile.Media.FileAccessEx;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.ShiftData.Shift2OutGetDetailOpeningTask;
import easyway.Mobile.util.CommonUtils;
import easyway.Mobile.util.IntercomCtrl;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.StringUtil;
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
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class Shift_Out_Remark extends ActivityEx implements OnClickListener {
	private String tag = "Shift_Out_Remark", trainNo = "", filePath = "";
	private easyway.Mobile.Media.ExtAudioRecorder extAudioRecorder;

	private String voicePath = "";
	private String TrainNum = "";
	private long teamId = 0, shiftId = 0;
	private MediaPlayer mediaplayer = new MediaPlayer();
	private boolean isShiftIn;
	private Button btnShiftPost;
	private TextView txRemark;
	private ListView mListView;
	private int shift_Id;
	private AttachAdapter mAdapter;
	private ArrayList<String> attachList = new ArrayList<String>();
	private ArrayList<String> base64List = new ArrayList<String>();
	private int saId;
	private String remarkType;
	private String Task;
	
	private int shift_Detail_Id;
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

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			closeProgressDialog();
			switch (msg.what) {
			case 0:
				closeProgressDialog();
				showToast((String) msg.obj);
				break;

			case 1:
				String text = (String) msg.obj;
				if (!StringUtil.isNullOrEmpty(text)) {
					txRemark.setText(text);
				} else {
					txRemark.setText("");
				}
				break;
			case 2:
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
			case 10:
				closeProgressDialog();
				showToast("上传成功");
				break;
			case 11:
				closeProgressDialog();
				showToast("上传失败");
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shift_out_detail);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		trainNo = bundle.getString("TrainNo");
		shift_Id = bundle.getInt("shift_Id");
		remarkType = bundle.getString("RemarkType");
		Task = bundle.getString("Task");
		saId = bundle.getInt("saId");
		// new Thread(){
		// public void run() {
		// isShiftIn = hasPermission();
		// };
		// }.start();

		initView();
		// showProgressDialog(R.string.GettingData);
		 new Thread(new Runnable() {
			
			@Override
			public void run() {
				shift_Detail_Id = queryDetailId();
			}
		}).start();
//		new Thread() {
//			@Override
//			public void run() {
//				GetDetail();
//				super.run();
//			}
//		}.start();

		mediaplayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {
				IntercomCtrl.open_intercom(Shift_Out_Remark.this);
			}
		});
	}

	protected int queryDetailId() {
		HashMap<String, String> parmValues = new HashMap<String, String>();
		if (Task.equals("single")) {
			parmValues.put("sessionId", Property.SessionId);
			parmValues.put("shift_Id", String.valueOf(shift_Id));
			parmValues.put("saId", String.valueOf(saId));
			parmValues.put("remark", "");
			String methodPath = Constant.MP_SHIFT;
			String methodName = "Shift2_Out_Detail_Remark";
			WebServiceManager webServiceManager = new WebServiceManager(
					getApplicationContext(), methodName, parmValues);
			try {
				String result = webServiceManager.OpenConnect(methodPath);
				Gson gson = new Gson();
				Type type = new TypeToken<Result<Shift2OutGetDetailOpeningTask>>() {
				}.getType();
				Result<Shift2OutGetDetailOpeningTask> serverData = gson
						.fromJson(result, type);
				Shift2OutGetDetailOpeningTask data = serverData.getData();
				return data.getShift_Detail_Id();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	private void initView() {

		TextView labTitle = (TextView) findViewById(R.id.title);
		if(TextUtils.isEmpty(trainNo))
			labTitle.setText(remarkType);
		else
		    labTitle.setText(trainNo+remarkType);
		txRemark = (TextView) findViewById(R.id.txRemark);
		
		btnShiftPost = (Button) findViewById(R.id.btnShiftPost);
		mListView = (ListView) findViewById(R.id.ShiftOut_ListAttach);
		if (isShiftIn) {
			btnShiftPost.setEnabled(false);
			btnShiftPost.setVisibility(View.GONE);
		} else {
			btnShiftPost.setEnabled(true);
			btnShiftPost.setVisibility(View.VISIBLE);
		}
		// btnShiftPost.setOnClickListener(postRemark());
		btnShiftPost.setOnClickListener(this);

		Button btnShiftRecordVoice = (Button) findViewById(R.id.btnShiftRecordVoice);
		// btnShiftRecordVoice.setOnTouchListener(recordLis());
		btnShiftRecordVoice.setOnClickListener(this);

		Button btnPlayVoice = (Button) findViewById(R.id.btnPlayVoice);
		// btnPlayVoice.setOnClickListener(palyVoice());
		if (remarkType.equals("文字备注")) {
			btnShiftRecordVoice.setVisibility(View.GONE);
			btnPlayVoice.setVisibility(View.GONE);
		}
		if (remarkType.equals("语音备注")) {
			txRemark.setVisibility(View.GONE);
		}
		btnPlayVoice.setOnClickListener(this);
		if (shiftId > 0) {
			btnShiftPost.setVisibility(View.GONE);
			btnShiftPost.setEnabled(false);
			btnShiftRecordVoice.setVisibility(View.GONE);
			btnShiftRecordVoice.setEnabled(false);
		}

		mAdapter = new AttachAdapter(this);
		mListView.setAdapter(mAdapter);

	}

	@Override
	public void onPause() {
		super.onPause();
		IntercomCtrl.open_intercom(Shift_Out_Remark.this);
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

	private void GetDetail() {
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("trainNo", String.valueOf(trainNo));
		parmValues.put("shift_Id", String.valueOf(shift_Id));
		parmValues.put("teamId", String.valueOf(teamId));
		String methodPath = "WebService/Shift.asmx";
		String methodName = "GetShiftDetail";

		WebServiceManager webServiceManager = new WebServiceManager(
				Shift_Out_Remark.this, methodName, parmValues);
		try {
			String result = webServiceManager.OpenConnect(methodPath);
			String errMsg = JsonUtil.GetJsonString(result, "Msg");
			// if (!errMsg.equals(""))
			// {
			// handel.post(mUpdateError);
			// }
			if (!StringUtil.isNullOrEmpty(errMsg)) {
				Message msg = new Message();
				msg.what = 0;
				msg.obj = errMsg;
				mHandler.sendMessage(msg);
			} else {
				JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
				if (jsonArray.length() > 0) {
					JSONObject jsonObj = (JSONObject) jsonArray.opt(0);
					voicePath = JsonUtil.GetJsonObjStringValue(jsonObj,
							"VoicePath");
					teamId = JsonUtil.GetJsonObjLongValue(jsonObj, "TssId");

					TrainNum = JsonUtil.GetJsonObjStringValue(jsonObj,
							"TrainNum");
				}
				Message msg = new Message();
				msg.what = 1;
				msg.obj = TrainNum;
				mHandler.sendMessage(msg);
			}
		} catch (Exception ex) {
			return;
		}
	}

	private boolean hasPermission() {
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("shiftId", "0");
		String methodPath = "WebService/Shift.asmx";
		String methodName = "CheckModify";
		WebServiceManager webServiceManager = new WebServiceManager(
				Shift_Out_Remark.this, methodName, parmValues);
		try {
			String result = webServiceManager.OpenConnect(methodPath);
			return JsonUtil.GetJsonString(result, "Data").equals("true");
		} catch (Exception ex) {
			return false;
		}

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
					msg.what = 2;
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

	private void postRemark() {
		AlertDialog.Builder builder = new Builder(Shift_Out_Remark.this);
		builder.setIcon(R.drawable.warning);
		builder.setMessage(R.string.Shift_Out_Remark_Confirm);
		builder.setTitle(R.string.Prompt);
		builder.setPositiveButton(R.string.Shift_Out_OK,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						final TextView txRemark = (TextView) findViewById(R.id.txRemark);

						// Shift_Out_Detail_Post_Task shiftOutDetailPostTask =
						// new Shift_Out_Detail_Post_Task(
						// Shift_Out_Remark.this, trainNo, URLEncoder
						// .encode(txRemark.getText().toString()),
						// filePath, teamId);
						// shiftOutDetailPostTask.handler = new
						// Shift_Out_Handler(
						// Shift_Out_Remark.this);
						// shiftOutDetailPostTask.execute();
						showProgressDialog("正在上传"+ remarkType);
						new Thread(new Runnable() {
							@Override
							public void run() {
								if (remarkType.equals("文字备注")) {
									boolean updateShift = posttext(txRemark);
									if (updateShift) {
//										Toast.makeText(getApplicationContext(),
//												"上传成功", 0).show();
										mHandler.sendEmptyMessage(10);
									}else{
										mHandler.sendEmptyMessage(11);
									}
								}if(remarkType.equals("语音备注")) {
									
//									base64List = encodeBase64File(attachList);
//									postVoice(base64List);
									String filePathString = attachList.get(0);
									postToVoice(filePathString);
								}
							}

							


						}).start();
					}
				});
		builder.setNegativeButton(R.string.Shift_Out_Cancel, null);
		builder.create().show();
	}
	private ArrayList<String> encodeBase64File(ArrayList<String> attachList2) {
			ArrayList<String> arrayList = new ArrayList<String>();
			for (int i = 0; i < attachList2.size(); i++) {
				File file = new File(attachList2.get(i));
				FileInputStream inputFile;
				try {
					inputFile = new FileInputStream(file);
					byte[] buffer = new byte[(int) file.length()];
					inputFile.read(buffer);
					
					inputFile.close();
//					 byte[] encode = Base64().encode(buffer,Base64.DEFAULT);
					 byte[] encode = Base64.encode(buffer, Base64.DEFAULT);
					 String data = new String(encode);
					arrayList.add(data);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
				
			return arrayList;
	}
	private boolean postVoice(ArrayList<String> encodeBase64File) {
		for (int i = 0; i < encodeBase64File.size(); i++) {
			HashMap<String, String> parmValues = new HashMap<String, String>();		
			if (Task.equals("single")) {
				parmValues.put("sessionId", Property.SessionId);
				parmValues.put("saId", String.valueOf(saId));
				parmValues.put("fileStreamString", encodeBase64File.get(i));
				String fileSize = encodeBase64File.get(i);
				parmValues.put("position", String.valueOf(fileSize.length()));
				String methodPath = Constant.MP_SHIFT;
				String methodName = "Shift2_Out_UploadDetailInVoice";
				WebServiceManager webServiceManager = new WebServiceManager(
						getApplicationContext(), methodName, parmValues);
				try {
					String result = webServiceManager.OpenConnect(methodPath);
					String errMsg = JsonUtil.GetJsonString(result, "Msg");
//					if (errMsg.equals("更新交班成功")) {
//						return true;
//					} else {
//						return false;
//					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
			if (Task.equals("whole")) {
				parmValues.put("sessionId", Property.SessionId);
				parmValues.put("shift_Id", String.valueOf(shift_Id));
				parmValues.put("fileStreamString", encodeBase64File.get(i));
				String fileSize = encodeBase64File.get(i);
				parmValues.put("position", String.valueOf(fileSize.length()));
				String methodPath = Constant.MP_SHIFT;
				String methodName = "Shift2_Out_UploadInVoice";
				WebServiceManager webServiceManager = new WebServiceManager(
						getApplicationContext(), methodName, parmValues);
				try {
					String result = webServiceManager.OpenConnect(methodPath);
					String errMsg = JsonUtil.GetJsonString(result, "Msg");
//					if (errMsg.equals("更新交班成功")) {
//						return true;
//					} else {
//						return false;
//					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}

			return false;
		}
		return false;
		
		
	}
	private boolean posttext(TextView txRemark2) {
		HashMap<String, String> parmValues = new HashMap<String, String>();
		if (Task.equals("single")) {
			parmValues.put("sessionId", Property.SessionId);
			parmValues.put("shift_Id", String.valueOf(shift_Id));
			parmValues.put("saId", String.valueOf(saId));
			parmValues.put("remark", txRemark2.getText().toString());
			String methodPath = Constant.MP_SHIFT;
			String methodName = "Shift2_Out_Detail_Remark";
			WebServiceManager webServiceManager = new WebServiceManager(
					getApplicationContext(), methodName, parmValues);
			try {
				String result = webServiceManager.OpenConnect(methodPath);
				String errMsg = JsonUtil.GetJsonString(result, "MsgType");
				if (errMsg.equals("true")) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
		if (Task.equals("whole")) {
			parmValues.put("sessionId", Property.SessionId);
			parmValues.put("shift_Id", String.valueOf(shift_Id));
			parmValues.put("remark", txRemark2.getText().toString());
			String methodPath = Constant.MP_SHIFT;
			String methodName = "Shift2_Out_Remark";
			WebServiceManager webServiceManager = new WebServiceManager(
					getApplicationContext(), methodName, parmValues);
			try {
				String result = webServiceManager.OpenConnect(methodPath);
				String errMsg = JsonUtil.GetJsonString(result, "MsgType");
				if (errMsg.equals("MsgType")) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		return false;
	}

	private void recordVoice() {
		// 弹出一个麦的View
		mSoundRecorderWindow = new PopupWindow(getLayoutInflater().inflate(
				R.layout.media_ability, null), LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		mSoundRecorderWindow.showAtLocation(
				findViewById(R.id.shift_out_detail_layout), Gravity.CENTER, 0,
				0);
		initPopupWindow();

		String fileDir = getString(R.string.Shift_Record_Path);
		Log.d(tag, fileDir);
		filePath = fileDir + trainNo + ".wav";

		File fDir = new File(fileDir);
		if (!fDir.exists()) {
			fDir.mkdirs();
		}
		File fileWav = new File(filePath);

		if (fileWav.exists()) {
			fileWav.delete();
			Log.w(tag, "Delete File " + fileWav.getPath());
		}

		// extAudioRecorder = ExtAudioRecorder.getInstanse(false);
		extAudioRecorder = ExtAudioRecorder.getInstanse(true);
		voicePath = filePath;
		extAudioRecorder.setOutputFile(filePath);
		IntercomCtrl.close_intercom(Shift_Out_Remark.this);
		try {
			extAudioRecorder.prepare();
			extAudioRecorder.start();
			// 启动线程刷新音量变化
			new Thread(mAudioPbRunnable).start();

		} catch (Exception e) {
			Log.d(tag, e.getMessage());
		}
	}

	private void playVoice() {
		if (!voicePath.equals("")) {
			try {
				IntercomCtrl.close_intercom(Shift_Out_Remark.this);
				mediaplayer.reset();
				mediaplayer.setDataSource(voicePath);
				mediaplayer.prepare();
				mediaplayer.start();
			} catch (Exception ex) {
				Builder builder = new AlertDialog.Builder(Shift_Out_Remark.this);
				builder.setTitle(R.string.Shift_Out);
				builder.setPositiveButton(R.string.OK, null);
				builder.setMessage(R.string.Shift_Out_Play_Sound_Exception);
				builder.show();
			}
		}
	}

	private void stopRecord() {
		// dissmiss 麦View
		if (mSoundRecorderWindow != null && mSoundRecorderWindow.isShowing()) {
			IntercomCtrl.open_intercom(Shift_Out_Remark.this);
			extAudioRecorder.stop();
			extAudioRecorder.release();
			mSoundRecorderWindow.dismiss();
			AddAttachList(filePath);
		}
	}

	/** 添加附件到list */
	private void AddAttachList(String attachUrl) {
		if (!attachList.contains(attachUrl)) {
			attachList.add(0, attachUrl);
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnShiftPost:
			stopRecord();
			postRemark();
			break;
		case R.id.btnShiftRecordVoice:
			if (mSoundRecorderWindow != null
					&& mSoundRecorderWindow.isShowing()) {
				stopRecord();
			} else {
				recordVoice();
			}
			break;
		case R.id.btnPlayVoice:
			stopRecord();
			playVoice();
			break;
		default:
			break;
		}

	}

	// 附件adapter
	class AttachAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Context mContext;

		public AttachAdapter(Context context) {
			this.mContext = context;
			mInflater = LayoutInflater.from(mContext);
		}

		public int getCount() {
			if (attachList == null)
				return 0;
			else
				return attachList.size();
		}

		public String getItem(int position) {
			if (attachList == null)
				return null;
			else
				return attachList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.livecase_report_item,
						null);
				holder = new ViewHolder();
				holder.AttachId = (TextView) convertView
						.findViewById(R.id.annexId);
				holder.AttachUrl = (TextView) convertView
						.findViewById(R.id.annexUrl);
				holder.btnDelete = (Button) convertView
						.findViewById(R.id.btnDelete);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			String filePath = getItem(position);
			String fileName = CommonUtils.getFileNameFromPath(filePath);
			holder.AttachId.setText(String.valueOf(position + 1) + ":");
			holder.AttachUrl.setText(fileName);
			holder.btnDelete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					attachList.remove(position);
					mAdapter.notifyDataSetChanged();
				}
			});
			return convertView;
		}

		class ViewHolder {
			TextView AttachId;
			TextView AttachUrl;
			Button btnDelete;
		}
	}
	
	private void postToVoice(String filePathString) {
		boolean isSuccess = false;
		if (Task.equals("single")) {
			FileAccessEx fileAccessEx;
			try {
				fileAccessEx = new FileAccessEx(filePathString, 0);
				Long nStartPos = 0l;
				Long length = fileAccessEx.getFileLength();
				
				int mBufferSize = 1024 * 100; // 每次处理1024 * 100字节
				byte[] buffer = new byte[mBufferSize];
				FileAccessEx.Detail detail;
				long nRead = 0l;
				long nStart = nStartPos;
				
				int postion = 0;
				while (nStart < length)
				{
					detail = fileAccessEx.getContent(nStart);
					nRead = detail.length;
					buffer = detail.b;
					
					nStart += nRead;
					nStartPos = nStart;
					
					HashMap<String, String> parmValues = new HashMap<String, String>();
					parmValues.put("sessionId", Property.SessionId);
					parmValues.put("shift_Detail_Id", String.valueOf(shift_Detail_Id));
					parmValues.put("fileStreamString", Base64.encodeToString(buffer, Base64.DEFAULT));
					parmValues.put("position", String.valueOf(postion));
					
					String methodPath =Constant.MP_SHIFT;
					String methodName = "Shift2_Out_UploadDetailInVoice";
					WebServiceManager webServiceManager = new WebServiceManager(
							getApplicationContext(), methodName, parmValues);
					String result = webServiceManager.OpenConnect(methodPath);
					String errMsg = JsonUtil.GetJsonString(result, "MsgType");
					if (errMsg.equals("true")) {
//						Message message = new Message();
//						message.what = 0;
//						message.obj = "上传成功";
//						mHandler.sendMessage(message);
						isSuccess = true;
					}else {
//						Message message = new Message();
//						message.what = 0;
//						message.obj = "上传失败";
//						mHandler.sendMessage(message);
						isSuccess = false;
					}
					Log.d(tag, "File Path is " + filePath + ",Postion is "
							+ postion);
					postion++;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}if (Task.equals("whole")) {
			FileAccessEx fileAccessEx;
			try {
				fileAccessEx = new FileAccessEx(filePathString, 0);
				Long nStartPos = 0l;
				Long length = fileAccessEx.getFileLength();
				
				int mBufferSize = 1024 * 100; // 每次处理1024 * 100字节
				byte[] buffer = new byte[mBufferSize];
				FileAccessEx.Detail detail;
				long nRead = 0l;
				long nStart = nStartPos;
				
				int postion = 0;
				while (nStart < length)
				{
					detail = fileAccessEx.getContent(nStart);
					nRead = detail.length;
					buffer = detail.b;
					
					nStart += nRead;
					nStartPos = nStart;
					
					HashMap<String, String> parmValues = new HashMap<String, String>();
					parmValues.put("sessionId", Property.SessionId);
					parmValues.put("shift_Id", String.valueOf(shift_Id));
					parmValues.put("fileStreamString", Base64.encodeToString(buffer, Base64.DEFAULT));
					parmValues.put("position", String.valueOf(postion));
					
					String methodPath =Constant.MP_SHIFT;
					String methodName = "Shift2_Out_UploadInVoice";
					WebServiceManager webServiceManager = new WebServiceManager(
							getApplicationContext(), methodName, parmValues);
					String result = webServiceManager.OpenConnect(methodPath);
					Log.d(tag, "File Path is " + filePath + ",Postion is "
							+ postion);
					String errMsg = JsonUtil.GetJsonString(result, "MsgType");
					if (errMsg.equals("true")) {
					
						isSuccess = true;
					}else {
						
						isSuccess = false;
					}
					postion++;
				}
				
					
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		if(isSuccess){
			Message message = new Message();
			message.what = 0;
			message.obj = "上传成功";
			mHandler.sendMessage(message);
		}else{
			Message message = new Message();
			message.what = 0;
			message.obj = "上传失败";
			mHandler.sendMessage(message);
		}
	}
}
