package easyway.Mobile.Shift;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.Application.ExitApplication;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.PullRefreshListView;
import easyway.Mobile.util.PullRefreshListView.OnRefreshListener;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class RollCall extends Fragment {
	
	private Activity act;
	private View mFragmetn;
	private PullRefreshListView list;
	private Adapter_RollCall adapter;
	private Button bt;
	ArrayList<RollCallBase> mlistData;
	private long WorkTypeId;
	private long TeamId;
	private long ShiftId;
	private boolean isFirstRun;
	private boolean isPullRefresh;
	
	private final int GetNetInfoOk = 0;
	private final int GetNetInfoIsFail = 1;
	private final int GetNetInfoIsNull = 2;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GetNetInfoOk:
				//如果是第一次运行，我就创建适配器。否则就只设置数据，刷新数据。
				mlistData = (ArrayList<RollCallBase>)msg.obj;
				//每次把最新的集合还给父类。
				New_Shift_In_Info sup = (New_Shift_In_Info)act;
				sup.setListData(mlistData);
				if(isFirstRun){
					FirstRun();
				}else{
					//除了第一次创建适配器，其余的都是刷新数据。
					//这里刷新不出数据。所以不处理。
//					adapter.setData(mlistData);
//					adapter.notifyDataSetChanged();
				}
				
				if (isPullRefresh) {
					list.onRefreshComplete();
					isPullRefresh = false;
				}
				break;
			case GetNetInfoIsFail:
				String errMsg = (String)msg.obj;
				Toast.makeText(act, errMsg, 0).show();
				break;
			case GetNetInfoIsNull:
				Toast.makeText(act, "没有信息", 0).show();
				break;
			default:
				break;
			}
		};
	};
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		isFirstRun = true;
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		act = activity;
		New_Shift_In_Info sup = (New_Shift_In_Info)act;
		long[] data = sup.getData();
		//这里强转成int有可能会因为精度的丢失而出问题。
		WorkTypeId = data[2];
		ShiftId = data[1];
		TeamId = data[0];
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mFragmetn = View.inflate(act, R.layout.fragment_rollcall, null);
		list = (PullRefreshListView) mFragmetn.findViewById(R.id.Fragment_RollCall_list);
		bt = (Button)mFragmetn.findViewById(R.id.Fragment_RollColl_bt);
		bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(act,AddStaff.class);
				intent.putExtra("TeamId", TeamId);
				intent.putExtra("ShiftId", ShiftId);
				intent.putExtra("WorkTypeId", WorkTypeId);
				startActivity(intent);
			}
		});
		return mFragmetn;
	}
	
	@Override
	public void onStart() {
		NewWork();
		super.onStart();
	}
	
	private void FirstRun(){
		
		//调用方法获取添加人员界面的两个集合、
		ArrayList<ArrayList<?>> list1 = ExitApplication.getInstance().getMStaff().getData();
		ArrayList<Long> data1 = (ArrayList<Long>) list1.get(0);
		ArrayList<String> data2 = (ArrayList<String>) list1.get(1);
		if(data1 != null && data1.size() > 0){
			int k = data1.size();
			ArrayList<RollCallBase> mData = new ArrayList<RollCallBase>();
			for(int i = 0;i < k;i++){
				RollCallBase base = new RollCallBase();
				base.staffId = data1.get(i);
				base.name = data2.get(i);
				mData.add(base);
			}
//			ArrayList<RollCallBase> mDatalist = mlistData;
//			mlistData.clear();
			
			mlistData.addAll(mData);
//			mlistData.addAll(mDatalist);
			//每次把最新的集合还给父类。
			New_Shift_In_Info sup = (New_Shift_In_Info)act;
			sup.setListData(mlistData);
		}
		
		adapter = new Adapter_RollCall(mlistData, act);
		list.setAdapter(adapter);
		list.setonRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				isPullRefresh = true;
				NewWork();
			}
		});
		isFirstRun = false;
	}
	
	private void NewWork(){
		new Thread(){
			public void run() {
				NetWorkPrepare();
			};
		}.start();
	}
	
	private void NetWorkPrepare(){
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("workTypeId", String.valueOf(WorkTypeId));
		String methodName = Constant.MN_GetStaffScheDetail;
		String methodPath = Constant.MP_SHIFT;
		WebServiceManager webService = new WebServiceManager(act, methodName, parmValues);
		String result = webService.OpenConnect(methodPath);
		String errMsg = JsonUtil.GetJsonString(result, "Msg");
		if(TextUtils.isEmpty(errMsg)){
			int code = JsonUtil.GetJsonInt(result, "Code");
			if(code == 1000){
				JSONArray data = JsonUtil.GetJsonArray(result, "Data");
				if(data != null && data.length() > 0){
					int k = data.length();
					ArrayList<RollCallBase> list1 = new ArrayList<RollCallBase>();
					for(int i = 0;i < k;i++){
						RollCallBase base = new RollCallBase();
						JSONObject jsonObj = (JSONObject)data.opt(i);
						base.staffId = JsonUtil.GetJsonObjLongValue(jsonObj, "StaffId");
						base.name = JsonUtil.GetJsonObjStringValue(jsonObj, "StaffName");
						list1.add(base);
					}
					Message ms = handler.obtainMessage();
					ms.obj = list1;
					ms.what = GetNetInfoOk;
					handler.sendMessage(ms);
				}else{
					handler.sendEmptyMessage(GetNetInfoIsNull);
				}
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
