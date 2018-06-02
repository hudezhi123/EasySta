package easyway.Mobile.PointTask;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.R;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

/*
 *  车次类型选择
 */
public class PTTrainTypeDialogActivity extends ActivityEx {
	public static final String KEY_TRAINTYPE = "TrainType";
	
	private TrainTypeAdapter mAdapter;
	private GridView gTrainType;
	private String mTrainType;
	private final int[]  ID_TrainTypes = {
			R.string.task_train_A,
			R.string.task_train_C,
			R.string.task_train_D,
			R.string.task_train_G,
			R.string.task_train_K,
			R.string.task_train_L,
			R.string.task_train_N,
			R.string.task_train_T,
			R.string.task_train_Y,
			R.string.task_train_Z,
			R.string.task_train_NULL};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pointtask_traintype_select);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null)
			mTrainType = bundle.getString(KEY_TRAINTYPE);
			
		gTrainType = (GridView) findViewById(R.id.gTrainType);
		mAdapter = new TrainTypeAdapter(this);
		gTrainType.setAdapter(mAdapter);
		gTrainType.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 < 0 || (arg2 + 1) > ID_TrainTypes.length) 
					return;
				
				Intent intent = new Intent();
				intent.putExtra(KEY_TRAINTYPE, getString(ID_TrainTypes[arg2]));
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	private class TrainTypeAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public TrainTypeAdapter(Context mContext) {
			mInflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			if (ID_TrainTypes == null)
				return 0;
			else
				return ID_TrainTypes.length;
		}

		@Override
		public Object getItem(int position) {
			if (ID_TrainTypes == null) {
				return null;
			} else {
				if (position < ID_TrainTypes.length) {
					return ID_TrainTypes[position];
				} else {
					return null;
				}
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int content_id = (Integer) getItem(position);
			String content = getString(content_id);
			
			ViewHolder holder;
			if (null == convertView) {
				convertView = mInflater.inflate(R.layout.pointtask_traintype_select_item, null);
				holder = new ViewHolder();

				holder.content = (TextView) convertView
						.findViewById(R.id.txtType);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.content.setText(content);
			if (mTrainType == null || mTrainType.length() == 0) {
				if (content.equals(getString(R.string.task_train_NULL)))
					holder.content.setBackgroundColor(getResources().getColor(R.color.gray));
				else
					holder.content.setBackgroundColor(getResources().getColor(R.color.white));
			} else {
				if (mTrainType.equals(content))
					holder.content.setBackgroundColor(getResources().getColor(R.color.gray));
				else
					holder.content.setBackgroundColor(getResources().getColor(R.color.white));
			}

			return convertView;
		}
	}

	private static class ViewHolder {
		TextView content;
	}
}
