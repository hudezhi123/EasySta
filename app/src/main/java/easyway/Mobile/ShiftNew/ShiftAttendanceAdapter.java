package easyway.Mobile.ShiftNew;

import java.util.ArrayList;

import easyway.Mobile.Property;
import easyway.Mobile.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/*
 * 交接班-出勤情况 Adapter
 */
public class ShiftAttendanceAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private ArrayList<ShiftAttendance> mList;
//	private Context mContext;

	public ShiftAttendanceAdapter(Context context, ArrayList<ShiftAttendance> list) {
		super();
		mInflater = LayoutInflater.from(context);
		mList = list;
//		mContext = context;
	}

	@Override
	public int getCount() {
		if (mList == null)
			return 0;
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		if (mList == null)
			return null;
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setData(ArrayList<ShiftAttendance> models) {
		mList = models;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		ShiftAttendance obj = (ShiftAttendance) getItem(position);
		if (obj == null)
			return null;
		
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.shift_attendance_item, null);
			holder = new ViewHolder();
			holder.txtNo = (TextView) convertView.findViewById(R.id.txtNo);
			holder.txtName = (TextView) convertView
					.findViewById(R.id.txtName);
			holder.txtStatus = (TextView) convertView
					.findViewById(R.id.txtStatus);
			holder.txtDep = (TextView) convertView
					.findViewById(R.id.txtDep);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.txtNo.setText(String.valueOf(position + 1));
		holder.txtName.setText(obj.Name);
		holder.txtStatus.setText(obj.Status);
		holder.txtDep.setText(obj.Dep);

		holder.txtNo.setWidth(Property.screenwidth/8);
		holder.txtName.setWidth(Property.screenwidth/4);
		holder.txtStatus.setWidth(Property.screenwidth/4);
		holder.txtDep.setWidth(Property.screenwidth * 3/8);
		
		return convertView;
	}

	class ViewHolder {
		TextView txtNo;
		TextView txtName;
		TextView txtStatus;
		TextView txtDep;
	}
}
