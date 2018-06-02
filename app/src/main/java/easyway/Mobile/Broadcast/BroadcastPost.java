package easyway.Mobile.Broadcast;

import java.util.HashMap;

import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Media.FileAccessEx;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.CommonFunc;
import easyway.Mobile.util.JsonUtil;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

public class BroadcastPost extends AsyncTask<Object, Integer, Void> {
	public Context context;

	private ProgressDialog dialog;
	private String tag = "BroadcastPost";
	public boolean UploadResult, uploading;

	public String filePath, broadArea, broadAreaIds;

	public BroadcastPostHandler handler;

	@Override
	protected void onPreExecute() {
		UploadResult = false;
		uploading = true;
		dialog = new ProgressDialog(context);
		dialog.setIcon(context.getResources().getDrawable(
				R.drawable.earth_upload));
		dialog.setTitle(R.string.uploading);
		dialog.setMessage(context.getString(R.string.uploadPre));
		dialog.setIndeterminate(false);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setProgress(0);
		dialog.show();

	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		dialog.setProgress(progress[0]);
		if (progress.equals(0)) {
			dialog.setMessage(context.getString(R.string.uploadPre));
		} else {
			dialog.setMessage(context.getString(R.string.uploading));
		}
	}

	@Override
	protected void onPostExecute(Void result) {
		try {
			dialog.dismiss();
			uploading = false;
			Message message;
			if (UploadResult) {
				Toast.makeText(context,
						context.getString(R.string.uploadSuccess),
						Toast.LENGTH_LONG).show();
				message = Message.obtain(handler,
						R.string.broad_area_post_success, "");
			} else {
				Toast.makeText(context,
						context.getString(R.string.uploadFailure),
						Toast.LENGTH_LONG).show();
				message = Message.obtain(handler, R.string.uploadFailure, "");
			}

			handler.sendMessage(message);
		} catch (Exception e) {
		}
	}

	private long SaveBroadManual() throws Exception {
		String methodName = "SaveBroadManual";
		String methodPath = "wsinternal.asmx";

		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		broadArea = CommonFunc.ClearSameItem(broadArea, ",");
		broadAreaIds = CommonFunc.ClearSameItem(broadAreaIds, ",");
		parmValues.put("broadArea", broadArea);
		parmValues.put("broadAreaIds", broadAreaIds);

		WebServiceManager webServiceManager = new WebServiceManager(context,
				methodName, parmValues);
		String result = webServiceManager.OpenConnect(methodPath);
		return JsonUtil.GetJsonLong(result, "Data");
	}

	@Override
	protected Void doInBackground(Object... arg0) {
		try {

			long broadcastId = SaveBroadManual();
			if (broadcastId == 0) {
				UploadResult = false;
				publishProgress(0);
				return null;
			}

			dialog.setProgress(0);
			String methodName = "Broad_Post_Manual";

			FileAccessEx fileAccessEx = new FileAccessEx(filePath, 0);
			Long nStartPos = 0l;
			Long length = fileAccessEx.getFileLength();

			int mBufferSize = 1024 * 100; // 每次处理1024 * 100字节
			byte[] buffer = new byte[mBufferSize];
			FileAccessEx.Detail detail;
			long nRead = 0l;
			long nStart = nStartPos;

			int postion = 0;

			while (nStart < length) {
				detail = fileAccessEx.getContent(nStart);
				nRead = detail.length;
				buffer = detail.b;

				nStart += nRead;
				nStartPos = nStart;

				HashMap<String, String> parmValues = new HashMap<String, String>();
				parmValues.put("sessionId", Property.SessionId);
				parmValues.put("broadcastCategoryId",
						String.valueOf(broadcastId));
				parmValues.put("broadAreaIds", broadAreaIds);
				parmValues.put("fileStreamString", Base64.encodeToString(buffer, Base64.DEFAULT));
				parmValues.put("position", String.valueOf(postion));
				parmValues.put("save2Db", String.valueOf(nStart >= length));

				WebServiceManager webServiceManager = new WebServiceManager(
						context, methodName, parmValues);
				String result = webServiceManager
						.OpenConnect("wsinternal.asmx");
				if (result == null) {
					return null;
				}

				if (result.equals("")) {
					break;
				}
				long retValue = JsonUtil.GetJsonLong(result, "Data");
				if (retValue == 0) {
					UploadResult = false;
				}
				Log.d(tag, "File Path is " + filePath + ",Postion is "
						+ postion);
				int progress = (int) ((nStart / (float) length) * 100);
				dialog.setProgress(progress);
				postion += 1;

			}
			dialog.setProgress(100);
			UploadResult = true;

		} catch (Exception ex) {
			UploadResult = false;
			publishProgress(0);
			return null;
		}
		return null;
	}

}
