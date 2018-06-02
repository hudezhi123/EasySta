package easyway.Mobile.Attach;

import java.util.HashMap;

import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/*
 * 取消任务/情况上报等
 */
public class CancelTask extends AsyncTask<Object, Integer, Integer> {
	public Context context;
	private int mCategory;
	private long mId;
	private ProgressDialog dialog;
	private ITaskCancel mITaskCancel;

	public static final int RET_CANCEL_SUCCEED = 0;
	public static final int RET_CANCEL_FAIL = 1;

	public CancelTask(Context ctx, long id, int category,
			ITaskCancel iTaskCancel) {
		context = ctx;
		mCategory = category;
		mId = id;
		mITaskCancel = iTaskCancel;
	}

	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(context);
		dialog.setMessage(context.getString(R.string.cancleing));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.setIcon(R.drawable.waiting);
		dialog.show();
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		if (progress[0] == -1) {
			Toast.makeText(context, R.string.exp_connecttimeout, Toast.LENGTH_LONG).show();
		} else {
			dialog.setMessage(context.getString(R.string.cancleing));
		}
	}

	@Override
	protected void onPostExecute(Integer ret) {
		dialog.dismiss();

		if (mITaskCancel != null) {
			mITaskCancel.OnCancelEnd(ret);
		}
	}

	@Override
	protected Integer doInBackground(Object... arg0) {
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("category", String.valueOf(mCategory));
		parmValues.put("id", String.valueOf(mId));

		String methodPath = Constant.MP_ATTACHMENT;
		String methodName = Constant.MN_DELETE_ATTACH;

		WebServiceManager webServiceManager = new WebServiceManager(
				context, methodName, parmValues);
		String result = webServiceManager.OpenConnect(methodPath);

		if (result == null || result.equals("")) {
			return RET_CANCEL_FAIL;
		}
			
		int code = JsonUtil.GetJsonInt(result, "Code");
		if (code == Constant.NORMAL) {
			return RET_CANCEL_SUCCEED;
		} else {
			if (code == Constant.EXP_NET_NO_CONNECT ||
					code == Constant.EXP_NET_CONNECT_ERROR ||
					code == Constant.EXP_NET_CONNECT_TIMEOUT)
				publishProgress(-1);
			
			return RET_CANCEL_FAIL;
		}
	}

}
