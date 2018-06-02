package easyway.Mobile.util;

import easyway.Mobile.R;
import android.app.ProgressDialog;
import android.content.Context;

public class ShowProgress {
	public static ProgressDialog mProDialog;

	public static ProgressDialog getmProDialog() {
		return mProDialog;
	}

	public static void setmProDialog(ProgressDialog mProDialog) {
		ShowProgress.mProDialog = mProDialog;
	}

	public static void showProgressDialog(String message, Context context) {
		showProgressDialog(message, false, context);
	}

	public static void closeProgressDialog() {
		if (mProDialog != null) {
			mProDialog.dismiss();
			mProDialog = null;
		}
	}

	public static void showProgressDialog(String message, boolean flag,
			Context context) {
		if (mProDialog == null) {

			mProDialog = new ProgressDialog(context);

		}

		if (!mProDialog.isShowing()) {
			mProDialog.dismiss();
			mProDialog.setMessage(message);
			mProDialog.setIndeterminate(false);
			mProDialog.setCancelable(flag);
			mProDialog.setIcon(R.drawable.waiting);
			mProDialog.show();
		} else {
			mProDialog.setMessage(message);
		}
	}
}
