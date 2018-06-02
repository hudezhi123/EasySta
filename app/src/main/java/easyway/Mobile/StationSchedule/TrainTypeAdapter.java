package easyway.Mobile.StationSchedule;

import java.util.ArrayList;

import easyway.Mobile.R;
import easyway.Mobile.Data.TrainType;
import easyway.Mobile.Property;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/*
 * 列车时刻表Adapter
 */
public class TrainTypeAdapter extends BaseAdapter {
	private LayoutInflater layoutInflater;
	private Context context;
	private ArrayList<TrainType> mList;

	public TrainTypeAdapter(Context context, ArrayList<TrainType> list) {
		this.context = context;
		layoutInflater = LayoutInflater.from(this.context);
		mList = list;
	}

	public int getCount() {
		if (mList == null)
			return 0;

		int  num = mList.size() / 2;
		if (num *2 == mList.size()) {
			return  num;
		} else {
			return num + 1;
		}
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

	public void setDate(ArrayList<TrainType> list) {
		mList = list;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Object obj = getItem(position);
		if (null != obj) {
			View temp = null;
			if (convertView != null) {
				temp = convertView;
			} else {
				temp = layoutInflater.inflate(
						R.layout.stationschedule_traintype_item, parent, false);
			}

			TextView txtTypeA = (TextView) temp.findViewById(R.id.txtTypeA);
			TextView txtTypeB = (TextView) temp.findViewById(R.id.txtTypeB);
			TextView txtNumberA = (TextView) temp.findViewById(R.id.txtNumberA);
			TextView txtNumberB = (TextView) temp.findViewById(R.id.txtNumberB);

			final TrainType TypeA = (TrainType) mList.get(position * 2);
			txtTypeA.setText(TypeA.Name);
			txtNumberA.setText(TypeA.Number);

			int indexB = position * 2 + 1;
			if (mList.size() > indexB) {
				final TrainType TypeB = (TrainType) mList.get(indexB);
				txtTypeB.setText(TypeB.Name);
				txtNumberB.setText(TypeB.Number);
			}

			int width = Property.screenheight /4;
			txtTypeA.setWidth(width);
			txtTypeB.setWidth(width);
			txtNumberA.setWidth(width);
			txtNumberB.setWidth(width);

			return temp;
		}

		return null;
	}

}
