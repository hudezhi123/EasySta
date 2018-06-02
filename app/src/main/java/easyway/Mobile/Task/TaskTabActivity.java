package easyway.Mobile.Task;

import easyway.Mobile.Application.ExitApplication;
import easyway.Mobile.MainFramework;
import easyway.Mobile.R;
import easyway.Mobile.util.CommonFunc;
import easyway.Mobile.util.HomeKey;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import java.util.Random;

/*
 * 待办任务
 */
public class TaskTabActivity extends TabActivity implements
        TabHost.OnTabChangeListener {
    public static final String ACTION_TASK_SEARCH = "easyway.Mobile.Task.Search";
    public static final String EXTRA_TRAINNO = "TRAINNO";
    public static final String EXTRA_DATEFLAG = "DATEFLAG";
    public static final int DATEFLAG_TODAY = 0;
    public static final int DATEFLAG_YESTERDAY = 1;
    private boolean exitApp = false;
    private final String TABSPEC_REVIEW = "review";
    private final String TABSPEC_OPENING = "opening";
    private final String TABSPEC_EMPAHASIS = "emphasis";
    private final String TABSPEC_SUCCESS = "success";

    private TabHost mTabHost;
    private EditText edtSearch;
    private LinearLayout layoutSearch;
    private Button searchBtn;
    private RadioButton radToday;
    private RadioButton radYesterday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (HomeKey.work && !HomeKey.disableHome) {
            getWindow().addFlags(HomeKey.FLAG_HOMEKEY_DISPATCHED);
        }
        super.onCreate(savedInstanceState);
        ExitApplication.getInstance().getmBacklog().setNum(0);
        setContentView(R.layout.task_tab);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.title_task);

        layoutSearch = (LinearLayout) findViewById(R.id.layoutSearch);
        searchBtn = (Button) findViewById(R.id.btnset);
        searchBtn.setText(R.string.search);
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

        radToday = (RadioButton) findViewById(R.id.radToday);
        radYesterday = (RadioButton) findViewById(R.id.radYesterday);

        Button btnReturn = (Button) findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TaskTabActivity.this,
                        MainFramework.class));
                finish();

            }
        });

        edtSearch = (EditText) findViewById(R.id.search_edit);
        edtSearch.setHint("");
        Button btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String trainNo = edtSearch.getText().toString().trim();
                int dateFlag = DATEFLAG_TODAY;
                if (radToday.isChecked()) {
                    dateFlag = DATEFLAG_TODAY;
                } else if (radYesterday.isChecked()) {
                    dateFlag = DATEFLAG_YESTERDAY;
                }
                Intent intent = new Intent();
                intent.putExtra(EXTRA_TRAINNO, trainNo);
                intent.putExtra(EXTRA_DATEFLAG, dateFlag);
                intent.setAction(ACTION_TASK_SEARCH);
                sendBroadcast(intent);
            }
        });

        initTabHost();

        searchBtn.setVisibility(View.VISIBLE);
        layoutSearch.setVisibility(View.GONE);
    }

    private void initTabHost() {
        mTabHost = getTabHost();
        mTabHost.setup();

        addOneTab();
        addTwoTab();
        addThreeTab();
        addfourTab();
        mTabHost.setOnTabChangedListener(this);
    }

    private void addOneTab() {

        Intent intent = new Intent();
//		intent.setClass(this, TaskOpeningActivity.class);
        intent.setClass(this, TaskOpeningActivity_2.class);

        TextView txt = new TextView(this);
        txt.setText(R.string.task_Opening);
        txt.setTextSize(20);
        txt.setGravity(Gravity.CENTER);
        txt.setTextColor(Color.BLACK);
        txt.setBackgroundResource(R.drawable.btn_tab_left_selector);
        TabSpec spec = mTabHost.newTabSpec(TABSPEC_OPENING);
        spec.setIndicator(txt);
        spec.setContent(intent);
        mTabHost.addTab(spec);

    }

    private void addTwoTab() {
        Intent intent = new Intent();
        intent.setClass(this, TaskReviewActivity.class);

        TextView txt = new TextView(this);
        txt.setText(R.string.task_Review);
        txt.setTextSize(20);
        txt.setGravity(Gravity.CENTER);
        txt.setTextColor(Color.BLACK);
        txt.setBackgroundResource(R.drawable.btn_tab_middle_selector);
        TabSpec spec = mTabHost.newTabSpec(TABSPEC_REVIEW);
        spec.setIndicator(txt);
        spec.setContent(intent);
        mTabHost.addTab(spec);
    }

    private void addThreeTab() {
        Intent intent = new Intent();
        intent.setClass(this, TaskEmphasisActivity.class);

        TextView txt = new TextView(this);
        txt.setText(R.string.task_Emphasis);
        txt.setTextSize(20);
        txt.setGravity(Gravity.CENTER);
        txt.setTextColor(Color.BLACK);
        txt.setBackgroundResource(R.drawable.btn_tab_middle_selector);
        TabSpec spec = mTabHost.newTabSpec(TABSPEC_EMPAHASIS);
        spec.setIndicator(txt);
        spec.setContent(intent);
        mTabHost.addTab(spec);
    }

    private void addfourTab() {
        Intent intent = new Intent();
        intent.setClass(this, TaskCompleteActivity.class);

        TextView txt = new TextView(this);
        txt.setText("已完成");
        txt.setTextSize(20);
        txt.setGravity(Gravity.CENTER);
        txt.setTextColor(Color.BLACK);
        txt.setBackgroundResource(R.drawable.btn_tab_right_selector);
        TabSpec spec = mTabHost.newTabSpec(TABSPEC_SUCCESS);
        spec.setIndicator(txt);
        spec.setContent(intent);
        mTabHost.addTab(spec);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getRepeatCount() == 0) {
                startActivity(new Intent(TaskTabActivity.this,
                        MainFramework.class));
                finish();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    // @Override
    // public boolean onKeyDown(int keyCode, KeyEvent event) {
    // exitApp = false;
    // if (keyCode == KeyEvent.KEYCODE_BACK) {
    //
    //
    // } else {
    // exitApp = super.onKeyDown(keyCode, event);
    // }
    // // TODO Auto-generated method stub
    // return exitApp;
    // }

    @Override
    public void onTabChanged(String arg0) {
        searchBtn.setVisibility(View.INVISIBLE);
        layoutSearch.setVisibility(View.GONE);
        if (arg0.equals(TABSPEC_REVIEW)) {
        }

        if (arg0.equals(TABSPEC_OPENING)) {
            searchBtn.setVisibility(View.VISIBLE);
            searchBtn.setEnabled(true);
        }

        if (arg0.equals(TABSPEC_EMPAHASIS)) {
            searchBtn.setVisibility(View.INVISIBLE);
            searchBtn.setEnabled(false);
        }

        if (arg0.equals(TABSPEC_SUCCESS)) {
            searchBtn.setVisibility(View.VISIBLE);
            searchBtn.setEnabled(true);
        }
    }

}
