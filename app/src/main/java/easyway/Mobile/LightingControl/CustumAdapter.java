package easyway.Mobile.LightingControl;

import java.util.ArrayList;
import java.util.HashMap;

import easyway.Mobile.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

public class CustumAdapter extends BaseAdapter {

	private ArrayList<LightingControl> mList;

	private ArrayList<LightingControl> list;
	private Context mContext;

	public ArrayList<LightingControl> getRememberlist() {
		return list;
	}

	public CustumAdapter(Context context, ArrayList<LightingControl> list) {
		super();
		mList = list;
		mContext = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder viewHolder;
		list = mList;

		convertView = LayoutInflater.from(mContext).inflate(
				R.layout.custum_list, null);
		viewHolder = new ViewHolder();
		viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
		viewHolder.btn_switch = (Switch) convertView
				.findViewById(R.id.btn_switch);

		LightingControl obj = mList.get(position);

		final LightingControl objtemp = list.get(position);
		viewHolder.tv_name.setText(obj.PointName);
		if (obj.DefaultValue == 1) {
			viewHolder.btn_switch.setChecked(true);
		} else {
			viewHolder.btn_switch.setChecked(false);
		}

		viewHolder.btn_switch
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							objtemp.DefaultValue = 1;// 灯开

						} else {

							objtemp.DefaultValue = 0;// 灯管

						}

					}
				});

		return convertView;
	}

	public class ViewHolder {
		private TextView tv_name;
		private Switch btn_switch;

	}

}