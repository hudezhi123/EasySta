package easyway.Mobile.Shift;

import java.io.File;
import java.util.HashMap;

import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Media.FileAccessEx;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.Toast;

public class Shift_Out_Upload_Voice_Task extends
		AsyncTask<Object, Integer, Void> {

	private Context context;
	private String filePath = "";
	private long shiftId = 0,teamId=0;
	private ProgressDialog dialog;
	private boolean uploadResult=false,isShiftOut=false;
	

	public Shift_Out_Upload_Voice_Task(Context context, String filePath,
			long shiftId,long teamId,boolean isShiftOut) {
		this.context = context;
		this.filePath = filePath;
		this.shiftId = shiftId;
		this.teamId=teamId;
		this.isShiftOut=isShiftOut;
	}
	
	@Override
    protected void onPreExecute()
    {
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
    protected void onProgressUpdate(Integer... progress)
    {
        dialog.setProgress(progress[0]);
        if (progress.equals(0))
        {
            dialog.setMessage(context.getString(R.string.uploadPre));
        }
        else
        {
            dialog.setMessage(context.getString(R.string.uploading));
        }
    }
    
    @Override
    protected void onPostExecute(Void result)
    {
        try
        {
            dialog.dismiss();
            if (uploadResult)
            {
                Toast.makeText(context,
                        R.string.uploadSuccess,
                        Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(context,
                        R.string.uploadFailure,
                        Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e)
        {
        }
    }

	@Override
	protected Void doInBackground(Object... params) {
		dialog.setProgress(0);
		
        String methodName = "";
        if (isShiftOut)
        {
        	methodName = "UploadShiftOutVoice";
        } else {
        	methodName = "UploadShiftInVoice";
        }
        try
        {
            if (filePath.equals(""))
            {                
            	uploadResult = false;
                dialog.setProgress(100);
                return null;
            }
            
            File file=new File(filePath);
            if (! file.exists())
            {
            	uploadResult = false;
                dialog.setProgress(100);
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
                parmValues.put("shiftId", String.valueOf(shiftId));
                parmValues.put("teamId", String.valueOf(teamId));
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
                        "Msg");
                if (!errMsg.equals(""))
                {
                    uploadResult = false;
                    return null;
                }
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
