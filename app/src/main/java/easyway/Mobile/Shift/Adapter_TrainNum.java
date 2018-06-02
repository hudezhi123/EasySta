package easyway.Mobile.Shift;

import java.util.ArrayList;

import easyway.Mobile.R;
import easyway.Mobile.util.CommonFunc;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class Adapter_TrainNum extends BaseAdapter {

	private Activity act;
	private ArrayList<TrainNum_Base> list;
	private long TeamId;
	
	public Adapter_TrainNum() {
		super();
	}

	public Adapter_TrainNum(Activity act, ArrayList<TrainNum_Base> list, long teamId) {
		super();
		this.act = act;
		this.list = list;
		TeamId = teamId;
	}

	@Override
	public int getCount() {
		return list.size();
	}
	
	public void setData(ArrayList<TrainNum_Base> list, long teamId){
		this.list = list;
		TeamId = teamId;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder holder;
		final TrainNum_Base base = list.get(position);
		if(convertView == null){
			view = View.inflate(act, R.layout.adapter_new_shift_info, null);
			holder = new ViewHolder();
			holder.num = (TextView)view.findViewById(R.id.Adapter_NewShiftInfo_Num);
			holder.Attachment = (TextView)view.findViewById(R.id.Adapter_NewShiftInfo_Attachment);
			holder.bton = (Button)view.findViewById(R.id.Adapter_NewShiftInfo_CarryOut);
			holder.text = (TextView)view.findViewById(R.id.Adapter_NewShiftInfo_text);
			view.setTag(holder);
		}else{
			view = convertView;
			holder = (ViewHolder)view.getTag();
		}
		
		holder.num.setText(String.valueOf(base.TrainNum));
		holder.text.setText(base.Text + "");
		String appendixUrl = CommonFunc.GetCSharpString(base.VoicePath);
		if(appendixUrl != null && appendixUrl.equals("")){
			 String[] listAppendix = appendixUrl.split(";");
	            final ArrayList<String> list = new ArrayList<String>();
	            for (String charSequence : listAppendix) { 
	                list.add(charSequence);
	            }
	            if (listAppendix.length > 0) {
	                holder.Attachment.setVisibility(View.VISIBLE);
	            }else{
	            	holder.Attachment.setVisibility(View.GONE);
	            }
		}else{
			holder.Attachment.setVisibility(View.GONE);
		}
		//点击执行人
		holder.bton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//跳转到添加执行人界面
				Intent intent = new Intent(act,Carryout.class);
				intent.putExtra("TeamId", TeamId);
				intent.putExtra("trainNo", base.TrainNum);
				act.startActivity(intent);
			}
		});
		return view;
	}
	
	class ViewHolder{
		TextView num;
		TextView Attachment;
		Button bton;
		TextView text;
	}

}
