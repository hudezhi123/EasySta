package easyway.Mobile.PointTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Attach.FileUploadTask;
import easyway.Mobile.Contacts.Contacts;
import easyway.Mobile.util.DBHelper;
import easyway.Mobile.Data.Staff;
import easyway.Mobile.Data.TaskPosition;
import easyway.Mobile.Data.TaskWorkspace;
import easyway.Mobile.Media.ExtAudioRecorder;
import easyway.Mobile.Media.FileAccessEx;
import easyway.Mobile.Media.ExtAudioRecorder.State;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.CommonUtils;
import easyway.Mobile.util.DateUtil;
import easyway.Mobile.util.ImgCompress;
import easyway.Mobile.util.IntercomCtrl;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.LogUtil;
import easyway.Mobile.util.StringUtil;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.app.AlertDialog.Builder;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;

/*
 * 添加重点任务-子任务
 */
public class PTAddChildActivity extends ActivityEx implements OnClickListener {
	private final int REQUEST_CODE_SELECTCHARGE = 100;
	private final int REQUEST_CODE_ADDIMAGE = 200;
	private final int REQUEST_CODE_ADDVIDEO = 300;

	private Button btnPosition; // 岗位
	private Button btnWorkspace; // 任务区域
	private Button btnBegintime; // 开始时间
	private Button btnEndtime; // 结束时间
	private Button btnStaff; // 执行人
	private EditText edtRemark; // 备注

	private Button btnPreview;
	private Button btnNext;
	private Button btnReturn;
	private MajorMode mMajor;
	private ChildMode mChild;

	private ArrayList<TaskPosition> mPositionlist;
	private ArrayList<TaskWorkspace> mWorkspacelist;

	private Uri fileUri;
	private int mHour;
	private int mMinute;
	private String beginTime;
	private String endTime;
	private TaskPosition mPosition;
	private TaskWorkspace mWorkspace;
	private Staff mStaff;

	private Button btnAddAudio;
	private Button btnAddVideo;
	private Button btnAddPhoto;
	private ArrayList<String> attachList = new ArrayList<String>(); // 上传照片、视频、音频的地址列表
	private ListView mListView;
	private AttachAdapter mAdapter;

	private int mIndex = PTAddActivity.INDEX_FROM_LIST;

	private ExtAudioRecorder extAudioRecorder;
	private PopupWindow mSoundRecorderWindow; // 按下录音提示窗口
	private ImageView mMicImageView; // 录制音频时麦克风的Image
	private boolean mProgressBarEable = true; // 判断是否停止线程
	private int mCurrentVoice; // 获取当前音量

	private final int MSG_DATA_CHECK_FAIL = 1;
	private final int MSG_GET_PARAM_FAIL = 2;
	private final int MSG_GET_DATA_FAIL = 3;
	private final int MSG_GET_DATA_SUCC = 4;
	private final int MSG_SOUND_AMPLITUDE = 5;
	private final int MSG_UPLOAD_TASK = 6;

	@SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			closeProgressDialog();
			switch (msg.what) {
			case MSG_DATA_CHECK_FAIL:
				int resid = (Integer) msg.obj;
				showToast(resid);
				break;
			case MSG_GET_PARAM_FAIL:
				showToast(R.string.exp_getparam);
				break;
			case MSG_GET_DATA_FAIL:
				break;
			case MSG_GET_DATA_SUCC:
				break;
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
			case MSG_UPLOAD_TASK:
				LogUtil.i("mChild.UpdateState -->" + mChild.UpdateState);
				if (mChild.UpdateState == ChildMode.UPDATESTATE_ALL) {
					finish();
				} else {
					ShowContinue2Upload();
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
		setContentView(R.layout.pointtask_addchild);

		initView();

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mIndex = bundle.getInt(PTAddActivity.KEY_INDEX,
					PTAddActivity.INDEX_FROM_LIST);
			mMajor = (MajorMode) bundle
					.getSerializable(PTAddActivity.KEY_MAJOR);

			if (mMajor != null)
				LogUtil.i("ChildAdd TaskId -->" + mMajor.TaskId);
			mChild = (ChildMode) bundle
					.getSerializable(PTAddActivity.KEY_CHILD);

			if (mChild != null) {
				btnPosition.setText(mChild.PositionName);
				btnWorkspace.setText(mChild.Workspace);
				edtRemark.setText(mChild.TaskRemark);
				btnBegintime.setText(DateUtil
						.formatDate(mChild.BeginWorkTime, DateUtil.HH_MM));
				btnEndtime.setText(DateUtil
						.formatDate(mChild.EndWorkTime,DateUtil.HH_MM));
				btnStaff.setText(mChild.StaffName);

				attachList = mChild.AttachList;
				mAdapter.notifyDataSetChanged();

				beginTime = mChild.BeginWorkTime;
				endTime = mChild.EndWorkTime;

				mPosition = new TaskPosition();
				mPosition.PositionName = mChild.PositionName;
				mPosition.PId = mChild.PId;

				mWorkspace = new TaskWorkspace();
				mWorkspace.Workspace = mChild.Workspace;
				mWorkspace.TwId = mChild.TwId;

				mStaff = new Staff();
				mStaff.StaffId = mChild.StaffId;
				mStaff.StaffName = mChild.StaffName;
			}
		}
		Calendar c = Calendar.getInstance();
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinute = c.get(Calendar.MINUTE);

		if (mIndex == PTAddActivity.INDEX_FROM_EDIT) {
			btnPreview.setVisibility(View.GONE);
			btnNext.setText(R.string.task_save);
			btnReturn.setVisibility(View.VISIBLE);
		}

		getData(true);

		if (mPosition != null)
			getData(false);
	}

	// 从数据库中获取岗位、任务区域信息
	private void getData(final boolean IsPosition) {
		showProgressDialog(R.string.GettingData);
		new Thread() {
			public void run() {
				boolean ret = false;
				if (IsPosition)
					ret = getTaskPosition(); // 获取岗位
				else
					ret = getTaskWorkspace(); // 获取任务区域

				if (ret)
					myhandle.sendEmptyMessage(MSG_GET_DATA_SUCC);
				else
					myhandle.sendEmptyMessage(MSG_GET_DATA_FAIL);
			}
		}.start();
	}

	private void initView() {
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.task_title_addchild);

		btnReturn = (Button) findViewById(R.id.btnReturn);
		btnReturn.setVisibility(View.INVISIBLE);
		btnReturn.setOnClickListener(this);

		// 岗位
		btnPosition = (Button) findViewById(R.id.btnPosition);
		btnPosition.setOnClickListener(this);

		// 任务区域
		btnWorkspace = (Button) findViewById(R.id.btnWorkspace);
		btnWorkspace.setOnClickListener(this);

		// 备注
		edtRemark = (EditText) findViewById(R.id.edtRemark);

		// 开始时间
		btnBegintime = (Button) findViewById(R.id.btnBegintime);
		btnBegintime.setOnClickListener(this);

		// 结束时间
		btnEndtime = (Button) findViewById(R.id.btnEndtime);
		btnEndtime.setOnClickListener(this);

		// 执行人
		btnStaff = (Button) findViewById(R.id.btnStaff);
		btnStaff.setOnClickListener(this);

		// 上一步
		btnPreview = (Button) findViewById(R.id.btnPreview);
		btnPreview.setOnClickListener(this);
		btnPreview.setWidth(Property.screenwidth / 2);

		// 下一步
		btnNext = (Button) findViewById(R.id.btnNext);
		btnNext.setOnClickListener(this);
		btnNext.setWidth(Property.screenwidth / 2);

		mListView = (ListView) findViewById(R.id.ListAttach);
		mAdapter = new AttachAdapter(this);
		mListView.setAdapter(mAdapter);

		// 录音
		btnAddAudio = (Button) findViewById(R.id.btnAddAudio);
		btnAddAudio.setOnClickListener(this);

		// 拍照
		btnAddVideo = (Button) findViewById(R.id.btnAddVideo);
		btnAddVideo.setOnClickListener(this);

		// 视频
		btnAddPhoto = (Button) findViewById(R.id.btnAddPhoto);
		btnAddPhoto.setOnClickListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		if (extAudioRecorder != null) {
			stopRecord();
			extAudioRecorder = null;
			mProgressBarEable = false;
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnReturn:
			finish();
			break;
		case R.id.btnPosition:
			showSelectPositionDlg();
			break;
		case R.id.btnWorkspace:
			if (mPosition == null)
				showToast(R.string.task_notify_inputposition);
			else {
				if (mWorkspacelist != null)
					showSelectWorkspaceDlg();
			}
			break;
		case R.id.btnBegintime:
			if (beginTime != null && beginTime.length() != 0) {
				String[] strs = beginTime.split(":");
				if (strs != null && strs.length == 3) {
					mHour = Integer.parseInt(strs[0]);
					mMinute = Integer.parseInt(strs[1]);
				}
			}

			new TimePickerDialog(this, onTimeSetLis(btnBegintime), mHour,
					mMinute, false).show();
			break;
		case R.id.btnEndtime:
			if (endTime != null && endTime.length() != 0) {
				String[] strs = endTime.split(":");
				if (strs != null && strs.length == 3) {
					mHour = Integer.parseInt(strs[0]);
					mMinute = Integer.parseInt(strs[1]);
				}
			}
			new TimePickerDialog(this, onTimeSetLis(btnEndtime), mHour,
					mMinute, false).show();
			break;
		case R.id.btnStaff:
			Intent intent = new Intent(PTAddChildActivity.this, Contacts.class);
			intent.putExtra(Contacts.KEY_FLAG, Contacts.FLAG_POINTTASK);
			startActivityForResult(intent, REQUEST_CODE_SELECTCHARGE);
			break;
		case R.id.btnPreview:
			if (mIndex == PTAddActivity.INDEX_FROM_MAJOR) {
				Intent intentpre = new Intent(PTAddChildActivity.this,
						PTAddActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(PTAddActivity.KEY_MAJOR, mMajor);
				intentpre.putExtras(bundle);
				startActivity(intentpre);
			}

			finish();
			break;
		case R.id.btnNext:
			if (checkData()) {
				if (mChild == null)
					mChild = new ChildMode();

				if (mPosition != null) {
					mChild.PId = mPosition.PId;
					mChild.PositionName = mPosition.PositionName;
				}

				if (mWorkspace != null) {
					mChild.TwId = mWorkspace.TwId;
					mChild.Workspace = mWorkspace.Workspace;
				}
				mChild.BeginWorkTime = beginTime;
				mChild.EndWorkTime = endTime;

				if (mStaff != null) {
					mChild.StaffId = mStaff.StaffId;
					mChild.StaffName = mStaff.StaffName;
				}

				mChild.AttachList = attachList;
				mChild.TaskRemark = edtRemark.getText().toString().trim();

				if (mIndex == PTAddActivity.INDEX_FROM_EDIT) {
					Post2Server();
				} else if (mIndex == PTAddActivity.INDEX_FROM_MAJOR) {
					Intent newintent = new Intent(PTAddChildActivity.this,
							PTAddListActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable(PTAddActivity.KEY_CHILD, mChild);
					bundle.putSerializable(PTAddActivity.KEY_MAJOR, mMajor);
					newintent.putExtras(bundle);
					startActivity(newintent);
					finish();
				} else {
					Intent intentback = new Intent();
					Bundle bundle = new Bundle();
					bundle.putInt(PTAddActivity.KEY_INDEX, mIndex);
					bundle.putSerializable(PTAddActivity.KEY_CHILD, mChild);
					intentback.putExtras(bundle);
					setResult(RESULT_OK, intentback);
					finish();
				}
			}
			break;
		case R.id.btnAddAudio: // 添加语音
			if (mSoundRecorderWindow != null
					&& mSoundRecorderWindow.isShowing()) {
				stopRecord();
			} else {
				recordVoice();
			}
			break;
		case R.id.btnAddPhoto: // 添加照片
			stopRecord();
			openCamera();
			break;
		case R.id.btnAddVideo: // 添加视频
			stopRecord();
			openMp4();
			break;
		default:
			break;
		}
	}

	// 时间控件
	private OnTimeSetListener onTimeSetLis(final Button btn) {
		OnTimeSetListener listener = new OnTimeSetListener() {
			@SuppressLint("SimpleDateFormat")
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				mHour = hourOfDay;
				mMinute = minute;
				String str = new StringBuilder().append(pad(mHour)).append(":")
						.append(pad(mMinute)).toString();
				btn.setText(str);

				if (btn.getId() == R.id.btnBegintime) {
					beginTime = str + ":00";
				} else if (btn.getId() == R.id.btnEndtime) {
					endTime = str + ":00";
				}
			}
		};
		return listener;
	}

	private String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	// 数据检测
	private boolean checkData() {
		// 岗位检测
		if (mPosition == null) {
			Message msg = new Message();
			msg.what = MSG_DATA_CHECK_FAIL;
			msg.obj = R.string.task_notify_inputposition;
			myhandle.sendMessage(msg);
			return false;
		}

		// 任务区域
		if (mWorkspace == null) {
			Message msg = new Message();
			msg.what = MSG_DATA_CHECK_FAIL;
			msg.obj = R.string.task_notify_inputworkspace;
			myhandle.sendMessage(msg);
			return false;
		}

		// 开始时间检测
		if (beginTime == null || beginTime.trim().length() == 0) {
			Message msg = new Message();
			msg.what = MSG_DATA_CHECK_FAIL;
			msg.obj = R.string.task_notify_inputbegintime;
			myhandle.sendMessage(msg);
			return false;
		}

		// 结束时间检测
		if (endTime == null || endTime.trim().length() == 0) {
			Message msg = new Message();
			msg.what = MSG_DATA_CHECK_FAIL;
			msg.obj = R.string.task_notify_inputendtime;
			myhandle.sendMessage(msg);
			return false;
		}

		// 开始时间与结束时间检测
		if (beginTime.compareTo(endTime) >= 0) {
			Message msg = new Message();
			msg.what = MSG_DATA_CHECK_FAIL;
			msg.obj = R.string.task_notify_invalidtime;
			myhandle.sendMessage(msg);
			return false;
		}

		// 任务区域
		if (mStaff == null) {
			Message msg = new Message();
			msg.what = MSG_DATA_CHECK_FAIL;
			msg.obj = R.string.task_notify_inputstaff;
			myhandle.sendMessage(msg);
			return false;
		}

		return true;
	}

	// 获取工作岗位
	private boolean getTaskPosition() {
		if (Property.OwnStation == null)
			return false;

		if (mPositionlist == null)
			mPositionlist = new ArrayList<TaskPosition>();
		else
			mPositionlist.clear();

		DBHelper dbHelper = new DBHelper(PTAddChildActivity.this);
		String[] columns = { DBHelper.POSITON_PID, DBHelper.POSITON_NAME };
		Cursor cursor = null;
		try {
			cursor = dbHelper.exeSql(DBHelper.POSITION_TABLE_NAME, columns,
					DBHelper.POSITON_STATIONCODE + " = '"
							+ Property.OwnStation.Code + "'", null, null, null,
					DBHelper.POSITON_PID);
			if (null != cursor && cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					TaskPosition obj = new TaskPosition();
					obj.PId = cursor.getLong(cursor
							.getColumnIndex(DBHelper.POSITON_PID));
					obj.PositionName = cursor.getString(cursor
							.getColumnIndex(DBHelper.POSITON_NAME));

					mPositionlist.add(obj);
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbHelper.closeCursor(cursor);
			dbHelper.close();
		}

		if (mPositionlist == null || mPositionlist.size() == 0)
			return false;
		else
			return true;
	}

	// 获取任务区域
	private boolean getTaskWorkspace() {
		if (Property.OwnStation == null)
			return false;

		if (mPosition == null)
			return false;

		if (mWorkspacelist == null)
			mWorkspacelist = new ArrayList<TaskWorkspace>();
		else
			mWorkspacelist.clear();

		DBHelper dbHelper = new DBHelper(PTAddChildActivity.this);
		Cursor cursor = null;
		try {
			String sql = "select distinct a.TwId, a.Workspace from Worksapce a, PWRS b "
					+ " where a.StationCode = '"
					+ Property.OwnStation.Code
					+ "' and a.TwId = b.TwId and  b.PId = '"
					+ mPosition.PId
					+ "' order by a.TwId;";

			LogUtil.i("sql -->  " + sql);
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);

			if (null != cursor && cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					TaskWorkspace obj = new TaskWorkspace();
					obj.TwId = cursor.getLong(cursor
							.getColumnIndex(DBHelper.WORKSPACE_TWID));
					obj.Workspace = cursor.getString(cursor
							.getColumnIndex(DBHelper.WORKSPACE_NAME));

					mWorkspacelist.add(obj);
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbHelper.closeCursor(cursor);
			dbHelper.close();
		}

		if (mWorkspacelist == null || mWorkspacelist.size() == 0)
			return false;
		else
			return true;
	}

	// 选择岗位
	private void showSelectPositionDlg() {
		if (null != mPositionlist) {
			String[] m = new String[mPositionlist.size()];
			for (int i = 0; i < mPositionlist.size(); i++) {
				m[i] = mPositionlist.get(i).PositionName;
			}

			AlertDialog dlg = new AlertDialog.Builder(PTAddChildActivity.this)
					.setTitle("")
					.setItems(m, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							if (item < mPositionlist.size()) {
								if (mPosition != null
										&& mPosition.PId != mPositionlist
												.get(item).PId) {
									mWorkspace = null;
									btnWorkspace
											.setText(R.string.task_input_workspace);
								}

								mPosition = mPositionlist.get(item);
								btnPosition.setText(mPositionlist.get(item).PositionName);

								getData(false);
							}
						}
					}).create();
			dlg.show();
		}
	};

	// 选择任务区域
	private void showSelectWorkspaceDlg() {
		if (null != mWorkspacelist) {
			String[] m = new String[mWorkspacelist.size()];
			for (int i = 0; i < mWorkspacelist.size(); i++) {
				m[i] = mWorkspacelist.get(i).Workspace;
			}

			AlertDialog dlg = new AlertDialog.Builder(PTAddChildActivity.this)
					.setTitle("")
					.setItems(m, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							if (item < mWorkspacelist.size()) {
								mWorkspace = mWorkspacelist.get(item);
								btnWorkspace.setText(mWorkspacelist.get(item).Workspace);
							}
						}
					}).create();
			dlg.show();
		}
	};

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

	/** 添加附件到list */
	private void AddAttachList(String attachUrl) {
		if (attachList == null)
			attachList = new ArrayList<String>();

		if (!attachList.contains(attachUrl)) {
			attachList.add(0, attachUrl);
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_SELECTCHARGE:
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					mStaff = (Staff) bundle.getSerializable(Contacts.KEY_STAFF);
					if (mStaff != null)
						btnStaff.setText(mStaff.StaffName);
				}
				break;
			case REQUEST_CODE_ADDVIDEO:
			case REQUEST_CODE_ADDIMAGE:
				String filePath = fileUri.getPath();
				if (requestCode == REQUEST_CODE_ADDIMAGE)
					ImgCompress.Compress(filePath, filePath, 800, 600, 80);
				File f = new File(filePath);
				if (!f.exists()) {
					return;
				}
				if (f.isDirectory()) {
					return;
				}
				if (attachList == null)
					attachList = new ArrayList<String>();

				if (attachList.contains(filePath)) {
					return;
				}

				AddAttachList(filePath);
				break;
			default:
				break;
			}
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
	 * 音频子线程 系统音量变化是否会发出广播，按广播接收方式进行处理
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
		fileUri = CommonUtils.getOutputMediaFileUri(PTAddChildActivity.this,
				CommonUtils.MEDIA_TYPE_AUDIO);
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
			IntercomCtrl.close_intercom(PTAddChildActivity.this);
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

	private void openMp4() {
		boolean hasCarema = CommonUtils
				.checkCameraHardware(PTAddChildActivity.this);
		if (!hasCarema) {
			AlertDialog.Builder build = new AlertDialog.Builder(
					PTAddChildActivity.this);

			build.setTitle(R.string.Prompt);
			build.setIcon(R.drawable.information);
			build.setPositiveButton(R.string.OK, null);
			build.setMessage(R.string.NonCarema);
			build.show();
			return;
		}
		// 创建拍照Intent并将控制权返回给调用的程序
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

		fileUri = CommonUtils.getOutputMediaFileUri(PTAddChildActivity.this,
				CommonUtils.MEDIA_TYPE_VIDEO);
		if (fileUri == null) {
			return;
		}

		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		// 启动图像捕获Intent
		startActivityForResult(intent, REQUEST_CODE_ADDVIDEO);
	}

	private void openCamera() {
		boolean hasCarema = CommonUtils
				.checkCameraHardware(PTAddChildActivity.this);
		if (!hasCarema) {
			AlertDialog.Builder build = new AlertDialog.Builder(
					PTAddChildActivity.this);

			build.setTitle(R.string.Prompt);
			build.setIcon(R.drawable.information);
			build.setPositiveButton(R.string.OK, null);
			build.setMessage(R.string.NonCarema);
			build.show();
			return;
		}
		// 创建拍照Intent并将控制权返回给调用的程序
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		// 创建保存图片的文件
		fileUri = CommonUtils.getOutputMediaFileUri(PTAddChildActivity.this,
				CommonUtils.MEDIA_TYPE_IMAGE);
		if (fileUri == null) {
			return;
		}

		// 设置图片文件名
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

		// 启动图像捕获Intent
		startActivityForResult(intent, REQUEST_CODE_ADDIMAGE);
	}

	private void stopRecord() {
		// dissmiss 麦View
		if (extAudioRecorder == null)
			return;

		if (extAudioRecorder.getState().equals(State.RECORDING)) {
			IntercomCtrl.open_intercom(PTAddChildActivity.this);
			extAudioRecorder.stop();
			extAudioRecorder.release();

			mSoundRecorderWindow.dismiss();
			AddAttachList(fileUri.getPath());
		}
	}

	// 上传任务
	private void Post2Server() {
		showProgressDialog(R.string.task_notify_uploading);
		new Thread() {
			public void run() {
				if (mChild.UpdateState == ChildMode.UPDATESTATE_ALL) { // 已经成功上传
					// do nothing
				}

				if (mChild.UpdateState == ChildMode.UPDATESTATE_PART) { // 部分成功上传
					if (mChild.SaId == ChildMode.SAID_INVALID) { // 若SaId无效，则判定为未上传
						mChild.UpdateState = ChildMode.UPDATESTATE_NOT;
					} else {
						mChild.IsAttachUpdateList = UpdateAttach(); // 上传附件

						if (mChild.IsAttachUpdateList == null
								|| mChild.IsAttachUpdateList.size() == 0) { // 若无附件则判定为上传成功
							mChild.UpdateState = ChildMode.UPDATESTATE_ALL;
						} else if (!mChild.IsAttachUpdateList.contains(false)) { // 附件全部上传成功
							mChild.UpdateState = ChildMode.UPDATESTATE_ALL;
						}
					}
				}

				if (mChild.UpdateState == ChildMode.UPDATESTATE_NOT) { // 还未上传
					boolean ret = UpdateTask();

					LogUtil.i(" UpdateTask ret -->" + ret);
					if (ret) {
						mChild.UpdateState = ChildMode.UPDATESTATE_PART;
						if (mChild.AttachList == null
								|| mChild.AttachList.size() == 0) { // 不存在附件，则判定为上传成功
							mChild.UpdateState = ChildMode.UPDATESTATE_ALL;
						} else {
							mChild.IsAttachUpdateList = UpdateAttach();
							if (mChild.IsAttachUpdateList == null
									|| mChild.IsAttachUpdateList.size() == 0) { // 若无附件则判定为上传成功
								mChild.UpdateState = ChildMode.UPDATESTATE_ALL;
							} else if (!mChild.IsAttachUpdateList
									.contains(false)) {// 附件全部上传成功
								mChild.UpdateState = ChildMode.UPDATESTATE_ALL;
							} else{
								//已保存的子任务判定为上传成功
								mChild.UpdateState = ChildMode.UPDATESTATE_ALL;								
							}
						}
					}
				}

				myhandle.sendEmptyMessage(MSG_UPLOAD_TASK);

			}
		}.start();
	}

	private String setTaskData(ChildMode child) {
		if (child == null)
			return "";

		JSONObject object = new JSONObject();
		try {
			object.put("BeginWorkTime", child.BeginWorkTime);
			object.put("EndWorkTime", child.EndWorkTime);
			object.put("StaffId", child.StaffId);
			object.put("StaffName", child.StaffName);
			object.put("PId", child.PId);
			object.put("PositionName", child.PositionName);
			object.put("TwId", child.TwId);
			object.put("Workspace", child.Workspace);
			object.put("TaskRemark", child.TaskRemark);
		} catch (Exception e) {

		}
		return object.toString();
	}

	// 上传子任务
	private boolean UpdateTask() {
		String valueStr = setTaskData(mChild);

		HashMap<String, String> paramValues = new HashMap<String, String>();
		paramValues.put("sessionId", Property.SessionId);
		paramValues.put("valueStr", StringUtil.Encode(valueStr, true));
		paramValues.put("taskId", String.valueOf(mMajor.TaskId));
		paramValues.put("saId", String.valueOf(mChild.SaId));
		if (Property.OwnStation != null)
			paramValues.put("stationCode", Property.OwnStation.Code);
		String methodPath = Constant.MP_TASK;
		String methodName = Constant.MN_SAVE_TASKITEM;
		WebServiceManager webServiceManager = new WebServiceManager(
				getApplicationContext(), methodName, paramValues);
		String result = webServiceManager.OpenConnect(methodPath);

		if (result == null || result.equals("")) {
			return false;
		}

		int Code = JsonUtil.GetJsonInt(result, "Code");

		switch (Code) {
		case Constant.NORMAL:
			if (mChild.SaId != ChildMode.SAID_INVALID)
				return true;

			JSONObject jsonObj = (JSONObject) JsonUtil.GetJsonObj(result,
					"Data");
			if (jsonObj == null)
				break;

			mChild.SaId = JsonUtil.GetJsonObjLongValue(jsonObj, "SaId");
			return true;
		case Constant.EXCEPTION:
		default:
			break;
		}

		return false;
	}

	// 上传附件
	private ArrayList<Boolean> UpdateAttach() {
		if (mChild == null)
			return null;

		if (mChild.AttachList == null || mChild.AttachList.size() == 0)
			return null;

		ArrayList<Boolean> list;
		if (mChild.IsAttachUpdateList == null)
			list = new ArrayList<Boolean>();
		else
			list = mChild.IsAttachUpdateList;

		for (int i = 0; i < mChild.AttachList.size(); i++) {
			String filePath = mChild.AttachList.get(i);
			if (i < list.size()) {

			} else {
				list.add(false);
			}

			if (list.get(i))
				continue;

			FileAccessEx fileAccessEx;
			try {
				fileAccessEx = new FileAccessEx(filePath, 0);
				Long fileLength = fileAccessEx.getFileLength();

				byte[] buffer = new byte[FileAccessEx.PIECE_LENGHT];
				FileAccessEx.Detail detail;
				long nRead = 0l;
				long nStart = 0l;
				boolean completed = false;

				File file = new File(filePath);
				String fileName = file.getName();

				int postTimes = 0;
				JSONObject jsonP = new JSONObject();
				try {
					jsonP.put("Category", FileUploadTask.CATEGORY_TASK);
					jsonP.put("Id", mChild.SaId);
					jsonP.put("FileName", fileName);
					jsonP.put("DeptId", Property.DeptId);
					jsonP.put("StaffId", Property.StaffId);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				while (nStart < fileLength) {
					detail = fileAccessEx.getContent(nStart);
					nRead = detail.length;
					buffer = detail.b;

					nStart += nRead;

					if (nStart < fileLength) {
						completed = false;
					} else {
						completed = true;
					}

					HashMap<String, String> parmValues = new HashMap<String, String>();
					parmValues.put("sessionId", Property.SessionId);
					parmValues.put("position", String.valueOf(postTimes));
					parmValues.put("completed", String.valueOf(completed));
					parmValues.put("jsonValues", jsonP.toString());
					parmValues.put("fileStreamString", Base64.encodeToString(
							buffer, 0, (int) nRead, Base64.DEFAULT));

					String methodPath = Constant.MP_ATTACHMENT;
					String methodName = Constant.MN_POST_ATTACH;

					WebServiceManager webServiceManager = new WebServiceManager(
							PTAddChildActivity.this, methodName, parmValues);
					String result = webServiceManager.OpenConnect(methodPath);
					if (result == null || result.equals("")) {
						break;
					}

					int code = JsonUtil.GetJsonInt(result, "Code");
					if (code != Constant.NORMAL) {
						break;
					}

					postTimes++;
					buffer = null;

					if (completed) {
						list.set(i, true);
						LogUtil.i(fileName + " upload succeed!!!");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
		return list;
	}

	// 重传
	private void ShowContinue2Upload() {
		AlertDialog.Builder builder = new Builder(PTAddChildActivity.this);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.Prompt);
		builder.setCancelable(false);

		builder.setMessage(R.string.task_update_fail);
		// 重传
		builder.setPositiveButton(R.string.task_update_retry,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Post2Server();
					}
				});

		// 取消
		builder.setNegativeButton(R.string.task_update_cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						finish();
					}
				});
		builder.create().show();
	}
}
