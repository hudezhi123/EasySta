package easyway.Mobile.Shift;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.TB_TASK_PlanReal;
import easyway.Mobile.Login.LoginFrame;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;

public class ShiftOut_Task_List extends ActivityEx implements OnGestureListener
{
    private String tag = "Shift_Out";
    private Handler handel = new Handler();
    private ProgressDialog progDialog;
    private ArrayList<TB_TASK_PlanReal> taskList = new ArrayList<TB_TASK_PlanReal>();
    private int startIndex = 0, limit = 10;
    private long totalItems = 0;
    private GestureDetector detector;
    private ShiftOut_Task_List_Adapter shift_out_adapter;
    private String funcName = "";
    private long shift_Id;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shift_out_task);
        detector = new GestureDetector(this, this);
        GridView gvList = (GridView) findViewById(R.id.gvList);
        gvList.setOnTouchListener(new OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event)
            {
                return detector.onTouchEvent(event);
            }
        });

        Button btnShiftIn = (Button) findViewById(R.id.btnShiftIn);
        btnShiftIn.setVisibility(View.GONE);
        btnShiftIn.setOnClickListener(shiftInLis());

        Intent intent = getIntent();
        if (intent != null)
        {
            Bundle bundle = intent.getExtras();
            shift_Id = bundle.getLong("Shift_Id");
            ShowAllTask4Shift();
        }
        else
        {
        	errMsg = getString(R.string.exp_params);
            handel.post(mUpdateError);
        }

    }

    private OnClickListener shiftInLis()
    {
        return new OnClickListener()
        {

            public void onClick(View v)
            {
                AlertDialog.Builder builder = new Builder(
                        ShiftOut_Task_List.this);
                builder.setIcon(R.drawable.information);
                builder.setMessage(R.string.Shift_In_Confirm);
                builder.setTitle(R.string.Prompt);
                builder.setPositiveButton(R.string.Shift_In_OK,
                        new DialogInterface.OnClickListener()
                        {

                            public void onClick(DialogInterface dialog,
                                    int which)
                            {
                                funcName = "ShiftIn";
                                progDialog = ProgressDialog.show(
                                        ShiftOut_Task_List.this,
                                        getString(R.string.Waiting),
                                        getString(R.string.SavingData), true,
                                        false);
                                progDialog.setIndeterminate(true);
                                progDialog.setCancelable(true);
                                progDialog.setIcon(R.drawable.waiting);

                                Thread thread = new Thread(null,
                                        doBackgroundThreadProcessing,
                                        "getAllPlayRecords");
                                thread.start();
                            }
                        });
                builder.setNegativeButton(R.string.Shift_Out_Cancel,
                        null);
                builder.create().show();

            }
        };
    }

    final Runnable mUpdateResults = new Runnable()
    {

        public void run()
        {
            if (progDialog != null)
            {
                progDialog.dismiss();
            }

            if (funcName.equals("ShowAllTask4Shift"))
            {
                GridView gvList = (GridView) findViewById(R.id.gvList);

                shift_out_adapter = new ShiftOut_Task_List_Adapter(
                        ShiftOut_Task_List.this, taskList);
                gvList.setAdapter(shift_out_adapter);

                Button btnShiftIn = (Button) findViewById(R.id.btnShiftIn);
                btnShiftIn.setVisibility(View.VISIBLE);
            }

            if (funcName.equals("ShiftIn"))
            {
            	errMsg = getString(R.string.Shift_In_Success);
                showErrMsg(errMsg);
                Intent intent = new Intent();
                Bundle b = new Bundle();
                b.putLong("ShiftId", shift_Id);
                intent.putExtras(b);
                setResult(200, intent);
            }

        }

    };

    final Runnable mUpdateError = new Runnable()
    {
        public void run()
        {
            if (progDialog != null)
            {
                progDialog.dismiss();
            }

//            showErrMsg(errMsg);
            Intent go2Login = new Intent(ShiftOut_Task_List.this,LoginFrame.class);
			startActivity(go2Login);
        }
    };

    private void ShowAllTask4Shift()
    {
        progDialog = ProgressDialog.show(ShiftOut_Task_List.this,
                getString(R.string.Waiting), getString(R.string.GettingData), true,
                false);
        progDialog.setIndeterminate(true);
        progDialog.setCancelable(true);
        progDialog.setIcon(R.drawable.waiting);
        funcName = "ShowAllTask4Shift";

        Thread thread = new Thread(null, doBackgroundThreadProcessing,
                "getAllPlayRecords");
        thread.start();
    }

    private Runnable doBackgroundThreadProcessing = new Runnable()
    {

        public void run()
        {
            String sessionId = Property.SessionId;
            if (sessionId.equals(""))
            {
                Log.d(tag, "Session Id is null");
                errMsg = getString(R.string.lostLoginInfo);
                handel.post(mUpdateError);
                return;
            }

            Looper.prepare();

            try
            {
                if (funcName.equals("ShowAllTask4Shift"))
                {
                    taskList = GetAllTask4Shift(startIndex, limit);
                }

                if (funcName.equals("ShiftIn"))
                {
                    postShiftIn2Server();
                }

                handel.post(mUpdateResults);
            }
            catch (Exception e)
            {
                Log.d(tag, e.getMessage());
                handel.post(mUpdateError);
            }
            Looper.loop();
        }

    };

    private void postShiftIn2Server() throws Exception
    {
        if (shift_Id == 0)
        {
        	errMsg = getString(R.string.exp_params);
            throw new Exception(errMsg);
        }
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("shift_Id", String.valueOf(shift_Id));
        String methodPath = "WebService/Shift.asmx";
        String methodName = "SaveShift";
        WebServiceManager webServiceManager = new WebServiceManager(
                getApplicationContext(), methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        errMsg = JsonUtil.GetJsonString(result, "Msg");
        if (!errMsg.equals(""))
        {
            throw new Exception(errMsg);
        }
    }

    private ArrayList<TB_TASK_PlanReal> GetAllTask4Shift(int startIndex,
            int limit) throws Exception
    {
        ArrayList<TB_TASK_PlanReal> list = new ArrayList<TB_TASK_PlanReal>();
        try
        {
            HashMap<String, String> parmValues = new HashMap<String, String>();
            parmValues.put("sessionId", Property.SessionId);
            parmValues.put("shiftId", String.valueOf(shift_Id));
            parmValues.put("limit", String.valueOf(limit));
            parmValues.put("start", String.valueOf(startIndex));
            String methodPath = "WebService/Task.asmx";
            String methodName = "GetAllTaskView4ShiftOut";
            WebServiceManager webServiceManager = new WebServiceManager(
                    getApplicationContext(), methodName, parmValues);
            String result = webServiceManager.OpenConnect(methodPath);
            errMsg = JsonUtil.GetJsonString(result, "Msg");
            if (!errMsg.equals(""))
            {
                throw new Exception(errMsg);
            }
            JSONArray jsonArray = JsonUtil
                    .GetJsonArray(result, "Data");
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
                TB_TASK_PlanReal task = new TB_TASK_PlanReal();
                /*
                 * task.ActorID =
                 * webServiceManager.GetJsonObjStringValue(jsonObj, "ActorID");
                 * task.ActorName = webServiceManager.GetJsonObjStringValue(
                 * jsonObj, "ActorName");
                 */
                task.TaskName = JsonUtil.GetJsonObjStringValue(
                        jsonObj, "TaskName");
                task.PlanDate = JsonUtil.GetJsonObjStringValue(jsonObj,
                        "PlanDate");
                task.BeginWorkTime = JsonUtil.GetJsonObjStringValue(
                        jsonObj, "BeginWorkTime");
                task.EndWorkTime = JsonUtil.GetJsonObjStringValue(
                        jsonObj, "EndWorkTime");
                task.WorkSpaces = JsonUtil.GetJsonObjStringValue(
                        jsonObj, "WorkSpaces");
                task.LinkRoleID = JsonUtil.GetJsonObjStringValue(
                        jsonObj, "LinkRoleID");
                task.TrainNum = JsonUtil.GetJsonObjStringValue(
                        jsonObj, "TrainNum");
                task.TempID = JsonUtil.GetJsonObjLongValue(jsonObj,
                        "TempID");
                task.TaskLevel = JsonUtil.GetJsonObjLongValue(jsonObj,
                        "TaskLevel");
                task.RBeginWorkTime = JsonUtil.GetJsonObjStringValue(
                        jsonObj, "RBeginWorkTime");
                task.REndWorkTime = JsonUtil.GetJsonObjStringValue(
                        jsonObj, "REndWorkTime");
                task.TaskSta = JsonUtil.GetJsonObjLongValue(jsonObj,
                        "TaskSta");
                task.ExcSta = JsonUtil.GetJsonObjLongValue(jsonObj,
                        "ExcSta");
                task.ID = JsonUtil.GetJsonObjLongValue(jsonObj, "ID");
                // task.WId = webServiceManager.GetJsonObjLongValue(jsonObj,
                // "WId");
                task.TeamId = JsonUtil.GetJsonObjLongValue(jsonObj,
                        "TeamId");
                list.add(task);

            }
            
                totalItems = JsonUtil.GetJsonLong(result, "total");
        }
        catch (Exception ex)
        {
            throw ex;
        }
        return list;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return detector.onTouchEvent(event);
    }

    public boolean onDown(MotionEvent e)
    {
        return false;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY)
    {
        float xDistince = (e1.getX() - e2.getX());
        int imgFeed = 0;

        if (Math.abs(xDistince) > 200)
        {
            if (xDistince > 0)
            {
                imgFeed = 1;
            }
            else
            {
                imgFeed = -1;
            }
        }

        Log.d(tag, "onFling: imgFeed=" + imgFeed);

        if (imgFeed != 0)
        {
            if (imgFeed < 0)
            {
                if (startIndex - limit >= 0)
                {
                    startIndex = startIndex - limit;
                    ShowAllTask4Shift();
                }
            }
            else
            {
                if (totalItems > startIndex + limit)
                {
                    startIndex = startIndex + limit;
                    ShowAllTask4Shift();
                }
            }
        }
        return false;
    }

    public void onLongPress(MotionEvent e)
    {

    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
            float distanceY)
    {
        return false;
    }

    public void onShowPress(MotionEvent e)
    {

    }

    public boolean onSingleTapUp(MotionEvent e)
    {
        // TODO Auto-generated method stub
        return false;
    }
}
