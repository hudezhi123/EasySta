package easyway.Mobile.ReportChart;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import easyway.Mobile.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChartBarActivity extends Activity {
	// 图表数据集
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	// 图表渲染器
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

	// 数据
	private DataForm mData;

	private GraphicalView mChartView;

	private Button btnResize;
	private Button btnNextP;
	private Button btnNextS;
	private Button btnData;
	
	private TextView txtTitle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rc_chartline);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mData = (DataForm) extras.getSerializable(ReportsActivity.KEY_DATA);
		}

		txtTitle = (TextView) findViewById(R.id.txtTitle);
		
		btnResize = (Button) findViewById(R.id.btnResize);
		btnResize.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mChartView != null) {
					mChartView.zoomReset();
				}

			}
		});

		btnNextP = (Button) findViewById(R.id.btnNextP);
		btnNextP.setVisibility(View.GONE);

		btnNextS = (Button) findViewById(R.id.btnNextS);
		btnNextS.setVisibility(View.GONE);

		btnData = (Button) findViewById(R.id.btnData);
		btnData.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(ChartBarActivity.this,
						DataLineActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(ReportsActivity.KEY_DATA, mData);
				intent.putExtras(bundle);
				startActivity(intent);
				
			}
		});
		
		initData();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mChartView == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
//			mChartView = ChartFactory.getBarChartView(this, mDataset,
//					mRenderer, BarChart.Type.STACKED);
			mChartView = ChartFactory.getBarChartView(this, mDataset,
					mRenderer, BarChart.Type.DEFAULT);
//			mChartView = ChartFactory.getLineChartView(this, mDataset,
//					mRenderer);
			// enable the chart click events
			mRenderer.setClickEnabled(true);
			mRenderer.setSelectableBuffer(10);
			
			mChartView.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// handle the click event on the chart
					SeriesSelection seriesSelection = mChartView
							.getCurrentSeriesAndPoint();
					if (seriesSelection == null) {
						Toast.makeText(ChartBarActivity.this, "No chart element",
								Toast.LENGTH_SHORT).show();
					} else {
						// display information of the clicked point
						Toast.makeText(
								ChartBarActivity.this,
								"Chart element in series index "
										+ seriesSelection.getSeriesIndex()
										+ " data point index "
										+ seriesSelection.getPointIndex()
										+ " was clicked"
										+ " closest point value X="
										+ seriesSelection.getXValue() + ", Y="
										+ seriesSelection.getValue(),
								Toast.LENGTH_SHORT).show();
					}
				}
			});
			layout.setBackgroundColor(Color.WHITE);
			layout.addView(mChartView, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		} else {
			mChartView.repaint();
		}
	}

	
	private void initData() {
		// set some properties on the main renderer

		if (mData != null && mData.Serises != null && mData.Serises.size() != 0) {
			txtTitle.setText(mData.Name);
			
			for (DataSerise dataSerise : mData.Serises) {
				XYSeries series = new XYSeries(dataSerise.Name);
				if (dataSerise.Datas != null && dataSerise.Datas.size() != 0) {
					for (BaseData baseData : dataSerise.Datas) {
						series.add(baseData.XValue, baseData.YValue);
					}
				}

				mDataset.addSeries(series);

				XYSeriesRenderer renderer = new XYSeriesRenderer();
				renderer.setDisplayChartValues(true);
				renderer.setDisplayChartValuesDistance(10);
				renderer.setChartValuesTextSize(20);

				renderer.setColor(DataForm.COLORS[(mDataset.getSeriesCount() - 1)
						% DataForm.COLORS.length]);
				mRenderer.addSeriesRenderer(renderer);
			}
		}

		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.argb(100, 20, 18, 82));
		mRenderer.setAxisTitleTextSize(30);
		mRenderer.setChartTitleTextSize(30);
		mRenderer.setLabelsTextSize(30);
		mRenderer.setLegendTextSize(30);
		mRenderer.setMargins(new int[] { 20, 30, 15, 0 });
		mRenderer.setMarginsColor(Color.rgb(90, 175, 242));
		mRenderer.setZoomButtonsVisible(false);
		mRenderer.setExternalZoomEnabled(true);
		mRenderer.setPointSize(10);
		mRenderer.setSelectEnabled(true);
		mRenderer.setShowGrid(true);
		double[] range = ChartLineActivity.getRange(mData);
		mRenderer.setRange(range);
		mRenderer.setZoomLimits(range);
		mRenderer.setPanLimits(range);
		mRenderer.setFitLegend(true);
		mRenderer.setXLabels(30);
		mRenderer.setYLabels(10);
		mRenderer.setBarSpacing(0.5f);
	}
}
