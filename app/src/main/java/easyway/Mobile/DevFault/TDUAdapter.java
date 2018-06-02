package easyway.Mobile.DevFault;

import java.util.ArrayList;

import easyway.Mobile.R;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TDUAdapter extends BaseAdapter{
	
	private ArrayList<TaskDevUnusualBase> list;
	private Activity act;
	

	public TDUAdapter() {
		super();
	}

	public TDUAdapter(ArrayList<TaskDevUnusualBase> list,Activity act) {
		super();
		this.list = list;
		this.act = act;
	}



	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		MyViewHolder holder;
		TaskDevUnusualBase base = list.get(position);
		if(convertView == null){
			view = View.inflate(act, R.layout.adapter_tdu, null);
			holder = new MyViewHolder();
			holder.StaffName = (TextView)view.findViewById(R.id.StaffName);
			holder.Workspace = (TextView)view.findViewById(R.id.Workspace);
			holder.Operate = (TextView)view.findViewById(R.id.Operate);
			view.setTag(holder);
		}else{
			view = convertView;
			holder = (MyViewHolder)view.getTag();
		}
		holder.StaffName.setText(base.StaffName);
		holder.Workspace.setText(base.Workspace);
		return view;
	}
	
	class MyViewHolder {
		TextView StaffName;
		TextView Workspace;
		TextView Operate;
	}

}
