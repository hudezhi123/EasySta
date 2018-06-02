package easyway.Mobile.Caution;

import java.io.File;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import easyway.Mobile.ActivityEx;
import easyway.Mobile.R;
import easyway.Mobile.Media.ExtAudioRecorder;
import easyway.Mobile.Media.ExtAudioRecorder.State;
import easyway.Mobile.util.CommonUtils;
import easyway.Mobile.util.DateUtil;
import easyway.Mobile.util.ImgCompress;
import easyway.Mobile.util.IntercomCtrl;

// 记事本编辑
public class CautionEditActivity extends ActivityEx implements OnClickListener {
	public static final String KEY_CAUTIONID = "ID";
	public static final String KEY_EDIT = "edit";
	
	private RadioButton radLevelHigh;		// 优先级
	private RadioButton radLevelNormal;
	private RadioButton radLevelLow;
	private Button btnDate; // 日期
	private Button btnTime; // 时间
	private Button btnAudio; // 录音
	private Button btnDelAudio; // 删除录音
	private Button btnPhoto; // 图片
	private Button btnDelPhoto; // 删除图片
	private Button btnSave; // 保存
	private Button btnValid; // 启用
	private TextView txtAudio; // 音频文件地址
	private TextView txtPhoto; // 图片文件地址
	private EditText edtContent; // 内容
	private EditText edtTitle; // 标题
	
	
	private RelativeLayout LayoutAudio;
	private RelativeLayout LayoutPhoto;

	private Caution mCaution;
	private boolean mIsEdit = false; // 是否是编辑

	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMinute;

	private Uri fileUri;
	private ExtAudioRecorder extAudioRecorder;
	private PopupWindow mSoundRecorderWindow; // 按下录音提示窗口
	private ImageView mMicImageView; // 录制音频时麦克风的Image
	private boolean mProgressBarEable = true; // 判断是否停止线程
	private int mCurrentVoice; // 获取当前音量

	private final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1; // 从相机添加图片

	private final int MSG_DATA_CHECK_FAIL = 1;
	private final int MSG_SOUND_AMPLITUDE = 2;

	@SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			closeProgressDialog();
			switch (msg.what) {
			case MSG_DATA_CHECK_FAIL:
				showToast(errMsg);
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
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.caution_edit);
		initView();

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			long id  = bundle.getLong(KEY_CAUTIONID);
			mCaution = Caution.GetById(CautionEditActivity.this, id);
			mIsEdit = bundle.getBoolean(KEY_EDIT, false);
		}

		if (mCaution == null) {
			mCaution = new Caution();

			Calendar c = Calendar.getInstance();
			mYear = c.get(Calendar.YEAR);
			mMonth = c.get(Calendar.MONTH);
			mDay = c.get(Calendar.DAY_OF_MONTH);
			mHour = c.get(Calendar.HOUR_OF_DAY);
			mMinute = c.get(Calendar.MINUTE);
		} else {
			String[] strs = mCaution.date.split("-");
			if (strs == null || strs.length != 3) {
				Calendar c = Calendar.getInstance();
				mYear = c.get(Calendar.YEAR);
				mMonth = c.get(Calendar.MONTH);
				mDay = c.get(Calendar.DAY_OF_MONTH);
			} else {
				mYear = Integer.parseInt(strs[0]);
				mMonth = Integer.parseInt(strs[1]) - 1;
				mDay = Integer.parseInt(strs[2]);
			}

			String[] strtimes = mCaution.time.split(":");
			if (strtimes == null || strtimes.length != 3) {
				Calendar c = Calendar.getInstance();
				mHour = c.get(Calendar.HOUR_OF_DAY);
				mMinute = c.get(Calendar.MINUTE);
			} else {
				mHour = Integer.parseInt(strtimes[0]);
				mMinute = Integer.parseInt(strtimes[1]);
			}

			edtTitle.setText(mCaution.title);		// 标题
			edtContent.setText(mCaution.content);	// 内容
			// 声音附件
			if (mCaution.attachaudio != null
					&& mCaution.attachaudio.length() != 0) {
				txtAudio.setText(mCaution.attachaudio);
				LayoutAudio.setVisibility(View.VISIBLE);
			}

			// 图片附件
			if (mCaution.attachphoto != null
					&& mCaution.attachphoto.length() != 0) {
				txtPhoto.setText(mCaution.attachphoto);
				LayoutPhoto.setVisibility(View.VISIBLE);
			}

			// 优先级
			switch (mCaution.level) {
			case Caution.LEVEL_HIGH:
				radLevelHigh.setChecked(true);
				break;
			case Caution.LEVEL_NORMAL:
				radLevelNormal.setChecked(true);
				break;
			case Caution.LEVEL_LOW:
				radLevelLow.setChecked(true);
				break;
			default:
				radLevelNormal.setChecked(true);
				break;
			}
		}

		mCaution.time = new StringBuilder().append(pad(mHour)).append(":")
				.append(pad(mMinute)).append(":00").toString();

		btnTime.setText(DateUtil.formatDate(mCaution.time,DateUtil.HH_MM));

		mCaution.date = new StringBuilder().append(mYear).append("-")
				.append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
				.append("-").append((mDay < 10) ? "0" + mDay : mDay).toString();
		btnDate.setText(mCaution.date);
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

	private void initView() {
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.Caution_Title);
		
		radLevelHigh = (RadioButton) findViewById(R.id.radLevelHigh);
		radLevelNormal = (RadioButton) findViewById(R.id.radLevelNormal);
		radLevelLow = (RadioButton) findViewById(R.id.radLevelLow);

		
		btnDate = (Button) findViewById(R.id.btnDate);
		btnTime = (Button) findViewById(R.id.btnTime);
		btnAudio = (Button) findViewById(R.id.btnAudio);
		btnDelAudio = (Button) findViewById(R.id.btnDelAudio);
		btnPhoto = (Button) findViewById(R.id.btnPhoto);
		btnDelPhoto = (Button) findViewById(R.id.btnDelPhoto);
		btnSave = (Button) findViewById(R.id.btnSave);
		btnValid = (Button) findViewById(R.id.btnValid);

		txtAudio = (TextView) findViewById(R.id.txtAudio);
		txtPhoto = (TextView) findViewById(R.id.txtPhoto);

		edtContent = (EditText) findViewById(R.id.edtContent);
		edtTitle = (EditText) findViewById(R.id.edtTitle);

		LayoutAudio = (RelativeLayout) findViewById(R.id.LayoutAudio);
		LayoutPhoto = (RelativeLayout) findViewById(R.id.LayoutPhoto);
		
		btnDate.setOnClickListener(this);
		btnTime.setOnClickListener(this);
		btnAudio.setOnClickListener(this);
		btnDelAudio.setOnClickListener(this);
		btnPhoto.setOnClickListener(this);
		btnDelPhoto.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		btnValid.setOnClickListener(this);
		txtAudio.setOnClickListener(this);
		txtPhoto.setOnClickListener(this);
		
		btnSave.setVisibility(View.GONE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED) {
			// do nothing
		} else if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE: // 拍照
				String filePath = fileUri.getPath();
				ImgCompress.Compress(filePath, filePath,
						800, 600, 80);
				File f = new File(filePath);
				if (!f.exists()) {
					return;
				}
				if (f.isDirectory()) {
					return;
				}

				mCaution.attachphoto = CommonUtils.getFileNameFromPath(filePath);
				txtPhoto.setText(mCaution.attachphoto);
				LayoutPhoto.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnDate:
			new DatePickerDialog(CautionEditActivity.this, date, mYear, mMonth,
					mDay).show();
			break;
		case R.id.btnTime:
			new TimePickerDialog(CautionEditActivity.this, time, mHour,
					mMinute, false).show();
			break;
		case R.id.btnAudio:
			if (mSoundRecorderWindow != null
					&& mSoundRecorderWindow.isShowing()) {
				stopRecord();
			} else {
				recordVoice();
			}
			break;
		case R.id.btnDelAudio:
			mCaution.attachaudio = "";
			txtAudio.setText("");
			LayoutAudio.setVisibility(View.GONE);
			break;
		case R.id.btnPhoto:
			stopRecord();
			openCamera();
			break;
		case R.id.btnDelPhoto:
			mCaution.attachphoto = "";
			txtPhoto.setText("");
			LayoutPhoto.setVisibility(View.GONE);
			break;
		case R.id.btnSave:
		case R.id.btnValid:
			if (DataCheck()) {
				String title = edtTitle.getText().toString().trim();
				mCaution.title = title;
				
				String content = edtContent.getText().toString().trim();
				mCaution.content = content;
				
				if (radLevelHigh.isChecked())
					mCaution.level = Caution.LEVEL_HIGH;
				if (radLevelNormal.isChecked())
					mCaution.level = Caution.LEVEL_NORMAL;
				if (radLevelLow.isChecked())
					mCaution.level = Caution.LEVEL_LOW;
				
				if (v.getId() == R.id.btnSave)
					mCaution.valid = Caution.VALID_DRAFT;
				else
					mCaution.valid = Caution.VALID_ON;
				
				if (mIsEdit) {
					Caution.UpdateCaution(CautionEditActivity.this, mCaution);
					finish();
				} else {
					long id = Caution.AddCaution(CautionEditActivity.this, mCaution);
					if (id > 0)
						finish();
				}
					
			} else{
				myhandle.sendEmptyMessage(MSG_DATA_CHECK_FAIL);
			}
				
			break;
		case R.id.txtAudio:
			break;
		case R.id.txtPhoto:
			break;

		}
	};

	// 设置日期控件
	DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			mCaution.date = new StringBuilder()
					.append(mYear)
					.append("-")
					.append((mMonth + 1) < 10 ? "0" + (mMonth + 1)
							: (mMonth + 1)).append("-")
					.append((mDay < 10) ? "0" + mDay : mDay).toString();
			btnDate.setText(mCaution.date);
		}
	};

	// 时间控件
	TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
		@SuppressLint("SimpleDateFormat")
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mHour = hourOfDay;
			mMinute = minute;
			mCaution.time = new StringBuilder().append(pad(mHour)).append(":")
					.append(pad(mMinute)).append(":00").toString();

			btnTime.setText(DateUtil.formatDate(mCaution.time,DateUtil.HH_MM));
		}
	};

	private String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	// 录音
	private void recordVoice() {
		fileUri = CommonUtils.getOutputMediaFileUri(CautionEditActivity.this,
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
			IntercomCtrl.close_intercom(CautionEditActivity.this);
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

	// 停止录音
	private void stopRecord() {
		// dissmiss 麦View
		if (extAudioRecorder == null)
			return;

		if (extAudioRecorder.getState().equals(State.RECORDING)) {
			IntercomCtrl.open_intercom(CautionEditActivity.this);
			extAudioRecorder.stop();
			extAudioRecorder.release();
			mSoundRecorderWindow.dismiss();

			mCaution.attachaudio = CommonUtils.getFileNameFromPath(fileUri
					.getPath());
			txtAudio.setText(mCaution.attachaudio);
			LayoutAudio.setVisibility(View.VISIBLE);
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

	// 拍照
	private void openCamera() {
		boolean hasCarema = CommonUtils.checkCameraHardware(CautionEditActivity.this);
		if (!hasCarema) {
			AlertDialog.Builder build = new AlertDialog.Builder(CautionEditActivity.this);

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
		fileUri = CommonUtils.getOutputMediaFileUri(CautionEditActivity.this,
				CommonUtils.MEDIA_TYPE_IMAGE);
		if (fileUri == null) {
			return;
		}
		
		// 设置图片文件名
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

		// 启动图像捕获Intent
		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}
	
	// 数据检测
	private boolean DataCheck() {
		boolean ret = true;
		
		String title = edtTitle.getText().toString().trim();
		if (title == null || title.length() == 0) {
			errMsg = getString(R.string.Caution_InputTilte);
			ret = false;
		}
		
		return ret;
	}

}
