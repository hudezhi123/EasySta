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
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.TB_Shift;
import easyway.Mobile.Login.LoginFrame;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;

public class Shift_In_Pool extends ActivityEx {
	private String tag = "Shift_Out";
	private Handler handel = new Handler();
	private ProgressDialog progDialog;
	private ArrayList<TB_Shift> shiftOutList = new ArrayList<TB_Shift>();
	private String funcName = "";
	private Handler shift_In_Pool_handler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			super.handleMessage(message);
			switch (message.what) {
			case 1:
				long shift_id = Long.valueOf(message.obj.toString());
				SetShiftId(shift_id);
				break;
			case 0:
				String errMsg = (String) message.obj.toString();
				SetErrMsg(errMsg);
				break;
			case 2:
				long shiftid = Long.valueOf(message.obj.toString());
				for (TB_Shift shift : shiftOutList) {
					if (shift.Shift_Id == shiftid) {
						shiftOutList.remove(shift);
						break;
					}
				}
				shiftInPoolAdapter.notifyDataSetChanged();
			default:
				break;
			}
		}
	};
	private Shift_In_Pool_Adapter shiftInPoolAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shift_in_pool);
		TextView labTitle = (TextView) findViewById(R.id.title);
		labTitle.setText(R.string.Shift_In_Pool);
		ShowAllShiftOut();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 200) {
			Bundle b = data.getExtras();
			long shiftId = b.getLong("ShiftId");
			for (TB_Shift item : shiftOutList) {
				if (item.Shift_Id == shiftId) {
					shiftOutList.remove(item);
					shiftInPoolAdapter.notifyDataSetChanged();
					break;
				}
			}
		}
	}

	public void SetShiftId(long shift_Id) {
		Intent data = new Intent(getApplicationContext(), Shift_In.class);
		Bundle bundle = new Bundle();
		bundle.putLong("Shift_Id", shift_Id);
		data.putExtras(bundle);
		setResult(2, data);
		finish();
	}

	public void SetErrMsg(String errMsg) {
		this.errMsg = errMsg;
		showErrMsg(errMsg);
	}

	private ArrayList<TB_Shift> GetAllShiftOut() throws Exception {
		ArrayList<TB_Shift> list = new ArrayList<TB_Shift>();
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		String methodPath = "WebService/Shift.asmx";
		String methodName = "GetAllShiftOut";
		WebServiceManager webServiceManager = new WebServiceManager(
				getApplicationContext(), methodName, parmValues);
		String result = webServiceManager.OpenConnect(methodPath);
		errMsg = JsonUtil.GetJsonString(result, "Msg");
		if (!errMsg.equals("")) {
			throw new Exception(errMsg);
		}
		JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
			TB_Shift shift = new TB_Shift();
			shift.Shift_Id = JsonUtil.GetJsonObjLongValue(jsonObj,
					"Shift_Id");
			shift.Shift_Out_TeamId = JsonUtil.GetJsonObjLongValue(
					jsonObj, "Shift_Out_TeamId");
			shift.Shift_Out_Shaff_Id = JsonUtil.GetJsonObjLongValue(
					jsonObj, "Shift_Out_Shaff_Id");
			shift.Shift_In_TeamId = JsonUtil.GetJsonObjLongValue(
					jsonObj, "Shift_In_TeamId");
			shift.Shift_In_Shaff_Id = JsonUtil.GetJsonObjLongValue(
					jsonObj, "Shift_In_Shaff_Id");
			shift.Shift_Out_Dt = JsonUtil.GetJsonObjDateValue(jsonObj,
					"Shift_Out_Dt");
			shift.Shift_In_Dt = JsonUtil.GetJsonObjDateValue(jsonObj,
					"Shift_In_Dt");
			;
			shift.Shift_Out_Staff_Name = JsonUtil
					.GetJsonObjStringValue(jsonObj, "Shift_Out_Staff_Name");
			shift.Shift_In_Staff_Name = JsonUtil
					.GetJsonObjStringValue(jsonObj, "Shift_In_Staff_Name");
			shift.Shift_Out_Team = JsonUtil.GetJsonObjStringValue(
					jsonObj, "Shift_Out_Team");
			list.add(shift);
		}
		return list;
	}

	private void ShowAllShiftOut() {
		progDialog = ProgressDialog.show(Shift_In_Pool.this,
				getString(R.string.Waiting), getString(R.string.GettingData), true,
				false);
		progDialog.setIndeterminate(true);
		progDialog.setCancelable(true);
		progDialog.setIcon(R.drawable.waiting);
		funcName = "ShowAllShiftOut";

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
				if (funcName.equals("ShowAllShiftOut")) {
					shiftOutList = GetAllShiftOut();
				}

				if (funcName.equals("ShiftIn")) {

				}

				handel.post(mUpdateResults);
			} catch (Exception e) {
				Log.d(tag, e.getMessage());
				handel.post(mUpdateError);
			}
			Looper.loop();
		}

	};

	final Runnable mUpdateResults = new Runnable() {

		public void run() {
			if (progDialog != null) {
				progDialog.dismiss();
			}

			if (funcName.equals("ShowAllShiftOut")) {
				ListView gvList = (ListView) findViewById(R.id.gvList);
				shiftInPoolAdapter = new Shift_In_Pool_Adapter(
						Shift_In_Pool.this, shiftOutList);
				shiftInPoolAdapter.handler = shift_In_Pool_handler;
				gvList.setAdapter(shiftInPoolAdapter);
			}

			if (funcName.equals("ShiftOut")) {
				errMsg = getString(R.string.Shift_Out_Success);
				showErrMsg(errMsg);
			}

		}

	};

	final Runnable mUpdateError = new Runnable() {
		public void run() {
			if (progDialog != null) {
				progDialog.dismiss();
			}

//			showErrMsg(errMsg);
			Intent go2Login = new Intent(Shift_In_Pool.this,LoginFrame.class);
			startActivity(go2Login);
		}
	};
}
