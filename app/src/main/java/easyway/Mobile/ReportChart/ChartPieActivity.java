package easyway.Mobile.ReportChart;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import easyway.Mobile.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
 * 饼状图
 */
public class ChartPieActivity extends Activity {
	// 图表数据集
	private CategorySeries mSeries = new CategorySeries("");
	// 图表渲染器
	private DefaultRenderer mRenderer = new DefaultRenderer();

	// 数据
	private DataForm mData;

	private GraphicalView mChartView;

	private TextView txtDesc;
	private Button btnResize;
	private boolean onMove = false;
	// private float oldX = 0;
	private float oldY = 0;
	private int selectIndex = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rc_chartpie);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mData = (DataForm) extras.getSerializable(ReportsActivity.KEY_DATA);
		}
		
		txtDesc = (TextView) findViewById(R.id.txtDesc);
		txtDesc.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				switch (arg1.getAction()) {
				case MotionEvent.ACTION_DOWN:
					onMove = true;
					oldY = arg1.getY();
					break;
				case MotionEvent.ACTION_MOVE:
					if (onMove) {
						float newY = arg1.getY();
						if (newY - oldY > 50.0) {
							onMove = false;
							selectIndex++;
							if (selectIndex >= mSeries.getItemCount())
								selectIndex = 0;

							for (int i = 0; i < mSeries.getItemCount(); i++) {
								mRenderer.getSeriesRendererAt(i)
										.setHighlighted(i == selectIndex);
							}
							mChartView.repaint();
							txtDesc.setText(mSeries.getCategory(selectIndex)
									+ " \n" + mSeries.getValue(selectIndex));

						} else if (oldY - newY > 50.0) {
							onMove = false;
							selectIndex--;
							if (selectIndex < 0)
								selectIndex = mSeries.getItemCount() - 1;

							if (selectIndex < 0)
								return true;

							for (int i = 0; i < mSeries.getItemCount(); i++) {
								mRenderer.getSeriesRendererAt(i)
										.setHighlighted(i == selectIndex);
							}
							mChartView.repaint();
							txtDesc.setText(mSeries.getCategory(selectIndex)
									+ " \n" + mSeries.getValue(selectIndex));
						}

					}
					break;
				case MotionEvent.ACTION_UP:
					onMove = false;
					oldY = 0;
					break;
				default:
					break;
				}

				return true;
			}

		});

		initData();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mChartView == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
			mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
			mRenderer.setClickEnabled(true);
			mChartView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SeriesSelection seriesSelection = mChartView
							.getCurrentSeriesAndPoint();
					if (seriesSelection == null) {
					} else {
						for (int i = 0; i < mSeries.getItemCount(); i++) {
							mRenderer.getSeriesRendererAt(i).setHighlighted(
									i == seriesSelection.getPointIndex());
						}
						mChartView.repaint();
						selectIndex = seriesSelection.getPointIndex();

						txtDesc.setText(mSeries.getCategory(selectIndex)
								+ " \n" + mSeries.getValue(selectIndex));
					}
				}
			});
			layout.addView(mChartView, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		} else {
			mChartView.repaint();
		}
	}
	
	private void initData() {
		mRenderer.setZoomButtonsVisible(false);
		mRenderer.setStartAngle(180);
		mRenderer.setDisplayValues(true);
//		mRenderer.setDisplayValues(false);
		mRenderer.setShowLabels(false);
		mRenderer.setLegendTextSize(30);
		mRenderer.setLabelsTextSize(30);
		mRenderer.setPanEnabled(false);
		mRenderer.setExternalZoomEnabled(true);

		btnResize = (Button) findViewById(R.id.btnResize);
		btnResize.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mChartView != null) {
					mChartView.zoomReset();
				}
			}
		});

		if (mData != null && mData.Serises != null && mData.Serises.size() == 1) {
			for (DataSerise dataSerise : mData.Serises) {
				if (dataSerise.Datas != null && dataSerise.Datas.size() != 0) {
					for (BaseData baseData : dataSerise.Datas) {
						mSeries.add(baseData.Content, baseData.XValue);
						SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
						renderer.setColor(DataForm.COLORS[(mSeries.getItemCount() - 1)
								% DataForm.COLORS.length]);
						mRenderer.addSeriesRenderer(renderer);
					}
				}
			}
		}
	}
}
