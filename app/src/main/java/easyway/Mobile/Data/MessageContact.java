package easyway.Mobile.Data;

// 联系人 （短消息）
public class MessageContact {
	public String contactName;
	public long contactId;
	public boolean BOnline = false;

	@Override
	public String toString() {
		return "MessageContact{" +
				"contactName='" + contactName + '\'' +
				", contactId=" + contactId +
				", BOnline=" + BOnline +
				'}';
	}
}
