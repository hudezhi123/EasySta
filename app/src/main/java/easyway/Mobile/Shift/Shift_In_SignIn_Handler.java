package easyway.Mobile.Shift;

import easyway.Mobile.Data.Duty_Staff_Shift;
import android.os.Handler;
import android.os.Message;

public class Shift_In_SignIn_Handler extends Handler {
	private Shift_In_SignIn shiftSignIn;

	public Shift_In_SignIn_Handler(Shift_In_SignIn shiftSignIn) {
		this.shiftSignIn = shiftSignIn;
	}

	@Override
	public void handleMessage(Message message) {
		Duty_Staff_Shift staffShift = (Duty_Staff_Shift) message.obj;
		switch (message.what) {
		case 0:
			// Unchecked
			shiftSignIn.UpdateUnSign(staffShift.staffId, staffShift.staffName,
					true);
			break;
		case 1:
			// Checked
			shiftSignIn.UpdateUnSign(staffShift.staffId, staffShift.staffName,
					false);
			break;
		default:
			break;
		}
	}

}
