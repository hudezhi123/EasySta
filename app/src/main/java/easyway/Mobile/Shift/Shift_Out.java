package easyway.Mobile.Shift;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.R;
import easyway.Mobile.util.IntercomCtrl;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Shift_Out extends ActivityEx implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shift2_out);
		initView();
	}

	private void initView() {
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("交班");
		Button btntask_shangshui = (Button) findViewById(R.id.btntask_shangshui);
		btntask_shangshui.setOnClickListener(this);
		Button btntask_station = (Button) findViewById(R.id.btntask_station);
		btntask_station.setOnClickListener(this);
		Button btntask_waitinghall = (Button) findViewById(R.id.btntask_waitinghall);
		btntask_waitinghall.setOnClickListener(this);
		Button btntask_checkin = (Button) findViewById(R.id.btntask_checkin);
		btntask_checkin.setOnClickListener(this);
		Button btntask_exit = (Button) findViewById(R.id.btntask_exit);
		btntask_exit.setOnClickListener(this);
	
	}

	@Override
	public void onPause() {
		super.onPause();
		IntercomCtrl.open_intercom(Shift_Out.this);
	}

	@Override
	public void onClick(View v) {

		Intent intent;
		switch (v.getId()) {
		case R.id.btntask_shangshui:
			intent = new Intent(Shift_Out.this, Shift_Out_Watering.class);
			intent.putExtra("shiftInWorkType", 0);
			startActivityForResult(intent, 1);
			break;
		case R.id.btntask_station:
			intent = new Intent(Shift_Out.this, Shift_Out_Watering.class);
			intent.putExtra("shiftInWorkType", 1);
			startActivityForResult(intent, 1);
			break;
		case R.id.btntask_waitinghall:
			intent = new Intent(Shift_Out.this, Shift_Out_Watering.class);
			intent.putExtra("shiftInWorkType", 2);
			startActivityForResult(intent, 1);
			break;
		case R.id.btntask_checkin:
			intent = new Intent(Shift_Out.this, Shift_Out_Watering.class);
			intent.putExtra("shiftInWorkType", 3);
			startActivityForResult(intent, 1);
			break;
		case R.id.btntask_exit:
			intent = new Intent(Shift_Out.this, Shift_Out_Watering.class);
			intent.putExtra("shiftInWorkType", 4);
			startActivityForResult(intent, 1);
			break;
		default:
			break;
		}

	}

}
