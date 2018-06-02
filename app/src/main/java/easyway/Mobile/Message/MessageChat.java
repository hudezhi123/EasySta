package easyway.Mobile.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Application.ExitApplication;
import easyway.Mobile.receiver.AlarmReceiver;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.DBHelper;
import easyway.Mobile.Data.MessageContact;
import easyway.Mobile.Data.MessageType;
import easyway.Mobile.Data.ZWTMessage;
import easyway.Mobile.Media.ExtAudioRecorder;
import easyway.Mobile.Media.ExtAudioRecorder.State;
import easyway.Mobile.util.CommonUtils;
import easyway.Mobile.util.ImgCompress;
import easyway.Mobile.util.IntercomCtrl;
import easyway.Mobile.util.LogUtil;
import easyway.Mobile.util.StringUtil;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/*
 * 发送消息
 */
@SuppressLint("SimpleDateFormat")
public class MessageChat extends ActivityEx implements OnClickListener,
        OnTouchListener {
    private ArrayList<MessageContact> mListContacts;
    public static final String TAG_STAFF_ID = "staff_id";
    public static final String TAG_STAFF_NAME = "staff_name";

    public static final int FLAG_SEND = 1;

    public static long MessageID = -1;
    private Button btnAddContact;
    private Button btnSendMsg;
    private Button btnAddaudio = null;
    private Button btnAddimage = null;
    private EditText messageEditText = null;
    private CheckBox chkReturn = null, chkSendSMS = null;
    private TextView txtContacts;
    private Uri fileUri;

    private final int REQUESTCODE_ADD_CONTACT = 1; // 添加接收人
    private final int REQUESTCODE_GET_IMAGE_FROM_CAMERA = 2; // 从相机添加图片
    private final int REQUESTCODE_GET_IMAGE_FROM_LOCAL = 3; // 从相册添加图片
    private final int REQUESTCODE_GET_COMMON_MSG = 4; // 添加常用消息
    private ListView lstMessage;
    private MessageChatAdapter mAdapter;
    private ArrayList<ZWTMessage> mlistMessage = null;
    private ExtAudioRecorder extAudioRecorder;
    private MediaPlayer mediaplayer = new MediaPlayer();
    private IOnDataChange iOnDataChange;

    private final int MSG_CONTENT_NULL = 1; // 发送内容为空
    private final int MSG_CONTACT_NULL = 2; // 消息接收人为空
    private final int MSG_MESSAGE_SEND = 4; // 发送消息
    private final int MSG_CONTACT_CHANGE = 6; // 消息接收人改变
    private final int MSG_SCROLL_DOWN = 8; // 载入新消息，并滑动到底部
    private final int MSG_SCROLL_NOT = 9; // 载入新消息，不滑动滚动条
    private final int MSG_DATA_CHANGE = 10; // 数据改变
    private final int MSG_PLAY_AUDIO = 11; // 播放录音

    public final static int MSG_recripe_ok = 13;
    public final static int MSG_recripe_no = 14;

    private Date recordBeginTime; // 语音开始时间
    private Date recordEndTime; // 语音结束时间
    private MessageReceiver receiver;

    private DBHelper dbHelper = null;
    private int thedbmsgNum = 0;

    public Handler getMyHandler() {
        return myHandler;
    }

    public void setMyHandler(Handler myHandler) {
        this.myHandler = myHandler;
    }

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MSG_recripe_ok:
                    Toast.makeText(MessageChat.this, (String) msg.obj,
                            Toast.LENGTH_LONG).show();
                    break;
                case MSG_recripe_no:
                    if ((String) msg.obj == null) {
                        Toast.makeText(MessageChat.this, "回执失败", Toast.LENGTH_LONG)
                                .show();
                    } else {
                        Toast.makeText(MessageChat.this, (String) msg.obj,
                                Toast.LENGTH_LONG).show();
                    }

                    break;

                case MSG_CONTENT_NULL: // 发送内容为空
                    Toast.makeText(MessageChat.this, R.string.notifymessagenull,
                            Toast.LENGTH_LONG).show();
                    break;
                case MSG_CONTACT_NULL: // 消息接收人为空
                    Toast.makeText(MessageChat.this, R.string.notifycontactnull,
                            Toast.LENGTH_LONG).show();
                    break;
                case MSG_MESSAGE_SEND: // 发送消息
                    if (mListContacts == null || mListContacts.size() == 0)
                        return;

                    // 刷新，滚动条拉到最下
                    if (mListContacts.size() == 1) { // 消息接收人为单人
                        new LoadDataThread(true, mListContacts.get(0).contactId)
                                .start();
                    } else { // 消息接收人为多人
                        ZWTMessage message = (ZWTMessage) msg.obj;

                        if (mlistMessage == null)
                            mlistMessage = new ArrayList<ZWTMessage>();

                        message.status = ZWTMessage.STATUS_SENDING;
                        mlistMessage.add(message);
                        mAdapter.setData(mlistMessage);
                        mAdapter.notifyDataSetChanged();

                        SendMessageThread thread = new SendMessageThread(
                                MessageChat.this, message, mListContacts,
                                iOnDataChange);
                        thread.start();
                        lstMessage
                                .setSelection(lstMessage.getAdapter().getCount() - 1);
                    }

                    break;
                case MSG_CONTACT_CHANGE: // 消息接收人改变
                    if (mListContacts == null || mListContacts.size() == 0)
                        return;

                    if (mListContacts.size() == 1) { // 消息接收人为单人
                        new LoadDataThread(true, mListContacts.get(0).contactId)
                                .start();
                    } else { // 消息接收人为多人
                        // 清空数据
                        if (mlistMessage != null)
                            mlistMessage.clear();
                        mAdapter.setData(mlistMessage);
                        mAdapter.notifyDataSetChanged();
                    }

                    break;
                case MSG_SCROLL_DOWN: // 载入新消息，并滑动到底部
                    mlistMessage = (ArrayList<ZWTMessage>) msg.obj;
                    for (ZWTMessage message : mlistMessage) {
                        if (message.status == ZWTMessage.STATUS_UNSEND || message.status == ZWTMessage.STATUS_SENDFAIL) {
                            // 发送尚未发送的短消息
                            new SendMessageThread(MessageChat.this, message,
                                    mListContacts, iOnDataChange).start();
                            message.status = ZWTMessage.STATUS_SENDING;
                        } else if (message.status == ZWTMessage.STATUS_UNREAD) {
                            // 下载未阅读短消息的附件
                            if (message.attach != null
                                    && message.attach.length() > 0) {
                                new DownloadAttachThread(MessageChat.this, message,
                                        iOnDataChange).start();
                                message.dattachstatus = ZWTMessage.STATUS_DATTACH_ING;
                            }
                        }
                    }
                    mAdapter.setData(mlistMessage);
                    mAdapter.notifyDataSetChanged();
                    LogUtil.d("刷新成功" + mlistMessage.size() + "消息的大小");
                    // TODO 添加数据并且刷新数据
                    if (msg.what == MSG_SCROLL_DOWN)
                        lstMessage.setSelection(lstMessage.getAdapter().getCount() - 1);
                    break;
                case MSG_SCROLL_NOT: // 载入新消息，不滑动滚动条
                    mlistMessage = (ArrayList<ZWTMessage>) msg.obj;
                    for (ZWTMessage message : mlistMessage) {
                        if (message.status == ZWTMessage.STATUS_UNSEND || message.status == ZWTMessage.STATUS_SENDFAIL) {
                            // 发送尚未发送的短消息
                            new SendMessageThread(MessageChat.this, message,
                                    mListContacts, iOnDataChange).start();
                            message.status = ZWTMessage.STATUS_SENDING;
                        } else if (message.status == ZWTMessage.STATUS_UNREAD) {
                            // 下载未阅读短消息的附件
                            if (message.attach != null
                                    && message.attach.length() > 0) {
                                new DownloadAttachThread(MessageChat.this, message,
                                        iOnDataChange).start();
                                message.dattachstatus = ZWTMessage.STATUS_DATTACH_ING;
                            }
                        }
                    }
                    mAdapter.setData(mlistMessage);
                    mAdapter.notifyDataSetChanged();
                    LogUtil.d("刷新成功" + mlistMessage.size() + "消息的大小");
                    // TODO 添加数据并且刷新数据
                    if (msg.what == MSG_SCROLL_DOWN)
                        lstMessage.setSelection(lstMessage.getAdapter().getCount() - 1);

                    break;
                case MSG_DATA_CHANGE: // 数据改变
                    mAdapter.setData(mlistMessage);
                    mAdapter.notifyDataSetChanged();
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

    private void initView() {
        btnAddaudio = (Button) findViewById(R.id.btnontouchspeak);
        btnAddaudio.setOnTouchListener(this);

        btnAddimage = (Button) findViewById(R.id.btnAddimage);
        btnAddimage.setOnClickListener(this);

        chkSendSMS = (CheckBox) findViewById(R.id.chkSMS);
        chkReturn = (CheckBox) findViewById(R.id.chkReturn);
        btnAddContact = (Button) findViewById(R.id.smAddContact);
        btnAddContact.setOnClickListener(this);

        txtContacts = (TextView) findViewById(R.id.smTextContact);
        lstMessage = (ListView) findViewById(R.id.lstMessage);
        iOnDataChange = new IOnDataChange() {
            @Override
            public void onDataChange() {
            }

            // 状态更新
            @Override
            public void onDataChange(long id, int status) {
                if (mListContacts == null || mListContacts.size() == 0)
                    return;

                // 重载内容，位置不滚动
                if (mListContacts.size() == 1) {
                    new LoadDataThread(false, mListContacts.get(0).contactId)
                            .start();
                } else {
                    if (mlistMessage == null)
                        return;

                    for (ZWTMessage message : mlistMessage) {
                        if (message.Id == id) {
                            message.status = status;
                            break;
                        }
                    }

                    myHandler.sendEmptyMessage(MSG_DATA_CHANGE);
                }
            }

            // 转发消息
            @Override
            public void onTransmit(String content) {
                if (mlistMessage != null)
                    mlistMessage.clear();
                if (mListContacts != null)
                    mListContacts.clear();
                mAdapter.setData(mlistMessage);
                mAdapter.notifyDataSetChanged();
                txtContacts.setText(getResources().getString(
                        R.string.msg_addContact));
                messageEditText.setText(content);
                btnAddContact.setVisibility(View.VISIBLE);
            }

            // 删除短消息
            @Override
            public void onDelete(long id) {
                if (mListContacts == null || mListContacts.size() == 0)
                    return;

                // 重载内容，位置不滚动
                if (mListContacts.size() == 1) {
                    new LoadDataThread(false, mListContacts.get(0).contactId)
                            .start();
                } else {
                    if (mlistMessage == null)
                        return;

                    for (ZWTMessage message : mlistMessage) {
                        if (message.Id == id) {
                            mlistMessage.remove(message);
                        }
                    }
                    myHandler.sendEmptyMessage(MSG_DATA_CHANGE);
                }
            }

            // 播放/关闭音乐
            @Override
            public void onPlay(long id, boolean play) {
                for (ZWTMessage message : mlistMessage) {
                    if (message.Id == id) {
                        message.isplay = play;

                        if (message.isplay) {
                            String filepath = "";
                            if (message.type == MessageType.TYPE_SEND) {
                                filepath = message.attach;
                            } else {
                                filepath = CommonUtils
                                        .getFilePath(MessageChat.this)
                                        + message.attach;
                            }

                            playVoice(filepath);
                        } else {
                            stopVoice();
                        }
                    } else {
                        message.isplay = false;
                    }
                }

                mAdapter.setData(mlistMessage);
                mAdapter.notifyDataSetChanged();
            }

            // 下载附件
            @Override
            public void onAttachDownload(long id, int status) {
                if (mListContacts == null || mListContacts.size() == 0)
                    return;

                if (mListContacts.size() != 1)
                    return;

                for (ZWTMessage message : mlistMessage) {
                    if (message.Id == id) {
                        message.dattachstatus = status;
                        break;
                    }
                }

                myHandler.sendEmptyMessage(MSG_DATA_CHANGE);
            }

            // 发送消息
            @Override
            public void onSendMessage(ZWTMessage message) {
                SendMessageThread thread = new SendMessageThread(
                        MessageChat.this, message, mListContacts, iOnDataChange);
                thread.start();
            }
        };

        mAdapter = new MessageChatAdapter(this, mlistMessage, iOnDataChange);
        lstMessage.setAdapter(mAdapter);

        messageEditText = (EditText) findViewById(R.id.smMessageContent);

        // 使用常用短消息
        messageEditText.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(MessageChat.this,
                        CommonMessage.class);

                startActivityForResult(intent, REQUESTCODE_GET_COMMON_MSG);
                return false;
            }
        });

        mListContacts = new ArrayList<MessageContact>();

        btnSendMsg = (Button) findViewById(R.id.smButtonMessage);
        btnSendMsg.setOnClickListener(this);
    }

    private SharedPreferences share;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msg_chat);
        ExitApplication.UnReadNum = 0;
        share = getSharedPreferences("MessageId", Context.MODE_PRIVATE);
        MessageID = share.getLong("ID", 10001);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.title_sendmsg);

        initView();
        mediaplayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer arg0) {
                IntercomCtrl.open_intercom(MessageChat.this);
                iOnDataChange.onPlay(-1, false);
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Long contactId = bundle.getLong(TAG_STAFF_ID);
            if (contactId != null && !contactId.equals("")) {
                String contactName = bundle.getString(TAG_STAFF_NAME);

                MessageContact tb_Message_Contact = new MessageContact();
                tb_Message_Contact.contactId = contactId;
                tb_Message_Contact.contactName = contactName;

                mListContacts.add(tb_Message_Contact);
                txtContacts.setText(contactName);
            }
        }

        if (mListContacts != null && mListContacts.size() == 1) {
            btnAddContact.setVisibility(View.GONE);

            new LoadDataThread(true, mListContacts.get(0).contactId).start();
        } else {
            btnAddContact.setVisibility(View.VISIBLE);
        }
    }

    public void onResume() {
        super.onResume();
        if (dbHelper == null)
            dbHelper = new DBHelper(MessageChat.this);

        regReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaplayer != null && mediaplayer.isPlaying())
            stopVoice();

        if (dbHelper != null)
            dbHelper.close();

        if (receiver != null) {
            try {
                unregisterReceiver(receiver);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        SharedPreferences.Editor editor = share.edit();
        editor.putLong("ID", MessageID);
        editor.commit();
    }

    // text : 发送内容是否为文本 filepath: 发送内容为文件情况下，文件的路径
    private void sendMessage(boolean text, String filepath) {
        String content = messageEditText.getText().toString();

        if (text) { // 发送文本信息，判断文本是否为空
            if (content == null || content.equals("")) {
                myHandler.sendEmptyMessage(MSG_CONTENT_NULL);
                return;
            }
        } else { // 发送附件，判断附件是否存在
            if (filepath == null || filepath.trim().length() == 0)
                return;

            if (!CommonUtils.fileIsExists(filepath))
                return;
        }

        // 判断收件人是否为空
        if (mListContacts == null || mListContacts.size() == 0) {
            myHandler.sendEmptyMessage(MSG_CONTACT_NULL);
            return;
        }

        if (mlistMessage == null) {
            mlistMessage = new ArrayList<ZWTMessage>();
        }

        ZWTMessage message = new ZWTMessage();

        if (text) {
            message.content = StringUtil.Encode(content, true);
            message.attach = "";
            messageEditText.setText("");
            if (chkSendSMS.isChecked()) {
                message.flag = ZWTMessage.FLAG_BOTH;
            } else {
                message.flag = ZWTMessage.FLAG_MESSAGE;
            }

            if (chkReturn.isChecked()) {
                message.receipt = "true";
            } else {
                message.receipt = "false";
            }

        } else {
            message.content = StringUtil.Encode("", true);
            message.flag = ZWTMessage.FLAG_MESSAGE;
            message.attach = filepath;
            message.receipt = "false";
        }
        //TODO 发送时间在这里赋值
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        message.createTime = df.format(new Date());
        message.ownerId = Property.StaffId;
        message.ownerName = Property.StaffName;
        message.status = ZWTMessage.STATUS_UNSEND;
        message.Id = MessageID;
        if (mListContacts.size() == 1) {
            message.contactId = mListContacts.get(0).contactId;
            message.contactName = mListContacts.get(0).contactName;
            ZWTMessage.Insert(MessageChat.this, message);
        }
//        else {
        MessageID++;
//        }
        Message msg = new Message();
        msg.what = MSG_MESSAGE_SEND;
        msg.obj = message;
        myHandler.sendMessage(msg);
    }

    // 录音
    private void recordVoice() {
        fileUri = CommonUtils.getOutputMediaFileUri(MessageChat.this,
                CommonUtils.MEDIA_TYPE_AUDIO);

        if (fileUri != null) {
            extAudioRecorder = ExtAudioRecorder.getInstanse(true); // compressed
            // recording
            // (WAV)
            extAudioRecorder.setOutputFile(fileUri.getPath());
            IntercomCtrl.close_intercom(MessageChat.this);
            try {
                extAudioRecorder.prepare();
                extAudioRecorder.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 结束录音
    private void stopRecord() {
        if (extAudioRecorder == null)
            return;
        if (extAudioRecorder.getState().equals(State.RECORDING)) {
            IntercomCtrl.open_intercom(MessageChat.this);
            extAudioRecorder.stop();
            extAudioRecorder.release();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            // do nothing
        } else if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUESTCODE_ADD_CONTACT: // 添加接收人
                    String resultString = data.getStringExtra("return");

                    ArrayList<MessageContact> Contacts = new ArrayList<MessageContact>();
                    String contactString = "";

                    if (!resultString.equals("")) {
                        resultString = resultString.substring(0,
                                resultString.length() - 1);
                        String[] conatactArray = resultString.split(":");

                        for (int i = 0; i < conatactArray.length; i++) {
                            String[] contactDetailsArray = conatactArray[i]
                                    .split(",");

                            MessageContact tb_Message_Contact = new MessageContact();
                            tb_Message_Contact.contactId = Long
                                    .parseLong(contactDetailsArray[0]);
                            tb_Message_Contact.contactName = contactDetailsArray[1];
                            Contacts.add(tb_Message_Contact);

                            contactString += contactDetailsArray[1] + ",";
                        }
                    }

                    if (!contactString.equals("")) {
                        contactString = contactString.substring(0,
                                contactString.length() - 1);

                        txtContacts.setText(contactString);
                    } else {
                        txtContacts.setText(getResources().getString(
                                R.string.msg_addContact));
                    }

                    if (!cmpContact(Contacts, mListContacts)) {
                        mListContacts = Contacts;
                        myHandler.sendEmptyMessage(MSG_CONTACT_CHANGE);
                    }
                    break;
                case REQUESTCODE_GET_IMAGE_FROM_CAMERA: // 拍照
                    sendMessage(
                            false,
                            ImgCompress.Compress(fileUri.getPath(),
                                    fileUri.getPath(), 800, 600, 80));
                    break;
                case REQUESTCODE_GET_IMAGE_FROM_LOCAL: // 从相册中获取
                    Uri uri = data.getData();

                    if (uri != null) {
                        fileUri = CommonUtils.getOutputMediaFileUri(
                                MessageChat.this, CommonUtils.MEDIA_TYPE_IMAGE);
                        sendMessage(false, ImgCompress.Compress(
                                getAbsoluteImagePath(uri), fileUri.getPath(), 800,
                                600, 80));
                    }
                    break;

                case REQUESTCODE_GET_COMMON_MSG: // 常用短消息
                    String content = data.getStringExtra("return");
                    messageEditText.setText(content);
                    break;
                default:
                    break;
            }
        }
    }

    // 获取图片地址
    private String getAbsoluteImagePath(Uri uri) {
        // can post image
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, proj, // Which columns to return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)

        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    //
    // // 播放录音
    // private void playVoice(String voicePath) {
    // if (!voicePath.equals("")) {
    // try {
    // IntercomCtrl.close_intercom(MessageChat.this);
    //
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
                // int delay = 0;
                if (IntercomCtrl.close_intercom(MessageChat.this)) {
                    showProgressDialog("");
                    // delay = IntercomCtrl.INTERCOM_WAIT_TIME;
                }

                Message msg = new Message();
                msg.obj = voicePath;
                msg.what = MSG_PLAY_AUDIO;
                // myHandler.sendMessageDelayed(msg, delay);
                myHandler.sendMessage(msg);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // 停止播放录音
    private void stopVoice() {
        try {
            IntercomCtrl.open_intercom(MessageChat.this);
            mediaplayer.reset();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddimage: // 添加图片
                addImage();
                break;
            case R.id.smAddContact: // 添加接收人
                addContact();
                break;
            case R.id.smButtonMessage: // 发送短消息
                sendMessage();
                break;
            default:
                break;
        }
    }

    // 发送消息
    private void sendMessage() {
        // 收起键盘
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                messageEditText.getWindowToken(), 0);

        sendMessage(true, null);
    }

    // 添加接收人
    private void addContact() {
        Intent intent = new Intent(MessageChat.this, SelectContact.class);

        Bundle bundle = new Bundle();
        String selectedStaffString = "";
        if (mListContacts.size() > 0) {
            for (int i = 0; i < mListContacts.size(); i++) {
                selectedStaffString += mListContacts.get(i).contactId + ","
                        + mListContacts.get(i).contactName + ":";
            }
        }

        if (!selectedStaffString.equals(""))
            selectedStaffString = selectedStaffString.substring(0,
                    selectedStaffString.length() - 1);

        bundle.putString("selectedStaff", selectedStaffString);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUESTCODE_ADD_CONTACT);
    }

    // 添加图片
    private void addImage() {
        AlertDialog dlg = new AlertDialog.Builder(MessageChat.this)
                .setTitle("")
                .setItems(
                        MessageChat.this.getResources().getStringArray(
                                R.array.ImageSource),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                if (item == 1) { // 拍照
                                    boolean hasCarema = CommonUtils
                                            .checkCameraHardware(MessageChat.this);
                                    if (!hasCarema) {
                                        AlertDialog.Builder build = new AlertDialog.Builder(
                                                MessageChat.this);

                                        build.setTitle(R.string.Prompt);
                                        build.setIcon(R.drawable.information);
                                        build.setPositiveButton(R.string.OK,
                                                null);
                                        build.setMessage(R.string.NonCarema);
                                        build.show();
                                        return;
                                    }

                                    fileUri = CommonUtils
                                            .getOutputMediaFileUri(
                                                    MessageChat.this,
                                                    CommonUtils.MEDIA_TYPE_IMAGE);
                                    if (fileUri == null) {
                                        return;
                                    }
                                    Intent intent = new Intent(
                                            MediaStore.ACTION_IMAGE_CAPTURE);
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                            fileUri);
                                    startActivityForResult(intent,
                                            REQUESTCODE_GET_IMAGE_FROM_CAMERA);
                                } else { // 从相册获取照片
                                    Intent getImage = new Intent(
                                            Intent.ACTION_GET_CONTENT);
                                    getImage.setType("image/*");
                                    startActivityForResult(getImage,
                                            REQUESTCODE_GET_IMAGE_FROM_LOCAL);
                                }
                            }
                        }).create();
        dlg.show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.btnontouchspeak:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        recordEndTime = new Date();
                        stopRecord();
                        if (recordEndTime.getTime() - recordBeginTime.getTime() < 1000) {
                            showToast(R.string.audiotooshort);
                            break;
                        }

                        if (fileUri != null) {
                            sendMessage(false, fileUri.getPath());
                        }
                        break;
                    case MotionEvent.ACTION_DOWN:
                        recordBeginTime = new Date();
                        recordVoice();
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return false;
    }

    // 验证收件人是否改变
    private boolean cmpContact(ArrayList<MessageContact> cantacts1,
                               ArrayList<MessageContact> cantacts2) {
        if (cantacts1 == null && cantacts2 == null)
            return true;

        if (cantacts1 == null)
            return false;

        if (cantacts2 == null)
            return false;

        if (cantacts1.size() != cantacts2.size())
            return false;

        for (MessageContact contact1 : cantacts1) {
            boolean contain = false;
            for (MessageContact contact2 : cantacts2) {
                if (contact2.contactId == contact1.contactId) {
                    contain = true;
                    break;
                }
            }

            if (!contain) {
                return false;
            }
        }
        return true;
    }

    // 从数据库中加载短信列表
    public class LoadDataThread extends Thread {
        private boolean isScroll = false;
        private long contactId;

        public LoadDataThread(boolean isScroll, long contactId) {
            this.isScroll = isScroll;
            this.contactId = contactId;
        }

        public void run() {
            ArrayList<ZWTMessage> msglist = new ArrayList<ZWTMessage>();

            String sql = "select * from " + DBHelper.MESSAGE_TABLE_NAME
                    + " where " + DBHelper.MESSAGE_OWNERID + " = '"
                    + Property.StaffId + "' and " + DBHelper.MESSAGE_CONTACTID
                    + " = '" + contactId + "' order by "
                    + DBHelper.MESSAGE_CREATETIME + " asc";

            Cursor cursor = null;

            try {
                if (dbHelper == null)
                    dbHelper = new DBHelper(MessageChat.this);

                cursor = dbHelper.getReadableDatabase().rawQuery(sql, null);
                while (cursor.moveToNext()) {
                    ZWTMessage message = new ZWTMessage();

                    message.Id = cursor.getLong(cursor
                            .getColumnIndex(DBHelper.MESSAGE_ID));
                    message.createTime = cursor.getString(cursor
                            .getColumnIndex(DBHelper.MESSAGE_CREATETIME));
                    message.ownerId = cursor.getLong(cursor
                            .getColumnIndex(DBHelper.MESSAGE_OWNERID));
                    message.ownerName = cursor.getString(cursor
                            .getColumnIndex(DBHelper.MESSAGE_OWNERNAME));
                    message.contactId = cursor.getLong(cursor
                            .getColumnIndex(DBHelper.MESSAGE_CONTACTID));
                    message.contactName = cursor.getString(cursor
                            .getColumnIndex(DBHelper.MESSAGE_CONTACTNAME));
                    message.type = cursor.getInt(cursor
                            .getColumnIndex(DBHelper.MESSAGE_TYPE));
                    message.content = cursor.getString(cursor
                            .getColumnIndex(DBHelper.MESSAGE_CONTENT));
                    message.status = cursor.getInt(cursor
                            .getColumnIndex(DBHelper.MESSAGE_STATUS));
                    message.attach = cursor.getString(cursor
                            .getColumnIndex(DBHelper.MESSAGE_ATTACH));
                    message.receipt = cursor.getString(cursor
                            .getColumnIndex(DBHelper.MESSAGE_receipt));
                    message.MsgId = cursor.getLong(cursor
                            .getColumnIndex(DBHelper.MESSAGE_MgsId));
                    msglist.add(message);
                }
            } catch (Exception e) {
                LogUtil.d("数据库尝试插入新数据失败");
            } finally {
                if (cursor != null)
                    dbHelper.closeCursor(cursor);

                dbHelper.close();
            }

            Message msg = new Message();
            msg.obj = msglist;

            if (isScroll)
                msg.what = MSG_SCROLL_DOWN;
            else
                msg.what = MSG_SCROLL_NOT;

            LogUtil.d("loaddatathread " + msglist.size() + thedbmsgNum);
            myHandler.sendMessage(msg);
        }
    }

    // 注册广播
    private void regReceiver() {
        receiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AlarmReceiver.ACTION_NEW_MESSAGE);
        registerReceiver(receiver, filter);
    }

    // 监听新消息 广播
    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            LogUtil.d("breadcast 1");
            if (bundle == null)
                return;
            String strIds = bundle.getString("contactIds");
            LogUtil.d("breadcast 2");
            if (strIds == null)
                return;
            LogUtil.d("breadcast 3");
            if (mListContacts == null)
                return;
            LogUtil.d("breadcast 4");
            if (mListContacts.size() != 1)
                return;

            String[] conatactArray = strIds.split(":");
            LogUtil.d("breadcast 5");
            // 有当前联系人的新消息
            for (String str : conatactArray) {
                if (str != "" && (Long.valueOf(str) == mListContacts.get(0).contactId)) {
                    new LoadDataThread(true, mListContacts.get(0).contactId)
                            .start();
                    thedbmsgNum++;
                    LogUtil.d("广播接收者接收消息成功" + bundle.getString("content") + ",有可能这里是空或者Null，，，，" + bundle.getString("id"));
                } else {
                    LogUtil.d("contact str is null or not the one");
                    Toast.makeText(MessageChat.this, "消息是空，或者联系人不是一个人", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }
}
