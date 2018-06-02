package easyway.Mobile.DevFault;

import java.util.ArrayList;

import easyway.Mobile.R;
import easyway.Mobile.Data.GetDevInGroupResult;
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
public class DeviceInGroupApter extends BaseAdapter {
	private ArrayList<GetDevInGroupResult> mList;
	private Context mContext;
	private LayoutInflater mInflater;

	public DeviceInGroupApter(Context context, ArrayList<GetDevInGroupResult> devList) {
		this.mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mList = devList;
	}

	public int getCount() {
		if (mList == null)
			return 0;
		return mList.size();
	}

	public GetDevInGroupResult getItem(int position) {
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

	public void setData(ArrayList<GetDevInGroupResult> devList) {
		mList = devList;
	}

	@SuppressLint("ResourceAsColor") public View getView(final int position, View convertView, ViewGroup parent) {
		final GetDevInGroupResult deviceBean = mList.get(position);
		ViewHolder holder;
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.device_list_item, null);
			holder = new ViewHolder();
			holder.deviceName = (TextView)convertView.findViewById(R.id.deviceName);
			holder.deviceTwName = (TextView)convertView.findViewById(R.id.deviceTwName);
			holder.deviceAddress = (TextView)convertView.findViewById(R.id.deviceAddress);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
        holder.deviceName.setText(deviceBean.getDevName());
        holder.deviceTwName.setText(deviceBean.getTwName());
        holder.deviceAddress.setText(deviceBean.getLocation());
		return convertView;
	}



	private static class ViewHolder {
		TextView deviceName;
		TextView deviceTwName;
		TextView deviceAddress;
	}

}
