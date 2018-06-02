package easyway.Mobile.Shift;

import java.util.HashMap;

import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Media.FileAccessEx;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.R.string;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

public class Shift_Out_Detail_Post_Task extends
        AsyncTask<Object, Integer, Void>
{

    private String trainNo, descriptions, filePath;
    private ProgressDialog dialog;
    private String tag = "Shift_Out_Detail_Post_Task";
    private Context context;
    public boolean uploadResult = true;
    public Shift_Out_Handler handler;
    private long teamId=0;

    public Shift_Out_Detail_Post_Task(Context context, String trainNo,
             String descriptions, String filePath,long teamId)
    {
    	this.context=context;
        this.trainNo = trainNo;
        this.descriptions = descriptions;
        this.filePath = filePath;
        this.teamId=teamId;
    }

    @Override
    protected void onPreExecute()
    {
        dialog = new ProgressDialog(context);
        dialog.setIcon(context.getResources().getDrawable(
                R.drawable.earth_upload));
        dialog.setTitle(context.getString(string.uploading));
        dialog.setMessage(context.getString(string.uploadPre));
        dialog.setIndeterminate(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setProgress(0);
        dialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... progress)
    {
        dialog.setProgress(progress[0]);
        if (progress.equals(0))
        {
            dialog.setMessage(context.getString(string.uploadPre));
        }
        else
        {
            dialog.setMessage(context.getString(string.uploading));
        }
    }

    @Override
    protected void onPostExecute(Void result)
    {
        try
        {
            dialog.dismiss();
            Message message;
            if (uploadResult)
            {
                message = Message.obtain(handler, 1, "");
                Toast.makeText(context,
                        context.getString(string.uploadSuccess),
                        Toast.LENGTH_LONG).show();
            }
            else
            {
                message = Message.obtain(handler, 0, "");
                Toast.makeText(context,
                        context.getString(string.uploadFailure),
                        Toast.LENGTH_LONG).show();
            }
            handler.sendMessage(message);
        }
        catch (Exception e)
        {
        }
    }

    @Override
    protected Void doInBackground(Object... params)
    {
        dialog.setProgress(0);
        String methodName = "SaveDetail";
        try
        {
            if (filePath.equals(""))
            {
                HashMap<String, String> parmValues = new HashMap<String, String>();
                parmValues.put("sessionId", Property.SessionId);
                parmValues.put("trainNo", trainNo);
                parmValues.put("teamId", String.valueOf(teamId));
                parmValues.put("shiftId", "0");
                parmValues.put("descriptions", descriptions);
                parmValues.put("fileStreamString", null);
                parmValues.put("position", String.valueOf(0));

                WebServiceManager webServiceManager = new WebServiceManager(
                        context, methodName, parmValues);
                String result = webServiceManager
                        .OpenConnect("webservice/Shift.asmx");
                if (result == null)
                {
                    uploadResult = false;
                    return null;
                }

                if (result.equals(""))
                {
                    uploadResult = false;
                    return null;
                }
                String errMsg = JsonUtil.GetJsonString(result,
                        "Msg");
                if (!errMsg.equals(""))
                {
                    uploadResult = false;
                    return null;
                }
                dialog.setProgress(100);
                uploadResult = true;
                return null;
            }

            FileAccessEx fileAccessEx = new FileAccessEx(filePath, 0);
            Long nStartPos = 0l;
            Long length = fileAccessEx.getFileLength();

            int mBufferSize = 1024 * 100; // 每次处理1024 * 100字节
            byte[] buffer = new byte[mBufferSize];
            FileAccessEx.Detail detail;
            long nRead = 0l;
            long nStart = nStartPos;

            int postion = 0;
            while (nStart < length)
            {
                detail = fileAccessEx.getContent(nStart);
                nRead = detail.length;
                buffer = detail.b;

                nStart += nRead;
                nStartPos = nStart;

                HashMap<String, String> parmValues = new HashMap<String, String>();
                parmValues.put("sessionId", Property.SessionId);
                parmValues.put("trainNo", trainNo);
                parmValues.put("teamId", String.valueOf(teamId));
                parmValues.put("shiftId", "0");
                parmValues.put("descriptions", descriptions);
                parmValues.put("fileStreamString", Base64.encodeToString(buffer, Base64.DEFAULT));
                parmValues.put("position", String.valueOf(postion));

                WebServiceManager webServiceManager = new WebServiceManager(
                        context, methodName, parmValues);
                String result = webServiceManager
                        .OpenConnect("webservice/Shift.asmx");
                if (result == null)
                {
                    uploadResult = false;
                    return null;
                }

                if (result.equals(""))
                {
                    uploadResult = false;
                    return null;
                }
                String errMsg = JsonUtil.GetJsonString(result,
                        "errMsg");
                if (!errMsg.equals(""))
                {
                    uploadResult = false;
                    return null;
                }

                Log.d(tag, "File Path is " + filePath + ",Postion is "
                        + postion);
                int progress = (int) ((nStart / (float) length) * 100);
                dialog.setProgress(progress);
                postion += 1;

            }
            dialog.setProgress(100);
            uploadResult = true;
        }
        catch (Exception ex)
        {
            uploadResult = false;
        }

        return null;
    }

}
