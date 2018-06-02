package easyway.Mobile.Shift;

import java.util.HashMap;

import com.google.gson.Gson;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.AnnotateUtil;
import easyway.Mobile.util.BindView;
import easyway.Mobile.util.LogUtil;
import easyway.Mobile.util.PullRefreshListView;

public class GetAllOpeningTaskActivity extends ActivityEx {

	private Activity act;
	@BindView(id = R.id.mlistView)
	private PullRefreshListView mlistView;
	@BindView(id = R.id.TextRemarks)
	private Button TextRemarks;
	@BindView(id = R.id.VoiceRemarks)
	private Button VoiceRemarks;
	@BindView(id = R.id.DetailRemarks)
	private Button DetailRemarks;
	@BindView(id = R.id.Complete)
	private Button Complete;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		act = this;
		setContentView(R.layout.act_shift_get_all_open_task);
		AnnotateUtil.initBindView(act);
		intiView();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void intiView() {
		int WorkType = getIntent().getIntExtra("WorkType", -1);
		getAllOpenTask(WorkType);
	}

	private void getAllOpenTask(final int WorkType) {
		new Thread() {
			@Override
			public void run() {
				super.run();
				HashMap<String, String> parmValues = new HashMap<String, String>();
				parmValues.put("sessionId", Property.SessionId);
				parmValues.put("workType", WorkType + "");
				String methodPath = Constant.MP_SHIFT;
				String methodName = Constant.MN_Shift2_Out_GetOpeningTask;
				WebServiceManager webServiceManager = new WebServiceManager(
						act, methodName, parmValues);
				String result = webServiceManager.OpenConnect(methodPath);
				if(result != null && result.length() > 0){
					Gson gson = new Gson();
					LogUtil.e("所有未完成任务" + result);
				}else{
					LogUtil.e("联网失败");
				}
			};
		}.start();
	}

}
