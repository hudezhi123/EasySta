package easyway.Mobile.site_monitoring;

import java.util.ArrayList;
import java.util.HashMap;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.PullRefreshListView;
import easyway.Mobile.util.PullRefreshListView.OnRefreshListener;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/*
 * 任务监控-类型2
 */
public class SMChildCatgTwoActivity extends ActivityEx {
	private ArrayList<SMWorkspace> mList = new ArrayList<SMWorkspace>();
	private SMCatgTwoAdapter mAdapter;
	private PullRefreshListView mListView;
	private boolean isPullRefresh = false;
	private long mId;
	
	
	private final int MSG_GETDATA_FAIL = 0;
	private final int MSG_GETDATA_SUCCEED = 1;
	
	@SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			closeProgressDialog();
			if (isPullRefresh) {
				isPullRefresh = false;
				mListView.onRefreshComplete();
			}
			switch (msg.what) {
			case MSG_GETDATA_FAIL:
				showToast(errMsg);
				break;
			case MSG_GETDATA_SUCCEED:
				ArrayList<SMWorkspace> list = (ArrayList<SMWorkspace>) msg.obj;
				if (null != list) {
					mList = list;
					mAdapter.setData(mList);
					mAdapter.notifyDataSetChanged();
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.site_task_layout);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mId = bundle.getLong(SMTabActivity.EXTRA_ID);
		}
		
		initView();
		getData();
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	private void initView() {
		mListView = (PullRefreshListView) findViewById(R.id.list_view);
		mAdapter = new SMCatgTwoAdapter(this, mList);
		mListView.setAdapter(mAdapter);

		mListView.setonRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				isPullRefresh = true;
				getData();
			}
		});
	}

	// 获取数据
	private void getData() {
		showProgressDialog(R.string.GettingData);
		new Thread() {
			public void run() {
				getServerData();
			}
		}.start();
	}

	// 获取平台数据
	private void getServerData() {
		ArrayList<SMWorkspace> list = new ArrayList<SMWorkspace>();

		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("mId", String.valueOf(mId));
		String methodPath = Constant.MP_TASK;
		String methodName = Constant.MN_GET_MONITORTASK;

		WebServiceManager webServiceManager = new WebServiceManager(
				getApplicationContext(), methodName, parmValues);
		String result = webServiceManager.OpenConnect(methodPath);

		if (result == null || result.equals("")) {
			errMsg = getString(R.string.exp_getdata);
			myhandle.sendEmptyMessage(MSG_GETDATA_FAIL);
			return;
		}

		int Code = JsonUtil.GetJsonInt(result, "Code");
		switch (Code) {
		

		case Constant.NORMAL:
			list = SMWorkspace.ParseFromString(result);

			Message message = new Message();
			message.what = MSG_GETDATA_SUCCEED;
			message.obj = list;
			myhandle.sendMessage(message);
			break;
		case Constant.EXCEPTION:
		default:
			errMsg = JsonUtil.GetJsonString(result, "Msg");
			myhandle.sendEmptyMessage(MSG_GETDATA_FAIL);
			break;
		}
	}
}
