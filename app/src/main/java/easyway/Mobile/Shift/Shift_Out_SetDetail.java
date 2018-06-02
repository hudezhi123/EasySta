package easyway.Mobile.Shift;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.ResultForListData;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.ShiftData.Shift2OutGetDetailOpeningTask;
import easyway.Mobile.ShiftData.Shift2OutGetOpeningTask;
import easyway.Mobile.util.CommonFunc;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.Player;
import easyway.Mobile.ShiftData.Shift2OutAllType;
public class Shift_Out_SetDetail extends ActivityEx implements OnClickListener{

	private Context context;
	private long shift_Id;
	private ArrayList<Shift2OutGetDetailOpeningTask> detailList = new ArrayList<Shift2OutGetDetailOpeningTask>(); 
	private Shift_Out_DetailAdapter shift_out_Detailadapter ;
	private ArrayList<Shift2OutAllType> shift2OutAllType = new ArrayList<Shift2OutAllType>();
	private ArrayList<Shift2OutAllType> shift2OutAllTypeList = new ArrayList<Shift2OutAllType>();
	private int saId = 0;
	private ArrayList<Shift2OutGetDetailOpeningTask> tempList = new ArrayList<Shift2OutGetDetailOpeningTask>();
	private ListView gvList;
	private boolean ShiftIn;
	private long shift_Detail_Id;
	private int workType;
	private final int Shift2_Out_GetAllDetails_Success = 10;
	private final int Shift2_Out_GetAllDetails_Fail = 11;
	private final int Net_Error = 12;
	private ArrayList<Shift2OutGetOpeningTask> prolist;
	private String saIds = "";
	private String Shift_In_StaffName = "";
	private int Sd_saId = 0;
	private boolean isShiftIn = false;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			closeProgressDialog();
			switch (msg.what) {
			case 0:
				showToast("Error");
				break;
			case 1:
				ArrayList<Shift2OutAllType> list = (ArrayList<Shift2OutAllType>) msg.obj;
				shift_out_Detailadapter = new Shift_Out_DetailAdapter(Shift_Out_SetDetail.this,shift2OutAllType);
				shift_out_Detailadapter.setData(list);
				gvList.setAdapter(shift_out_Detailadapter);
//				shift_out_Detailadapter.notifyDataSetChanged();
				break;

			case 2:
//				shift2OutAllType.remove(msg.obj);
				showToast("删除成功");
				ArrayList<Shift2OutAllType> list2 = (ArrayList<Shift2OutAllType>) msg.obj;
				shift_out_Detailadapter.setData(list2);
				shift_out_Detailadapter.notifyDataSetChanged();
				break;
//			case Shift2_Out_GetAllDetails_Success:
//				detailList = (ArrayList<Shift2OutGetDetailOpeningTask>)msg.obj;
//				if(detailList != null && detailList.size()>0){
//					shift_out_Detailadapter = new Shift_Out_DetailAdapter(context, detailList);
////					shift_out_Detailadapter.setData(detailList);
////					shift_out_Detailadapter.notifyDataSetChanged();
//					gvList.setAdapter(shift_out_Detailadapter);
//					
//				}else{
//					showToast("没有设置明细信息");
//				}
//				
//				break;
			case 3:
				showToast((String) msg.obj);
				break;
			case 4:
				String sucStr = (String)msg.obj;
				showToast(sucStr);
				break;
			case Shift2_Out_GetAllDetails_Fail:
				String failStr = (String)msg.obj;
				showToast(failStr);
				break;
			case 10:
				showToast("请选择任务");
				break;
			case Net_Error:
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
		setContentView(R.layout.shift_out_checkdetail);

		context = this;
		init();
//		new Thread(new AllTask4ShiftThread()).start();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				detailList = GetAllTaskDetail();
				prolist = getAllTask();
				shift2OutAllType = conbine(detailList,prolist);
				Message message = new Message();
				message.what=1;
				message.obj = shift2OutAllType;
				mHandler.sendMessage(message);
			}
		}).start();
		
	}

	protected ArrayList<Shift2OutAllType> conbine(
			ArrayList<Shift2OutGetDetailOpeningTask> detailList2,
			ArrayList<Shift2OutGetOpeningTask> prolist2) {
		ArrayList<Shift2OutAllType> allList = new ArrayList<Shift2OutAllType>();
		ArrayList<Integer> saidList = new ArrayList<Integer>();
		
		for (int i = 0; i < detailList2.size(); i++) {
			Shift2OutGetDetailOpeningTask d = detailList2.get(i);
			Shift2OutAllType allType = new Shift2OutAllType();
			allType.Sd_TRNO_PRO = d.Sd_TRNO_PRO;
			allType.Sd_BeginWorkTime = d.Sd_BeginWorkTime;
			allType.Sd_EndWorkTime = d.Sd_EndWorkTime;
			allType.Sd_RBeginWorkTime = d.Sd_RBeginWorkTime;
			allType.Sd_DeptName = d.Sd_DeptName;
			allType.Sd_RWorkspaces = d.Sd_RWorkspaces;
			allType.Sd_StaffName = d.Sd_StaffName;
			allType.Sd_TaskId = d.Sd_TaskId;
			allType.Sd_Remark = d.Sd_Remark;
			allType.Shift_Detail_Id = d.Shift_Detail_Id;
			allType.Sd_SaId = d.Sd_SaId;
			allType.SaId = d.Sd_SaId;
			allType.Sd_VoicePath = d.Sd_VoicePath;
			saidList.add(d.Sd_SaId);
			allList.add(allType);
		}
		for (int i = 0; i < prolist2.size(); i++) {
			Shift2OutGetOpeningTask p = prolist2.get(i);
			Shift2OutAllType allType = new Shift2OutAllType();
			allType.TRNO_PRO = p.TRNO_PRO;
			allType.BeginWorkTime = p.BeginWorkTime;
			allType.EndWorkTime = p.EndWorkTime;
			allType.RBeginWorkTime = p.RBeginWorkTime;
			allType.DeptName = p.DeptName;
			allType.RWorkspaces = p.RWorkspaces;
			allType.StaffName = p.StaffName;
			allType.TaskId = p.TaskId;
			allType.TaskRemark = p.TaskRemark;
			allType.SaId = p.SaId;
			if (!saidList.contains(p.SaId)) {
				allList.add(allType);
				
			}
		}
		
		
		return allList;
	}

	private ArrayList<Shift2OutGetOpeningTask> getAllTask() {
		ArrayList<Shift2OutGetOpeningTask> list = new ArrayList<Shift2OutGetOpeningTask>();
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("workType", String.valueOf(workType));
		String methodPath = "WebService/Shift.asmx";
		String methodName = "Shift2_Out_GetOpeningTask";
		WebServiceManager webServiceManager = new WebServiceManager(
				Shift_Out_SetDetail.this, methodName, parmValues);
		try {
			String result = webServiceManager.OpenConnect(methodPath);
			String errMsg = JsonUtil.GetJsonString(result, "Msg");
			Gson gson = new Gson();
			Type type = new TypeToken<ResultForListData<Shift2OutGetOpeningTask>>() {
			}.getType();
			ResultForListData<Shift2OutGetOpeningTask> serverData = gson
					.fromJson(result, type);
			List<Shift2OutGetOpeningTask> data = serverData.getData();
			list.addAll(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	
	}

	private void init() {
		int id = getIntent().getIntExtra("shift_Id", -1);
		workType = getIntent().getIntExtra("workType", -1);
		shift_Id = Long.parseLong(String.valueOf(id));
		Shift_In_StaffName = getIntent().getStringExtra("Shift_In_StaffName");
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("交班明细");
		Button btn_shiftOutPost = (Button) findViewById(R.id.btn_shiftOutPost);
		btn_shiftOutPost.setOnClickListener(this);
		if(TextUtils.isEmpty(Shift_In_StaffName)){
			btn_shiftOutPost.setEnabled(true);
			isShiftIn = false;
		}else{
			btn_shiftOutPost.setEnabled(false);
			isShiftIn = true;
		}
	

		gvList = (ListView)findViewById(R.id.gvList);
		

	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_shiftOutPost:
			AlertDialog.Builder builder = new Builder(Shift_Out_SetDetail.this);
			builder.setIcon(R.drawable.information);
			builder.setMessage(getString(R.string.Shift_Out_Confirm));
			builder.setTitle(getString(R.string.Prompt));
			builder.setPositiveButton(getString(R.string.Shift_Out_OK),
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

							showProgressDialog("正在交班");
							new Thread() {
								@Override
								public void run() {
									postShiftOut();
									super.run();
								}
							}.start();
						}
					}).show();
			break;

		default:
			break;
		}
		

	}

//	private class AllTask4ShiftThread implements Runnable {
		// ArrayList<String> list = new ArrayList<String>();
//	private class AllTask4ShiftThread implements Runnable {
//		// ArrayList<String> list = new ArrayList<String>();
//
//		@Override
//		public void run() {
//
//			detailList= GetAllTaskDetail();
//
//			if (null == detailList) {
//				Message msg = new Message();
//				msg.what = 0;
//				mHandler.sendMessage(msg);
//			} else {
//				Message msg = new Message();
//				msg.what = 1;
//				msg.obj = detailList;
//				mHandler.sendMessage(msg);
//			}
//		}
//	}

	

	public ArrayList<Shift2OutGetDetailOpeningTask>  GetAllTaskDetail() {
		
			
		HashMap<String, String> parmValues = new HashMap<String, String>();
		ArrayList<Shift2OutGetDetailOpeningTask> list = new ArrayList<Shift2OutGetDetailOpeningTask>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("shift_Id", String.valueOf(shift_Id));
		String methodPath = Constant.MP_SHIFT;
		String methodName = "Shift2_Out_GetAllDetails";
		WebServiceManager webServiceManager = new WebServiceManager(
				getApplicationContext(), methodName, parmValues);
		try {
		String result = webServiceManager.OpenConnect(methodPath);
		Gson gson = new Gson();
		Type type = new TypeToken<ResultForListData<Shift2OutGetDetailOpeningTask>>() {
		}.getType();
		ResultForListData<Shift2OutGetDetailOpeningTask> serverData = gson
				.fromJson(result, type);
		
		String msg = serverData.getMsg();
		if (!msg.equals("获取交班信息成功")) {
			return null;
		}
			//			boolean msgType = serverData.isMsgType();
			//			if(!msgType)
			//				return null;
			if(result != null && result.length() > 0){
				if(serverData.isMsgType()){
//					mHandler.sendMessage(mHandler.obtainMessage(Shift2_Out_GetAllDetails_Success, serverData.getData()));
					List<Shift2OutGetDetailOpeningTask> data = serverData.getData();
					list.addAll(data);
					return list;
				}else{
					mHandler.sendMessage(mHandler.obtainMessage(Shift2_Out_GetAllDetails_Fail, serverData.getMsg()));
				}
			}else{
				mHandler.sendEmptyMessage(Net_Error);
			}
//			List<Shift2OutGetOpeningTask> data = serverData.getData();
//			list.addAll(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	
	private void postShiftOut() {
		for (int i = 0; i < shift2OutAllTypeList.size(); i++) {
			if (i < shift2OutAllTypeList.size() - 1) {
				saIds = saIds + shift2OutAllTypeList.get(i).getSaId() + ",";
			} else {
				saIds = saIds + shift2OutAllTypeList.get(i).getSaId();
			}
		}
		if(TextUtils.isEmpty(saIds)){
			mHandler.sendEmptyMessage(10);
			return;
		}
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("shift_Id", String.valueOf(shift_Id));
		parmValues.put("saIds", saIds);
		String methodPath = "WebService/Shift.asmx";
		String methodName = "Shift2_Out_Complete";
		
		WebServiceManager webServiceManager = new WebServiceManager(
				getApplicationContext(), methodName, parmValues);
		try {
			String result = webServiceManager.OpenConnect(methodPath);
			String errMsg = JsonUtil.GetJsonString(result, "MsgType");
			String Msg = JsonUtil.GetJsonString(result, "Msg");
			if (!errMsg.equals("true")) {
				// throw new Exception(errMsg);
				Message msg = new Message();
				msg.what = 3;
				msg.obj = errMsg;
				mHandler.sendMessage(msg);
			} else {
				Message msg = new Message();
				msg.what = 4;
				msg.obj = Msg;
				mHandler.sendMessage(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private class Shift_Out_DetailAdapter extends BaseAdapter {

		private ArrayList<Shift2OutAllType> todoList;
		private LayoutInflater infl;
		private Player player;
		public Shift_Out_DetailAdapter(Context context,
				ArrayList<Shift2OutAllType> todoList ) {
			this.todoList = todoList;
			this.infl = LayoutInflater.from(context);
		}

		public void setData(ArrayList<Shift2OutAllType> detailList) {
			todoList = detailList;
			Log.i("1","1" );
		}

		private boolean isChoosed(Shift2OutAllType trainNum) {
			return shift2OutAllTypeList.contains(trainNum);
		}
		

		@Override
		public int getCount() {
			if (todoList == null)
				return 0;
			return todoList.size();
		}

		@Override
		public Shift2OutAllType getItem(int position) {
			if (getCount() == 0)
				return null;
			return todoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final Shift2OutAllType trainNum = getItem(position);
			Sd_saId = trainNum.getSd_SaId();
			saId = trainNum.getSaId();
			
			final ViewHolder holder;
			if (null == convertView) {
				convertView = infl.inflate(
						R.layout.shift_out_detail_list_item_new, null);
				holder = new ViewHolder();
				holder.rl_item = (RelativeLayout) convertView
						.findViewById(R.id.rl_item);
				holder.chkTrainNum = (ImageView) convertView
						.findViewById(R.id.chkTrainNum);
				holder.train_number = (TextView) convertView
						.findViewById(R.id.train_number);
				holder.plan_time = (TextView) convertView
						.findViewById(R.id.plan_time);
				holder.plan_end = (TextView) convertView
						.findViewById(R.id.plan_end);
				holder.act_start = (TextView) convertView
						.findViewById(R.id.act_start);
				holder.area = (TextView) convertView.findViewById(R.id.area);
				holder.exe_arm = (TextView) convertView
						.findViewById(R.id.exe_arm);
				holder.exe_staff = (TextView) convertView
						.findViewById(R.id.exe_staff);
				holder.task_state = (TextView) convertView
						.findViewById(R.id.task_state);
				holder.remark_text = (TextView) convertView
						.findViewById(R.id.remark_text);
				holder.remark_voice = (Button) convertView
						.findViewById(R.id.remark_voice);
				holder.btn_delet = (Button) convertView
						.findViewById(R.id.btn_delet);
				

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			String voicePath = trainNum.Sd_VoicePath;
			if(TextUtils.isEmpty(voicePath)){
				holder.remark_voice.setVisibility(View.GONE);
			}else{
				holder.remark_voice.setVisibility(View.VISIBLE);
			}
			holder.remark_voice.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					String serverName = CommonFunc.GetServer(context);
					String voicePathStr = trainNum.Sd_VoicePath;
					voicePathStr = voicePathStr.substring(1);
					String url = serverName+voicePathStr;//"Uploaded/Shift/2016/09/11/22/M22.wav";
					String str = holder.remark_voice.getText().toString().trim();
					if(str.equals("语音播放")){
						if(player == null){
							player = new Player();
							player.mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
								
								@Override
								public void onCompletion(MediaPlayer arg0) {
									// TODO Auto-generated method stub
									Toast.makeText(context, "播放完成", Toast.LENGTH_LONG).show();
									holder.remark_voice.setText("语音播放");
								}
							});
						}  
						player.playUrl(url);
						holder.remark_voice.setText("暂停");
					}else {
						player.stop();
						player = null;
						holder.remark_voice.setText("语音播放");
					}
				}
			});
			holder.btn_delet.setOnClickListener(new OnClickListener() {
				

				@Override
				public void onClick(View v) {
					

					new Thread(new Runnable() {
						
						
						@Override
						public void run() {
							Shift2OutAllType item = getItem(position);
							shift_Detail_Id = item.getShift_Detail_Id();
							boolean detailRemove = detailRemove();
							if (detailRemove) {
								detailList = GetAllTaskDetail();
								prolist = getAllTask();
								shift2OutAllType = conbine(detailList,prolist);
								Message message = new Message();
								message.what = 2;
								message.obj = shift2OutAllType;
								mHandler.sendMessage(message);
							}
							
						}
						private boolean detailRemove() {
							HashMap<String, String> parmValues = new HashMap<String, String>();
							ArrayList<Shift2OutGetDetailOpeningTask> list = new ArrayList<Shift2OutGetDetailOpeningTask>();
							parmValues.put("sessionId", Property.SessionId);
							parmValues.put("shift_Detail_Id", String.valueOf(shift_Detail_Id));
							//		parmValues.put("shift_Id", "22");
							String methodPath = Constant.MP_SHIFT;
							String methodName = "Shift2_Out_Detail_Remove";
							WebServiceManager webServiceManager = new WebServiceManager(
									getApplicationContext(), methodName, parmValues);
							try {
								String result = webServiceManager.OpenConnect(methodPath);
								String errMsg = JsonUtil.GetJsonString(result, "MsgType");
								if (!errMsg.equals("true")) {
									Message message = new Message();
									message.what =0;
									mHandler.sendMessage(message);
									return false;
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							return true;
						}
					}).start();
				}
			});
			holder.chkTrainNum
			.setImageResource(isChoosed(trainNum) ? R.drawable.checkbox_selected
					: R.drawable.checkbox_normal);
			holder.rl_item.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
//					if (ShiftIn) {
						holder.chkTrainNum
								.setImageResource(R.drawable.checkbox_selected);

						if (isChoosed(trainNum)) {
							holder.chkTrainNum
									.setImageResource(R.drawable.checkbox_normal);
							shift2OutAllTypeList.remove(trainNum);

						} else {
							holder.chkTrainNum
									.setImageResource(R.drawable.checkbox_selected);
							shift2OutAllTypeList.add(trainNum);

						}
					}
//				}
			});
			
				if (Sd_saId==saId) {
					
					holder.train_number.setText(trainNum.getSd_TRNO_PRO());
					holder.plan_time.setText(trainNum.getSd_BeginWorkTime());
					holder.plan_end.setText(trainNum.getSd_EndWorkTime());
					holder.act_start.setText(trainNum.getSd_RBeginWorkTime());
					holder.exe_arm.setText(trainNum.getSd_DeptName());
					holder.area.setText(trainNum.getSd_RWorkspaces());
					holder.exe_staff.setText(trainNum.getSd_StaffName());
					holder.task_state.setText("已交班");
					holder.chkTrainNum.setVisibility(View.GONE);
					holder.btn_delet.setVisibility(View.VISIBLE);
					holder.rl_item.setEnabled(false);
				}else {
					holder.train_number.setText(trainNum.getTRNO_PRO());
					holder.plan_time.setText(trainNum.getBeginWorkTime());
					holder.plan_end.setText(trainNum.getEndWorkTime());
					holder.act_start.setText(trainNum.getRBeginWorkTime());
					holder.exe_arm.setText(trainNum.getDeptName());
					holder.area.setText(trainNum.getRWorkspaces());
					holder.exe_staff.setText(trainNum.getStaffName());
					holder.task_state.setText(String.valueOf(trainNum.getSd_TaskId()));
					holder.chkTrainNum.setVisibility(View.VISIBLE);
					holder.btn_delet.setVisibility(View.GONE);
					holder.rl_item.setEnabled(true);
				}
				
			if(isShiftIn)
				holder.rl_item.setEnabled(false);
			else
				holder.rl_item.setEnabled(true);

			return convertView;
		}
   }
	

	private static class ViewHolder {
		private ImageView chkTrainNum;
		private TextView train_number;
		private TextView plan_time;
		private TextView remark_text;
		private TextView plan_end;
		private TextView act_start;
		private TextView area;
		private TextView exe_arm;
		private TextView exe_staff;
		private TextView task_state;
		private Button remark_voice;
		private Button btn_delet;
		private RelativeLayout rl_item;
	}

}
