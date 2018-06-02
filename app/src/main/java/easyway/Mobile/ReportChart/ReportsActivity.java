package easyway.Mobile.ReportChart;

import java.util.ArrayList;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.R;
import easyway.Mobile.util.PullRefreshListView;
import easyway.Mobile.util.PullRefreshListView.OnRefreshListener;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

/*
 *  股道占用
 */
public class ReportsActivity extends ActivityEx {
	public static final String KEY_DATA = "DATA";
	private PullRefreshListView lstReports;
	private ReportsAdapter mAdapter;
	private ArrayList<DataForm> mList = new ArrayList<DataForm>();
	private boolean isPullRefresh = false;

	private final int MSG_GETDATA_FAIL = 0;
	private final int MSG_GETDATA_SUCCEED = 1;

	@SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			closeProgressDialog();
			switch (msg.what) {
			case MSG_GETDATA_FAIL:
				if (isPullRefresh) {
					isPullRefresh = false;
					lstReports.onRefreshComplete();
				}
				showToast(errMsg);
				break;
			case MSG_GETDATA_SUCCEED:
				// if (isPullRefresh) {
				// isPullRefresh = false;
				// lstReports.onRefreshComplete();
				// }
				//
				// if (mList == null) {
				// mList = new ArrayList<DataForm>();
				// } else {
				// mList.clear();
				// }
				//
				// mList.addAll((ArrayList<DataForm>) msg.obj);
				mAdapter.setData(mList);
				mAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rc_reports);

		initView();
		getData();
	}

	private void initView() {
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.title_reportchart);

		lstReports = (PullRefreshListView) findViewById(R.id.lstReports);
		mAdapter = new ReportsAdapter(ReportsActivity.this, mList);
		lstReports.setAdapter(mAdapter);
		lstReports.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// isPullRefresh = true;
				// getData();
			}
		});

		lstReports.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 - 1 < mAdapter.getCount()) {
					DataForm data = mAdapter.getItem(arg2 - 1);
					if (data.Type == DataForm.TYPE_LINE) {
						Intent intent = new Intent(ReportsActivity.this,
								ChartLineActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable(KEY_DATA, data);
						intent.putExtras(bundle);
						startActivity(intent);
					} else if (data.Type == DataForm.TYPE_PIE) {
						Intent intent = new Intent(ReportsActivity.this,
								ChartPieActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable(KEY_DATA, data);
						intent.putExtras(bundle);
						startActivity(intent);
					} else if (data.Type == DataForm.TYPE_BAR) {
						Intent intent = new Intent(ReportsActivity.this,
								ChartBarActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable(KEY_DATA, data);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				}
			}
		});

	}

	// 获取数据
	private void getData() {
		DataForm pieData = new DataForm();
		pieData.Name = "2013年各季度营收（饼状图）";
		pieData.Type = DataForm.TYPE_PIE;
		pieData.Unit = "万元";
		pieData.Serises = new ArrayList<DataSerise>();
		DataSerise pieSerise = new DataSerise();
		pieSerise.Name = "2013年";
		pieSerise.Datas = new ArrayList<BaseData>();
		pieSerise.Datas.add(new BaseData("1季度", 3984.68, 0));
		pieSerise.Datas.add(new BaseData("2季度", 7420.11, 0));
		pieSerise.Datas.add(new BaseData("3季度", 2356.77, 0));
		pieSerise.Datas.add(new BaseData("4季度", 9863.68, 0));
		pieData.Serises.add(pieSerise);

		DataForm lineData = new DataForm();
		lineData.Name = "2012&2013年  12月每日营收（线形图）";
		lineData.Type = DataForm.TYPE_LINE;
		lineData.Unit = "万元";
		lineData.Serises = new ArrayList<DataSerise>();

		DataSerise lineSerise1 = new DataSerise();
		lineSerise1.Name = "2012年";
		lineSerise1.Datas = new ArrayList<BaseData>();
		lineSerise1.Datas.add(new BaseData("1日", 1, 980.3311));
		lineSerise1.Datas.add(new BaseData("2日", 2, 1129.0604));
		lineSerise1.Datas.add(new BaseData("3日", 3, 965.6578));
		lineSerise1.Datas.add(new BaseData("4日", 4, 1013.4161));
		lineSerise1.Datas.add(new BaseData("5日", 5, 1090.8328));
		lineSerise1.Datas.add(new BaseData("6日", 6, 1149.8132));
		lineSerise1.Datas.add(new BaseData("7日", 7, 1232.5793));
		lineSerise1.Datas.add(new BaseData("8日", 8, 997.9411));
		lineSerise1.Datas.add(new BaseData("9日", 9, 979.9027));
		lineSerise1.Datas.add(new BaseData("10日", 10, 1142.3759));
		lineSerise1.Datas.add(new BaseData("11日", 11, 1282.3236));
		lineSerise1.Datas.add(new BaseData("12日", 12, 1255.9042));
		lineSerise1.Datas.add(new BaseData("13日", 13, 970.3164));
		lineSerise1.Datas.add(new BaseData("14日", 14, 944.248));
		lineSerise1.Datas.add(new BaseData("15日", 15, 700.5295));
		lineSerise1.Datas.add(new BaseData("16日", 16, 747.3739));
		lineSerise1.Datas.add(new BaseData("17日", 17, 828.3197));
		lineSerise1.Datas.add(new BaseData("18日", 18, 730.5975));
		lineSerise1.Datas.add(new BaseData("19日", 19, 709.9845));
		lineSerise1.Datas.add(new BaseData("20日", 20, 818.5268));
		lineSerise1.Datas.add(new BaseData("21日", 21, 638.234));
		lineSerise1.Datas.add(new BaseData("22日", 22, 511.0601));
		lineSerise1.Datas.add(new BaseData("23日", 23, 346.813));
		lineSerise1.Datas.add(new BaseData("24日", 24, 318.0744));
		lineSerise1.Datas.add(new BaseData("25日", 25, 400.0667));
		lineSerise1.Datas.add(new BaseData("26日", 26, 497.7883));
		lineSerise1.Datas.add(new BaseData("27日", 27, 489.5076));
		lineSerise1.Datas.add(new BaseData("28日", 28, 510.9383));
		lineSerise1.Datas.add(new BaseData("29日", 29, 543.1301));
		lineSerise1.Datas.add(new BaseData("30日", 30, 635.6467));
		lineSerise1.Datas.add(new BaseData("31日", 31, 710.8174));
		lineData.Serises.add(lineSerise1);

		DataSerise lineSerise2 = new DataSerise();
		lineSerise2.Name = "2013年";
		lineSerise2.Datas = new ArrayList<BaseData>();
		lineSerise2.Datas.add(new BaseData("1日", 1, 780.3311));
		lineSerise2.Datas.add(new BaseData("2日", 2, 1229.0604));
		lineSerise2.Datas.add(new BaseData("3日", 3, 963.6578));
		lineSerise2.Datas.add(new BaseData("4日", 4, 1023.4161));
		lineSerise2.Datas.add(new BaseData("5日", 5, 990.8328));
		lineSerise2.Datas.add(new BaseData("6日", 6, 749.8132));
		lineSerise2.Datas.add(new BaseData("7日", 7, 1222.5793));
		lineSerise2.Datas.add(new BaseData("8日", 8, 1023.9411));
		lineSerise2.Datas.add(new BaseData("9日", 9, 970.9027));
		lineSerise2.Datas.add(new BaseData("10日", 10, 1192.3759));
		lineSerise2.Datas.add(new BaseData("11日", 11, 1382.3236));
		lineSerise2.Datas.add(new BaseData("12日", 12, 1055.9042));
		lineSerise2.Datas.add(new BaseData("13日", 13, 973.3164));
		lineSerise2.Datas.add(new BaseData("14日", 14, 1044.248));
		lineSerise2.Datas.add(new BaseData("15日", 15, 800.5295));
		lineSerise2.Datas.add(new BaseData("16日", 16, 947.3739));
		lineSerise2.Datas.add(new BaseData("17日", 17, 1228.3197));
		lineSerise2.Datas.add(new BaseData("18日", 18, 930.5975));
		lineSerise2.Datas.add(new BaseData("19日", 19, 909.9845));
		lineSerise2.Datas.add(new BaseData("20日", 20, 1018.5268));
		lineSerise2.Datas.add(new BaseData("21日", 21, 698.234));
		lineSerise2.Datas.add(new BaseData("22日", 22, 711.0601));
		lineSerise2.Datas.add(new BaseData("23日", 23, 246.813));
		lineSerise2.Datas.add(new BaseData("24日", 24, 418.0744));
		lineSerise2.Datas.add(new BaseData("25日", 25, 300.0667));
		lineSerise2.Datas.add(new BaseData("26日", 26, 397.7883));
		lineSerise2.Datas.add(new BaseData("27日", 27, 689.5076));
		lineSerise2.Datas.add(new BaseData("28日", 28, 410.9383));
		lineSerise2.Datas.add(new BaseData("29日", 29, 643.1301));
		lineSerise2.Datas.add(new BaseData("30日", 30, 935.6467));
		lineSerise2.Datas.add(new BaseData("31日", 31, 712.8174));
		lineData.Serises.add(lineSerise2);

		DataForm barData = new DataForm();
		barData.Type = DataForm.TYPE_BAR;
		barData.Name = "2012&2013年  12月每日营收（柱状图）";
		barData.Unit = "万元";
		barData.Serises = new ArrayList<DataSerise>();
		barData.Serises.add(lineSerise1);
		barData.Serises.add(lineSerise2);
		
		mList.add(pieData);
		mList.add(lineData);
		mList.add(barData);
		myhandle.sendEmptyMessage(MSG_GETDATA_SUCCEED);
	}
}
