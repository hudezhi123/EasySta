package easyway.Mobile.ShiftNew;

import java.util.Calendar;

import easyway.Mobile.R;
import easyway.Mobile.util.HomeKey;
import android.app.DatePickerDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

/*
 * 交接班
 */
public class ShiftTabActivity extends TabActivity implements
		TabHost.OnTabChangeListener {
	public static final String ACTION_DATE_CHANGE = "easyway.Mobile.Shift.DateChange";

	private TabHost mTabHost;
	private TextView txtDate;

	private int mYear;
	private int mMonth;
	private int mDay;
	public static String mDate = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (HomeKey.work && !HomeKey.disableHome) {
			getWindow().addFlags(HomeKey.FLAG_HOMEKEY_DISPATCHED);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shift_tab);

		initView();
		initTabHost();
	}

	private void initView() {
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.Shift_Title);

		txtDate = (TextView) findViewById(R.id.txtDate);
		txtDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new DatePickerDialog(ShiftTabActivity.this, date, mYear,
						mMonth, mDay).show();
			}
		});
		
		Button searchBtn = (Button) findViewById(R.id.btnset);
		searchBtn.setVisibility(View.VISIBLE);
		searchBtn.setText(R.string.search);
		searchBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (txtDate.getVisibility() == View.VISIBLE) {
					txtDate.setVisibility(View.GONE);
				} else {
					txtDate.setVisibility(View.VISIBLE);
				}	
			}
			
		});

		Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		mDate = new StringBuilder()
		.append(mYear)
		.append("-")
		.append((mMonth + 1) < 10 ? "0" + (mMonth + 1)
				: (mMonth + 1)).append("-")
		.append((mDay < 10) ? "0" + mDay : mDay).toString();
		txtDate.setText(mDate);
		
		Button btnReturn = (Button) findViewById(R.id.btnReturn);
		btnReturn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void initTabHost() {
		mTabHost = getTabHost();
		mTabHost.setup();

		addOneTab();
		addTwoTab();

		mTabHost.setOnTabChangedListener(this);
	}

	private void addOneTab() {
		Intent intent = new Intent();
		intent.setClass(this, ShiftTaskActivity.class);

		TextView txt = new TextView(this);
		txt.setText(R.string.Shift_Task);
		txt.setTextSize(20);
		txt.setGravity(Gravity.CENTER);
		txt.setTextColor(Color.BLACK);
		txt.setBackgroundResource(R.drawable.btn_tab_left_selector);
		TabSpec spec = mTabHost.newTabSpec("Task");
		spec.setIndicator(txt);
		spec.setContent(intent);
		mTabHost.addTab(spec);
	}

	private void addTwoTab() {
		Intent intent = new Intent();
		intent.setClass(this, ShiftAttendanceActivity.class);

		TextView txt = new TextView(this);
		txt.setText(R.string.Shift_Attendance);
		txt.setTextSize(20);
		txt.setGravity(Gravity.CENTER);
		txt.setTextColor(Color.BLACK);
		txt.setBackgroundResource(R.drawable.btn_tab_right_selector);
		TabSpec spec = mTabHost.newTabSpec("Attendance");
		spec.setIndicator(txt);
		spec.setContent(intent);
		mTabHost.addTab(spec);
	}

	@Override
	public void onTabChanged(String arg0) {
		// do nothing
	}

	// 设置日期控件
	DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			mDate = new StringBuilder()
					.append(mYear)
					.append("-")
					.append((mMonth + 1) < 10 ? "0" + (mMonth + 1)
							: (mMonth + 1)).append("-")
					.append((mDay < 10) ? "0" + mDay : mDay).toString();
			txtDate.setText(mDate);

			Intent intent = new Intent();
			intent.setAction(ACTION_DATE_CHANGE);
			sendBroadcast(intent);
		}
	};
}
