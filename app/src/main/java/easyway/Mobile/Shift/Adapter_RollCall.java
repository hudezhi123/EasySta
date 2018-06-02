package easyway.Mobile.Shift;

import java.util.ArrayList;

import easyway.Mobile.R;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Adapter_RollCall extends BaseAdapter {

	private ArrayList<RollCallBase> list;
	private Activity act;
	private ArrayList<Long> remember;
	
	public Adapter_RollCall(ArrayList<RollCallBase> list, Activity act) {
		super();
		this.list = list;
		this.act = act;
		remember = new ArrayList<Long>();
	}
	
	public Adapter_RollCall() {
		super();
		remember = new ArrayList<Long>();
	}

	public void setData(ArrayList<RollCallBase> list1){
		list = list1;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view;
		final RollCallViewHolder holder;
		final RollCallBase base = list.get(position);
		if(convertView == null){
			view = View.inflate(act, R.layout.adapter_rollcall, null);
			holder = new RollCallViewHolder();
			holder.name = (TextView)view.findViewById(R.id.Adapter_RollCall_name);
			holder.check = (ImageView)view.findViewById(R.id.Adapter_RollCall_check);
			holder.item = (LinearLayout)view.findViewById(R.id.Adapter_RollCall_item);
			view.setTag(holder);
		}else{
			view = convertView;
			holder = (RollCallViewHolder)view.getTag();
		}
		holder.name.setText(base.name);
		
		if(remember.contains(base.staffId)){
			holder.check.setImageResource(R.drawable.checkbox_selected);
		}else{
			holder.check.setImageResource(R.drawable.checkbox_normal);
		}
		
		holder.item.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(remember.contains(base.staffId)){
					remember.remove(base.staffId);
					holder.check.setImageResource(R.drawable.checkbox_normal);
				}else{
					remember.add(base.staffId);
					holder.check.setImageResource(R.drawable.checkbox_selected);
				}
			}
		});
		return view;
	}

	class RollCallViewHolder {
		TextView name;
		ImageView image;
		ImageView check;
		LinearLayout item;
	}
}
