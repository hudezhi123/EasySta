package easyway.Mobile.DefaultReportToRepair;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


import easyway.Mobile.Media.FileAccessEx;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.LogUtil;

/**
 * Created by boy on 2017/11/30.
 */

public class AttachUploadTask extends AsyncTask<Object, Integer, Integer> {
    public Context context;
    private int mCategory;
    private String repairId;
    private ProgressDialog dialog;
    private IAttachFileUpload mIAttachFileUpload;

    public ArrayList<String> attachList;            // 附件列表
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

    private String msg;


    public AttachUploadTask(Context ctx, ArrayList<String> filelist, String id, int category, IAttachFileUpload mIAttachFileUpload) {
        context = ctx;
        mCategory = category;
        repairId = id;
        attachList = filelist;
        this.mIAttachFileUpload = mIAttachFileUpload;
    }

    public AttachUploadTask(Context ctx, String filepath, String id, int category, IAttachFileUpload mIAttachFileUpload) {
        context = ctx;
        mCategory = category;
        repairId = id;
        attachList = new ArrayList<String>();
        attachList.add(filepath);
        this.mIAttachFileUpload = mIAttachFileUpload;
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
        } else if (progress[0] == -12) {
            Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show();
        } else {
            dialog.setProgress(progress[0]);
            dialog.setTitle(R.string.uploading);
        }
    }

    @Override
    protected void onPostExecute(Integer result) {
        dialog.dismiss();

        if (mIAttachFileUpload != null) {
            if (result == RET_FILE_NOT_EXIST) {
                mIAttachFileUpload.OnUploadEnd(RET_FILE_NOT_EXIST, null);
            } else if (result == RET_UPLOAD_OVER) {
                if (lstFail2Upload == null || lstFail2Upload.size() == 0) {
                    mIAttachFileUpload.OnUploadEnd(RET_UPLOAD_SUCCEED, null);
                } else {
                    mIAttachFileUpload.OnUploadEnd(RET_UPLOAD_FAIL, lstFail2Upload);
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
            progress = (currentPos - 1) * 100 / filenum;
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
                    String methodPath = Constant.MP_REPAIR;
                    String methodName = Constant.UPLOAD_ATTACH;
                    HashMap<String, String> paramValue = new HashMap<String, String>();
                    paramValue.put("sessionId", Property.SessionId);
                    paramValue.put("repairId", repairId);
                    paramValue.put("stationCode", Property.StationCode);
                    paramValue.put("fileName", fileName);
                    paramValue.put("fileStreamString", Base64.encodeToString(buffer, 0, (int) nRead, Base64.DEFAULT));
                    WebServiceManager webServiceManager = new WebServiceManager(
                            context, methodName, paramValue);
                    String result = webServiceManager.OpenConnect(methodPath);

                    if (result == null || result.equals("")) {
                        lstFail2Upload.add(filePath);
                        break;
                    }
                    if (result.equals("false")) {
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
                        } else if (code == Constant.EXCEPTION) {
                            msg = JsonUtil.GetJsonString(result, "Msg");
                            publishProgress(-12);
                        }
                    }
                    progress = (int) ((currentPos - 1) * 100 / filenum + (nStart * 100 / fileLength) / filenum);
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
