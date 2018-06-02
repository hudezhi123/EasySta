package easyway.Mobile.Shift;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

import easyway.Mobile.R;
import easyway.Mobile.util.HomeKey;

public class MyShiftInOutActivity extends TabActivity implements
TabHost.OnTabChangeListener{
	private TabHost mTabHost;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (HomeKey.work && !HomeKey.disableHome) {
			getWindow().addFlags(HomeKey.FLAG_HOMEKEY_DISPATCHED);
		}
		setContentView(R.layout.act_myshiftinout);
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("我的交接班");


		Button btnReturn = (Button) findViewById(R.id.btnReturn);
		btnReturn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		initTabHost();
	}
	private void initTabHost() {
		mTabHost = getTabHost();
		mTabHost.setup();

		addOneTab();
		addThreeTab();

		mTabHost.setOnTabChangedListener(this);
	}
	
	private void addOneTab() {
		Intent intent = new Intent();
		intent.putExtra("shiftInWorkType", 5);
		intent.setClass(this, New_Shift_In.class);

		TextView txt = new TextView(this);
		txt.setText("交班");
		txt.setTextSize(20);
		txt.setGravity(Gravity.CENTER);
		txt.setTextColor(Color.BLACK);
		txt.setBackgroundResource(R.drawable.btn_tab_left_selector);
		TabSpec spec = mTabHost.newTabSpec("Origin");
		spec.setIndicator(txt);
		spec.setContent(intent);
		mTabHost.addTab(spec);
	}

	private void addThreeTab() {
		Intent intent = new Intent();
		intent.putExtra("shiftInWorkType", 6);
		intent.setClass(this, New_Shift_In.class);

		TextView txt = new TextView(this);
		txt.setText("接班");
		txt.setTextSize(20);
		txt.setGravity(Gravity.CENTER);
		txt.setTextColor(Color.BLACK);
		txt.setBackgroundResource(R.drawable.btn_tab_right_selector);
		TabSpec spec = mTabHost.newTabSpec("All");
		spec.setIndicator(txt);
		spec.setContent(intent);
		mTabHost.addTab(spec);
	}

	@Override
	public void onTabChanged(String arg0) {
		// TODO Auto-generated method stub
		
	}

}
