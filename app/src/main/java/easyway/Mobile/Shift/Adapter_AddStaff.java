package easyway.Mobile.Shift;

import java.util.ArrayList;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.R;
import easyway.Mobile.Data.Staff;

import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Adapter_AddStaff extends BaseAdapter{

	private ActivityEx act;
	private ArrayList<Staff> list;
	private ArrayList<Long> remember;
	private ArrayList<String> remember1;
	public Adapter_AddStaff(ActivityEx act, ArrayList<Staff> list) {
		super();
		this.act = act;
		this.list = list;
		remember = new ArrayList<Long>();
		remember1 = new ArrayList<String>();
	}

	public Adapter_AddStaff() {
		super();
		remember = new ArrayList<Long>();
		remember1 = new ArrayList<String>();
	}
	
	ArrayList<ArrayList<?>> getData(){
		ArrayList<ArrayList<?>> mDataList = new ArrayList<ArrayList<?>>();
		mDataList.add(0, remember);
		mDataList.add(1, remember1);
		return mDataList;
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
		final mAddStaffHolder holder;
		final Staff base = list.get(position);
		if(convertView == null){
			view = View.inflate(act, R.layout.adapter_rollcall, null);
			holder = new mAddStaffHolder();
			holder.name = (TextView)view.findViewById(R.id.Adapter_RollCall_name);
			holder.check = (ImageView)view.findViewById(R.id.Adapter_RollCall_check);
			holder.item = (LinearLayout)view.findViewById(R.id.Adapter_RollCall_item);
			view.setTag(holder);
		}else{
			view = convertView;
			holder = (mAddStaffHolder)view.getTag();
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
					if(remember.size()>0){
						act.showToast("只能选择分配一人");
					}else{
						remember.add(base.StaffId);
						remember1.add(base.StaffName);
						holder.check.setImageResource(R.drawable.checkbox_selected);
					}
					
				}
			}
		});	
		
		return view;
	}
	
	class mAddStaffHolder {
		TextView name;
		ImageView image;
		ImageView check;
		LinearLayout item;
	}
}
