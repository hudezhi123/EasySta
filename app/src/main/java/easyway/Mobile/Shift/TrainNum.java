package easyway.Mobile.Shift;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.PullRefreshListView;
import easyway.Mobile.util.PullRefreshListView.OnRefreshListener;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class TrainNum extends Fragment {
	
	private Activity act;
	private View mFragment;
	private PullRefreshListView list;
	private Adapter_TrainNum adapter;
	private long TeamId;
	private long ShiftId;
	private boolean isPullRefresh;
	private boolean isFirstRun;
	
	private final int GetNetInfoOk = 0;
	private final int GetNetInfoIsFail = 1;
	private final int GetNetInfoIsNull = 2;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GetNetInfoOk:
				ArrayList<TrainNum_Base> list1 = (ArrayList<TrainNum_Base>) msg.obj;
				//首次运行就创建适配器
				if(isFirstRun){
					FirstRun(list1);
				}else{
					//除了第一次创建适配器，其余的都是刷新数据。
					adapter.setData(list1,TeamId);
					adapter.notifyDataSetChanged();
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
		TeamId = data[0];
		ShiftId = data[1];
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mFragment = inflater.inflate(R.layout.fragment_train_num, null);
		
		list = (PullRefreshListView)mFragment.findViewById(R.id.fra_TrainNum_list);
		
		return mFragment;
	}
	
	@Override
	public void onStart() {
		new Thread(){
			public void run() {
				NetWork();
			};
		}.start();
		super.onStart();
	}
	
	private void FirstRun(ArrayList<TrainNum_Base> list1){
		adapter = new Adapter_TrainNum(act, list1,TeamId);
		list.setAdapter(adapter);
		list.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				isPullRefresh = true;
				new Thread(){
					public void run() {
						NetWork();
					};
				}.start();
			}
		});
		isFirstRun = false;
	}
	
	private void NetWork(){
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("teamId", String.valueOf(TeamId));
		parmValues.put("shiftId", String.valueOf(ShiftId));
		String methodName = Constant.MN_GetShiftOutInfo;
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
					ArrayList<TrainNum_Base> list1 = new ArrayList<TrainNum_Base>();
					for(int i = 0; i < k; i++){
						TrainNum_Base base = new TrainNum_Base();
						JSONObject jsonObj = (JSONObject) data.opt(i);
						base.TrainNum = JsonUtil.GetJsonObjStringValue(jsonObj, "TrainNum");
						base.VoicePath = JsonUtil.GetJsonObjStringValue(jsonObj, "VoicePath");
						base.Text = JsonUtil.GetJsonObjStringValue(jsonObj, "Descriptions");
						list1.add(base);
					}
					Message mes = handler.obtainMessage();
					mes.obj = list1;
					mes.what = GetNetInfoOk;
					handler.sendMessage(mes);
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
