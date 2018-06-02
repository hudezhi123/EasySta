package easyway.Mobile.Shift;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Application.ExitApplication;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.ResultForListData;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.ShiftData.Shift2InGetAllShiftOutData;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class New_Shift_In extends ActivityEx{

	private ActivityEx act;
	private ListView list;
	private LinearLayout titleView;
	private Adapter_New_Shift_In adapter;
	private final int GetNetInfoIsOk = 0;
	private final int GetNetInfoIsFail = 1;
	private final int NetError = 2;

	private final int SHIFT2INSAVE_SUCCESS = 10;
	private final int SHIFT2INSAVE_FAIL = 11;

	private final int SHIFT2MYSHIFT_SUCCESS = 12;
	private final int SHIFT2MYSHIFT_FAIL = 13;

	private int workType;



	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GetNetInfoIsOk:
				closeProgressDialog();
				ArrayList<Shift2InGetAllShiftOutData> list1 = (ArrayList<Shift2InGetAllShiftOutData>) msg.obj;
				adapter = new Adapter_New_Shift_In(act, list1,handler,workType);
				list.setAdapter(adapter);
				break;
			case GetNetInfoIsFail:
				closeProgressDialog();
				String infoFailStr = (String)msg.obj;
				showToast(infoFailStr);
				break;
			case SHIFT2INSAVE_SUCCESS:
				closeProgressDialog();
				String saveStr = (String)msg.obj;
				showToast(saveStr);
				getInfo();
				break;
			case SHIFT2INSAVE_FAIL:
				closeProgressDialog();
				String saveFailStr = (String)msg.obj;
				showToast(saveFailStr);
				break;
			case SHIFT2MYSHIFT_SUCCESS:
				closeProgressDialog();
				ArrayList<Shift2InGetAllShiftOutData> list2 = (ArrayList<Shift2InGetAllShiftOutData>) msg.obj;
				adapter = new Adapter_New_Shift_In(act, list2,handler,workType);
				list.setAdapter(adapter);
				break;
			case SHIFT2MYSHIFT_FAIL:
				closeProgressDialog();
				String myFailStr = (String)msg.obj;
				showToast(myFailStr);
				break;
			case NetError:
				closeProgressDialog();
				showToast(R.string.ConnectFail);
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
		titleView = (LinearLayout)findViewById(R.id.titleView);
		workType = getIntent().getIntExtra("shiftInWorkType", -1);
		TextView labTitle = (TextView) findViewById(R.id.title);
		switch (workType) {
		case 0:
			labTitle.setText("上水任务");
			break;
		case 1:
			labTitle.setText("站台任务");
			break;
		case 2:
			labTitle.setText("候车厅任务");
			break;
		case 3:
			labTitle.setText("检票口任务");
			break;
		case 4:
			labTitle.setText("出站口任务");
			break;
		default:
			titleView.setVisibility(View.GONE);
			break;
		}

		ExitApplication.getInstance().addActivity(act);

		list = (ListView) findViewById(R.id.act_newShift_out_List);
		ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if(info != null && info.isAvailable()){
			//			new Thread(){
			//				public void run() {
			if(workType == 5)
				getInfo1(0);
			else if(workType == 6)
				getInfo1(1);
			else
				getInfo();

			//				};
			//			}.start();
		}else{
			showToast("未连接网络，请连接网络后再试");
		}

	}

	private void getInfo(){
		showProgressDialog("正在获取未接班所有交班信息");
		new Thread(){
			@Override
			public void run() {
				super.run();
				HashMap<String, String> parmValues = new HashMap<String, String>();
				parmValues.put("sessionId", Property.SessionId);
				parmValues.put("workType", String.valueOf(workType));
				String methodPath = Constant.MP_SHIFT;
				String methodName = Constant.MN_SHIFT2INGETALLSHIFTOUT;
				WebServiceManager webServiceManager = new WebServiceManager(act,
						methodName, parmValues);
				String result = webServiceManager.OpenConnect(methodPath);
				Log.e("zwt", "返回结果" + result);
				Gson gson = new Gson();
				Type type = new TypeToken<ResultForListData<Shift2InGetAllShiftOutData>>(){}.getType();
				ResultForListData<Shift2InGetAllShiftOutData> serverData = gson.fromJson(result, type);
				if(result != null && result.length() > 0){
					if(serverData.isMsgType()){
						handler.sendMessage(handler.obtainMessage(GetNetInfoIsOk, serverData.getData()));
					}else{
						handler.sendMessage(handler.obtainMessage(GetNetInfoIsFail, serverData.getMsg()));
					}
				}else{
					handler.sendEmptyMessage(NetError);
				}

			};
		}.start();
	}

	private void getInfo1(final int shiftType){
		if(shiftType == 0)
			showProgressDialog("正在获取我的交班信息");
		else
			showProgressDialog("正在获取我的接班信息");
		new Thread(){
			@Override
			public void run() {
				super.run();
				HashMap<String, String> parmValues = new HashMap<String, String>();
				parmValues.put("sessionId", Property.SessionId);
				parmValues.put("shiftType", String.valueOf(shiftType));
				String methodPath = Constant.MP_SHIFT;
				String methodName = Constant.MN_SHIFT2MYSHIFT;		
				WebServiceManager webService = new WebServiceManager(act, methodName, parmValues);
				String result = webService.OpenConnect(methodPath);
				Log.e("zwt", "返回结果" + result);
				Gson gson = new Gson();
				Type type = new TypeToken<ResultForListData<Shift2InGetAllShiftOutData>>(){}.getType();
				ResultForListData<Shift2InGetAllShiftOutData> serverData = gson.fromJson(result, type);
				if(result != null && result.length() > 0){
					if(serverData.isMsgType()){
						handler.sendMessage(handler.obtainMessage(SHIFT2MYSHIFT_SUCCESS, serverData.getData()));
					}else{
						handler.sendMessage(handler.obtainMessage(SHIFT2MYSHIFT_FAIL, serverData.getMsg()));
					}
				}else{
					handler.sendEmptyMessage(NetError);
				}
			};
		}.start();
	}



}
