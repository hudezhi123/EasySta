package easyway.Mobile.Shift;

import java.util.ArrayList;

import easyway.Mobile.R;
import easyway.Mobile.Data.TB_Shift;
import easyway.Mobile.util.CommonUtils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class Shift_In_Pool_Adapter extends BaseAdapter {

	private ArrayList<TB_Shift> shiftOutList = new ArrayList<TB_Shift>();
	private Context context;
	public Handler handler;
	private LayoutInflater infl = null;

	public Shift_In_Pool_Adapter(Context context,
			ArrayList<TB_Shift> shiftOutList) {
		this.context = context;
		if (shiftOutList != null) {
			this.shiftOutList = shiftOutList;
		}
		this.infl = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	public int getCount() {
		return shiftOutList.size();
	}

	public TB_Shift getItem(int position) {
		if (shiftOutList.size() > position) {
			return shiftOutList.get(position);
		} else {
			return null;
		}

	}

	public long getItemId(int position) {
		TB_Shift shift = getItem(position);
		if (shift == null)
			return 0;
		return shift.Shift_Id;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final TB_Shift shift = getItem(position);

		convertView = infl.inflate(R.layout.shift_in_pool_list_item, null);
		TextView labShift_Out_Team = (TextView) convertView
				.findViewById(R.id.labShift_Out_Team), labShift_Out_Dt = (TextView) convertView
				.findViewById(R.id.labShift_Out_Dt), labShift_Out_Staff = (TextView) convertView
				.findViewById(R.id.labShift_Out_Staff);
		Button btnViewShiftOutTrain = (Button) convertView
				.findViewById(R.id.btnViewShiftOutTrain);

		labShift_Out_Team.setText(":" + String.valueOf(shift.Shift_Out_Team));
		labShift_Out_Dt.setText(":"
				+ CommonUtils.ConvertDate(shift.Shift_Out_Dt));
		labShift_Out_Staff.setText(":" + shift.Shift_Out_Staff_Name);

		final long shiftid = shift.Shift_Id;

		btnViewShiftOutTrain.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				ShowTasksLis(shiftid);
			}
		});

		return convertView;
	}

	private void ShowTasksLis(long shift_out_id) {
		Intent intent = new Intent(context, Shift_In_Train.class);
		Bundle bundle = new Bundle();
		bundle.putLong("Shift_Id", shift_out_id);
		intent.putExtras(bundle);
		((Activity) context).startActivityForResult(intent, 1);
	}

}
