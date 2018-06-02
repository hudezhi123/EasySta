package easyway.Mobile.Message;

import java.io.File;
import java.util.HashMap;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.DBHelper;
import easyway.Mobile.Data.ZWTMessage;
import easyway.Mobile.util.CommonFunc;
import easyway.Mobile.util.CommonUtils;
import easyway.Mobile.util.IntercomCtrl;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.LogUtil;
import easyway.Mobile.util.StringUtil;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/*
 * 短信详情界面
 */
public class MessageView extends ActivityEx {

    private Button btnSendReceipt;
    private long messageId;
    public DBHelper dbHelper = null;
    private String attach;
    private boolean audioplay = false;
    private MediaPlayer mediaplayer = new MediaPlayer();
    private TextView amvTextContent;
    private ImageView imgAttach;
    private ProgressDialog progDialog;

    private final int MSG_ATTACH_NOT_EXIST = 1; // 附件不存在
    private final int MSG_ATTACH_DOWNLOADING = 2; // 附件下载中
    private final int MSG_ATTACH_DOWNLOAD_FAIL = 3; // 附件下载失败
    private final int MSG_ATTACH_DOWNLOAD_SUCC = 4; // 附件下载成功
    private final int MSG_ATTACH_PLAY_AUDIO = 5; // 播放音频
    private final int MSG_ATTACH_STOP_AUDIO = 6; // 停止播放音频
    private final int MSG_ATTACH_PLAY_END = 7; // 播放音频结束
    private final int MSG_ATTACH_EXIST = 8; // 附件存在
    private final int MSG_PLAY_AUDIO = 11; // 播放录音

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ATTACH_NOT_EXIST: // 附件不存在
                    Toast.makeText(MessageView.this, R.string.notifyfilenotexist,
                            Toast.LENGTH_SHORT).show();
                    amvTextContent.setVisibility(View.GONE);
                    imgAttach.setVisibility(View.GONE);
                    break;
                case MSG_ATTACH_DOWNLOADING: // 附件下载中
                    progDialog = ProgressDialog.show(MessageView.this,
                            getString(R.string.Waiting),
                            getString(R.string.attachdownloading), true, false);
                    progDialog.setIcon(R.drawable.waiting);

                    amvTextContent.setVisibility(View.GONE);
                    imgAttach.setVisibility(View.GONE);
                    break;
                case MSG_ATTACH_DOWNLOAD_FAIL: // 附件下载失败
                    if (progDialog != null) {
                        progDialog.dismiss();
                    }

                    Toast.makeText(MessageView.this, R.string.attachdownloadfail,
                            Toast.LENGTH_SHORT).show();
                    amvTextContent.setVisibility(View.GONE);
                    imgAttach.setVisibility(View.GONE);
                    break;
                case MSG_ATTACH_DOWNLOAD_SUCC: // 附件下载成功
                case MSG_ATTACH_EXIST: // 附件存在
                    amvTextContent.setVisibility(View.GONE);
                    if (progDialog != null) {
                        progDialog.dismiss();
                    }

                    if (attach.endsWith(".wav")) { // 附件类型为音频
                        imgAttach.setVisibility(View.VISIBLE);
                        imgAttach.setImageResource(R.drawable.audioplay_big);
                        imgAttach.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (audioplay) {
                                    audioplay = false;
                                    imgAttach
                                            .setImageResource(R.drawable.audioplay_big);
                                    stopVoice();
                                } else {
                                    audioplay = true;
                                    imgAttach
                                            .setImageResource(R.drawable.audiostop_big);
                                    playVoice(attach);
                                }
                            }
                        });
                    } else if (attach.endsWith(".jpg")) { // 附件类型为图片
                        LogUtil.i("attachfile -->" + attach);
                        Bitmap bm = BitmapFactory.decodeFile(attach);

                        if (bm != null) {
                            imgAttach.setVisibility(View.VISIBLE);
                            imgAttach.setImageBitmap(bm);
                            // bm = ThumbnailUtils.extractThumbnail(bm, 400, 300);

                            imgAttach.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    File file = new File(attach);
                                    if (file != null && file.isFile() == true) {
                                        Intent intent = new Intent();
                                        intent.setAction(android.content.Intent.ACTION_VIEW);
                                        intent.setDataAndType(Uri.fromFile(file),
                                                "image/*");
                                        startActivity(intent);
                                    }
                                }
                            });
                        } else {
                            imgAttach.setImageResource(R.drawable.error);
                            Toast.makeText(MessageView.this,
                                    R.string.fileException, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }

                    break;
                case MSG_ATTACH_PLAY_AUDIO: // 播放音频
                    imgAttach.setImageResource(R.drawable.audiostop_big);
                    audioplay = true;
                    break;
                case MSG_ATTACH_STOP_AUDIO: // 停止播放音频
                case MSG_ATTACH_PLAY_END: // 播放音频结束
                    imgAttach.setImageResource(R.drawable.audioplay_big);
                    audioplay = false;
                    break;
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
        setContentView(R.layout.msg_view);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.title_message);
        btnSendReceipt = (Button) findViewById(R.id.btn_notice_sendreceipt);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String contactName = bundle.getString("contactName");
            String createTime = bundle.getString("createTime");
            String content = bundle.getString("content");
            messageId = bundle.getLong("id");
            attach = bundle.getString("attach");

            TextView amvTextName = (TextView) findViewById(R.id.amvTextContact);
            amvTextName.setText(contactName + "   " + createTime);

            amvTextContent = (TextView) findViewById(R.id.amvTextContent);
            imgAttach = (ImageView) findViewById(R.id.amvImgAttach);

            if (content == null || content.trim().length() == 0) {
                amvTextContent.setVisibility(View.GONE);
                imgAttach.setVisibility(View.GONE);

                LogUtil.i("AttachShow");
                // 判断文件是否存在，若不存在则从网络获取
                new Thread() {
                    public void run() {
                        AttachShow();
                    }
                }.start();
            } else {
                amvTextContent.setVisibility(View.VISIBLE);
                imgAttach.setVisibility(View.GONE);
                amvTextContent.setText(StringUtil.Encode(content, false));
                amvTextContent.setMovementMethod(ScrollingMovementMethod
                        .getInstance());
            }
        }
//        myHandler.post(updateMessageStatus);
        btnSendReceipt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getSendReceipt(messageId);
                    }
                }).start();

            }
        });
        getMessageStatus(messageId);
        mediaplayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer arg0) {
                IntercomCtrl.open_intercom(MessageView.this);
                myHandler.sendEmptyMessage(MSG_ATTACH_PLAY_END);
            }
        });
    }

    private void getSendReceipt(long MsgId) {

        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("id", MsgId + "");
        String methodPath = Constant.MP_SMS;
        String methodName = Constant.MN_GET_SendReceipt;
        WebServiceManager webServiceManager = new WebServiceManager(this,
                methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);

        if (result == null || result.equals("")) {
            return;
        }
        int Code = JsonUtil.GetJsonInt(result, "Code");
        switch (Code) {
            case Constant.NORMAL:
                boolean msgType = JsonUtil.GetJsonBoolean(result, "MsgType");
                if (msgType) {
                    try {
                        dbHelper = new DBHelper(MessageView.this);

                        String sql = "update " + DBHelper.MESSAGE_TABLE_NAME + " set "
                                + DBHelper.MESSAGE_STATUS + "= '"
                                + ZWTMessage.STATUS_READED + "' where "
                                + DBHelper.MESSAGE_ID + "= '" + messageId + "';";

                        dbHelper.getWritableDatabase().execSQL(sql);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnSendReceipt.setClickable(false);
                                btnSendReceipt.setBackgroundResource(R.drawable.sendreceipt_shape_unclickble);
                            }
                        });
//                        myHandler.sendEmptyMessage();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        dbHelper.close();
                    }
                } else {
                    Log.i("---receipt", "发送回执失败");
                }
                break;
            case Constant.EXCEPTION:
            default:
                Log.i("---receipt", "发送回执失败");
                break;
        }

    }

    // 显示附件
    private void AttachShow() {
        if (attach == null || attach.trim().length() == 0)
            return;

        String filesavepath = CommonUtils.getFilePath(MessageView.this);
        if (attach.contains(filesavepath)) { // 判断附件地址是否带路径，带路径则为已下载，否则为还未下载
            if (CommonUtils.fileIsExists(attach)) { // 附近存在
                myHandler.sendEmptyMessage(MSG_ATTACH_EXIST);
            } else { // 附件不存在
                myHandler.sendEmptyMessage(MSG_ATTACH_NOT_EXIST);
            }
        } else {
            if (CommonUtils.fileIsExists(filesavepath + attach)) { // 重新组合附件地址，判断文件是否存在
                attach = filesavepath + attach;
                myHandler.sendEmptyMessage(MSG_ATTACH_EXIST);
            } else { // 从平台下载附件
                if (CommonUtils.getFileFromServer(
                        CommonFunc.GetServer(MessageView.this)
                                + DownloadAttachThread.MESSAGE_FILE_PATH
                                + attach, filesavepath + attach)) {
                    attach = filesavepath + attach;
                    myHandler.sendEmptyMessage(MSG_ATTACH_DOWNLOAD_SUCC);
                } else {
                    myHandler.sendEmptyMessage(MSG_ATTACH_DOWNLOAD_FAIL);
                }
            }
        }
    }

    private void getMessageStatus(long messageId) {
        try {
            dbHelper = new DBHelper(MessageView.this);

            String sql = "select * from " + DBHelper.MESSAGE_TABLE_NAME + " where "
                    + DBHelper.MESSAGE_ID + "= '" + messageId + "';";
            Cursor cursor = dbHelper.getCursorResult(sql);
            if (cursor == null) {
                return;
            }
            cursor.moveToFirst();
            int status = cursor.getInt(cursor.getColumnIndex(DBHelper.MESSAGE_STATUS));
            dbHelper.closeCursor(cursor);
            boolean msgStatus = false;
            if (status == 1) {
                msgStatus = true;
            } else if (status == 0) {
                msgStatus = false;
            }
            if (msgStatus) {
                btnSendReceipt.setClickable(false);
                btnSendReceipt.setBackgroundResource(R.drawable.sendreceipt_shape_unclickble);
            } else {
                btnSendReceipt.setClickable(true);
                btnSendReceipt.setBackgroundResource(R.drawable.sendreceipt_shape_clickble);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbHelper.close();
        }
    }

    // 更新短消息状态为已读
    private Runnable updateMessageStatus = new Runnable() {
        public void run() {
            try {
                dbHelper = new DBHelper(MessageView.this);

                String sql = "update " + DBHelper.MESSAGE_TABLE_NAME + " set "
                        + DBHelper.MESSAGE_STATUS + "= '"
                        + ZWTMessage.STATUS_READED + "' where "
                        + DBHelper.MESSAGE_ID + "= '" + messageId + "';";

                dbHelper.getWritableDatabase().execSQL(sql);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                dbHelper.close();
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if (mediaplayer != null && mediaplayer.isPlaying()) {
            stopVoice();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // // 播放音频
    // private void playVoice(String voicePath) {
    // if (!voicePath.equals("")) {
    // try {
    // IntercomCtrl.close_intercom(MessageView.this);
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
            try {
                //	int delay = 0;
                if (IntercomCtrl.close_intercom(MessageView.this)) {
                    showProgressDialog("");
                    //		delay = IntercomCtrl.INTERCOM_WAIT_TIME;
                }

                Message msg = new Message();
                msg.obj = voicePath;
                msg.what = MSG_PLAY_AUDIO;
                //	myHandler.sendMessageDelayed(msg, delay);
                myHandler.sendMessage(msg);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // 停止播放音频
    private void stopVoice() {
        try {
            IntercomCtrl.open_intercom(MessageView.this);
            mediaplayer.reset();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
