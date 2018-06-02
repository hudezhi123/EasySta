package easyway.Mobile.Message;

import easyway.Mobile.Data.ZWTMessage;

public interface IOnDataChange {
	void onDataChange();			// 数据改变
	void onDataChange(long id, int status);		//数据改变
	void onTransmit(String content);		// 转发消息
	void onDelete(long id);		// 删除消息
	void onPlay(long id, boolean play);		// 播放音频
	void onAttachDownload(long id, int status);	// 下载附件
	void onSendMessage(ZWTMessage message);	// 发送消息
}