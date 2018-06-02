package easyway.Mobile.Data;

import java.util.List;

import easyway.Mobile.util.DBHelper;
import easyway.Mobile.util.DateUtil;
import easyway.Mobile.util.LogUtil;

import android.content.Context;

public class ZWTMessage {
    public Long Id = -1L;
    public int type;
    public long ownerId;
    public String ownerName;
    public long contactId;
    public String contactName;
    public String content;
    public String createTime;
    public String attach;
    public long MsgId;
    public int status;
    public boolean isplay = false;
    public int flag = FLAG_MESSAGE;
    public String receipt;

    public int dattachstatus = STATUS_DATTACH_FAIL;

    public static final int STATUS_UNREAD = 0; // 未读
    public static final int STATUS_READED = 1; // 已读
    public static final int STATUS_UNSEND = 2; // 未发送
    public static final int STATUS_SENDFAIL = 3; // 发送失败
    public static final int STATUS_SENDED = 4; // 已发送
    public static final int STATUS_SENDING = 5; // 发送中

    public static final int FLAG_SMS = 0;
    public static final int FLAG_MESSAGE = 1;

    public static final int FLAG_BOTH = 2; // FLAG_SMS + FLAG_MESSAGE

    public static final int STATUS_DATTACH_SUCC = 10;
    public static final int STATUS_DATTACH_FAIL = 11;
    public static final int STATUS_DATTACH_ING = 12;

    public static void Insert(Context context, List<ZWTMessage> list) {
        if (list == null)
            return;

        if (list.size() == 0)
            return;

        for (ZWTMessage message : list) {
            Insert(context, message);
        }

    }

    public static void Insert(Context context, ZWTMessage message) {
        if (message == null)
            return;

        DBHelper dbHelper = new DBHelper(context);
        try {
            message.createTime = DateUtil.getNowDate();
            String sql = "insert into " + DBHelper.MESSAGE_TABLE_NAME + "("
                    + DBHelper.MESSAGE_ATTACH + "," + DBHelper.MESSAGE_CONTENT
                    + "," + DBHelper.MESSAGE_OWNERID + ","
                    + DBHelper.MESSAGE_OWNERNAME + ","
                    + DBHelper.MESSAGE_STATUS + ","
                    + DBHelper.MESSAGE_CONTACTID + ","
                    + DBHelper.MESSAGE_CONTACTNAME + ","
                    + DBHelper.MESSAGE_TYPE + "," + DBHelper.MESSAGE_FLAG + ","
                    + DBHelper.MESSAGE_receipt + ","
                    + DBHelper.MESSAGE_MgsId + ","
                    + DBHelper.MESSAGE_ID + ","
                    + DBHelper.MESSAGE_CREATETIME + ") values ('"
                    + message.attach + "','" + message.content + "','"
                    + message.ownerId + "','" + message.ownerName + "','"
                    + message.status + "','" + message.contactId + "','"
                    + message.contactName + "','" + message.type + "','"
                    + message.flag + "','" + message.receipt + "','"
                    + message.MsgId + "','"
                    + message.Id + "','"
                    + message.createTime + "');";
            dbHelper.execSQL(sql);
            LogUtil.d("消息的内容" + message.content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbHelper.close();
        }
    }

    public static void UpdateStatus(Context context, ZWTMessage message) {
        if (message == null)
            return;

        DBHelper dbHelper = new DBHelper(context);
        try {
            String sql = "update " + DBHelper.MESSAGE_TABLE_NAME + " set "
                    + DBHelper.MESSAGE_STATUS + " = '" + message.status
                    + "' where " + DBHelper.MESSAGE_ID + " = '" + message.Id
                    + "';";

            dbHelper.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbHelper.close();
        }
    }

    public static void UpdateStatus(Context context, long msgId, int status) {
        DBHelper dbHelper = new DBHelper(context);
        try {
            String sql = "update " + DBHelper.MESSAGE_TABLE_NAME + " set "
                    + DBHelper.MESSAGE_STATUS + " = '" + status
                    + "' where "
                    + DBHelper.MESSAGE_ID + " = '" + msgId
                    + "';";

            dbHelper.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbHelper.close();
        }
    }

    public static void Delete(Context context, ZWTMessage message) {
        if (message == null)
            return;

        DBHelper dbHelper = new DBHelper(context);
        try {
            String sql = "delete from " + DBHelper.MESSAGE_TABLE_NAME
                    + " where " + DBHelper.MESSAGE_ID + "= '" + message.Id
                    + "';";

            dbHelper.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbHelper.close();
        }
    }

}
