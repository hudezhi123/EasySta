package easyway.Mobile.Shift;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Data.Result;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.ShiftData.Shift2InGetAllShiftOutData;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Adapter_New_Shift_In extends BaseAdapter {

	private ActivityEx act;
	private ArrayList<Shift2InGetAllShiftOutData> list;
	private Handler mHandler;
	private final int SHIFT2INSAVE_SUCCESS = 10;
	private final int SHIFT2INSAVE_FAIL = 11;
	private final int NET_ERROR = 2;
	private int workType;
	public void setData(ArrayList<Shift2InGetAllShiftOutData> list){
		this.list = list;
		
	}
	
	public Adapter_New_Shift_In(ActivityEx act, ArrayList<Shift2InGetAllShiftOutData> list,Handler handler,int workType) {
		super();
		this.act = act;
		this.list = list;
		this.mHandler = handler;
		this.workType = workType;
	}
	
	public Adapter_New_Shift_In() {
		super();
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder holder;
		final Boolean isShiftIn;
		final Shift2InGetAllShiftOutData base = list.get(position);
		if(convertView == null){
			view = View.inflate(act, R.layout.shift2_in_adapter, null);
			holder = new ViewHolder();
			holder.shift2InItemView = (RelativeLayout)view.findViewById(R.id.shift2InItemView);
			holder.ShiftOut_PeopleName = (TextView)view.findViewById(R.id.ShiftOut_PeopleName);
			holder.ShiftOut_Time = (TextView)view.findViewById(R.id.ShiftOut_Time);
		    holder.shiftInBtn = (Button)view.findViewById(R.id.shiftInBtn);
			view.setTag(holder);
		}else{
			view = convertView;
			holder = (ViewHolder)view.getTag();
		}
           holder.ShiftOut_PeopleName.setText(base.getShift_Out_StaffName());
           holder.ShiftOut_Time.setText(base.getShift_Out_Dttm());
           if(!TextUtils.isEmpty(base.getShift_In_StaffName())){
        	   holder.shiftInBtn.setText("已接班");
        	   holder.shiftInBtn.setEnabled(false);
        	   isShiftIn = true;
           }else{
        	   holder.shiftInBtn.setText("接班");
        	   holder.shiftInBtn.setEnabled(true);
        	   isShiftIn = false;
           }
           
           if(workType == 5)
        	   holder.shiftInBtn.setVisibility(View.GONE);
        	   
           holder.shiftInBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getInfo(base.getShift_Id());
			}
		});
		holder.shift2InItemView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO 跳转到接班信息页面。
//				Intent intent = new Intent(act,New_Shift_In_Info.class);
//				intent.putExtra("TeamId", base.TeamId);
//				intent.putExtra("ShiftId", base.ShiftId);
//				intent.putExtra("WorkTypeId", base.WorkTypeId);
				if(workType != 5){
					Intent intent = new Intent(act,ShiftInDetailActivity.class);
					intent.putExtra("ShiftId", base.getShift_Id());
					intent.putExtra("isShiftIn", isShiftIn);
					intent.putExtra("outVoice", base.getShift_Out_Voice());
					act.startActivity(intent);
				}else{
					Intent intent = new Intent(act,Shift_Out_SetDetail.class);
					intent.putExtra("shift_Id", base.getShift_Id());
					intent.putExtra("workType", base.getShift_WorkType());
					intent.putExtra("Shift_In_StaffName", base.getShift_In_StaffName());
					act.startActivity(intent);
				}
				
			}
		});
			 
	  
		
		return view;
	}
	
	class ViewHolder{
		RelativeLayout shift2InItemView;
		TextView ShiftOut_PeopleName;
		TextView ShiftOut_Time;
		Button shiftInBtn;
	}
	
	private void getInfo(final int shiftId){
		act.showProgressDialog("正在接班");
		new Thread(){
			@Override
			public void run() {
				super.run();
				HashMap<String, String> parmValues = new HashMap<String, String>();
				parmValues.put("sessionId", Property.SessionId);
				parmValues.put("shift_Id", String.valueOf(shiftId));
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
						mHandler.sendMessage(mHandler.obtainMessage(SHIFT2INSAVE_SUCCESS, serverData.getMsg()));
					}else{
						mHandler.sendMessage(mHandler.obtainMessage(SHIFT2INSAVE_FAIL, serverData.getMsg()));
					}
				}else{
					mHandler.sendEmptyMessage(NET_ERROR);
				}
				
			};
		}.start();
	}

}
