package easyway.Mobile.SiteRules;

import java.util.ArrayList;
import java.util.HashMap;


import android.annotation.SuppressLint;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.HomeKey;

/*
 * 站内规章
 */
public class SRTabActivity extends TabActivity implements
		TabHost.OnTabChangeListener {

	public static final String SHARE_TYPE_FILE_NAME = "RuleType";
	public static final String SHARE_TYPE_KEY = "JSONResult";
	public static final String ACTION_SITERULE_SEARCH = "easyway.mobile.SiteRule.Search";
	public static final String EXTRA_KEY = "KEY";
	public static final String EXTRA_TYPE = "TYPE";

	private String errMsg;
	private ArrayList<SiteRuleType> mTypes;

	private TabHost mTabHost;
	private EditText edtSearch;
	private LinearLayout layoutSearch;
	private Button searchBtn;
	private GridView typelist;
	private SRTypeAdapter mAdapter;

	private final int MSG_GETDATA_FAIL = 0;
	private final int MSG_GETDATA_SUCCEED = 1;

	@SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_GETDATA_FAIL:
					Toast.makeText(SRTabActivity.this, errMsg, Toast.LENGTH_LONG)
							.show();
					break;
				case MSG_GETDATA_SUCCEED:
					mTypes = (ArrayList<SiteRuleType>) msg.obj;
					mAdapter.setData(mTypes);
					mAdapter.notifyDataSetChanged();
					break;
				default:
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (HomeKey.work && !HomeKey.disableHome) {
			getWindow().addFlags(HomeKey.FLAG_HOMEKEY_DISPATCHED);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sr_tab);

		initView();
		initTabHost();
		getData();
	}

	private void initView() {
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.SR_Title);

		Button btnReturn = (Button) findViewById(R.id.btnReturn);
		btnReturn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		layoutSearch = (LinearLayout) findViewById(R.id.layoutSearch);
		layoutSearch.setVisibility(View.GONE);
		searchBtn = (Button) findViewById(R.id.btnset);
		searchBtn.setText(R.string.search);
		searchBtn.setVisibility(View.VISIBLE);
		searchBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (layoutSearch.getVisibility() == View.VISIBLE) {
					layoutSearch.setVisibility(View.GONE);
				} else {
					layoutSearch.setVisibility(View.VISIBLE);
				}
			}
		});

		edtSearch = (EditText) findViewById(R.id.search_edit);
		edtSearch.setHint(R.string.SR_InputTitleOrKey);
		Button btnSearch = (Button) findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String key = edtSearch.getText().toString().trim();
				String type = "";
				if (mTypes != null && mTypes.size() != 0) {
					for (SiteRuleType item : mTypes) {
						if (item.check) {
							if (type.equals(""))
								type += "'" + item.id + "'";
							else
								type += ",'" + item.id + "'";
						}

					}
				}

				Intent intent = new Intent();
				intent.putExtra(EXTRA_KEY, key);
				intent.putExtra(EXTRA_TYPE, type);
				intent.setAction(ACTION_SITERULE_SEARCH);
				sendBroadcast(intent);
			}
		});

		typelist = (GridView) findViewById(R.id.typelist);
		mAdapter = new SRTypeAdapter(SRTabActivity.this, null);
		mAdapter.setIDataChange(new IDataChange() {

			@Override
			public void ItemClick(int index) {
				// do nothing
			}

			@Override
			public void ItemClick(int index, boolean check) {
				if (mTypes == null)
					return;

				if (index < mTypes.size()) {
					mTypes.get(index).check = check;
					mAdapter.setData(mTypes);
					mAdapter.notifyDataSetChanged();
				}
			}
		});

		typelist.setAdapter(mAdapter);
		typelist.setSelector(new ColorDrawable(Color.TRANSPARENT));            // 屏蔽gridview选中状态
	}

	private void initTabHost() {
		mTabHost = getTabHost();
		mTabHost.setup();
		addOneTab();
		addTwoTab();
		mTabHost.setOnTabChangedListener(this);
	}

	// 线上文件tab
	private void addOneTab() {
		Intent intent = new Intent();
//        intent.setClass(this, SRNetActivity.class);
		intent.setClass(this, NewSRNetActivity.class);
		TextView txt = new TextView(this);
		txt.setText(R.string.SR_Net);
		txt.setTextSize(20);
		txt.setGravity(Gravity.CENTER);
		txt.setTextColor(Color.BLACK);
		txt.setBackgroundResource(R.drawable.btn_tab_left_selector);
		TabSpec spec = mTabHost.newTabSpec("Net");
		spec.setIndicator(txt);
		spec.setContent(intent);
		mTabHost.addTab(spec);
	}


	// 本地文件tab
	private void addTwoTab() {
		Intent intent = new Intent();
		intent.setClass(this, SRLocalActivity.class);

		TextView txt = new TextView(this);
		txt.setText(R.string.SR_Local);
		txt.setTextSize(20);
		txt.setGravity(Gravity.CENTER);
		txt.setTextColor(Color.BLACK);
		txt.setBackgroundResource(R.drawable.btn_tab_right_selector);
		TabSpec spec = mTabHost.newTabSpec("Local");
		spec.setIndicator(txt);
		spec.setContent(intent);
		mTabHost.addTab(spec);
	}

	@Override
	public void onTabChanged(String arg0) {
		// do nothing
	}

	private void getData() {
		new Thread() {
			public void run() {
				getSRType();
			}
		}.start();
	}

	// 获取文档类型
	private void getSRType() {
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("key", "RegulationsType");
		parmValues.put("xPath", "");
		parmValues.put("sessionId", Property.SessionId);
		String methodPath = Constant.MP_SPARK;
		String methodName = Constant.MN_GET_CMKEYVALUEXML;
		WebServiceManager webServiceManager = new WebServiceManager(
				getApplicationContext(), methodName, parmValues);
		String result = webServiceManager.OpenConnect(methodPath);

		if (result == null || result.equals("")) {
			errMsg = getString(R.string.exp_getdata);
			myhandle.sendEmptyMessage(MSG_GETDATA_FAIL);
			return;
		}
		SharedPreferences sharedPreferences = getSharedPreferences(SHARE_TYPE_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(SHARE_TYPE_KEY, result);
		editor.commit();
		ArrayList<SiteRuleType> list = SiteRuleType.ParseFromString(result);
		if (list == null || list.size() == 0) {
			errMsg = "解析结果为空";
			myhandle.sendEmptyMessage(MSG_GETDATA_FAIL);
		} else {
			Message message = new Message();
			message.what = MSG_GETDATA_SUCCEED;
			message.obj = list;
			myhandle.sendMessage(message);
		}
	}

}
