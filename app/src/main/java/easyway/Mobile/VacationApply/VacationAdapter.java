package easyway.Mobile.VacationApply;

import java.util.ArrayList;

import easyway.Mobile.R;
import easyway.Mobile.Data.Vacation;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/*
 * 请假单Adapter
 */
public class VacationAdapter extends BaseAdapter {
	private ArrayList<Vacation> mlist;
	private LayoutInflater mInflater;


	public VacationAdapter(Context context, ArrayList<Vacation> list) {
		mInflater = LayoutInflater.from(context);
		mlist = list;
	}

	public int getCount() {
		if (mlist == null)
			return 0;
		return mlist.size();
	}

	public Vacation getItem(int position) {
		if (mlist == null) {
			return null;
		} else {
			return mlist.get(position);
		}
	}

	public long getItemId(int position) {
		return position;
	}

	public void setData(ArrayList<Vacation> models) {
		mlist = models;
	}

	public View getView(final int position, View convertView, ViewGroup arg2) {
		final Vacation obj = getItem(position);
		if (obj == null)
			return null;

		ViewHolder holder;
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.vacation_item, null);
			holder = new ViewHolder();
			holder.txtID = (TextView) convertView
					.findViewById(R.id.txtID);
			holder.txtUser = (TextView) convertView.findViewById(R.id.txtUser);
			holder.txtType = (TextView) convertView
					.findViewById(R.id.txtType);
			holder.txtStartTime = (TextView) convertView
					.findViewById(R.id.txtStartTime);
			holder.txtEndTime = (TextView) convertView
					.findViewById(R.id.txtEndTime);
			holder.txtAddTime = (TextView) convertView
					.findViewById(R.id.txtAddTime);
			holder.txtStatus = (TextView) convertView
					.findViewById(R.id.txtStatus);
			holder.txtRemark = (TextView) convertView
					.findViewById(R.id.txtRemark);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.txtID.setText(String.valueOf(obj.Id));
		holder.txtUser.setText(obj.UserName + "(" + obj.DeptName + ")");
		holder.txtType.setText(obj.Type);
		holder.txtStartTime.setText(obj.StartTime);
		holder.txtEndTime.setText(obj.EndTime);
		holder.txtAddTime.setText(obj.CreateTime);
		holder.txtStatus.setText(obj.StatusName);
		holder.txtRemark.setText(obj.Remark);

		return convertView;
	}

	static class ViewHolder {
		TextView txtID; // ID
		TextView txtUser; //  申请人
		TextView txtType; // 假别
		TextView txtStartTime; // 开始时间
		TextView txtEndTime; // 结束时间
		TextView txtAddTime; // 申请时间
		TextView txtStatus;	// 状态
		TextView txtRemark;	// 备注
		
	}
}
