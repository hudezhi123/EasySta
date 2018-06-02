package easyway.Mobile.Shift;

import android.os.Handler;
import android.os.Message;

public class Shift_In_Pool_Handler extends Handler {
	private Shift_In_Pool shiftOutPool;

	public Shift_In_Pool_Handler(Shift_In_Pool shiftOutPool) {
		this.shiftOutPool = shiftOutPool;
	}

	@Override
	public void handleMessage(Message message) {
		switch (message.what) {
		case 1:
			long shift_id = Long.valueOf(message.obj.toString());
			shiftOutPool.SetShiftId(shift_id);
			break;
		case 0:
			String errMsg = (String) message.obj.toString();
			shiftOutPool.SetErrMsg(errMsg);
			break;
		default:
			break;
		}
	}
}
