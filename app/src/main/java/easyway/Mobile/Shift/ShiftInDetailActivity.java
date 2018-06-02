package easyway.Mobile.Shift;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Application.ExitApplication;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.Result;
import easyway.Mobile.Data.ResultForListData;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.ShiftData.Shift2AllDetailsData;
import easyway.Mobile.ShiftData.Shift2InGetAllShiftOutData;
import easyway.Mobile.util.AnnotateUtil;
import easyway.Mobile.util.BindView;
import easyway.Mobile.util.CommonFunc;
import easyway.Mobile.util.Player;

public class ShiftInDetailActivity extends ActivityEx implements OnClickListener{
	private Activity act;

	@BindView(id = R.id.title)
	private TextView title;
	@BindView(id = R.id.btnReturn)
	private Button btnReturn;

	@BindView(id = R.id.shiftInDetailList)
	private ListView detailList;
	@BindView(id = R.id.setActorBtn)
	private Button setActorBtn;
	@BindView(id = R.id.shiftInBtn)
	private Button shiftInBtn;
	@BindView(id = R.id.btnset)
	private Button btnset;
	
	@BindView(id = R.id.voiceBtn)
	private Button voiceBtn;
	
	private ShiftInDetailAdapter adapter;
	
	private final int SHIFT2_ALLDETAILS_SUCCESS = 0;
	private final int SHIFT2_ALLDETAILS_FAIL = 1;
	private final int NET_ERROR = 2;
	private final int SHIFT2INSAVE_SUCCESS = 10;
	private final int SHIFT2INSAVE_FAIL = 11;
	private final int SHIFT2INSETACTOR_SUCCESS = 12;
	private final int SHIFT2INSETACTOR_FAIL = 13;
	private ArrayList<Shift2AllDetailsData> allDetailsDatas = new ArrayList<Shift2AllDetailsData>();
    private Boolean ISShiftIn = false;
    private int ShiftId;
    private int shift_Detail_Id;
    private String outVoice;
    private Player player;

	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case SHIFT2_ALLDETAILS_SUCCESS:
				closeProgressDialog();
				allDetailsDatas = (ArrayList<Shift2AllDetailsData>)msg.obj;
				if(allDetailsDatas != null && allDetailsDatas.size()>0){
					adapter = new ShiftInDetailAdapter(act, allDetailsDatas, ISShiftIn,handler);
			        detailList.setAdapter(adapter);
				}else
					showToast("没有明细信息");
				break;
			case SHIFT2_ALLDETAILS_FAIL:
				closeProgressDialog();
				String detailsFail = (String)msg.obj;
				showToast(detailsFail);
				break;
			case SHIFT2INSAVE_SUCCESS:
				closeProgressDialog();
				String saveStr = (String)msg.obj;
				showToast(saveStr);
				btnset.setVisibility(View.INVISIBLE);
				ISShiftIn = true;
				shift2AllDetails();
				break;
			case SHIFT2INSAVE_FAIL:
				closeProgressDialog();
				String saveFailStr = (String)msg.obj;
				showToast(saveFailStr);
				break;
			case SHIFT2INSETACTOR_SUCCESS:
				closeProgressDialog();
				String setSuStr = (String)msg.obj;
				showToast(setSuStr);
				shift2AllDetails();
			    break;
			case SHIFT2INSETACTOR_FAIL:
				closeProgressDialog();
				String setFaStr = (String)msg.obj;
				showToast(setFaStr);
				break;
			case NET_ERROR:
				closeProgressDialog();
				showToast(R.string.ConnectFail);
				break;

			default:
				break;
			}
		}
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_shiftin_detail);
		act = this;
		AnnotateUtil.initBindView(act);
		  ExitApplication.getInstance().addActivity(act);
		initView();
	}
	private void initView(){
		ShiftId = getIntent().getIntExtra("ShiftId", -1);
		ISShiftIn = getIntent().getBooleanExtra("isShiftIn", false);
		outVoice = getIntent().getStringExtra("outVoice");
		voiceBtn.setOnClickListener(this);
		if(TextUtils.isEmpty(outVoice))
			voiceBtn.setEnabled(false);
		else
			voiceBtn.setEnabled(true);
		title.setText("接班明细");
		btnset.setVisibility(View.VISIBLE);
		btnset.setText("接班");
		btnReturn.setOnClickListener(this);
		shiftInBtn.setOnClickListener(this);
		btnset.setOnClickListener(this);
		if(ISShiftIn)
			btnset.setVisibility(View.INVISIBLE);
		else
			btnset.setVisibility(View.VISIBLE);
        shift2AllDetails();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnReturn:
			finish();
			break;
		case R.id.shiftInBtn:
			shift2InSave();
			break;
		case R.id.btnset:
			shift2InSave();
			break;
		case R.id.voiceBtn:
			String serverName = CommonFunc.GetServer(act);
			String outVoiceStr = getIntent().getStringExtra("outVoice");
			String url = serverName+outVoiceStr;//"Uploaded/Shift/2016/09/11/22/M22.wav";
//			String url = "http://58.211.125.61:8005/Uploaded/Shift/2016/09/11/36/411.wav";
			String str = voiceBtn.getText().toString().trim();
			
			if(str.equals("语音播放")){
				if(player == null){
					 player = new Player();
					player.mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
						
						@Override
						public void onCompletion(MediaPlayer arg0) {
							// TODO Auto-generated method stub
							showToast("播放完成");
							voiceBtn.setText("语音播放");
						}
					});
				} 
				player.playUrl(url);
				voiceBtn.setText("暂停");
			}else {
				player.stop();
				player = null;
				voiceBtn.setText("语音播放");
			}
			break;
		default:
			break;
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
		   case 1111:
		   long staffId = data.getLongExtra("staffId", -1);
		   shift_Detail_Id = data.getIntExtra("shift_Detail_Id", -1);
		   shift2InSetActor(staffId);
		    break;
		default:
		    break;
		    }
		}
	private void shift2AllDetails(){
		showProgressDialog("正在获取明细");
		new Thread(){
			@Override
			public void run() {
				super.run();
				HashMap<String, String> parmValues = new HashMap<String, String>();
				parmValues.put("sessionId", Property.SessionId);
				parmValues.put("shift_Id", String.valueOf(ShiftId));
				String methodPath = Constant.MP_SHIFT;
				String methodName = Constant.MN_SHIFT2ALLDETAILS;
				WebServiceManager webServiceManager = new WebServiceManager(act,
						methodName, parmValues);
				String result = webServiceManager.OpenConnect(methodPath);
				Log.e("zwt", "返回结果" + result);
				Gson gson = new Gson();
				Type type = new TypeToken<ResultForListData<Shift2AllDetailsData>>(){}.getType();
				ResultForListData<Shift2AllDetailsData> serverData = gson.fromJson(result, type);
				if(result != null && result.length() > 0){
					if(serverData.isMsgType()){
						handler.sendMessage(handler.obtainMessage(SHIFT2_ALLDETAILS_SUCCESS, serverData.getData()));
					}else{
						handler.sendMessage(handler.obtainMessage(SHIFT2_ALLDETAILS_FAIL, serverData.getMsg()));
					}
				}else{
					handler.sendEmptyMessage(NET_ERROR);
				}
				
			};
		}.start();
	}
	
	private void shift2InSave(){
		showProgressDialog("正在接班");
		new Thread(){
			@Override
			public void run() {
				super.run();
				HashMap<String, String> parmValues = new HashMap<String, String>();
				parmValues.put("sessionId", Property.SessionId);
				parmValues.put("shift_Id", String.valueOf(ShiftId));
				String methodPath = Constant.MP_SHIFT;
				String methodName = Constant.MN_SHIFT2INSAVE;
				WebServiceManager webServiceManager = new WebServiceManager(act,
						methodName, parmValues);
				String result = webServiceManager.OpenConnect(methodPath);
				Log.e("zwt", "返回结果" + result);
				Gson gson = new Gson();
				Type type = new TypeToken<Result<Shift2InGetAllShiftOutData>>(){}.getType();
				Result<Shift2InGetAllShiftOutData> serverData = gson.fromJson(result, type);
				if(result != null && result.length() > 0){
					if(serverData.isMsgType()){
						handler.sendMessage(handler.obtainMessage(SHIFT2INSAVE_SUCCESS, serverData.getMsg()));
					}else{
						handler.sendMessage(handler.obtainMessage(SHIFT2INSAVE_FAIL, serverData.getMsg()));
					}
				}else{
					handler.sendEmptyMessage(NET_ERROR);
				}
				
			};
		}.start();
	}
	
	private void shift2InSetActor(final long staffId){
		showProgressDialog("正在分配人员");
		new Thread(){
			@Override
			public void run() {
				super.run();
				HashMap<String, String> parmValues = new HashMap<String, String>();
				parmValues.put("sessionId", Property.SessionId);
				parmValues.put("shift_Detail_Id", String.valueOf(shift_Detail_Id));
				parmValues.put("staffId", String.valueOf(staffId));
				String methodPath = Constant.MP_SHIFT;
				String methodName = Constant.MN_SHIFT2INSETACTOR;
				WebServiceManager webServiceManager = new WebServiceManager(act,
						methodName, parmValues);
				String result = webServiceManager.OpenConnect(methodPath);
				Log.e("zwt", "返回结果" + result);
				Gson gson = new Gson();
				Type type = new TypeToken<Result<Shift2InGetAllShiftOutData>>(){}.getType();
				Result<Shift2InGetAllShiftOutData> serverData = gson.fromJson(result, type);
				if(result != null && result.length() > 0){
					if(serverData.isMsgType()){
						handler.sendMessage(handler.obtainMessage(SHIFT2INSETACTOR_SUCCESS, serverData.getMsg()));
					}else{
						handler.sendMessage(handler.obtainMessage(SHIFT2INSETACTOR_FAIL, serverData.getMsg()));
					}
				}else{
					handler.sendEmptyMessage(NET_ERROR);
				}
				
			};
		}.start();
	}
	
}
