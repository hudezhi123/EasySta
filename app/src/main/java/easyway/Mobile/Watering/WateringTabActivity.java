package easyway.Mobile.Watering;

import easyway.Mobile.R;
import easyway.Mobile.util.DateLine;
import easyway.Mobile.util.HomeKey;
import easyway.Mobile.util.IDateLineListener;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

/*
 * 上水
 */
public class WateringTabActivity extends TabActivity implements
		TabHost.OnTabChangeListener {
	public static final String ACTION_DATE_CHANGE = "easyway.Mobile.Watering.DateChange";

	private TabHost mTabHost;
	private DateLine dateline;
	
	public static String mDate = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (HomeKey.work && !HomeKey.disableHome) {
			getWindow().addFlags(HomeKey.FLAG_HOMEKEY_DISPATCHED);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.water_tab);

		initView();
		initTabHost();
		mDate = dateline.getDate();
	}

	private void initView() {
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.Watering_Title);

		dateline = (DateLine) findViewById(R.id.dateline);
		dateline.setListener(new IDateLineListener() {
			@Override
			public void DateChange() {
				mDate = dateline.getDate();
				Intent intent = new Intent();
				intent.setAction(ACTION_DATE_CHANGE);
				sendBroadcast(intent);
			}
		});
		
		Button searchBtn = (Button) findViewById(R.id.btnset);
		searchBtn.setVisibility(View.VISIBLE);
		searchBtn.setText(R.string.search);
		searchBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dateline.getVisibility() == View.VISIBLE) {
					dateline.setVisibility(View.GONE);
				} else {
					dateline.setVisibility(View.VISIBLE);
				}	
			}
			
		});

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

	// 待完成
	private void addOneTab() {
		Intent intent = new Intent();
		intent.setClass(this, WateringProcessingActivity.class);

		TextView txt = new TextView(this);
		txt.setText(R.string.Watering_Processing);
		txt.setTextSize(20);
		txt.setGravity(Gravity.CENTER);
		txt.setTextColor(Color.BLACK);
		txt.setBackgroundResource(R.drawable.btn_tab_left_selector);
		TabSpec spec = mTabHost.newTabSpec("Processing");
		spec.setIndicator(txt);
		spec.setContent(intent);
		mTabHost.addTab(spec);
	}

	// 已完成
	private void addTwoTab() {
		Intent intent = new Intent();
		intent.setClass(this, WateringCompletedActivity.class);

		TextView txt = new TextView(this);
		txt.setText(R.string.Watering_Completed);
		txt.setTextSize(20);
		txt.setGravity(Gravity.CENTER);
		txt.setTextColor(Color.BLACK);
		txt.setBackgroundResource(R.drawable.btn_tab_right_selector);
		TabSpec spec = mTabHost.newTabSpec("Completed");
		spec.setIndicator(txt);
		spec.setContent(intent);
		mTabHost.addTab(spec);
	}

	@Override
	public void onTabChanged(String arg0) {
		// do nothing
	}
}
