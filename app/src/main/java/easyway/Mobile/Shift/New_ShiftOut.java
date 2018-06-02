package easyway.Mobile.Shift;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Application.ExitApplication;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

public class New_ShiftOut extends ActivityEx{
	
	private Activity act;
	private ListView list;
	private Adapter_New_ShiftOut adapter;
	private final int GetNetInfoIsOk = 0;
	private final int GetNetInfoIsFail = 1;
	private final int GetNetInfoIsNull = 2;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GetNetInfoIsOk:
				ArrayList<New_ShiftOut_Base> list1 = (ArrayList<New_ShiftOut_Base>) msg.obj;
				adapter = new Adapter_New_ShiftOut(act, list1);
				list.setAdapter(adapter);
				break;
			case GetNetInfoIsFail:
				showErrMsg(errMsg);
				break;
			case GetNetInfoIsNull:
				showToast("没有接班信息！");
				break;
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_new_shift_out);
		act = this;
		initView();
		super.onCreate(savedInstanceState);
	}
	
	private void initView(){
		TextView labTitle = (TextView) findViewById(R.id.title);
        labTitle.setText("接班");
        ExitApplication.getInstance().addActivity(act);
        list = (ListView) findViewById(R.id.act_newShift_out_List);
        ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if(info != null && info.isAvailable()){
			new Thread(){
				public void run() {
					getInfo();
				};
			}.start();
		}else{
			showToast("未连接网络，请连接网络后再试");
		}
		
	}
	
	private void getInfo(){
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("limit", "300");
		parmValues.put("start", "0");
		String methodName = Constant.MN_GetShiftOutALLInfo;
		String methodPath = Constant.MP_SHIFT;
		WebServiceManager webService = new WebServiceManager(act, methodName, parmValues);
		String result = webService.OpenConnect(methodPath);
		String errMsg = JsonUtil.GetJsonString(result, "Msg");
		if(TextUtils.isEmpty(errMsg)){
			JSONArray data = JsonUtil.GetJsonArray(result, "Data");
			if(data != null && data.length() > 0){
				int k = data.length();
				ArrayList<New_ShiftOut_Base> list1 = new ArrayList<New_ShiftOut_Base>();
				for(int i = 0;i < k;i++ ){
					 New_ShiftOut_Base base = new New_ShiftOut_Base();
					 JSONObject jsonObj = (JSONObject) data.opt(i);
					 base.shiftOutName = JsonUtil.GetJsonObjStringValue(jsonObj, "shiftOutName");
					 base.ShiftOutDt = JsonUtil.GetJsonObjDateValue(jsonObj, "ShiftOutDt");
					 base.VoicePath = JsonUtil.GetJsonObjStringValue(jsonObj, "VoicePath");
					 base.WorkTypeName = JsonUtil.GetJsonObjStringValue(jsonObj, "WorkTypeName");
					 base.TeamId = JsonUtil.GetJsonObjLongValue(jsonObj, "teamid");
					 base.ShiftId = JsonUtil.GetJsonObjLongValue(jsonObj, "shiftId");
					 base.WorkTypeId = JsonUtil.GetJsonObjIntValue(jsonObj, "WorkTypeId");
					list1.add(base);
				}
				Log.d("msg", result);
				Message ms = handler.obtainMessage();
				ms.obj = list1;
				ms.what = GetNetInfoIsOk;
				handler.sendMessage(ms);
			}else{
				//服务器没有内容。接班，必须得在交班之后才会有内容。交班码
				handler.sendEmptyMessage(GetNetInfoIsNull);
			}
		}else{
			handler.sendEmptyMessage(GetNetInfoIsFail);
		}
	}
	
	
	
}
