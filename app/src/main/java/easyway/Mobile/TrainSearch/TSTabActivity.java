package easyway.Mobile.TrainSearch;

import easyway.Mobile.R;
import easyway.Mobile.util.HomeKey;
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
 * 到发通告
 */
public class TSTabActivity extends TabActivity implements
		TabHost.OnTabChangeListener {
	public static final String ACTION_TRAINAD_SEARCH = "easyway.Mobile.TrainAD.Search";
	public static final String EXTRA_TRAINNO = "TRAINNO";
	public static final String EXTRA_STATUS = "STATUS";
	public static final String EXTRA_PALTFORM = "PALTFORM";
	
	private TabHost mTabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (HomeKey.work && !HomeKey.disableHome) {
			getWindow().addFlags(HomeKey.FLAG_HOMEKEY_DISPATCHED);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trainsearch_tab);

		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.title_trainsearch);

//		Button searchBtn = (Button) findViewById(R.id.btnset);

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
		addTwoTab();

		mTabHost.setOnTabChangedListener(this);
	}

	private void addOneTab() {
		Intent intent = new Intent();
		intent.setClass(this, TSStationActivity.class);

		TextView txt = new TextView(this);
		txt.setText(R.string.TS_ByStationName);
		txt.setTextSize(20);
		txt.setGravity(Gravity.CENTER);
		txt.setTextColor(Color.BLACK);
		txt.setBackgroundResource(R.drawable.btn_tab_left_selector);
		TabSpec spec = mTabHost.newTabSpec("Origin");
		spec.setIndicator(txt);
		spec.setContent(intent);
		mTabHost.addTab(spec);
	}

	private void addTwoTab() {
		Intent intent = new Intent();
		intent.setClass(this, TSTrainNoActivity.class);

		TextView txt = new TextView(this);
		txt.setText(R.string.TS_ByTrainNo);
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
		// do nothing
	}
}
