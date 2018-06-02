package easyway.Mobile.DevFault;

import java.util.ArrayList;

import easyway.Mobile.R;
import easyway.Mobile.Data.GetDevSparePartsUsingHistoryData;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;



/*
 * 设备报障Adapter
 */
public class SpareUsingHistoryAdapter extends BaseAdapter {
	private ArrayList<GetDevSparePartsUsingHistoryData> mList;
	private Context mContext;
	private LayoutInflater mInflater;

	public SpareUsingHistoryAdapter(Context context, ArrayList<GetDevSparePartsUsingHistoryData> devList) {
		this.mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mList = devList;
	}

	public int getCount() {
		if (mList == null)
			return 0;
		return mList.size();
	}

	public GetDevSparePartsUsingHistoryData getItem(int position) {
		if (mList == null)
			return null;

		if (position >= getCount()) {
			return null;
		} else {
			return mList.get(position);
		}
	}

	public long getItemId(int position) {
		return position;
	}

	public void setData(ArrayList<GetDevSparePartsUsingHistoryData> devList) {
		mList = devList;
	}

	@SuppressLint("ResourceAsColor") public View getView(final int position, View convertView, ViewGroup parent) {
		final GetDevSparePartsUsingHistoryData deviceBean = mList.get(position);
		ViewHolder holder;
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.device_list_usehistoryitem, null);
			holder = new ViewHolder();
			holder.DevName = (TextView)convertView.findViewById(R.id.DevName);
			holder.DevCode = (TextView)convertView.findViewById(R.id.DevCode);
			holder.UseTime = (TextView)convertView.findViewById(R.id.UseTime);
			holder.UseStaffName = (TextView)convertView.findViewById(R.id.UseStaffName);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
        holder.DevName.setText(deviceBean.getDevName());
        holder.DevCode.setText(deviceBean.getDevCode());
        holder.UseTime.setText(deviceBean.getUseTime());
        holder.UseStaffName.setText(deviceBean.getUseStaffName());
		return convertView;
	}



	private static class ViewHolder {
		TextView DevName;
		TextView DevCode;
		TextView UseTime;
		TextView UseStaffName;
	}

}
