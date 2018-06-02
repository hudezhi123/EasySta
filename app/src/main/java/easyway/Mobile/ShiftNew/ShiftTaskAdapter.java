package easyway.Mobile.ShiftNew;

import java.util.ArrayList;

import easyway.Mobile.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/*
 * 交接班-重要事项 Adapter
 */
public class ShiftTaskAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private ArrayList<ShiftTask> mList;
//	private Context mContext;

	public ShiftTaskAdapter(Context context, ArrayList<ShiftTask> list) {
		super();
		mInflater = LayoutInflater.from(context);
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

	public void setData(ArrayList<ShiftTask> models) {
		mList = models;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		ShiftTask obj = (ShiftTask) getItem(position);
		if (obj == null)
			return null;

		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.shift_task_item, null);
			holder = new ViewHolder();
			holder.txtNo = (TextView) convertView.findViewById(R.id.txtNo);
			holder.txtLevel = (TextView) convertView
					.findViewById(R.id.txtLevel);
			holder.txtPublisher = (TextView) convertView
					.findViewById(R.id.txtPublisher);
			holder.txtContent = (TextView) convertView
					.findViewById(R.id.txtContent);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.txtNo.setText(String.valueOf(position + 1));
		switch (obj.Level) {
		case ShiftTask.LEVEL_HIGH:
			holder.txtLevel.setText(R.string.Shift_Task_LEVEL_HIGH);
			break;
		case ShiftTask.LEVEL_NORMAL:
			holder.txtLevel.setText(R.string.Shift_Task_LEVEL_NORMAL);
			break;
		case ShiftTask.LEVEL_LOW:
			holder.txtLevel.setText(R.string.Shift_Task_LEVEL_LOW);
			break;
		default:
			holder.txtLevel.setText(String.valueOf(obj.Level));
			break;
		}
		holder.txtPublisher.setText(obj.PublishDep + "  " + obj.Publisher);
		holder.txtContent.setText(obj.Content);

		return convertView;
	}

	class ViewHolder {
		TextView txtNo;
		TextView txtLevel;
		TextView txtPublisher;
		TextView txtContent;
	}
}
