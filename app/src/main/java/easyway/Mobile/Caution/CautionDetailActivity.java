package easyway.Mobile.Caution;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import easyway.Mobile.ActivityEx;
import easyway.Mobile.R;
import easyway.Mobile.util.CommonUtils;
import easyway.Mobile.util.IntercomCtrl;

// 详情界面
public class CautionDetailActivity extends ActivityEx implements
		OnClickListener {
	private Caution mCaution;
	private MediaPlayer mediaplayer = new MediaPlayer();
	private boolean isPlay = false;
	private long id = -1;
	private final int MSG_PLAY_AUDIO = 11;	// 播放录音
	
	@SuppressLint("HandlerLeak")
	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_PLAY_AUDIO :
				String voicePath = (String) msg.obj;
				closeProgressDialog();
				try {
					mediaplayer.reset();
					mediaplayer.setDataSource(voicePath);
					mediaplayer.prepare();
					mediaplayer.start();
				} catch (Exception e) {
					e.printStackTrace();
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
		setContentView(R.layout.caution_detail);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			id = bundle.getLong(CautionEditActivity.KEY_CAUTIONID, -1);
		}

		mediaplayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {
				IntercomCtrl.open_intercom(CautionDetailActivity.this);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (id != -1) {
			mCaution = Caution.GetById(CautionDetailActivity.this, id);
			initView();
		}
	}

	private void initView() {
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.Caution_Title);

		Button btnEdit = (Button) findViewById(R.id.btnset);
		btnEdit.setVisibility(View.VISIBLE);
		btnEdit.setText(R.string.Caution_Edit);
		btnEdit.setOnClickListener(this);

		TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
		TextView txtLevel = (TextView) findViewById(R.id.txtLevel);
		TextView txtTime = (TextView) findViewById(R.id.txtTime);
		TextView txtContent = (TextView) findViewById(R.id.txtContent);

		TextView txtAudio = (TextView) findViewById(R.id.txtAudio);
		TextView txtPhoto = (TextView) findViewById(R.id.txtPhoto);

		Button btnAudio = (Button) findViewById(R.id.btnAudio);
		Button btnPhoto = (Button) findViewById(R.id.btnPhoto);

		RelativeLayout LayoutAudio = (RelativeLayout) findViewById(R.id.LayoutAudio);
		RelativeLayout LayoutPhoto = (RelativeLayout) findViewById(R.id.LayoutPhoto);

		btnAudio.setOnClickListener(this);
		btnPhoto.setOnClickListener(this);

		if (mCaution != null) {
			txtTitle.setText(mCaution.title); // 标题

			switch (mCaution.level) { // 优先级
			case Caution.LEVEL_HIGH:
				txtLevel.setText(R.string.Caution_InputLevelHigh);
				break;
			case Caution.LEVEL_NORMAL:
				txtLevel.setText(R.string.Caution_InputLevelNormal);
				break;
			case Caution.LEVEL_LOW:
				txtLevel.setText(R.string.Caution_InputLevelLow);
				break;
			default:
				txtLevel.setText(R.string.Caution_InputLevelNormal);
				break;
			}

			txtTime.setText(mCaution.date + " " + mCaution.time); // 日期 + 时间
			txtContent.setText(mCaution.content); // 内容

			if (mCaution.attachaudio == null || mCaution.attachaudio.equals("")) { // 音频附件
				LayoutAudio.setVisibility(View.GONE);
			} else {
				LayoutAudio.setVisibility(View.VISIBLE);
				txtAudio.setText(mCaution.attachaudio);
				btnAudio.setBackgroundResource(R.drawable.audioplay);
			}

			if (mCaution.attachphoto == null || mCaution.attachphoto.equals("")) { // 照片附件
				LayoutPhoto.setVisibility(View.GONE);
			} else {
				LayoutPhoto.setVisibility(View.VISIBLE);
				txtPhoto.setText(mCaution.attachphoto);
			}
		} else {
			txtTitle.setText("");
			txtLevel.setText("");
			txtTime.setText("");
			txtContent.setText("");
			LayoutAudio.setVisibility(View.GONE);
			LayoutPhoto.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnAudio: // 查看音频附件
			Button btnAudio = (Button) findViewById(R.id.btnAudio);
			if (!isPlay) {
				String filepath = CommonUtils
						.getFilePath(CautionDetailActivity.this)
						+ mCaution.attachaudio;

				playVoice(filepath);
				isPlay = true;
				btnAudio.setBackgroundResource(R.drawable.audiostop);
			} else {
				stopVoice();
				isPlay = false;
				btnAudio.setBackgroundResource(R.drawable.audioplay);
			}
			break;
		case R.id.btnPhoto: // 查看图片附件
			String filepath = CommonUtils
					.getFilePath(CautionDetailActivity.this)
					+ mCaution.attachphoto;

			File file = new File(filepath);
			if (file != null && file.isFile() == true) {
				Intent intent = new Intent();
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(file), "image/*");
				startActivity(intent);
			}
			break;
		case R.id.btnset: // 编辑
			if (mCaution != null) {
				Intent intent = new Intent(CautionDetailActivity.this,
						CautionEditActivity.class);
				intent.putExtra(CautionEditActivity.KEY_CAUTIONID, mCaution.ID);
				intent.putExtra(CautionEditActivity.KEY_EDIT, true);
				startActivity(intent);
			}
			break;
		}
	}

	// // 播放录音
	// private void playVoice(String voicePath) {
	// if (!voicePath.equals("")) {
	// try {
	// IntercomCtrl.close_intercom(CautionDetailActivity.this);
	// mediaplayer.reset();
	// mediaplayer.setDataSource(voicePath);
	// mediaplayer.prepare();
	// mediaplayer.start();
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// }
	// }
	// }
	
	// 播放录音
		private void playVoice(final String voicePath) {
			if (!voicePath.equals("")) {
		//		int delay = 0;
				if (IntercomCtrl.close_intercom(CautionDetailActivity.this)) {
					showProgressDialog("");
				//	delay = IntercomCtrl.INTERCOM_WAIT_TIME;
				}
					
				Message msg = new Message();
				msg.obj = voicePath;
				msg.what = MSG_PLAY_AUDIO;
			//	myHandler.sendMessageDelayed(msg, delay);			
				myHandler.sendMessage(msg);
			}
		}

	// 停止播放录音
	private void stopVoice() {
		try {
			IntercomCtrl.open_intercom(CautionDetailActivity.this);
			mediaplayer.reset();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
