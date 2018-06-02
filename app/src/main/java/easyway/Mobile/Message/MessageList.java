package easyway.Mobile.Message;

import java.util.ArrayList;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Application.ExitApplication;
import easyway.Mobile.receiver.AlarmReceiver;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.DBHelper;
import easyway.Mobile.Data.MessageType;
import easyway.Mobile.Data.ZWTMessage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

/*
 * 短信列表
 */
public class MessageList extends ActivityEx {
    private ArrayList<ArrayList<ZWTMessage>> mMsgList = null;
    private ArrayList<MessageType> mTypeList = null;
    public DBHelper dbHelper = null;
    private ExpandableListView mExListView;
    private MessageListAdapter mAdapter = null;
    private MessageReceiver receiver;

    private final int MSG_READ_MESSAGE = 1;
    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            closeProgressDialog();
            switch (msg.what) {
                case MSG_READ_MESSAGE:
                    mMsgList = (ArrayList<ArrayList<ZWTMessage>>) msg.obj;
                    if (mAdapter != null) {
                        mAdapter.setData(mMsgList, mTypeList);
                        mAdapter.notifyDataSetChanged();
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
        setContentView(R.layout.msg_list);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.title_message);

        if (Property.SessionId == null || Property.SessionId.equals(""))
            finish();

        // 删除消息
        Button deleteButton = (Button) findViewById(R.id.mlButtonDelete);
        deleteButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(MessageList.this,
                        DeleteMessage.class);
                startActivity(intent);
            }
        });

        // 新增消息
        Button newButton = (Button) findViewById(R.id.mlButtonNew);
        newButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MessageList.this, MessageChat.class);
                startActivity(intent);
            }
        });

        // 返回按钮
        Button btnReturn = (Button) findViewById(R.id.btnReturn);
        if (btnReturn != null) {
            btnReturn.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    finish();
                }
            });
        }

        // 初始化短消息分类
        initMessageType();
        mExListView = (ExpandableListView) findViewById(R.id.mlExpandableListView);
        mExListView.setGroupIndicator(null);
        mExListView.setChildIndicator(null);
        IOnDataChange iOnDateChange = new IOnDataChange() {

            @Override
            public void onDataChange() {
                ReadMessage();
            }

            @Override
            public void onDataChange(long id, int status) {
                // do noting
            }

            @Override
            public void onTransmit(String content) {
                // do noting
            }

            @Override
            public void onDelete(long id) {
                // do noting
            }

            @Override
            public void onPlay(long id, boolean play) {
                // do noting
            }

            @Override
            public void onAttachDownload(long id, int status) {
                // do noting
            }

            @Override
            public void onSendMessage(ZWTMessage message) {
                // do noting
            }

        };
        mAdapter = new MessageListAdapter(MessageList.this, iOnDateChange);
        mExListView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // 初始化短消息分类
    private void initMessageType() {
        mTypeList = new ArrayList<MessageType>();

        // 短信
        MessageType receiveMsg = new MessageType();
        receiveMsg.id = MessageType.TYPE_NORMAL;
        receiveMsg.name = getResources().getString(R.string.msg_message);

        // 任务提醒
        MessageType dutyNotice = new MessageType();
        dutyNotice.id = MessageType.TYPE_TASK_NOTICE;
        dutyNotice.name = getResources().getString(R.string.msg_dutyNotice);

        // 站内公告
        MessageType siteNotice = new MessageType();
        siteNotice.id = MessageType.TYPE_SITE_NOTICE;
        siteNotice.name = getResources().getString(R.string.msg_siteNotic);

        //设备报障
        MessageType dfNotice = new MessageType();
        dfNotice.id = MessageType.TYPE_DF_NOTICE;
        dfNotice.name = getResources().getString(R.string.msg_dfNotic);

        mTypeList.add(receiveMsg);
        mTypeList.add(dutyNotice);
        mTypeList.add(siteNotice);
        mTypeList.add(dfNotice);
    }

    @Override
    public void onResume() {
        super.onResume();
        dbHelper = new DBHelper(MessageList.this);
        regReceiver();
        ReadMessage();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_HOME) {
            finish();
        }
        return true;
    }

    public void ReadMessage() {
        showProgressDialog(R.string.GettingData);
        new Thread() {
            public void run() {
                for (MessageType msgtype : mTypeList) {
                    msgtype.totalNum = 0;
                    msgtype.unreadNum = 0;
                }
                LoadMessage();
            }
        }.start();
    }

    // 从数据库加载
    private void LoadMessage() {
        ArrayList<ZWTMessage> msglist = new ArrayList<ZWTMessage>();

        String sql = "select * from " + DBHelper.MESSAGE_TABLE_NAME + " where "
                + DBHelper.MESSAGE_OWNERID + " = '" + Property.StaffId
                + "' order by " + DBHelper.MESSAGE_CREATETIME + " desc";

        Cursor cursor = null;

        try {
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
                msglist.add(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                dbHelper.closeCursor(cursor);
        }

        ArrayList<ArrayList<ZWTMessage>> list = new ArrayList<ArrayList<ZWTMessage>>();
        ArrayList<ZWTMessage> listnormal = new ArrayList<ZWTMessage>();
        ArrayList<ZWTMessage> listtask = new ArrayList<ZWTMessage>();
        ArrayList<ZWTMessage> listsite = new ArrayList<ZWTMessage>();
        ArrayList<ZWTMessage> listdf = new ArrayList<ZWTMessage>();


        for (ZWTMessage message : msglist) {
            switch (message.type) {
                case MessageType.TYPE_NORMAL:    // 普通
                case MessageType.TYPE_SEND:    // 已发送
                    if (message.status == ZWTMessage.STATUS_UNREAD) {
                        mTypeList.get(0).unreadNum++;
                    }
                    mTypeList.get(0).totalNum++;
                    boolean in = false;
                    for (ZWTMessage msgnormal : listnormal) {
                        if (msgnormal.contactId == message.contactId) {
                            in = true;

                            if (message.status == ZWTMessage.STATUS_UNREAD)
                                msgnormal.status = ZWTMessage.STATUS_UNREAD;
                            break;
                        }
                    }

                    if (!in)
                        listnormal.add(message);

                    break;
                case MessageType.TYPE_TASK_NOTICE:        // 任务提醒
                    listtask.add(message);
                    if (message.status == ZWTMessage.STATUS_UNREAD) {
                        mTypeList.get(1).unreadNum++;
                    }
                    mTypeList.get(1).totalNum++;
                    break;
                case MessageType.TYPE_SITE_NOTICE:        // 站内通告
                    listsite.add(message);
                    if (message.status == ZWTMessage.STATUS_UNREAD) {
                        mTypeList.get(2).unreadNum++;
                    }
                    mTypeList.get(2).totalNum++;
                    break;
                case MessageType.TYPE_DF_NOTICE:        // 设备报障
                    listdf.add(message);
                    if (message.status == ZWTMessage.STATUS_UNREAD) {
                        mTypeList.get(3).unreadNum++;
                    }
                    mTypeList.get(3).totalNum++;
                    break;
            }
        }

        list.add(listnormal);
        list.add(listtask);
        list.add(listsite);
        list.add(listdf);

        Message msg = new Message();
        msg.what = MSG_READ_MESSAGE;
        msg.obj = list;
        myHandler.sendMessage(msg);
    }

    ;

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void onPause() {
        super.onPause();

        if (dbHelper != null)
            dbHelper.close();

        if (receiver != null) {
            try {
                unregisterReceiver(receiver);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void regReceiver() {
        receiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AlarmReceiver.ACTION_NEW_MESSAGE);
        registerReceiver(receiver, filter);
    }

    private class MessageReceiver extends BroadcastReceiver {
        // 自定义一个广播接收器
        @Override
        public void onReceive(Context context, Intent intent) {
            ReadMessage();
        }
    }
}
