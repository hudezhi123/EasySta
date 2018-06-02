package easyway.Mobile.TrainAD;

import java.util.ArrayList;

import easyway.Mobile.R;
import easyway.Mobile.Data.TB_OCS_MTMInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/*
 * 列车时刻表-三乘人员信息
 */
public class TrainADScheduleMTMAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private ArrayList<TB_OCS_MTMInfo> mList;
	private Context context;

	public TrainADScheduleMTMAdapter(Context context,
			ArrayList<TB_OCS_MTMInfo> list) {
		mInflater = LayoutInflater.from(context);
		this.context = context;
		this.mList = list;
	}

	public int getCount() {
		if (mList == null)
			return 0;
		return mList.size();
	}

	public TB_OCS_MTMInfo getItem(int position) {
		if (mList == null)
			return null;
		return mList.get(position);
	}

	public long getItemId(int position) {
		return getItem(position).id;
	}

	public void setData(ArrayList<TB_OCS_MTMInfo> models) {
		mList = models;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.train_schedule_mtm_item,
					null);
			holder = new ViewHolder();
			holder.Sche_VestIn = (TextView) convertView
					.findViewById(R.id.Sche_VestIn);
			holder.Sche_Post = (TextView) convertView
					.findViewById(R.id.Sche_Post);
			holder.Sche_Name = (TextView) convertView
					.findViewById(R.id.Sche_Name);
			holder.Sche_Mobile = (TextView) convertView
					.findViewById(R.id.Sche_Mobile);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.Sche_VestIn.setText(context.getString(R.string.Sche_VestIn)
				+ ":" + mList.get(position).VestIn);
		holder.Sche_Post.getPaint().setFakeBoldText(true);
		holder.Sche_Post.setText(context.getString(R.string.Sche_Post) + ":"
				+ mList.get(position).Post);
		holder.Sche_Name.setText(context.getString(R.string.Sche_Name) + ":"
				+ mList.get(position).Name);
		holder.Sche_Mobile.setText(context.getString(R.string.Sche_Mobile)
				+ ":" + mList.get(position).Tel);

		return convertView;
	}

	static class ViewHolder {
		TextView Sche_VestIn;
		TextView Sche_Post;
		TextView Sche_Name;
		TextView Sche_Mobile;
	}
}
