package easyway.Mobile.Shift;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.TB_TASK_PlanReal;
import easyway.Mobile.Login.LoginFrame;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;

public class Shift_In_Show_Team_Train_Task extends ActivityEx {

	private String tag = "Shift_In_Show_Team_Train_Task";

	private String trainNo = "";
	private long shiftId = 0;
	private Handler handel = new Handler();
	private ProgressDialog progDialog;
	private ArrayList<TB_TASK_PlanReal> taskList = new ArrayList<TB_TASK_PlanReal>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shift_out_team_task_of_train);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		trainNo = bundle.getString("TrainNo");
		shiftId = bundle.getLong("ShiftId");

		TextView labTitle = (TextView) findViewById(R.id.title);
		labTitle.setText(trainNo);
		ShowAllTask();

	}

	private void ShowAllTask() {
		progDialog = ProgressDialog.show(Shift_In_Show_Team_Train_Task.this,
				getString(R.string.Waiting), getString(R.string.GettingData), true,
				false);
		progDialog.setIndeterminate(true);
		progDialog.setCancelable(true);
		progDialog.setIcon(R.drawable.waiting);

		Thread thread = new Thread(null, doBackgroundThreadProcessing,
				"getAllPlayRecords");
		thread.start();
	}

	private Runnable doBackgroundThreadProcessing = new Runnable() {

		public void run() {
			String sessionId = Property.SessionId;
			if (sessionId.equals("")) {
				Log.d(tag, "Session Id is null");
				errMsg = getString(R.string.lostLoginInfo);
				handel.post(mUpdateError);
				return;
			}

			Looper.prepare();

			try {
				taskList = DownloadAllTask();

				handel.post(mUpdateResults);
			} catch (Exception e) {
				Log.d(tag, e.getMessage());
				handel.post(mUpdateError);
			}
			Looper.loop();
		}

	};

	private ArrayList<TB_TASK_PlanReal> DownloadAllTask() throws Exception {
		ArrayList<TB_TASK_PlanReal> list = new ArrayList<TB_TASK_PlanReal>();
		try {
			HashMap<String, String> parmValues = new HashMap<String, String>();
			parmValues.put("sessionId", Property.SessionId);
			parmValues.put("shiftId", String.valueOf(shiftId));
			parmValues.put("trainNum", trainNo);

			String methodPath = "WebService/Task.asmx";
			String methodName = "GetAllTask4ShiftIn";
			WebServiceManager webServiceManager = new WebServiceManager(
					getApplicationContext(), methodName, parmValues);
			String result = webServiceManager.OpenConnect(methodPath);
			errMsg = JsonUtil.GetJsonString(result, "Msg");
			if (!errMsg.equals("")) {
				throw new Exception(errMsg);
			}
			JSONArray jsonArray = JsonUtil
					.GetJsonArray(result, "Data");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
				TB_TASK_PlanReal task = new TB_TASK_PlanReal();
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
				task.TeamId = JsonUtil.GetJsonObjLongValue(jsonObj,
						"TeamId");
				list.add(task);

			}
		} catch (Exception ex) {
			throw ex;
		}
		return list;
	}

	final Runnable mUpdateError = new Runnable() {
		public void run() {
			if (progDialog != null) {
				progDialog.dismiss();
			}

//			showErrMsg(errMsg);
			Intent go2Login = new Intent(Shift_In_Show_Team_Train_Task.this,LoginFrame.class);
			startActivity(go2Login);
		}
	};

	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			if (progDialog != null) {
				progDialog.dismiss();
			}
			ListView gvList = (ListView) findViewById(R.id.gvList);
			gvList.setAdapter(new Shift_In_Show_Team_Train_Task_Adapter(
					Shift_In_Show_Team_Train_Task.this, taskList));
		}
	};
}
