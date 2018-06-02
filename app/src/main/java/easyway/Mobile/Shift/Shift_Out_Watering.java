package easyway.Mobile.Shift;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.Result;
import easyway.Mobile.Data.ResultForListData;
import easyway.Mobile.Media.ExtAudioRecorder;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.ShiftData.Shift2InGetAllShiftOutData;
import easyway.Mobile.ShiftData.Shift2OutGetOpeningTask;
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

public class Shift_Out_Watering extends ActivityEx implements OnClickListener {
	private static final String TAG = "Shift_Out";
	private ArrayList<Shift2OutGetOpeningTask> trainNumList = new ArrayList<Shift2OutGetOpeningTask>();
	private ArrayList<Shift2OutGetOpeningTask> tempList = new ArrayList<Shift2OutGetOpeningTask>();
	private int startIndex = 0;
	private int limit = 10;
	private ListView mListView;
	private Shift_Out_Adapter shift_out_adapter;
	private long teamId = 0;
	private boolean isShiftIn;
	private int workType;
	private String filePath = "";
	private boolean isRecording = false;
	private ExtAudioRecorder extAudioRecorder;
	private MediaPlayer mediaplayer = new MediaPlayer();
	private String voicePath = "";
	private long shiftId = 0;
	// 员工排班的标识号
	private long TssId;
	private boolean ShiftIn = false;
	private boolean isAllChecked = false;
	private Shift2InGetAllShiftOutData shiftOutData;
	private int shift_Id;
	private int saId;
	private String saIds = "";
	private Button btnTextNoteShiftOut;
	private Button btnVoiceNoteShiftOut;

	private Button btnShiftOut;
	private Button bt_selectall;
	private Button bt_cancel;
	private Button btnSetDetail;
	
	

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
//			closeProgressDialog();
			switch (msg.what) {
			case 0:
				closeProgressDialog();
				showToast("Error");
				break;

			case 1:
				closeProgressDialog();
				ArrayList<Shift2OutGetOpeningTask> list = (ArrayList<Shift2OutGetOpeningTask>) msg.obj;
				// for (Shift2OutGetOpeningTask trainNum : list) {
				// tempList.add(trainNum);
				// }
				if(list.size() <= 0)
					showToast("暂无数据");
				shift_out_adapter.setData(list);
				shift_out_adapter.notifyDataSetChanged();
				break;

			case 3:
				closeProgressDialog();
				tempList.clear();
				shift_out_adapter.notifyDataSetChanged();
				showToast((String) msg.obj);
				break;

			case 4:
				closeProgressDialog();
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
			case 7:
				closeProgressDialog();
				showToast("开始交班成功");
				btnTextNoteShiftOut.setEnabled(true);
				btnTextNoteShiftOut.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.btn_m_selector));

				btnVoiceNoteShiftOut.setEnabled(true);
				btnVoiceNoteShiftOut.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.btn_m_selector));
				 btnSetDetail.setEnabled(false);
//				 btnSetDetail.setBackgroundDrawable(getResources().getDrawable(
//				 R.drawable.btn_m_selector));
				btnShiftOut.setText("交班完成");
				btnShiftOut.setEnabled(false);
				btnShiftOut.setBackgroundColor(0);
				ShiftIn = true;
				shift_out_adapter.notifyDataSetChanged();
				break;
			case 8:
				tempList.clear();
				shift_out_adapter.notifyDataSetChanged();
				break;
			case 9:
				closeProgressDialog();
				showToast("开始交班失败");
				break;
			case 10:
				btnShiftOut.setEnabled(true);
				btnShiftOut.setBackgroundDrawable(getResources().getDrawable(
				 R.drawable.btn_m_selector));
				shift_out_adapter.notifyDataSetChanged();
				break;
			case 11:
				btnShiftOut.setEnabled(false);
				btnShiftOut.setBackgroundColor(0);
				shift_out_adapter.notifyDataSetChanged();
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
		initView();
		showProgressDialog(R.string.GettingData);
		new Thread(new AllTask4ShiftThread()).start();
		// new Thread(){
		// @Override
		// public void run() {
		// voicePath = getVoicePath();
		// boolean temp = hasPermission();
		// isShiftIn = temp;
		// mHandler.sendEmptyMessage(6);
		// teamId = getTeamId();
		// super.run();
		// }
		// }.start();
		mediaplayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {
				IntercomCtrl.open_intercom(Shift_Out_Watering.this);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		btnSetDetail.setEnabled(false);
		btnSetDetail.setBackgroundColor(0);
		Message message = new Message();
		message.what = 8;
		mHandler.sendMessage(message);
	}
	
	private void initView() {
		workType = getIntent().getIntExtra("shiftInWorkType", -1);
		TextView labTitle = (TextView) findViewById(R.id.title);
		labTitle.setText(getString(R.string.Shift_Out));
		switch (workType) {
		case 0:
			labTitle.setText(getString(R.string.task_shangshui));
			break;
		case 1:
			labTitle.setText(getString(R.string.task_station));
			break;
		case 2:
			labTitle.setText(getString(R.string.task_waitinghall));
			break;
		case 3:
			labTitle.setText(getString(R.string.task_checkin));
			break;
		case 4:
			labTitle.setText(getString(R.string.task_exit));
			break;
		default:
			break;
		}
		mListView = (ListView) findViewById(R.id.gvList);
		shift_out_adapter = new Shift_Out_Adapter(this, trainNumList, teamId);
		mListView.setAdapter(shift_out_adapter);
		btnShiftOut = (Button) findViewById(R.id.btnShiftOut);
		// btnShiftOut.setOnClickListener(postShiftOutLis());
		btnShiftOut.setOnClickListener(this);

		// btnRecordShiftOut = (Button) findViewById(R.id.btnRecordShiftOut);
		// btnRecordShiftOut.setOnClickListener(recordShiftOutLis());
		// btnRecordShiftOut.setOnClickListener(this);
		// btnRecordShiftOut.setOnTouchListener(recordLis());

		btnSetDetail = (Button) findViewById(R.id.btnSetDetail);
		btnTextNoteShiftOut = (Button) findViewById(R.id.btnTextNoteShiftOut);
		btnVoiceNoteShiftOut = (Button) findViewById(R.id.btnVoiceNoteShiftOut);
		btnTextNoteShiftOut.setOnClickListener(this);
		btnVoiceNoteShiftOut.setOnClickListener(this);
		// btnPlayShiftOutVoice.setOnClickListener(palyVoice());
		// btnPlayShiftOutVoice.setOnClickListener(this);
		btnSetDetail.setOnClickListener(this);
		// LinearLayout layShiftOut = (LinearLayout)
		// findViewById(R.id.layShiftOut);
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
		IntercomCtrl.open_intercom(Shift_Out_Watering.this);
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
				Shift_Out_Watering.this, filePath, shiftId, teamId, true);
		shiftOutDetailPostTask.execute();
	}

	// private OnTouchListener recordLis() {
	// return new OnTouchListener() {
	//
	// @Override
	// public boolean onTouch(View v, MotionEvent event) {
	// switch (event.getAction()) {
	// case MotionEvent.ACTION_DOWN:
	//
	// mediaplayer.reset();
	// // 弹出一个麦的View
	// mSoundRecorderWindow = new PopupWindow(getLayoutInflater()
	// .inflate(R.layout.media_ability, null),
	// LayoutParams.WRAP_CONTENT,
	// LayoutParams.WRAP_CONTENT);
	// mSoundRecorderWindow.showAtLocation(
	// findViewById(R.id.shift_out_layout),
	// Gravity.CENTER, 0, 0);
	// initPopupWindow();
	//
	// if (!isRecording) {
	// // btnRecordShiftOut.setTextColor(Color.BLACK);
	// String fileDir = getString(R.string.Shift_Record_Path);
	// Log.d(TAG, fileDir);
	// filePath = fileDir + "ShiftOut.wav";
	//
	// File fDir = new File(fileDir);
	// if (!fDir.exists()) {
	// fDir.mkdirs();
	// }
	// voicePath = filePath;
	// File fileWav = new File(filePath);
	//
	// if (fileWav.exists()) {
	// fileWav.delete();
	// Log.w(TAG, "Delete File " + fileWav.getPath());
	// }
	// // extAudioRecorder = ExtAudioRecorder
	// // .getInstanse(false);
	// extAudioRecorder = ExtAudioRecorder.getInstanse(true);
	//
	// extAudioRecorder.setOutputFile(filePath);
	// IntercomCtrl.close_intercom(Shift_Out.this);
	// try {
	// extAudioRecorder.prepare();
	// extAudioRecorder.start();
	// isRecording = true;
	// // 启动线程刷新音量变化
	// new Thread(mAudioPbRunnable).start();
	//
	// } catch (Exception e) {
	// Log.d(TAG, e.getMessage());
	// }
	// } else {
	// stopRecord();
	// isRecording = false;
	// }
	//
	// break;
	// case MotionEvent.ACTION_UP:
	// // dissmiss 麦View
	// if (mSoundRecorderWindow != null) {
	// mSoundRecorderWindow.dismiss();
	// }
	//
	// stopRecord();
	// break;
	// default:
	// break;
	// }
	// return false;
	// }
	// };
	// }

	// private long getTeamId() {
	//
	// HashMap<String, String> parmValues = new HashMap<String, String>();
	// parmValues.put("sessionId", Property.SessionId);
	// String methodPath = "WebService/spark.asmx";
	// String methodName = "GetTeamId";
	// WebServiceMananger webServiceManager = new WebServiceMananger(
	// getApplicationContext(), methodName, parmValues);
	// try {
	// String result = webServiceManager.OpenConnect(methodPath);
	// String errMsg = JsonUtil.GetJsonString(result, "Msg");
	// if (!errMsg.equals("")) {
	// return 0;
	// }
	// teamId = JsonUtil.GetJsonLong(result, "Data");
	// return teamId;
	// } catch (Exception ex) {
	// return 0;
	// }
	// }

	private void postShiftOut() {
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("shift_Id", String.valueOf(shift_Id));
		parmValues.put("saIds", saIds);
		String methodPath = "WebService/Shift.asmx";
		String methodName = "Shift2_Out_Complete";
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

	// private boolean hasPermission() {
	// HashMap<String, String> parmValues = new HashMap<String, String>();
	// parmValues.put("sessionId", Property.SessionId);
	// parmValues.put("teamId", String.valueOf(TssId));
	// parmValues.put("shiftId", "0");
	// String methodPath = "WebService/Shift.asmx";
	// String methodName = "CheckModify";
	// WebServiceMananger webServiceManager = new WebServiceMananger(
	// Shift_Out_Watering.this, methodName, parmValues);
	// try {
	// String result = webServiceManager.OpenConnect(methodPath);
	// return JsonUtil.GetJsonString(result, "Data").equals("true");
	// } catch (Exception ex) {
	// return false;
	// }
	//
	// }

	private String getVoicePath() {

		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("shiftId", "0");
		String methodPath = "WebService/Shift.asmx";
		String methodName = "GetShiftOut";
		WebServiceManager webServiceManager = new WebServiceManager(
				Shift_Out_Watering.this, methodName, parmValues);
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

			JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
			if (null != jsonArray && jsonArray.length() > 0) {
				JSONObject jsonObj = (JSONObject) jsonArray.opt(0);
				shiftId = JsonUtil.GetJsonObjLongValue(jsonObj, "Shift_Id");
				return JsonUtil.GetJsonObjStringValue(jsonObj,
						"Shift_Out_Voice");
			} else {
				return "";
			}
		} catch (Exception ex) {
			return "";
		}

	}

	private ArrayList<Shift2OutGetOpeningTask> GetAllTask4Shift(int startIndex,
			int limit)

	{

		ArrayList<Shift2OutGetOpeningTask> list = new ArrayList<Shift2OutGetOpeningTask>();
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		// parmValues.put("limit", String.valueOf(limit));
		parmValues.put("workType", String.valueOf(workType));
		// parmValues.put("start", String.valueOf(startIndex));
		String methodPath = "WebService/Shift.asmx";
		String methodName = "Shift2_Out_GetOpeningTask";
		WebServiceManager webServiceManager = new WebServiceManager(
				Shift_Out_Watering.this, methodName, parmValues);
		try {
			String result = webServiceManager.OpenConnect(methodPath);
			String errMsg = JsonUtil.GetJsonString(result, "Msg");
			// if (!errMsg.equals("")) {
			// // throw new Exception(errMsg);
			// return null;
			// }
			// JSONArray jsonArray = JsonUtil
			// .GetJsonArray(result, "Data");
			// for (int i = 0; i < jsonArray.length(); i++) {
			// JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
			// list.add(JsonUtil.GetJsonObjStringValue(jsonObj,
			// "trainNum"));
			// // TODO 员工排班的标示号。
			// TssId = JsonUtil.GetJsonObjLongValue(jsonObj, "TssId");
			// // list.add(JsonUtil.GetJsonObjStringValue(TssId1, "TssId"));
			// }
			Gson gson = new Gson();
			Type type = new TypeToken<ResultForListData<Shift2OutGetOpeningTask>>() {
			}.getType();
			ResultForListData<Shift2OutGetOpeningTask> serverData = gson
					.fromJson(result, type);
			List<Shift2OutGetOpeningTask> data = serverData.getData();
			list.addAll(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	

	private class Shift_Out_Adapter extends BaseAdapter {

		private ArrayList<Shift2OutGetOpeningTask> todoList;
		private LayoutInflater infl;
		private long teamId = 0;

		// private Handler handel = null;

		public Shift_Out_Adapter(Context context,
				ArrayList<Shift2OutGetOpeningTask> todoList, long teamId) {
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
		public Shift2OutGetOpeningTask getItem(int position) {
			if (getCount() == 0)
				return null;
			return todoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		private OnClickListener remarkLis(final String trainNum,final int position) {
			return new OnClickListener() {

				public void onClick(View v) {
					Intent intent = new Intent(Shift_Out_Watering.this,
							Shift_Out_Remark.class);
					Shift2OutGetOpeningTask item = getItem(position);
					saId = item.SaId;
					Bundle bundle = new Bundle();
					bundle.putString("TrainNo", trainNum);
					bundle.putInt("shift_Id", shift_Id);
					bundle.putInt("saId", saId);
					// bundle.putLong("TeamId", TssId);
					Button button = (Button) v;
					bundle.putString("RemarkType", button.getText().toString());
					bundle.putString("Task", "single");

					intent.putExtras(bundle);
					startActivity(intent);
				}
			};
		}

		private boolean isChoosed(Shift2OutGetOpeningTask trainNum) {
			return tempList.contains(trainNum);
		}

		public void setData(ArrayList<Shift2OutGetOpeningTask> models) {
			todoList = models;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final Shift2OutGetOpeningTask trainNum = getItem(position);
			TssId = trainNum.getTssId();
			saId = trainNum.getSaId();
			final ViewHolder holder;
			if (null == convertView) {
				// convertView =
				// infl.inflate(R.layout.shift_out_train_list_item,
				// null);
				convertView = infl.inflate(
						R.layout.shift_out_train_list_item_new, null);
				holder = new ViewHolder();

				// holder.itemView = (RelativeLayout) convertView
				// .findViewById(R.id.item_View);
				holder.rl_item = (RelativeLayout) convertView
						.findViewById(R.id.rl_item);
				holder.chkTrainNum = (ImageView) convertView
						.findViewById(R.id.chkTrainNum);
				// holder.labTrainNum = (TextView) convertView
				// .findViewById(R.id.labTrainNum);
				//
				// holder.btnRemarksButton = (Button) convertView
				// .findViewById(R.id.btnRemarks);
				holder.train_number = (TextView) convertView
						.findViewById(R.id.train_number);
				holder.plan_time = (TextView) convertView
						.findViewById(R.id.plan_time);
				holder.plan_end = (TextView) convertView
						.findViewById(R.id.plan_end);
				holder.act_start = (TextView) convertView
						.findViewById(R.id.act_start);
				holder.area = (TextView) convertView.findViewById(R.id.area);
				holder.exe_arm = (TextView) convertView
						.findViewById(R.id.exe_arm);
				holder.exe_staff = (TextView) convertView
						.findViewById(R.id.exe_staff);
				holder.task_state = (TextView) convertView
						.findViewById(R.id.task_state);
				holder.btn_text_note = (Button) convertView
						.findViewById(R.id.btn_text_note);
				holder.btn_voice_note = (Button) convertView
						.findViewById(R.id.btn_voice_note);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// holder.labTrainNum.setText(trainNum.getShift_Out_DeptName());
			holder.chkTrainNum
					.setImageResource(isChoosed(trainNum) ? R.drawable.checkbox_selected
							: R.drawable.checkbox_normal);
			// holder.chkTrainNum.setChecked(getIsSelected().get(position));
			holder.rl_item.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (ShiftIn) {
						holder.chkTrainNum
								.setImageResource(R.drawable.checkbox_selected);

						if (isChoosed(trainNum)) {
							holder.chkTrainNum
									.setImageResource(R.drawable.checkbox_normal);
							tempList.remove(trainNum);
							if (!tempList.isEmpty()) {
								btnSetDetail.setEnabled(false);
								
//								btnSetDetail.setBackgroundDrawable(getResources().getDrawable(
//										R.drawable.btn_m_selector));
							}else {
								Message message = new Message();
								message.what =11;
								mHandler.sendMessage(message);
								
									btnSetDetail.setEnabled(false);
									btnSetDetail.setBackgroundColor(0);
							}
						} else {
							holder.chkTrainNum
									.setImageResource(R.drawable.checkbox_selected);
							tempList.add(trainNum);
							if (!tempList.isEmpty()) {
								btnSetDetail.setEnabled(false);
								Message message = new Message();
								message.what = 10;
								mHandler.sendMessage(message);
//								btnSetDetail.setBackgroundDrawable(getResources().getDrawable(
//										R.drawable.btn_m_selector));
								
							}
						}
					}
				}
			});
			// holder.btnRemarksButton.setOnClickListener(remarkLis(trainNum.getShift_Out_DeptName()));

			holder.train_number.setText(trainNum.getTRNO_PRO());
			holder.plan_time.setText(trainNum.getBeginWorkTime());
			holder.plan_end.setText(trainNum.getEndWorkTime());
			holder.act_start.setText(trainNum.getRBeginWorkTime());
			holder.exe_arm.setText(trainNum.getDeptName());
			holder.area.setText(trainNum.getRWorkspaces());
			holder.exe_staff.setText(trainNum.getStaffName());
			holder.task_state.setText(trainNum.getTaskRemark());
			holder.btn_text_note.setOnClickListener(remarkLis(trainNum
					.getTRNO_PRO(),position));
			holder.btn_voice_note.setOnClickListener(remarkLis(trainNum
					.getTRNO_PRO(),position));
			if (ShiftIn) {
				holder.btn_text_note.setEnabled(true);
				holder.btn_voice_note.setEnabled(true);
				
			}else {
				holder.btn_text_note.setEnabled(false);
				holder.btn_voice_note.setEnabled(false);
			}

			return convertView;
		}
	}

	private static class ViewHolder {
		// private View itemView;
		private ImageView chkTrainNum;
		// private TextView labTrainNum;
		// private Button btnRemarksButton;
		private TextView train_number;
		private TextView plan_time;
		private TextView plan_end;
		private TextView act_start;
		private TextView area;
		private TextView exe_arm;
		private TextView exe_staff;
		private TextView task_state;
		private Button btn_text_note;
		private Button btn_voice_note;
		private RelativeLayout rl_item;
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
			IntercomCtrl.close_intercom(Shift_Out_Watering.this);
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
		AlertDialog.Builder builder = new Builder(Shift_Out_Watering.this);
		builder.setIcon(R.drawable.information);
		builder.setMessage(getString(R.string.Shift_Out_Confirm));
		builder.setTitle(getString(R.string.Prompt));
		builder.setPositiveButton(getString(R.string.Shift_Out_OK),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// ShiftOut();
						ShiftOutOk();

						showProgressDialog(R.string.GettingData);
						new Thread() {
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

	protected void ShiftOutOk() {
		btnTextNoteShiftOut.setEnabled(false);
		btnTextNoteShiftOut.setBackgroundResource(0);
		btnVoiceNoteShiftOut.setEnabled(false);
		btnVoiceNoteShiftOut.setBackgroundResource(0);
		btnSetDetail.setEnabled(true);
		btnSetDetail.setBackgroundDrawable(getResources().getDrawable(
				 R.drawable.btn_m_selector));
		btnShiftOut.setText("交班");
		ShiftIn = false;
		
		for (int i = 0; i < tempList.size(); i++) {
			if (i < tempList.size() - 1) {
				saIds = saIds + tempList.get(i).getSaId() + ",";
			} else {
				saIds = saIds + tempList.get(i).getSaId();
			}
		}
	}

	private void playVoice() {
		IntercomCtrl.close_intercom(Shift_Out_Watering.this);
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
				Builder builder = new AlertDialog.Builder(
						Shift_Out_Watering.this);
				builder.setTitle(getString(R.string.Shift_Out));
				builder.setPositiveButton(getString(R.string.OK), null);
				builder.setMessage(getString(R.string.Shift_Out_Play_Sound_Exception));
				builder.show();
			}
		}
	}

	private void upLoadVoice() {
		AlertDialog.Builder builder = new Builder(Shift_Out_Watering.this);
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

			if (ShiftIn) {

				stopRecord();
				shiftOut();
			} else {
				showProgressDialog("开始交班");
				new Thread(new Runnable() {

					@Override
					public void run() {
						shiftOutData = startShifftOut();
						if(shiftOutData != null){
							 shift_Id = shiftOutData.getShift_Id();
							Message message = new Message();
							message.what = 7;
							mHandler.sendMessage(message);
						}else{
							Message message = new Message();
							message.what = 9;
							mHandler.sendMessage(message);
						}
						 
					}
				}).start();
//				shift_out_adapter.notifyDataSetChanged();
			}
			break;
		case R.id.btnSetDetail:
			Intent intent = new Intent(Shift_Out_Watering.this,
					Shift_Out_SetDetail.class);
			intent.putExtra("shift_Id", shift_Id);
			intent.putExtra("workType", workType);
			startActivity(intent);
			break;
		case R.id.btnTextNoteShiftOut:
			Intent intent2 = new Intent(Shift_Out_Watering.this,
					Shift_Out_Remark.class);
			Bundle bundle = new Bundle();
			bundle.putInt("shift_Id", shift_Id);

			Button button = (Button) v;
			bundle.putString("RemarkType", button.getText().toString());
			bundle.putString("Task", "whole");
			intent2.putExtras(bundle);
			startActivity(intent2);
			break;
		case R.id.btnVoiceNoteShiftOut:
			Intent intent3 = new Intent(Shift_Out_Watering.this,
					Shift_Out_Remark.class);
			Bundle bundle3 = new Bundle();
			bundle3.putInt("shift_Id", shift_Id);
			Button button3 = (Button) v;
			bundle3.putString("RemarkType", button3.getText().toString());
			bundle3.putString("Task", "whole");
			intent3.putExtras(bundle3);
			startActivity(intent3);
			break;
		// case R.id.btnPlayShiftOutVoice:
		// stopRecord();
		// playVoice();
		// break;

		default:
			break;
		}

	}

	private Shift2InGetAllShiftOutData startShifftOut() {
		ArrayList<Shift2InGetAllShiftOutData> list = new ArrayList<Shift2InGetAllShiftOutData>();
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		// parmValues.put("limit", String.valueOf(limit));
		parmValues.put("workType", String.valueOf(workType));
		// parmValues.put("start", String.valueOf(startIndex));
		String methodPath = "WebService/Shift.asmx";
		String methodName = "Shift2_Out_Start";
		WebServiceManager webServiceManager = new WebServiceManager(
				Shift_Out_Watering.this, methodName, parmValues);
		try {
			String result = webServiceManager.OpenConnect(methodPath);
			Gson gson = new Gson();
			Type type = new TypeToken<Result<Shift2InGetAllShiftOutData>>() {
			}.getType();
			Result<Shift2InGetAllShiftOutData> serverData = gson.fromJson(
					result, type);
			if(serverData.isMsgType())
		    	return serverData.getData();
			else 
				return null;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	private void stopRecord() {
		if (mSoundRecorderWindow != null && mSoundRecorderWindow.isShowing()) {
			IntercomCtrl.open_intercom(Shift_Out_Watering.this);
			mSoundRecorderWindow.dismiss();
			extAudioRecorder.stop();
			extAudioRecorder.release();
		}
	}

}
