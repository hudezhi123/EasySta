package easyway.Mobile.Attach;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Media.FileAccessEx;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.LogUtil;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.Toast;

/*
 * 文件上传
 */
public class FileUploadTask extends AsyncTask<Object, Integer, Integer> {
	public Context context;
	private int mCategory;
	private long mId;
	private ProgressDialog dialog;
	private IFileUpload mIFileUpload;

	public ArrayList<String> attachList;			// 附件列表
	private ArrayList<String> lstFail2Upload; // 失败列表
	
	public static final int CATEGORY_LIVECASE = 0;
	public static final int CATEGORY_SHIFT = 1;
	public static final int CATEGORY_MESSAGE = 2;
	public static final int CATEGORY_TASK = 3;
	public static final int CATEGORY_DEVFAULT = 4;
	public static final int CATEGORY_BROADCAST = 5;
	

	public static final int RET_UPLOAD_OVER = 0;
	public static final int RET_UPLOAD_SUCCEED = 1;
	public static final int RET_UPLOAD_FAIL = 2;
	public static final int RET_FILE_NOT_EXIST = 3;
	
	
	public FileUploadTask(Context ctx, ArrayList<String> filelist, long id, int category, IFileUpload iFileUpload) {
		context = ctx;
		mCategory = category;
		mId = id;
		attachList = filelist;
		mIFileUpload = iFileUpload;
	}
	
	public FileUploadTask(Context ctx, String filepath, long id, int category, IFileUpload iFileUpload) {
		context = ctx;
		mCategory = category;
		mId = id;
		attachList = new ArrayList<String> ();
		attachList.add(filepath);
		mIFileUpload = iFileUpload;
	}

	@Override
	protected void onPreExecute() {
		if (lstFail2Upload == null) {
			lstFail2Upload = new ArrayList<String>();
		}
		lstFail2Upload.clear();

		dialog = new ProgressDialog(context);
		dialog.setIcon(R.drawable.earth_upload);
		dialog.setTitle(R.string.uploadPre);
		dialog.setIndeterminate(false);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setProgress(0);
		dialog.setMax(100);
		dialog.show();
		dialog.setCancelable(false);
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		if (progress[0] == -1) {
			Toast.makeText(context, R.string.exp_connecttimeout, Toast.LENGTH_LONG).show();
		} else {
			dialog.setProgress(progress[0]);
			dialog.setTitle(R.string.uploading);
		}
	}

	@Override
	protected void onPostExecute(Integer result) {
		dialog.dismiss();

		if (mIFileUpload != null) {
			if (result == RET_FILE_NOT_EXIST) {
				mIFileUpload.OnUploadEnd(RET_FILE_NOT_EXIST, null);
			} else if (result == RET_UPLOAD_OVER) {
				if (lstFail2Upload == null || lstFail2Upload.size() == 0) {
					mIFileUpload.OnUploadEnd(RET_UPLOAD_SUCCEED, null);
				} else {
					mIFileUpload.OnUploadEnd(RET_UPLOAD_FAIL, lstFail2Upload);
				}
			}
		}
	}

	@Override
	protected Integer doInBackground(Object... arg0) {
		int progress = 0;

		if (lstFail2Upload == null) {
			lstFail2Upload = new ArrayList<String>();
		}
		lstFail2Upload.clear();

		if (attachList == null || attachList.size() == 0)
			return RET_FILE_NOT_EXIST;

		int currentPos = 0;
		int filenum = attachList.size();
		for (String filePath : attachList) {
			currentPos += 1;
			LogUtil.d("progress -->" + progress);
			progress = (currentPos - 1) * 100/filenum;
			publishProgress(progress);

			FileAccessEx fileAccessEx;
			try {
				fileAccessEx = new FileAccessEx(filePath, 0);
				Long fileLength = fileAccessEx.getFileLength();

				byte[] buffer = new byte[FileAccessEx.PIECE_LENGHT];
				FileAccessEx.Detail detail;
				long nRead = 0l;
				long nStart = 0l;
				boolean completed = false;

				File file = new File(filePath);
				String fileName = file.getName();

				int postTimes = 0;
				JSONObject jsonP = new JSONObject();
				try {
					jsonP.put("Category", mCategory);
					jsonP.put("Id", mId);
					jsonP.put("FileName", fileName);
					if (mCategory == CATEGORY_SHIFT) {
						jsonP.put("DeptId", Property.DeptId);
//						jsonP.put("ShiftType", ShiftType);
					} else if (mCategory == CATEGORY_TASK) {
						jsonP.put("DeptId", Property.DeptId);
						jsonP.put("StaffId", Property.StaffId);
					} else if (mCategory == CATEGORY_BROADCAST) {
//						jsonP.put("BroadAreaIds", BroadAreaIds);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				while (nStart < fileLength) {
					detail = fileAccessEx.getContent(nStart);
					nRead = detail.length;
					buffer = detail.b;

					nStart += nRead;

					if (nStart < fileLength) {
						completed = false;
					} else {
						completed = true;
					}
					
					String methodPath = Constant.MP_ATTACHMENT;
					String methodName = Constant.MN_POST_ATTACH;
					
					HashMap<String, String> parmValues = new HashMap<String, String>();
					if(mCategory == CATEGORY_DEVFAULT){
						parmValues.put("sessionId", Property.SessionId);
						parmValues.put("dfId", String.valueOf(mId));//faultId
//						parmValues.put("fileName", fileName);
						parmValues.put("fileStreamString", Base64.encodeToString(buffer, 0, (int)nRead, Base64.DEFAULT));
//						
						parmValues.put("position", String.valueOf(postTimes));
						parmValues.put("completed", String.valueOf(completed));
						parmValues.put("jsonValues", jsonP.toString());
						
//						parmValues.put("startPos", String.valueOf(0));	
//						methodPath = Constant.MP_DEVFAULT;
//						methodName = Constant.MN_UPLOAD_FILE1;	
						
						methodPath = Constant.MP_ATTACHMENT;
						methodName = Constant.MN_POST_ATTACH;
					}else{
						parmValues.put("sessionId", Property.SessionId);
						parmValues.put("position", String.valueOf(postTimes));
						parmValues.put("completed", String.valueOf(completed));
						parmValues.put("jsonValues", jsonP.toString());
						parmValues.put("fileStreamString", Base64.encodeToString(buffer, 0, (int)nRead, Base64.DEFAULT));						
					}
						
					WebServiceManager webServiceManager = new WebServiceManager(
							context, methodName, parmValues);
					String result = webServiceManager.OpenConnect(methodPath);
					if (result == null || result.equals("")) {
						lstFail2Upload.add(filePath);
						break;
					}

					if(result.equals("false")){
						publishProgress(-1);
						lstFail2Upload.add(filePath);
						break;						
					}
					
					int code = JsonUtil.GetJsonInt(result, "Code");
					if (code != Constant.NORMAL) {
						if (code == Constant.EXP_NET_NO_CONNECT
								|| code == Constant.EXP_NET_CONNECT_ERROR
								|| code == Constant.EXP_NET_CONNECT_TIMEOUT || code == Constant.EXP_NET_SERVICE_ER) {
							publishProgress(-1);
							lstFail2Upload.add(filePath);
							break;
						}
					}
						
					progress = (int) ((currentPos - 1)* 100/filenum  + (nStart * 100 /fileLength )/filenum);
					LogUtil.d("progress -->" + progress);
					publishProgress(progress);
					postTimes++;
					buffer = null;
						
					if (completed)
						LogUtil.i(fileName + " upload succeed!!!");
				}
			} catch (IOException e) {
				lstFail2Upload.add(filePath);
				e.printStackTrace();
				break;
			}
		}
		
		return RET_UPLOAD_OVER;
	}

}
