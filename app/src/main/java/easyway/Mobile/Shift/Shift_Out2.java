package easyway.Mobile.Shift;

import java.lang.reflect.Type;
import java.util.HashMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.Result;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;

public class Shift_Out2 extends ActivityEx implements OnClickListener{
	
	private Activity act;
	private final int GetMsgOk = 1;
	private final int GetMsgFail = 2;
	private final int ServerError = 3;
	private int WorkType = -1;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GetMsgOk:
				ShiftOut2Data data = (ShiftOut2Data) msg.obj;
				Intent go2 = new Intent(act,GetAllOpeningTaskActivity.class);
				go2.putExtra("WorkType", WorkType);
				startActivity(go2);
				break;
			case GetMsgFail:
				String erroreMsg = (String) msg.obj;
				showToast(erroreMsg);
				break;
			case ServerError:
				showToast("连接失败");
				break;
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		act = this;
		setContentView(R.layout.act_shift_out2);
		initView();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void initView(){
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("交班");
		Button btnReturn = (Button) findViewById(R.id.btnReturn);
		btnReturn.setOnClickListener(this);
		
	}
	//上水任务
	public void WaterTask(View v){
		StartTask("0");
		WorkType = 0;
	}
	//站台任务
	public void PlatformTask(View v){
		StartTask("1");
		WorkType = 1;
	}
	//候车厅任务
	public void WaitingTask(View v){
		StartTask("2");
		WorkType = 2;
	}
	//检票任务
	public void CheckTask(View v){
		StartTask("3");
		WorkType = 3;
	}
	//出站口任务
	public void ExitTask(View v){
		StartTask("4");
		WorkType = 4;
	}
	
	private void StartTask(final String workType){
		new Thread(){
			@Override
			public void run() {
				super.run();
				HashMap<String, String> parmValues = new HashMap<String, String>();
				parmValues.put("sessionId", Property.SessionId);
				parmValues.put("workType", workType);
				String methodPath = Constant.MP_SHIFT;
				String methodName = Constant.MN_Shift2_Out_Start;
				WebServiceManager webServiceManager = new WebServiceManager(
				act, methodName, parmValues);
				String result = webServiceManager.OpenConnect(methodPath);
				if(result == null || result.equals("")){
					handler.sendEmptyMessage(ServerError);
					return;
				}
				Gson gson = new Gson();
				Type type = new TypeToken<Result<ShiftOut2Data>>(){}.getType();
				Result<ShiftOut2Data> ReturnData = gson.fromJson(result, type);
				if(ReturnData.isMsgType()){
					handler.sendMessage(handler.obtainMessage(GetMsgOk, ReturnData.getData()));
				}else{
					handler.sendMessage(handler.obtainMessage(GetMsgFail, ReturnData.getMsg()));
				}
			}
		}.start();
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnReturn:
			finish();
			break;
		default:
			break;
		}
	}
	
}
