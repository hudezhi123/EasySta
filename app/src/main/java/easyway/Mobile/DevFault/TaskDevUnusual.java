package easyway.Mobile.DevFault;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.PullRefreshListView;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class TaskDevUnusual extends ActivityEx{
	
	private Activity act;
	private ArrayList<TaskDevUnusualBase> arrList;
	private PullRefreshListView listView;
	private TDUAdapter adapter;
	private String Workspace1;
	
	private final int getDataIsNull = 0;
	private final int getDataIsFill = 1;
	private final int getDataIsSucess = 2;
	
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case getDataIsNull:
				String ErrMsg = "连接失败";
				showToast(ErrMsg);
				break;
			case getDataIsFill:
				String errMsg = (String)msg.obj;
				showToast(errMsg);
				break;
			case getDataIsSucess:
				adapter = new TDUAdapter(arrList,act);
				listView.setAdapter(adapter);
				break;
			default:
				break;
			}
			
		};
	};
	
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_task_dev_unusual);
		act = this;
		initView();
		super.onCreate(savedInstanceState);
	}

	private void initView(){
		listView = (PullRefreshListView)findViewById(R.id.TDUList);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				TextView Operate = (TextView)view.findViewById(R.id.Operate);
				Operate.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Workspace1 = arrList.get(position).Workspace;
						new Thread(){
							public void run() {
								FixTaskDevFault();
							};
						}.start();
					}
				});
				
			}
			
		});
	}
	
	@Override
	protected void onResume() {
		NetWork();
		super.onResume();
	}
	
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	private void NetWork(){
		new Thread(){
			@Override
			public void run() {
				GetTaskDevFault();
				super.run();
			}
		}.start();
	}
	
	private void GetTaskDevFault(){
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("stationCode", Property.StationCode);
		String methodPath = Constant.MP_TASK;
		String methodName = Constant.MN_GetTaskDevFault;
		WebServiceManager webServiceManager = new WebServiceManager(act, methodName, parmValues);
		String result = webServiceManager.OpenConnect(methodPath);
		
		if (result == null || result.equals("")) {
			handler.sendEmptyMessage(getDataIsNull);
			
		}
		int Code = JsonUtil.GetJsonInt(result, "Code");
		switch (Code) {
		case 1000:
			JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
			int num = jsonArray.length();
			arrList = new ArrayList<TaskDevUnusualBase>();
			for(int i = 0; i < num; i ++){
				JSONObject obi = (JSONObject)jsonArray.opt(i);
				TaskDevUnusualBase base = new TaskDevUnusualBase();
				base.StaffName = JsonUtil.GetJsonObjStringValue(obi, "StaffName");
				base.StationCode = JsonUtil.GetJsonObjStringValue(obi, "StationCode");
				base.Workspace = JsonUtil.GetJsonObjStringValue(obi, "Workspace");
				arrList.add(base);
			}
			handler.sendEmptyMessage(getDataIsSucess);
			break;
		case 911:
			String errMsg = JsonUtil.GetJsonString(result, "Msg");
			Message msg = handler.obtainMessage();
			msg.what = getDataIsFill;
			msg.obj = errMsg;
			handler.sendMessage(msg);
		default:
			break;
		}
	}
	
	private void FixTaskDevFault(){
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("stationCode", Property.StationCode);
		parmValues.put("Workspace", Workspace1);
		String methodPath = Constant.MP_TASK;
		String methodName = Constant.MN_FixTaskDevFault;
		WebServiceManager webServiceManager = new WebServiceManager(act, methodName, parmValues);
		String result = webServiceManager.OpenConnect(methodPath);
		if(TextUtils.isEmpty(result)){
			handler.sendEmptyMessage(getDataIsNull);
		}else{
			int Code = JsonUtil.GetJsonInt(result, "Code");
			if(Code == 1000){
				String s = JsonUtil.GetJsonString(result, "Msg");
				Message msg = handler.obtainMessage();
				msg.what = getDataIsFill;
				msg.obj = s;
				handler.sendMessage(msg);
			}else{
				String errMsg = JsonUtil.GetJsonString(result, "Msg");
				Message msg = handler.obtainMessage();
				msg.what = getDataIsFill;
				msg.obj = errMsg;
				handler.sendMessage(msg);
			}
		}
	}
	
	
}
