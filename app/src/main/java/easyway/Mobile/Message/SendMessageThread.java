package easyway.Mobile.Message;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import easyway.Mobile.Property;
import easyway.Mobile.Data.MessageContact;
import easyway.Mobile.Data.MessageType;
import easyway.Mobile.Data.ZWTMessage;
import easyway.Mobile.Media.FileAccessEx;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.CommonFunc;
import easyway.Mobile.util.CommonUtils;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.LogUtil;

import android.content.Context;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;

/*
 * 短信发送线程
 */
public class SendMessageThread extends Thread {
    private ArrayList<MessageContact> contacts; // 接收人
    private ZWTMessage message; // 消息
    private Context context;
    private IOnDataChange iOnDataChange;

    public SendMessageThread(Context context, ZWTMessage message,
                             ArrayList<MessageContact> contacts, IOnDataChange iOnDataChange) {
        this.context = context;
        this.message = message;
        this.contacts = contacts;
        this.iOnDataChange = iOnDataChange;
    }

    public void run() {
        LogUtil.d("SendMessageThread start !");

        try {
            if (contacts == null || contacts.size() == 0) {
                return;
            }

            UpdateStatus(ZWTMessage.STATUS_SENDING); // 更新消息状态

            String staffIdList = "";
            String staffNameList = "";
            String fileName = "";

            if (message.content == null || message.content.length() == 0) { // 发送的是文件
                if (CommonUtils.fileIsExists(message.attach)) { // 判断文件是否存在
                    FileAccessEx fileAccessEx = new FileAccessEx(
                            message.attach, 0);
                    Long nStartPos = 0l;
                    Long length = fileAccessEx.getFileLength();
                    int mBufferSize = 1024 * 100; // 每次处理1024 * 100字节
                    byte[] buffer = new byte[mBufferSize];
                    FileAccessEx.Detail detail;
                    long nRead = 0l;
                    long nStart = nStartPos;

                    File file = new File(message.attach);
                    fileName = file.getName();
                    int postTimes = 0;
                    while (nStart < length) {
                        detail = fileAccessEx.getContent(nStart);
                        nRead = detail.length;
                        buffer = detail.b;

                        nStart += nRead;
                        nStartPos = nStart;

                        HashMap<String, String> parmValues = new HashMap<String, String>();
                        parmValues.put("sessionId", Property.SessionId);
                        parmValues.put("fileName", fileName);
                        parmValues.put("startPos", String.valueOf(postTimes));

                        try {
                            parmValues.put("fileStreamString", Base64
                                    .encodeToString(buffer, 0, (int) nRead,
                                            Base64.DEFAULT));

                            String methodPath = Constant.MP_SMS;
                            String methodName = Constant.MN_UPLOAD_FILE;

                            WebServiceManager webServiceManager = new WebServiceManager(
                                    context, methodName, parmValues);
                            String result = webServiceManager
                                    .OpenConnect(methodPath);

                            if (result == null || result.length() == 0) {
                                UpdateStatus(ZWTMessage.STATUS_SENDFAIL);
                                return;
                            }

                            if (Boolean.valueOf(result)) {
                                LogUtil.d("Upload Successfully");
                            } else {
                                UpdateStatus(ZWTMessage.STATUS_SENDFAIL);
                                return;
                            }
                            System.gc();
                            postTimes += 1;

                            buffer = null;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            UpdateStatus(ZWTMessage.STATUS_SENDFAIL);
                            return;
                        }
                    }
                }
            }

            for (MessageContact contact : contacts) {
                if (staffIdList.length() == 0) {
                    staffIdList += contact.contactId;
                    staffNameList += contact.contactName;
                } else {
                    staffIdList += ";" + contact.contactId;
                    staffNameList += ";" + contact.contactName;
                }
            }
            HashMap<String, String> parmValues = new HashMap<String, String>();
            parmValues.put("sessionId", Property.SessionId);
            parmValues.put("senderId", Long.toString(Property.StaffId));
            parmValues.put("senderName", Property.UserName);
            parmValues.put("senderIp", CommonFunc.getLocalIpAddress());
            parmValues.put("context", message.content);
            parmValues.put("staffIdList", staffIdList);
            parmValues.put("staffNameList", staffNameList);
            parmValues.put("messageType", Integer.toString(message.flag));
            parmValues.put("attachList", fileName);
            parmValues.put("contentType",
                    Integer.toString(MessageType.TYPE_NORMAL));

            parmValues.put("taskId", Integer.toString(0));
            parmValues.put("saId", Integer.toString(0));

            parmValues.put("receipt", message.receipt);

            String methodPath = Constant.MP_SMS;
            String methodName = Constant.MN_SEND_MESSAGE;

            long startTime = SystemClock.uptimeMillis();

            WebServiceManager webServiceManager = new WebServiceManager(
                    context, methodName, parmValues);
            String result = webServiceManager.OpenConnect(methodPath);


            long now = SystemClock.uptimeMillis();
            long l = now - startTime;
            Log.e("MyErrorLog", "发送消息返回的结果" + result);

            if (result == null || result.equals("")) {
                UpdateStatus(ZWTMessage.STATUS_SENDFAIL); // 更新消息状态
            } else {
                int Code = JsonUtil.GetJsonInt(result, "Code");
                switch (Code) {
                    case Constant.NORMAL:
                        UpdateStatus(ZWTMessage.STATUS_SENDED); // 更新消息状态
                        break;
                    case Constant.EXCEPTION:
                        // 异常处理,这里发送失败，所以走到默认的那里。里面会重新发送。
                    default:
                        UpdateStatus(ZWTMessage.STATUS_SENDFAIL); // 更新消息状态
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            UpdateStatus(ZWTMessage.STATUS_SENDFAIL); // 更新消息状态
        }
    }

    // 更新状态
    private void UpdateStatus(int status) {
        message.status = status;
        if (contacts.size() == 1) {
            ZWTMessage.UpdateStatus(context, message);
        } else {
            message.status = status;
            if (status == ZWTMessage.STATUS_SENDED) {
                for (MessageContact contact : contacts) {
                    message.contactId = contact.contactId;
                    message.contactName = contact.contactName;
                    ZWTMessage.Insert(context, message);
                }
            }
        }

        if (iOnDataChange != null)
            iOnDataChange.onDataChange(message.Id, status);
    }
}
