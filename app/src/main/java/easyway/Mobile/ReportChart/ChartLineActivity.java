package easyway.Mobile.ReportChart;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/*
 *  线形图
 */
public class ChartLineActivity extends Activity {
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
					Log.i("Demo ", "zoomReset");
					mChartView.zoomReset();
				}

			}
		});

		btnNextP = (Button) findViewById(R.id.btnNextP);
		btnNextP.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mChartView != null) {
					int serieIndex = mRenderer.getSelectSerieIndex();
					int pointIndex = mRenderer.getSelectPointIndex();

					if (serieIndex < mDataset.getSeriesCount()) {
						XYSeries serie = mDataset.getSeriesAt(serieIndex);
						if (pointIndex + 1 < serie.getItemCount()) {
							mRenderer.setSelectPointIndex(serieIndex,
									pointIndex + 1);
						} else {
							mRenderer.setSelectPointIndex(serieIndex, 0);
						}
						mChartView.repaint();
					}
				}

			}
		});

		btnNextS = (Button) findViewById(R.id.btnNextS);
		btnNextS.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mChartView != null) {
					int serieIndex = mRenderer.getSelectSerieIndex();
					int pointIndex = mRenderer.getSelectPointIndex();

					if (serieIndex < mDataset.getSeriesCount()) {
						serieIndex++;
						if (serieIndex >= mDataset.getSeriesCount()) {
							serieIndex = 0;
						}
						XYSeries serie = mDataset.getSeriesAt(serieIndex);
						if (pointIndex < serie.getItemCount()) {
							mRenderer.setSelectPointIndex(serieIndex,
									pointIndex);
						} else {
							mRenderer.setSelectPointIndex(serieIndex, 0);
						}

						mChartView.repaint();
					}
				}

			}
		});

		btnData = (Button) findViewById(R.id.btnData);
		btnData.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(ChartLineActivity.this,
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
			mChartView = ChartFactory.getLineChartView(this, mDataset,
					mRenderer);
			// enable the chart click events
			mRenderer.setClickEnabled(true);
			mRenderer.setSelectableBuffer(10);
			mChartView.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// handle the click event on the chart
					SeriesSelection seriesSelection = mChartView
							.getCurrentSeriesAndPoint();
					if (seriesSelection == null) {
						Toast.makeText(ChartLineActivity.this,
								"No chart element", Toast.LENGTH_SHORT).show();
					} else {
						// display information of the clicked point
						Toast.makeText(
								ChartLineActivity.this,
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
		mRenderer.setApplyBackgroundColor(true);
		// mRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
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
				renderer.setPointStyle(PointStyle.CIRCLE);
				renderer.setFillPoints(true);
				renderer.setDisplayChartValues(true);
				renderer.setDisplayChartValuesDistance(10);
				renderer.setChartValuesTextSize(20);

				renderer.setColor(DataForm.COLORS[(mDataset.getSeriesCount() - 1)
						% DataForm.COLORS.length]);
				mRenderer.addSeriesRenderer(renderer);
			}
		}

		mRenderer.setShowGrid(true);
		double[] range = getRange(mData);
		mRenderer.setRange(range);
		mRenderer.setZoomLimits(range);
		mRenderer.setPanLimits(range);
		mRenderer.setFitLegend(true);
		mRenderer.setXLabels(30);
		mRenderer.setYLabels(10);
	}

	public static double[] getRange(DataForm data) {
		double minX = 0; // X轴最小值
		double maxX = 0; // X轴最大值
		double minY = 0; // Y轴最小值
		double maxY = 0; // Y轴最大值

		// 获取数据中的X、Y的值域范围
		if (data != null && data.Serises != null && data.Serises.size() != 0) {
			for (int i = 0; i < data.Serises.size(); i++) {
				DataSerise dataSerise = data.Serises.get(i);
				if (dataSerise.Datas != null && dataSerise.Datas.size() != 0) {
					for (int j = 0; j < dataSerise.Datas.size(); j++) {
						BaseData baseData = dataSerise.Datas.get(j);
						if (i == 0 && j == 0) {
							minX = baseData.XValue;
							maxX = baseData.XValue;
							minY = baseData.YValue;
							maxY = baseData.YValue;
						} else {
							if (baseData.XValue < minX)
								minX = baseData.XValue;

							if (baseData.XValue > maxX)
								maxX = baseData.XValue;

							if (baseData.YValue < minY)
								minY = baseData.YValue;

							if (baseData.YValue > maxY)
								maxY = baseData.YValue;
						}
					}
				}
			}
		}

		// 画图中的X、Y范围设定
		if (minX >= 0) { // X轴数据都在正方向
			minX = 0;
			maxX = maxX * 6 / 5;
		} else {
			if (maxX <= 0) { // X轴数据都在负方向
				maxX = 0;
				minX = minX * 6 / 5;
			} else { // X轴数据分部在正、负方向
				maxX = maxX * 6 / 5;
				minX = minX * 6 / 5;
			}
		}

		if (minY >= 0) { // Y轴数据都在正方向
			minY = 0;
			maxY = maxY * 6 / 5;
		} else {
			if (maxY <= 0) { // Y轴数据都在负方向
				maxY = 0;
				minY = minY * 6 / 5;
			} else { // Y轴数据分部在正、负方向
				maxY = maxY * 6 / 5;
				minY = minY * 6 / 5;
			}
		}
		return new double[] { minX, maxX, minY, maxY };
	}

}
