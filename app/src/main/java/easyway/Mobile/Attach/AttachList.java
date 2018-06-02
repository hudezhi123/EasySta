package easyway.Mobile.Attach;

import java.util.ArrayList;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.R;
import easyway.Mobile.util.IntercomCtrl;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;
import android.widget.TextView;

/*
 * 附件列表
 */
public class AttachList extends ActivityEx {
	private AttachListAdapter attahListAdapter = null;
	private MediaPlayer mediaplayer = new MediaPlayer();

	private final int MSG_PLAY_AUDIO = 11; // 播放录音

	@SuppressLint("HandlerLeak")
	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_PLAY_AUDIO:
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
		setContentView(R.layout.attach_list);

		mediaplayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {
				IntercomCtrl.open_intercom(AttachList.this);
			}
		});

		mediaplayer.setOnErrorListener(new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				IntercomCtrl.open_intercom(AttachList.this);
				return false;
			}
		});

		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.title_attach);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			ArrayList<String> list = bundle.getStringArrayList("url");

			if (list == null) {
				showErrMsg(R.string.ex_load);
				return;
			}

			ListView gv_Report_List = (ListView) findViewById(R.id.gv_Report_List);
			attahListAdapter = new AttachListAdapter(AttachList.this, list);
			gv_Report_List.setAdapter(attahListAdapter);

			attahListAdapter.setIAudioPlay(new IAudioPlay() {

				@Override
				public void OnPlayVoice(String voicePath) {
					playVoice(voicePath);
				}
			});

		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (attahListAdapter != null)
			attahListAdapter.StopDownload();

		if (mediaplayer != null && mediaplayer.isPlaying())
			stopVoice();
	}

	// 停止播放录音
	private void stopVoice() {
		try {
			IntercomCtrl.open_intercom(AttachList.this);
			mediaplayer.reset();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// 播放录音
	private void playVoice(final String voicePath) {
		if (!voicePath.equals("")) {
		//	int delay = 0;
			if (IntercomCtrl.close_intercom(AttachList.this)) {
				showProgressDialog("");
			//	delay = IntercomCtrl.INTERCOM_WAIT_TIME;
			}
				
			Message msg = new Message();
			msg.obj = voicePath;
			msg.what = MSG_PLAY_AUDIO;
			//myHandler.sendMessageDelayed(msg, delay);	
			myHandler.sendMessage(msg);
		}
	}
}
