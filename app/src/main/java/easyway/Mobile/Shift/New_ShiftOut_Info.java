package easyway.Mobile.Shift;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;

public class New_ShiftOut_Info extends ActivityEx implements OnClickListener {
	
	private Activity act;
	private TextView trainNum;
	private TextView rollCall;
	private Button bt;
	private long TeamId;
	private long ShiftId;
	private int WorkTypeId;
	
	private ArrayList<String> StaffNameList;
	private ArrayList<Long> StaffIdList;
	
	private final int GetNetInfoOk = 0;
	private final int GetNetInfoIsFail = 1;
	private final int GetNetInfoIsNull = 2;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GetNetInfoOk:
				showToast("保存接班信息成功");
				break;
			case GetNetInfoIsFail:
				String errMsg = (String)msg.obj;
				showErrMsg(errMsg);
				break;
			case GetNetInfoIsNull:
				showToast("没有信息");
				break;
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_new_shift_out_info);
		act = this;
		
		Intent intent = getIntent();
		TeamId = intent.getLongExtra("TeamId", 0);
		ShiftId = intent.getLongExtra("ShiftId", 0);
		WorkTypeId = intent.getIntExtra("WorkTypeId", 0);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onStart() {
		//创建两个fragment
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		initView();
		super.onResume();
	}
	
	private void initView(){
		
		TextView labTitle = (TextView) findViewById(R.id.title);
        labTitle.setText("接班信息");
		trainNum = (TextView)findViewById(R.id.act_New_ShiftOutInfo_TrainNum);
		rollCall = (TextView)findViewById(R.id.act_New_ShiftOutInfo_RollCall);
		bt = (Button)findViewById(R.id.act_New_ShiftOutInfo_button);
		
		trainNum.setOnClickListener(this);
		rollCall.setOnClickListener(this);
		bt.setOnClickListener(this);
		FragmentManager manager = getFragmentManager();
		manager.beginTransaction().replace(R.id.fl, new RollCall()).commit();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.act_New_ShiftOutInfo_TrainNum:
			FragmentManager manager = getFragmentManager();
			manager.beginTransaction().replace(R.id.fl, new TrainNum()).commit();
		
			break;
		case R.id.act_New_ShiftOutInfo_RollCall:
			FragmentManager manager1 = getFragmentManager();
			Fragment RollCall = new RollCall();
			manager1.beginTransaction().replace(R.id.fl, RollCall).commit();
			
			break;
		case R.id.act_New_ShiftOutInfo_button:
			NetWork();
			break;
		default:
			break;
		}
	}
	
	public long[] getData(){
		long[] data = new long[3];
		data[0] = TeamId;
		data[1] = ShiftId;
		data[2] = WorkTypeId;
		return data;
	}
	
	public void setListData(ArrayList<RollCallBase> data){
		StaffIdList = new ArrayList<Long>();
		StaffNameList = new ArrayList<String>();
		int k = data.size();
		for(int i = 0; i < k; i++){
			RollCallBase base = data.get(i);
			StaffIdList.add(base.staffId);
			StaffNameList.add(base.name);
		}
	}
	
	private void NetWork(){
		new Thread(){
			public void run() {
				NetWorkPrepare();
			};
		}.start();
	}
	
	private void NetWorkPrepare(){
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("StaffIdList", StaffIdList.toString());
		parmValues.put("StaffNameList", StaffNameList.toString());
		parmValues.put("teamId",String.valueOf(TeamId));
		parmValues.put("shiftId", String.valueOf(ShiftId));
		parmValues.put("workTypeId", String.valueOf(WorkTypeId));
		String methodName = Constant.MN_SaveShiftInInfo;
		String methodPath = Constant.MP_SHIFT;
		WebServiceManager webService = new WebServiceManager(act, methodName, parmValues);
		String result = webService.OpenConnect(methodPath);
		String errMsg = JsonUtil.GetJsonString(result, "Msg");
		if(TextUtils.isEmpty(errMsg)){
			int code = JsonUtil.GetJsonInt(result, "Code");
			if(code == 1000){
				handler.sendEmptyMessage(GetNetInfoOk);
			}else{
				Message ms = handler.obtainMessage();
				ms.obj = "服务器返回失败";
				ms.what = GetNetInfoIsFail;
				handler.sendMessage(ms);
			}
		}else{
			Message ms = handler.obtainMessage();
			ms.obj = errMsg;
			ms.what = GetNetInfoIsFail;
			handler.sendMessage(ms);
		}
	}
}
