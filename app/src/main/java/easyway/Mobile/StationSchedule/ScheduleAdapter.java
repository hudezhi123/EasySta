package easyway.Mobile.StationSchedule;

import java.util.ArrayList;

import easyway.Mobile.R;
import easyway.Mobile.bean.TrainBase;
import easyway.Mobile.Property;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
 * 列车时刻表Adapter
 */
public class ScheduleAdapter extends BaseAdapter {
	private LayoutInflater layoutInflater;
	private Context context;
	private int mFlag ;
	private ArrayList<TrainBase> mList;

	public ScheduleAdapter(Context context, ArrayList<TrainBase> list) {
		this.context = context;
		layoutInflater = LayoutInflater.from(this.context);
		mList = list;
	}

	public int getCount() {
		if (mList == null)
			return 0;
		else
			return mList.size();
	}

	public Object getItem(int position) {
		if (mList == null)
			return null;
		else
			return mList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
	
	public void setDate(ArrayList<TrainBase> list, int flag) {
		mList = list;
		mFlag = flag;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		Object obj = getItem(position);
		if (null != obj) {
			View temp = null;
			if (convertView != null) {
				temp = convertView;
			} else {
				temp = layoutInflater.inflate(R.layout.stationschedule_item,
						parent, false);
			}

			LinearLayout LinearMain = (LinearLayout) temp.findViewById(R.id.LinearMain);
			TextView txtTRNO_PRO = (TextView) temp.findViewById(R.id.txtTRNO_PRO);
			TextView txtSTRTSTN_TT = (TextView) temp.findViewById(R.id.txtSTRTSTN_TT);
			TextView txtTILSTN_TT = (TextView) temp.findViewById(R.id.txtTILSTN_TT);
			TextView txtDepaTime = (TextView) temp
					.findViewById(R.id.txtDepaTime);
			TextView txtStationArrTime = (TextView) temp
					.findViewById(R.id.txtStationArrTime);
			TextView txtStationDepaTime = (TextView) temp.findViewById(R.id.txtStationDepaTime);
			TextView txtStationAttr = (TextView) temp.findViewById(R.id.txtStationAttr);
			TextView txtMiles = (TextView) temp.findViewById(R.id.txtMiles);

			final TrainBase base = (TrainBase) getItem(position);
			txtTRNO_PRO.setText(base.TRNO_PRO);
			txtSTRTSTN_TT.setText(base.STRTSTN_TT);
			txtTILSTN_TT.setText(base.TILSTN_TT);	
			txtDepaTime.setText(base.DepaTime);
			txtStationArrTime.setText(base.StationArrTime);
			txtStationDepaTime.setText(base.StationDepaTime);
			txtStationAttr.setText(base.StationAttr);
			txtMiles.setText(base.Miles+"");
			if(mFlag == 0){
				int width = Property.screenheight/6;
				txtTRNO_PRO.setWidth(width);
				txtSTRTSTN_TT.setWidth(width);
				txtTILSTN_TT.setWidth(width);
				txtDepaTime.setWidth(width);
				txtStationDepaTime.setWidth(width);
				txtMiles.setWidth(width);
				txtStationAttr.setVisibility(View.GONE);
				txtStationArrTime.setVisibility(View.GONE);
			}else{
				txtStationArrTime.setVisibility(View.VISIBLE);
				txtStationAttr.setVisibility(View.VISIBLE);
				int width = Property.screenheight/8;
				txtTRNO_PRO.setWidth(width);
				txtSTRTSTN_TT.setWidth(width);
				txtTILSTN_TT.setWidth(width);
				txtDepaTime.setWidth(width);
				txtStationArrTime.setWidth(width);
				txtStationDepaTime.setWidth(width);
				txtStationAttr.setWidth(width);
				txtMiles.setWidth(width);
			}
			
			LinearMain.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, TrainSchedule.class);
					Bundle bundle = new Bundle();
					bundle.putString("TrainNo", base.TRNO_PRO);
					intent.putExtras(bundle);
					context.startActivity(intent);
				}		
			});

			return temp;
		}

		return null;
	}

}
