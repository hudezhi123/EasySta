package easyway.Mobile.DevFault;

import java.util.ArrayList;

import easyway.Mobile.R;
import easyway.Mobile.Data.DspFaultReport;
import easyway.Mobile.ShiftAdd.EquipmentDetail;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class DspAdapter extends BaseAdapter {

	private ArrayList<DspFaultReport> mList;
	private Context mContext;
	private LayoutInflater mInflater;

	private ArrayList<DspFaultReport> dataList = new ArrayList<DspFaultReport>();

	public DspAdapter(Context context, ArrayList<DspFaultReport> devFaultList) {
		this.mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mList = devFaultList;

	}

	public int getCount() {
		if (mList == null)
			return 0;
		return mList.size();
	}

	public DspFaultReport getItem(int position) {
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

	public void setData(ArrayList<DspFaultReport> devFaultList) {
		mList = devFaultList;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final DspFaultReport faultBean = mList.get(position);

		ViewHolder holder;

		convertView = mInflater.inflate(R.layout.device_dsp_item, null);
		holder = new ViewHolder();
        holder.item_view = (RelativeLayout) convertView.findViewById(R.id.item_view);
		holder.DspName = (TextView) convertView.findViewById(R.id.textView1);

		holder.cb_ok = (CheckBox) convertView.findViewById(R.id.cb_ok);

		holder.DspName.setText(faultBean.DevName);
		holder.cb_ok.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					dataList.add(faultBean);
				} else {
					dataList.remove(faultBean);
				}

			}
		});
		holder.item_view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent detailIntent = new Intent(mContext,EquipmentDetail.class);
				detailIntent.putExtra("dspId", faultBean.DspId);
				mContext.startActivity(detailIntent);
			}
		});
		return convertView;
	}

	public ArrayList<DspFaultReport> getDataList() {
		return dataList;
	}

	public void setDataList(ArrayList<DspFaultReport> dataList) {
		this.dataList = dataList;
	}

	private static class ViewHolder {
		RelativeLayout item_view;
		TextView DspName;
		CheckBox cb_ok;
	}

}
