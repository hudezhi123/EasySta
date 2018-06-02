package easyway.Mobile.ReportChart;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import easyway.Mobile.ActivityEx;
import easyway.Mobile.R;

public class DataLineActivity extends ActivityEx {
	private ListView lstData;
	private TextView txtTitle;
	private DataLineAdapter mAdapter;
	
	private LinearLayout Linearhead;;
	// 数据
	private DataForm mData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rc_chartlinedata);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mData = (DataForm) extras.getSerializable(ReportsActivity.KEY_DATA);
		}
		
		initView();
	}
	
	private void initView() {
		txtTitle = (TextView) findViewById(R.id.txtTitle);
		if (mData != null)
			txtTitle.setText(mData.Name);

		Linearhead =  (LinearLayout) findViewById(R.id.head);
		Linearhead.setOnTouchListener(new ListViewAndHeadViewTouchLinstener());
		lstData  = (ListView) findViewById(R.id.lstData);
		if (mData != null && mData.Serises != null && mData.Serises.size() != 0) {
			LinearLayout LinearContent =  (LinearLayout) findViewById(R.id.LinearContent);
			int length = mData.Serises.get(0).Datas.size();
			TextView[] txts = new TextView[length];
			for (int index = 0; index < length; index++) {
				txts[index] = new TextView(DataLineActivity.this);
				txts[index].setTextColor(Color.BLACK);
				txts[index].setTextSize(18);
				txts[index].setWidth(200);
				txts[index].setSingleLine(true);
				txts[index].setText(mData.Serises.get(0).Datas.get(index).Content);
				LinearContent.addView(txts[index]);
			}
		}
		
		mAdapter = new DataLineAdapter(DataLineActivity.this, mData);
		lstData.setAdapter(mAdapter);
		lstData.setOnTouchListener(new ListViewAndHeadViewTouchLinstener());
	}
	
	
	class ListViewAndHeadViewTouchLinstener implements View.OnTouchListener {

		public boolean onTouch(View arg0, MotionEvent arg1) {
			HorizontalScrollView headSrcrollView = (HorizontalScrollView) findViewById(R.id.RCHScrollView);
			headSrcrollView.onTouchEvent(arg1);
			return false;
		}
	}
	
	public class DataLineAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Context mContext;

		public DataLineAdapter(Context context, DataForm data) {
			mInflater = LayoutInflater.from(context);
			mContext = context;
		}

		@Override
		public int getCount() {
			if (mData == null || mData.Serises == null)
				return 0;
			else
				return mData.Serises.size();
		}

		@Override
		public DataSerise getItem(int arg0) {
			if (mData == null || mData.Serises == null)
				return null;
			else
				return mData.Serises.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			DataSerise serise = getItem(position);
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.rc_dataitem, null);
				holder = new ViewHolder();

				holder.txtCol1 = (TextView) convertView
						.findViewById(R.id.txtCol1);
				holder.RCHScrollView = (RCHScrollView) convertView
						.findViewById(R.id.RCHScrollView);
				holder.LinearContent = (LinearLayout) convertView
						.findViewById(R.id.LinearContent);

				if (serise.Datas != null) {
					int length = serise.Datas.size();
					if (length != 0) {
						holder.txtArray = new TextView[length];
						
						for (int index = 0; index < length; index++) {
							holder.txtArray[index] = new TextView(mContext);
							holder.txtArray[index].setTextColor(Color.BLACK);
							holder.txtArray[index].setTextSize(18);
							holder.txtArray[index].setWidth(200);
							holder.txtArray[index].setSingleLine(true);
							holder.LinearContent.addView(holder.txtArray[index]);
						}
					}
				}

				RCHScrollView headSrcrollView = (RCHScrollView) Linearhead
						.findViewById(R.id.RCHScrollView);
				headSrcrollView
						.AddOnScrollChangedListener(new OnScrollChangedListenerImp(
								holder.RCHScrollView));
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			if (serise != null) {
				holder.txtCol1.setText(serise.Name);
				if (holder.txtArray!= null && holder.txtArray.length != 0) {
					for (int index = 0; index < holder.txtArray.length; index ++) {
						if (serise.Datas != null && serise.Datas.size() > index)
							holder.txtArray[index].setText(String.valueOf(serise.Datas.get(index).YValue));
					}
				}
			}

			return convertView;
		}

		class OnScrollChangedListenerImp implements
				RCHScrollView.OnScrollChangedListener {
			RCHScrollView mScrollView;

			public OnScrollChangedListenerImp(RCHScrollView scrollViewar) {
				mScrollView = scrollViewar;
			}

			public void onScrollChanged(int l, int t, int oldl, int oldt) {
				mScrollView.smoothScrollTo(l, t);
			}
		};

		class ViewHolder {
			TextView txtCol1;
			TextView[] txtArray;
			LinearLayout LinearContent;
			RCHScrollView RCHScrollView;
		}
	}
}
