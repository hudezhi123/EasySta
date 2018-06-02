package easyway.Mobile.TrainAD;

import java.util.ArrayList;

import easyway.Mobile.R;
import easyway.Mobile.Data.VIAStation;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/*
 * 列车时刻表-上下车人员信息
 */
public class TrainADScheduleAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private ArrayList<VIAStation> mList;
	private Context context;

	public TrainADScheduleAdapter(Context context, ArrayList<VIAStation> list) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
		mList = list;
	}

	public int getCount() {
		if (mList == null)
			return 0;
		return mList.size();
	}

	public VIAStation getItem(int position) {
		if (mList == null)
			return null;
		return mList.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	public void setData(ArrayList<VIAStation> models) {
		mList = models;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.trainad_schedule_item,
					null);
			holder = new ViewHolder();
			holder.stationOrder = (TextView) convertView
					.findViewById(R.id.stationOrder);
			holder.stationName = (TextView) convertView
					.findViewById(R.id.stationName);
			holder.txtTime = (TextView) convertView.findViewById(R.id.txtTime);
			holder.passengerNum = (TextView) convertView
					.findViewById(R.id.passengerNum);

			holder.miles = (TextView) convertView.findViewById(R.id.miles);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.stationOrder
				.setText(String.valueOf(mList.get(position).StationOrder) + ".");
		holder.stationOrder.getPaint().setFakeBoldText(true);

		holder.stationName.setText(mList.get(position).Station);
		holder.stationName.getPaint().setFakeBoldText(true);

		if (position == 0) {
			holder.txtTime.setText(mList.get(position).DepaTime
					+ context.getString(R.string.leave));
		} else if (position == mList.size() - 1) {
			holder.txtTime.setText(mList.get(position).ArrTime
					+ context.getString(R.string.arrive));
		} else {
			holder.txtTime.setText(mList.get(position).ArrTime
					+ context.getString(R.string.arrive) + "/"
					+ mList.get(position).DepaTime
					+ context.getString(R.string.leave));
		}

		String passengerNum = "";
		if (mList.get(position).CSRS == null
				|| mList.get(position).CSRS.equals(""))
			passengerNum += "0";
		else
			passengerNum += mList.get(position).CSRS;

		passengerNum += context.getString(R.string.man) + "/";
		if (mList.get(position).SCRS == null
				|| mList.get(position).SCRS.equals(""))
			passengerNum += "0";
		else
			passengerNum += mList.get(position).SCRS;

		passengerNum += context.getString(R.string.man) + "/";
		if (mList.get(position).XCRS == null
				|| mList.get(position).XCRS.equals(""))
			passengerNum += "0";
		else
			passengerNum += mList.get(position).XCRS;
		passengerNum += context.getString(R.string.man);

		holder.passengerNum.setText(passengerNum);
		holder.miles.setText(mList.get(position).Miles
				+ context.getString(R.string.kilometre));

		return convertView;
	}

	static class ViewHolder {
		TextView stationOrder;
		TextView stationName;
		TextView txtTime;
		TextView passengerNum;
		TextView miles;
	}
}
