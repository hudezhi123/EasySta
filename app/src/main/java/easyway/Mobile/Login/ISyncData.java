package easyway.Mobile.Login;

import android.os.Handler;

public interface ISyncData {
	void SyncEnd(boolean ret);		// 数据同步结果
	Handler getHandler();
}
