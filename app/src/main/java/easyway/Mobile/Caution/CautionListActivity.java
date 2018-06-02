package easyway.Mobile.Caution;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import easyway.Mobile.ActivityEx;
import easyway.Mobile.R;
import easyway.Mobile.util.CommonUtils;
import easyway.Mobile.util.DateLine;
import easyway.Mobile.util.IDateLineListener;
import easyway.Mobile.util.IntercomCtrl;
import easyway.Mobile.util.PullRefreshListView;
import easyway.Mobile.util.PullRefreshListView.OnRefreshListener;

/*
 *  记事本列表
 */
public class CautionListActivity extends ActivityEx implements OnClickListener {
	private PullRefreshListView mListView;
	private CautionListAdapter mAdapter;
	private DateLine dateline;

	private ArrayList<Caution> mList;
	private MediaPlayer mediaplayer = new MediaPlayer();
	private IOnDataChange iOnDataChange;
	private boolean isPullRefresh = false;

	private final int MSG_GETDATAL = 0; // 获取数据
	private final int MSG_DATE_CHANGE = 1; // 日期改变
	private final int MSG_PLAY_AUDIO = 11;	// 播放录音
	
	@SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			closeProgressDialog();
			switch (msg.what) {
			case MSG_GETDATAL:
				if (isPullRefresh) {
					isPullRefresh = false;
					mListView.onRefreshComplete();
				}

				mList = (ArrayList<Caution>) msg.obj;
				mAdapter.setData(mList);
				mAdapter.notifyDataSetChanged();
				break;
			case MSG_DATE_CHANGE:
				getData();
				break;
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
		setContentView(R.layout.caution_list);
		initView();
		
		mediaplayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {
				IntercomCtrl.open_intercom(CautionListActivity.this);
				if (iOnDataChange != null)
					iOnDataChange.onPlay(-1, false);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		getData();
	}

	private void initView() {
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.Caution_Title);

		Button addBtn = (Button) findViewById(R.id.btnset);
		addBtn.setVisibility(View.VISIBLE);
		addBtn.setText(R.string.Caution_Add);
		addBtn.setOnClickListener(this);
		Button btnReturn = (Button) findViewById(R.id.btnReturn);
		if (btnReturn != null) {
			btnReturn.setOnClickListener(this);
		}

		dateline = (DateLine) findViewById(R.id.dateline);
		dateline.setListener(new IDateLineListener() {
			@Override
			public void DateChange() {
				myhandle.sendEmptyMessage(MSG_DATE_CHANGE); // 日期改变
			}
		});

		mListView = (PullRefreshListView) findViewById(R.id.cautionlist);
		mAdapter = new CautionListAdapter(this, null);
		mListView.setAdapter(mAdapter);

		mListView.setonRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				isPullRefresh = true;
				getData();
			}
		});

		iOnDataChange = new IOnDataChange() {

			@Override
			public void onPlay(long id, boolean play) {		// 播放/停止 音频
				for (Caution caution : mList) {
					if (caution.ID == id) {
						caution.isplay = play;

						if (caution.isplay) {
							String filepath = CommonUtils
									.getFilePath(CautionListActivity.this)
									+ caution.attachaudio;

							playVoice(filepath);
						} else {
							stopVoice();
						}
					} else {
						caution.isplay = false;
					}
				}

				mAdapter.setData(mList);
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void onLongClick(long id) {
				showDeleteDialog(id);
			}
		};
		mAdapter.setIOnDataChange(iOnDataChange);
	}

	// 获取数据
	private void getData() {
		showProgressDialog(R.string.GettingData);

		new Thread() {
			public void run() {
				ArrayList<Caution> list = Caution.LocalLoad(
						CautionListActivity.this, dateline.getDate());
				Message msg = new Message();
				msg.what = MSG_GETDATAL;
				msg.obj = list;
				myhandle.sendMessage(msg);
			}
		}.start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnset: // 添加记录
			Intent intent = new Intent(CautionListActivity.this,
					CautionEditActivity.class);
			startActivity(intent);
			break;
		case R.id.btnReturn: // 返回
			finish();
			break;
		default:
			break;
		}
	}

//	// 播放录音
//	private void playVoice(String voicePath) {
//		if (!voicePath.equals("")) {
//			try {
//				IntercomCtrl.close_intercom(CautionListActivity.this);
//				mediaplayer.reset();
//				mediaplayer.setDataSource(voicePath);
//				mediaplayer.prepare();
//				mediaplayer.start();
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
//		}
//	}
	
	// 播放录音
	private void playVoice(final String voicePath) {
		if (!voicePath.equals("")) {
		//	int delay = 0;
			if (IntercomCtrl.close_intercom(CautionListActivity.this)) {
				showProgressDialog("");
			//	delay = IntercomCtrl.INTERCOM_WAIT_TIME;
			}
				
			Message msg = new Message();
			msg.obj = voicePath;
			msg.what = MSG_PLAY_AUDIO;
		//	myhandle.sendMessageDelayed(msg, delay);
			myhandle.sendMessage(msg);
		}
	}

	// 停止播放录音
	private void stopVoice() {
		try {
			IntercomCtrl.open_intercom(CautionListActivity.this);
			mediaplayer.reset();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	// 删除对话
	private void showDeleteDialog(final long id) {
		String[] strs = new String[] { getString(R.string.Caution_Delete) };

		AlertDialog dlg = new AlertDialog.Builder(CautionListActivity.this)
				.setTitle("")
				.setItems(strs, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						if (item == 0) { // 删除
							Caution.DeleteCaution(CautionListActivity.this, id);
							getData();							
						}
					}
				}).create();
		dlg.show();
	}

}
