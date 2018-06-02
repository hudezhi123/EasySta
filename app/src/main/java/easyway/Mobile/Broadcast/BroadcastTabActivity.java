package easyway.Mobile.Broadcast;

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
 * 移动广播
 */
public class BroadcastTabActivity extends TabActivity implements
		TabHost.OnTabChangeListener {
	private TabHost mTabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (HomeKey.work && !HomeKey.disableHome) {
			getWindow().addFlags(HomeKey.FLAG_HOMEKEY_DISPATCHED);
		}
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.broadcast_tab);

		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.title_broadcast);

		Button searchBtn = (Button) findViewById(R.id.btnset);
		searchBtn.setVisibility(View.VISIBLE);
		searchBtn.setText(R.string.View);
		searchBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
			}
		});

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

	// 专题广播
	private void addOneTab() {
		Intent intent = new Intent();
		intent.setClass(this, BroadcastSubject.class);

		TextView txt = new TextView(this);
		txt.setText(R.string.title_broadcastspec);
		txt.setTextSize(20);
		txt.setGravity(Gravity.CENTER);
		txt.setTextColor(Color.BLACK);
		txt.setBackgroundResource(R.drawable.btn_tab_left_selector);
		TabSpec spec = mTabHost.newTabSpec("Spec");
		spec.setIndicator(txt);
		spec.setContent(intent);
		mTabHost.addTab(spec);
	}


	// 人工广播
	private void addTwoTab() {
		Intent intent = new Intent();
		intent.setClass(this, BroadcastManualAddActivity.class);

		TextView txt = new TextView(this);
		txt.setText(R.string.title_broadcastmanual);
		txt.setTextSize(20);
		txt.setGravity(Gravity.CENTER);
		txt.setTextColor(Color.BLACK);
		txt.setBackgroundResource(R.drawable.btn_tab_right_selector);
		TabSpec spec = mTabHost.newTabSpec("Manual");
		spec.setIndicator(txt);
		spec.setContent(intent);
		mTabHost.addTab(spec);
	}

	@Override
	public void onTabChanged(String arg0) {
		// do nothing
	}
}
