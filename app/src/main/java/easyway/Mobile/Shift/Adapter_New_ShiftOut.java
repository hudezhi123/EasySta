package easyway.Mobile.Shift;

import java.util.ArrayList;

import easyway.Mobile.R;
import easyway.Mobile.Attach.AttachList;
import easyway.Mobile.util.CommonFunc;
import easyway.Mobile.util.CommonUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Adapter_New_ShiftOut extends BaseAdapter {

	private Activity act;
	private ArrayList<New_ShiftOut_Base> list;
	
	public void setData(ArrayList<New_ShiftOut_Base> list){
		this.list = list;
	}
	
	public Adapter_New_ShiftOut(Activity act, ArrayList<New_ShiftOut_Base> list) {
		super();
		this.act = act;
		this.list = list;
	}
	
	public Adapter_New_ShiftOut() {
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
		final New_ShiftOut_Base base = list.get(position);
		if(convertView == null){
			view = View.inflate(act, R.layout.adapter_new_shift_out, null);
			holder = new ViewHolder();
			holder.TaskName = (TextView)view.findViewById(R.id.Adapter_newShiftOut_TaskName);
			holder.PeopleName = (TextView)view.findViewById(R.id.Adapter_newShiftOut_PeopleName);
			holder.time = (TextView)view.findViewById(R.id.Adapter_newShiftOut_Time);
			holder.bt = (Button)view.findViewById(R.id.Adapter_newShiftOut_Info);
			holder.Attachment = (TextView)view.findViewById(R.id.Adapter_newShiftOut_Attachment);
			holder.lay = (RelativeLayout)view.findViewById(R.id.Adapter_newShiftOut);
			view.setTag(holder);
		}else{
			view = convertView;
			holder = (ViewHolder)view.getTag();
		}
			holder.TaskName.setText(base.WorkTypeName);
			holder.PeopleName.setText(base.shiftOutName);
			String timer = CommonUtils.ConvertDate(base.ShiftOutDt);
			holder.time.setText(timer);
			
			String appendixUrl = CommonFunc.GetCSharpString(base.VoicePath);
			 if (!appendixUrl.equals("")) {
		            String[] listAppendix = appendixUrl.split(";");
		            final ArrayList<String> list = new ArrayList<String>();
		            for (String charSequence : listAppendix) { 
		                list.add(charSequence);
		            }
		            if (listAppendix.length > 0) {
		                holder.lay.setOnClickListener(new OnClickListener() {

		                    public void onClick(View v) {
		                        Intent intent = new Intent(act,
		                                AttachList.class);
		                        Bundle bundle = new Bundle();
		                        bundle.putStringArrayList("url", list);
		                        intent.putExtras(bundle);
		                        act.startActivity(intent);
		                    }
		                });
		                holder.Attachment.setVisibility(View.VISIBLE);
		            }else{
		            	holder.Attachment.setVisibility(View.GONE);
		            }
		  }else{
			  holder.Attachment.setVisibility(View.GONE);
			  holder.lay.setOnClickListener(null);
		  }
		holder.bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO 跳转到接班信息页面。
				Intent intent = new Intent(act,New_ShiftOut_Info.class);
				intent.putExtra("TeamId", base.TeamId);
				intent.putExtra("ShiftId", base.ShiftId);
				intent.putExtra("WorkTypeId", base.WorkTypeId);
				act.startActivity(intent);
			}
		});
			 
	  
		
		return view;
	}
	
	class ViewHolder{
		TextView TaskName;
		TextView PeopleName;
		TextView time;
		Button bt;
		TextView Attachment;
		RelativeLayout lay;
	}

}
