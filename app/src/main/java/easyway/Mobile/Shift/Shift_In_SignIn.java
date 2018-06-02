package easyway.Mobile.Shift;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.Duty_Staff_Shift;
import easyway.Mobile.Login.LoginFrame;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;

public class Shift_In_SignIn extends ActivityEx
{

    private String tag = "Shift_In_SignIn";
    private Handler handel = new Handler();
    private ProgressDialog progDialog;
    private ArrayList<Duty_Staff_Shift> staff_Shift_List = new ArrayList<Duty_Staff_Shift>();
    private HashMap<Long, String> hmUnSignSigned = new HashMap<Long, String>();
    private String funcName = "";

    private Shift_In_SignIn_Adapter shiftInSignInAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shift_in_sign_in);
        
        TextView labTitle = (TextView) findViewById(R.id.title);
        labTitle.setText(R.string.Shift_In_Sign);

        Button btnShift_In_Sign = (Button) findViewById(R.id.btnShift_In_Sign), 
        		btnShift_In_SelectAll = (Button) findViewById(R.id.btnShift_In_SelectAll),
        		btnShift_In_UnSelectAll = (Button) findViewById(R.id.btnShift_In_UnSelectAll)
        		;
        btnShift_In_Sign.setOnClickListener(postSignLis());
        btnShift_In_SelectAll.setOnClickListener(selectAllLis());
        btnShift_In_UnSelectAll.setOnClickListener(unSelectAllLis());

        ShowStaffSignInfo();
    }

    private OnClickListener selectAllLis()
    {
        return new OnClickListener()
        {
            public void onClick(View v)
            {
                hmUnSignSigned.clear();
                shiftInSignInAdapter.notifyDataSetChanged();
            }
        };
    }
    
    private OnClickListener unSelectAllLis()
    {
        return new OnClickListener()
        {
            public void onClick(View v)
            {
                hmUnSignSigned.clear();
                
                for(Duty_Staff_Shift dutyStaffShift : staff_Shift_List)
                {
                	hmUnSignSigned.put(dutyStaffShift.staffId, dutyStaffShift.staffName);
                }
                shiftInSignInAdapter.notifyDataSetChanged();
            }
        };
    }

    private OnClickListener postSignLis()
    {
        return new OnClickListener()
        {
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new Builder(Shift_In_SignIn.this);
                builder.setIcon(R.drawable.information);
                builder.setMessage(R.string.Shift_In_Sign_Confirm);
                builder.setTitle(R.string.Prompt);
                builder.setPositiveButton(R.string.Shift_In_Sign,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,
                                    int which)
                            {
                                PostSign();
                            }
                        });
                builder.setNegativeButton(R.string.Shift_Out_Cancel,
                        null);
                builder.create().show();

            }
        };
    }

    private void ShowStaffSignInfo()
    {
        progDialog = ProgressDialog.show(Shift_In_SignIn.this,
                getString(R.string.Waiting), getString(R.string.GettingData), true,
                false);
        progDialog.setIndeterminate(true);
        progDialog.setCancelable(true);
        progDialog.setIcon(R.drawable.waiting);
        funcName = "ShowStaffSignInfo";
        Thread thread = new Thread(null, doBackgroundThreadProcessing,
                "getAllPlayRecords");
        thread.start();
    }

    private void PostSign()
    {
        progDialog = ProgressDialog.show(Shift_In_SignIn.this,
                getString(R.string.Waiting), getString(R.string.SavingData), true,
                false);
        progDialog.setIndeterminate(true);
        progDialog.setCancelable(true);
        progDialog.setIcon(R.drawable.waiting);
        funcName = "PostSign";
        Thread thread = new Thread(null, doBackgroundThreadProcessing,
                "getAllPlayRecords");
        thread.start();
    }

    private void PostSign2Server() throws Exception
    {
        String exStaffs = "";
        for (Entry<Long, String> item : hmUnSignSigned.entrySet())
        {
            exStaffs += item.getKey() + ",";
        }
        if (!exStaffs.equals(""))
        {
            exStaffs = exStaffs.substring(0, exStaffs.length() - 1);
        }
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("exStaffs", exStaffs);
        String methodPath = "WebService/Shift.asmx";
        String methodName = "SaveShiftStaff";
        WebServiceManager webServiceManager = new WebServiceManager(
                getApplicationContext(), methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        errMsg = JsonUtil.GetJsonString(result, "Msg");
        if (!errMsg.equals(""))
        {
            throw new Exception(errMsg);
        }
    }

    private ArrayList<Duty_Staff_Shift> GetDutyStaffShift() throws Exception
    {
        hmUnSignSigned.clear();
        ArrayList<Duty_Staff_Shift> list = new ArrayList<Duty_Staff_Shift>();
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        String methodPath = "WebService/Shift.asmx";
        String methodName = "GetAllStaffs";
        WebServiceManager webServiceManager = new WebServiceManager(
                getApplicationContext(), methodName, parmValues);
        String result = webServiceManager.OpenConnect(methodPath);
        errMsg = JsonUtil.GetJsonString(result, "Msg");
        if (!errMsg.equals(""))
        {
            throw new Exception(errMsg);
        }
        JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
        for (int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
            Duty_Staff_Shift staffShift = new Duty_Staff_Shift();
            staffShift.SID = JsonUtil.GetJsonObjLongValue(jsonObj,
                    "SID");
            staffShift.staffId = JsonUtil.GetJsonObjLongValue(jsonObj,
                    "staffId");
            staffShift.TeamId = JsonUtil.GetJsonObjLongValue(jsonObj,
                    "TeamId");
            staffShift.wid = JsonUtil.GetJsonObjLongValue(jsonObj,
                    "wid");
            staffShift.shift_id = JsonUtil.GetJsonObjLongValue(
                    jsonObj, "shift_id");
            staffShift.staffName = JsonUtil.GetJsonObjStringValue(
                    jsonObj, "staffName");
            staffShift.RoleName = JsonUtil.GetJsonObjStringValue(
                    jsonObj, "RoleName");
            staffShift.RepDate = JsonUtil.GetJsonObjDateValue(jsonObj,
                    "RepDate");
            if (staffShift.RepDate == null)
            {
                if (!hmUnSignSigned.containsKey(staffShift.staffId))
                {
                    hmUnSignSigned
                            .put(staffShift.staffId, staffShift.staffName);
                }
            }
            list.add(staffShift);

        }

        return list;
    }

    final Runnable mUpdateResults = new Runnable()
    {

        public void run()
        {
            if (progDialog != null)
            {
                progDialog.dismiss();
            }

            if (funcName.equals("ShowStaffSignInfo"))
            {
            	ListView gvList = (ListView) findViewById(R.id.gvList);
                shiftInSignInAdapter = new Shift_In_SignIn_Adapter(
                        Shift_In_SignIn.this, staff_Shift_List, hmUnSignSigned);
                shiftInSignInAdapter.handler = new Shift_In_SignIn_Handler(
                        Shift_In_SignIn.this);
                gvList.setAdapter(shiftInSignInAdapter);
            }

            if (funcName.equals("PostSign"))
            {
            	errMsg = getString(R.string.Shift_In_Sign_Success);
                showErrMsg(errMsg);
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
            Intent go2Login = new Intent(Shift_In_SignIn.this,LoginFrame.class);
            startActivity(go2Login);
        }
    };

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
                if (funcName.equals("ShowStaffSignInfo"))
                {
                    staff_Shift_List = GetDutyStaffShift();
                }

                if (funcName.equals("PostSign"))
                {
                    PostSign2Server();
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

    public void UpdateUnSign(long staffId, String staffName, boolean remove)
    {
        if (!remove)
        {
            if (hmUnSignSigned.containsKey(staffId))
            {
                hmUnSignSigned.remove(staffId);
            }
        }
        else
        {
            if (!hmUnSignSigned.containsKey(staffId))
            {
                hmUnSignSigned.put(staffId, staffName);
            }
        }
    }
}
