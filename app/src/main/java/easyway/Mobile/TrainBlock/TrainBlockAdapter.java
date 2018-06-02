package easyway.Mobile.TrainBlock;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/*
 * 车辆闭塞 Adapter
 */
public class TrainBlockAdapter extends BaseAdapter {
//	private LayoutInflater mInflater;
	private ArrayList<TrainBlock> mList;
//	private Context mContext;

	public TrainBlockAdapter(Context context, ArrayList<TrainBlock> list) {
		super();
//		mInflater = LayoutInflater.from(context);
		mList = list;
//		mContext = context;
	}

	@Override
	public int getCount() {
		if (mList == null)
			return 0;
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		if (mList == null)
			return null;
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setData(ArrayList<TrainBlock> models) {
		mList = models;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
//		ViewHolder holder;
		TrainBlock obj = (TrainBlock) getItem(position);
		if (obj == null)
			return null;

//		if (null == convertView) {
//			convertView = mInflater.inflate(R.layout.shift_task_item, null);
//			holder = new ViewHolder();
//			holder.txtNo = (TextView) convertView.findViewById(R.id.txtNo);
//			holder.txtLevel = (TextView) convertView
//					.findViewById(R.id.txtLevel);
//			holder.txtPublisher = (TextView) convertView
//					.findViewById(R.id.txtPublisher);
//			holder.txtContent = (TextView) convertView
//					.findViewById(R.id.txtContent);
//
//			convertView.setTag(holder);
//		} else {
//			holder = (ViewHolder) convertView.getTag();
//		}
//
//		holder.txtNo.setText(String.valueOf(position + 1));
//		switch (obj.Level) {
//		case ShiftTask.LEVEL_HIGH:
//			holder.txtLevel.setText(R.string.Shift_Task_LEVEL_HIGH);
//			break;
//		case ShiftTask.LEVEL_NORMAL:
//			holder.txtLevel.setText(R.string.Shift_Task_LEVEL_NORMAL);
//			break;
//		case ShiftTask.LEVEL_LOW:
//			holder.txtLevel.setText(R.string.Shift_Task_LEVEL_LOW);
//		default:
//			holder.txtLevel.setText(String.valueOf(obj.Level));
//			break;
//		}
//		holder.txtPublisher.setText(obj.PublishDep + "  " + obj.Publisher);
//		holder.txtContent.setText(obj.Content);

		return convertView;
	}

	class ViewHolder {
		TextView txtNo;
		TextView txtLevel;
		TextView txtPublisher;
		TextView txtContent;
	}
}
