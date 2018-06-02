package easyway.Mobile.Data;

// 短消息大分类
public class MessageType {
	public int id;
	public String name;
	public int totalNum;
	public int unreadNum;
	
	public static final int TYPE_SEND = 0;					// 已发送
	public static final int TYPE_NORMAL = 1;				// 短消息
	public static final int TYPE_TASK_NOTICE = 2;		// 任务提醒
	public static final int TYPE_SITE_NOTICE = 3;			// 站内公告
	public static final int TYPE_DF_NOTICE = 10;			// 报障
}
