package easyway.Mobile.Shift;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import easyway.Mobile.ActivityEx;
import easyway.Mobile.Application.ExitApplication;
import easyway.Mobile.R;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.util.AnnotateUtil;
import easyway.Mobile.util.BindView;

public class ShiftInHomeActivity extends ActivityEx implements OnClickListener{
	private Activity act;
	@BindView (id = R.id.btnReturn)
	private Button btnReturn;
	@BindView (id = R.id.title)
	private TextView title;
	@BindView (id = R.id.btnset)
	private Button btnset;
	
	@BindView (id = R.id.taskShangshui)
	private Button taskShangshui;
	@BindView (id = R.id.taskStation)
	private Button taskStation;
	@BindView (id = R.id.taskWaitinghall)
	private Button taskWaitinghall;
	@BindView (id = R.id.taskCheckin)
	private Button taskCheckin;
	@BindView (id = R.id.taskExit)
	private Button taskExit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.act_shift_in_home);
		act = this;
		AnnotateUtil.initBindView(act);
		  ExitApplication.getInstance().addActivity(act);
		initView();
		super.onCreate(savedInstanceState);
	}
	
	private void initView(){
		title.setText("接班");
		btnset.setVisibility(View.VISIBLE);
		btnset.setText("交接班");
		btnset.setOnClickListener(this);
		
		btnReturn.setOnClickListener(this);
		taskShangshui.setOnClickListener(this);
		taskStation.setOnClickListener(this);
		taskWaitinghall.setOnClickListener(this);
		taskCheckin.setOnClickListener(this);
		taskExit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnReturn:
			finish();
			break;
		case R.id.taskShangshui:
			Intent shangIntent = new Intent(act,New_Shift_In.class);
			shangIntent.putExtra("shiftInWorkType", Constant.WT_SHANGSHUI);
			startActivity(shangIntent);
			break;
		case R.id.taskStation:
			Intent stationIntent = new Intent(act,New_Shift_In.class);
			stationIntent.putExtra("shiftInWorkType", Constant.WT_STATION);
			startActivity(stationIntent);
			break;
		case R.id.taskWaitinghall:
			Intent waitIntent = new Intent(act,New_Shift_In.class);
			waitIntent.putExtra("shiftInWorkType", Constant.WT_WAITINGHALL);
			startActivity(waitIntent);
			break;
		case R.id.taskCheckin:
			Intent checkIntent = new Intent(act,New_Shift_In.class);
			checkIntent.putExtra("shiftInWorkType", Constant.WT_CHECKIN);
			startActivity(checkIntent);
			break;
		case R.id.taskExit:
			Intent exitIntent = new Intent(act,New_Shift_In.class);
			exitIntent.putExtra("shiftInWorkType", Constant.WT_EXIT);
			startActivity(exitIntent);
			break;
		case R.id.btnset:
			Intent myIntent = new Intent(act,MyShiftInOutActivity.class);
			startActivity(myIntent);
			break;
		default:
			break;
		}
	}
	
}
