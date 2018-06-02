package easyway.Mobile.DevFault;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

import easyway.Mobile.R;
import easyway.Mobile.util.HomeKey;
import easyway.Mobile.util.LogUtil;

/**
 * Created by JSC on 2018/1/4.
 */

public class DeviceFaultActivity extends TabActivity implements
        TabHost.OnTabChangeListener {
    public static final String EXTRA_TRAINNO = "TRAINNO";
    private TabHost mTabHost;
    private TextView txtReport;
    private TextView txtList;
    private Button searchBtn;
    private boolean isOnClick = false;
    private ShowSearchBarListenerManager showSearchBarListenerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (HomeKey.work && !HomeKey.disableHome) {
            getWindow().addFlags(HomeKey.FLAG_HOMEKEY_DISPATCHED);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_fault_layout);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.title_devfault);


//		Button searchBtn = (Button) findViewById(R.id.btnset);

        Button btnReturn = (Button) findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        searchBtn = (Button) findViewById(R.id.btnset);

        searchBtn.setText(R.string.search);
        searchBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isOnClick = !isOnClick;
                ShowSearchBarListenerManager.getInstance().showSearch(isOnClick);
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
        intent.setClass(this, DeviceFaultReportActivity.class);

        txtReport = new TextView(this);
        txtReport.setText(R.string.title_devfaultreport);
        txtReport.setTextSize(20);
        txtReport.setGravity(Gravity.CENTER);
        txtReport.setTextColor(Color.BLACK);
        txtReport.setBackgroundResource(R.drawable.btn_m_selected);
        TabHost.TabSpec spec = mTabHost.newTabSpec("Origin");
        spec.setIndicator(txtReport);
        spec.setContent(intent);
        mTabHost.addTab(spec);
    }

    private void addTwoTab() {
        Intent intent = new Intent();
        intent.setClass(this, DeviceFaultListActivity.class);
        txtList = new TextView(this);
        txtList.setText(R.string.title_devfault_check);
        txtList.setTextSize(20);
        txtList.setGravity(Gravity.CENTER);
        txtList.setTextColor(Color.BLACK);
        txtList.setBackgroundResource(R.drawable.btn_m_normal);
        TabHost.TabSpec spec = mTabHost.newTabSpec("All");
        spec.setIndicator(txtList);
        spec.setContent(intent);
        mTabHost.addTab(spec);
    }


    @Override
    public void onTabChanged(String tabId) {
        LogUtil.e("tabId==" + tabId);
        if (tabId.equals("All")) {
            txtList.setTextColor(Color.BLACK);
            txtList.setBackgroundResource(R.drawable.btn_m_selected);
            txtReport.setBackgroundResource(R.drawable.btn_m_normal);
            searchBtn.setVisibility(View.VISIBLE);


        } else {
            txtReport.setTextColor(Color.BLACK);
            txtReport.setBackgroundResource(R.drawable.btn_m_selected);
            txtList.setBackgroundResource(R.drawable.btn_m_normal);
            searchBtn.setVisibility(View.INVISIBLE);
        }
    }



    public interface ShowSearchBarListener {
        void showSearch(boolean isShow);
    }

}
