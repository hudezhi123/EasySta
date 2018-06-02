package easyway.Mobile.VacationAudit;

import java.util.ArrayList;
import java.util.HashMap;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.Vacation;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.VacationApply.VacationAdapter;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.PullRefreshListView;
import easyway.Mobile.util.PullRefreshListView.OnRefreshListener;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/*
 *  请假审批列表
 */
public class VacationAuditListActivity extends ActivityEx {
	private final int REQUEST_CODE_AUDIT = 101;
	public static final String KEY_CODE = "key";
	
	private PullRefreshListView mListView;
	private VacationAdapter mAdapter;
	private ArrayList<Vacation> mList;
	
	private boolean isPullRefresh = false;

	private final int MSG_GETVACATION_FAIL = 1;	// 获取审批列表失败
	private final int MSG_GETVACATION_SUCC = 2;	// 获取审批列表成功
	private final int MSG_GETVACATION_NO=3;// 没有数据
	@SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			closeProgressDialog();
			switch (msg.what) {
			case MSG_GETVACATION_FAIL:
				showToast("获取数据失败");
				break;
				
			case MSG_GETVACATION_NO:
				showToast("暂无数据");
				break;
			case MSG_GETVACATION_SUCC:
				if (isPullRefresh) {
					isPullRefresh = false;
					mListView.onRefreshComplete();
				}
				mList = (ArrayList<Vacation>) msg.obj;
				mAdapter.setData(mList);
				mAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vacation_auditlist);
		initView();
		
		getData();
	}

	private void initView() {
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.Vacation_Auditlist_Title);

		mListView = (PullRefreshListView) findViewById(R.id.list);
		mAdapter = new VacationAdapter(this, null);
		mListView.setAdapter(mAdapter);

		mListView.setonRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				isPullRefresh = true;
				getData();
			}
		});
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int index = arg2 - 1;
				if (index >=0 && index < mList.size()) {
					Intent intent = new Intent(VacationAuditListActivity.this, VacationAuditActivity.class);					 
					Bundle extras = new Bundle();
					extras.putSerializable(Vacation.KEY_VACATION, mList.get(index));
					intent.putExtras(extras);
					startActivityForResult(intent, REQUEST_CODE_AUDIT);
				}
			}
		});
	}
	
	// 获取数据
	private void getData() {
		showProgressDialog(R.string.GettingData);

		new Thread() {
			public void run() {
				getVacation();
			}
		}.start();
	}

	// 获取请假列表
	private void getVacation() {
		HashMap<String, String> paramValues = new HashMap<String, String>();
		paramValues.put("sessionId", Property.SessionId);
		paramValues.put("status", "1");	// 请假确认
		
		if (Property.OwnStation != null)
			paramValues.put("stationCode", Property.OwnStation.Code);

		String methodPath = Constant.MP_ATTANDENCE;
		String methodName = Constant.MN_GET_VACATION;
		WebServiceManager webServiceManager = new WebServiceManager(
				getApplicationContext(), methodName, paramValues);
		String result = webServiceManager.OpenConnect(methodPath);

		if (result == null || result.equals("")) {
			errMsg = getString(R.string.Vacation_Exp_GetVacation);
			myhandle.sendEmptyMessage(MSG_GETVACATION_FAIL);
			return;
		}

		int Code = JsonUtil.GetJsonInt(result, "Code");

		switch (Code) {
		case Constant.NORMAL:
			ArrayList<Vacation> list = Vacation.ParseFromString(result);
			Message msg = new Message();
			if (null==list||list.size()<=0) {				
				msg.obj = list;
				msg.what = MSG_GETVACATION_NO;
				myhandle.sendMessage(msg);
			}else {
				msg.obj = list;
				msg.what = MSG_GETVACATION_SUCC;
				myhandle.sendMessage(msg);
			}
			
			break;
		case Constant.EXCEPTION:
		default:
			errMsg = JsonUtil.GetJsonString(result, "Msg");
			if (errMsg == null || errMsg.length() == 0)
				errMsg = getString(R.string.Vacation_Exp_GetVacation);
			myhandle.sendEmptyMessage(MSG_GETVACATION_FAIL);
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED) {
		} else if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_AUDIT:
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					if (bundle.getBoolean(KEY_CODE, false))	// 刷新界面
						getData();
				}
				break;
			default:
				break;
			}
		}
	}
}
