package easyway.Mobile.Message;

import java.io.File;

import easyway.Mobile.Data.ZWTMessage;
import easyway.Mobile.util.CommonFunc;
import easyway.Mobile.util.CommonUtils;
import easyway.Mobile.util.LogUtil;
import android.content.Context;

// 下载短消息附件
public class DownloadAttachThread extends Thread {
	private Context context;
	private ZWTMessage message;
	private IOnDataChange iOnDataChange;
	public final static String MESSAGE_FILE_PATH = "Uploaded/Message/";
	
	public DownloadAttachThread(Context context, ZWTMessage message,
			IOnDataChange iOnDataChange) {
		this.context = context;
		this.message = message;
		this.iOnDataChange = iOnDataChange;
	}

	public void run() {
		if (message.attach == null || message.attach.length() == 0)
			return;
		
		String filename = "";
		if (message.attach.contains("/"))
			filename = CommonUtils.getFileNameFromPath(message.attach);
		else
			filename = message.attach;
		
		LogUtil.i("filename -->" + filename);
		String filesavepath = CommonUtils.getFilePath(context);
		
		if (CommonUtils.fileIsExists(filesavepath + filename)) {
			return;
		} else {
	        File filepath = new File(filesavepath);
	        if (!filepath.exists())
	        	filepath.mkdirs();

			iOnDataChange.onAttachDownload(message.Id, ZWTMessage.STATUS_DATTACH_ING);
			if (CommonUtils.getFileFromServer(CommonFunc.GetServer(context)
					+ MESSAGE_FILE_PATH + filename, filesavepath
					+ filename)) {
				iOnDataChange.onAttachDownload(message.Id, ZWTMessage.STATUS_DATTACH_SUCC);
			} else {
				iOnDataChange.onAttachDownload(message.Id,
						ZWTMessage.STATUS_DATTACH_FAIL);
			}
		}
	}
}
