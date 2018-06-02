package easyway.Mobile.Shift;

import java.util.ArrayList;

import easyway.Mobile.R;
import easyway.Mobile.ShiftData.Shift2AllDetailsData;
import easyway.Mobile.util.CommonFunc;
import easyway.Mobile.util.Player;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ShiftInDetailAdapter extends BaseAdapter{
	private Activity context;
    private ArrayList<Shift2AllDetailsData> detailsDatas = new ArrayList<Shift2AllDetailsData>();
    private LayoutInflater inflater;
    private Boolean ISShiftIn = false;
    private Handler handler;
    private Player player;
    
    public Boolean getISShiftIn() {
		return ISShiftIn;
	}
	public void setISShiftIn(Boolean iSShiftIn) {
		ISShiftIn = iSShiftIn;
	}
	public ShiftInDetailAdapter(Activity context,ArrayList<Shift2AllDetailsData> detailsDatas,Boolean ISShiftIn,Handler handler){
    	this.context = context;
    	this.detailsDatas = detailsDatas;
    	this.inflater = LayoutInflater.from(context);
    	this.ISShiftIn = ISShiftIn;
    	this.handler = handler;
    }
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(detailsDatas != null && detailsDatas.size() > 0)
			return detailsDatas.size();
		else
		  return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if(detailsDatas != null && detailsDatas.size() > 0)
			return detailsDatas.get(position);
		else
		   return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		if(detailsDatas != null && detailsDatas.size() > 0)
			return position;
		else
		   return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.shift_indetail_adapter, null);
			holder = new ViewHolder();
			holder.Sd_TRNO_PRO = (TextView)convertView.findViewById(R.id.Sd_TRNO_PRO);
			holder.Sd_BeginWorkTime = (TextView)convertView.findViewById(R.id.Sd_BeginWorkTime);
			holder.Sd_EndWorkTime = (TextView)convertView.findViewById(R.id.Sd_EndWorkTime);
			holder.Sd_RBeginWorkTime = (TextView)convertView.findViewById(R.id.Sd_RBeginWorkTime);
			holder.Sd_Workspace = (TextView)convertView.findViewById(R.id.Sd_Workspace);
			holder.Sd_RDeptName = (TextView)convertView.findViewById(R.id.Sd_RDeptName);
			holder.Sd_RStaffNames = (TextView)convertView.findViewById(R.id.Sd_RStaffNames);
			holder.Sd_Status = (TextView)convertView.findViewById(R.id.Sd_Status);
			holder.Sd_Remark = (TextView)convertView.findViewById(R.id.Sd_Remark);
			holder.Sd_VoicePath = (Button)convertView.findViewById(R.id.Sd_VoicePath);
			holder.setActorBtn = (Button)convertView.findViewById(R.id.setActorBtn);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		final Shift2AllDetailsData detailsData = detailsDatas.get(position);
		holder.Sd_TRNO_PRO.setText(detailsData.getSd_TRNO_PRO());
		holder.Sd_BeginWorkTime.setText(detailsData.getSd_BeginWorkTime());
		holder.Sd_EndWorkTime.setText(detailsData.getSd_EndWorkTime());
		holder.Sd_Workspace.setText(detailsData.getSd_Workspace());
		holder.Sd_RDeptName.setText(detailsData.getSd_RDeptName());
		holder.Sd_RStaffNames.setText(detailsData.getSd_RStaffNames());
		holder.Sd_Remark.setText(detailsData.getSd_Remark());
		if(ISShiftIn)
			holder.setActorBtn.setVisibility(View.VISIBLE);
		else
			holder.setActorBtn.setVisibility(View.GONE);
		if(detailsData.getSd_RBeginWorkTime().contains("1900-01-01")){
			holder.Sd_RBeginWorkTime.setText("");
			holder.Sd_Status.setText("未到岗");
		}else{
			holder.Sd_RBeginWorkTime.setText(detailsData.getSd_RBeginWorkTime());
			holder.Sd_Status.setText("到岗");
		}
		if(TextUtils.isEmpty(detailsData.getSd_RStaffNames()))
			holder.setActorBtn.setText("分配");
		else
			holder.setActorBtn.setText("重新分配");
		holder.setActorBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 Intent intent = new Intent(context,AddStaff.class);
				intent.putExtra("shift_Detail_Id", detailsData.getShift_Detail_Id());
				context.startActivityForResult(intent, 1111);
			}
		});
		String voicePath = detailsData.getSd_VoicePath();
		if(TextUtils.isEmpty(voicePath)){
			holder.Sd_VoicePath.setVisibility(View.GONE);
		}else
			holder.Sd_VoicePath.setVisibility(View.VISIBLE);
		holder.Sd_VoicePath.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String serverName = CommonFunc.GetServer(context);
				String voicePathStr = detailsData.getSd_VoicePath();
				voicePathStr = voicePathStr.substring(1);
				String url = serverName+voicePathStr;//"Uploaded/Shift/2016/09/11/22/M22.wav";
				String str = holder.Sd_VoicePath.getText().toString().trim();
				if(str.equals("语音播放")){
					if(player == null){
						player = new Player();
						player.mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
							
							@Override
							public void onCompletion(MediaPlayer arg0) {
								// TODO Auto-generated method stub
								Toast.makeText(context, "播放完成", Toast.LENGTH_LONG).show();
								holder.Sd_VoicePath.setText("语音播放");
							}
						});
					}  
					player.playUrl(url);
					holder.Sd_VoicePath.setText("暂停");
				}else {
					player.stop();
					player = null;
					holder.Sd_VoicePath.setText("语音播放");
				}
				   
			}
		});
			
		return convertView;
	}
	
	class ViewHolder{
		TextView Sd_TRNO_PRO;
		TextView Sd_BeginWorkTime;
		TextView Sd_EndWorkTime;
		TextView Sd_RBeginWorkTime;
		TextView Sd_Workspace;
		TextView Sd_RDeptName;
		TextView Sd_RStaffNames;
		TextView Sd_Status;
		TextView Sd_Remark;
		Button Sd_VoicePath;
		Button setActorBtn;
	}
	
	

}
