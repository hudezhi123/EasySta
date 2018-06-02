package easyway.Mobile.Shift;

import java.util.ArrayList;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import easyway.Mobile.R;
import easyway.Mobile.Data.Staff;

public class Adapter_Carryout extends BaseAdapter{
	
	private ArrayList<Staff> list;
	private Activity act;
	private ArrayList<Long> remember;
	private ArrayList<String> remember1;
	
	
	public Adapter_Carryout(ArrayList<Staff> list, Activity act) {
		super();
		this.list = list;
		this.act = act;
		remember = new ArrayList<Long>();
		remember1 = new ArrayList<String>();
	}
	
	public Adapter_Carryout() {
		super();
		remember = new ArrayList<Long>();
		remember1 = new ArrayList<String>();
	}

	ArrayList<ArrayList<?>> getData(){
		ArrayList<ArrayList<?>> mData = new ArrayList<ArrayList<?>>();
		mData.add(0, remember);
		mData.add(1, remember1);
		return mData;
	}
	
	

	@Override
	public int getCount() {
		return list.size();
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
		final CarryOutViewHolder holder;
		final Staff base = list.get(position);
		if(convertView == null){
			view = View.inflate(act, R.layout.adapter_rollcall, null);
			holder = new CarryOutViewHolder();
			holder.name = (TextView)view.findViewById(R.id.Adapter_RollCall_name);
			holder.check = (ImageView)view.findViewById(R.id.Adapter_RollCall_check);
			holder.item = (LinearLayout)view.findViewById(R.id.Adapter_RollCall_item);
			view.setTag(holder);
		}else{
			view = convertView;
			holder = (CarryOutViewHolder)view.getTag();
		}
		holder.name.setText(base.StaffName);
		if(remember.contains(base.StaffId)){
			holder.check.setImageResource(R.drawable.checkbox_selected);
		}else{
			holder.check.setImageResource(R.drawable.checkbox_normal);
		}
		
		holder.item.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(remember.contains(base.StaffId)){
					remember.remove(base.StaffId);
					remember1.remove(base.StaffName);
					holder.check.setImageResource(R.drawable.checkbox_normal);
				}else{
					remember.add(base.StaffId);
					remember1.add(base.StaffName);
					holder.check.setImageResource(R.drawable.checkbox_selected);
				}
			}
		});
		
		return view;
	}
	
	class CarryOutViewHolder {
		TextView name;
		ImageView image;
		ImageView check;
		LinearLayout item;
	}
}
